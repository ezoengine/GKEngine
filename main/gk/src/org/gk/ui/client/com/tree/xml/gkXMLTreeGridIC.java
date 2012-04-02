/*
 * Copyright (C) 2000-2012  InfoChamp System Corporation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gk.ui.client.com.tree.xml;

import java.util.List;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.utils.StringUtils;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * <title>XML樹控元件</title>
 * 
 * <pre>
 * 將XML轉成Grid樹控元件的呈現方式
 * </pre>
 * 
 * @author w10447
 * @since 2011/02/14
 */
public abstract class gkXMLTreeGridIC extends ContentPanel implements IC {

	protected CoreIC core;
	protected TreeGrid tree;
	protected TreeStore<ModelData> store;
	protected ColumnModel cm;
	// XML's Document
	protected Document doc;
	protected static final String NAME = "name"; // 顯示的節點名稱
	protected static final String TEXT = "text"; //
	protected static final String NODE = "node"; // XML節點屬性ID
	protected static final String ID = "id"; // 唯一的识别ID

	protected static final int KeyCodeF2 = 113;
	protected static final int KeyCodeEnter = 13;
	protected static final String EditTree = "editTree";
	protected boolean inEdit = false; // 树节点是否为正在修改名字状态

	public static interface Event {
		public final static String setXMLInfo = ".setXMLInfo";
		public final static String getXMLInfo = ".getXMLInfo";
		public final static String onClick = ".onClick";
		public final static String onDoubleClick = ".onDoubleClick";
		public final static String afterLoad = ".afterLoad";
		public final static String onEdit = ".onEdit";
	}

	public String evtSetXMLInfo() {
		return getId() + Event.setXMLInfo;
	}

	public String evtGetXMLInfo() {
		return getId() + Event.getXMLInfo;
	}

	public String evtOnClick() {
		return getId() + Event.onClick;
	}

	public String evtOnDoubleClick() {
		return getId() + Event.onDoubleClick;
	}

	public String evtAfterLoad() {
		return getId() + Event.afterLoad;
	}

	public String evtOnEdit() {
		return getId() + Event.onEdit;
	}

	@Override
	public CoreIC core() {
		return core;
	}

	public gkXMLTreeGridIC(String id) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree("");
		setLayout(new FitLayout());
		createTreeNodeIconProvider(tree);
		add(tree);
	}

	public gkXMLTreeGridIC(String id, String treeType) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree(treeType);
		setLayout(new FitLayout());
		createTreeNodeIconProvider(tree);
		add(tree);
	}

	public TreeGrid getTree() {
		return tree;
	}

	@Override
	public void setInfo(Object info) {
		// 清掉所有節點
		store.removeAll();
		// 透過GWT API解析整個XML字串轉成doc物件
		doc = XMLParser.parse("<root>" + info + "</root>");
		XMLParser.removeWhitespace(doc.getFirstChild());
		// 解析doc's node資訊放到store裡面
		parseXmlDoc2Store(doc, store, cm);
	}

	@Override
	public Object getInfo() {
		return doc.toString();
	}

	@Override
	public void bindEvent() {
		// 訂閱XML更新資訊
		core.subscribe(evtSetXMLInfo(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				setInfo(eo.getInfoString());
			}
		});

	}

	/**
	 * 展開樹控元件所有節點
	 */
	protected void expandAll() {
		// 必須在樹控元件繪製到畫面才能展開所有節點
		// 因此必須透過監聽 Render事件
		tree.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				((TreeGrid) be.getSource()).expandAll();
			}
		});
	}

	protected abstract ColumnModel createColumnModel();

	/**
	 * 建立樹控元件，使用EditTreeGrid才能編輯樹節點名稱
	 * 
	 * @param treeType
	 */
	public void createTree(String treeType) {
		store = new TreeStore<ModelData>();

		store.setKeyProvider(new ModelKeyProvider<ModelData>() {

			@Override
			public String getKey(ModelData model) {
				return model.get("name") + "";
			}
		});

		cm = createColumnModel();
		// 设定树的类型为可编辑树
		if (treeType.equals(EditTree)) {
			tree = new EditorTreeGrid(store, cm) {

				@Override
				protected boolean hasChildren(ModelData model) {
					// 根據節點名稱來決定是否可包含其他節點
					List list = ((BaseTreeModel) model).getChildren();
					return (list == null || list.size() == 0) ? false : true;
				}
			};
			((EditorTreeGrid) tree).setClicksToEdit(ClicksToEdit.TWO);
		} else {
			tree = new TreeGrid(store, cm) {

				@Override
				protected boolean hasChildren(ModelData model) {
					// 根據節點名稱來決定是否可包含其他節點
					List list = ((BaseTreeModel) model).getChildren();
					return (list == null || list.size() == 0) ? false : true;
				}
			};
		}

	}

	public void addRowEditorPlugin() {
		RowEditor<ModelData> editor = new RowEditor<ModelData>();
		tree.addPlugin(editor);
	}

	/**
	 * 建立根節點
	 * 
	 * @param doc
	 * @param store
	 * @param cm
	 */
	protected void parseXmlDoc2Store(Document doc, TreeStore store,
			ColumnModel cm) {
		Node rootNode = doc.getFirstChild();
		BaseTreeModel treeModel = getRootNodeInfo();
		treeModel.set("node", rootNode);
		preprocessNode(rootNode, treeModel, cm);
		store.add(treeModel, true);
	}

	/**
	 * 
	 * @param xmlNode
	 * @param treeModel
	 * @param cm
	 */
	public void preprocessNode(Node xmlNode, BaseTreeModel treeModel,
			ColumnModel cm) {
		NodeList xmlNodeList = xmlNode.getChildNodes();
		// 將所有子節點加入
		for (int i = 0; i < xmlNodeList.getLength(); i++) {
			Node subXMLNode = xmlNodeList.item(i);
			if (subXMLNode.getNodeType() == Node.COMMENT_NODE
					|| subXMLNode.getNodeType() == Node.TEXT_NODE) {
				continue;
			}

			BaseTreeModel subTreeNode = createTreeNode(subXMLNode, cm);
			preprocessNode(subXMLNode, subTreeNode, cm);
			treeModel.add(subTreeNode);
		}
	}

	/**
	 * <pre>
	 * 取得根節點資訊,讓子類別決定根節點的名稱和顯示的文字
	 * 例如
	 * new dejgMap("name", "ui").fill("text", "GUL元件庫");
	 * name 節點名稱、text 節點顯示文字
	 * </pre>
	 * 
	 * @return BaseTreeModel
	 */
	protected abstract BaseTreeModel getRootNodeInfo();

	/**
	 * 定義XML哪些Node Name是容器，可以拖放節點進去 null表示都可以，new gkList()表示都不行
	 * 
	 * @return List
	 */
	protected List getFolderNode() {
		return null;
	}

	/**
	 * <pre>
	 * 準備放到TreeNode的資訊
	 * NAME 節點名稱
	 * NODE XML節點物件
	 * cm ColumnConfig
	 * </pre>
	 * 
	 * @param xmlNode
	 * @param cm
	 * @return BaseTreeModel
	 */
	public BaseTreeModel createTreeNode(Node xmlNode, ColumnModel cm) {
		BaseTreeModel tm = new BaseTreeModel();
		String name = (xmlNode.getAttributes().getNamedItem("name") + "")
				.equals("null") ? xmlNode.getNodeName() : (xmlNode
				.getAttributes().getNamedItem("name") + "");
		String id = (xmlNode.getAttributes().getNamedItem("id") + "")
				.equals("null") ? DOM.createUniqueId() : (xmlNode
				.getAttributes().getNamedItem("id") + "");
		Node iconNode = xmlNode.getAttributes().getNamedItem("icon");

		List<ColumnConfig> columns = cm.getColumns();
		for (ColumnConfig cc : columns) {
			String key = cc.getId();
			String value = (xmlNode.getAttributes().getNamedItem(key) + "")
					.equals("null") ? "" : (xmlNode.getAttributes()
					.getNamedItem(key) + "");
			tm.set(key, value);
		}

		tm.set(NAME, name);
		tm.set(NODE, xmlNode);
		tm.set(ID, id);
		if (iconNode != null) {
			tm.set("icon", iconNode.getNodeValue());
		} else {
			tm.set("icon", xmlNode.getNodeName());
		}

		return tm;
	}

	/**
	 * 幫忙處理拖拉Tree節點時，將樹節點裡面包含的XML Node也進行更新處理
	 */
	protected void createTreeHandler() {

	}

	protected void publishXmlUpdate() {
		// 拖拉後，將整個XML字串整理好，發佈出去
		String xmlInfo = StringUtils.xmlPretty(doc.getFirstChild() + "");
		core.getBus().publish(new EventObject(evtGetXMLInfo(), xmlInfo));
	}

	public gkXMLTreeGridIC() {
		this(XDOM.getUniqueId());
	}

	public void createTreeNodeIconProvider(TreeGrid tree) {
		// 根據model裡面的icon屬性，設定圖示 (如果有的話)
		tree.setIconProvider(new ModelIconProvider<ModelData>() {

			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				String icon = model.get("icon");
				return icon == null ? null : iconProvider(icon);
			}
		});
	}

	/**
	 * 讓子類別改寫提供icon
	 * 
	 * @param name
	 * @return AbstractImagePrototype
	 */
	protected abstract AbstractImagePrototype iconProvider(String name);

	/**
	 * 建立滑鼠右鍵選單，將原本按左鍵會隱藏視窗功能Disable
	 * 
	 * @return Menu
	 */
	protected Menu createMenu() {
		return new Menu() {
			{
				sinkEvents(com.google.gwt.user.client.Event.KEYEVENTS);
			}

			@Override
			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
				if (event.getKeyCode() == 37) {
					event.stopPropagation();
				} else {
					super.onBrowserEvent(event);
				}
			}
		};
	}

	public void addMenuItem(String txt, ImageResource imgRes, EventProcess ep) {
		addMenuItem(txt, AbstractImagePrototype.create(imgRes), ep);
	}

	/**
	 * 增加滑鼠右鍵選單項目
	 * 
	 * @param txt
	 * @param iconStyle
	 * @param ep
	 */
	public void addMenuItem(String txt, String iconStyle, EventProcess ep) {
		addMenuItem(txt, IconHelper.create(iconStyle), ep);
	}

	/**
	 * 增加滑鼠右鍵選單項目
	 * 
	 * @param txt
	 * @param imgIcon
	 * @param ep
	 */
	protected void addMenuItem(final String txt,
			AbstractImagePrototype imgIcon, final EventProcess ep) {
		if (getContextMenu() == null) {
			setContextMenu(createMenu());
		}
		Menu menu = getContextMenu();
		MenuItem item = new MenuItem();
		item.setText(txt);
		item.setIcon(imgIcon);
		item.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// 如果Tree沒有Item被點選，md將會是 null
				ModelData md = getTree().getSelectionModel().getSelectedItem();
				gkMap m = new gkMap();
				if (md != null) {
					m.putAll(md.getProperties());
				}
				ep.execute(txt, new EventObject(txt, m));
			}
		});
		menu.add(item);
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}
}
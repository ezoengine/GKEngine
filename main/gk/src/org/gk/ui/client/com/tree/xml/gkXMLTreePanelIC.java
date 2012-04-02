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
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.utils.StringUtils;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * <title>XML樹控元件</title>
 * 
 * <pre>
 * 將XML轉成樹控元件的呈現方式，並支持本身節點拖拉，
 * 接收外部拖拉進來的節點
 * </pre>
 * 
 * @author I21890
 * @since 2010/08/14
 */
public abstract class gkXMLTreePanelIC extends ContentPanel implements IC {

	protected CoreIC core;
	protected TreePanel tree;
	protected TreeStore<ModelData> store;
	// XML's Document
	protected Document doc;
	public static final String NAME = "name"; // 顯示的節點名稱
	public static final String TEXT = "text"; //
	public static final String NODE = "node"; // XML節點屬性ID
	public static final String ID = "id"; // 唯一的识别ID
	public static final String ICON = "icon", PARENT_NODE = "parentNode",
			NODE_DATA = "nodeData", ELEMENT_ID = "elementId";

	protected static final int KeyCodeF2 = 113;
	protected static final int KeyCodeEnter = 13;
	protected static final String EditTree = "editTree";
	protected boolean inEdit = false; // 树节点是否为正在修改名字状态
	protected boolean hasChildren = false; // 設定是否有子節點

	public static interface Event {
		public final static String setXMLInfo = ".setXMLInfo";
		public final static String getXMLInfo = ".getXMLInfo";
		public final static String onClick = ".onClick";
		public final static String onMouseOver = ".onMouseOver";
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

	public String evtOnMouseOver() {
		return getId() + Event.onMouseOver;
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

	public gkXMLTreePanelIC(String id) {
		this(id, NAME);
	}

	public gkXMLTreePanelIC(String id, String displayProperty) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree("");
		// createTreeHandler();
		setLayout(new FitLayout());
		tree.setDisplayProperty(displayProperty);
		createTreeNodeIconProvider(tree);
		tree.setStateful(true);

		add(tree);
		setLayout(new FitLayout());
	}

	public gkXMLTreePanelIC(String id, String displayProperty, String treeType) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree(treeType);
		createTreeHandler();
		setLayout(new FitLayout());
		tree.setDisplayProperty(displayProperty);
		createTreeNodeIconProvider(tree);
		tree.setStateful(true);

		add(tree);
		setLayout(new FitLayout());
	}

	public gkXMLTreePanelIC(String id, String displayProperty, String treeType,
			String dragable) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree(treeType);
		if (dragable.equals("true")) {
			createTreeHandler();
		}
		setLayout(new FitLayout());
		tree.setDisplayProperty(displayProperty);
		createTreeNodeIconProvider(tree);
		tree.setStateful(true);
		tree.getStyle().setLeafIcon(iconProvider("file"));

		add(tree);
		setLayout(new FitLayout());
	}

	public TreePanel getTree() {
		return tree;
	}

	@Override
	public void setInfo(Object info) {
		store.removeAll(); // 清掉所有節點
		// 透過GWT API解析整個XML字串轉成doc物件
		String rootNmae = getRootNodeInfo().get(NAME);
		doc = XMLParser.parse("<" + rootNmae + " id='" + getId() + "' >" + info
				+ "</" + rootNmae + ">");
		XMLParser.removeWhitespace(doc.getFirstChild());
		// 解析doc's node資訊放到store裡面
		parseXmlDoc2Store(doc, store);
		// 重新繪製tree
		tree.repaint();
		tree.fireEvent(Events.Render);
	}

	/**
	 * 設定是否有子節點，預設為否
	 * 
	 * @param hasChildren
	 */
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
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

		// 监听编辑动作
		core.subscribe(evtOnEdit(), new EventProcess() {
			@Override
			public void execute(String eventId, EventObject e) {
				TreeNode tn = (TreeNode) e.getInfoMap().get(
						(tree.getSelectionModel().getSelectedItem()
								.getProperties().get(ID) + ""));
				// 取得当前的节点
				com.google.gwt.dom.client.Node nd = tn.getElement().getChild(0);
				// 生成一个input的element
				final Element input = DOM.createElement("input");
				// 取得当前节点的最后一个元素,也即时写有名称的那个span
				final com.google.gwt.dom.client.Node childnd = nd
						.getLastChild();
				// 保存span中的内容
				final String oldString = childnd.getFirstChild().getNodeValue();
				// 默认设定新的input为原来span中的内容
				input.setAttribute("value", oldString);
				// 设定新input的ID以便后面抓取
				input.setAttribute("id", "newText");
				// 保存原来span的element
				final com.google.gwt.dom.client.Node oldNode = childnd
						.getFirstChild().cloneNode(true);
				// 把原来span的位置替换成input以实现能编辑的功能
				childnd.replaceChild(input, childnd.getFirstChild());
				// 设定字符可以被选取
				tree.disableTextSelection(false);
				// 聚焦当前input
				input.focus();

				// 对新的input注册失去焦点的动作和keydown的动作
				DOM.sinkEvents(input, com.google.gwt.user.client.Event.ONBLUR
						| com.google.gwt.user.client.Event.ONKEYDOWN
						| com.google.gwt.user.client.Event.ONMOUSEOVER
						| com.google.gwt.user.client.Event.ONMOUSEOUT);
				// 监听新的input,当失去焦点或者按下enter的时候做更新动作
				DOM.setEventListener(input, new EventListener() {
					// 判断鼠标是否在input框内 防止点击的时候出发onblur动作
					boolean mouseIn = true;

					@Override
					public void onBrowserEvent(
							com.google.gwt.user.client.Event event) {
						if (event.getTypeInt() == com.google.gwt.user.client.Event.ONMOUSEOUT) {
							mouseIn = false;
						}
						if (event.getTypeInt() == com.google.gwt.user.client.Event.ONMOUSEOVER) {
							mouseIn = true;
						}

						if (event.getTypeInt() == com.google.gwt.user.client.Event.ONBLUR
								|| event.getKeyCode() == KeyCodeEnter) {
							if (event.getKeyCode() == KeyCodeEnter
									|| mouseIn == false) {
								// 取得此时input内的内容
								String newString = getvalue();
								// 如果为空则还原原来的内容
								if (newString.equals("")) {
									input.setAttribute("value", oldString);
								}
								// 把原span的内容换成input中的内容
								oldNode.setNodeValue(newString);
								// 在用span把input替换回来
								childnd.replaceChild(oldNode, input);
								// 对tree的modelData进行设定,其中会发布一个事件,ap可以根据此事件在做自己的调整
								setModelData(newString, tree);
								inEdit = false;
							}
						}
					}
				});
			}
		});
	}

	/**
	 * 展開樹控元件所有節點
	 */
	protected void expandAll() {
		// 必須在樹控元件繪製到畫面才能展開所有節點，因此必須透過監聽 Render事件
		tree.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				((TreePanel) be.getSource()).expandAll();
			}
		});
	}

	/**
	 * 建立樹控元件，使用EditTreePanel才能編輯樹節點名稱
	 * 
	 * @param treeType
	 */
	public void createTree(String treeType) {
		store = new TreeStore<ModelData>();

		store.setKeyProvider(new ModelKeyProvider<ModelData>() {

			@Override
			public String getKey(ModelData model) {
				if (!model.getProperties().containsKey(ID))
					model.set(ID, DOM.createUniqueId());
				return model.get(ID) + "";
			}
		});

		tree = new TreePanel(store) {
			@Override
			protected void onRender(Element target, int index) {
				clearState();
				super.onRender(target, index);
			}

			@Override
			public String getId() {
				return gkXMLTreePanelIC.this.getId();
			}

			@Override
			public void onComponentEvent(ComponentEvent ce) {
				TreePanelEvent tpe = (TreePanelEvent) ce;
				int type = ce.getEventTypeInt();
				switch (type) {
				case com.google.gwt.user.client.Event.ONMOUSEOVER:
					onMouseOver(tpe);
					break;
				case com.google.gwt.user.client.Event.ONCLICK:
					onClick(tpe);
					break;
				case com.google.gwt.user.client.Event.ONDBLCLICK:
					onDoubleClick(tpe);
					break;
				case com.google.gwt.user.client.Event.ONSCROLL:
					onScroll(tpe);
					break;
				case com.google.gwt.user.client.Event.ONFOCUS:
					onFocus(ce);
					break;
				}
				view.onEvent(tpe);
			}

			// 滑鼠移到樹節點上，將該節點資訊發佈出去
			protected void onMouseOver(TreePanelEvent tpe) {
				if (tpe.getNode() != null) {
					Map nodeInfo = (Map) tpe.getNode().getModel();
					nodeInfo.put(ELEMENT_ID, tpe.getNode().getElement().getId());
					core.getBus().publish(
							new EventObject(evtOnMouseOver(), nodeInfo));
				}
			}

			// 點選樹節點，將該節點資訊發佈出去
			@Override
			protected void onClick(TreePanelEvent tpe) {
				super.onClick(tpe);
				if (tpe.getNode() == null) {
					return;
				}
				Map nodeInfo = (gkMap) tpe.getNode().getModel();
				nodeInfo.put(ELEMENT_ID, tpe.getNode().getElement().getId());
				core.getBus().publish(new EventObject(evtOnClick(), nodeInfo));
			}

			// 雙擊樹節點，將該節點資訊發佈出去
			@Override
			protected void onDoubleClick(TreePanelEvent tpe) {
				super.onDoubleClick(tpe);
				if (tpe.getNode() == null) {
					return;
				}
				Map nodeInfo = (gkMap) tpe.getNode().getModel();
				nodeInfo.put(ELEMENT_ID, tpe.getNode().getElement().getId());
				core.getBus().publish(
						new EventObject(evtOnDoubleClick(), nodeInfo));
			}

			@Override
			protected boolean hasChildren(ModelData model) {
				if (hasChildren) {
					return super.hasChildren(model);
				}
				return true;
			}
		};
		// 设定树的类型为可编辑树
		if (treeType.equals(EditTree)) {
			tree.disableTextSelection(false);
			// 监听到F2建时修改动作 inEdit表示目前是否为编辑中的状态
			tree.addListener(Events.OnKeyDown, new Listener<TreePanelEvent>() {
				@Override
				public void handleEvent(TreePanelEvent be) {
					if (be.getKeyCode() == KeyCodeF2 && inEdit == false) {
						inEdit = true;
						Map modelData = tree.getSelectionModel()
								.getSelectedItem().getProperties();
						String treeNodeId = tree.getId() + "_"
								+ modelData.get(ID);
						Element el = DOM.getElementById(treeNodeId);
						TreeNode tn = tree.findNode(el);
						Map treeNode = new gkMap();
						treeNode.put(modelData.get(ID), tn);
						EventBus.get().publish(
								new EventObject(evtOnEdit(), treeNode));
					}
				}
			});
		}
	}

	/**
	 * 建立根節點
	 * 
	 * @param doc
	 * @param store
	 */
	protected void parseXmlDoc2Store(Document doc, TreeStore store) {
		Node rootNode = doc.getFirstChild();
		gkMap treeNode = getRootNodeInfo();
		treeNode.put("id", getId());
		treeNode.put(NODE, rootNode);
		// 將root node資料attribute放到 treeNode
		store.add(treeNode, false);
		preprocessNode(store, rootNode, treeNode);
	}

	/**
	 * 處理節點
	 * 
	 * @param store
	 * @param xmlNode
	 * @param treeNode
	 */
	public void preprocessNode(TreeStore store, Node xmlNode, ModelData treeNode) {
		NodeList xmlNodeList = xmlNode.getChildNodes();
		// 將所有子節點加入
		for (int i = 0; i < xmlNodeList.getLength(); i++) {
			Node subXMLNode = xmlNodeList.item(i);
			if (subXMLNode.getNodeType() == Node.COMMENT_NODE
					|| subXMLNode.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			gkMap subTreeNode = createTreeNode(subXMLNode);
			store.add(treeNode, subTreeNode, true);
			preprocessNode(store, subXMLNode, subTreeNode);
		}
	}

	public void expandAllNode(boolean expand) {
		getTree().getSelectionModel().deselectAll();
		if (expand) {
			getTree().disableEvents(true);
			getTree().expandAll();
			getTree().disableEvents(false);
		} else {
			getTree().disableEvents(true);
			getTree().collapseAll();
			getTree().disableEvents(false);
		}
	}

	public void expandNode(String value, boolean expand) {
		ModelData md = getTree().getStore().findModel(value);
		assert (md != null);
		getTree().getSelectionModel().deselectAll();
		getTree().getSelectionModel().select(true, md);
		getTree().setExpanded(md, expand);
	}

	public void expandNode(String key, String value, boolean expand) {
		ModelData md = getTree().getStore().findModel(key, value);
		assert (md != null);
		getTree().getSelectionModel().deselectAll();
		getTree().getSelectionModel().select(true, md);
		getTree().setExpanded(md, expand);
	}

	/**
	 * <pre>
	 * 取得根節點資訊,讓子類別決定根節點的名稱和顯示的文字
	 * 例如
	 * new dejgMap("name", "ui").fill("text", "GUL元件庫");
	 * name 節點名稱、text 節點顯示文字
	 * </pre>
	 * 
	 * @return gkMap
	 */
	protected abstract gkMap getRootNodeInfo();

	/**
	 * 定義XML哪些Node Name是容器，可以拖放節點進去 null表示都可以，new dejgList()表示都不行
	 * 
	 * @return List
	 */
	protected List getFolderNode() {
		return null;
	}

	/**
	 * 準備放到TreeNode的資訊
	 * 
	 * @param xmlNode
	 * @return gkMap
	 */
	public gkMap createTreeNode(Node xmlNode) {
		gkMap info = new gkMap();
		NamedNodeMap nameMap = xmlNode.getAttributes();
		for (int i = 0; i < nameMap.getLength(); i++) {
			Node attrNode = nameMap.item(i);
			info.put(attrNode.getNodeName(), attrNode.getNodeValue());
		}
		Node name = xmlNode.getAttributes().getNamedItem(NAME);
		Node id = xmlNode.getAttributes().getNamedItem(ID);
		Node icon = xmlNode.getAttributes().getNamedItem(ICON);
		info.put(NAME,
				name == null ? xmlNode.getNodeName() : name.getNodeValue());
		info.put(NODE, xmlNode);
		info.put(ID, id == null ? DOM.createUniqueId() : id.getNodeValue());
		info.put(ICON,
				icon == null ? xmlNode.getNodeName() : icon.getNodeValue());
		return info;
	}

	/**
	 * 幫忙處理拖拉Tree節點時，將樹節點裡面包含的XML Node也進行更新處理
	 */
	protected void createTreeHandler() {
		new gkTreeHandler(tree, Operation.MOVE) {

			@Override
			public void update(Map info) {
				publishXmlUpdate();
			}
		};
	}

	protected void publishXmlUpdate() {
		// 拖拉後，將整個XML字串整理好，發佈出去
		String xmlInfo = StringUtils.xmlPretty(doc.getFirstChild() + "");
		core.getBus().publish(new EventObject(evtGetXMLInfo(), xmlInfo));
	}

	public gkXMLTreePanelIC() {
		this(XDOM.getUniqueId());
	}

	public void createTreeNodeIconProvider(TreePanel tree) {
		// 根據model裡面的icon屬性，設定圖示 (如果有的話)
		tree.setIconProvider(new ModelIconProvider<ModelData>() {
			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				String icon = model.get(ICON);
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

	/**
	 * 取得input中的vaule GWT中取得的value不是实时变化的.
	 * 
	 * @return string
	 */
	private native String getvalue()/*-{
		var a = $wnd.document.getElementById('newText');
		return a.value;
	}-*/;

	/**
	 * 修改名称后的更改TreeModel
	 * 
	 * @param value
	 * @param tp
	 */
	private void setModelData(String value, TreePanel tp) {
		Map changeData = tp.getSelectionModel().getSelectedItem()
				.getProperties();
		changeData.put("newName", value);
		EventBus.get().publish(new EventObject("changeNodeName", changeData));
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}
}
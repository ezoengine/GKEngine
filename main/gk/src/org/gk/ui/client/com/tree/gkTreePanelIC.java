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
package org.gk.ui.client.com.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;
import jfreecode.gwt.event.client.bus.EventProcessImpl;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
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
import com.google.gwt.dom.client.Node;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 可拖拉並保存資料在Server端的Tree
 * 
 * @author I21890
 * @since 2010/01/11
 */
public class gkTreePanelIC extends ContentPanel implements IC {
	protected CoreIC core;

	@Override
	public CoreIC core() {
		return core;
	}

	public gkTreePanelIC(String id) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree("");
		setLayout(new FitLayout());
		tree.setDisplayProperty(NAME);
		createTreeNodeIconProvider(tree);
		initTree(tree);
		add(tree);
	}

	public gkTreePanelIC(String id, String treeType) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		createTree(treeType);
		setLayout(new FitLayout());
		tree.setDisplayProperty(NAME);
		createTreeNodeIconProvider(tree);
		initTree(tree);
		add(tree);
	}

	public TreePanel getTree() {
		return tree;
	}

	protected TreeLoader<ModelData> loader;
	protected TreeStore<ModelData> store;
	protected TreePanel<ModelData> tree;
	protected String beanName = "";
	public static final String NAME = "name";
	public static final String PATH = "path";
	protected static final String IS_LEAF = "isleaf";

	public static final String EditTree = "editTree";
	protected static final int KeyCodeF2 = 113;
	protected static final int KeyCodeEnter = 13;

	protected boolean inEdit = false;

	public static interface Event {
		public final static String setBean = ".setBean";
		public final static String onClick = ".onClick";
		public final static String onDoubleClick = ".onDoubleClick";
		public final static String afterLoad = ".afterLoad";
		public final static String onEdit = ".onEdit";
	}

	public String evtSetBean() {
		return getId() + Event.setBean;
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

	protected void createSubscribeEvent() {

	}

	@Override
	public void bindEvent() {
		core.subscribe(evtSetBean(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				setBean(eo.getInfoString());
			}
		});

		// 監聽編輯動作
		core.subscribe(evtOnEdit(), new EventProcess() {
			@Override
			public void execute(String eventId, EventObject e) {
				TreeNode tn = (TreeNode) e.getInfoMap().get(
						(tree.getSelectionModel().getSelectedItem()
								.getProperties().get(PATH) + ""));
				// 取得當前的節點
				final Node nd = tn.getElement().getChild(0);
				// 生成一個input的element
				final Element input = DOM.createElement("input");
				// 取得當前節點的最後一個元素，也即時寫有名稱的那個span
				final Node childnd = nd.getLastChild();
				// 保存span中的內容
				final String oldString = childnd.getFirstChild().getNodeValue();
				// 默認設定新的input為原来span中的內容
				input.setAttribute("value", oldString);
				// 設定新input的ID以便後面抓取
				input.setAttribute("id", "newText");
				// 保存原來span的element
				final Node oldNode = childnd.getFirstChild().cloneNode(true);
				// 把原來span的位置替换成input以實現能編輯的功能
				childnd.replaceChild(input, childnd.getFirstChild());
				// 設定字符可以被選取
				tree.disableTextSelection(false);
				// 聚焦當前input
				input.focus();

				// 對新的input註冊失去焦點的動作和keydown的動作
				DOM.sinkEvents(input, com.google.gwt.user.client.Event.ONBLUR
						| com.google.gwt.user.client.Event.ONKEYDOWN
						| com.google.gwt.user.client.Event.ONMOUSEOVER
						| com.google.gwt.user.client.Event.ONMOUSEOUT);
				// 監聽新的input，當失去焦點或者按下enter的時候做更新動作
				DOM.setEventListener(input, new EventListener() {
					// 判斷滑鼠是否在input框內，防止點擊的時候發出onblur動作
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
								// 取得此時input內的內容
								String newString = getvalue();
								// 如果為空則還原原來的內容
								if (newString.equals("")) {
									input.setAttribute("value", oldString);
								}
								// 把原span的內容換成input中的內容
								oldNode.setNodeValue(newString);
								// 再用span把input替換回來
								childnd.replaceChild(oldNode, input);
								// 對tree的modelData進行設定，其中會發布一個事件，ap可以根據此事件再做自己的調整
								setModelData(newString, tree);
								inEdit = false;
							}
						}
					}
				});
			}
		});
	}

	public void createTree(String treeType) {
		loader = new BaseTreeLoader<ModelData>(createProxy()) {
			// 以參數subnode決定是不是還有子節點
			@Override
			public boolean hasChildren(ModelData model) {
				Object obj = model.getProperties().get(IS_LEAF);
				return obj == null || !((Boolean) obj).booleanValue();
			}
		};
		store = new TreeStore<ModelData>(loader);
		store.setKeyProvider(new ModelKeyProvider<ModelData>() {
			@Override
			public String getKey(ModelData model) {
				return model.get(PATH) + "";
			}
		});
		tree = new TreePanel<ModelData>(store) {
			@Override
			protected void onClick(TreePanelEvent tpe) {
				super.onClick(tpe);
				if (tpe.getNode() == null) {
					return;
				}
				Map nodeInfo = new gkMap(tpe.getNode().getModel()
						.getProperties());
				core.getBus().publish(new EventObject(evtOnClick(), nodeInfo));
			}

			@Override
			protected void onDoubleClick(TreePanelEvent tpe) {
				super.onClick(tpe);
				Map nodeInfo = new gkMap(tpe.getNode().getModel()
						.getProperties());
				core.getBus().publish(
						new EventObject(evtOnDoubleClick(), nodeInfo));
			}

		};

		// 設定樹的類型為可編輯樹
		if (treeType.equals(EditTree)) {
			tree.disableTextSelection(false);
			// 監聽到F2鍵時修改動作，inEdit表示目前是否為編輯中的狀態
			tree.addListener(Events.OnKeyDown, new Listener<TreePanelEvent>() {
				@Override
				public void handleEvent(TreePanelEvent be) {
					if (be.getKeyCode() == KeyCodeF2 && inEdit == false) {
						inEdit = true;
						Map modelData = tree.getSelectionModel()
								.getSelectedItem().getProperties();
						String treeNodeId = tree.getId() + "_"
								+ modelData.get(PATH);
						Element el = DOM.getElementById(treeNodeId);
						TreeNode tn = tree.findNode(el);
						Map treeNode = new gkMap();
						treeNode.put(modelData.get(PATH), tn);
						EventBus.get().publish(
								new EventObject(evtOnEdit(), treeNode));
					}
				}
			});
		}
	}

	public gkTreePanelIC() {
		this(XDOM.getUniqueId());
	}

	public void setBean(String beanName) {
		this.beanName = beanName;
	}

	public void createTreeNodeIconProvider(TreePanel tree) {
	}

	/**
	 * 建立使用EventBus專屬的RpcProxy
	 * 
	 * @return RpcProxy
	 */
	private RpcProxy createProxy() {
		RpcProxy proxy = new RpcProxy() {

			@Override
			protected void load(final Object loadConfig,
					final AsyncCallback callback) {
				EventProcess loadProc = new EventProcessImpl(getId() + ".rpc") {

					@Override
					public void execute(String eventId, EventObject eo) {
						Map resp = eo.getInfoMap();
						gkList list = new gkList();
						Collection c = resp.values();
						Iterator it = c.iterator();
						while (it.hasNext()) {
							Object obj = it.next();
							if (obj instanceof Map) {
								BaseTreeModel b = new BaseTreeModel();
								b.setAllowNestedValues(false);
								b.setProperties((Map) obj);
								list.add(b);
							}
						}
						callback.onSuccess(list);
						core.getBus().publish(
								new EventObject(evtAfterLoad(), new gkMap()
										.fill("selectedModel", loadConfig)));
					}
				};
				BaseTreeModel btm = ((BaseTreeModel) loadConfig);
				// 取得當下要展開節點的路徑
				String path = btm == null ? "" : btm.getProperties().get(PATH)
						+ "";
				// 傳目前展開節點的path參數到後端,如果沒有設定Bean就不動作
				if (beanName != null && !beanName.equals("")) {
					core.getBus().publishRemote(
							new EventObject(beanName, path), loadProc);
				}
			}
		};
		return proxy;
	}

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
	 * AP可覆寫此方法來實現tree的各種功能
	 * 
	 * @param tree
	 */
	public void initTree(TreePanel tree) {

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
	 * 取得input中的value，GWT中取得的value不是實時變化的
	 * 
	 * @return string
	 */
	private native String getvalue()/*-{
		var a = $wnd.document.getElementById('newText');
		return a.value;
	}-*/;

	/**
	 * 修改名稱後的更改TreeModel
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
	public void setInfo(Object info) {
		throw new RuntimeException("setInfo(Object info) not yet implemented !");
	}

	@Override
	public Object getInfo() {
		throw new RuntimeException("setInfo(Object info) not yet implemented !");
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}
}

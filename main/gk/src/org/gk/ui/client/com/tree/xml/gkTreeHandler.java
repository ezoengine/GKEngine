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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.utils.TreeUtils;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.xml.client.Node;

/**
 * <pre>
 * 幫忙處理拖拉Tree節點時，將節點資訊中的XML Node物件
 * 進行更新處理，因為拖拉的節點可能包含其他節點，所以Node物件
 * 要拿掉原來的parentNode，重新加入新拖拉的parentNode
 * </pre>
 * 
 * @author I21890
 * @since 2010/07/16
 */
public abstract class gkTreeHandler {
	private TreePanelDropTarget target;

	public gkTreeHandler(TreePanel tree, final Operation op) {
		new TreePanelDragSource(tree) {
			/**
			 * 覆寫拖拉時不移除，這樣在drop才能透過TreeNode物件取得path
			 */
			@Override
			protected void onDragDrop(DNDEvent event) {
				event.setOperation(op);
			}
		};
		target = new TreePanelDropTarget(tree) {
			@Override
			protected void onDragFail(DNDEvent event) {
				// 修正當拖拉失敗時，tree的traceMouseOver失效
				tree.setTrackMouseOver(true);
				super.onDragFail(event);
			}

			@Override
			protected void showFeedback(DNDEvent event) {
				final TreeNode overItem = tree.findNode(event.getTarget());
				if (overItem == null) {
					clearStyles(event);
				}

				// 改寫部份:判斷目標節點不可為來源節點及其子節點
				if (overItem != null
						&& event.getDropTarget().getComponent() == event
								.getDragSource().getComponent()) {

					List<TreeModel> list = event.getData();
					ModelData overModel = overItem.getModel();
					for (int i = 0; i < list.size(); i++) {
						ModelData sel = (ModelData) list.get(i).get("model");
						if (overModel == sel) {
							clearStyles(event);
							return;
						}

						List<ModelData> children = tree.getStore().getChildren(
								sel, true);

						if (children.contains(overItem.getModel())) {
							clearStyles(event);
							return;
						}
					}
				}
				// ================================

				boolean append = feedback == Feedback.APPEND
						|| feedback == Feedback.BOTH;
				boolean insert = feedback == Feedback.INSERT
						|| feedback == Feedback.BOTH;

				if (overItem == null) {
					handleAppend(event, overItem);
				} else if (insert) {
					handleInsert(event, overItem);
				} else if ((!overItem.isLeaf() || isAllowDropOnLeaf())
						&& append) {
					handleAppend(event, overItem);
				} else {
					if (activeItem != null) {
						tree.getView().onDropChange(activeItem, false);
					}
					status = -1;
					activeItem = null;
					appendItem = null;
					Insert.get().hide();
					event.getStatus().setStatus(false);
				}

				// 新增部份
				if (activeItem == null) {
					event.getStatus().setStatus(false);
				}
			}

			@Override
			protected void onDragEnter(DNDEvent event) {
				removeDuplicateNode(event);

				super.onDragEnter(event);
				super.clearStyles(event);// 處理 IE 拖拉後選取項目樣式未被清除問題
			}

			private void removeDuplicateNode(DNDEvent event) {
				List<TreeStoreModel> modelList = (List<TreeStoreModel>) event
						.getData();

				// 1.收集 Parent Node
				Set<String> parentNodeSet = new HashSet<String>();
				for (TreeStoreModel model : modelList) {
					Node node = model.getModel().get("node");
					if (!model.isLeaf()) {
						parentNodeSet.add(node.toString());
					}
				}

				// 2.移除重複出現的 Leaf Node (Leaf's Parent in parentNodeSet)
				Iterator<TreeStoreModel> modelIt = modelList.iterator();
				while (modelIt.hasNext()) {
					TreeStoreModel model = modelIt.next();

					Node node = model.getModel().get("node");
					if (model.isLeaf()) {
						if (node.getParentNode() != null
								&& parentNodeSet.contains(node.getParentNode()
										.toString())) {
							modelIt.remove();
						}
					} else {
						parentNodeSet.add(node.toString());
					}
				}

				// 3.更新拖拉時顯示被選項目個數
				event.getStatus().update(
						Format.substitute(
								event.getDragSource().getStatusText(),
								modelList.size()));
			}

			/**
			 * 此方法在拖拉的節點放到指定節點上，放開滑鼠左鍵時觸發
			 */
			@Override
			protected void onDragDrop(DNDEvent e) {
				TreePanel srcTree = (TreePanel) e.getDragSource()
						.getComponent();
				if (activeItem == null) {
					super.onDragDrop(e);
					return;
				}
				List treeStoreModelList = (List) e.getData();

				// 要放置的XML Node物件
				Node dropNode = activeItem.getModel().get("node");
				Node dropParentNode = dropNode.getParentNode();

				// state=1,activeItem是目前要插入節點的上面那個Node
				// state=0,則是下面那個TreeNode
				// 因?xml doc的根??只能有一?.所以如果拖到根??以外就默??根???
				if (dropParentNode != null
						&& dropParentNode.toString()
								.equals(dropNode.toString())) {
					status = -1;
				}

				// Copy Mode
				// 1.相同來源節點可拖二次以上到同一個目標節點
				// 2.修正下面使用appendChild後，導致來源樹節點資料被更新的問題
				// 3.修正拖拉後子項目資料未被更新問題
				if (Operation.COPY == e.getOperation()) {
					List newModels = genNewTreeStoreModelList(srcTree,
							treeStoreModelList);
					e.setData(newModels);
					treeStoreModelList = newModels;
				}

				List<Node> dragNodeList = getNodeList(treeStoreModelList);

				String tarNodeId = "";
				// 2011/06/28 要解決無法拿到拖拉後放置的路徑，第幾個位置的問題
				TreeNode srcTreeNode = srcTree.findNode((Element) e
						.getDragEvent().getStartElement());
				switch (status) {
				case -1: // 節點裡面
					for (Node node : dragNodeList) {
						dropNode.appendChild(node);
					}

					tarNodeId = TreeUtils.getNodeId(activeItem, srcTreeNode,
							status);
					break;
				case 1: // 節點下面
					Node siblingNode = dropNode.getNextSibling();
					for (Node node : dragNodeList) {
						dropParentNode.insertBefore(node, siblingNode);
					}
					// 下一個位置
					tarNodeId = TreeUtils.getNodeId(activeItem, srcTreeNode, 1);
					break;
				case 0: // 節點上面
					for (Node node : dragNodeList) {
						dropParentNode.insertBefore(node, dropNode);
					}
					// 上一個位置
					tarNodeId = TreeUtils.getNodeId(activeItem, srcTreeNode, 0);
					break;
				}

				// Move Mode
				// 如果是搬移的話，就將來源tree拖拉資料移除掉
				if (Operation.MOVE == e.getOperation()) {
					List<TreeModel> sel = e.getData();
					for (TreeModel tm : sel) {
						srcTree.getStore().remove((ModelData) tm.get("model"));
					}
				}

				// 發佈事件通知更新
				update(compositeInfo(srcTree, tree,
						TreeUtils.getNodeId(srcTreeNode), tarNodeId));
				super.onDragDrop(e);
			}
		};
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH);
	}

	public gkTreeHandler(gkTreeDirPanelIC tree) {
		new TreePanelDragSource(tree) {
			/**
			 * 覆寫拖拉時不移除，這樣在drop才能透過TreeNode物件取得path
			 */
			@Override
			protected void onDragDrop(DNDEvent event) {
				// event.setOperation(op);
			}
		};
		target = new TreePanelDropTarget(tree) {
			@Override
			protected void onDragFail(DNDEvent event) {
				// 修正當拖拉失敗時，tree的traceMouseOver失效
				tree.setTrackMouseOver(true);
				super.onDragFail(event);
			}

			@Override
			protected void showFeedback(DNDEvent event) {
				final TreeNode overItem = tree.findNode(event.getTarget());
				if (overItem == null) {
					clearStyles(event);
				}

				// 改寫部份:判斷目標節點不可為來源節點及其子節點
				if (overItem != null
						&& event.getDropTarget().getComponent() == event
								.getDragSource().getComponent()) {
					List<TreeModel> list = event.getData();
					ModelData overModel = overItem.getModel();
					for (int i = 0; i < list.size(); i++) {
						ModelData sel = (ModelData) list.get(i).get("model");
						if (overModel == sel) {
							clearStyles(event);
							return;
						}

						List<ModelData> children = tree.getStore().getChildren(
								sel, true);
						if (children.contains(overItem.getModel())) {
							clearStyles(event);
							return;
						}
					}
				}
				// ================================

				boolean append = feedback == Feedback.APPEND
						|| feedback == Feedback.BOTH;
				boolean insert = feedback == Feedback.INSERT
						|| feedback == Feedback.BOTH;

				if (overItem == null) {
					handleAppend(event, overItem);
				} else if (insert) {
					handleInsert(event, overItem);
				} else if ((!overItem.isLeaf() || isAllowDropOnLeaf())
						&& append) {
					handleAppend(event, overItem);
				} else {
					if (activeItem != null) {
						tree.getView().onDropChange(activeItem, false);
					}
					status = -1;
					activeItem = null;
					appendItem = null;
					Insert.get().hide();
					event.getStatus().setStatus(false);
				}

				// 新增部份
				// activeItem是有機會在為null時，但status不為false的情形，所以加上這個判斷
				if (activeItem == null) {
					event.getStatus().setStatus(false);
				} else {
					if (event.getDragSource().getComponent() instanceof TreePanel) {
						// 當目標節點的子節點，與來源節點name相同時，不可放進目標節點
						List tarChildList = tree.getStore().getChildren(
								activeItem.getModel()); // 目標節點的所有子節點
						List srcNames = new ArrayList(); // 存放來源節點的name
						List<TreeModel> sel = event.getData();
						for (TreeModel tm : sel) {
							ModelData m = (ModelData) tm.get("model");
							srcNames.add(m.get("name"));
						}

						Iterator it = srcNames.iterator();
						while (it.hasNext()) {
							String name = it.next().toString();
							for (int i = 0; i < tarChildList.size(); i++) {
								ModelData child = (ModelData) tarChildList
										.get(i);
								if (name.equals(child
										.get(gkTreeDirPanelIC.NAME))) {
									event.getStatus().setStatus(false);
									break;
								}
							}
							if (!event.getStatus().getStatus()) {
								break;
							}
						}
					}
				}
			}

			/**
			 * 此方法在拖拉的節點放到指定節點上，放開滑鼠左鍵時觸發
			 */
			@Override
			protected void onDragDrop(DNDEvent e) {
				if (e.getDragSource().getComponent() instanceof TreePanel) {
					TreePanel srcTree = (TreePanel) e.getDragSource()
							.getComponent();

					List<TreeModel> srcData = (List) e.getData();
					ModelData tarData = activeItem.getModel();
					List srcPathList = new ArrayList(); // 儲存來源節點的path
					// 刪除資料後，才更新path資料，避免無法刪除原資料
					for (int i = 0; i < srcData.size(); i++) {
						TreeModel tm = srcData.get(i);
						ModelData m = (ModelData) tm.get("model");
						srcPathList.add(new gkMap("path", m.get(
								gkTreeDirPanelIC.PATH).toString()).fill("name",
								m.get(gkTreeDirPanelIC.NAME).toString()));
					}

					Map dataMap = compositeInfoDir(srcTree, tree, srcPathList,
							tarData);
					update(dataMap); // 發佈事件通知更新啦

				} else {
					List srcData = new ArrayList();
					ModelData tarData = activeItem.getModel();

					List dataList = (List) e.getData();
					Iterator it = dataList.iterator();
					while (it.hasNext()) {
						Object ob = it.next();
						if (ob instanceof ModelData) {
							ModelData temp = (ModelData) ob;
							HashMap map = new HashMap();
							if (temp.get("file") == null
									|| temp.get("path") == null) {
								continue;
							}
							map.put("file", temp.get("file"));
							map.put("path", temp.get("path"));
							srcData.add(map);
						}
					}
					Map dataMap = compositeInfoDir(e.getSource(), tree,
							srcData, tarData);
					update(dataMap); // 發佈事件通知更新啦
				}

				// 單純為了不讓畫面做新增節點的動作
				tree.getView().onDropChange(activeItem, false);
				activeItem = null;
				status = 0;

				super.onDragDrop(e);
			}
		};
		target.setAllowSelfAsSource(true);
		target.setAllowDropOnLeaf(true);
	}

	private Map compositeInfo(TreePanel srcTree, TreePanel tarTree,
			String srcNodeInfo, String tarNodeInfo) {
		Map info = new HashMap();
		String srcNodeId = srcNodeInfo.split(":")[0];
		info.put("src", new gkMap("treeId", srcTree.getId()).fill(
				gkXMLTreePanelIC.ID, srcNodeId));
		String tarNodeId = tarNodeInfo.split(":")[0];
		String tarIdx = tarNodeInfo.split(":")[1];
		info.put(
				"target",
				new gkMap("treeId", tarTree.getId()).fill(gkXMLTreePanelIC.ID,
						tarNodeId).fill("idx", tarIdx));
		return info;
	}

	private Map compositeInfoDir(Object srcObj, TreePanel tarTree,
			List srcChgData, ModelData tarData) {
		Map info = new HashMap();
		String srcNodeId = "";
		String srcNodeFile = "";
		String srcNodeName = "";
		for (Iterator it = srcChgData.iterator(); it.hasNext();) {
			Map map = (Map) it.next();
			srcNodeId += map.get("path") + ";";
			srcNodeFile += map.get("file") == null ? "" : map.get("file") + ";";
			srcNodeName += map.get("name") == null ? "" : map.get("name") + ";";
		}

		gkMap src = new gkMap(gkTreeDirPanelIC.ID, srcNodeId);
		if (srcNodeFile.length() > 0) {
			src.fill("files", srcNodeFile);
		}
		if (srcNodeName.length() > 0) {
			src.fill("names", srcNodeName);
		}
		info.put("src", src);

		String tarNodeId = tarData.get("path");
		info.put("target", new gkMap(gkXMLTreePanelIC.ID, tarNodeId));
		return info;
	}

	private List genNewTreeStoreModelList(TreePanel tree,
			List<TreeStoreModel> models) {
		List list = new ArrayList();

		for (TreeStoreModel model : models) {
			list.add(genNewTreeStoreModel(tree, model));
		}
		return list;
	}

	private TreeStoreModel genNewTreeStoreModel(TreePanel tree,
			TreeStoreModel model) {
		// 1.Clone Node
		Node cloneNode = ((Node) model.getModel().get("node")).cloneNode(true);

		// 2.Clone ModelData
		ModelData cloneModelData = (ModelData) gkMap.clone((gkMap) model
				.getModel());

		cloneModelData.set("node", cloneNode);

		// 3.Setting New ID
		setNewNodeId(cloneNode, cloneModelData);

		// 4.Update Child
		genNewChildren(tree, model);

		// 5.Rebuild parent-child relationships
		TreeStoreModel newModel = tree.getStore().getModelState(cloneModelData);
		newModel.setChildren((List) ((ArrayList<ModelData>) model.getChildren())
				.clone());

		return newModel;
	}

	private void genNewChildren(TreePanel tree, TreeStoreModel model) {
		List<ModelData> childModelList = new ArrayList<ModelData>();

		for (ModelData modelData : model.getChildren()) {
			TreeStoreModel newModel = genNewTreeStoreModel(tree,
					(TreeStoreModel) modelData);
			childModelList.add(newModel);
		}

		model.setChildren(childModelList);
	}

	private void setNewNodeId(Node node, ModelData modelData) {
		String id = modelData.get("id");
		if (id == null) {
			id = XDOM.getUniqueId();
		} else {
			id = id + "_" + XDOM.getUniqueId();
		}
		modelData.set("id", id);

		Node nodeId = node.getAttributes().getNamedItem("id");
		if (nodeId != null) {
			nodeId.setNodeValue(id);
		}
	}

	/**
	 * <pre>
	 * 提供拖拉的異動資訊 
	 * 第0個元素存放放置節點的path 
	 * 第1個之後是拖拉的節點 (考慮可能一次拖拉多個節點)
	 * </pre>
	 * 
	 * @param listInfo
	 */
	public abstract void update(Map info);

	/**
	 * @Description: 取得被拖拉的 Node 節點清單 (TreeStoreModel --> Node)
	 * @param models
	 * @return list List<Node>
	 * @author Jewway (I25974)
	 * @date 2012/6/6
	 */
	private List<Node> getNodeList(List<TreeStoreModel> models) {
		List<Node> list = new ArrayList<Node>();
		for (Object model : models) {
			// XML Node 物件
			Node dragNode = ((TreeStoreModel) model).getModel().get("node");
			list.add(dragNode);
		}

		return list;
	}
}

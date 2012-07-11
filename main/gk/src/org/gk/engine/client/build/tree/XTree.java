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
package org.gk.engine.client.build.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gk.engine.client.build.panel.XContentPanel;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.res.UIRes;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.tree.xml.gkTreeHandler;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.xml.client.Node;

/**
 * 包裝XMLTreePanelIC提供直接使用Tree
 * 
 * @author I21890
 * @since 2010/9/9
 */
public class XTree extends XContentPanel {

	protected String name, value, operation, hasChildren;
	protected String dragSource, dropTarget;
	protected String onClick, onExpand, onCollapse, onMouseOver;

	public XTree(Node node, List<UIGen> widgets) {
		super(node, widgets);
		// 預設根節點的顯示名稱
		name = super.getAttribute("name", "ROOT");
		value = super.getAttribute("value", "ROOT");
		operation = super.getAttribute("operation", "move");
		hasChildren = super.getAttribute("hasChildren", "false");
		dragSource = super.getAttribute("drag", "false");
		dropTarget = super.getAttribute("drop", "false");

		onClick = super.getAttribute("onClick", "");
		onExpand = super.getAttribute("onExpand", "");
		onCollapse = super.getAttribute("onCollapse", "");
		onMouseOver = super.getAttribute("onMouseOver", "");
	}

	@Override
	public Component build() {
		gkXMLTreePanelIC tree = new gkXMLTreePanelIC(id, "name", "", dragSource) {
			@Override
			protected gkMap getRootNodeInfo() {
				return new gkMap(gkXMLTreePanelIC.NAME, name).fill("text",
						value);
			}

			/**
			 * <pre>
			 * 處理onDrop事件發生時，提供拖拉的異動資訊 
			 * 第0個元素存放放置節點的path 
			 * 第1個之後是拖拉的節點 (考慮可能一次拖拉多個節點)
			 * </pre>
			 */
			@Override
			protected void createTreeHandler() {
				Operation op = operation.toLowerCase().equals("copy") ? Operation.COPY
						: Operation.MOVE;
				new gkTreeHandler(tree, op) {
					@Override
					public void update(Map info) {
						DNDEvent dnd = new DNDEvent(null);
						dnd.setData(info);
						EventCenter.exec(id, onDrop, XTree.this, dnd);
					}
				};
			}

			/**
			 * 透過IconHelper取得icon，若沒有，再透過UIRes提供icon，如果沒有設定則使用GXT Tree預設Icon
			 */
			@Override
			protected AbstractImagePrototype iconProvider(String name) {
				AbstractImagePrototype img = IconHelper.create(name);
				if (img == null) {
					ImageResource imgRes = UIRes.icon.get(name);
					img = imgRes == null ? null : AbstractImagePrototype
							.create(imgRes);
				}
				return img;
			}
		};
		this.initComponent(tree);

		Iterator<UIGen> it = widgets.iterator();
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			if (com instanceof Menu) {
				tree.setContextMenu((Menu) com);
			}
		}
		return tree;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		final gkXMLTreePanelIC tree = (gkXMLTreePanelIC) com;
		// 設定是否有子節點
		tree.setHasChildren(Boolean.parseBoolean(hasChildren));
		// 如果GUL語法有填寫onMouseOver事件，就進行監聽
		tree.getTree().addListener(Events.OnMouseOver,
				new Listener<TreePanelEvent>() {

					@Override
					public void handleEvent(TreePanelEvent tpe) {
						// 如果是點選展開節點，則不進行onClick動作
						// http://icsclink.appspot.com/event/put/x/file.download.go?1289444343944_882698.png
						String className = tpe.getTarget().getClassName()
								.trim();
						if (!className.equals("x-tree3-node-joint")
								&& tpe.getNode() != null) {
							EventCenter.exec(id, onMouseOver, XTree.this, tpe);
						}
					}
				});
		// 如果GUL語法有填寫onClick事件，就進行監聽
		tree.getTree().addListener(Events.OnClick,
				new Listener<TreePanelEvent>() {

					@Override
					public void handleEvent(TreePanelEvent tpe) {
						// 如果是點選展開節點，則不進行onClick動作
						// http://icsclink.appspot.com/event/put/x/file.download.go?1289444343944_882698.png
						String className = tpe.getTarget().getClassName()
								.trim();
						if (!className.equals("x-tree3-node-joint")) {
							EventCenter.exec(id, onClick, XTree.this, tpe);
						}
					}
				});
		// 節點閉合會調用 onCollapse事件
		tree.getTree().addListener(Events.Collapse,
				new Listener<TreePanelEvent>() {

					@Override
					public void handleEvent(TreePanelEvent tpe) {
						TreePanelSelectionModel sm = tree.getTree()
								.getSelectionModel();
						sm.select(tpe.getItem(), false);
						tree.getTree().setSelectionModel(sm);
						EventCenter.exec(id, onCollapse, XTree.this, tpe);
					}
				});
		// 節點展開會調用 onExpand事件
		tree.getTree().addListener(Events.Expand,
				new Listener<TreePanelEvent>() {

					@Override
					public void handleEvent(TreePanelEvent tpe) {
						EventCenter.exec(id, onExpand, XTree.this, tpe);
					}
				});
	}
}

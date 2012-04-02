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

import org.gk.engine.client.build.EngineDataStore;
import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.menu.XMenu;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.gen.UIGen;
import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkTreeHandler;

import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.xml.client.Node;

public class XTreeDir extends XComponent {

	protected String name;
	protected String drag;
	protected String onClick, onExpand, onCollapse, onMouseOver, onDrop;

	public XTreeDir(Node node, List<UIGen> widgets) {
		super(node, widgets);
		// 預設根節點的顯示名稱
		name = super.getAttribute("name", "ROOT");
		drag = super.getAttribute("drag", "false");

		onClick = super.getAttribute("onClick", "");
		onExpand = super.getAttribute("onExpand", "");
		onCollapse = super.getAttribute("onCollapse", "");
		onMouseOver = super.getAttribute("onMouseOver", "");
		onDrop = super.getAttribute("onDrop", "");
	}

	@Override
	public Component build() {
		gkTreeDirPanelIC tdp = new gkTreeDirPanelIC(getId());
		if (drag.equals("true")) {
			new gkTreeHandler(tdp) {
				@Override
				public void update(Map info) {
					DNDEvent dnd = new DNDEvent(null);
					dnd.setData(info);
					EventCenter.exec(id, onDrop, XTreeDir.this, dnd);
				}
			};
		}

		tdp.setRootName(name);
		tdp.getStyle().setLeafIcon(tdp.getStyle().getNodeCloseIcon()); // 設定leaf節點的icon也為資料夾
		this.initComponent(tdp);
		return tdp;
	}

	@Override
	public void init() {
		if (init.startsWith("bean:")) {
			gkTreeDirPanelIC tree = (gkTreeDirPanelIC) EngineDataStore
					.getComponent(getId());
			tree.setBean(init.substring(5));
		} else {
			super.init();
		}
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		final gkTreeDirPanelIC tree = (gkTreeDirPanelIC) com;
		// 如果GUL語法有填寫onMouseOver事件，就進行監聽
		tree.addListener(Events.OnMouseOver, new Listener<TreePanelEvent>() {

			@Override
			public void handleEvent(TreePanelEvent tpe) {
				// 如果是點選展開節點，則不進行onClick動作
				// http://icsclink.appspot.com/event/put/x/file.download.go?1289444343944_882698.png
				String className = tpe.getTarget().getClassName().trim();
				if (!className.equals("x-tree3-node-joint")
						&& tpe.getNode() != null) {
					EventCenter.exec(id, onMouseOver, XTreeDir.this, tpe);
				}
			}
		});
		// 如果GUL語法有填寫onClick事件，就進行監聽
		tree.addListener(Events.OnClick, new Listener<TreePanelEvent>() {

			@Override
			public void handleEvent(TreePanelEvent tpe) {
				// 如果是點選展開節點，則不進行onClick動作
				// http://icsclink.appspot.com/event/put/x/file.download.go?1289444343944_882698.png
				String className = "";
				if (tpe.getTarget() != null) {
					className = tpe.getTarget().getClassName().trim();
				}
				if (!className.equals("x-tree3-node-joint")) {
					EventCenter.exec(id, onClick, XTreeDir.this, tpe);
				}
			}
		});
		tree.addListener(Events.Collapse, new Listener<TreePanelEvent>() {

			@Override
			public void handleEvent(TreePanelEvent tpe) {
				TreePanelSelectionModel sm = tree.getSelectionModel();
				sm.select(tpe.getItem(), false);
				tree.setSelectionModel(sm);
				EventCenter.exec(id, onCollapse, XTreeDir.this, tpe);
			}
		});
		tree.addListener(Events.Expand, new Listener<TreePanelEvent>() {

			@Override
			public void handleEvent(TreePanelEvent tpe) {
				TreePanelSelectionModel sm = tree.getSelectionModel();
				sm.select(tpe.getItem(), false);
				tree.setSelectionModel(sm);
				EventCenter.exec(id, onExpand, XTreeDir.this, tpe);
			}
		});
		Iterator<XMenu> it = widgets.iterator();
		while (it.hasNext()) {
			XMenu menu = it.next();
			tree.setContextMenu((Menu) menu.build());
		}
	}
}

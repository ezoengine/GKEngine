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

import jfreecode.gwt.event.client.bus.EventObject;

import org.gk.engine.client.build.panel.XContentPanel;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.res.UIRes;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.tree.xml.gkXMLTreeGridIC;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.xml.client.Node;

/**
 * 包裝XMLTreeGridIC提供直接使用Tree
 * 
 * @author I21890
 * @since 2010/9/9
 */
public class XTreeGrid extends XContentPanel {

	protected String name, value;
	protected String autoExpand;
	protected String onClick;

	public XTreeGrid(Node node, List<UIGen> widgets) {
		super(node, widgets);
		// 預設根節點的顯示名稱
		name = super.getAttribute("name", "ROOT");
		value = super.getAttribute("value", "ROOT");
		autoExpand = super.getAttribute("autoExpand", "");

		onClick = super.getAttribute("onClick", "");
	}

	@Override
	public Component build() {
		final List fields = new gkList<ColumnConfig>();
		final List header = new gkList<Map>(); // HeaderGroupConfig
		final List aggRow = new gkList<Map>(); // Aggregation Row
		Iterator<UIGen> it = widgets.iterator();
		// grid可放ColumnConfig或HeaderGroupConfig或AggregationRowConfig
		boolean isSetRender = false;
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			Object obj = com.getData("columnConfig");
			if (obj instanceof ColumnConfig) {
				if (isSetRender == false) {
					((ColumnConfig) obj)
							.setRenderer(new TreeGridCellRenderer<ModelData>());
					isSetRender = true;
				}
				fields.add(obj);
			} else if (obj instanceof Map
					&& "aggRow".equals(((Map) obj).get("type") + "")) {
				aggRow.add(obj);
			} else {
				header.add(obj);
			}
		}
		gkXMLTreeGridIC tree = createTreeGrid(id, fields, header);
		// 找沒有註冊訂閱事件
		tree.bindEvent();
		this.initComponent(tree);
		tree.getTree().getTreeView().setBufferEnabled(false);
		return tree;
	}

	private gkXMLTreeGridIC createTreeGrid(String id, final List fields,
			final List<Map> header) {
		gkXMLTreeGridIC treeGrid = new gkXMLTreeGridIC(id) {

			@Override
			protected BaseTreeModel getRootNodeInfo() {
				BaseTreeModel btm = new BaseTreeModel();
				btm.set("name", name);
				btm.set("text", value);
				btm.set("id", name);
				return btm;
			}

			/**
			 * 透過UIRes提供icon，如果沒有設定則使用GXT Tree預設Icon
			 */
			@Override
			protected AbstractImagePrototype iconProvider(String name) {
				ImageResource img = UIRes.icon.get(name);
				return img == null ? null : AbstractImagePrototype.create(img);
			}

			@Override
			public ColumnModel createColumnModel() {
				attachGridHeaderGroup(header, cm);
				ColumnModel cm = new ColumnModel(fields);
				return cm;
			}
		};
		return treeGrid;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		gkXMLTreeGridIC tree = (gkXMLTreeGridIC) com;
		// 如果GUL語法有填寫onClick事件，就進行監聽
		tree.getTree().addListener(Events.OnClick,
				new Listener<TreeGridEvent>() {

					@Override
					public void handleEvent(TreeGridEvent tpe) {
						// 如果是點選展開節點，則不進行onClick動作
						// http://icsclink.appspot.com/event/put/x/file.download.go?1289444343944_882698.png
						String className = tpe.getTarget().getClassName()
								.trim();
						if (!className.equals("x-tree3-node-joint")) {
							EventCenter.exec(id, onClick, XTreeGrid.this, tpe);
						}
					}
				});
		if (!autoExpand.equals("")) {
			tree.getTree().setAutoExpandColumn(autoExpand);
		}
	}

	private void attachGridHeaderGroup(List<Map> header, ColumnModel cm) {
		Iterator<Map> it = header.iterator();
		while (it.hasNext()) {
			Map info = it.next();
			int row = Integer.parseInt("" + info.get("row"));
			int col = Integer.parseInt("" + info.get("col"));
			int rowSpan = Integer.parseInt("" + info.get("rowSpan"));
			int colSpan = Integer.parseInt("" + info.get("colSpan"));
			String name = (String) info.get("label");
			cm.addHeaderGroup(row, col, new HeaderGroupConfig(name, rowSpan,
					colSpan));
		}
	}

	@Override
	public void onInfo(String eventId, String content) {
		bus.publish(new EventObject(id + gkXMLTreeGridIC.Event.setXMLInfo,
				content));
	}
}

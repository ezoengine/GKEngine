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
package org.gk.engine.client.event.attrib;

import java.util.Map;

import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

/**
 * <title>目前此類別用於控制ContentPanel的Collapsible屬性</title>
 * 
 * @author I21890
 * @since 2010/10/15
 */
public class CollapseAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		// 設定contentPanel收摺
		if (com instanceof ContentPanel) {
			boolean isCollapse = Boolean.parseBoolean(value + "");
			ContentPanel cp = (ContentPanel) com;
			Object layoutData = cp.getData("layoutData");
			ContentPanel collpasePanel = cp.getData("collapse");
			if (layoutData instanceof BorderLayoutData) {
				// 當contentPanel在borderLayout中
				// region=center時，使用contentPanel原來的收摺功能
				BorderLayoutData bld = (BorderLayoutData) layoutData;
				if (bld.getRegion().equals(LayoutRegion.CENTER)) {
					cp.setExpanded(!isCollapse);
				} else {
					if (isCollapse) {
						if (cp.getParent() == null) {
							return;
						}
						LayoutContainer parent = (LayoutContainer) cp
								.getParent();
						BorderLayout parentLayout = (BorderLayout) parent
								.getLayout();
						parentLayout.collapse(bld.getRegion());
					} else {
						if (collpasePanel == null
								|| collpasePanel.getParent() == null) {
							return;
						}
						bld = (BorderLayoutData) collpasePanel
								.getData("layoutData");
						LayoutContainer parent = (LayoutContainer) collpasePanel
								.getParent();
						BorderLayout parentLayout = (BorderLayout) parent
								.getLayout();
						parentLayout.expand(bld.getRegion());
					}
				}
			} else {
				cp.setExpanded(!isCollapse);
			}
		}
		// 收摺Tree節點
		if (com instanceof gkXMLTreePanelIC) {
			gkXMLTreePanelIC tree = (gkXMLTreePanelIC) com;
			String nodeId = "";

			if (value == null) {
				// 收摺所有節點
				tree.expandAllNode(false);
			} else {
				// 收摺指定節點
				if (value instanceof Map) {
					Map map = (Map) value;
					assert map.containsKey(gkXMLTreePanelIC.ID) : "can't found nodeId:"
							+ map;
					nodeId = (String) map.get(gkXMLTreePanelIC.ID);
				} else {
					nodeId = value + "";
				}
				tree.expandNode(nodeId, false);
			}
		} else if (com instanceof gkTreeDirPanelIC) {
			gkTreeDirPanelIC tree = (gkTreeDirPanelIC) com;
			String nodeId = "";

			// 收摺指定節點
			if (value instanceof Map) {
				Map map = (Map) value;
				assert map.containsKey(gkTreeDirPanelIC.ID) : "can't found nodeId:"
						+ map;
				nodeId = (String) map.get(gkTreeDirPanelIC.ID);
			} else {
				nodeId = value + "";
			}
			ModelData md = tree.getStore().findModel(nodeId);
			assert (md != null);
			tree.setExpanded(md, false);
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		boolean isCollapse = false;
		if (com instanceof ContentPanel) {
			isCollapse = ((ContentPanel) com).isCollapsed();
		}
		return isCollapse;
	}
}

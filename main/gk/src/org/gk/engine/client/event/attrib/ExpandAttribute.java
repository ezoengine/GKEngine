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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.widget.Component;

public class ExpandAttribute implements IAttribute {

	/**
	 * 取得被點選的row資料
	 */
	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		return value;
	}

	/**
	 * 設定指定點選的Row
	 */
	@Override
	public void setAttributeValue(Component com, Object value) {
		// 展開Tree節點
		if (com instanceof gkXMLTreePanelIC) {
			gkXMLTreePanelIC tree = (gkXMLTreePanelIC) com;
			String nodeId = "";
			if (value == null) {
				// 展開所有節點
				tree.expandAllNode(true);
			} else {
				// 展開指定節點
				if (value instanceof Map) {
					Map map = (Map) value;
					assert map.containsKey(gkXMLTreePanelIC.ID) : "can't found nodeId:"
							+ map;
					nodeId = (String) map.get(gkXMLTreePanelIC.ID);
				} else {
					nodeId = value + "";
				}
				tree.expandNode(nodeId, true);
			}
		} else if (com instanceof gkTreeDirPanelIC) {
			gkTreeDirPanelIC tree = (gkTreeDirPanelIC) com;
			List node = new ArrayList();
			String nodeId = "";
			if (value == null) {

			} else {
				// 展開指定節點
				if (value instanceof Map) {
					Map map = (Map) value;
					assert map.containsKey(gkTreeDirPanelIC.ID) : "can't found nodeId:"
							+ map;
					nodeId = (String) map.get(gkTreeDirPanelIC.ID);
				} else {
					nodeId = value + "";
				}
				// 整理需要展開的節點路徑
				String[] nodeNameArray = nodeId.split("/");
				int recIndex = 0;
				for (int i = 0; i < nodeNameArray.length; i++) {
					if (nodeNameArray[i].equals("")) {
						continue;
					}
					int startIndex = nodeId.indexOf(nodeNameArray[i], recIndex);
					int endIndex = nodeId.indexOf("/", startIndex) + 1;
					String otherNodeId = nodeId.substring(0, endIndex);
					node.add(otherNodeId);
					recIndex = endIndex;
				}
				tree.expandNode(node);
			}
		}
	}
}

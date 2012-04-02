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

import org.gk.engine.client.Engine;
import org.gk.ui.client.com.grid.gkGridIC;
import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.xml.client.Node;

/**
 * Del屬性
 * 
 * @author i23250
 * @since 2011/3/26
 */
public class DelAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof gkGridIC) {
			((gkGridIC) com).removeRow();
		} else if (com instanceof gkTreeDirPanelIC) {
			gkTreeDirPanelIC tree = (gkTreeDirPanelIC) com;
			ModelData md = null;
			if (value instanceof Map) {
				Map info = (Map) value;
				assert info.containsKey(gkTreeDirPanelIC.ID) : "nodeId not Found "
						+ info;
				md = tree.getStore().findModel(
						(String) info.get(gkTreeDirPanelIC.ID));
			}
			assert md != null : "can't found TreeNode " + md;
			tree.getStore().remove(md);
		} else if (com instanceof gkXMLTreePanelIC) {
			gkXMLTreePanelIC tree = (gkXMLTreePanelIC) com;
			TreeStore store = tree.getTree().getStore();
			ModelData md = null;
			if (value instanceof Map) {
				Map info = (Map) value;
				assert info.containsKey(gkXMLTreePanelIC.ID) : "nodeId not Found "
						+ info;
				md = store.findModel((String) info.get(gkXMLTreePanelIC.ID));
			} else {
				md = tree.getTree().getSelectionModel().getSelectedItem();
			}
			assert md != null : "can't found TreeNode " + md;
			Node node = (Node) md.get(gkXMLTreePanelIC.NODE);
			node.getParentNode().removeChild(node);
			store.remove(md);
		} else if (com instanceof TabPanel) {
			if (value instanceof String) {
				TabPanel tabPanel = (TabPanel) com;
				tabPanel.remove(tabPanel.findItem((String) value, false));
				Engine.get().removeComponent((String) value);
			}
		} else if (com instanceof ComboBox) {
			ComboBox combo = (ComboBox) com;
			// 如果是comboBox , 可接受 字串或 Map
			if (value instanceof String) {
				// 根據value，於combo的PropertyEditor找到該ModelData
				ModelData md = combo.getPropertyEditor().convertStringValue(
						(String) value);
				// 於store內，移除該ModelData
				combo.getStore().remove(md);
				// 將最新的store存入combo的PropertyEditor
				combo.getPropertyEditor().setList(combo.getStore().getModels());
				// 將目前的value清空
				combo.clear();
			}
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		return null;
	}
}

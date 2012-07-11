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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.gkCheckBoxSelectionModel;
import org.gk.ui.client.com.grid.gkGridIC;
import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * Select屬性
 * 
 * @author i21890
 * @since 2010/10/13
 */
public class SelectAttribute implements IAttribute {

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof gkGridIC) {
			gkGridIC grid = (gkGridIC) com;
			// 取得所有勾選列資料，並重組成map
			value = grid.getSelectedRowItems();
		} else if (com instanceof ComboBox) {
			ComboBox combo = (ComboBox) com;
			value = combo.getValue();
		} else if (com instanceof TabPanel) {
			TabPanel tp = (TabPanel) com;
			value = tp.getSelectedItem().getId();
		} else if (com instanceof gkXMLTreePanelIC) {
			gkXMLTreePanelIC xtp = (gkXMLTreePanelIC) com;
			value = xtp.getTree().getSelectionModel().getSelectedItem();
		} else if (com instanceof gkTreeDirPanelIC) {
			gkTreeDirPanelIC xtp = (gkTreeDirPanelIC) com;
			value = xtp.getSelectionModel().getSelectedItem();
		}
		return value;
	}

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof gkGridIC) {
			gkGridIC grid = (gkGridIC) com;
			// 如果傳Collection表示要選擇指定的row (可以是數字或字串)
			if (value instanceof Collection) {
				grid.setSelectRowItem((Collection) value);
			} else if (value instanceof TreeMap) {
				TreeMap tm = (TreeMap) value;
				if (tm.size() == 1) {
					grid.setSelectRowItem((Map) tm.get(tm.firstKey()));
				}
			} else if (value instanceof String
					&& grid.getGrid().getSelectionModel() instanceof gkCheckBoxSelectionModel) {
				// for select all
				if ("all".equalsIgnoreCase((String) value)) {
					grid.getGrid().getSelectionModel().selectAll();
				}
			}
		} else if (com instanceof ComboBox) {
			ComboBox combo = (ComboBox) com;
			if (value instanceof String) {
				combo.setValue(combo.getPropertyEditor().convertStringValue(
						value + ""));
			} else {
				combo.setValue((gkMap) value);
			}
			combo.fireEvent(Events.Change);
		} else if (com instanceof TabPanel) {
			TabPanel tp = (TabPanel) com;
			tp.setSelection(tp.getItemByItemId(value + ""));
		} else if (com instanceof gkXMLTreePanelIC) {
			gkXMLTreePanelIC tree = (gkXMLTreePanelIC) com;
			String[] nodeIds;
			if (value instanceof Map) {
				nodeIds = ((String) ((Map) value).get(gkXMLTreePanelIC.ID))
						.split(",");
			} else {
				nodeIds = ((String) value).split(",");
			}

			tree.getTree().getSelectionModel().deselectAll();
			for (String nodeId : nodeIds) {
				ModelData md = tree.getTree().getStore().findModel(nodeId);
				tree.getTree().getSelectionModel().select(md, true);
				tree.getTree().scrollIntoView(md);
			}
		}
	}
}

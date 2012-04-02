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

import java.util.Arrays;
import java.util.List;

import org.gk.ui.client.com.grid.gkGridIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * Filter屬性
 * 
 * @author I21890
 * @since 2011/8/23
 */
public class FilterAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof gkXMLTreePanelIC) {
			gkXMLTreePanelIC tree = (gkXMLTreePanelIC) com;

			if (value == null) {
				tree.getTree().setExpandOnFilter(false);
				TreeStore store = tree.getTree().getStore();
				clearFilters(store);

				// 將tree節點展開
				statefulExpand(tree.getTree());
			} else if (value instanceof String) {
				tree.getTree().setExpandOnFilter(false);
				TreeStore store = tree.getTree().getStore();
				clearFilters(store);

				final List str = Arrays.asList(((String) value).split(","));
				StoreFilter filter = new StoreFilter() {
					@Override
					public boolean select(Store store, ModelData parent,
							ModelData item, String property) {

						String id = item.get(property);
						String[] strId = id.split("_");
						for (int i = 0; i < strId.length; i++) {
							if (str.contains(strId[i]))
								return false;
						}

						return true;
					}
				};

				store.addFilter(filter);
				store.filter(gkXMLTreePanelIC.ID);

				// 將tree節點展開
				statefulExpand(tree.getTree());
			}
		} else if (com instanceof gkGridIC) {
			gkGridIC grid = (gkGridIC) com;
			grid.filter(value.toString());
		}
	}

	/**
	 * 清除store裡的所有filter
	 * 
	 * @param store
	 */
	public void clearFilters(TreeStore store) {
		List filters = store.getFilters();
		if (filters != null) {
			for (int i = 0; i < filters.size(); i++) {
				store.removeFilter((StoreFilter) filters.get(i));
			}
		}
	}

	/**
	 * 利用tree的state，來展開節點
	 * 
	 * @param tree
	 */
	public void statefulExpand(TreePanel tree) {
		tree.disableEvents(true);
		TreeStore store = tree.getStore();
		List children = store.getRootItems();
		if (tree.isStateful() && store.getKeyProvider() != null) {
			List expanded = (List) tree.getState().get("expanded");
			if (expanded != null && expanded.size() > 0) {
				for (int i = 0; i < children.size(); i++) {
					ModelData child = (ModelData) children.get(i);
					String id = store.getKeyProvider().getKey(child);
					if (expanded.contains(id)) {
						tree.setExpanded(child, true);
					}
				}
			}
		}
		tree.disableEvents(false);
	}

	@Override
	public Object getAttributeValue(Component com) {
		return null;
	}
}

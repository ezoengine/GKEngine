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
package org.gk.engine.client.build.grid;

import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.engine.client.utils.NodeUtils;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.google.gwt.xml.client.Node;

public class XHeaderGroup implements UIGen {

	private String label;
	private String row;
	private String col;
	private String rowSpan;
	private String colSpan;

	public XHeaderGroup(Node node) {
		label = NodeUtils.getNodeValue(node, "label", "");
		row = NodeUtils.getNodeValue(node, "row", "0");
		col = NodeUtils.getNodeValue(node, "col", "0");
		rowSpan = NodeUtils.getNodeValue(node, "rowSpan", "1");
		colSpan = NodeUtils.getNodeValue(node, "colSpan", "1");
	}

	@Override
	public void init() {

	}

	@Override
	public Component build() {
		HeaderGroupConfig config;
		if (colSpan.matches(IRegExpUtils.INTEGER)
				&& rowSpan.matches(IRegExpUtils.INTEGER)) {
			config = new HeaderGroupConfig(label, Integer.parseInt(rowSpan),
					Integer.parseInt(colSpan));
		} else {
			config = new HeaderGroupConfig(label);
		}

		if (row.matches(IRegExpUtils.INTEGER)) {
			config.setRow(Integer.parseInt(row));
		}

		if (col.matches(IRegExpUtils.INTEGER)) {
			config.setColumn(Integer.parseInt(col));
		}

		Component com = new Component() {
		};
		com.setData("columnConfig", config);
		return com;
	}
}

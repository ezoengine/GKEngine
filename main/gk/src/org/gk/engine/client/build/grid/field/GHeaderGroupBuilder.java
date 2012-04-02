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
package org.gk.engine.client.build.grid.field;

import org.gk.engine.client.build.field.XField;

import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;

/**
 * 建立HeaderGroupConfig
 * 
 * @author I21890
 * @since 2011/5/25
 */
public class GHeaderGroupBuilder extends GridFieldBuilder {

	public GHeaderGroupBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public Object create() {
		XField x = getField().clone();
		String label = x.getLabel();
		String row = x.getAttribute("row", "0");
		String col = x.getAttribute("col", "0");
		String colSpan = x.getAttribute("colSpan", "0");
		String rowSpan = x.getAttribute("rowSpan", "0");

		HeaderGroupConfig config = new HeaderGroupConfig(label,
				Integer.parseInt(rowSpan), Integer.parseInt(colSpan));
		config.setRow(Integer.parseInt(row));
		config.setColumn(Integer.parseInt(col));
		return config;
	}
}
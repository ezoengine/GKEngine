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
package org.gk.ui.client.com.grid.column;

import org.gk.ui.client.com.form.gkLabelField;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public abstract class gkLabelColumnConfig extends gkCellColumnConfig {

	private String defValue;

	public gkLabelColumnConfig(gkColumnInfo field) {
		super(field);
		this.defValue = field.getValue();
	}

	@Override
	protected Field createField() {
		return new gkLabelField() {
			@Override
			public void setFieldLabel(String fieldLabel) {
				super.setFieldLabel(fieldLabel);
				setHeader(fieldLabel);
			}
		};
	}

	@Override
	protected Field createColumnCell(ModelData model, String property,
			ListStore<ModelData> store, int rowIndex, int colIndex,
			Grid<ModelData> grid) {
		return null;
	}

	@Override
	protected GridCellRenderer<ModelData> createCellRender() {
		GridCellRenderer<ModelData> fieldRender = new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				Object info = model.get(property);
				if (info == null) {
					info = defValue;
					model.set(property, info);
				}

				StringBuffer sb = new StringBuffer("<div id='");
				sb.append(getId()).append("_").append(rowIndex).append("'");
				sb.append(" style='").append(fieldInfo.getInputStyle())
						.append("' ");
				sb.append(getColumnAlign());
				sb.append(" title='").append(fieldInfo.getTitle()).append("' ");
				sb.append(">").append(info).append("</div>");
				return sb.toString();
			}
		};
		return fieldRender;
	}
}

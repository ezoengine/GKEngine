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

import java.util.List;

import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkListFieldIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkListColumnConfig extends gkCellColumnConfig {

	public gkListColumnConfig(gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createField() {
		return new gkListFieldIC() {
			@Override
			public void setFieldLabel(String fieldLabel) {
				super.setFieldLabel(fieldLabel);
				setHeader(fieldLabel);
			}
		};
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store, int rowIndex,
			int colIndex, Grid<ModelData> grid) {
		gkListFieldIC lf = (gkListFieldIC) createField();
		// 将更改后的选定项放入store
		lf.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				gkList selectList = new gkList(se.getSelection());
				model.set(property, selectList);
			}
		});

		onField(lf);

		Object value = model.get(property);
		if (value != null && value instanceof List) {
			lf.setSelectItem((List) value);
		} else {
			// 第一次显示，把初始值放入store
			model.set(property, lf.getSelectedItem());
		}
		addListener(lf, grid, rowIndex, colIndex, store);
		return lf;
	}
}

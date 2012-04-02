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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkSpinnerColumnConfig extends gkCellColumnConfig {

	public gkSpinnerColumnConfig(gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store, int rowIndex,
			int colIndex, Grid<ModelData> grid) {
		final Field sf = createField();
		// change事件，輸入值後運行
		sf.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				Object value = sf.getValue();
				model.set(property, value == null ? "" : value);
			}
		});
		// 在change事件之後，才初始化field
		onField(sf);
		// 當刷新欄位時運行。從store裡取得值，如果沒有這段，刷新出的欄位將沒有值
		Object value = model.get(property);
		if (value != null) {
			if (value.toString().length() > 0) {
				sf.setValue(sf.getPropertyEditor().convertStringValue(
						value.toString()));
			}
		} else {
			value = sf.getValue();
			model.set(property, value == null ? "" : value);
		}
		addListener(sf, grid, rowIndex, colIndex, store);
		return sf;
	}
}

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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkSliderFieldColumnConfig extends gkCellColumnConfig {

	public gkSliderFieldColumnConfig(gkColumnInfo columnInfo) {
		super(columnInfo);
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store,
			final int rowIndex, final int colIndex, final Grid<ModelData> grid) {

		SliderField sf = (SliderField) createField();
		final Slider slider = sf.getSlider();
		// change事件，输入值后运行
		slider.addListener(Events.Change, new Listener<SliderEvent>() {

			@Override
			public void handleEvent(SliderEvent be) {
				model.set(property, slider.getValue());
			}
		});
		// 当刷新栏位时运行。从store里取得值。如果没有这段刷新出的栏位将没有值
		Object value = model.get(property);
		if (value != null && value.toString().length() != 0) {
			slider.setValue(Integer.parseInt(value.toString()));
		} else {
			model.set(property, slider.getValue());
		}
		addListener(sf, grid, rowIndex, colIndex, store);
		onField(sf);
		return sf;
	}

	@Override
	public abstract Field createField();
}

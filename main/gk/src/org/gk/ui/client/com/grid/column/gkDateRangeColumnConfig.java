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

import org.gk.ui.client.com.form.gkDateField;
import org.gk.ui.client.com.form.gkDateRangeField;
import org.gk.ui.client.com.form.gkTimeField;
import org.gk.ui.client.com.utils.DateTimeUtils;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkDateRangeColumnConfig extends gkCellColumnConfig {

	public gkDateRangeColumnConfig(gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createColumnCell(final ModelData model, String property,
			ListStore<ModelData> store, int rowIndex, int colIndex,
			Grid<ModelData> grid) {
		gkDateRangeField drf = (gkDateRangeField) createField();
		LayoutContainer lc = (LayoutContainer) drf.getWidget();

		for (int i = 0; i < lc.getItemCount(); i++) { // 取出layoutConatainer裡面的物件
			Object obj = lc.getItem(i);
			if (obj instanceof gkDateField) { // 需要是field才作監聽動作
				final gkDateField df = (gkDateField) obj;
				// 如果重新render的時候，原本的就有值，要把那個值寫回去
				Object value = model.get(df.getId());
				if (value != null && value.toString().length() != 0) {
					DateTimeUtils.setValue(df, value.toString());
				} else {
					model.set(df.getId(), DateTimeUtils.getValue(df));
				}
				// 監聽事件
				df.addListener(Events.Change, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						model.set(df.getId(), DateTimeUtils.getValue(df));
					}
				});

				addListener(df, grid, rowIndex, colIndex, store);
			}
			if (obj instanceof gkTimeField) { // 需要是field才作監聽動作
				final gkTimeField tf = (gkTimeField) obj;
				// 如果重新render的時候，原本的就有值，要把那個值寫回去
				if (model.get(tf.getId()) != null
						&& model.get(tf.getId()).toString().length() != 0) {
					tf.setTimeValue(model.get(tf.getId()).toString());
				} else {
					model.set(tf.getId(), tf.getTimeValue());
				}

				// 監聽事件
				tf.addListener(Events.Change, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						model.set(tf.getId(), tf.getTimeValue());
					}
				});
				addListener(tf, grid, rowIndex, colIndex, store);
			}
		}
		onField(drf);
		return drf;
	}
}

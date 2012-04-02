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

import org.gk.ui.client.com.form.gkTextField;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkTextColumnConfig extends gkCellColumnConfig {

	public gkTextColumnConfig(final gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createField() {
		return new gkTextField() {
			@Override
			public void focus() {
				if (rendered) {
					getFocusEl().focus();
					onFocus(new FieldEvent(this));
				}
				if (!hasFocus) {
					fireEvent(Events.Focus);
				}
			}

			@Override
			public void setFieldLabel(String fieldLabel) {
				super.setFieldLabel(fieldLabel);
				setHeader(fieldLabel);
			}

			@Override
			protected void blur() {
				if (rendered) {
					getFocusEl().blur();
				}
				if (hasFocus) {
					fireEvent(Events.Blur);
					hasFocus = false;
				}
			}
		};
	}

	@Override
	protected Object createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store, int rowIndex,
			int colIndex, Grid<ModelData> grid) {
		final Field tf = createField();
		// change事件，输入值后运行
		tf.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				Object value = tf.getValue();
				model.set(property, value == null ? "" : value);
			}
		});
		// 在change事件之後，才初始化field
		onField(tf);
		// 当刷新栏位时运行。从store里取得值。如果没有这段刷新出的栏位将没有值
		Object value = model.get(property);
		if (value != null) {
			if (value instanceof String) {
				tf.setValue(tf.getPropertyEditor().convertStringValue(
						(String) value));
			} else {
				tf.setValue(value);
			}
		} else {
			value = tf.getValue();
			model.set(property, value == null ? "" : value);
		}
		addListener(tf, grid, rowIndex, colIndex, store);
		return tf;
	}
}

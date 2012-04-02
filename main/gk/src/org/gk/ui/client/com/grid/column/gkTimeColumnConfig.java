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

import org.gk.ui.client.com.form.gkTimeField;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkTimeColumnConfig extends gkCellColumnConfig {

	public gkTimeColumnConfig(gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createField() {
		return new gkTimeField() {
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
	protected CellEditor createCellEditor() {
		final gkTimeField tf = (gkTimeField) createField();
		addListener(tf);
		onField(tf);
		return new CellEditor(tf) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null || value instanceof ModelData) {
					return value;
				}
				return tf.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				return value == null ? "" : ((ModelData) value).get(tf
						.getValueField());
			}

			@Override
			public String getDisplayValue(Object value) {
				ModelData md = tf.findModel(value.toString());
				if (md != null) {
					return md.get(tf.getDisplayField());
				}
				return super.getDisplayValue(value);
			}
		};
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store,
			final int rowIndex, final int colIndex, final Grid<ModelData> grid) {
		final gkTimeField tf = (gkTimeField) createField();
		// change事件，输入值后运行
		tf.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				model.set(property, tf.getTimeValue());
			}
		});
		// 在change事件之後，才初始化field
		onField(tf);
		// 当刷新栏位时运行。从store里取得值。如果没有这段刷新出的栏位将没有值
		Object value = model.get(property);
		if (value != null && value.toString().length() != 0) {
			tf.setTimeValue(value.toString());
		} else {
			model.set(property, tf.getTimeValue());
		}
		addListener(tf, grid, rowIndex, colIndex, store);
		return tf;
	}
}

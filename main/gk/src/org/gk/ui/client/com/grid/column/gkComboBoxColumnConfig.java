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

import org.gk.ui.client.com.form.gkComboBox;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public abstract class gkComboBoxColumnConfig extends gkCellColumnConfig {

	public gkComboBoxColumnConfig(gkColumnInfo fieldInfo) {
		super(fieldInfo);
	}

	@Override
	protected Field createField() {
		gkComboBox combo = new gkComboBox() {
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
		combo.setUseQueryCache(false);
		return combo;
	}

	@Override
	protected CellEditor createCellEditor() {
		final gkComboBox combo = (gkComboBox) createField();
		addListener(combo);
		onField(combo);
		return new CellEditor(combo) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null || value instanceof ModelData) {
					return value;
				}
				return combo.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return "";
				}
				return ((ModelData) value).get("value");
			}

			@Override
			public String getDisplayValue(Object value) {
				if (value instanceof ModelData) {
					return ((ModelData) value).get("text");
				} else {
					ModelData md = combo.findModel(value.toString());
					if (md != null) {
						return md.get("text");
					}
				}
				return super.getDisplayValue(value);
			}
		};
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store, int rowIndex,
			int colIndex, Grid<ModelData> grid) {
		final gkComboBox cb = (gkComboBox) createField();
		onField(cb);
		cb.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				ModelData md = se.getSelectedItem();
				model.set(property, md != null ? md.get("value") : "");
			}
		});

		cb.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				ModelData md = cb.getValue();
				model.set(property, md != null ? md.get("value") : "");
			}
		});

		Object value = model.get(property);
		if (value != null) {
			if (value instanceof ModelData) {
				cb.setValue((ModelData) value);
			} else {
				cb.setValue(cb.getPropertyEditor().convertStringValue(
						value.toString()));
			}
		} else {
			ModelData md = cb.getValue();
			model.set(property, md != null ? md.get("value") : "");
		}
		addListener(cb, grid, rowIndex, colIndex, store);
		return cb;
	}
}

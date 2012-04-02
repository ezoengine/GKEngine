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
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public abstract class gkTriggerColumnConfig extends gkCellColumnConfig {

	public gkTriggerColumnConfig(gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createField() {
		TriggerField tf = new TriggerField() {
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

			@Override
			protected boolean validateBlur(DomEvent ce, Element target) {
				String str = getData("_gk_data"); // field的data屬性資料
				if (str != null && isInEditor()
						&& DOM.getElementById(str) != null) {
					return false;
				}
				return super.validateBlur(ce, target);
			}
		};
		tf.setTriggerStyle("x-form-search-trigger");
		return tf;
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store,
			final int rowIndex, final int colIndex, final Grid<ModelData> grid) {
		final TriggerField tf = (TriggerField) createField();
		// 當triggerField在grid裡面使用時，為了讓trigger input值更新到store，所以fire Change Event
		tf.addListener(Events.TriggerClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				tf.fireEvent(Events.Change);
			}
		});
		tf.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				Object value = tf.getValue();
				model.set(property, value == null ? "" : value);
			}
		});
		// 在change事件之後，才初始化field
		onField(tf);
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

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
package org.gk.engine.client.build.form.field;

import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.TriggerField;

public class TriggerFieldBuilder extends FormFieldBuilder {

	public TriggerFieldBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public Component create() {
		TriggerField field = new TriggerField() {
			@Override
			public void focus() {
				if (rendered) {
					getFocusEl().focus();
					onFocus(new FieldEvent(this));
				}
			}
		};
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		TriggerField field = new TriggerField() {
			@Override
			public void focus() {
				if (rendered) {
					getFocusEl().focus();
					onFocus(new FieldEvent(this));
				}
			}
		};
		form.fieldBinding(field, getField().getName());
		initField(field);
		return field;
	}

	private void initField(TriggerField field) {
		field.setFieldLabel(getField().getLabel());
		field.setTriggerStyle("x-form-search-trigger");

		String value = getField().getValue();
		if (!value.equals("")) {
			field.setValue(value);
			field.fireEvent(Events.Change);
		}
	}
}
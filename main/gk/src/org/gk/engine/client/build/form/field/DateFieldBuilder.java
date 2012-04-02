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

import org.gk.ui.client.com.form.gkDateField;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;

public class DateFieldBuilder extends FormFieldBuilder {

	public DateFieldBuilder(String date) {
		super(date);
	}

	@Override
	public Component create() {
		gkDateField field = new gkDateField(getField().getFormat());
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		gkDateField field = form.createDateField(getField().getName(),
				getField().getFormat());
		initField(field);
		return field;
	}

	private void initField(gkDateField field) {
		field.setFieldLabel(getField().getLabel());

		String value = getField().getValue();
		if (!value.equals("")) {
			field.setUseDate(value);
			field.fireEvent(Events.Change);
		}
	}
}

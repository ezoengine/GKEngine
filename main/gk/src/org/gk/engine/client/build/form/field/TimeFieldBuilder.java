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

import java.util.Map;

import org.gk.ui.client.binding.gkFieldBinding;
import org.gk.ui.client.binding.gkTimeFieldBinding;
import org.gk.ui.client.com.form.gkTimeField;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;

public class TimeFieldBuilder extends FormFieldBuilder {

	public TimeFieldBuilder(String time) {
		super(time);
	}

	@Override
	public Component create() {
		gkTimeField field = new gkTimeField();
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		gkTimeField field = new gkTimeField();
		gkFieldBinding fb = new gkTimeFieldBinding(field, getField().getName(),
				(Map) form.getInfo());
		form.addFieldBinding(fb);
		initField(field);
		return field;
	}

	private void initField(gkTimeField field) {
		field.setFieldLabel(getField().getLabel());

		String value = getField().getValue();
		if (!value.equals("")) {
			field.setTimeValue(value);
			field.fireEvent(Events.Change);
		}
	}
}

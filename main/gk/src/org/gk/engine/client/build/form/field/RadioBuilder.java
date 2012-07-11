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
import org.gk.ui.client.binding.gkRadioBinding;
import org.gk.ui.client.com.form.gkRadio;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Radio;

public class RadioBuilder extends FormFieldBuilder {

	public RadioBuilder(String radioBox) {
		super(radioBox);
	}

	@Override
	public Component create() {
		Radio field = new gkRadio();
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		final Map info = (Map) form.getInfo();
		final String infoKey = getField().getName();
		final String infoValue = getField().getValue();

		Radio field = new gkRadio() {

			@Override
			protected void onFocus(ComponentEvent ce) {
				if (!hasFocus) {
					if (!readOnly) {
						info.put(infoKey, infoValue);
					}
					super.onFocus(ce);
				}
			}
		};
		gkFieldBinding fb = new gkRadioBinding(field, getField().getName(),
				info, getField().getValue());
		form.addFieldBinding(fb);
		initField(field);
		return field;
	}

	private void initField(Radio field) {
		String label = getField().getLabel();
		String boxLabel = getField().getAttribute("boxLabel", label);
		String checked = getField().getAttribute("checked", "false");

		field.setFieldLabel(label);
		field.setBoxLabel(boxLabel);
		field.setValue(Boolean.parseBoolean(checked));
	}
}

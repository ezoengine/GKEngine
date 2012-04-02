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

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

public class CheckBoxBuilder extends FormFieldBuilder {

	public CheckBoxBuilder(String checkBox) {
		super(checkBox);
	}

	@Override
	public Component create() {
		CheckBox cb = new CheckBox() {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if ((boxLabelEl != null && boxLabelEl.dom.isOrHasChild(ce
						.getTarget())) || readOnly) {
					return;
				}
				fireEvent(Events.Select, ce);
			}
		};
		initField(cb);
		return cb;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		CheckBox cb = form.createCheckBox(getField().getName(), getField()
				.getValue());
		initField(cb);
		return cb;
	}

	private void initField(CheckBox cb) {
		String label = getField().getLabel();
		String boxLabel = getField().getAttribute("boxLabel", label);
		String checked = getField().getAttribute("checked", "false");

		cb.setFieldLabel(label);
		cb.setBoxLabel(boxLabel);
		cb.setValue(Boolean.parseBoolean(checked));
	}
}

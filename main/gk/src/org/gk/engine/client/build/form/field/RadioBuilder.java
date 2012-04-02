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

import com.extjs.gxt.ui.client.core.DomQuery;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;

public class RadioBuilder extends FormFieldBuilder {

	public RadioBuilder(String radioBox) {
		super(radioBox);
	}

	@Override
	public Component create() {
		Radio field = new Radio() {

			@Override
			public void focus() {
				if (rendered) {
					getFocusEl().focus();
					onFocus(new FieldEvent(this));
				}
			}

			@Override
			public void setValue(Boolean value) {
				if (rendered && value != null && value && group == null) {
					String select = "input[type='radio'][name='" + name + "']";
					NodeList list = DomQuery.select(select);
					for (int i = 0; i < list.getLength(); i++) {
						Element ele = (Element) ((Element) list.getItem(i))
								.getParentElement();
						if (ele.getId().equals(getId())) {
							continue;
						}
						Component com = ComponentManager.get().get(ele.getId());
						if (com instanceof Field) {
							Field fd = (Field) com;
							fd.setValue(false);
						}
					}
				}
				super.setValue(value);
			}

			@Override
			protected void onClick(ComponentEvent be) {
				// if we click the boxLabel, the browser fires an own click
				// event
				// automatically, so we ignore one of it
				if (boxLabelEl != null
						&& boxLabelEl.dom.isOrHasChild(be.getTarget())) {
					return;
				}
				if (readOnly) {
					be.stopEvent();
					return;
				}
				setValue(true);
				fireEvent(Events.Select, be);
			}
		};
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		Radio field = form.createRadio(getField().getName(), getField()
				.getValue());
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

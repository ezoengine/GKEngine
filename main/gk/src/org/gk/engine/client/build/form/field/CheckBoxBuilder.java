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

import java.util.List;
import java.util.Map;

import org.gk.ui.client.binding.gkCheckBoxBinding;
import org.gk.ui.client.binding.gkFieldBinding;
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
		final Map info = (Map) form.getInfo();
		final String infoKey = getField().getName();
		final String infoValue = getField().getValue();

		CheckBox field = new CheckBox() {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if ((boxLabelEl != null && boxLabelEl.dom.isOrHasChild(ce
						.getTarget())) || readOnly) {
					return;
				}
				fireEvent(Events.Select, ce);
			}

			@Override
			public void setValue(Boolean b) {
				super.setValue(b);
				Object value = info.get(infoKey);
				if (value instanceof List) {
					List cbList = (List) value;
					if (b != null && b) {
						// 如果是true而且不在cbList裡面，表示狀態更新了
						if (!cbList.contains(infoValue)) {
							cbList.add(infoValue);
							// 透過put發布InfoChange事件
							info.put(infoKey, cbList);
						}
					} else {
						// 如果在cbList裡面，表示狀態更新了
						if (cbList.contains(infoValue)) {
							cbList.remove(infoValue);
							// 透過put發布InfoChange事件
							info.put(infoKey, cbList);
						}
					}
				}
			}
		};

		gkFieldBinding fb = new gkCheckBoxBinding(field, infoKey, info,
				infoValue);
		form.addFieldBinding(fb);
		initField(field);
		return field;
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

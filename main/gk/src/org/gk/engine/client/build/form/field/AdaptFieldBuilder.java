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

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.form.XFormField;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.google.gwt.user.client.ui.Widget;

public class AdaptFieldBuilder extends FormFieldBuilder {

	public AdaptFieldBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public Component create() {
		return createField(null);
	}

	@Override
	public Component create(gkFormPanelIC form) {
		return createField(form);
	}

	private Field createField(gkFormPanelIC form) {

		XField xf = getField().clone();

		String vertical = xf.getAttribute("vertical", "false");
		String space = xf.getAttribute("space", "0");
		// 配合GAdaptFieldBuilder增加此屬性，判斷只有在Grid中的adaptField才使用form的Id當作RadioField的name
		String adaptingrid = xf.getAttribute("adaptingrid", "false");

		LayoutContainer lc;
		String ver = "";
		List childs = xf.getWidgets();
		// 若vertical為true，則實體面板為垂直面板，若false，則為水平面板
		if (Boolean.parseBoolean(vertical)) {
			lc = new VerticalPanel();
			((VerticalPanel) lc).setTableWidth("100%");
			ver = "bottom";
		} else {
			lc = new HorizontalPanel();
			if (childs.size() == 1) {
				((HorizontalPanel) lc).setTableWidth("100%");
			}
			ver = "right";
		}
		Iterator<XFormField> it = childs.iterator();
		while (it.hasNext()) {
			XFormField xField = it.next();
			xField.setForm(form);
			Component com = xField.build();
			if (com instanceof Button) {
				com.setStyleAttribute("padding-" + ver, space + "px");
			} else {
				com.setStyleAttribute("margin-" + ver, space + "px");
				if (com instanceof Radio && form != null
						&& Boolean.parseBoolean(adaptingrid)) {
					((Radio) com).setName(form.getId());
				}
			}
			lc.add(com);
		}
		AdapterField field = new AdapterField(lc) {
			@Override
			public void setReadOnly(boolean readOnly) {
				Widget widget = getWidget();
				if (widget instanceof Field) {
					((Field) widget).setReadOnly(readOnly);
				} else if (widget instanceof LayoutContainer) {
					setReadOnlyInAdaptField((LayoutContainer) widget, readOnly);
				}
			}
		};
		field.setFieldLabel(xf.getLabel());
		return field;
	}

	private void setReadOnlyInAdaptField(LayoutContainer lc, boolean readOnly) {
		Iterator<Component> comList = lc.iterator();
		while (comList.hasNext()) {
			Component c = comList.next();
			if (c instanceof Field) {
				((Field) c).setReadOnly(readOnly);
			}
		}
	}
}

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

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;

public class HtmlEditorBuilder extends FormFieldBuilder {

	public HtmlEditorBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public Component create() {
		return createField();
	}

	@Override
	public Component create(gkFormPanelIC form) {
		return createField();
	}

	private HtmlEditor createField() {
		HtmlEditor he = new HtmlEditor();
		he.setFieldLabel(getField().getLabel());

		String value = getField().getAttribute("value", "");
		if (!value.equals("")) {
			he.setValue(value);
		}
		return he;
	}
}

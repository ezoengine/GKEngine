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

import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.i18n.client.NumberFormat;

public class NumFieldBuilder extends FormFieldBuilder {

	public NumFieldBuilder(String num) {
		super(num);
	}

	@Override
	public Component create() {
		NumberField field = new NumberField();
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		NumberField field = form.createNumberField(getField().getName());
		initField(field);
		return field;
	}

	private void initField(NumberField field) {
		field.setFieldLabel(getField().getLabel());
		field.setInputStyleAttribute("text-align", "right");

		// 设定format
		String format = getField().getFormat();
		if (!format.equals("")) {
			field.setFormat(NumberFormat.getFormat(format));
			addNumListener(field);
		}
		String value = getField().getValue();
		if (value.matches(IRegExpUtils.FLOAT)) {
			field.setValue(Double.valueOf(value));
			field.fireEvent(Events.Change);
		}
	}

	/**
	 * 當手動輸入資料以後 觸發Change事件 重新做一次setValue，
	 * 如果有設定format就會有format的效果，否則手動輸入的數字沒有format的效果
	 * 
	 * @param field
	 */
	private void addNumListener(final NumberField field) {
		field.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				field.setValue(field.getValue());
			}
		});
	}
}

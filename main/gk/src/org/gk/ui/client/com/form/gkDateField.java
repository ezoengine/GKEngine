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
package org.gk.ui.client.com.form;

import org.gk.ui.client.com.i18n.CDateTimeFormat;
import org.gk.ui.client.com.utils.DateTimeUtils;

import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 日期欄位元件，提供設定畫面挑選元件要為西元年或是民國年格式
 * 
 * @author I23979,I23250
 * @since 2009/11/04
 */
public class gkDateField extends DateField {

	public gkDateField() {
		getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy/MM/dd"));
	}

	/**
	 * 設定日期顯示格式
	 * 
	 * @param pattern
	 */
	public void setFormat(String pattern) {
		pattern = DateTimeUtils.normalize(pattern);
		if (pattern.matches("[^y]*y{3}[^y]*")) {
			getPropertyEditor().setFormat(CDateTimeFormat.getFormat(pattern));
			getDatePicker().setDateType(DatePicker.CHINESE_YEAR);
		} else {
			getPropertyEditor().setFormat(DateTimeFormat.getFormat(pattern));
		}
	}

	@Override
	public void focus() {
		if (rendered) {
			getFocusEl().focus();
			onFocus(new FieldEvent(this));
		}
	}
}

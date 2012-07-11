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
package org.gk.ui.client.binding;

import java.util.Date;
import java.util.Map;

import org.gk.ui.client.com.form.gkYMField;
import org.gk.ui.client.com.utils.DateTimeUtils;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;

public class gkYMFieldBinding extends gkFieldBinding {

	public gkYMFieldBinding(Field field, String name, Map info) {
		super(field, name, info);
	}

	@Override
	public void execute(Object value) {
		DateTimeUtils.setValue((DateField) field, value + "");
		updateInfoValue(field.getValue());
	}

	@Override
	protected void updateInfoValue(Object value) {
		String result = "";
		if (value != null) {
			gkYMField ym = (gkYMField) field;
			if (ym.getYMPicker().isYearPicker()) {
				result = DateTimeUtils.formatYear((Date) value);
			} else {
				result = DateTimeUtils.formatYM((Date) value);
			}
		}
		info.put(name, result);
	}
}

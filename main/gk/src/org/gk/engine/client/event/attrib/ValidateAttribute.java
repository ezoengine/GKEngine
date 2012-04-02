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
package org.gk.engine.client.event.attrib;

import org.gk.ui.client.com.form.gkFormPanel;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * Validate屬性
 * 
 * @author I21890
 * @since 2010/11/29
 */
public class ValidateAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {

	}

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof gkFormPanel) {
			gkFormPanel form = (gkFormPanel) com;
			value = form.isValid();
		} else if (com instanceof Field) {
			Field field = (Field) com;
			value = field.isValid();
		}
		return value;
	}
}

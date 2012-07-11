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

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

/**
 * Checked屬性
 * 
 * @author i23250
 * @since 2012/6/22
 */
public class CheckedAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof CheckBox) {
			CheckBox cb = (CheckBox) com;
			cb.setValue(Boolean.parseBoolean(value + ""));
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof CheckBox) {
			CheckBox cb = (CheckBox) com;
			value = cb.getValue();
		}
		return value;
	}
}

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
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * MaxLength屬性
 * 
 * @author i23250
 * @since 2011/8/22
 */
public class MaxLengthAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof TextField) {
			int maxLength = Integer.parseInt("" + value);
			TextField tf = (TextField) com;
			tf.setMaxLength(maxLength);
			tf.el().firstChild().setElementAttribute("maxLength", maxLength);
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		if (com instanceof TextField) {
			return ((TextField) com).getMaxLength();
		}
		return null;
	}
}

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
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;

/**
 * Heading屬性
 * 
 * @author i23250
 * @since 2010/11/24
 */
public class HeadingAttribute implements IAttribute {

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof ContentPanel) {
			ContentPanel cp = (ContentPanel) com;
			value = cp.getHeading();
		} else if (com instanceof FieldSet) {
			FieldSet fs = (FieldSet) com;
			value = fs.getHeading();
		}
		return value;
	}

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof ContentPanel) {
			ContentPanel cp = (ContentPanel) com;
			cp.setHeading((String) value);
		} else if (com instanceof FieldSet) {
			FieldSet fs = (FieldSet) com;
			fs.setHeading((String) value);
		}
	}
}

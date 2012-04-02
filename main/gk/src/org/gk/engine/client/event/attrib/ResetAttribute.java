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

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * Reset屬性
 * 
 * @author i23250
 * @since 2011/7/12
 */
public class ResetAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof gkFormPanel) {
			((gkFormPanel) com).reset();
		} else if (com instanceof Field) {
			Field field = (Field) com;
			field.reset();
			field.fireEvent(Events.Change);
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		return null;
	}
}

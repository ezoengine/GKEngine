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

import org.gk.engine.client.build.grid.XGridField;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

/**
 * Label屬性
 * 
 * @author i21890
 * @since 2010/12/2
 */
public class LabelAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof Field) {
			((Field) com).setFieldLabel("" + value);
		} else if (com instanceof Button) {
			((Button) com).setText((String) value);
		} else if (com.getData(XGridField.COLUMN_CONFIG) instanceof ColumnConfig) {
			ColumnConfig cc = (ColumnConfig) com
					.getData(XGridField.COLUMN_CONFIG);
			cc.fireEvent(Events.HeaderChange, new BaseEvent("" + value));
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof Field) {
			Field field = (Field) com;
			value = field.getFieldLabel();
		} else if (com instanceof Button) {
			Button btn = (Button) com;
			value = btn.getText();
		}
		return value;
	}
}

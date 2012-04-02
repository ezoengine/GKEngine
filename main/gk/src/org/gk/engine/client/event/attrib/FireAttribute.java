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

import org.gk.engine.client.event.EventHandler;
import org.gk.engine.client.event.IEventConstants;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TriggerField;

/**
 * Fire屬性
 * 
 * @author i23250
 * @since 2011/9/22
 */
public class FireAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		String eventType = (String) value;
		if (eventType.equalsIgnoreCase(IEventConstants.EVENT_ONCLICK)) {
			if (com instanceof Button) {
				// 由於GXT元件的Button使用select，所以需要轉換
				eventType = IEventConstants.EVENT_ONSELECT;
			} else if (com instanceof TriggerField) {
				eventType = IEventConstants.EVENT_ONTRIGGERCLICK;
			}
		}
		com.fireEvent(EventHandler.getEventType(eventType));
	}

	@Override
	public Object getAttributeValue(Component com) {
		return null;
	}
}

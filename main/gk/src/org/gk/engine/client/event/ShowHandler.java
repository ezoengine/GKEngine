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
package org.gk.engine.client.event;

import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.event.EventValue.Type;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.user.client.Window;

/**
 * Show事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class ShowHandler extends EventHandler {

	@Override
	public void process(String xComId, List sources, List targets,
			XComponent xCom, BaseEvent be) {
		if (!sources.isEmpty()) {
			for (Object value : sources) {
				EventValue ev = EventFactory.convertToEventValue(value);
				String id = ev.getContent();
				if (ev.getType() == Type.EXPR) {
					id = eval(ev.getContent()) + "";
				}
				Window.alert(getAttributeValue(id) + "");
			}
		}
	}
}

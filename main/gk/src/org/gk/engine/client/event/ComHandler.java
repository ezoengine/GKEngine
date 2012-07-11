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

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.event.EventValue.Type;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Com事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class ComHandler extends EventHandler {

	@Override
	public void process(String xComId, List sources, List targets,
			XComponent xCom, BaseEvent be) {
		if (targets.isEmpty()) {
			EventValue ev = new EventValue();
			ev.setContent(xComId);
			ev.setType(Type.ID);
			targets.add(ev);
			doProcess(targets, sources, be);
		} else {
			doProcess(sources, targets, be);
		}
	}

	private void doProcess(List sources, List targets, BaseEvent be) {
		Object value = null;
		if (!sources.isEmpty()) {
			Object obj = sources.get(0);
			EventValue ev;
			if (obj instanceof EventValue) {
				ev = (EventValue) obj;
			} else {
				ev = EventFactory.convertToEventValue(sources.get(0));
			}
			String content = ev.getContent();
			if (ev.getType() == Type.EXPR) {
				value = eval(content);
			} else if (ev.getType() == Type.ID) {
				value = getAttributeValue(content);
				if (content.startsWith("!")) {
					value = reverseValue(getAttributeValue(content
							.replaceFirst("!", "")));
				} else if (content.startsWith(IEventConstants.TYPE_DATA)) {
					value = content.substring(1);
				}
			} else if (ev.getType() == Type.STRING) {
				value = content;
			}
		}

		if (value != null && !targets.isEmpty()) {
			for (Iterator it = targets.iterator(); it.hasNext();) {
				EventValue ev = EventFactory.convertToEventValue(it.next());
				String id = ev.getContent();
				if (ev.getType() == Type.EXPR) {
					id = eval(ev.getContent()) + "";
				}
				setAttributeValue(id, value);
			}
		}

		if (be != null && be.getSource() instanceof JavaScriptObject) {
			JavaScriptObject func = (JavaScriptObject) be.getSource();
			invokeFunction(func, value);
		}
	}

	/**
	 * 輸入的值若為"true"或"false"，則將值反轉
	 * 
	 * @param value
	 * @return Object
	 */
	private Object reverseValue(Object value) {
		Object result = value;
		if ("true".equals(value)) {
			result = "false";
		} else if ("false".equals(value)) {
			result = "true";
		} else if (Boolean.TRUE == value) {
			result = Boolean.FALSE;
		} else if (Boolean.FALSE == value) {
			result = Boolean.TRUE;
		}
		return result;
	}
}

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

import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Com事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class ComHandler extends EventHandler {

	@Override
	public void process(String xComId, String content, XComponent xCom,
			BaseEvent be) {
		JsArrayString split = splitContent(content);
		if (split.length() == 2) {
			doProcess(split.get(0), split.get(1), be);
		} else {
			doProcess(xComId, split.get(0), be);
		}
	}

	private void doProcess(String source, String target, BaseEvent be) {
		Object value = getValue(source);
		if (value == null) {
			if (source.startsWith("!")) {
				value = reverseValue(getAttributeValue(source.replaceFirst("!",
						"")));
			} else {
				value = getAttributeValue(source);
			}
		}
		if (value != null) {
			String[] comma = target.split(IEventConstants.SPLIT_COMMA);
			for (String id : comma) {
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

	private native JsArrayString splitContent(String content)/*-{
		content = content.replace('}:', '}&');
		if (content.indexOf('&') == -1) {
			content = content.replace(']:', ']&');
			if (content.indexOf('&') == -1) {
				content = content.replace(':', '&');
			}
		}
		return content.split('&');
	}-*/;
}

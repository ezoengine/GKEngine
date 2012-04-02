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

import java.util.Map;

import org.gk.engine.client.build.XComponent;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.core.client.JsArrayString;

/**
 * Pub事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class PubHandler extends EventHandler {

	@Override
	public void process(String xComId, String content, XComponent xCom,
			BaseEvent be) {
		Map info = new gkMap();
		info.put("src", xComId);
		info.put("url", getURL());

		JsArrayString split = splitContent(content);
		if (split.length() == 2) {
			Object value = getValue(split.get(1));
			if (value != null) {
				info.put(xComId, value);
			} else {
				info.putAll(getInfo(split.get(1)));
			}
		}
		EventCenter.publish(split.get(0), info);
	}

	protected native JsArrayString splitContent(String content)/*-{
		content = content.replace(':{', '&{');
		if (content.indexOf('&') == -1) {
			content = content.replace(':[', '&[');
			if (content.indexOf('&') == -1) {
				content = content.replace(':', '&');
			}
		}
		return content.split("&");
	}-*/;
}

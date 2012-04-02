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
package org.gk.engine.client.utils;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.JsonConvert;

import com.extjs.gxt.ui.client.util.Format;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 對字串進行轉換和處理
 * 
 * @author I21890
 * @since 2010/8/15
 */
public class StringUtils {

	/**
	 * 提供將json字串放到EventObject
	 * 
	 * @param eventId
	 * @param jsonString
	 * @return EventObject
	 */
	public static EventObject toEventObject(String eventId, String jsonString) {
		JSONValue val = JSONParser.parseLenient(jsonString);
		EventObject eo = null;
		if (jsonString.startsWith("{")) {
			eo = new EventObject(eventId, JsonConvert.jsonToMap(val.isObject()));
		} else if (jsonString.startsWith("[")) {
			eo = new EventObject(eventId, JsonConvert.jsonToList(val.isArray()));
		} else {
			eo = new EventObject(eventId, jsonString);
		}
		return eo;
	}

	/**
	 * 將Html特殊符號轉換成一般符號，如：&apos;轉成'，&semi;轉成;
	 * 
	 * @param value
	 * @return String
	 */
	public static String htmlDecode(String value) {
		return Format.htmlDecode(value).replace("&apos;", "'")
				.replace("&semi;", ";");
	}
}

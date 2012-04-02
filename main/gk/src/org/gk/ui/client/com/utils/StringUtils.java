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
package org.gk.ui.client.com.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class StringUtils {

	public final static String DATA = "_gk_data";

	/**
	 * 使用此方法在gwt.xml檔案必須引入格式化json字串的3rd JavaScript檔案 <script
	 * src="json2.js"></script>
	 * 
	 * @param json
	 * @return String
	 */
	public static String jsonPretty(String json) {
		if (json == null) {
			return "";
		}
		if (json.startsWith("[")) {
			JSONArray jobj = JSONParser.parseLenient(json).isArray();
			return jsonArray(jobj.getJavaScriptObject());
		} else if (json.startsWith("{")) {
			JSONObject jobj = JSONParser.parseLenient(json).isObject();
			return jsonMap(jobj.getJavaScriptObject());
		} else {
			return json;
		}
	}

	private static native String jsonMap(JavaScriptObject obj)/*-{
		//移除GWT自己加的obj Id
		delete obj['__gwt_ObjectId'];
		return $wnd.JSON.stringify(obj, null, '\t');
	}-*/;

	private static native String jsonArray(JavaScriptObject obj)/*-{
		return $wnd.JSON.stringify(obj, null, '\t');
	}-*/;

	public static native String xmlPretty(String xml)/*-{
		var reg = /(>)(<)(\/*)/g;
		var wsexp = / *(.*) +\n/g;
		var contexp = /(<.+>)(.+\n)/g;
		xml = xml.replace(reg, '$1\n$2$3').replace(wsexp, '$1\n').replace(
				contexp, '$1\n$2');
		var pad = 0;
		var formatted = '';
		var lines = xml.split('\n');
		var indent = 0;
		var lastType = 'other';
		// 4 types of tags - single, closing, opening, other (text, doctype, comment) - 4*4 = 16 transitions 
		var transitions = {
			'single->single' : 0,
			'single->closing' : -1,
			'single->opening' : 0,
			'single->other' : 0,
			'closing->single' : 0,
			'closing->closing' : -1,
			'closing->opening' : 0,
			'closing->other' : 0,
			'opening->single' : 1,
			'opening->closing' : 0,
			'opening->opening' : 1,
			'opening->other' : 1,
			'other->single' : 0,
			'other->closing' : -1,
			'other->opening' : 0,
			'other->other' : 0
		};

		for ( var i = 0; i < lines.length; i++) {
			var ln = lines[i];
			var single = Boolean(ln.match(/<.+\/>/)); // is this line a single tag? ex. <br />
			var closing = Boolean(ln.match(/<\/.+>/)); // is this a closing tag? ex. </a>
			var opening = Boolean(ln.match(/<[^!].*>/)); // is this even a tag (that's not <!something>)
			var type = single ? 'single' : closing ? 'closing'
					: opening ? 'opening' : 'other';
			var fromTo = lastType + '->' + type;
			lastType = type;
			var padding = '';
			indent += transitions[fromTo];
			if (ln.substr(0, 1) === '<') {
				for ( var j = 0; j < indent; j++) {
					padding += '    ';
				}
				if (formatted.substr(this.length - 1) != '\n'
						& formatted.length > 0) {
					formatted += '\n';
				}
				formatted += padding + ln;
			} else {
				formatted += ln + '\n';
			}
		}
		return formatted;
	}-*/;
}

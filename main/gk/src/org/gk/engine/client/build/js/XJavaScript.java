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
package org.gk.engine.client.build.js;

import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.obj.InfoList;
import jfreecode.gwt.event.client.bus.obj.InfoMap;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.event.EventHandler;
import org.gk.engine.client.event.IEventConstants;
import org.gk.engine.client.logging.EngineLogger;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Node;

public class XJavaScript extends XComponent {

	private static int IDIDX = 0;
	private static final String MAXIDLENGTH = "0000";

	private DNDEvent dndEvent;
	// 觸發事件的源頭資料 (從getData()取得)
	private String srcData;
	// 觸發此JavaScript的元件id
	private String comId;

	public XJavaScript(Node node) {
		super(node, null);
		Node firstNode = node.getFirstChild();
		if (firstNode != null) {
			content = firstNode.getNodeValue();
		}
		if (content == null || content.equals("null")) {
			content = "";
		}
	}

	public XJavaScript(String content) {
		id = "js-" + System.currentTimeMillis();
		this.content = content;
	}

	public XJavaScript(String id, String content) {
		this.id = id;
		this.content = content;
	}

	@Override
	public String getData() {
		return srcData;
	}

	public void setData(String srcData) {
		this.srcData = srcData;
	}

	public void setComId(String comId) {
		this.comId = comId;
	}

	public void setDNDEvent(DNDEvent be) {
		this.dndEvent = be;
	}

	public String getComId() {
		return comId;
	}

	@Override
	public Component build() {
		return null;
	}

	@Override
	public void init() {
		initJSMethod(this);
		super.init();
	}

	/**
	 * 提供透過JavaScript更新DNDEvent中的Data資料 DND資料內定為List格式
	 * 
	 * @param jso
	 */
	private void updateData(JavaScriptObject jso) {
		JSONValue json = new JSONArray(jso);
		Object obj = JsonConvert.jsonString2Object(json.toString());
		dndEvent.setData(obj);
	}

	public native void initJSMethod(XJavaScript xjavaScript)/*-{
		$wnd.gk.getData = function(id) {
			var value = xjavaScript.@org.gk.engine.client.build.js.XJavaScript::getData()();
			var c = value.charAt(0);
			return c == '[' || c == '{' ? eval('(' + value + ')') : value;
		}
		//JS如果有setData，將資料放到Event Object data裡面
		$wnd.gk.setData = function(data) {
			xjavaScript.@org.gk.engine.client.build.js.XJavaScript::updateData(Lcom/google/gwt/core/client/JavaScriptObject;)(data);
		}
		//在<js>區塊中調用 gk.event 會自動注入目前js區塊的id
		$wnd.gk.event = function(arg1, arg2, arg3) {
			//傳一個參數的處理
			switch (arguments.length) {
			case 1:
				var id = xjavaScript.@org.gk.engine.client.build.js.XJavaScript::getId()();
				@org.gk.engine.client.Engine::invokeEvent(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(id,arg1, null);
				break;
			//傳兩個參數的處理,傳兩個有可能是前兩個或後兩個參數
			case 2:
				//傳前兩個參數的處理 (指定id,事件字串)
				if (typeof arg2 == 'string') {
					@org.gk.engine.client.Engine::invokeEvent(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(arg1, arg2,null);
				} else {
					//傳後兩個參數的處理 (事件字串,call方法)
					var id = xjavaScript.@org.gk.engine.client.build.js.XJavaScript::getId()();
					@org.gk.engine.client.Engine::invokeEvent(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(id, arg1, arg2);
				}
				break;
			//傳三個參數的處理 (指定id,事件字串,call方法)
			case 3:
				@org.gk.engine.client.Engine::invokeEvent(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(arg1, arg2,arg3);
				break;
			}
		}
	}-*/;

	protected static void setAttributeValue(String id, Object value) {
		try {
			EventHandler.setAttributeValue(id, value);
		} catch (Exception e) {
			EngineLogger.log(e);
		}
	}

	protected static void setAttributeValue(String id, boolean isArray,
			JavaScriptObject jsObj) {
		Object info = isArray ? JsonConvert.jsonToList(new JSONArray(jsObj))
				: JsonConvert.jsonToMap(new JSONObject(jsObj));
		setAttributeValue(id, info);
	}

	/**
	 * 將資料轉成json格式，方便JavaScript使用
	 * 
	 * @param id
	 * @return String
	 */
	public static String getJSONValue(String id) {
		Object obj = EventHandler.getAttributeValue(id);
		if (obj == null) {
			return null;
		} else if (obj instanceof Map) {
			return new InfoMap((Map) obj).toString();
		} else if (obj instanceof List) {
			return new InfoList((List) obj).toString();
		} else {
			return "" + obj;
		}
	}

	/**
	 * 宣告觸發事件的元件id
	 * 
	 * @return String
	 */
	private String getJSContent() {
		return "var id = '" + getComId() + "';" + content;
	}

	/**
	 * 將JavaScript放到head執行
	 * 
	 * @return Object
	 */
	public native Object createScriptNodeToExecute()/*-{
		var content = this.@org.gk.engine.client.build.js.XJavaScript::getJSContent()();
		var id = this.@org.gk.engine.client.build.js.XJavaScript::getId()();
		var script = $doc.createElement("script");
		script.text = content;
		script.id = id;
		var header = $doc.getElementsByTagName("head")[0];
		header.appendChild(script);
		header.removeChild(script);
	}-*/;

	/**
	 * 觸發指定元件的事件
	 * 
	 * @param id
	 * @param eventType
	 */
	private static void fire(String id, String eventType) {
		assert (eventType != null);
		id = id + "." + IEventConstants.ATTRIB_FIRE;
		setAttributeValue(id, eventType);
	}

	/**
	 * 將gul字串中的 ${id}，取代自動建立不重複的id
	 * 
	 * @param gul
	 * @return String
	 */
	public static String genUniqueId(String gul) {
		int pos = 0;
		while ((pos = gul.indexOf("${id}")) >= 0) {
			String id = createUniqueId();
			gul = gul.substring(0, pos) + id + gul.substring(pos + 5);
		}
		// 下述寫法暫保留
		while ((pos = gul.indexOf("$id")) >= 0) {
			String id = createUniqueId();
			gul = gul.substring(0, pos) + id + gul.substring(pos + 3);
		}
		return gul;
	}

	/**
	 * 將gul字串中的 ${id}取代自動建立不重複的id，同時又可以將 ${自定變數}
	 * 
	 * @param gul
	 * @return String
	 */
	public static String createUniqueId(String gul) {
		// 元件庫入gul變數
		List<String> list = JsonConvert.jsonToList(new JSONArray(
				findReplaceAttributes(gul)));
		for (String key : list) {
			if (key.equals("$id") || key.equals("${id}")) {
				gul = genUniqueId(gul);
			} else {
				gul = gul.replace(key, createUniqueId());
			}
		}
		return gul;
	}

	/**
	 * 傳入GUL，取得需要取代的屬性清單
	 * 
	 * @param gul
	 * @return JavaScriptObject
	 */
	private static native JavaScriptObject findReplaceAttributes(String gul)/*-{
		var replaces = [];
		var split = gul.split(/(?=\$)|\'|\"/);
		for (i = 0; i < split.length; i++) {
			// 符合「${...}」或 「${...」才放入Array
			if (split[i].match(/\$\{.*\}$/) != null) {
				replaces.push(split[i]);
			}
		}
		return replaces;
	}-*/;

	/**
	 * 建立不重複的id
	 * 
	 * @return String
	 */
	private static String createUniqueId() {
		return XDOM.getUniqueId();
	}
}

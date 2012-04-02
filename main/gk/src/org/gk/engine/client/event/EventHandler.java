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
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBusIfc;
import jfreecode.gwt.event.client.bus.JsonConvert;

import org.gk.engine.client.IEngine;
import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.js.XJavaScript;
import org.gk.engine.client.event.attrib.IAttribute;
import org.gk.engine.client.exception.GKEngineException;
import org.gk.engine.client.exception.InvalidValueException;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.engine.client.utils.ComponentUtils;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * 事件處理器
 * 
 * <pre>
 * 可使用的事件種類:
 * pub:發佈本地端事件
 * bean:調用Spring元件，發佈遠端事件
 * file:讀取檔案(當使用靜態網頁才能使用)
 * sub:訂閱事件
 * com:直接改變另一元件
 * show:顯示訊息
 * js:編寫javascript
 * </pre>
 * 
 * @author i23250
 * @since 2010/9/24
 */
public abstract class EventHandler implements IHandler {

	private final static String JAVASCRIPT = "_gk_js_";

	protected EventBusIfc bus = IEngine.bus;

	// 存放能處理的事件指令 (pub,sub,file,bean...)
	private static Map<String, IHandler> handlerGroup = EventFactory
			.createHandlerGroup();
	// 存放能存取的屬性 (id,value,width...)
	private static Map<String, IAttribute> attributeGroup = EventFactory
			.createAttributeGroup();
	// 存放GXT的事件種類
	private static Map<String, EventType> eventTypeGroup = EventFactory
			.createEventTypeGroup();

	/**
	 * 根據輸入的cmd，取得對應的處理器介面
	 * 
	 * @param cmd
	 * @return IHandler
	 */
	private static IHandler getHandler(String cmd) {
		if (!handlerGroup.containsKey(cmd)) {
			throw new GKEngineException(
					EngineMessages.msg.error_handlerNotFound(cmd));
		}
		return handlerGroup.get(cmd);
	}

	/**
	 * 根據輸入的cmd，取得對應的屬性介面
	 * 
	 * @param cmd
	 * @return IAttribute
	 */
	private static IAttribute getAttribute(String cmd) {
		String key = cmd.toLowerCase();
		if (!attributeGroup.containsKey(key)) {
			// 找不到對應的屬性，則拋出訊息(不可丟出exception會導致之後的屬性沒有設定到)
			Info.display(EngineMessages.msg.warning(),
					EngineMessages.msg.error_attributeNotImplement(cmd));
		}
		return attributeGroup.get(key);
	}

	/**
	 * 根據輸入的eventType，取得對應的事件種類
	 * 
	 * @param eventType
	 * @return EventType
	 */
	public static EventType getEventType(String eventType) {
		String key = eventType.toLowerCase();
		if (!eventTypeGroup.containsKey(key)) {
			Info.display(EngineMessages.msg.warning(),
					EngineMessages.msg.error_eventTypeNotSupport(eventType));
		}
		return eventTypeGroup.get(key);
	}

	/**
	 * 此方法由EventCenter調用，開始執行事件解析 指派對應的Handler進行處理
	 * 
	 * @param xComId
	 * @param content
	 * @param xCom
	 * @param be
	 */
	public static void doProcess(String xComId, String content,
			XComponent xCom, BaseEvent be) {
		String[] args = content.split(IEventConstants.SPLIT_COLON);
		String cmd = args[0];
		IHandler handler = EventHandler.getHandler(cmd);
		handler.process(xComId,
				content.replaceFirst(cmd + IEventConstants.SPLIT_COLON, ""),
				xCom, be);
	}

	/**
	 * 取得元件屬性值
	 * 
	 * @param id
	 * @return Object
	 */
	public static Object getAttributeValue(String id) {
		Object value = null;
		String[] dot = id.split(IEventConstants.SPLIT_DOT);

		Component com = ComponentUtils.findComponent(dot[0]);
		if (com != null) {
			if (dot.length == 2) {
				value = getAttributeValue(com, dot[1]);
			} else if (dot.length == 1) {
				value = getAttributeValue(com);
			}
		}
		return value;
	}

	/**
	 * 取得元件屬性值
	 * 
	 * @param com
	 * @param attribute
	 * @return Object
	 */
	public static Object getAttributeValue(Component com, String attribute) {
		Object value = null;
		IAttribute attrib = getAttribute(attribute);
		if (attrib != null) {
			value = attrib.getAttributeValue(com);
		}
		return value;
	}

	/**
	 * 取得元件屬性值
	 * 
	 * @param com
	 * @return Object
	 */
	public static Object getAttributeValue(Component com) {
		return getAttributeValue(com, IEventConstants.ATTRIB_VALUE);
	}

	/**
	 * 設定元件屬性值
	 * 
	 * @param id
	 * @param value
	 */
	public static void setAttributeValue(String id, Object value) {
		// 若id為_gk_js_，則直接執行JavaScript腳本 (當後續種類變多時，再重構增加"預設行為分派類")
		if (id.equals(JAVASCRIPT)) {
			new XJavaScript(value + "").createScriptNodeToExecute();
		} else {
			String[] dot = id.split(IEventConstants.SPLIT_DOT);
			Component com = ComponentUtils.findComponent(dot[0]);
			if (com != null) {
				// 判斷是否有指定元件的屬性,例如grid1.value
				if (dot.length == 2) {
					setAttributeValue(com, dot[1], value);
				} else if (dot.length == 1) {
					setAttributeValue(com, value);
				}
			}
		}
	}

	/**
	 * 設定元件屬性值
	 * 
	 * @param com
	 * @param attribute
	 * @param value
	 */
	public static void setAttributeValue(Component com, String attribute,
			Object value) {
		IAttribute attrib = getAttribute(attribute);
		if (attrib != null) {
			try {
				attrib.setAttributeValue(com, value);
			} catch (Exception e) {
				throw new InvalidValueException(
						EngineMessages.msg.error_invalidValue(com.getId(),
								value));
			}
		}
	}

	/**
	 * 設定元件屬性值
	 * 
	 * @param com
	 * @param value
	 */
	public static void setAttributeValue(Component com, Object value) {
		setAttributeValue(com, IEventConstants.ATTRIB_VALUE, value);
	}

	/**
	 * 根據content，解析並取得Map
	 * 
	 * @param content
	 * @return Map
	 */
	protected Map getInfo(String content) {
		Map data = new gkMap();
		String[] comma = content.split(IEventConstants.SPLIT_COMMA);
		for (int i = 0; i < comma.length; i++) {
			Object value = getAttributeValue(comma[i]);
			if (value != null) {
				data.put(comma[i], value);
			}
		}
		return data;
	}

	/**
	 * 取得URL
	 * 
	 * @return String
	 */
	public static native String getURL()/*-{
		return $wnd.location.href;
	}-*/;

	/**
	 * 判斷是否為json字串或一般字串，並取得其值
	 * 
	 * @param str
	 * @return Object
	 */
	protected Object getValue(String str) {
		Object value = null;
		if (JsonConvert.isJSONString(str)) {
			value = JsonConvert.jsonString2Object(str);
		} else if (str.matches("^('|\"|#).*(('|\")$)?")) {
			if (str.startsWith("#")) {
				value = str.replaceFirst("#", "");
			} else {
				value = str.replaceAll("'|\"", "");
			}
		}
		return value;
	}

	/**
	 * 調用JavaScript的function
	 * 
	 * @param func
	 * @param data
	 */
	protected void invokeFunction(JavaScriptObject func, Object data) {
		if (data instanceof String) {
			invokeFunction(func, (String) data);
		} else if (data instanceof Map) {
			JSONObject json = JsonConvert.toJSONObject((Map) data);
			invokeFunction(func, json.getJavaScriptObject());
		} else if (data instanceof List) {
			JSONArray json = JsonConvert.toJSONArray((List) data);
			invokeFunction(func, json.getJavaScriptObject());
		} else {
			invokeFunction(func, data + "");
		}
	};

	private static native void invokeFunction(JavaScriptObject func,
			JavaScriptObject data)/*-{
		func(data);
	}-*/;

	private static native void invokeFunction(JavaScriptObject func, String data)/*-{
		func(data);
	}-*/;
}

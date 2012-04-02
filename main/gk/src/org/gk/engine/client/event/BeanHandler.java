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
import java.util.Map;
import java.util.Map.Entry;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;
import jfreecode.gwt.event.client.bus.obj.InfoList;
import jfreecode.gwt.event.client.bus.obj.InfoMap;
import jfreecode.gwt.event.client.bus.obj.InfoString;

import org.gk.engine.client.build.XComponent;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Bean事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class BeanHandler extends EventHandler {

	@Override
	public void process(String xComId, String content, XComponent xCom,
			BaseEvent be) {
		Map info = new gkMap();
		JavaScriptObject jso = null;

		// 除了init外，其他事件觸發時，皆會帶be，若是Field，則從be取得ComponentId
		if (be != null) {
			Object obj = be.getSource();
			if (obj instanceof Field) {
				xComId = ((Field) obj).getId();
			} else if (obj instanceof JavaScriptObject) {
				jso = (JavaScriptObject) obj;
			}
		}
		// src記錄此遠端事件是由哪個ComponentId發起的
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
		publishRemote(split.get(0), info, jso);
	}

	protected void publishRemote(String eventId, Map info,
			final JavaScriptObject jso) {

		EventObject eo = new EventObject(eventId, info);
		bus.publishRemote(eo, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				// 預期GK事件模型回傳的應該是Map型別，並將Map資訊送給GK引擎處理
				if (eo.getInfo() instanceof InfoMap) {
					Map info = eo.getInfoMap();
					Iterator it = info.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, Object> entry = (Entry) it.next();
						setAttributeValue(entry.getKey(), entry.getValue());
					}
				}
				// 如果前端gk.event有傳入callback function，則進行調用
				if (jso != null) {
					if (eo.getInfo() instanceof InfoMap) {
						invokeFunction(jso, eo.getInfoMap());
					} else if (eo.getInfo() instanceof InfoString) {
						invokeFunction(jso, eo.getInfoString());
					} else if (eo.getInfo() instanceof InfoList) {
						invokeFunction(jso, eo.getInfoList());
					}
				}
			}
		});
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
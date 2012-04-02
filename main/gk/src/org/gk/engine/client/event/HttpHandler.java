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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Http事件處理器
 * 
 * @author i21890
 * @since 2011/9/22
 */
public class HttpHandler extends BeanHandler {

	@Override
	protected void publishRemote(String eventId, Map info,
			final JavaScriptObject jso) {
		String remoteUrl = eventId;
		EventObject eo = new EventObject(eventId, info);
		bus.publishRemote(remoteUrl, eo, new EventProcess() {

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
}
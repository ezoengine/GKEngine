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
import java.util.Map;
import java.util.Map.Entry;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;
import jfreecode.gwt.event.client.bus.obj.InfoList;
import jfreecode.gwt.event.client.bus.obj.InfoMap;
import jfreecode.gwt.event.client.bus.obj.InfoString;

import org.gk.engine.client.Engine;
import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Bean事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class BeanHandler extends EventHandler {

	@Override
	public void process(String xComId, List sources, List targets,
			XComponent xCom, BaseEvent be) {
		// 除了init外，其他事件觸發時，皆會帶be，若是Field，則從be取得ComponentId
		JavaScriptObject jso = null;
		if (be != null) {
			Object obj = be.getSource();
			if (obj instanceof Field) {
				xComId = ((Field) obj).getId();
			} else if (obj instanceof JavaScriptObject) {
				jso = (JavaScriptObject) obj;
			}
		}
		publishRemote(prepareEventId(sources), prepareInfo(xComId, targets),
				jso);
	}

	protected void publishRemote(String eventId, Map info,
			final JavaScriptObject jso) {
		EventObject eo = new EventObject(eventId, info);
		EventProcess ep = new EventProcess() {

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
		};
		String gkPath = Engine.getGKPath();
		if (gkPath != null && gkPath.startsWith("http://")
				&& !eventId.startsWith("http://")) {
			String remoteUrl = gkPath + "/event/put/mobile/" + eventId;
			info.put("url", remoteUrl);
			bus.publishRemote(remoteUrl.substring(5), eo, ep);
		} else {
			bus.publishRemote(eo, ep);
		}
	}
}

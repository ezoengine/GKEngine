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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Sub事件處理器
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class SubHandler extends EventHandler {

	@Override
	public void process(String xComId, String content, XComponent xCom,
			BaseEvent be) {
		JavaScriptObject jso = null;
		if (be != null) {
			Object obj = be.getSource();
			if (obj instanceof JavaScriptObject) {
				jso = (JavaScriptObject) obj;
			}
		}
		subscribe(xComId, content, jso);
	}

	private void subscribe(final String xComId, String content,
			final JavaScriptObject jso) {
		final String colon[] = content.split(IEventConstants.SPLIT_COLON);
		EventCenter.subscribe(xComId, colon[0], new ISubscriber() {

			@Override
			public void execute(Object info) {
				// 預期Pub資訊應該是Map型別
				if (info instanceof Map) {
					Map map = (Map) info;
					if (colon.length == 2) {
						Object value = map.get(colon[1]);
						setAttributeValue(xComId, value);
					}
					// 如果前端gk.event有傳入callback function，則進行調用
					if (jso != null) {
						invokeFunction(jso, map);
					}
				}
			}
		});
	}
}

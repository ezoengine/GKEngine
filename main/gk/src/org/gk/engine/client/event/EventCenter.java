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

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.exception.GKEngineException;
import org.gk.engine.client.res.UIRes;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;

/**
 * 事件解析處理中心
 * 
 * <pre>
 * 事件處理指令寫法如下
 * ${cmd}:${eventId}:${componentId1},${componentId2}
 * 
 * 範例1:file:data.json
 * 範例2:pub:eventId:id1,id2
 * </pre>
 * 
 * @author I21890
 * @since 2010/7/26
 */
public class EventCenter {

	private static Map<String, List> subscribers = new gkMap();

	static {
		ScriptInjector.fromString(UIRes.get.parserJS().getText()).inject();
	}

	/**
	 * 執行事件語法
	 * 
	 * <p>
	 * 使用Object型別是由於切割 ui，engine兩個 package，不希望ui
	 * package中有參考到XComponent類別，所以轉型為Object處理
	 * </p>
	 * 
	 * @param xComId
	 * @param event
	 * @param com
	 * @param be
	 */
	public static void exec(String xComId, String eventString, Object com,
			BaseEvent be) {
		if (eventString.equals("") || !(com instanceof XComponent)) {
			return;
		}

		EventList events = EventFactory
				.convertToEventList(parseEventString(eventString));
		if (!events.isError()) {
			List lists = events.getEvents();
			for (Iterator it = lists.iterator(); it.hasNext();) {
				EventData ed = EventFactory.convertToEventData(it.next());
				EventHandler.doProcess(xComId, ed, (XComponent) com, be);
			}
		} else {
			throw new GKEngineException(events.getErrorMessage());
		}
	}

	/**
	 * 清除所有訂閱者
	 */
	public static void clear() {
		subscribers.clear();
	}

	/**
	 * 發佈事件
	 * 
	 * @param eventId
	 * @param info
	 */
	public static void publish(String eventId, Object info) {
		Iterator<List> it = subscribers.values().iterator();
		while (it.hasNext()) {
			for (Iterator<Map> it2 = it.next().iterator(); it2.hasNext();) {
				Map<String, ISubscriber> datas = it2.next();
				if (datas.containsKey(eventId)) {
					ISubscriber subscriber = datas.get(eventId);
					subscriber.execute(info);
				}
			}
		}
	}

	/**
	 * 移除輸入的訂閱者
	 * 
	 * @param subscriberId
	 * @return boolean
	 */
	public static boolean remove(String subscriberId) {
		return subscribers.remove(subscriberId) != null;
	}

	/**
	 * 訂閱事件
	 * 
	 * @param subscriberId
	 * @param eventId
	 * @param subscriber
	 */
	public static void subscribe(String subscriberId, String eventId,
			ISubscriber subscriber) {
		if (!subscribers.containsKey(subscriberId)) {
			subscribers.put(subscriberId, new gkList());
		}
		List list = subscribers.get(subscriberId);
		list.add(new gkMap(eventId, subscriber));
	}

	/**
	 * 透過gk_parse方法，將事件語法字串分割
	 * 
	 * @param eventString
	 * @return JavaScriptObject
	 */
	private static native JavaScriptObject parseEventString(String eventString)/*-{
		return gk_parse(eventString);
	}-*/;
}

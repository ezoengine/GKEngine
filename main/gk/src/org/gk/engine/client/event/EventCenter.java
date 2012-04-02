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
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.google.gwt.core.client.JsArrayString;

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
	public static void exec(String xComId, String events, Object com,
			BaseEvent be) {
		if (events.equals("") || !(com instanceof XComponent)) {
			return;
		}

		JsArrayString sp = splitEvent(events);
		for (int i = 0; i < sp.length(); i++) {
			// 檢查符合的話，就轉由EventHandler.doProcess開始進行事件解析與處理
			if (matchEventPattern(sp.get(i))) {
				EventHandler.doProcess(xComId, sp.get(i), (XComponent) com, be);
			}
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
	 * 將多個串聯在一起的事件語法依pub、bean、file、sub、com、show、js做分割
	 * 
	 * @param events
	 * @return JsArrayString
	 */
	private static native JsArrayString splitEvent(String events)/*-{
		return events.split(/,?(?=pub:|bean:|file:|sub:|com:|show:|js:)/);
	}-*/;

	/**
	 * 檢查是否符合事件撰寫語法
	 * 
	 * @param event
	 * @return boolean
	 */
	private static native boolean matchEventPattern(String event)/*-{
		return event.match(/\w+:.+(:.+)?/) != null;
	}-*/;
}

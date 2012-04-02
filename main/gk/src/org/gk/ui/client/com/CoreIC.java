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
package org.gk.ui.client.com;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventBusIfc;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.WidgetListener;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * IC的核心，有下列功能 (1)所有IC元件都必須複合此物件並實作 dejiCoreIC介面 (2)提供 Map儲存一些特定的資訊
 * (3)提供事件定閱並在解構時自動刪除定閱事件
 * 
 * @author I21890
 * 
 */
public class CoreIC {
	// 存放一些參數，方便資料交換和後端處理
	protected Map info = new gkMap();
	protected Component com;
	protected EventBusIfc bus = EventBus.get();
	protected Map<String, EventProcess> subEvent = new LinkedHashMap<String, EventProcess>();
	protected boolean detach = false;

	public Map getInfo() {
		return info;
	}

	public EventBusIfc getBus() {
		return bus;
	}

	public void setInfo(Map info) {
		this.info = info;
	}

	public void setInfo(Object key, Object value) {
		info.put(key, value);
	}

	public void putAllInfo(Map info) {
		info.putAll(info);
	}

	public CoreIC(Component com) {
		this.com = com;
	}

	/**
	 * 提供子類別進行事件的訂閱
	 * 
	 * @param eventId
	 * @param ep
	 */
	public void subscribe(String eventId, EventProcess ep) {
		bus.subscribe(eventId, ep); // 訂閱事件
		subEvent.put(eventId, ep); // 存放元件IC所訂閱的事件,當元件解構時清除定閱事件
	}

	public void init() {
		// 當元件解構時，將訂閱事件移除
		com.addListener(Events.Detach, new WidgetListener() {

			@Override
			public void widgetDetached(ComponentEvent ce) {
				Iterator<String> it = subEvent.keySet().iterator();
				while (it.hasNext()) {
					String eventId = it.next();
					EventProcess ep = subEvent.get(eventId);
					bus.removeSubscribe(eventId, ep);
				}
				detach = true;
			}
		});
		// 當元件建構時，如果先前已經建構過，就恢復原來的訂閱關係
		com.addListener(Events.Attach, new WidgetListener() {
			@Override
			public void widgetAttached(ComponentEvent ce) {
				if (!detach) {
					return;
				}
				detach = false;
				Iterator<String> it = subEvent.keySet().iterator();
				while (it.hasNext()) {
					String eventId = it.next();
					EventProcess ep = subEvent.get(eventId);
					bus.subscribe(eventId, ep);
				}
			}
		});
		((IC) com).bindEvent();
	}
}

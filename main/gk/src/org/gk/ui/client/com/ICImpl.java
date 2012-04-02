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

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.WidgetListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * <title>元件IC結構</title>
 * 
 * <pre>
 * 一個元件IC應有的方法
 * 1.boolean initEvent參數,儲存是否已經設定訂閱事件
 * 2.建構子必須傳入id,此id不能更改
 * 3.改寫 setId(String id)方法，初始化事件後要設定會拋Exception
 * 4.static Event介面，宣告有哪些發行或訂閱事件
 * 5.eventId_xxxx() 方法，組合元件ID.觸發動作
 * 6.eventRegistry() 方法，元件訂閱事件在此宣告
 * 7.在createSubEvent()方法，需增加 Events.Detach事件
 * </pre>
 * 
 * @author I21890
 * @since 2010-01-20
 */
public abstract class ICImpl extends LayoutContainer {
	protected boolean initEvent;
	protected EventBusIfc bus = EventBus.get();
	protected Map<String, EventProcess> subEvent = new LinkedHashMap<String, EventProcess>();

	public ICImpl(String id) {
		setId(id);
		init();
	}

	/**
	 * 提供子類別進行事件的訂閱
	 * 
	 * @param eventId
	 * @param ep
	 */
	protected void subscribe(String eventId, EventProcess ep) {
		subEvent.put(eventId, ep); // 存放元件IC所訂閱的事件
	}

	void init() {
		bindEvent();
		// 進行事件的訂閱
		Iterator<String> it = subEvent.keySet().iterator();
		while (it.hasNext()) {
			initEvent = true;
			String eventId = it.next();
			EventProcess ep = subEvent.get(eventId);
			bus.subscribe(eventId, ep);
		}
		// 當元件解構時，將訂閱事件移除
		addListener(Events.Detach, new WidgetListener() {
			@Override
			public void widgetDetached(ComponentEvent ce) {
				Iterator<String> it = subEvent.keySet().iterator();
				while (it.hasNext()) {
					String eventId = it.next();
					EventProcess ep = subEvent.get(eventId);
					bus.removeSubscribe(eventId, ep);
				}
				subEvent.clear();
			}
		});
	}

	/**
	 * IC元件必須實作此方法註冊事件 初始化事件的處理
	 */
	protected abstract void bindEvent();

	/**
	 * 當完成事件訂閱後，禁止重設id
	 */
	@Override
	public void setId(String id) {
		if (initEvent)
			throw new RuntimeException(
					"Modify the ID's actions must be taken before setting EventBus.");
		super.setId(id);
	}
}
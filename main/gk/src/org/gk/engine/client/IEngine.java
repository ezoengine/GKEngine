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
package org.gk.engine.client;

import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventBusIfc;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public interface IEngine {
	EventBusIfc bus = EventBus.get("gk");
	EventBusIfc builder = EventBus.get("builder");

	/**
	 * 根據 gul語法，產生畫面放在LayoutContainer裡面
	 * 
	 * @param gul
	 * @param lc
	 * @param clearAll
	 */
	void render(String gul, LayoutContainer lc, boolean clearAll);

	void render(String gul, LayoutContainer lc);

	/**
	 * 取得gul裡面有定義ID的任何Widget，AP在自行轉型使用
	 * 
	 * @param id
	 * @return Component
	 */
	Component getComponent(String id);

	/**
	 * 取得Engine處理的所有元件
	 * 
	 * @return Map
	 */
	Map getComponent();
}

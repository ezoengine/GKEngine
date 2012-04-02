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
package org.gk.engine.client.utils;

import org.gk.engine.client.build.EngineDataStore;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class ComponentUtils {

	/**
	 * 根據id，尋找畫面上的Component
	 * 
	 * @param id
	 * @return Component
	 */
	public static Component findComponent(String id) {
		// 先到GK引擎找
		Component com = EngineDataStore.getComponent(id);
		// 找不到就找所有GXT attach到DOM元件
		if (com == null) {
			com = ComponentManager.get().get(id);
		}
		// 找不到透過JQuery找，然後封裝成Component供後面使用，後面將直接取得element，也就是說gk.set、
		// gk.get可以直接從document取得任意element進行操控 (詳見 ValueAttribute)
		if (com == null) {
			Element ele = DOM.getElementById(id);
			if (ele != null) {
				com = new Component(ele, false) {
				};
			}
		}
		return com;
	}
}

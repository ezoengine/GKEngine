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
package org.gk.engine.client.build;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.gk.engine.client.exception.DuplicationIdException;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;

/**
 * Engine元件容器
 * 
 * @author i23250,I21890
 * @since 2010/9/6
 */
public class EngineDataStore {

	private static Set genNodeSequence = new LinkedHashSet<String>();

	/**
	 * 改寫put、remove方法，讓put時，放到當下放入的元件id 移除時也將其移除掉
	 * 透過genNodeSequence可知道引擎解析Node的順序
	 */
	private static Map<String, UIGen> uiGenNodeMap = new gkMap() {

		@Override
		public Object put(Object key, Object value) {
			genNodeSequence.add(key);
			return super.put(key, value);
		}

		@Override
		public Object remove(String key) {
			genNodeSequence.remove(key);
			return super.remove(key);
		}
	};

	private static Map<String, Component> componentMap = new gkMap();
	private static Map<String, UIGen> uiGenNodeMapBak = new gkMap();
	private static Map<String, Component> componentMapBak = new gkMap();

	/**
	 * 備份
	 */
	public static void backup() {
		uiGenNodeMapBak.putAll(uiGenNodeMap);
		componentMapBak.putAll(componentMap);
	}

	/**
	 * 回復
	 */
	public static void rollback() {
		uiGenNodeMap.clear();
		uiGenNodeMap.putAll(uiGenNodeMapBak);
		componentMap.clear();
		componentMap.putAll(componentMapBak);
		genNodeSequence.clear(); // 當拋出例外時，這個可能會沒清空，所以加上這行
	}

	/**
	 * 清除備份
	 */
	public static void clearBackup() {
		uiGenNodeMapBak.clear();
		componentMapBak.clear();
	}

	/**
	 * 加入元件
	 * 
	 * @param key
	 * @param uiGen
	 */
	public static void addUIGenNode(String key, UIGen uiGen) {
		// 保證不重複
		if (uiGenNodeMap.containsKey(key)) {
			throw new DuplicationIdException(
					EngineMessages.msg.error_idDuplication(key));
		}
		uiGenNodeMap.put(key, uiGen);
	}

	/**
	 * 移除元件
	 * 
	 * @param key
	 */
	public static boolean removeUIGenNode(String key) {
		return uiGenNodeMap.remove(key) != null;
	}

	/**
	 * 清空元件容器
	 */
	public static void clearUIGenNodeMap() {
		uiGenNodeMap.clear();
	}

	/**
	 * 取得元件容器大小
	 * 
	 * @return int
	 */
	public static int getUIGenNodeMapSize() {
		return uiGenNodeMap.size();
	}

	/**
	 * 取得元件容器
	 * 
	 * @return Map
	 */
	public static Map getUIGenNodeMap() {
		return Collections.unmodifiableMap(uiGenNodeMap);
	}

	/**
	 * 取得元件
	 * 
	 * @param key
	 * @return UIGen
	 */
	public static UIGen getUIGenNode(String key) {
		return uiGenNodeMap.get(key);
	}

	/**
	 * 加入GXT元件
	 * 
	 * @param key
	 * @param com
	 */
	public static void addComponent(String key, Component com) {
		// 保證不重複
		if (componentMap.containsKey(key)) {
			throw new DuplicationIdException(
					EngineMessages.msg.error_idDuplication(key));
		}
		componentMap.put(key, com);
	}

	/**
	 * 移除GXT元件
	 * 
	 * @param key
	 */
	public static boolean removeComponent(String key) {
		hideWindow(key);
		return componentMap.remove(key) != null;
	}

	/**
	 * 清空GXT元件容器
	 */
	public static void clearComponentMap() {
		Iterator<String> it = componentMap.keySet().iterator();
		while (it.hasNext()) {
			hideWindow(it.next());
		}
		componentMap.clear();
	}

	private static void hideWindow(String key) {
		Component com = componentMap.get(key);
		if (com instanceof Window) {
			com.hide();
		}
	}

	/**
	 * 取得GXT元件容器
	 * 
	 * @return Map
	 */
	public static Map getComponentMap() {
		return Collections.unmodifiableMap(componentMap);
	}

	/**
	 * 取得GXT元件
	 * 
	 * @param key
	 * @return Component
	 */
	public static Component getComponent(String key) {
		return componentMap.get(key);
	}

	/**
	 * 取得GXT元件容器大小
	 * 
	 * @return int
	 */
	public static int getComponentMapSize() {
		return componentMap.size();
	}

	/**
	 * 清空所有容器
	 */
	public static void clearAll() {
		clearUIGenNodeMap();
		clearComponentMap();
		genNodeSequence.clear();
	}

	public static String genSequence() {
		return genNodeSequence + "";
	}

	public static Set genSequenceSet() {
		return genNodeSequence;
	}
}

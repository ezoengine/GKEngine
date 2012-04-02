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
package org.gk.ui.client.com.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.obj.InfoMap;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.resources.client.ImageResource;

/**
 * 此類別繼承GXT's FastMap並實作ModelData
 * 
 * @author I21890
 */
public class gkMap implements ModelData, Map {

	private Map info = new LinkedHashMap();

	public gkMap() {
	}

	public gkMap(String key, String value) {
		info.put(key, value);
	}

	public gkMap(String key, Number value) {
		info.put(key, value);
	}

	public gkMap(String key, Object obj) {
		info.put(key, obj);
	}

	public gkMap(String key, ImageResource value) {
		info.put(key, value);
	}

	public gkMap(Map mapInfo) {
		if (mapInfo == null || mapInfo.size() == 0)
			return;
		this.info.putAll(mapInfo);
	}

	// 實做ModeData
	@Override
	public <X> X get(String property) {
		return (X) info.get(property);
	}

	@Override
	public Map<String, Object> getProperties() {
		return info;
	}

	@Override
	public Collection<String> getPropertyNames() {
		return info.keySet();
	}

	@Override
	public <X> X remove(String property) {
		return (X) info.remove(property);
	}

	@Override
	public <X> X set(String property, X value) {
		return (X) info.put(property, value);
	}

	// 實做Map
	@Override
	public void clear() {
		info.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return info.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return info.containsValue(value);
	}

	@Override
	public Set entrySet() {
		return info.entrySet();
	}

	@Override
	public Object get(Object key) {
		return info.get(key);
	}

	@Override
	public boolean isEmpty() {
		return info.isEmpty();
	}

	@Override
	public Set keySet() {
		return info.keySet();
	}

	@Override
	public Object put(Object key, Object value) {
		return info.put(key, value);
	}

	public gkMap fill(Object key, Object value) {
		info.put(key, value);
		return this;
	}

	public gkMap add(Object key, Object value) {
		info.put(key, value);
		return this;
	}

	@Override
	public void putAll(Map m) {
		info.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return info.remove(key);
	}

	@Override
	public int size() {
		return info.size();
	}

	@Override
	public Collection values() {
		return info.values();
	}

	@Override
	public String toString() {
		return info.toString();
	}

	public static Map clone(Map srcMap) {
		String jsonStr = new InfoMap(srcMap).toString();
		return JsonConvert.jsonToMap(JSONParser.parseLenient(jsonStr)
				.isObject());
	}
}
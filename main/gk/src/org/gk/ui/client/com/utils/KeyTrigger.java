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
package org.gk.ui.client.com.utils;

import java.util.Iterator;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.form.gkMap;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * <title>組合鍵觸發</title>
 * 
 * <pre>
 * 讓某個Widget可以抓取組合鍵 
 * 組合鍵的定義 shift+? , ctrl+? , alt+? , ctrl+alt+?
 * </pre>
 * 
 * @author I21890
 * @since 2010/08/08
 */
public class KeyTrigger {
	private static Map<String, Object> eventMap = new gkMap();
	public static String TRIGGER = ".keyTrigger";
	public static String[] keepKey = { "ctrl+C", "ctrl+V", "ctrl+X", "ctrl+A" };
	public static String Ctrl_Z = "ctrl+Z";
	public static String Ctrl_Y = "ctrl+Y";
	public static String Ctrl_A = "ctrl+A";
	public static String Ctrl_C = "ctrl+C";
	public static String Ctrl_V = "ctrl+V";
	public static String Ctrl_X = "ctrl+X";

	static {
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (eventMap.size() == 0)
					return;
				String eventId = event.getNativeEvent().getType();
				NativeEvent ne = event.getNativeEvent();
				int keyCode = ne.getKeyCode();
				// shift(16) , 排除ctrl(17) , alt(18)
				if (eventId.equals("keydown")
						&& !(keyCode >= 16 && keyCode <= 18)) {
					String specKey = "";
					specKey += ne.getCtrlKey() ? "+ctrl" : "";
					specKey += ne.getAltKey() ? "+alt" : "";
					if (ne.getCtrlKey() || ne.getAltKey()) {
						specKey += ne.getShiftKey() ? "+shift" : "";
					}

					// 沒有按ctrl , alt , shift 其中一種就不動作
					if (specKey.length() == 0)
						return;
					specKey = specKey.substring(1);
					specKey += "+" + (char) ne.getKeyCode();
					// 保留 复制 (ctrl+C) 粘贴(ctrl+V) 剪切 (ctrl+X) 取消(ctrl+Z)
					// 全选(ctrl+A) 的功能
					for (int i = 0; i < keepKey.length; i++) {
						if (specKey.toUpperCase().equals(
								keepKey[i].toString().toUpperCase()))
							return;
					}

					ne.preventDefault();
					broadcast(specKey);
				}
			}
		});
	}

	/**
	 * 將組合鍵傳播給所有綁定的Widget 如果當初綁定的EventProcess就直接調用，不透過EventBus
	 * 
	 * @param key
	 */
	private static void broadcast(String key) {
		Iterator it = eventMap.values().iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Widget) {
				String id = ((Widget) obj).getElement().getId();

				EventBus.get().publish(new EventObject(id + TRIGGER, key));
			}
			if (obj instanceof EventProcess) {
				EventObject eo = new EventObject(key, key);
				((EventProcess) obj).execute(key, eo);
			}
		}
	}

	/**
	 * <pre>
	 * 將傳進來的Widget進行key的綁定
	 * @param widget
	 * </pre>
	 */
	public static void bind(Widget widget) {
		String id = widget.getElement().getId();
		eventMap.put(id, widget);
	}

	public static void bind(EventProcess ep) {
		eventMap.put(ep + "", ep);
	}

	public static void bind(Widget widget, String... Keys) {
		String id = widget.getElement().getId();
		keepKey = Keys;
		eventMap.put(id, widget);
	}

	public static void bind(String id, Widget widget) {
		eventMap.put(id, widget);
	}

	public static void bind(String id, Widget widget, String... Keys) {
		keepKey = Keys;
		eventMap.put(id, widget);
	}

	/**
	 * <pre>
	 * 取消傳入的Widget組合鍵偵測功能 
	 * @param widget
	 * </pre>
	 */
	public static Widget unbind(Widget widget) {
		String id = widget.getElement().getId();
		Object obj = eventMap.remove(id);
		return obj instanceof Widget ? (Widget) obj : null;
	}
}

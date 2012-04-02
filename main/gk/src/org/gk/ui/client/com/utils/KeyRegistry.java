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

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.form.gkMap;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * 有別於KeyTrigger,用另一種方式註冊快速鍵
 * 
 * @author I21890
 * @since 2010/09/11
 */
public class KeyRegistry {
	private static Map<String, gkMap> keyMap = new gkMap();
	public final static short F1 = 112, F2 = 113, F3 = 114, F4 = 115, F5 = 116,
			F6 = 117, F7 = 118, F8 = 119, F9 = 120, F10 = 121, F11 = 122,
			F12 = 123, SHIFT = 16, CTRL = 17, ALT = 18;

	static {
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent ne = event.getNativeEvent();
				String eventId = ne.getType();
				if (!eventId.equals("keydown") || keyMap.size() == 0)
					return;
				int keyCode = ne.getKeyCode();
				switch (keyCode) {
				case SHIFT:
				case CTRL:
				case ALT:
					break;
				case F1:
				case F2:
				case F3:
				case F4:
				case F5:
				case F6:
				case F7:
				case F8:
				case F9:
				case F10:
				case F11:
				case F12:
					keyInvoker(ne, "f" + (keyCode - 111));
					break;
				default:
					// 如果有按下組合鍵，才進行處理
					if (ne.getAltKey() || ne.getShiftKey() || ne.getCtrlKey()) {
						String specKey = "";
						specKey += ne.getCtrlKey() ? "+ctrl" : "";
						specKey += ne.getAltKey() ? "+alt" : "";
						if (ne.getCtrlKey() || ne.getAltKey()) {
							specKey += ne.getShiftKey() ? "+shift" : "";
						}
						if (specKey.length() > 0) {
							specKey = specKey.substring(1);
							specKey += "+" + (char) ne.getKeyCode();
							keyInvoker(ne, specKey);
						}
					}
				}
			}

			private void keyInvoker(NativeEvent ne, String specKey) {
				if (!keyMap.containsKey(specKey))
					return;
				ne.preventDefault();
				Iterator<EventProcess> it = keyMap.get(specKey).values()
						.iterator();
				while (it.hasNext()) {
					it.next().execute(specKey,
							new EventObject(specKey, specKey));
				}
			}
		});
	}

	/**
	 * 註冊快速鍵要觸發哪些處理
	 * 
	 * @param key
	 * @param ep
	 */
	public static void bind(String key, EventProcess ep) {
		// 如果是功能鍵 (F1~F12) 轉成小寫
		if (key.toLowerCase().startsWith("f") && key.length() == 2) {
			key = key.toLowerCase();
		}
		if (!keyMap.containsKey(key)) {
			keyMap.put(key, new gkMap());
		}
		keyMap.get(key).put(ep + "", ep);
	}

	public static void unbind(String key, EventProcess ep) {
		if (keyMap.get(key) != null) {
			keyMap.get(key).remove(ep + "");
		}
	}
}

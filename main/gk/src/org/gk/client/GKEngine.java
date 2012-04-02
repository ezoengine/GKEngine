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
package org.gk.client;

import jfreecode.gwt.event.client.bus.EventBus;

import org.gk.engine.client.Engine;
import org.gk.ui.client.gkComponent;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

public class GKEngine extends gkComponent {

	private Engine gk;

	@Override
	public void start() {
		gk = determineEngineType() ? Engine.getJSEngine() : Engine.get();
		Timer timer = new Timer() {

			@Override
			public void run() {
				if (gk.isReady()) {
					cancel();
					registry(GKEngine.this, gk);
				}
			}
		};
		timer.scheduleRepeating(200);
	}

	/**
	 * <pre>
	 * 根據javascript有沒有renderPage方法決定使用哪種引擎，
	 * JSEngine沒有codespilt，Engine則有gwt codespilt
	 * </pre>
	 * 
	 * @return boolean
	 */
	native boolean determineEngineType()/*-{
		return $wnd.renderPage != undefined;
	}-*/;

	/**
	 * <pre>
	 * 註冊render方法，提供由JavaScript呼叫進行畫面繪製， 
	 * 但考量外部JavaScript啟動時，GK Engine可能還沒初始化完成，
	 * 所以在registry方法最後呼叫 $wnd.renderPage()回調外部 JavaScript，
	 * 通知外部JavaScript GK Engine已經準備好了。
	 * </pre>
	 * 
	 * @param engineWrap
	 * @param engine
	 */
	public native void registry(GKEngine engineWrap, Engine engine)/*-{
		$wnd.gk.gul = function(gulCode) {
			if (arguments.length == 0) {
				return engineWrap.@org.gk.client.GKEngine::gul()();
			} else {
				$wnd.gk.render(gulCode);
				return gulCode;
			}
		}
		//取得EventBus目前訂閱狀況
		$wnd.gk.bus = function(busName) {
			return engineWrap.@org.gk.client.GKEngine::eventBus(Ljava/lang/String;)(busName);
		}
		$wnd.gk.load = function(gulFilePath) {
			$wnd.$.ajax({
				type : 'GET',
				url : gulFilePath + "?render=true",
				dataType : 'text',
				success : function(gul) {
					$wnd.gk.render(gul);
					$wnd.gk.path = gulFilePath;
				}
			});
		}
		$wnd.gk.render = function(gulCode, id) {
			engineWrap.@org.gk.client.GKEngine::renderByJS(Ljava/lang/String;Ljava/lang/String;)(gulCode,id);
		}
		$wnd.gk.version = function() {
			return @org.gk.engine.client.Engine::getVersion()();
		}
		$wnd.gk.theme = function(theme, def) {
			return @org.gk.client.GKEngine::switchTheme(Ljava/lang/String;Ljava/lang/String;)(theme,''+def);
		}
		if ($wnd.renderPage != undefined) {
			$wnd.renderPage();
		}
	}-*/;

	private static String switchTheme(String themeId, String def) {
		themeId = themeId.toLowerCase();
		if (!themeId.equals(GXT.getThemeId())) {
			Theme t = ThemeManager.findTheme(themeId);
			if (Boolean.parseBoolean(def)) {
				GXT.setDefaultTheme(t, true);
			}
			GXT.switchTheme(t);
		}
		return themeId;
	}

	/**
	 * <pre>
	 * 由外部JavaScript觸發通知GK Engine進行畫面繪製，
	 * 如果傳入id不為null，表示要將畫面塞到此element裡面，
	 * 反之則GK Engine將使用整個body版面
	 * <img width='280' src='http://icsclink.appspot.com/event/put/x/file.download.go?j={"i":"1281843983964_447249.png"}' />
	 * </pre>
	 * 
	 * @param gulSyntax
	 *            GUL語法
	 * @param id
	 *            如果不為null，表示要將畫面塞到此element裡面
	 */
	public void renderByJS(String gulSyntax, String id) {
		if (id != null && RootPanel.get(id) != null) {
			LayoutContainer gulPanel = new LayoutContainer();
			RootPanel.get(id).add(gulPanel);
			gk.render(gulSyntax, gulPanel, false);
		} else {
			gk.render(gulSyntax, content, true);
		}
	}

	public String gul() {
		return gk.gul();
	}

	/**
	 * 提供 eventBus資訊
	 * 
	 * @param busName
	 * @return String
	 */
	private String eventBus(String busName) {
		if (busName == null || busName.equals("")) {
			return EventBus.getEventBusList();
		} else {
			return "" + EventBus.get(busName);
		}
	}

	private native void setTitle(String title)/*-{
		$wnd.document.title = title;
	}-*/;
}

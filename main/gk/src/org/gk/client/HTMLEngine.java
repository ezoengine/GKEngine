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

import java.util.Map;

import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.MapCreate;

import org.gk.engine.client.Engine;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class HTMLEngine implements EntryPoint {

	private Engine gk = Engine.getJSEngine();
	static {
		JsonConvert.setMapCreate(new MapCreate() {
			@Override
			public Map create() {
				return new gkMap();
			}
		});
		/**
		 * <pre>
		 * GXT 程式碼Initializes GXT時調用
		 *  if ("none".equals(XDOM.getComputedStyle(div,"backgroundImage"))) { 
		 *  isHighContrastMode = true;
		 * XDOM.getBodyEl().addStyleName("x-contrast"); }
		 * </pre>
		 */
		GXT.isHighContrastMode = false;
		GXT.setAutoIdPrefix("gk");
	}

	/**
	 * <pre>
	 * 註冊render方法，提供由JavaScript呼叫進行畫面繪製，
	 * 但考量外部JavaScript啟動時，GK Engine可能還沒初始化完成，
	 * 所以在registry方法最後呼叫 $wnd.renderPage()回調外部JavaScript，
	 * 通知外部JavaScript GK Engine已經準備好了。
	 * 
	 * 所以外部JavaScript應該註冊renderPage方法，收到GK Engine的通知，
	 * 再進行調用render方法通知GK Engine進行畫面繪製
	 * </pre>
	 * 
	 * @param gk
	 * @param engine
	 */
	public native void registry(HTMLEngine gk, Engine engine)/*-{
		$wnd.gk.render = function() {
			var gks = $wnd.document.getElementsByTagName('gk');
			var len = gks.length;
			while (gks.length > 0) {
				gk.@org.gk.client.HTMLEngine::renderByJS(Lcom/google/gwt/user/client/Element;)(gks[0]);
			}
		}
		$wnd.gk.render();
	}-*/;

	public void renderByJS(Element ele) {
		String gulSyntax = "<page>" + ele.getInnerHTML() + "</page>";
		Element div = DOM.createDiv();
		div.setId(ele.getId());
		ele.getParentElement().replaceChild(div, ele);
		if (div.getId().equals("")) {
			div.setId(DOM.createUniqueId());
		}
		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(new FitLayout());
		RootPanel.get(div.getId()).add(lc);
		gk.render(gulSyntax, lc);
	}

	@Override
	public void onModuleLoad() {
		registry(this, gk);
		loadGULPage();
	}

	private void loadGULPage() {
		String file = getParameter("file");
		if (file == null || file.equals("")) {
			return;
		}
		load(this, file);
	}

	public void render(String gulSyntax) {
		Viewport lc = new Viewport();
		lc.setLayout(new FitLayout());
		RootPanel.get().add(lc);
		Engine.get().render(gulSyntax, lc);
	}

	protected native void load(HTMLEngine engine, String file)/*-{
		$wnd.$.get(file, function(data) {
			engine.@org.gk.client.HTMLEngine::render(Ljava/lang/String;)(data);
		});
	}-*/;

	/**
	 * 取得request parameters
	 * 
	 * @param parm
	 * @return String
	 */
	protected static native String getParameter(String parm)/*-{
		var searchParm = parm + "=";
		var url = $wnd.location.href;
		var index = url.indexOf("?");
		if (index > 0) {
			var queryString = url.substring(index + 1, url.length);
			var parms = queryString.split("&");

			for ( var i = 0; i < parms.length; i++) {
				if (parms[i].indexOf(searchParm) == 0) {
					// Found the id parm. 
					return parms[i].substring(searchParm.length,
							parms[i].length);
				}
			}
		}
		return null;
	}-*/;
}

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
package org.gk.ui.client;

import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventBusIfc;
import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.MapCreate;

import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.themes.AccessTheme;
import org.gk.ui.client.themes.SlateTheme;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.custom.ThemeSelector;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class gkComponent implements EntryPoint {

	protected static EventBusIfc bus = EventBus.get();
	protected Viewport viewport = new Viewport();
	protected LayoutContainer content = new LayoutContainer();
	protected ThemeSelector selector = new ThemeSelector();
	static {
		// //設定預設的事件匯流排名稱為 module
		// .因 EventBusIfc 暫無 getName() 先行 mark
		// EventBus.setDefaultName(bus.getName());

		// 改使用gkMap (此類別繼承GXT's FastMap並實作ModelData)
		// 能讓EventBus傳回來的Map資料不再需要轉型
		JsonConvert.setMapCreate(new MapCreate() {

			@Override
			public Map create() {
				return new gkMap();
			}
		});
		// 註冊佈景主題,GRAY已經內定了，所以直接重設路徑就可以了
		Theme.GRAY.set("file", GWT.getModuleBaseURL()
				+ "../../res/gxt/css/gxt-gray.css");
		ThemeManager.register(SlateTheme.SLATE);
		ThemeManager.register(AccessTheme.ACCESS);
	}

	@Override
	public void onModuleLoad() {
		viewport.setLayout(new BorderLayout());
		viewport.add(content, new BorderLayoutData(LayoutRegion.CENTER));
		// 啟動AP的start方法
		start();
		if (content.getLayout() == null) {
			content.setLayout(new FitLayout());
		}
		RootPanel.get().add(viewport);
	}

	public abstract void start();

	/**
	 * 取得request parameters
	 * 
	 * @param param
	 * @return String
	 */
	public static String getParameter(String param) {
		return getParameterNative(param);
	}

	/**
	 * 取得網址
	 * 
	 * @return String
	 */
	public static native String getURL()/*-{
		return $wnd.location.href;
	}-*/;

	/**
	 * 取得request parameters
	 * 
	 * @param param
	 * @return String
	 */
	public static native String getParameterNative(String parm)/*-{
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

	protected void setLayout(Layout layout) {
		content.setLayout(layout);
	}

	/**
	 * 移除content裡面所有widget
	 * 
	 * @return boolean
	 */
	protected boolean removeAll() {
		return content.removeAll();
	}

	/**
	 * 重新繪製viewport
	 * 
	 * @return boolean
	 */
	protected boolean layout() {
		return viewport.layout();
	}
}
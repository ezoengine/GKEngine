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
package org.gk.ui.client.com.panel;

import java.util.Iterator;
import java.util.Map;

import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class gkHtmlContainer extends HtmlContainer {

	private Element elem;
	private String html = "";
	private Map info = new gkMap();

	/**
	 * <pre>
	 * 將參數帶入已經寫好的html語法中
	 * 例如:
	 * <htmlPanel>
	 *    Hello ${name}
	 * </htmlPanel>
	 * </pre>
	 * 
	 * @param info
	 */
	public void setInfo(Map info) {
		this.info = info;
		setHtml(html);
	}

	public String getHtml() {
		return html;
	}

	@Override
	public void setHtml(String html) {
		if (elem != null) {
			String htm = scrubHTML(scrubScriptTag(injectInfo(html)));
			elem.setInnerHTML(htm);
			renderAll();
		} else {
			this.html = html;
		}
	}

	public gkHtmlContainer() {

	}

	public gkHtmlContainer(Element elem) {
		super(elem);
	}

	public gkHtmlContainer(String html) {
		this.html = html;
	}

	@Override
	protected void onRender(Element target, int index) {
		elem = DOM.createElement(getTagName());
		setElement(elem, target, index);
		String htm = scrubHTML(scrubScriptTag(injectInfo(html)));
		elem.setInnerHTML(htm);
		renderAll();
		if (GXT.isFocusManagerEnabled() && !getFocusSupport().isIgnore()) {
			el().setTabIndex(0);
			el().setElementAttribute("hideFocus", "true");
			sinkEvents(Event.FOCUSEVENTS);
		}
	}

	/**
	 * 將info資訊注入html裡面
	 * 
	 * @param html
	 * @return
	 */
	private String injectInfo(String html) {
		Iterator<String> it = info.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next(); // ${}
			html = html.replaceAll("\\$\\{" + key + "}", info.get(key) + "");
		}
		return html;
	}

	/**
	 * <pre>
	 * 這樣做的原因是 IE , fireFox , Chrome對於innerHTML中有JavaScript
	 * 執行方式不同，IE需加上defer屬性，fireFox需放在element中第一個節點.
	 * chorme怎麼試都跑不出來..Orz...
	 * 最後使用此方法，三種瀏覽器都可以正常運行
	 * 將JavaScript從html字串中擷取出來，append到head中執行
	 * </pre>
	 * 
	 * @param html
	 *            可能包含JavaScript字串的HTML字串
	 * @return String 純粹HTML字串
	 */
	protected native String scrubHTML(String html)/*-{
		var reg = /<script[^>]*>[\S\s]*?<\/script[^>]*>/ig
		var match = html.match(reg);
		if (match != null) {
			for (i = 0; i < match.length; i++) {
				var scriptTxt = match[i];
				var scrStart = scriptTxt.indexOf('>') + 1;
				var scrEnd = scriptTxt.lastIndexOf('<');
				scriptTxt = scriptTxt.substring(scrStart, scrEnd);
				var script = document.createElement("script");
				script.text = scriptTxt;

				document.getElementsByTagName("head")[0].appendChild(script);
				html = html.replace(reg, "");
			}
		}
		return html;
	}-*/;

	/**
	 * 處理引用外部script檔案的寫法
	 * 
	 * @param html
	 * @return
	 */
	protected native String scrubScriptTag(String html)/*-{
		var reg = /<script [\S\s]*?[^>]*\/>/ig
		var match = html.match(reg);
		if (match != null) {
			for (i = 0; i < match.length; i++) {
				var scriptTxt = match[i];
				var srcPos = scriptTxt.indexOf('src=') + 5;
				scriptTxt = scriptTxt.substring(srcPos);
				var x1 = scriptTxt.indexOf('\'');
				var x2 = scriptTxt.indexOf('\"');
				if (x1 > 0 && x1 < x2) {
					scriptTxt = scriptTxt.substring(0, x1);
				} else {
					scriptTxt = scriptTxt.substring(0, x2);
				}
				var script = document.createElement("script");
				script.src = scriptTxt;
				document.getElementsByTagName("head")[0].appendChild(script);
				html = html.replace(reg, "");
			}
		}
		return html;
	}-*/;
}

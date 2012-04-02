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
package org.gk.engine.client.event;

import java.util.Map;

import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.obj.InfoMap;

import org.gk.engine.client.build.EngineDataStore;
import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.js.XJavaScript;
import org.gk.engine.client.exception.GKEngineException;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.i18n.EngineMessages;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.TreePanelEvent;

/**
 * JS事件處理器
 * 
 * @author i23250
 * @since 2010/10/28
 */
public class JSHandler extends EventHandler {

	@Override
	public void process(String xComId, String content, XComponent xCom,
			BaseEvent be) {
		// 如果源頭是FieldEvent，就取得來源欄位實際的id，因為Field在Grid 裡面會加上序號
		// (參考 gkTextColumnConfig 類別)，所以必須取得設定的真正id
		if (be instanceof FieldEvent) {
			xComId = ((FieldEvent) be).getBoxComponent().getId();
		}
		XJavaScript xJavaScript;
		// 若content為「this」，則表示直接執行本身內的script，不透過事件觸發
		if (content.equals("this")) {
			content = xComId;
		}
		// 若為「#」、「"」與「'」開頭，則表示後面整段都是要執行的script
		if (content.startsWith(IEventConstants.TYPE_DATA)) {
			xJavaScript = new XJavaScript(content.substring(1));
		} else if (content.startsWith("\"") || content.startsWith("'")) {
			xJavaScript = new XJavaScript(content.substring(1,
					content.length() - 1));
		} else {
			UIGen uiGen = EngineDataStore.getUIGenNode(content);
			if (uiGen == null || !(uiGen instanceof XJavaScript)) {
				throw new GKEngineException(
						EngineMessages.msg.error_jsNotFound(content));
			}
			xJavaScript = (XJavaScript) uiGen;
			xJavaScript.setComId(xComId);
		}
		// 重新產生xJavaScript必須重新註冊 JSMethod方法,因為原先的會被移除掉
		xJavaScript.initJSMethod(xJavaScript);
		// 如果是拖拉事件，轉型為DNDEvent取得源頭資料
		if (be instanceof DNDEvent) {
			xJavaScript.setDNDEvent((DNDEvent) be);
			Object data = ((DNDEvent) be).getData();
			xJavaScript.setData(JsonConvert.Object2JSONString(data));
		} else if (be instanceof TreePanelEvent) {
			TreePanelEvent tpe = (TreePanelEvent) be;
			if (tpe.getNode() != null) {
				Map nodeInfo = (Map) tpe.getNode().getModel();
				String json = new InfoMap(nodeInfo).toString();
				xJavaScript.setData(json);
			} else {
				return;
			}
		}
		xJavaScript.createScriptNodeToExecute();
	}
}

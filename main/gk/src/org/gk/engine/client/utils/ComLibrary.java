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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;
import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.obj.Info;

import org.gk.engine.client.Engine;
import org.gk.engine.client.IEngine;
import org.gk.engine.client.logging.EngineLogger;
import org.gk.ui.client.gkComponent;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.FastSet;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.js.JsUtil;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 靜態元件庫
 * 
 * <pre>
 * 引擎必須等靜態元件庫讀取完成後，才能開始運行
 * 靜態元件庫提供了 key,value取得指定元件模板
 * 並且可注入參數取得實際要render的gul字串,
 * 並將gul字串轉成node並replace 指定元件
 * 例如 <員工挑選清單/>，attach到目前解析的Document
 * 串接原流程
 * </pre>
 * 
 * @author I21890
 * @since 2011/4/19
 */
public class ComLibrary {
	private static Map<String, String> library = new FastMap();

	private static boolean ready;

	/**
	 * 元件庫是否已經載入元件
	 * 
	 * @return boolean
	 */
	public static boolean isReady() {
		return ready;
	}

	/**
	 * 如果content是/開頭，視為Server上的page (例如 .jsp , .htm等)
	 * 
	 * @param nodeName
	 * @return boolean
	 */
	public static boolean isServerPage(String nodeName) {
		return getContent(nodeName).charAt(0) == '/';
	}

	public static String getContent(String key) {
		return library.get(key) == null ? "" : library.get(key);
	}

	/**
	 * 檢查元件庫是否包含該元件
	 * 
	 * @param nodeName
	 * @return boolean
	 */
	public static boolean contains(String nodeName) {
		return library.containsKey(nodeName);
	}

	/**
	 * 取得元件庫元件所有自定義的屬性清單(${...})
	 * 
	 * @param nodeName
	 * @return JavaScriptObject
	 */
	public static JavaScriptObject getLibraryAttributes(String nodeName) {
		String gul = getContent(nodeName);
		JsArrayString list = (JsArrayString) findReplaceAttributes(gul);
		Set libAttr = new FastSet();
		for (int i = 0; i < list.length(); i++) {
			String el = replaceToEL(list.get(i));
			if (matchELPattern(el)) {
				String key = retriveELParameter(el).trim();
				libAttr.add(key);
			} else {
				libAttr.add(el);
			}
		}
		return JsUtil.toJavaScriptArray(libAttr.toArray());
	}

	/**
	 * 建立屬性對照表
	 * 
	 * @param node
	 * @param gul
	 * @return Map
	 */
	private static Map createMappingTable(Node node, String gul) {
		Map<String, String> nodeAttr = new FastMap();
		// 從node中取得屬性Map
		NamedNodeMap map = node.getAttributes();
		for (int index = 0; index < map.getLength(); index++) {
			Node attrNode = map.item(index);
			nodeAttr.put("${" + attrNode.getNodeName() + "}",
					attrNode.getNodeValue());
		}
		gul = replaceExtraBackslash(gul);
		// 從元件庫中取得屬性Map
		JsArrayString list = (JsArrayString) findReplaceAttributes(gul);
		Map<String, String> libAttr = new FastMap();
		for (int i = 0; i < list.length(); i++) {
			String attr = list.get(i);
			if (!libAttr.containsKey(attr)) {
				String el = replaceToEL(attr);
				if (matchELPattern(el)) {
					String key = retriveELParameter(el).trim();
					String value = nodeAttr.get("${" + key + "}");
					libAttr.put(attr,
							execEL(el, key, value == null ? "" : value));
				} else {
					if (!nodeAttr.containsKey(attr)) {
						libAttr.put(attr, XDOM.getUniqueId());
					}
				}
			}
		}
		nodeAttr.putAll(libAttr);
		return nodeAttr;
	}

	/**
	 * 取代原節點為元件庫內的元件
	 * 
	 * @param nodeName
	 * @param originNode
	 * @return NodeList
	 */
	public static NodeList replaceNode(String nodeName, Node originNode) {
		String gul = getContent(nodeName);
		return replaceNode(originNode, gul);
	}

	public static NodeList replaceNode(Node originNode, String gul) {
		gul = Engine.beforeParser(gul);
		Map mapping = createMappingTable(originNode, gul);
		for (Iterator it = mapping.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = (Entry) it.next();
			gul = gul.replace(entry.getKey(), entry.getValue());
		}
		Document doc = NodeUtils.parseGUL(gul);
		return doc.getFirstChild().getChildNodes();
	}

	/**
	 * 將id是${xxx}的取代成uniqueId
	 * 
	 * @param gul
	 * @return String
	 */
	public static String replaceAnonymousId(String gul) {
		JsArrayString list = (JsArrayString) findReplaceAttributes(gul);
		for (int i = 0; i < list.length(); i++) {
			gul = gul.replace(list.get(i), XDOM.getUniqueId());
		}
		return gul;
	}

	/**
	 * 取得Expression Language 的變數名稱，如 ${val1==''?'':val1} 取得 val1
	 * 
	 * @param el
	 * @return String
	 */
	private static native String retriveELParameter(String el)/*-{
		return el.substring(0, el.search(/(\==|\!=|\>|\<|\>=|\<=)/i));
	}-*/;

	/**
	 * 判斷是否符合EL寫法的regExp
	 * 
	 * @param el
	 * @return boolean
	 */
	private static native boolean matchELPattern(String el)/*-{
		var reg = /(\w*|\W*)(={2}|\!=|\>|\<|\>\=|\<\=)(\s*)(\'\w*\'|\w*|\'\W*\'|\W*)(\s*)(\?)/i;
		return el.match(reg) != null;
	}-*/;

	private static String replaceToEL(String el) {
		return el.replaceAll("\\$\\{|\\}", "").replaceAll("&lt;", "<");
	}

	/**
	 * 組EL字串時因為使用backslash(\)的符號，會導致在findReplaceAttributes會漏掉el的變數
	 * 
	 * @param gul
	 * @return String
	 */
	private static String replaceExtraBackslash(String gul) {
		gul = gul.replace("=\\\"", "=\"").replace("}\\", "}");
		return gul;
	}

	private static native String execEL(String el, String key, String value)/*-{
		var reg = new RegExp("={2}|\!=|\>\=|\<\=|\>|\<", "i");
		var vars = [];
		var symbol = el.match(reg);
		var symbolLen = symbol[0].length;
		vars[0] = el.substring(0, el.search(reg));
		vars[1] = el.substring(el.search(reg) + symbolLen, el.search(/(\?)/i));
		vars[2] = el.substring(el.search(/(\?)/i) + 1, el.search(/:/));
		vars[3] = el.substring(el.search(/:/) + 1, el.length);

		for ( var i = 0; i < vars.length; i++) {
			if (vars[i] == key) {
				vars[i] = "'" + value + "'";
			}
		}
		var newEL = vars[0] + symbol[0] + vars[1] + "?" + vars[2] + ":"
				+ vars[3];
		return $wnd.eval(newEL);
	}-*/;

	/**
	 * 傳入GUL，取得需要取代的屬性清單
	 * 
	 * @param gul
	 * @return JavaScriptObject
	 */
	private static native JavaScriptObject findReplaceAttributes(String gul)/*-{
		var replaces = [];
		// 使用「="」、「"」、「$」做第一次的分割
		var splitEL = gul.split(/=\"|\"|\(|(?=\$)/);
		for (i = 0; i < splitEL.length; i++) {
			// 去掉前後空白
			var mapper = splitEL[i].replace(/^\s*|\s*$/g, '');
			// 符合「${...}」才放入Array
			if (mapper.match(/\$\{.*\}$/) != null) {
				replaces.push(mapper);
			}
		}
		// 再使用「='」、「'」、「$」做第二次的分割
		splitEL = gul.split(/='|'|\(|(?=\$)/);
		for (i = 0; i < splitEL.length; i++) {
			// 去掉前後空白
			var mapper = splitEL[i].replace(/^\s*|\s*$/g, '');
			// 符合「${...}」才放入Array
			if (mapper.match(/\$\{.*\}$/) != null) {
				replaces.push(mapper);
			}
		}
		return replaces;
	}-*/;

	/**
	 * 提供gkEngine直接設定GULLibrary資料進來
	 * 
	 * @param allGULSyntax
	 */
	public static void setLibrary(String allGULSyntax) {
		if (allGULSyntax != null && allGULSyntax.length() > 0
				&& JsonConvert.isJSONString(allGULSyntax)) {
			library.clear();
			Map lib = (Map) JsonConvert.jsonString2Object(allGULSyntax);
			library.putAll(lib);
		}
	}

	/**
	 * 載入元件庫
	 * 
	 * @param url
	 */
	public static void loadingLibrary(String url) {
		if (url.equals("")) {
			ready = true;
			return;
		}
		requestGet(url);
	}

	public static void requestGet(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(url));
		builder.setCallback(new RequestCallback() {

			@Override
			public void onError(Request request, Throwable exception) {
				EngineLogger.log(exception);
			}

			@Override
			public void onResponseReceived(Request request, Response response) {
				// response的狀態碼OK才去做set
				if (response.getStatusCode() == Response.SC_OK) {
					setLibrary(response.getText());
					ready = true;
				} else {
					loadingLibraryByEventBus();
				}
			}
		});
		try {
			builder.send();
		} catch (RequestException e) {
			EngineLogger.log(e);
		}
	}

	/**
	 * 透過loadLibrary.bsh來載入元件庫
	 */
	public static void loadingLibraryByEventBus() {
		String url = gkComponent.getURL();
		if (url.startsWith("http:")) {
			IEngine.bus.publishRemote(new EventObject("loadLibrary.bsh"),
					new EventProcess() {

						@Override
						public void execute(String eventId, EventObject eo) {
							if (eo.getInfoType().equals(Info.MAP)) {
								Map infoMap = eo.getInfoMap();
								for (Iterator it = infoMap.entrySet()
										.iterator(); it.hasNext();) {
									Entry entry = (Entry) it.next();
									library.put((String) entry.getKey(),
											(String) entry.getValue());
								}
							}
							ready = true;
						}
					});
		}
	}

	/**
	 * 檢核node是否要進行override tag
	 * 
	 * @param nName
	 * @return boolean
	 */
	public static boolean checkParserTag(String nName) {
		if (nName.endsWith("#text") || nName.endsWith("#comment")
				|| nName.endsWith("import") || nName.endsWith("js")
				|| nName.endsWith("#cdata-section")) {
			return false;
		}
		return true;
	}

	/**
	 * 取得Override元件庫內容
	 * 
	 * @param node
	 * @param nName
	 * @param strOverride
	 * @param overrideType
	 * @return String
	 */
	public static String getLibContent(Node node, String nName,
			String strOverride, String overrideType) {
		// 預設元件庫
		String defaultLib = defaultLib(nName, overrideType);
		// 若有override屬性，進行判斷
		// false:表示不執行override
		// true:表示使用預設的元件庫
		// 其它:override有設定元件庫的名稱,則以該名稱做為override的對象
		if (strOverride != null && !strOverride.equals("")) {
			if (strOverride.equals("false")) {
				return "";
			} else if (strOverride.equals("true")) {

			} else {
				defaultLib = strOverride;
			}
		}
		return getContent(defaultLib);
	}

	/**
	 * 元件庫Override
	 * 
	 * @param node
	 * @return Node
	 */
	public static Node overrideNode(Node node) {
		String nName = node.getNodeName();
		if (!checkParserTag(nName)) {
			return node;
		}
		// 將node轉成屬性的Map物件
		Map nodeMap = NodeUtils.getAttributes(node);
		String strOverride = (String) empty(nodeMap.get("override"));
		String overrideType = (String) empty(nodeMap.get("type"));
		String libgul = "";

		// 取出元件庫內容
		libgul = getLibContent(node, nName, strOverride, overrideType);
		// 元件庫找不到則不使用override
		if (libgul.equals("") || libgul.equals("undefined")) {
			return node;
		}
		// 更新每個元件的Override屬性
		node = updateOverrideAttr(libgul, node);
		return node;
	}

	/**
	 * 更新每個元件的Override屬性
	 * 
	 * @param libgul
	 * @param node
	 * @return Node
	 */
	private static Node updateOverrideAttr(String libgul, Node node) {
		Document doc = NodeUtils.parseGUL(libgul);
		NodeList nodeList = doc.getChildNodes();
		// 取出override元件庫檔案，並且取出所有的屬性
		NamedNodeMap libNodeMap = null;
		Map libMap = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			libNodeMap = nodeList.item(i).getAttributes();
			// 元件庫若是有<page>....</page>時，往下多找一層
			if (nodeList.item(i).getNodeName().equals("page")) {
				Node libNode = nodeList.item(i).getFirstChild();
				libNodeMap = libNode.getAttributes();
			}
		}

		libMap = NodeUtils.getAttributes(node);
		// 將元件庫的屬性更新及新增到原node存放的Map裡
		Node attrNode;
		for (int i = 0; i < libNodeMap.getLength(); i++) {
			attrNode = libNodeMap.item(i);
			// 除了type以外，其餘的屬性都要update
			if (attrNode.getNodeName().equals("type"))
				continue;
			libMap.put(attrNode.getNodeName(), attrNode.getNodeValue());
		}
		return node = updateNodeAttribute(node, libMap);
	}

	/**
	 * 將屬性更新到node
	 * 
	 * @param node
	 * @param allAttr
	 * @return Node
	 */
	private static Node updateNodeAttribute(Node node, Map allAttr) {
		Element el = (Element) node;
		Iterator userMap = allAttr.entrySet().iterator();
		while (userMap.hasNext()) {
			Map.Entry entry = (Map.Entry) userMap.next();
			el.setAttribute(entry.getKey().toString(), entry.getValue()
					.toString());
		}
		return el;
	}

	/**
	 * x[nodeName]_[nodeType] 例如: xfield_txt;xfield_date;xform
	 * 
	 * @param nodeName
	 * @param nodeType
	 * @return String
	 */
	private static String defaultLib(String nodeName, String nodeType) {
		if (nodeType.equals("") || nodeType.equals("undefined")) {
			return "x" + nodeName;
		}
		return "x" + nodeName + "_" + nodeType;
	}

	private static Object empty(Object o) {
		if (o == null || o.equals("") || o.equals("undefined")) {
			return "";
		}
		return o;
	}
}

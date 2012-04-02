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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;
import jfreecode.gwt.event.client.bus.JsonConvert;
import jfreecode.gwt.event.client.bus.obj.Info;

import org.gk.engine.client.Engine;
import org.gk.engine.client.IEngine;
import org.gk.engine.client.exception.LibraryNotFoundException;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.engine.client.logging.EngineLogger;
import org.gk.ui.client.gkComponent;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
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
 */
public class ComLibrary {

	private static final String GUL_LIB_COMPONENT_LIB = "gul/lib/component.lib";

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
	 * 建立屬性對照表
	 * 
	 * @param node
	 * @param gul
	 * @return Map
	 */
	private static Map createMappingTable(Node node, String gul) {
		Map attrMap = new FastMap();
		// node 宣告傳入變數的值
		NamedNodeMap map = node.getAttributes();
		for (int index = 0; index < map.getLength(); index++) {
			Node attrNode = map.item(index);
			attrMap.put("${" + attrNode.getNodeName() + "}",
					attrNode.getNodeValue());
		}
		// 元件庫入gul變數
		List<String> list = JsonConvert.jsonToList(new JSONArray(
				findReplaceAttributes(gul)));
		for (int index = 0; index < list.size(); index++) {
			String attr = list.get(index);
			// 取出ExpressionLanguage中實際變數名稱
			String nodeKey = "${" + retriveELParameter(attr) + "}";
			if (!attrMap.containsKey(attr)) {
				// 使用EL判斷式時，變數值為空值
				if (!attr.equals(nodeKey)) {
					attrMap.put(attr, "");
				} else {
					attrMap.put(attr, XDOM.getUniqueId());
				}
			}

			if (!attr.equals(nodeKey) && attrMap.containsKey(nodeKey)) {
				String nodeKeyVal = "" + attrMap.get(nodeKey);
				// TODO
				if (!nodeKeyVal.startsWith("gk")) {
					attrMap.put(attr, attrMap.get(nodeKey));
				}
			}
		}
		return attrMap;
	}

	/**
	 * 取代原節點為元件庫內的元件
	 * 
	 * @param nodeName
	 * @param originNode
	 * @return NodeList
	 */
	public static NodeList replaceNode(String nodeName, Node originNode) {
		String gul = library.get(nodeName);
		return replaceNode(nodeName, originNode, gul);
	}

	public static NodeList replaceNode(String nodeName, Node originNode,
			String gul) {
		gul = Engine.beforeParser(gul);
		Map mapping = createMappingTable(originNode, gul);
		String value = "";
		for (Iterator it = mapping.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = (Entry) it.next();
			value = execEL(entry.getKey(), entry.getValue());
			gul = gul.replace(entry.getKey(), value);
		}
		Document doc = NodeUtils.parseGUL(gul);
		return doc.getFirstChild().getChildNodes();
	}

	/**
	 * 取得Expression Language 的變數名稱 ${val1==''?'':val1} 取得 val1
	 * 
	 * @param elTag
	 * @return String
	 */
	private static native String retriveELParameter(String elTag)/*-{
		elTag = @org.gk.engine.client.utils.ComLibrary::replaceTag(Ljava/lang/String;)(elTag);
		var para = elTag;
		var isELPattern = @org.gk.engine.client.utils.ComLibrary::matchELPattern(Ljava/lang/String;)(elTag);
		//若不是EL(例如:${value=='?'':value} )表示式，則以原始的變數傳回(${value})
		if (isELPattern) {
			var el = elTag.split("?", 2);
			var expression = el[0];
			//取出變數,例如: ${value=='?'':value} 則取得 value
			para = expression.substring(0, expression
					.search(/(={2}|\!=|\>|\<|\>\=|\<\=)/i));
		}
		return para.replace(/\s\s*$/, '');
		;
	}-*/;

	/**
	 * 判斷是否符合EL寫法的regExp
	 * 
	 * @param elTag
	 * @return boolean
	 */
	private static native boolean matchELPattern(String elTag)/*-{
		var reg = /(\w*|\W*)(={2}|\!=|\>|\<|\>\=|\<\=)(\s*)(\'\w*\'|\w*|\'\W*\'|\W*)(\s*)(\?)/i;
		return elTag.match(reg) != null;
	}-*/;

	private static native String replaceTag(String elTag)/*-{
		elTag = elTag.replace("${", "");
		elTag = elTag.replace("}", "");
		elTag = elTag.replace("&lt;", "<");
		return elTag;
	}-*/;

	private static native String execEL(String elTag, String val)/*-{
		elTag = @org.gk.engine.client.utils.ComLibrary::replaceTag(Ljava/lang/String;)(elTag);
		var isELPattern = @org.gk.engine.client.utils.ComLibrary::matchELPattern(Ljava/lang/String;)(elTag);
		if (isELPattern) {
			var para = @org.gk.engine.client.utils.ComLibrary::retriveELParameter(Ljava/lang/String;)(elTag);
			//動態產生變數
			//$wnd.eval("var " + para + "='" + val + "'");
			while (elTag.indexOf(para) != -1) {
				elTag = elTag.replace(para, "'" + val + "'");
			}
			return $wnd.eval(elTag);
		} else {
			return val;
		}
	}-*/;

	/**
	 * 傳入GUL，取得需要取代的屬性清單
	 * 
	 * @param gul
	 * @return JavaScriptObject
	 */
	private static native JavaScriptObject findReplaceAttributes(String gul)/*-{
		var replaces = [];
		var split = gul.split(/(?=\$)|\'|\"/);
		for (i = 0; i < split.length; i++) {
			// 符合「${...}」才放入Array
			if (split[i].match(/\$\{.*\}$/) != null) {
				replaces.push(split[i]);
			}
		}
		//取出gul中變數寫法是使用EL
		var splitEL = gul.split(/=\"|\"/);
		for (i = 0; i < splitEL.length; i++) {
			// 符合「${...}」才放入Array
			if (splitEL[i].match(/\$\{.*\}$/) != null) {
				replaces.push(splitEL[i]);
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
	 */
	public static void loadingLibrary() {
		String homeUrl = GWT.getModuleBaseURL();
		homeUrl = homeUrl.substring(0, homeUrl.indexOf("html/"));
		requestGet(homeUrl + GUL_LIB_COMPONENT_LIB);
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
		// 元件庫找不到拋出例外
		if (!strOverride.equals("true") && !strOverride.equals("")) {
			if (null == libgul) {
				throw new LibraryNotFoundException(
						EngineMessages.msg.error_libraryNotFound(strOverride));
			}
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

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
package org.gk.engine.client;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcessImpl;
import jfreecode.gwt.event.client.bus.JsonConvert;

import org.gk.engine.client.build.Builder;
import org.gk.engine.client.build.EngineDataStore;
import org.gk.engine.client.build.INodeProvider;
import org.gk.engine.client.build.js.XJavaScript;
import org.gk.engine.client.build.layout.XLayoutData;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.event.EventHandler;
import org.gk.engine.client.exception.LibraryNotFoundException;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.engine.client.logging.EngineLogger;
import org.gk.engine.client.utils.ComLibrary;
import org.gk.engine.client.utils.NodeUtils;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * <title>前端GWT的GK核心</title>
 * 
 * <pre>
 * GK核心是用來解析gul語法，完成gul描述畫面的生成，
 * 並提供API讓AP可控制生成畫面中的任何Widget。
 * 使用方式
 * 1.在GWT模組繼承gk模組
 * 2.每一個GWTPage搭配一個Engine
 * </pre>
 * 
 * @author I21890
 * @since 2010/07/07
 */
public class Engine implements IEngine, INodeProvider {

	static {
		// 當這為true時，pageToolbar image出不來 (路徑不對)
		// 這boolean是在GXT.java初始化根據瀏覽器放div的效果決定的，
		// 但問題出在有時是true,有時是false
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
	private static Engine engine;
	protected String id = XDOM.getUniqueId();
	protected String gul = "<page>\r\n</page>";
	// 目前正在處理的XML Node
	protected Node preprocessNode;
	// 存放目前解析好的GK元件，GK元件型別為UIGen，可產生Component或程式碼
	protected List<UIGen> uiGenNodeList = new gkList<UIGen>();
	// 存放render過的LayoutContainer擁有的元件
	protected static Map<String, Set> renderPanelCom = new gkMap();
	protected static Map<String, Set> renderPanelComBak = new gkMap();

	public static Engine get() {
		if (engine == null) {
			engine = new Engine();
			engine.runAsync();
		}
		return engine;
	}

	public static Engine getJSEngine() {
		if (engine == null) {
			engine = new Engine();
			engine.runSync();
		}
		return engine;
	}

	public static String getVersion() {
		return Version.BUILD;
	}

	/**
	 * <pre>
	 * 載入引擎內定可用的元件 <com></com> 啟動元件建構器,
	 * 傳入NodeProvider讓元件 建構器可解析Node進行畫面元件的建構
	 * </pre>
	 */
	private Engine() {
		// 目前對於GKEngine例外，或其他例外的捕捉先直接透過alert顯示
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				EngineLogger.log(e);
			}
		});
	}

	protected void runSync() {
		build();
	}

	protected void runAsync() {
		GWT.runAsync(Engine.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				build();
			}

			@Override
			public void onFailure(Throwable reason) {
				EngineLogger.log(reason);
			}
		});
	}

	private void build() {
		Builder.attach(Engine.this);
		loadingLibrary();
		hookJSMethod();
	}

	/**
	 * 取得lib檔案所在
	 */
	public native void loadingLibrary()/*-{
		(function() {
			var gkEngine = $wnd.document.getElementById('_gk_');
			if (gkEngine != null && gkEngine.getAttribute('lib') != null) {
				var gkEngineLib = gkEngine.getAttribute('lib');
				@org.gk.engine.client.utils.ComLibrary::loadingLibrary(Ljava/lang/String;)(gkEngineLib);
			} else {
				@org.gk.engine.client.utils.ComLibrary::loadingLibrary()();
			}
		})();
	}-*/;

	/**
	 * 註冊JavaScript方法，提供隨時給JavaScript調用
	 */
	public native void hookJSMethod()/*-{
		$wnd.gk = new Object();
		$wnd.gk.set = function(id, value) {
			//判断元素是否为 array 原本的value instanceof $wnd.Array 貌似会判断不出来
			var isArray = Object.prototype.toString.apply(value) === '[object Array]';
			var isObject = typeof (value) == 'object';
			if (isArray || isObject) {
				@org.gk.engine.client.build.js.XJavaScript::setAttributeValue(Ljava/lang/String;ZLcom/google/gwt/core/client/JavaScriptObject;)(id, isArray, value)
			} else {
				try {
					@org.gk.engine.client.build.js.XJavaScript::setAttributeValue(Ljava/lang/String;Ljava/lang/Object;)(id, value);
				} catch (x) {
					// 其他非Object型別的value轉成字串來處理，如number、boolean
					@org.gk.engine.client.build.js.XJavaScript::setAttributeValue(Ljava/lang/String;Ljava/lang/Object;)(id, '' + value);
				}
			}
		}
		$wnd.gk.get = function(id) {
			var value = @org.gk.engine.client.build.js.XJavaScript::getJSONValue(Ljava/lang/String;)(id);
			if (value == null) {
				return null;
			} else {
				var c = value.charAt(0);
				return c == '[' || c == '{' ? eval('(' + value + ')') : value;
			}
		}
		$wnd.gk.createUniqueId = function(gul) {
			return @org.gk.engine.client.build.js.XJavaScript::createUniqueId(Ljava/lang/String;)(gul);
		}
		$wnd.gk.jsonp = function(url, param) {
			var script = $doc.createElement('script');
			script.type = 'text/javascript';
			if (arguments.length == 2 && param.length > 0) {
				url += "?";
				for (i = 0; i < param.length; i++) {
					url += i == param.length - 1 ? param[i] : param[i] + "&";
				}
			}
			script.src = url;
			$doc.getElementsByTagName('head')[0].appendChild(script);
		}
		$wnd.gk.fire = function(id, eventType) {
			return @org.gk.engine.client.build.js.XJavaScript::fire(Ljava/lang/String;Ljava/lang/String;)(id, eventType);
		}
		$wnd.gk.library = function(gulSyntax) {
			@org.gk.engine.client.utils.ComLibrary::setLibrary(Ljava/lang/String;)(gulSyntax);
		}
		$wnd.gk.getParameter = function(parm) {
			var searchParm = parm + "=";
			var url = $wnd.location.href;
			var index = url.indexOf("?");
			if (index > 0) {
				var queryString = url.substring(index + 1, url.length);
				var parms = queryString.split("&");
				for ( var i = 0; i < parms.length; i++) {
					if (parms[i].indexOf(searchParm) == 0) {
						return parms[i].substring(searchParm.length,
								parms[i].length);
					}
				}
			}
			return null;
		}
		$wnd.gk.event = function(gulAtt, callback) {
			@org.gk.engine.client.Engine::invokeEvent(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(gulAtt, callback);
		}
		$wnd.gk.param = $wnd.gk.getParameter;
		$wnd.gk.row = function(obj) {
			return obj['_gk_idx'].split(',')[0];
		}
		$wnd.gk.col = function(obj) {
			return obj['_gk_idx'].split(',')[1];
		}
		$wnd.gk.column = $wnd.gk.col;
		$wnd.gk.beforeParser = function(gul) {
			return gul;
		}
		$wnd.gk.i18n = function(gul) {
			var split = gul.split(/(?=#D\{)|\'|\"/);
			for (i = 0; i < split.length; i++) {
				// 去掉空白
				var mapper = split[i].replace(/^\s*|\s*$/g, '');
				// 符合「#D{...}」才做替換
				if (mapper.match(/^(#D\{).+\}$/) != null) {
					var code = mapper.substring(mapper.indexOf('{') + 1, mapper
							.indexOf('}'));
					gul = gul.replace(mapper, $wnd.$.i18n.prop(code));
				}
			}
			return gul;
		}
		$wnd.gk.listener = function(userId, midTime) {
			@org.gk.engine.client.Engine::registryWindowFocusBlur(Ljava/lang/String;Ljava/lang/String;)(userId, '' + midTime);
			@org.gk.engine.client.Engine::listener(Ljava/lang/String;Ljava/lang/String;)(userId, '' + midTime);
			return 'done.';
		}
		$wnd.gk.cookie = function(name, value) {
			var numargs = arguments.length;
			if (numargs == 1) {
				return @org.gk.engine.client.Engine::getCookie(Ljava/lang/String;)(name);
			} else if (numargs == 2) {
				@org.gk.engine.client.Engine::setCookie(Ljava/lang/String;Ljava/lang/String;)(name, value);
			}
		}
	}-*/;

	private static String getCookie(String name) {
		return Cookies.getCookie(name);
	}

	private static void setCookie(String name, String value) {
		long timestamp = new Date().getTime() + (1000 * 86400) * 14; // 2 week
		Cookies.setCookie(name, value, new Date(timestamp));
	}

	/**
	 * 執行javascript指令
	 * 
	 * @param js
	 *            javascript指令
	 */
	private static native void executeJS(String js)/*-{
		$wnd.eval(js);
	}-*/;

	private static native void registryWindowFocusBlur(String userId,
			String midTime)/*-{
		$wnd.onfocus = function() {
			@org.gk.engine.client.Engine::listener(Ljava/lang/String;Ljava/lang/String;)(userId, '' + midTime);
		}
		$wnd.onblur = function() {
			@org.gk.engine.client.Engine::listener(Ljava/lang/String;Ljava/lang/String;)(userId, '0');
		}
	}-*/;

	/**
	 * 啟動polling機制，接收後端傳來資料
	 * 
	 * @param userId
	 * @param midTime
	 */
	private static void listener(String userId, String midTime) {
		if (midTime == null || midTime.equals("0")
				|| (userId != null && userId.toLowerCase().equals("guest"))) {
			EventBus.get(userId).disconnect();
			return;
		}
		EventBus.get(userId).connectServer(Integer.parseInt(midTime));
		EventBus.get(userId).subscribe("javascript",
				new EventProcessImpl("javascript") {
					@Override
					public void execute(String eventId, EventObject eo) {
						try {
							executeJS(eo.getInfoString());
						} catch (Exception e) {
							EngineLogger.console("executeJS:" + e);
						}
					}
				});
		EventBus.get(userId).subscribe("callback",
				new EventProcessImpl("callback") {
					@Override
					public void execute(String eventId, EventObject eo) {
						Map gkInfo = eo.getInfoMap();
						Iterator it = gkInfo.entrySet().iterator();
						while (it.hasNext()) {
							Entry<String, Object> entry = (Entry) it.next();
							EventHandler.setAttributeValue(entry.getKey(),
									entry.getValue());
						}
					}
				});
	}

	protected static void invokeEvent(String gulAttribute, JavaScriptObject jso) {
		invokeEvent("", gulAttribute, jso);
	}

	protected static void invokeEvent(String srcId, String gulAttribute,
			JavaScriptObject jso) {
		XJavaScript js = new XJavaScript(srcId, "");
		EventCenter.exec(js.getId(), gulAttribute, js, new BaseEvent(jso));
	}

	@Override
	public Component getComponent(String id) {
		return getComponent().get(id);
	}

	@Override
	public Map<String, Component> getComponent() {
		return EngineDataStore.getComponentMap();
	}

	@Override
	public void render(String gul, LayoutContainer lc) {
		render(gul, lc, true);
	}

	@Override
	public void render(String gul, LayoutContainer lc, boolean clearAll) {
		if (clearAll) {
			this.gul = gul;
			renderPanelCom.clear();
			EngineDataStore.clearAll();
			bus.removeAll();
			EventCenter.clear();
		}
		renderPanel(gul, lc);
	}

	public String gul() {
		return gul;
	}

	public void renderPanel(String gul, LayoutContainer lc) {
		renderPanel(gul, lc, false);
	}

	/**
	 * <pre>
	 *  讓引擎可以解析GUL語法後將元件放到傳入的LayoutContainer
	 *  在解析GUL語法過程記錄所有產生的Component id，才能在下次
	 *  要重新繪製時，移除既有的元件ID
	 * </pre>
	 * 
	 * @param gul
	 * @param container
	 * @param hasRoot
	 */
	public void renderPanel(String gul, LayoutContainer container,
			boolean hasRoot) {
		// 提供使用者在Engine解析前介入處理
		gul = beforeParser(gul);
		gul = i18n(gul);
		// 為了包含多個元件，需要的話可以用root包起來讓xml解析完，再拿掉root Tag
		if (hasRoot) {
			gul = new StringBuffer("<root>").append(gul).append("</root>")
					.toString();
		}
		uiGenNodeList.clear();

		// 將面板擁有的所有 component 清空
		removeAllComponent(container);

		// 將目前使用的Bus名稱記起來，改用引擎本身的Bus
		String outsideBusName = EventBus.get().getName();
		EventBus.setDefaultName(bus.getName());
		// 資料備份
		backup();
		try {
			// 將GUL文件轉成XML文件，如果有問題就拋出Exception
			Document doc = NodeUtils.parseGUL(gul);

			NodeList nodeList = hasRoot ? doc.getChildNodes().item(0)
					.getChildNodes() : doc.getChildNodes();

			// GUL文件轉換完成，開始解析節點
			long time = System.currentTimeMillis();
			parserNode(uiGenNodeList, nodeList);
			putRenderPanelComSet(container);
			long processNodeSpendTime = System.currentTimeMillis() - time;

			// 節點解析完成，開始建立元件並產生畫面
			time = System.currentTimeMillis();
			renderUI(container);
			container.layout();
			initialAllUIGen(container);
			long renderUISpendTime = System.currentTimeMillis() - time;

			// 列印節點解析花費時間與元件建立花費時間
			printLastRenderSpendTime(processNodeSpendTime, renderUISpendTime);
			// 畫面產生完成，發佈事件通知
			bus.publish(new EventObject(getId() + ".afterRender"));
		} catch (Throwable e) {
			EngineLogger.log(e);
			rollback();
		} finally {
			// 清除備份
			clearBackup();
			// 改回原Bus名稱
			EventBus.setDefaultName(outsideBusName);
			// 每次render結束後，插上log以觀察是否有記憶體洩漏的狀況
			EngineLogger.console(EventBus.getEventBusList());
			EngineLogger.console("Nodes:"
					+ EngineDataStore.getUIGenNodeMapSize());
			EngineLogger.console("Components:"
					+ EngineDataStore.getComponentMapSize());
			EngineLogger.console("renderPanelCom:" + renderPanelCom.size());
		}
	}

	/**
	 * 備份
	 */
	private void backup() {
		EngineDataStore.backup();
		renderPanelComBak.putAll(renderPanelCom);
	}

	/**
	 * 回復
	 */
	private void rollback() {
		EngineDataStore.rollback();
		renderPanelCom.clear();
		renderPanelCom.putAll(renderPanelComBak);
	}

	/**
	 * 清除備份
	 */
	private void clearBackup() {
		EngineDataStore.clearBackup();
		renderPanelComBak.clear();
	}

	public void removeComponent(String id) {
		EngineDataStore.removeComponent(id);// 刪除 GXT 元件
		EngineDataStore.removeUIGenNode(id);// 刪除 XComponent 元件
		EventCenter.remove(id); // 取消元件訂閱的事件
	}

	/**
	 * @param containerId
	 */
	public void removeRenderPanelComponent(String containerId) {
		this.renderPanelCom.remove(containerId);
	}

	private void removeAllComponent(Container layoutContainer) {
		Iterator<Component> comIt = layoutContainer.getItems().iterator();
		while (comIt.hasNext()) {
			Component com = comIt.next();
			if (com instanceof Container) {
				removeAllComponent((Container) com);
			} else if (com instanceof Grid) {
				removeColumnModel((Grid) com);
			}
			this.removeComponent(com.getId());
		}
		this.renderPanelCom.remove(layoutContainer.getId());

		// 將面板清空
		layoutContainer.removeAll();
	}

	/**
	 * 清掉Grid同時，要將Grid裡面的ColumnConfig元件從EngineDataStore中移除
	 * 
	 * @param grid
	 */
	private void removeColumnModel(Grid grid) {
		ColumnModel cm = grid.getColumnModel();
		Iterator<ColumnConfig> columnIt = cm.getColumns().iterator();
		while (columnIt.hasNext()) {
			this.removeComponent(columnIt.next().getId());
		}
	}

	/**
	 * 記錄此次繪製RenderPanel產生的元件id , 才能在不需要的時候移除掉
	 * 
	 * @param lc
	 */
	private void putRenderPanelComSet(LayoutContainer lc) {
		renderPanelCom.put(lc.getId(),
				new LinkedHashSet(EngineDataStore.genSequenceSet()));
		EngineDataStore.genSequenceSet().clear();
	}

	private static void printLastRenderSpendTime(long processNodeTime,
			long renderTime) {
		String p = "processNode:" + (processNodeTime / 1000.0);
		String r = "render:" + (renderTime / 1000.0);
		System.err.println("GK: " + p + " / " + r);
	}

	public String getId() {
		return id;
	}

	@Override
	public void parserNode(List<UIGen> list, NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); i++) {
			preprocessNode(nodes.item(i), list);
		}
	}

	@Override
	public void parserNode(List<UIGen> list, Node node) {
		preprocessNode(node, list);
	}

	/**
	 * 開始解析目前的Node
	 * 
	 * @param node
	 *            準備要解析的Node
	 * @param list
	 *            解析Node拿到的uiGen放在此list
	 */
	public void preprocessNode(Node node, List<UIGen> list) {
		// Override元件庫
		this.preprocessNode = ComLibrary.overrideNode(node);

		int processTagAmt = 0;
		String nName = node.getNodeName();
		// 過濾 <#text></#text>、<#comment></#comment>、<import></import>
		if (nName.endsWith("#text") || nName.endsWith("#comment")
				|| nName.endsWith("import")) {
			return;
		}
		// 如果不是com節點，通知此節點需要透過buildList註冊的建構器進行處理
		processTagAmt = builder.publish(new EventObject(nName.toLowerCase(),
				list));
		// processTagAmt=0表示該Tag沒人處理，將tag名稱視為GUL檔案名稱到元件庫去找該元件
		if (processTagAmt == 0) {
			processExternalTag(nName, node, list);
		}
	}

	/**
	 * 檢查引擎是否已完成載入元件庫
	 * 
	 * @return boolean
	 */
	public boolean isReady() {
		return ComLibrary.isReady();
	}

	/**
	 * 處理元件庫的節點
	 * 
	 * @param nodeName
	 * @param node
	 * @param list
	 */
	private void processExternalTag(String nodeName, Node node, List<UIGen> list) {
		// 找到元件將處理新attach的節點
		if (ComLibrary.contains(nodeName)) {
			if (!ComLibrary.isServerPage(nodeName)) {
				NodeList nList = ComLibrary.replaceNode(nodeName, node);
				parserNode(list, nList);
			} else {
				Map nodeInfo = NodeUtils.getAttributes(node);
				String url = ComLibrary.getContent(nodeName);
				String gul = ajaxComponent(url, nodeInfo);
				NodeList nList = ComLibrary.replaceNode(nodeName, node, gul);
				parserNode(list, nList);
			}
		} else {
			throw new LibraryNotFoundException(
					EngineMessages.msg.error_libraryNotFound(nodeName));
		}
	}

	/**
	 * <pre>
	 * 此方法可透過ajax調用jsp檔案取得GUL畫面再redner到前端
	 * 尚未提供傳資料給後端jsp的功能，可參考
	 * http://api.jquery.com/jQuery.post/   透過 data屬性設定 j={...} 資訊
	 * </pre>
	 * 
	 * @param url
	 * @param nodeInfo
	 * @return String
	 */
	private String ajaxComponent(String url, Map nodeInfo) {
		String id = DOM.createUniqueId();
		nodeInfo.put("_gk_file", url);
		StringBuffer gul = new StringBuffer("<page><panel id='").append(id);
		gul.append("' layout='fit'>").append("<js init='js:this'>");
		gul.append("$.ajax({type : 'POST',url  : 'event/put/ajaxComponent/jspBean.forward.go");
		gul.append("',data:");
		gul.append("'j={\"t\":\"map\",\"i\":")
				.append(JsonConvert.toJSONObject(nodeInfo) + "").append("}");
		gul.append("',dataType : 'text',success : function(gul) {gk.set('");
		gul.append(id).append("',gul);}});</js></panel></page>");
		return gul.toString();
	}

	/**
	 * 開始將nodeList裡面的元件進行調用，產生Component元件，並放入目前Panel
	 * 
	 * @param panel
	 */
	private void renderUI(LayoutContainer panel) {
		Iterator<UIGen> it = uiGenNodeList.iterator();
		// 增加uiGenNodeList不为空的判断，多次调用renderpage()时之前的uiGenNodeList会被清空
		while (it.hasNext() && !uiGenNodeList.isEmpty()) {
			UIGen uiGen = it.next();
			Component com = uiGen.build();
			if (com != null) {
				// 如果是Window，就不添加到容器中
				if (!(com instanceof Window)) {
					// 如果元件被layout包起來，放元件到容器時會設定layoutData
					if (uiGen instanceof XLayoutData) {
						XLayoutData xLayout = (XLayoutData) uiGen;
						panel.add(com, xLayout.getLayoutData());
					} else {
						panel.add(com);
					}
				}
				// 增加判断，如果uiGenNodeList包含之前取到的元素，才调用remove
				if (uiGenNodeList.contains(uiGen)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * 初始化所有此次建立的元件
	 * 
	 * @param lc
	 */
	private void initialAllUIGen(LayoutContainer lc) {
		Iterator<String> it = renderPanelCom.get(lc.getId()).iterator();
		while (it.hasNext()) {
			UIGen ui = EngineDataStore.getUIGenNode(it.next());
			ui.init();
		}
	}

	public native static String beforeParser(String gul)/*-{
		return $wnd.gk.beforeParser(gul);
	}-*/;

	public native static String i18n(String gul)/*-{
		return $wnd.gk.i18n(gul);
	}-*/;

	@Override
	public Node getPreprocessNode() {
		return preprocessNode;
	}
}
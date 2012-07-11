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
import org.gk.engine.client.exception.GKEngineException;
import org.gk.engine.client.exception.LibraryNotFoundException;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.engine.client.logging.EngineLogger;
import org.gk.engine.client.utils.ComLibrary;
import org.gk.engine.client.utils.NodeUtils;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.DomQuery;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
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
			engine.build();
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

	private void build() {
		Builder.attach(Engine.this);
		loadingLibrary();
		JSMethods.hookJSMethods();
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
				@org.gk.engine.client.utils.ComLibrary::loadingLibrary(Ljava/lang/String;)("/gul/lib/component.lib");
			}
		})();
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
	 * 取得gk.load指定GUL
	 * 
	 * <pre>
	 * Page時，該Page的Path如果是Web通常會使用相對路徑方式設定， 
	 * 如果考慮行動裝置的話，應設定完整路徑
	 * 例如: http://www.ezoui.com/portal/index.gul
	 * </pre>
	 * 
	 * @return String
	 */
	public static native String getGKPath()/*-{
		return $wnd.gk.path;
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
		String gkPath = getGKPath();
		if (gkPath != null && gkPath.startsWith("http://")) {
			String[] urlSplit = gkPath.split("/");
			if (urlSplit.length == 3) {
				throw new GKEngineException("gk.listener failure! " + gkPath);
			}
			String host = urlSplit[0] + "//" + urlSplit[2] + "/" + urlSplit[3]
					+ "/";
			EventBus.get(userId).connectServer(host, Integer.parseInt(midTime));
		} else {
			EventBus.get(userId).connectServer(Integer.parseInt(midTime));
		}
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
			Document doc = parseGUL(gul);

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
			// 每次render結束後，插上log以觀察是否有記憶體洩漏的狀況，並檢查資料的一致性
			check();
		}
	}

	private Document parseGUL(String gul) {
		gul = ComLibrary.replaceAnonymousId(gul);
		return NodeUtils.parseGUL(gul);
	}

	private void check() {
		// 每次render結束後，插上log以觀察是否有記憶體洩漏的狀況
		EngineLogger.console(EventBus.getEventBusList());
		EngineLogger.console("Nodes:" + EngineDataStore.getUIGenNodeMapSize());
		EngineLogger.console("Components:"
				+ EngineDataStore.getComponentMapSize());
		EngineLogger.console("renderPanelCom:" + renderPanelCom.size());

		// 檢查資料是否一致
		int uiGenNodeSize = EngineDataStore.getUIGenNodeMapSize();
		int rpComSize = 0;
		Set key = renderPanelCom.keySet();
		Iterator it = key.iterator();
		while (it.hasNext()) {
			String ke = it.next().toString() + "";
			Set map = renderPanelCom.get(ke);
			rpComSize += map.size();
		}
		if (uiGenNodeSize != rpComSize) {
			String temp = "";
			Map nodeMap = EngineDataStore.getUIGenNodeMap();
			it = key.iterator();
			while (it.hasNext()) {
				String ke = it.next().toString() + "";
				Set map = renderPanelCom.get(ke);
				Iterator mapIt = map.iterator();
				while (mapIt.hasNext()) {
					Object obj = mapIt.next();
					if (!nodeMap.containsKey(obj)) {
						temp += obj + ",";
					}
				}
			}
			if (!temp.equals("")) {
				EngineLogger.console("different info===\n");
				EngineLogger.console(temp + "\n");
				EngineLogger.console("============");
			}
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
		renderPanelCom.remove(containerId);
	}

	private void removeAllComponent(Container layoutContainer) {
		// 依gxt容器內部資料做清除動作
		Iterator<Component> componentIt = layoutContainer.getItems().iterator();
		while (componentIt.hasNext()) {
			Component com = componentIt.next();
			if (com instanceof Container) {
				removeAllComponent((Container) com);
			} else if (com instanceof Grid) {
				removeColumnModel((Grid) com);
			} else if (com instanceof AdapterField) {
				removeAdaptWidget((AdapterField) com);
			}
			removeComponent(com.getId());
			removeRenderPanelComById(com.getId());
		}
		// 依renderPanelCom內的資料做清除動作
		removeRenderPanelCom(layoutContainer.getId());
		// 將面板清空
		layoutContainer.removeAll();
	}

	private void removeRenderPanelCom(String containerId) {
		if (renderPanelCom.get(containerId) == null) {
			return;
		}
		Iterator<String> comIt = renderPanelCom.get(containerId).iterator();
		while (comIt.hasNext()) {
			removeComponent(comIt.next());
		}
		renderPanelCom.remove(containerId);
	}

	private void removeRenderPanelComById(String id) {
		Set key = renderPanelCom.keySet();
		Iterator it = key.iterator();
		while (it.hasNext()) {
			String ke = it.next().toString() + "";
			Set set = renderPanelCom.get(ke);
			if (set.contains(id)) {
				set.remove(id);
				return;
			}
		}
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
			String id = columnIt.next().getId();
			removeComponent(id);
			removeRenderPanelComById(id);
		}
		// 透過DomQuery，找到grid內，自訂id_rowIndex的元件，並清除
		com.google.gwt.dom.client.NodeList<Element> nodes = DomQuery.select(
				"*[id*=_]", grid.getElement());
		for (int i = 0; i < nodes.getLength(); i++) {
			Element ele = nodes.getItem(i);
			removeComponent(ele.getId());
			removeRenderPanelComById(ele.getId());
		}
	}

	/**
	 * 清掉adaptField同時，要將adaptField裡面的widget從EngineDataStore中移除
	 * 
	 * @param field
	 */
	private void removeAdaptWidget(AdapterField field) {
		com.google.gwt.user.client.ui.Widget widget = field.getWidget();
		if (widget instanceof Container) {
			removeAllComponent((Container) widget);
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
				NodeList nList = ComLibrary.replaceNode(node, gul);
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
		// 增加uiGenNodeList不為空的判斷，多次調用renderpage()時之前的uiGenNodeList會被清空
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
				// 增加判斷，如果uiGenNodeList包含之前取到的元素，才調用remove
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

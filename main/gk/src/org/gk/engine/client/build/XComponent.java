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
package org.gk.engine.client.build;

import java.util.List;

import jfreecode.gwt.event.client.bus.EventBusIfc;
import jfreecode.gwt.event.client.bus.JsonConvert;

import org.gk.engine.client.IEngine;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.event.EventListener;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.engine.client.utils.NodeUtils;
import org.gk.ui.client.com.IC;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Node;

/**
 * GK核心元件
 * 
 * <pre>
 * 畫面元件根類別，提供共同的屬性設定
 * </pre>
 * 
 * @author I21890
 * @since 2010/7/26
 */
public abstract class XComponent implements UIGen {
	public static final String DATA = "_gk_data";
	protected EventBusIfc bus = IEngine.bus;

	protected String tag;
	protected String content;

	protected String id, type;
	protected String width, height, enable, visible;
	protected String init, bean, borders;
	protected String tabIndex, style, title, clazz;

	protected String resizable, preserveRatio, data;
	protected String maxHeight, maxWidth, minHeight, minWidth;

	protected Node node;

	protected List widgets;

	public XComponent() {
	}

	public XComponent(Node node, List widgets) {
		this.node = node;
		this.widgets = widgets;

		tag = node.getNodeName();

		id = getAttribute("id", tag + "-" + XDOM.getUniqueId());
		type = getAttribute("type", "unknownComType");
		width = getAttribute("width", "");
		height = getAttribute("height", "");
		enable = getAttribute("enable", "true");
		visible = getAttribute("visible", "true");
		init = getAttribute("init", "");
		bean = getAttribute("bean", "");
		borders = getAttribute("borders", "false");
		tabIndex = getAttribute("tabIndex", "");
		style = getAttribute("style", "");
		clazz = getAttribute("class", "_none_");
		title = getAttribute("title", "");
		data = getAttribute("data", "");
		resizable = getAttribute("resizable", "false");
		preserveRatio = getAttribute("preserveRatio", "false");
		maxHeight = getAttribute("maxHeight", "");
		maxWidth = getAttribute("maxWidth", "");
		minHeight = getAttribute("minHeight", "");
		minWidth = getAttribute("minWidth", "");

		EngineDataStore.addUIGenNode(id, this);
	}

	/**
	 * 將gul上的attribute data='' 轉成Map or List or String
	 * 
	 * @return
	 */
	public Object getData() {
		try {
			char startChar = data.length() > 0 ? data.charAt(0) : ' ';
			if (startChar == '{' || startChar == '[') {
				JSONValue jsonObj = JSONParser.parseLenient(data);
				if (jsonObj instanceof JSONObject) {
					return JsonConvert.jsonToMap((JSONObject) jsonObj);
				} else {
					return JsonConvert.jsonToList((JSONArray) jsonObj);
				}
			} else {
				return data;
			}
		} catch (Exception e) {
			return "JSONParser exception!" + e.getMessage() + ",data [" + data
					+ "]";
		}
	}

	public String getTag() {
		return tag;
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String hide) {
		this.visible = hide;
	}

	public String getInit() {
		return init;
	}

	public String getBean() {
		return bean;
	}

	public String getBorders() {
		return borders;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public String getStyle() {
		return style;
	}

	public String getClazz() {
		return clazz;
	}

	public String getTitle() {
		return title;
	}

	public Node getNode() {
		return node;
	}

	public String getResizable() {
		return resizable;
	}

	public String getPreserveRatio() {
		return preserveRatio;
	}

	public String getMaxHeight() {
		return maxHeight;
	}

	public String getMaxWidth() {
		return maxWidth;
	}

	public String getMinHeight() {
		return minHeight;
	}

	public String getMinWidth() {
		return minWidth;
	}

	public List getWidgets() {
		return widgets;
	}

	/**
	 * 元件建構完成後透過此方法初始化
	 */
	@Override
	public void init() {
		EventCenter.exec(id, init, this, null);
	}

	/**
	 * 回傳的資訊 (目前已完成檔案讀取)
	 * 
	 * <pre>
	 * 當元件有設定動態語法File時，此方法會被調用， 注入取得的資訊
	 * </pre>
	 * 
	 * @param eventId
	 * @param content
	 */
	public void onInfo(String eventId, String content) {
		Object info;
		if (JsonConvert.isJSONString(content)) {
			info = JsonConvert.jsonString2Object(content);
		} else {
			info = content;
		}
		Component com = getComponent();
		if (com instanceof IC) {
			((IC) com).setInfo(info);
		}
	}

	/**
	 * 元件建構後透過此方法設定屬性
	 * 
	 * @param com
	 */
	protected void initComponent(Component com) {
		com.setId(id);
		if (!width.equals("")) {
			com.setWidth(width);
		}
		if (!height.equals("")) {
			com.setHeight(height);
		}
		if (!data.equals("")) {
			com.setData(DATA, getData());
		}
		// 設定元件是否致能
		boolean bool = Boolean.parseBoolean(enable);
		if (com.isEnabled() != bool) {
			com.setEnabled(!com.isEnabled());
		}
		// 設定元件是否不顯示
		if (!Boolean.parseBoolean(visible)) {
			com.setVisible(false);
		}

		com.setBorders(Boolean.parseBoolean(borders));
		// tabIndex為數字才去setTabIndex
		if (tabIndex.matches("\\d+")) {
			com.setTabIndex(Integer.parseInt(tabIndex));
		}

		initStyleAttribute(com);
		initClassAttribute(com);
		// 判斷如果有設定title，就設定到元件中
		if (!title.equals("")) {
			com.setTitle(title);
		}
		// 设定Component是否可以resize
		setResizePara(com);

		EngineDataStore.addComponent(com.getId(), com);
	}

	/**
	 * 设定Component是否可以resize，及相关resize属性参数
	 * 
	 * @param com
	 */
	public void setResizePara(Component com) {
		// 如果resizable为true，其他相关属性才生效，同时判断com需为BoxComponent
		if (Boolean.parseBoolean(resizable) && com instanceof BoxComponent) {
			Resizable r = new Resizable((BoxComponent) com);
			r.setDynamic(Boolean.parseBoolean(resizable));
			// 是否保持宽高比例不变
			r.setPreserveRatio(Boolean.parseBoolean(preserveRatio));
			// 最大高度
			if (maxHeight.matches(IRegExpUtils.POSITIVE_INTEGER)) {
				r.setMaxHeight(Integer.parseInt(maxHeight));
			}
			// 最大宽度
			if (maxWidth.matches(IRegExpUtils.POSITIVE_INTEGER)) {
				r.setMaxWidth(Integer.parseInt(maxWidth));
			}
			// 最小高度
			r.setMinHeight(getMinIntValue(minHeight, height, r.getMinHeight()));
			// 最小宽度
			r.setMinWidth(getMinIntValue(minWidth, width, r.getMinWidth()));
		}
	}

	/**
	 * 取得最小int值，取值优先级：minVlue > value > defValue
	 * 
	 * @param minValue
	 * @param value
	 * @param defValue
	 * @return int
	 */
	private int getMinIntValue(String minValue, String value, int defValue) {
		boolean minFlag = minValue.matches(IRegExpUtils.POSITIVE_INTEGER);
		boolean flag = value.matches(IRegExpUtils.POSITIVE_INTEGER);
		return minFlag ? Integer.parseInt(minValue) : flag ? Integer
				.parseInt(value) : defValue;
	}

	/**
	 * 取得屬性值
	 * 
	 * @param nodeName
	 * @param defaultValue
	 * @return String
	 */
	public String getAttribute(String nodeName, String defaultValue) {
		return NodeUtils.getNodeValue(node, nodeName, defaultValue);
	}

	@Override
	public String toString() {
		return id;
	}

	/**
	 * 將指定的Component添加指定的事件
	 * 
	 * @param com
	 *            要進行監聽的元件
	 * @param eventType
	 *            GXT的事件型別
	 * @param gulAttribute
	 *            GUL屬性,例如 <grid onClick='xxx'/> , onClick就是gulAttribute
	 */
	protected void addEventListener(Component com, EventType eventType,
			String gulAttribute) {
		if (gulAttribute != null && !gulAttribute.equals("")) {
			com.addListener(eventType,
					new EventListener(id, gulAttribute, this));
		}
	}

	/**
	 * <pre>
	 * 取得此物件生成的Component 
	 * 此方法必須在GK引擎完成Render後才能使用
	 * </pre>
	 * 
	 * @return Component
	 */
	public Component getComponent() {
		return EngineDataStore.getComponent(id);
	}

	/**
	 * 初始化style屬性
	 * 
	 * @param com
	 */
	private void initStyleAttribute(final Component com) {
		final String[] styleAtt = style.split(";");
		if (styleAtt.length == 0 || styleAtt[0].equals("")) {
			return;
		}
		// 讓元件加入DOM後進行style設定，可覆寫原先style
		com.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				for (int i = 0; i < styleAtt.length; i++) {
					String[] att = styleAtt[i].split(":");
					if (att.length == 2) {
						com.el()
								.setStyleAttribute(att[0].trim(), att[1].trim());
					}
				}
			}
		});
	}

	/**
	 * 初始化class屬性
	 * 
	 * @param com
	 */
	private void initClassAttribute(Component com) {
		if (!clazz.equals("_none_")) {
			com.setStyleName(clazz);
		}
	}
}

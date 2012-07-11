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
package org.gk.engine.client.build.field;

import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.com.form.gkDateField;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.form.TwinTriggerField;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.xml.client.Node;

/**
 * 欄位屬性
 * 
 * @author I21890 2010/1/12
 * @since 2010/7/26
 */
public abstract class XField extends XComponent {

	protected String label, name, value;
	protected String readOnly, allowBlank;
	protected String format, regex, pwd;
	protected String min, max, maxLength;
	protected String labelStyle, labelSeparator;
	protected String inputStyle, empty;
	protected String editable;
	protected String messageTarget;

	protected String onFocus, onBlur, onChange, onClick, onSelect, onTwinClick,
			onKeyUp;

	public XField(Node node, List widgets) {
		super(node, widgets);
		// 一般屬性
		label = super.getAttribute("label", "");
		name = super.getAttribute("name", label.equals("") ? id : label);
		value = super.getAttribute("value", "");
		allowBlank = super.getAttribute("allowBlank", "");
		readOnly = super.getAttribute("readOnly", "");
		format = super.getAttribute("format", "");
		regex = super.getAttribute("regex", "");
		pwd = super.getAttribute("pwd", "false");
		min = super.getAttribute("min", "");
		max = super.getAttribute("max", "");
		maxLength = super.getAttribute("maxLength", "");
		labelStyle = super.getAttribute("labelStyle", "");
		labelSeparator = super.getAttribute("labelSeparator", "");
		inputStyle = super.getAttribute("inputStyle", "");
		empty = super.getAttribute("empty", "");
		editable = super.getAttribute("editable", "");
		messageTarget = super.getAttribute("messageTarget", "tooltip");
		// 事件屬性
		onFocus = super.getAttribute("onFocus", "");
		onBlur = super.getAttribute("onBlur", "");
		onChange = super.getAttribute("onChange", "");
		onClick = super.getAttribute("onClick", "");
		onSelect = super.getAttribute("onSelect", "");
		onTwinClick = super.getAttribute("onTwinClick", "");
		onKeyUp = super.getAttribute("onKeyUp", "");

		content = node.getChildNodes() + "";
		if (content == null || content.equals("null")) {
			content = "";
		}
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getAllowBlank() {
		return allowBlank;
	}

	public String getReadOnly() {
		return readOnly;
	}

	public String getFormat() {
		return format;
	}

	public String getRegex() {
		return regex;
	}

	public String getPwd() {
		return pwd;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public String getLabelSeparator() {
		return labelSeparator;
	}

	public String getInputStyle() {
		return inputStyle;
	}

	public String getEmpty() {
		return empty;
	}

	public String getEditable() {
		return editable;
	}

	public String getMessageTarget() {
		return messageTarget;
	}

	public String getOnFocus() {
		return onFocus;
	}

	public String getOnBlur() {
		return onBlur;
	}

	public String getOnChange() {
		return onChange;
	}

	public String getOnClick() {
		return onClick;
	}

	public String getOnSelect() {
		return onSelect;
	}

	public String getOnTwinClick() {
		return onTwinClick;
	}

	public String getOnKeyUp() {
		return onKeyUp;
	}

	/**
	 * 因為在GridFieldBuilder中用setAttribute是在CellRenderBuilder的callback的時候才能做，
	 * 所以會導致設置屬性時候所取得的XField並不是自己本身，而是最後一個。所以需要一個clone來保存每個grid欄位中自己的XField
	 */
	public XField clone() {
		return this;
	}

	/**
	 * form和grid中的Component共有設定部分寫在此method中
	 * 
	 * @param com
	 */
	public void initializeComponent(Component com) {
		if (com instanceof Field) {
			Field field = (Field) com;
			if (!name.equals("")) {
				field.setName(name);
			}
			// 如果有填才進行設定
			if (!readOnly.equals("")) {
				field.setReadOnly(Boolean.parseBoolean(readOnly));
			}
			// 設定標題與欄位分隔符號
			field.setLabelSeparator(labelSeparator);
			// 設定顯示文字
			if (!empty.equals("")) {
				field.setEmptyText(empty);
			}
			// 只要是TextField型?的元件(包含繼承的元件)增加額外判斷
			if (field instanceof TextField) {
				setTextFieldAttribute((TextField) field);
			}
			// 設定LabelStyle
			initLabelStyle(field);
			// 設定InputStyle
			initInputStyle(field);
			// 設定MessageTarget
			field.setMessageTarget(messageTarget);

			addEventListener(field, Events.Focus, onFocus);
			addEventListener(field, Events.Change, onChange);
			addEventListener(field, Events.Blur, onBlur);
			addEventListener(field, Events.OnKeyUp, onKeyUp);

			// 針對TriggerField增加額外判斷與onClick、onSelect事件
			if (field instanceof TriggerField) {
				TriggerField tf = (TriggerField) field;
				if (!editable.equals("")) {
					tf.setEditable(Boolean.parseBoolean(editable));
				}
				// TriggerField內部改寫接收onClick事件後，會發佈TriggerClick事件
				addEventListener(tf, Events.TriggerClick, onClick);
				if (tf instanceof gkDateField) {
					addEventListener(((gkDateField) field).getDatePicker(),
							Events.Select, onSelect);
				} else {
					addEventListener(tf, Events.Select, onSelect);
				}

				// TwinTriggerField要多加一個TwinTrigger事件
				if (field instanceof TwinTriggerField) {
					addEventListener(field, Events.TwinTriggerClick,
							onTwinClick);
				}
			} else if (field instanceof CheckBox) {
				// RadioBox與CheckBox要多加一個onClick事件
				addEventListener(com, Events.Select, onClick);
			}
		} else if (com instanceof Button) {
			addEventListener(com, Events.Select, onClick);
		}
	}

	/**
	 * 只要是TextField元件(包含繼承的元件)，增加設定屬於該型別的屬性
	 * 
	 * @param field
	 */
	private void setTextFieldAttribute(final TextField field) {
		if (!regex.equals("")) {
			field.setRegex(regex);
		}
		field.setPassword(Boolean.parseBoolean(pwd));

		if (!allowBlank.equals("")) {
			field.setAllowBlank(Boolean.parseBoolean(allowBlank));
		}

		if (max.matches(IRegExpUtils.POSITIVE_INTEGER)) {
			field.setMaxLength(Integer.parseInt(max));
		}

		if (min.matches(IRegExpUtils.POSITIVE_INTEGER)) {
			field.setMinLength(Integer.parseInt(min));
		}

		// 由於element必須render後才能取得，所以註冊Render事件
		if (!maxLength.equals("")) {
			field.addListener(Events.Render, new Listener() {

				@Override
				public void handleEvent(BaseEvent be) {
					NodeList<Element> inputTag = field.getElement()
							.getElementsByTagName("input");
					if (inputTag.getLength() > 0) {
						// maxLength的「L」需大寫，於IE上才有效果
						inputTag.getItem(0)
								.setAttribute("maxLength", maxLength);
					}
				}
			});
		}
	}

	/**
	 * 設定input的style
	 * 
	 * @param field
	 */
	private void initInputStyle(Field field) {
		String[] split = inputStyle.split(";");
		if (split.length == 0 || split[0].equals("")) {
			return;
		}
		for (int i = 0; i < split.length; i++) {
			String[] att = split[i].split(":");
			if (att.length == 2) {
				field.setInputStyleAttribute(att[0].trim(), att[1].trim());
			}
		}
	}

	/**
	 * 通過vertical-align:
	 * top|middle|bottom，計算出對應的top-padding，以達到能夠通過vertical-align來設定Field
	 * Label的垂直位置 (IE中這種通過計算的方式來設定vertical-align不起作用)
	 * 
	 * @param field
	 */
	private void initLabelStyle(final Field field) {
		labelStyle = labelStyle.toLowerCase();
		final String[] split = labelStyle.split(";");
		final String vAlign = "vertical-align";
		if (split.length == 0 || labelStyle.indexOf(vAlign) < 0) {
			field.setLabelStyle(labelStyle);
			return;
		}
		field.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// 暫時只知道這種方式來取labelEl，以後有好的方法再行修正
				Element labelEl = field.getElement().getParentElement()
						.getParentElement().getFirstChildElement();
				if (labelEl == null) {
					return;
				}
				int labelOffsetHeight = labelEl.getOffsetHeight();
				// 記錄label寬度
				int labelWidth = labelEl.getOffsetWidth();
				for (int i = 0; i < split.length; i++) {
					String cell = split[i];
					String[] detail = cell.split(":");
					if (detail.length == 2 && detail[0].trim().equals(vAlign)) {
						int top = 0;
						int height = field.getHeight();
						if (detail[1].trim().equals("middle")) {
							top = (height - labelOffsetHeight) / 2;
						} else if (detail[1].trim().equals("bottom")) {
							top = (height - labelOffsetHeight);
						}
						String style = top == 0 ? "" : "padding-top:"
								+ (top + 4) + "px;";
						labelStyle += labelStyle.endsWith(";") ? style : ";"
								+ style;
						break;
					}
				}
				// 增加label width設定，將width設定回原來的寬度
				labelStyle += "width:" + labelWidth + "px;";
				labelEl.setAttribute("style", labelStyle);
			}
		});
	}
}

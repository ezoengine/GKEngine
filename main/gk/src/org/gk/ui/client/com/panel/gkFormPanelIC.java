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

import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkDateField;
import org.gk.ui.client.com.form.gkFormPanel;
import org.gk.ui.client.com.form.gkFormRow;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkListFieldIC;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.form.gkTextField;
import org.gk.ui.client.com.form.gkTimeField;
import org.gk.ui.client.com.form.gkYMField;
import org.gk.ui.client.com.utils.BindingUtils;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * <title>表單元件</title>
 * 
 * <pre>
 * 表單元件可使用gkFormRow排版，提供 setInfo , getInfo 取得所有欄位
 * 的值。每個欄位需透過此類別的 createXXXField方法建立，這樣才能binding
 * 該欄位，自動驗證欄位是否合法。
 * 目前提供的建立欄位有
 * TextField , NumberField , LabelField , Radio
 * CheckBox , FileUpload , ComboBox , TextArea
 * DateField , TimeField 
 * -=-=-=-=-
 *   事件清單
 * -=-=-=-=-
 * setInfo 設定所有欄位資訊
 * getInfo 取得所有欄位資訊
 * infoChange 欄位值變更通知
 * </pre>
 * 
 * @author I21890,張明龍、呂毓閔、黃國峰
 * @since 2009/07/06
 */
public abstract class gkFormPanelIC extends gkFormPanel implements IC {

	protected CoreIC core;

	@Override
	public CoreIC core() {
		return core;
	}

	/**
	 * 此元件IC擁有的事件清單
	 */
	public static interface Event {
		public final static String LOADED = ".loaded";
		public final static String SET_INFO = ".setInfo";
		public final static String INFO_CHANGE = ".infoChange";
		public final static String EDIT_FIELD = ".editField";
		public final static String DIRTY_FIELD = ".dirtyField";
	}

	public String evtLoaded() {
		return getId() + Event.LOADED;
	}

	public String evtSetInfo() {
		return getId() + Event.SET_INFO;
	}

	public String evtInfoChange() {
		return getId() + Event.INFO_CHANGE;
	}

	public String evtEditField() {
		return getId() + Event.EDIT_FIELD;
	}

	public String evtDirtyField() {
		return getId() + Event.DIRTY_FIELD;
	}

	/**
	 * 只要有任何欄位更新，就發佈infoChange事件
	 */
	protected Map info = new gkMap() {

		@Override
		public Object put(Object key, Object value) {
			Object rtn = super.put(key, value);
			infoChange(key, value);
			// 發佈infoChange事件
			core.getBus().publish(new EventObject(evtInfoChange(), info));
			return rtn;
		}
	};

	protected List dirtyList = new gkList() {
		private static final long serialVersionUID = 7938387831522388517L;

		@Override
		public boolean add(Object e) {
			boolean rtnBoolean = super.add(e);
			// 發佈dirtyField List資料
			core.getBus().publish(
					new EventObject(evtDirtyField(), e.toString()));
			return rtnBoolean;
		}
	};

	protected ModelData outerModelData;

	/**
	 * id自動設定為form開頭 (為了搭配demo,先拿掉此預設form的動作)
	 * 
	 * @param id
	 */
	public gkFormPanelIC(String id) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		init();
	}

	public gkFormPanelIC() {
		core = new CoreIC(this);
		core.init();
		init();
	}

	/**
	 * 將表單資訊同步更新傳進來的ModelData
	 */
	@Override
	public void linkInfo(Object md) {
		outerModelData = (ModelData) md;
	}

	/**
	 * 留給子類別，改寫此方法，處理欄位值改變後要做甚麼事
	 * 
	 * @param key
	 * @param value
	 */
	protected void infoChange(Object key, Object value) {
		if (outerModelData != null) {
			outerModelData.set((String) key, value);
		}
	}

	/**
	 * 訂閱 setInfo事件，設定所有欄位資訊
	 */
	@Override
	public void bindEvent() {
		core.subscribe(evtSetInfo(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map info = eo.getInfoMap();
				putInfo(info);
				// 更新所有欄位後，清掉dirty標記
				dirtyClean();
				// 發佈給ExprField事件
				core.getBus().publish(new EventObject(evtLoaded(), info));
			}
		});
	}

	protected void init() {
		info.put(Event.DIRTY_FIELD, dirtyList);
		setHeaderVisible(false);
		setLabelAlign(LabelAlign.RIGHT);
		setBodyBorder(false);
		setPadding(0);
		setFieldWidth(Style.DEFAULT);
		addField();
	}

	protected abstract void addField();

	/**
	 * 取得所有欄位資訊
	 * 
	 * @return Object
	 */
	@Override
	public Object getInfo() {
		return info;
	}

	/**
	 * 當AP呼叫此方法更新info時，需通知所有欄位進行更新
	 * 
	 * @param info
	 */
	public void putInfo(Map info) {
		core.getBus().publish(new EventObject(getId(), info));
	}

	/**
	 * 當AP呼叫此方法更新info時，需通知所有欄位進行更新
	 * 
	 * @param key
	 * @param value
	 */
	public void putInfo(String key, Object value) {
		info.put(key, value);
		core.getBus().publish(new EventObject(getId(), info));
	}

	@Override
	public void setInfo(Object info) {
		setInfo((Map) info);
	}

	/**
	 * 如果傳進來的Map是null或size=0的就不進行處理
	 * 
	 * @param info
	 */
	public void setInfo(Map info) {
		if (info != null && !info.isEmpty()) {
			core.getBus().publish(new EventObject(getId(), info));
		}
	}

	@Override
	public void clear() {
		for (Field field : getFields()) {
			field.clear();
			field.fireEvent(Events.Change);
		}
	}

	@Override
	public void reset() {
		for (Field field : getFields()) {
			field.reset();
			field.fireEvent(Events.Change);
		}
	}

	@Override
	public List<Field<?>> getFields() {
		List<Field<?>> fields = new gkList<Field<?>>();
		getChildFields(this, fields);
		return fields;
	}

	private void getChildFields(Container<Component> c, List<Field<?>> fields) {
		for (Component comp : c.getItems()) {
			if (comp instanceof Field) {
				if (comp instanceof AdapterField) {
					getChildFields(
							(Container) ((AdapterField) comp).getWidget(),
							fields);
				} else {
					fields.add((Field<?>) comp);
				}
			} else if (comp instanceof Container) {
				getChildFields((Container<Component>) comp, fields);
			}
		}
	}

	public TextField createTextField(String key) {
		TextField tf = new gkTextField();
		fieldBinding(tf, key);
		return tf;
	}

	public NumberField createNumberField(String key) {
		return BindingUtils.createNumberField(getId(), info, key, core);
	}

	/**
	 * 建立 LabelField
	 * 
	 * @param key
	 * @return LabelField
	 */
	public LabelField createLabelField(String key) {
		return BindingUtils.createLabelField(getId(), info, key, core);
	}

	public gkDateField createDateField(String key, String format) {
		return BindingUtils.createDateField(getId(), info, key, format, core);
	}

	public gkYMField createYMField(String key, String format) {
		return BindingUtils.createYMField(getId(), info, key, format, core);
	}

	public Radio createRadio(String key, String value) {
		return BindingUtils.createRadio(getId(), info, key, value, core);
	}

	public CheckBox createCheckBox(String key, String value) {
		return BindingUtils.createCheckBox(getId(), info, key, value, core);
	}

	public ComboBox createComboBox(String key) {
		return BindingUtils.createComboBox(getId(), info, key, core);
	}

	/**
	 * 建立ListFieldIC
	 * 
	 * @param key
	 * @return gkListFieldIC
	 */
	public gkListFieldIC createListFieldIC(String key) {
		return BindingUtils.createListFieldIC(getId(), info, key, core);
	}

	public gkTimeField createTimeField(String key) {
		return BindingUtils.createTimeField(getId(), info, key, core);
	}

	public void fieldBinding(Field field, gkFieldAccessIfc access) {
		BindingUtils.binding(getId(), field, info, access, core);
	}

	/**
	 * 欄位binding
	 * 
	 * @param field
	 * @param infoKey
	 */
	public void fieldBinding(Field field, String infoKey) {
		BindingUtils.binding(getId(), field, infoKey, info, core);
	}

	/**
	 * 清除dirtyFieldList內容
	 */
	public void dirtyClean() {
		if (info.containsKey(Event.DIRTY_FIELD)) {
			((List) info.get(Event.DIRTY_FIELD)).clear();
		}
	}

	/**
	 * 新增formRow
	 * 
	 * @return gkFormRow
	 */
	public gkFormRow createRow() {
		return createRow("");
	}

	/**
	 * 新增formRow
	 * 
	 * @param labelAlign
	 * @return gkFormRow
	 */
	public gkFormRow createRow(String labelAlign) {
		return new gkFormRow(labelAlign);
	}

	/**
	 * 將傳進來的元件進行binding
	 * 
	 * @param key
	 * @param ic
	 */
	public void icBinding(final String key, final IC ic) {
		core.subscribe(getId(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				if (!eo.getInfoMap().containsKey(key)) {
					return;
				}
				String value = (String) eo.getInfoMap().get(key);
				ic.setInfo(value);
				info.put(key, value);
			}
		});
		ic.linkInfo(info);
		info.put(key, ic.getInfo());
	}
}
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

import org.gk.ui.client.binding.gkFieldBinding;
import org.gk.ui.client.binding.gkFormBinding;
import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkFormPanel;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.utils.BindingUtils;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * 自定表單元件
 * 
 * @author I21890、I23250
 * @since 2009/07/06
 */
public abstract class gkFormPanelIC extends gkFormPanel implements IC {

	protected gkFormBinding formBinding;

	protected CoreIC core;

	@Override
	public CoreIC core() {
		return core;
	}

	/**
	 * 此元件IC擁有的事件清單
	 */
	public static interface Event {
		public final static String INFO_CHANGE = ".infoChange";
		public final static String DIRTY_FIELD = ".dirtyField";
	}

	public String evtInfoChange() {
		return getId() + Event.INFO_CHANGE;
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

	protected List dirtyList = new gkList();

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

	@Override
	public void bindEvent() {

	}

	protected void init() {
		formBinding = new gkFormBinding();
		info.put(Event.DIRTY_FIELD, dirtyList);
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

	@Override
	public void setInfo(Object info) {
		if (info instanceof Map) {
			setInfo((Map) info);
		}
	}

	private void setInfo(Map info) {
		// 如果傳進來的Map是null或size=0的就不進行處理
		if (info != null && !info.isEmpty()) {
			formBinding.publish(info);
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

	@Deprecated
	public void fieldBinding(Field field, gkFieldAccessIfc access) {
		BindingUtils.binding(getId(), field, info, access, core);
	}

	/**
	 * 加入欄位binding
	 * 
	 * @param fieldBinding
	 */
	public void addFieldBinding(gkFieldBinding fieldBinding) {
		formBinding.addFieldBinging(fieldBinding);
	}

	/**
	 * 清空Dirty Field
	 */
	public void cleanDirtyField() {
		if (info.containsKey(Event.DIRTY_FIELD)) {
			((List) info.get(Event.DIRTY_FIELD)).clear();
		}
	}
}
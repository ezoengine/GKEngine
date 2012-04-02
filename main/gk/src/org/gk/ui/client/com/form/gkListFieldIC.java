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
package org.gk.ui.client.com.form;

import java.util.Iterator;
import java.util.List;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ListField;

/**
 * 多選選單
 * 
 * @author W11239
 */
public class gkListFieldIC extends ListField implements IC {

	protected CoreIC core;
	protected ListStore store;
	protected List<gkMap> selectList = new gkList<gkMap>();
	private List originalValue;

	@Override
	public CoreIC core() {
		return core;
	}

	public gkListFieldIC() {
		init();
	}

	private void init() {
		core = new CoreIC(this);
		core.init();
		store = new ListStore();
		setStore(store);
		getListView().setStore(store);
		setValueField("value");
	}

	@Override
	public void setInfo(Object info) {
		selectList = transToList((List) info);
		store.removeAll();
		store.add(selectList);
	}

	@Override
	public Object getInfo() {
		return selectList;
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}

	public List getSelectedItem() {
		return getListView().getSelectionModel().getSelectedItems();
	}

	@Override
	public void bindEvent() {

	}

	@Override
	public void reset() {
		super.reset();
		setSelectItem(originalValue);
	}

	/**
	 * 如果傳入的是一個字符數組，則預設為valueField值進行匹配來設定選定項
	 * 
	 * @param selectValues
	 */
	public void setSelectItem(String[] selectValues) {
		if (selectValues == null || selectValues.length <= 0) {
			return;
		}
		String key = getValueField();
		List matchList = new gkList();
		for (Iterator it = selectList.iterator(); it.hasNext();) {
			ModelData record = (ModelData) it.next();
			String value = record.get(key) + "";
			for (int i = 0; i < selectValues.length; i++) {
				if (value != null && value.equals(selectValues[i])) {
					matchList.add(record);
				}
			}
		}
		getListView().getSelectionModel().setSelection(matchList);
		originalValue = originalValue == null ? matchList : originalValue;
	}

	/**
	 * 通過displayField值來設定選定項
	 * 
	 * @param list
	 */
	public void setSelectItem(List list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		String key = getDisplayField();
		List matchList = new gkList();
		for (Iterator it = selectList.iterator(); it.hasNext();) {
			ModelData record = (ModelData) it.next();
			String value = record.get(key) + "";
			for (Iterator it2 = list.iterator(); it2.hasNext();) {
				ModelData md = (ModelData) it2.next();
				if (value != null && value.equals(md.get(key) + "")) {
					matchList.add(record);
				}
			}
		}
		getListView().getSelectionModel().setSelection(matchList);
		originalValue = originalValue == null ? matchList : originalValue;
	}

	/**
	 * <pre>
	 * 後端傳回來的List資料如果不是List<Map>而是List<String>
	 * 將自動用gkMap包裝該字串 key: name , value: 該字串
	 * </pre>
	 * 
	 * @param inputList
	 * @return List
	 */
	private List<gkMap> transToList(List inputList) {
		List data = new gkList();
		for (int i = 0; i < inputList.size(); i++) {
			// 如果本身就是gkMap，就不做轉換
			if (inputList.get(i) instanceof String) {
				data.add(new gkMap(getDisplayField(), "" + inputList.get(i)));
			} else {
				return inputList;
			}
		}
		return data;
	}
}

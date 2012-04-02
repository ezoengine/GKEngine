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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * ComboBox元件
 * 
 * @author i23250
 * @since 2011/5/27
 */
public class gkComboBox extends ComboBox {
	// 用來紀錄是否按下了comboBox的下拉選單按鈕(Trigger)，
	// ListAttribute會根據是否按下而自動進行展開動作
	private boolean triggerExpand = false;

	public gkComboBox() {
		setLazyRender(false);
		setPropertyEditor(new gkListModelPropertyEditor());
		// 每次點選下拉選單時，都取得所有選單
		setTriggerAction(TriggerAction.ALL);
	}

	public boolean isTriggerExpand() {
		return triggerExpand;
	}

	@Override
	public void expand() {
		super.expand();
		triggerExpand = false;
	}

	@Override
	protected void onTriggerClick(ComponentEvent ce) {
		if (!isExpanded()) {
			triggerExpand = true;
		}
		super.onTriggerClick(ce);
	}

	@Override
	public void focus() {
		if (rendered) {
			getFocusEl().focus();
			onFocus(new FieldEvent(this));
		}
	}

	/**
	 * 根據輸入的value，尋找完整選項資料
	 * 
	 * @param value
	 * @return ModelData
	 */
	public ModelData findModel(String value) {
		for (Object obj : store.getModels()) {
			ModelData md = (ModelData) obj;
			if (value.equals(md.get("value"))) {
				return md;
			}
		}
		return null;
	}
}

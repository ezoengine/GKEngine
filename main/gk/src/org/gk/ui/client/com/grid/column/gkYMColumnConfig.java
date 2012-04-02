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
package org.gk.ui.client.com.grid.column;

import java.util.Date;

import org.gk.ui.client.com.form.gkYMField;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.i18n.client.DateTimeFormat;

public abstract class gkYMColumnConfig extends gkCellColumnConfig {

	public gkYMColumnConfig(gkColumnInfo field) {
		super(field);
	}

	@Override
	protected Field createField() {
		return new gkYMField() {
			@Override
			public void focus() {
				if (rendered) {
					getFocusEl().focus();
					onFocus(new FieldEvent(this));
				}
				if (!hasFocus) {
					fireEvent(Events.Focus);
				}
			}

			@Override
			public void setFieldLabel(String fieldLabel) {
				super.setFieldLabel(fieldLabel);
				setHeader(fieldLabel);
			}

			@Override
			protected void blur() {
				if (rendered) {
					getFocusEl().blur();
				}
				if (hasFocus) {
					fireEvent(Events.Blur);
					hasFocus = false;
				}
			}
		};
	}

	@Override
	protected CellEditor createCellEditor() {
		final gkYMField dateField = (gkYMField) createField();
		addListener(dateField);
		onField(dateField);
		return new CellEditor(dateField) {

			@Override
			public String getDisplayValue(Object value) {
				if (value != null && !value.toString().equals("")) {
					// 考慮到當使用api set data到store時，未將資料設到欄位時的情形，
					// 所以先set data給欄位後，再取showDate
					dateField.setUseDate(value.toString());
					return dateField.getShowDate(dateField.getInputDate());
				}

				return super.getDisplayValue(value);
			}

			/**
			 * 複寫onBlur，為了判斷當日曆選單開啟狀態，不進行onBlur()
			 * 當開啟日曆選單後，進行日曆挑選，就會觸發inputField的onBlur， 但當下挑選的值還沒寫setValue，
			 * 所以onBlur()取得的值是舊的! 因此必須判斷Window還沒關閉時，就不進行onBlur()，然後 註冊dateField
			 * Change事件，在觸發Change事件後，才調用父類(Editor)
			 * 的onBlur()取值顯示到畫面，此時getValue才是真正日曆挑選的值
			 */
			@Override
			protected void onBlur(FieldEvent fe) {
				if (dateField.getYMWindow().isAttached()) {
					return;
				} else {
					super.onBlur(fe);
				}
			}

			@Override
			/**
			 * click cellEditor後觸發，不拿顯示的值，直接從gkDateField欄位拿取
			 * 省得還要將"2011/09/12" 字串轉成 Date物件
			 * fix: 拿不到DateField參考(所有物件同一參考),還是得轉Date物件
			 */
			public Object preProcessValue(Object value) {
				if (value == null || value.toString().equals("")) {
					return null;
				}
				dateField.setUseDate("" + value);
				return dateField.getInputDate();
			}

			/**
			 * DateField set/get Value是Date，為了顯示到畫面上， 必須轉成日期格式的字串
			 * fix:顯示在畫面的格式由gridView處理，這裡回傳要存在store的資料格式
			 */
			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return "";
				}

				return DateTimeFormat.getFormat("yyyyMM").format((Date) value);
			}
		};
	}

	@Override
	protected Field createColumnCell(final ModelData model,
			final String property, ListStore<ModelData> store,
			final int rowIndex, final int colIndex, final Grid<ModelData> grid) {
		final gkYMField ym = (gkYMField) createField();
		onField(ym);
		// 当刷新栏位时运行。从store里取得值。如果没有这段刷新出的栏位将没有值
		if (model.get(property) != null
				&& model.get(property).toString().length() != 0) {
			ym.setUseDate(model.get(property).toString());
		} else {
			if (!"error".equals(ym.getUseDate())) {
				model.set(property, ym.getUseDate());
			}
		}
		// change事件，输入值后运行
		ym.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				model.set(property, ym.getUseDate());
			}
		});

		addListener(ym, grid, rowIndex, colIndex, store);
		return ym;
	}
}

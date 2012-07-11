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

import java.util.Date;

import org.gk.ui.client.com.i18n.CDateTimeFormat;
import org.gk.ui.client.com.utils.DateTimeUtils;

import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;

/**
 * 年月欄位元件，提供設定畫面挑選元件要為西元年或是民國年格式以及年、月曆挑選元件
 * 
 * @author I23979,I24580,I23250
 * @since 2009/11/13
 */
public class gkYMField extends DateField {

	// 畫面彈跳出來的window
	private Window w = new Window();

	// 年月挑選視窗
	public gkYMPicker picker;

	public gkYMField() {
		getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy/MM"));
	}

	public void setFormat(String pattern) {
		pattern = DateTimeUtils.normalize(pattern);
		if (pattern.matches("[^y]*y{3}[^y]*")) {
			getPropertyEditor().setFormat(CDateTimeFormat.getFormat(pattern));
			getYMPicker().setChineseYear(true);
		} else {
			getPropertyEditor().setFormat(DateTimeFormat.getFormat(pattern));
		}
		if (pattern.equals("yyy") || pattern.equals("yyyy")) {
			getYMPicker().setYearPicker(true);
		}
	}

	public gkYMPicker getYMPicker() {
		if (picker == null) {
			picker = new gkYMPicker(this);
		}
		return picker;
	}

	public void select(Date date) {
		if (date != null) {
			setValue(date);
			fireEvent(Events.Select);
			focus();
		}
		w.hide();
	}

	@Override
	protected void expand() {
		// 先移除window中所有元件
		w.removeAll();
		// 設定自動隱藏
		w.setAutoHide(true);
		Object v = getValue();
		Date d = null;
		if (v instanceof Date) {
			d = (Date) v;
		} else {
			d = new Date();
		}

		getYMPicker().setValue(d);
		// 更新日曆內容
		getYMPicker().updateContent();
		// 為了避免大小跑掉, 所以設定固定大小
		w.setWidth(200);
		// 設定開啟的位置
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				w.setVisible(true);
				w.el().alignTo(el().dom, "tl-bl?", new int[] { 0, 0 });
				w.focus();
			}
		});
		// 加到視窗中
		w.add(picker);
		w.layout();
	}

	@Override
	protected boolean validateBlur(DomEvent e, Element target) {
		return w == null || (w != null && !w.isVisible());
	}

	@Override
	public void focus() {
		if (rendered) {
			getFocusEl().focus();
			onFocus(new FieldEvent(this));
		}
	}
}

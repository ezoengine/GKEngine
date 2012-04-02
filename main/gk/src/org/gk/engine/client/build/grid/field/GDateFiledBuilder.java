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
package org.gk.engine.client.build.grid.field;

import org.gk.engine.client.build.grid.XGridField;
import org.gk.ui.client.com.form.gkDateField;
import org.gk.ui.client.com.grid.column.gkDateColumnConfig;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GDateFiledBuilder extends GridFieldBuilder {

	public GDateFiledBuilder(String date) {
		super(date);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkDateColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);
				String format = x.getFormat();
				String value = x.getValue();
				gkDateField df = (gkDateField) field;
				// 已經render的dateField必須使用setuseDate來設定欄位的值
				if (!value.equals("")) {
					df.setUseDate(value);
				}
				// 设定日期显示格式
				if (!format.equals("")) {
					df.setFormat(format);
				}
			}
		};
		return cc;
	}
}

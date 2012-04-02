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

import java.util.Map;

import org.gk.engine.client.build.grid.XGridField;
import org.gk.ui.client.com.form.gkDateRangeField;
import org.gk.ui.client.com.form.gkDateRangeField.DateRangeType;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.column.gkDateRangeColumnConfig;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GDateRangeFieldBuilder extends GridFieldBuilder {

	public GDateRangeFieldBuilder(String dateRange) {
		super(dateRange);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();

		ColumnConfig cc = new gkDateRangeColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);
			}

			@Override
			public Field createField() {
				String name = x.getName();
				String ext = x.getAttribute("ext", "");
				String separate = x.getAttribute("separate", "false");
				String fmt = x.getFormat();
				String value = x.getValue();
				String initDate = x.getAttribute("initDate", "false");

				String dateWidth = x.getAttribute("dateWidth", "");
				String timeWidth = x.getAttribute("timeWidth", "");
				String inner = getField().getAttribute("inner", "");
				String editable = getField().getAttribute("editable", "true");

				gkDateRangeField dates;
				// dateRange有三種格式，兩日期、兩日期兩時間、一日期兩時間(預設一日期兩時間)
				if (DateRangeType.DATES.value().equalsIgnoreCase(ext)) {
					dates = new gkDateRangeField(x.getLabel(), name + "-dateS",
							name + "-dateE", fmt,
							Boolean.parseBoolean(separate)) {
						@Override
						public void setFieldLabel(String fieldLabel) {
							super.setFieldLabel(fieldLabel);
							setHeader(fieldLabel);
						}
					};
				} else if (DateRangeType.DATESTIMES.value().equalsIgnoreCase(
						ext)) {
					dates = new gkDateRangeField(x.getLabel(), name + "-dateS",
							name + "-timeS", name + "-dateE", name + "-timeE",
							fmt, Boolean.parseBoolean(separate)) {
						@Override
						public void setFieldLabel(String fieldLabel) {
							super.setFieldLabel(fieldLabel);
							setHeader(fieldLabel);
						}
					};
				} else {
					dates = new gkDateRangeField(x.getLabel(), name + "-dateS",
							name + "-timeS", name + "-timeE", fmt) {
						@Override
						public void setFieldLabel(String fieldLabel) {
							super.setFieldLabel(fieldLabel);
							setHeader(fieldLabel);
						}
					};
				}

				Map datas = getValues(value);
				dates.setDefaultBeginDate(chkObj(datas, "dateS"));
				dates.setDefaultEndDate(chkObj(datas, "dateE"));
				dates.setDefaultBeginTime(chkObj(datas, "timeS"));
				dates.setDefaultEndTime(chkObj(datas, "timeE"));

				// 若initDate="true"，設定日期欄位默認為當天日期
				if (Boolean.parseBoolean(initDate)) {
					dates.initDate();
				}

				if (!dateWidth.equals("")) {
					dates.setDateFieldWidth(dateWidth);
				}

				if (!timeWidth.equals("")) {
					dates.setTimeFieldWidth(timeWidth);
				}

				if (!inner.equals("")) {
					dates.setInnerLabel(inner);
				}
				dates.setEditable(Boolean.parseBoolean(editable));
				return dates;
			}
		};
		return cc;
	}

	private Map getValues(String value) {
		// value格式：
		// 1.value='20090110,200100101'
		// 2.value='20090110,1100,1200'
		// 3.value='20090110,1100,200100101,1200'
		// 其中日期一定要是yyyymmdd八碼，時間為hhmm四碼
		Map result = new gkMap();
		String[] split = value.split(",");
		result.put("dateS", split[0]);
		if (split.length == 2) {
			result.put("dateE", split[1]);
		} else if (split.length == 3) {
			result.put("timeS", split[1]);
			result.put("timeE", split[2]);
		} else if (split.length == 4) {
			result.put("dateE", split[2]);
			result.put("timeS", split[1]);
			result.put("timeE", split[3]);
		}

		return result;
	}

	private String chkObj(Map m, String key) {
		return m.get(key) == null ? "" : m.get(key).toString();
	}
}

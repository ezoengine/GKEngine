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
package org.gk.engine.client.build.form.field;

import java.util.Map;

import org.gk.ui.client.com.form.gkDateRangeField;
import org.gk.ui.client.com.form.gkDateRangeField.DateRangeType;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.panel.gkFieldAccessIfc;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.widget.Component;

public class DateRangeFieldBuilder extends FormFieldBuilder {

	public DateRangeFieldBuilder(String dateRange) {
		super(dateRange);
	}

	@Override
	public Component create() {
		return createField();
	}

	@Override
	public Component create(gkFormPanelIC form) {
		final gkDateRangeField field = createField();
		final String name = getField().getName();

		form.fieldBinding(field, new gkFieldAccessIfc() {

			@Override
			public void setValue(Object obj) {
				if (obj != null) {
					gkMap info = (gkMap) obj;
					field.setDefaultBeginDate(chkObj(info, name + "-dateS"));
					field.setDefaultEndDate(chkObj(info, name + "-dateE"));
					field.setDefaultBeginTime(chkObj(info, name + "-timeS"));
					field.setDefaultEndTime(chkObj(info, name + "-timeE"));
				}
			}

			@Override
			public Object getValue(String id) {
				if (id.equals(name + "-dateS")) {
					return field.getDefaultBeginDate();
				} else if (id.equals(name + "-dateE")) {
					return field.getDefaultEndDate();
				} else if (id.equals(name + "-timeS")) {
					return field.getDefaultBeginTime();
				} else {
					return field.getDefaultEndTime();
				}
			}
		});

		return field;
	}

	private gkDateRangeField createField() {
		String label = getField().getLabel();
		String name = getField().getName();
		String ext = getField().getAttribute("ext", "");
		String separate = getField().getAttribute("separate", "false");
		String fmt = getField().getFormat();
		String value = getField().getValue();
		String initDate = getField().getAttribute("initDate", "false");

		String dateWidth = getField().getAttribute("dateWidth", "");
		String timeWidth = getField().getAttribute("timeWidth", "");
		String inner = getField().getAttribute("inner", "");
		String editable = getField().getAttribute("editable", "true");

		gkDateRangeField dates;
		// dateRange有三種格式，兩日期、兩日期兩時間、一日期兩時間(預設一日期兩時間)
		if (DateRangeType.DATES.value().equalsIgnoreCase(ext)) {
			dates = new gkDateRangeField(getField().getLabel(),
					name + "-dateS", name + "-dateE", fmt,
					Boolean.parseBoolean(separate));
		} else if (DateRangeType.DATESTIMES.value().equalsIgnoreCase(ext)) {
			dates = new gkDateRangeField(getField().getLabel(),
					name + "-dateS", name + "-timeS", name + "-dateE", name
							+ "-timeE", fmt, Boolean.parseBoolean(separate));
		} else {
			dates = new gkDateRangeField(getField().getLabel(),
					name + "-dateS", name + "-timeS", name + "-timeE", fmt);
		}
		dates.setFieldLabel(label);

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

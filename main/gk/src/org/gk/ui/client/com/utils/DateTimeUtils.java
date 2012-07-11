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
package org.gk.ui.client.com.utils;

import java.util.Date;

import org.gk.ui.client.com.form.gkDateField;
import org.gk.ui.client.com.form.gkYMField;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateTimeUtils {

	private static DateTimeFormat format = DateTimeFormat.getFormat("yyyyMMdd");
	private static DateTimeFormat ym = DateTimeFormat.getFormat("yyyyMM");
	private static DateTimeFormat year = DateTimeFormat.getFormat("yyyy");

	/**
	 * 根據輸入的field，決定要取得8碼、6碼或4碼日期
	 * 
	 * @param field
	 * @return String
	 */
	public static String getValue(DateField field) {
		String result = "";
		Date date = field.getValue();
		if (date != null) {
			if (field instanceof gkDateField) {
				result = formatDate(date);
			} else if (field instanceof gkYMField) {
				gkYMField ym = (gkYMField) field;
				if (ym.getYMPicker().isYearPicker()) {
					result = formatYear(date);
				} else {
					result = formatYM(date);
				}
			}
		}
		return result;
	}

	/**
	 * 根據輸入的field，決定要設定字串到哪種field
	 * 
	 * @param field
	 * @param str
	 */
	public static void setValue(DateField field, String str) {
		Date date = null;
		if (field instanceof gkDateField) {
			date = parseDate(str);
		} else if (field instanceof gkYMField) {
			gkYMField ym = (gkYMField) field;
			if (ym.getYMPicker().isYearPicker()) {
				date = parseYear(str);
			} else {
				date = parseYM(str);
			}
		}
		field.setValue(date);
	}

	/**
	 * 將Date物件格式化成8碼日期
	 * 
	 * @param date
	 * @return String
	 */
	public static String formatDate(Date date) {
		return format.format(date);
	}

	/**
	 * 將Date物件格式化成6碼年月
	 * 
	 * @param date
	 * @return String
	 */
	public static String formatYM(Date date) {
		return ym.format(date);
	}

	/**
	 * 將Date物件格式化成4碼年
	 * 
	 * @param date
	 * @return String
	 */
	public static String formatYear(Date date) {
		return year.format(date);
	}

	/**
	 * 將8碼日期格式化成Date物件
	 * 
	 * @param str
	 * @return Date
	 */
	public static Date parseDate(String str) {
		if (str == null || str.equals("")) {
			return null;
		}
		if (str.indexOf("/") != -1) {
			str = str.replaceAll("/", "");
		}
		return format.parseStrict(str);
	}

	/**
	 * 將6碼年月格式化成Date物件
	 * 
	 * @param str
	 * @return Date
	 */
	public static Date parseYM(String str) {
		if (str == null || str.equals("")) {
			return null;
		}
		if (str.indexOf("/") != -1) {
			str = str.replaceAll("/", "");
		}
		return ym.parseStrict(str);
	}

	/**
	 * 將4碼年格式化成Date物件
	 * 
	 * @param str
	 * @return Date
	 */
	public static Date parseYear(String str) {
		if (str == null || str.equals("")) {
			return null;
		}
		return year.parseStrict(str);
	}

	/**
	 * 將輸入的pattern標準化
	 * 
	 * @param pattern
	 * @return String
	 */
	public static String normalize(String pattern) {
		return pattern.replaceAll("Y", "y").replaceAll("m", "M")
				.replaceAll("D", "d");
	}
}

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
package org.gk.ui.client.com.i18n;

import java.util.Date;
import java.util.Map;

import org.gk.ui.client.com.form.gkMap;

import com.google.gwt.i18n.client.DateTimeFormat;

public class CDateTimeFormat extends DateTimeFormat {

	private static final Map<String, DateTimeFormat> cache = new gkMap();

	protected CDateTimeFormat(String pattern) {
		super(pattern);
	}

	public static DateTimeFormat getFormat(String pattern) {
		DateTimeFormat dtf = cache.get(pattern);
		if (dtf == null) {
			dtf = new CDateTimeFormat(pattern);
			cache.put(pattern, dtf);
		}
		return dtf;
	}

	@Override
	public String format(Date date) {
		String year = DateTimeFormat.getFormat(PredefinedFormat.YEAR).format(
				date);
		String chinese = convertToCYear(year);
		String result = super.format(date);
		return result.replaceAll(year, chinese);
	}

	@Override
	@SuppressWarnings("deprecation")
	public Date parseStrict(String text) throws IllegalArgumentException {
		Date result = super.parseStrict(text);
		result.setYear(result.getYear() + 1911);
		return result;
	}

	@Override
	@SuppressWarnings("deprecation")
	public Date parse(String text) throws IllegalArgumentException {
		Date result = super.parse(text);
		result.setYear(result.getYear() + 1911);
		return result;
	}

	private String convertToCYear(String year) {
		int value = Integer.parseInt(year) - 1911;
		if (value <= 0) {
			return "000";
		}
		StringBuffer result = new StringBuffer(String.valueOf(value));
		// 不滿三位數以上則補0
		if (result.length() < 3) {
			for (int i = 3 - result.length(); i > 0; i--) {
				result.insert(0, "0");
			}
		}
		return result.toString();
	}
}

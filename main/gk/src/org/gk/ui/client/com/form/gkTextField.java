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

import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * 改寫 TextField，成為中冠公用的 TextField
 * 
 * @author I21890
 * @since 2009/05/22
 */
public class gkTextField<D> extends TextField<D> {

	private final static String AUTO_UPPER_CASE = "1";
	private final static String AUTO_LOWER_CASE = "-1";
	// normal case
	private String convertTextCase = "0";

	@Override
	public String getRawValue() {
		if (convertTextCase.equals(AUTO_LOWER_CASE)) {
			return super.getRawValue().toLowerCase();
		} else if (convertTextCase.equals(AUTO_UPPER_CASE)) {
			return super.getRawValue().toUpperCase();
		} else {
			return super.getRawValue();
		}
	};

	/**
	 * 將輸入的字串轉成大寫
	 */
	public void autoUpperCase() {
		this.convertTextCase = AUTO_UPPER_CASE;
		setInputStyleAttribute("text-transform", "uppercase");
	}

	/**
	 * 將輸入的字串轉成小寫
	 */
	public void autoLowerCase() {
		this.convertTextCase = AUTO_LOWER_CASE;
		setInputStyleAttribute("text-transform", "lowercase");
	}
}

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

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.ui.Widget;

/**
 * Layout工具
 * 
 * @author i23250
 * @since 2011/08/09
 */
public class LayoutUtils {

	private static final String INTEGER = "^-?\\d+$";

	/**
	 * 根據Widget與其width、height，建立FormData
	 * 
	 * @param widget
	 * @param width
	 * @param height
	 * @return FormData
	 */
	public static FormData createFormData(Widget widget, String width,
			String height) {
		FormData fd = new FormData();
		String widthSpec = "none";
		String heightSpec = "none";
		if (widget instanceof Field) {
			if (width.matches(INTEGER)) {
				fd.setWidth(Integer.parseInt(width));
			}

			if (height.matches(INTEGER)) {
				fd.setHeight(Integer.parseInt(height));
			}
		}
		if (width.length() > 0 && width.endsWith("%")) {
			widthSpec = width;
		}
		if (height.length() > 0 && height.endsWith("%")) {
			heightSpec = height;
		}
		fd.setAnchorSpec(new StringBuffer(widthSpec).append(" ")
				.append(heightSpec).toString());
		return fd;
	}
}

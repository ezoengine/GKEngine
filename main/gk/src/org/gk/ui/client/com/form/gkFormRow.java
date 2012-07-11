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

import org.gk.ui.client.com.utils.LayoutUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

public class gkFormRow extends LayoutContainer {

	private LabelAlign labelAlign = LabelAlign.LEFT;

	public gkFormRow() {
		setLayout(new ColumnLayout());
	}

	public gkFormRow(String labelAlign) {
		this();
		setLabelAlign(labelAlign);
	}

	@Override
	public boolean add(Widget widget) {
		return add(widget, "", "", "");
	}

	public boolean add(Widget widget, String widthRate, String width,
			String height) {
		boolean result = true;

		String[] rate = splitWidthRate(widthRate);
		FormLayout fl = new FormLayout();
		fl.setLabelAlign(labelAlign);
		fl.setLabelWidth(xferLabelWidth(rate[0]));
		// gxt-all.css中，.x-form-label-right優先於.x-form-label-left，需對left另外處理
		if (widget instanceof Field && labelAlign == LabelAlign.LEFT) {
			setFieldLabelAlign((Field) widget, labelAlign.name().toLowerCase());
		}
		if (widget instanceof ToolBar) {
			result &= add(widget, new ColumnData(xferDataWidth(rate[1])));
		} else {
			LayoutContainer lc = new LayoutContainer(fl);
			result &= lc.add(widget,
					LayoutUtils.createFormData(widget, width, height));
			result &= add(lc, new ColumnData(xferDataWidth(rate[1])));
		}
		return result;
	}

	private void setFieldLabelAlign(Field field, String labelAlign) {
		StringBuffer labelStyle = new StringBuffer(field.getLabelStyle());
		// 先判斷使用者是否已經設定text-align屬性了，有的話就照使用者設定，沒有才需另外處理
		if (labelStyle.indexOf("text-align") == -1) {
			// 如果最後面沒有加上分號，則幫忙加上
			if (labelStyle.length() != 0
					&& !(labelStyle.charAt(labelStyle.length() - 1) == ';')) {
				labelStyle.append(";");
			}
			labelStyle.append("text-align:").append(labelAlign);
			field.setLabelStyle(labelStyle.toString());
		}
	}

	public void setLabelAlign(LabelAlign labelAlign) {
		this.labelAlign = labelAlign;
	}

	/**
	 * 設定每個Row的Label對齊方式
	 * 
	 * @param labelAlign
	 */
	public void setLabelAlign(String labelAlign) {
		if (labelAlign.matches("right|left|top")) {
			this.labelAlign = LabelAlign.valueOf(labelAlign.toUpperCase());
		}
	}

	/**
	 * width字串轉換成 integer
	 * 
	 * @param width
	 * @return int
	 */
	private int xferLabelWidth(String width) {
		int retWidth = 75;
		if (width.matches("^-?\\d+$")) {
			retWidth = Integer.valueOf(width);
		}
		return retWidth;
	}

	/**
	 * width字串轉換成 double
	 * 
	 * @param width
	 * @return double
	 */
	private double xferDataWidth(String width) {
		double retWidth = 0.25;
		if (width.endsWith("%")) {
			width = width.replaceAll("%", "");
			if (width.matches("\\d+")) {
				retWidth = Double.parseDouble(width) / 100;
			}
		} else {
			if (width.matches("(\\d*\\.)?\\d+")) {
				retWidth = Double.parseDouble(width);
			}
		}
		return retWidth;
	}

	/**
	 * 以「:」切割字串，分為labelWidth與columnWidth
	 * 
	 * @param widthRate
	 * @return String[]
	 */
	private String[] splitWidthRate(String widthRate) {
		String[] colon = widthRate.split(":");
		if (colon.length == 1) {
			colon = new String[] { "", colon[0] };
		}
		return colon;
	}
}

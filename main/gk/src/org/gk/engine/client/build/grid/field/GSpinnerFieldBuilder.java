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
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.com.grid.column.gkSpinnerColumnConfig;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.i18n.client.NumberFormat;

public class GSpinnerFieldBuilder extends GridFieldBuilder {

	public GSpinnerFieldBuilder(String spin) {
		super(spin);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkSpinnerColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);
			}

			@Override
			public Field createField() {
				// maxValue、minValue、inc、value、format小數部分的最大長度
				int maxDecimalLength = 0;
				String numType = x.getAttribute("numType", "");
				String inc = x.getAttribute("inc", "");
				String maxValue = x.getAttribute("maxValue", "");
				String minValue = x.getAttribute("minValue", "");
				String value = x.getValue();
				String format = x.getFormat();

				SpinnerField sf = new SpinnerField() {
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

				// 設定number type
				if (!numType.equals("")) {
					sf.getPropertyEditor().setType(getNumberType(numType));
				}
				// 設定增量
				if (inc.matches(IRegExpUtils.FLOAT)) {
					sf.setIncrement(Double.parseDouble(inc));
					maxDecimalLength = getMaxDecLength(inc, maxDecimalLength);
				}
				// 設定最大值
				if (maxValue.matches(IRegExpUtils.FLOAT)) {
					sf.setMaxValue(Double.parseDouble(maxValue));
					maxDecimalLength = getMaxDecLength(maxValue,
							maxDecimalLength);
				}
				// 設定最小值
				if (minValue.matches(IRegExpUtils.FLOAT)) {
					sf.setMinValue(Double.parseDouble(minValue));
					maxDecimalLength = getMaxDecLength(minValue,
							maxDecimalLength);
				}
				// 設定初始值
				if (value.matches(IRegExpUtils.FLOAT)) {
					sf.setValue(Double.parseDouble(value));
					maxDecimalLength = getMaxDecLength(value, maxDecimalLength);
				}
				// 設定format
				if (!format.equals("")) {
					sf.getPropertyEditor().setFormat(
							NumberFormat.getFormat(format));
				} else {
					String zeroStr = ((long) Math.pow(10, maxDecimalLength) + "")
							.replaceAll("1", "");
					format = "##0" + (zeroStr.equals("") ? "" : "." + zeroStr);
					sf.getPropertyEditor().setFormat(
							NumberFormat.getFormat(format));
				}
				return sf;
			}
		};
		return cc;
	}

	/**
	 * 取得傳入value的小數位數
	 * 
	 * @param value
	 * @param maxDecimalLength
	 * @return int
	 */
	private int getMaxDecLength(String value, int maxDecimalLength) {
		maxDecimalLength = Math.max(
				maxDecimalLength,
				value.indexOf(".") < 0 ? 0 : value.substring(
						value.indexOf(".") + 1).length());
		return maxDecimalLength;
	}

	/**
	 * 取得NumberType， 支援類型：Short、Integer、Long、Float、Double(預設)
	 * 
	 * @param numType
	 * @return Class
	 */
	private Class getNumberType(String numType) {
		Class numberType;
		if (numType.equalsIgnoreCase("Short")) {
			numberType = Short.class;
		} else if (numType.equalsIgnoreCase("Integer")) {
			numberType = Integer.class;
		} else if (numType.equalsIgnoreCase("Long")) {
			numberType = Long.class;
		} else if (numType.equalsIgnoreCase("Float")) {
			numberType = Float.class;
		} else {
			numberType = Double.class;
		}
		return numberType;
	}
}

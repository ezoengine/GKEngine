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

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.menu.DateMenu;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;

/**
 * 中冠公用元件dejgDateField, 繼承原有GXT DateField功能 提供AP設定畫面挑選元件要為西元年或是民國年格式,
 * 並根據格式驗證輸入內容
 * 
 * @author I23979 - 林明儀
 * @since 2009/11/04
 */
public class gkDateField extends DateField {
	protected String[] dateFormat;
	private Date minValue;
	private Date maxValue;
	private DateMenu menu;
	private boolean formatValue;
	private Date inputDate;
	protected String format;

	public gkDateField() {
		this("yyyy/mm/dd");
	}

	public gkDateField(String format) {
		assert format != null : "format should not be null";
		// 如果format为空使用默认格式"yyyy/mm/dd"，否则使用指定年月格式
		if (format
				.matches("yyyy/mm/dd|yyy/mm/dd|dd/mm/yyyy|dd/mm/yyy|mm/dd/yyyy|mm/dd/yyy")) {
			this.setFormat(format);
		} else {
			this.setFormat("yyyy/mm/dd");
		}

		propertyEditor = new DateTimePropertyEditor() {
			public String getStringValue(Date value) {
				return getShowDate((Date) value);
			}

			public Date convertStringValue(String value) {
				return convertValue(value);
			}
		};
	}

	@Override
	public Date getMinValue() {
		return minValue;
	}

	@Override
	public void setMinValue(Date minValue) {
		this.minValue = minValue;
	}

	@Override
	public Date getMaxValue() {
		return maxValue;
	}

	@Override
	public void setMaxValue(Date maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public boolean isFormatValue() {
		return formatValue;
	}

	@Override
	public void setFormatValue(boolean formatValue) {
		this.formatValue = formatValue;
	}

	@Override
	protected void expand() {
		DatePicker picker = getDatePicker();
		Object v = null;
		// 將畫面已經填入的日期連動到DataPicker
		if (dateFormat != null && !getRawValue().equals("")) {
			String[] showDate = getRawValue().split("/");

			String year = "";
			String month = "";
			String day = "";

			if (getDatePicker().getDateType() == DatePicker.CHINESE_YEAR) {
				year = convertYearC(showDate);
			} else {
				year = convertYearW(showDate);
			}

			month = convertMonth(showDate);
			day = convertDay(showDate);
			if (!month.equals("error") && !day.equals("error")
					&& !year.equals("error")) {
				v = DateTimeFormat.getFormat("yyyy-MM-dd").parse(
						year + "-" + month + "-" + day);
			}
		} else {
			v = getValue();
		}
		Date d = null;
		if (v instanceof Date) {
			d = (Date) v;
		} else {
			d = new Date();
		}
		picker.setValue(d, true);
		picker.setMinDate(minValue);
		picker.setMaxDate(maxValue);

		// handle case when down arrow is opening menu
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				menu.show(el().dom, "tl-bl?");
				menu.getDatePicker().focus();
			}
		});
	}

	@Override
	public DatePicker getDatePicker() {
		if (menu == null) {
			menu = new DateMenu();

			menu.getDatePicker().addListener(Events.Select,
					new Listener<ComponentEvent>() {
						@Override
						public void handleEvent(ComponentEvent ce) {
							focusValue = getValue();
							setValue(menu.getDate());
							menu.hide();
						}
					});
			menu.addListener(Events.Hide, new Listener<ComponentEvent>() {
				@Override
				public void handleEvent(ComponentEvent be) {
					focus();
				}
			});
		}
		return menu.getDatePicker();
	}

	@Override
	protected boolean validateBlur(DomEvent e, Element target) {
		return menu == null || (menu != null && !menu.isVisible());
	}

	@Override
	protected boolean validateValue(String inputDate) {
		if (inputDate.length() < 1 || inputDate.equals("")) {
			if (getAllowBlank()) {
				clearInvalid();
				return true;
			} else {
				markInvalid(getMessages().getBlankText());
				return false;
			}
		} else {
			clearInvalid();
		}

		// 宣告一個變數供檢驗method檢查
		String value = "";
		if (dateFormat == null) {
			// 將外部傳進來date設定為原本父類別做檢查用變數value
			value = inputDate;
		} else {
			value = getDate(inputDate);
		}

		DateTimeFormat format = DateTimeFormat.getFormat(this.format);
		Date date = null;
		String[] dates = value.split("/");
		if (dates.length == 3) {
			try {
				date = DateTimeFormat.getFormat("yyyy-MM-dd").parse(
						dates[2] + "-" + dates[0] + "-" + dates[1]);
			} catch (Exception E) {
				date = null;
			}
		}

		if (date == null) {
			String error = null;
			// if (getMessages().getInvalidText() != null) {
			// error = Format.substitute(getMessages().getInvalidText(), 0);
			// } else {
			error = GXT.MESSAGES.dateField_invalidText(inputDate, format
					.getPattern().toUpperCase());
			// }
			markInvalid(error);
			return false;
		}

		if (minValue != null && date.before(minValue)) {
			String error = null;
			if (getMessages().getMinText() != null) {
				error = Format.substitute(getMessages().getMinText(),
						format.format(minValue));
			} else {
				error = GXT.MESSAGES.dateField_minText(format.format(minValue));
			}
			markInvalid(error);
			return false;
		}
		if (maxValue != null && date.after(maxValue)) {
			String error = null;
			if (getMessages().getMaxText() != null) {
				error = Format.substitute(getMessages().getMaxText(),
						format.format(maxValue));
			} else {
				error = GXT.MESSAGES.dateField_maxText(format.format(maxValue));
			}
			markInvalid(error);
			return false;
		}

		if (formatValue && getPropertyEditor().getFormat() != null) {
			setRawValue(getPropertyEditor().getFormat().format(date));
		}

		if (validator != null) {
			String msg = validator.validate(this, value);
			if (msg != null) {
				markInvalid(msg);
				return false;
			}
		}

		return true;
	}

	/**
	 * 設定日期顯示格式
	 * 
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format.toLowerCase();
		dateFormat = format.split("/");
		// 判斷輸入的日期格式
		for (int i = 0; i < dateFormat.length; i++) {
			if (dateFormat[i].trim().toUpperCase().equals("YYY")) {
				getDatePicker().setDateType(DatePicker.CHINESE_YEAR);
			}
		}
	}

	/**
	 * 將外部傳入日期轉為驗證method所接受的日期格式
	 * 
	 * @param inputDate
	 * @return String
	 */
	protected String getDate(String inputDate) {
		String year = "";
		String month = "";
		String day = "";

		// 將畫面輸入日期切成字串陣列
		String[] tempDate = inputDate.split("/");

		if (tempDate.length != dateFormat.length) {
			return "error";
		} else {
			// 判斷目前日期顯示格式為民國年或是西元
			if (getDatePicker().getDateType() == DatePicker.CHINESE_YEAR) {
				year = convertYearC(tempDate);
			} else {
				year = convertYearW(tempDate);
			}

			month = convertMonth(tempDate);
			day = convertDay(tempDate);

			return month + "/" + day + "/" + year;
		}
	}

	/**
	 * cyear轉換
	 * 
	 * @param tempDate
	 * @return
	 */
	protected String convertYearC(String[] tempDate) {
		String year = "error";
		for (int i = 0; i < dateFormat.length; i++) {
			if (dateFormat[i].trim().toUpperCase().equals("YYY")
					&& isNumber(tempDate[i])) {
				year = tempDate[i].trim().length() != 3 ? "error" : Integer
						.parseInt(tempDate[i].trim()) + 1911 + "";
			}
		}

		return year;
	}

	/**
	 * wyear轉換
	 * 
	 * @param tempDate
	 * @return
	 */
	protected String convertYearW(String[] tempDate) {
		String year = "error";

		for (int i = 0; i < dateFormat.length; i++) {
			if (dateFormat[i].trim().toUpperCase().equals("YYYY")
					&& isNumber(tempDate[i])) {

				year = tempDate[i].trim().length() != 4 ? "error" : tempDate[i]
						.trim();
			}
		}

		return year;
	}

	/**
	 * month轉換
	 * 
	 * @param tempDate
	 * @return
	 */
	protected String convertMonth(String[] tempDate) {
		String month = "error";

		for (int i = 0; i < dateFormat.length; i++) {
			if (dateFormat[i].trim().toUpperCase().equals("MM")) {
				// 判斷未輸入月份
				if (tempDate.length < i + 1)
					return "error";
				if (tempDate[i].trim().equals("")
						|| tempDate[i].trim().length() == 0
						|| tempDate[i].trim().length() > 2
						|| !isNumber(tempDate[i].trim().toString())
						|| Integer.parseInt(tempDate[i].trim().toString()) > 12
						|| Integer.parseInt(tempDate[i].trim().toString()) < 1) {
					month = "error";
				} else {
					month = tempDate[i].trim().length() != 2 ? "0"
							+ tempDate[i].trim() : tempDate[i].trim();
				}
			}
		}

		return month;
	}

	/**
	 * day轉換
	 * 
	 * @param tempDate
	 * @return
	 */
	protected String convertDay(String[] tempDate) {
		String day = "";

		for (int i = 0; i < dateFormat.length; i++) {
			if (dateFormat[i].trim().toUpperCase().equals("DD")) {
				// 判斷未輸入星期
				if (tempDate.length < i + 1)
					return "error";
				if (tempDate[i].trim().equals("")
						|| tempDate[i].trim().length() == 0
						|| !isNumber(tempDate[i].trim().toString())
						|| tempDate[i].trim().length() > 2
						|| Integer.parseInt(tempDate[i].trim().toString()) > 31
						|| Integer.parseInt(tempDate[i].trim().toString()) < 1) {
					day = "error";
				} else {
					day = tempDate[i].trim().length() != 2 ? "0"
							+ tempDate[i].trim() : tempDate[i].trim();
				}
			}
		}

		return day;
	}

	/**
	 * 從DatePicker挑選完後傳回畫面上的日期
	 * 
	 * @param rawDate
	 * @return
	 */
	public String getShowDate(Date rawDate) {
		int num = dateFormat == null ? 0 : dateFormat.length;
		String[] content = new String[num];

		String showDate = "";
		String shortDate = DateTimeFormat.getFormat(
				this.format.replace("mm", "MM")).format(rawDate);

		String[] tempDate = new String[3];

		tempDate = shortDate.split("/");

		String year = "";
		String month = "";
		String day = "";
		for (int i = 0; i < dateFormat.length; i++) {
			if (dateFormat[i].trim().toUpperCase().startsWith("YYY")) {
				year = tempDate[i];
			}
		}
		month = convertMonth(tempDate);
		day = convertDay(tempDate);
		// 組畫面上顯示字串
		for (int i = 0; i < num; i++) {
			if (getDatePicker().getDateType() == DatePicker.CHINESE_YEAR) {
				if (dateFormat[i].trim().toUpperCase().equals("YYY")) {
					content[i] = (Integer.parseInt(year) - 1911) < 100 ? "0"
							+ (Integer.parseInt(year) - 1911) : ""
							+ (Integer.parseInt(year) - 1911);
				} else if (dateFormat[i].trim().toUpperCase().equals("MM")) {
					content[i] = month.length() < 2 ? "0" + month : month;
				} else {
					content[i] = day.length() < 2 ? "0" + day : day;
				}
			} else {
				if (dateFormat[i].trim().toUpperCase().equals("YYYY")) {
					content[i] = year;
				} else if (dateFormat[i].trim().toUpperCase().equals("MM")) {
					content[i] = month.length() < 2 ? "0" + month : month;
				} else {
					content[i] = day.length() < 2 ? "0" + day : day;
				}
			}
		}

		// 判斷如果
		if (num == 0) {
			// 組出完整字串
			String fullMonth = month.length() < 2 ? "0" + month : month;
			String fullDay = day.length() < 2 ? "0" + day : day;

			showDate = fullMonth + "/" + fullDay + "/" + year;
		} else {
			// 組出完整字串
			for (int i = 0; i < num; i++) {
				if (i == 0) {
					showDate = content[i];
				} else {
					showDate = showDate + "/" + content[i];
				}
			}
		}

		return showDate;
	}

	/**
	 * 提供ap使用抓取現在畫面上所填欄位, 並轉成yyyymmdd
	 * 
	 * @return 取得yyyymmdd格式字串
	 */
	public String getUseDate() {
		if (dateFormat != null && !getRawValue().equals("")) {
			String[] showDate = getRawValue().split("/");
			showDate = fixGetUseDate(showDate);

			String year = "";
			String month = "";
			String day = "";

			if (getDatePicker().getDateType() == DatePicker.CHINESE_YEAR) {
				year = convertYearC(showDate);
			} else {
				year = convertYearW(showDate);
			}

			month = convertMonth(showDate);
			day = convertDay(showDate);
			if (showDate.length > 3) {
				return year + month + day + "error";
			}

			return year + month + day;
		} else {
			Date useDate = getValue();
			if (useDate == null) {
				return "";
			}
			String d = DateTimeFormat.getFormat("yyyy-MM-dd").format(useDate);
			String[] dArr = d.split("-");
			return dArr[0] + dArr[1] + dArr[2];
		}
	}

	protected String[] fixGetUseDate(String[] showDate) {
		try {
			if (showDate.length == 1) {
				String contentStr = showDate[0];
				int index = 0;
				String[] fixShowDate = new String[dateFormat.length];
				for (int i = 0; i < dateFormat.length; i++) {
					int end = dateFormat[i].trim().length() + index;
					fixShowDate[i] = contentStr.substring(index, end);
					index = end;
				}
				for (int i = 0; i < fixShowDate.length; i++) {
					if (fixShowDate[i] == null) {
						return showDate;
					}
				}
				return fixShowDate;
			}
			return showDate;
		} catch (Exception e) {
			return showDate;
		}
	}

	/**
	 * <pre>
	 * 傳的日期字串可使用下列格式 
	 * yyyy/mm/dd|yyy/mm/dd|dd/mm/yyyy|dd/mm/yyy|mm/dd/yyyy|mm/dd/yyy
	 * && 2種特殊數據格式yyymmdd;yyyymmdd
	 * @param useDate
	 * </pre>
	 */
	public void setUseDate(String userDate) {
		Date inputDate = convertValue(userDate);
		setValue(inputDate);
	}

	protected Date convertValue(String userDate) {
		Date returnDate = null;
		if (userDate == null || userDate.length() < 7) {
			return returnDate;
		}
		String year = "";
		String month = "";
		String day = "";
		String transDate = "";
		String formatUpCase = this.format.toUpperCase();
		// 2種特殊數據格式yyymmdd;yyyymmdd需要處理 此2種格式的format必須為yyy/mm/dd;yyyy/mm/dd
		if (userDate.indexOf("/") == -1) {
			switch (userDate.length()) {
			case 7: // yyymmdd
				year = userDate.substring(0, 3);
				month = userDate.substring(3, 5);
				day = userDate.substring(5, 7);
				if (formatUpCase.indexOf("YYYY") == -1) {
					transDate = formatUpCase.replaceFirst("YYY", year);
				} else {
					year = Integer.toString(1911 + Integer.parseInt(year));
					transDate = formatUpCase.replaceFirst("YYYY", year);
				}
				break;
			case 8: // yyyymmdd
				year = userDate.substring(0, 4);
				month = userDate.substring(4, 6);
				day = userDate.substring(6, 8);
				if (formatUpCase.indexOf("YYYY") == -1) {
					year = (Integer.parseInt(year) - 1911) < 100 ? "0"
							+ (Integer.parseInt(year) - 1911) : ""
							+ (Integer.parseInt(year) - 1911);
					transDate = formatUpCase.replaceFirst("YYY", year);
				} else {
					transDate = formatUpCase.replaceFirst("YYYY", year);
				}

				break;
			}
			transDate = transDate.replaceFirst("MM", month);
			transDate = transDate.replaceFirst("DD", day);
			userDate = transDate;
		}
		String[] tempDate = new String[3];
		if (validateValue(userDate)) {
			// 將畫面輸入日期切成字串陣列
			tempDate = userDate.split("/");

			if (tempDate.length == dateFormat.length) {
				// 判斷目前日期顯示格式為民國年或是西元
				if (getDatePicker().getDateType() == DatePicker.CHINESE_YEAR) {
					year = convertYearC(tempDate);
				} else {
					year = convertYearW(tempDate);
				}

				month = convertMonth(tempDate);
				day = convertDay(tempDate);
				returnDate = DateTimeFormat.getFormat("yyyy-MM-dd").parse(
						year + "-" + month + "-" + day);
			}
		}
		return returnDate;
	}

	@Override
	public void setRawValue(String value) {
		if (value.split("/").length == 3) {
			String showDate = this.getShowDate(inputDate);
			super.setRawValue(showDate);
		} else {
			super.setRawValue(value);
		}
	}

	public Date getInputDate() {
		return inputDate;
	}

	@Override
	public void setValue(Date value) {
		inputDate = value;
		super.setValue(value);
	}

	@Override
	public void focus() {
		if (rendered) {
			getFocusEl().focus();
			onFocus(new FieldEvent(this));
		}
	}

	private boolean isNumber(String num) {

		try {
			Integer.parseInt(num);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}

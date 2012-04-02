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

import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;

/**
 * 中冠公用元件dejgYMField, 繼承原有dejgDateField功能 提供AP設定畫面挑選元件要為西元年或是民國年格式以及年、月曆挑選元件,
 * 並根據格式驗證輸入內容
 * 
 * @author I23979-林明儀, I24580-張明龍
 * @since 2009/11/13
 */
public class gkYMField extends gkDateField {

	// 畫面彈跳出來的window
	private Window w = new Window();

	// ympicker
	public gkYMPicker ympicker;

	public gkYMField() {
		this("yyyy/mm");
	}

	public gkYMField(String format) {
		assert format != null : "format should not be null";
		// 如果為空使用默認格式"yyyy/mm"，否则使用指定年月格式
		if (format.matches("yyyy/mm|mm/yyyy|yyy/mm|mm/yyy|yyyy|yyy")) {
			setFormat(format);
		} else {
			setFormat("yyyy/mm");
		}
		setTriggerStyle("x-form-date-trigger");
	}

	/**
	 * 設定日期顯示格式
	 * 
	 * @param format
	 */
	@Override
	public void setFormat(String format) {
		super.setFormat(format);
		getYMPicker().setDateType(getDatePicker().getDateType());
	}

	public Window getYMWindow() {
		return w;
	}

	public gkYMPicker getYMPicker() {
		if (ympicker == null) {
			ympicker = new gkYMPicker(this);
		}

		return ympicker;
	}

	public void select(String value) {
		// 判斷是取消事件或是點選事件
		if (value.equals("CANCEL")) {

		} else {
			// 根據畫面輸入格式轉成相同的格式呈現
			String[] selDate = value.split("/");
			// tempDate = value;
			Date setDate = new Date();
			setDate.setYear(Integer.parseInt(selDate[0]) - 1900);

			if (selDate.length == 2) {
				setDate.setDate(1);
				setDate.setMonth(Integer.parseInt(selDate[1]) - 1);
			}
			setValue(setDate);
			focus();
		}
		// 此事件一律關閉視窗
		w.close();
	}

	@Override
	protected void expand() {
		// 先移除window中所有元件
		w.removeAll();
		// 設定自動隱藏
		w.setAutoHide(true);
		String nowYear = "";
		String nowMonth = "";

		// 一定會有dateFormat, 因為我們預設格式為yyyy/mm, 所以只要判斷畫面上有沒有填入值
		if (!getRawValue().equals("")) {
			String[] tempDate = getRawValue().split("/");

			if (getYMPicker().getDateType() == gkYMPicker.CHINESE_YEAR) {
				nowYear = convertYearC(tempDate);
			} else {
				nowYear = convertYearW(tempDate);
			}
			if (convertMonth(tempDate).equals("")) {
				nowMonth = "";
			} else {
				if (convertMonth(tempDate).equals("error")) {
					nowMonth = "error";
				} else {
					nowMonth = (Integer.parseInt(convertMonth(tempDate)) - 1)
							+ "";
				}
			}
		}

		getYMPicker().setFocusYear(nowYear);
		getYMPicker().setFocusMonth(nowMonth);

		// 設定年曆或是月曆
		setCal();
		// 更新日曆內容
		getYMPicker().updateContent();
		// 為了避免大小跑掉, 所以設定固定大小
		w.setWidth(200);
		// 設定開啟的位置
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				w.setVisible(true);
				w.el().alignTo(el().dom, "tl-bl?", new int[] { 0, 0 });
				w.focus();
			}
		});
		// 加到視窗中
		w.add(ympicker);
		w.layout();
	}

	// 判斷是年曆或月曆(預設為月曆)
	private void setCal() {
		if (dateFormat.length == 1) {
			getYMPicker().setCalendarType(gkYMPicker.CALENDAR_Y);
		}
	}

	/**
	 * 將外部傳入日期轉為驗證method所接受的日期格式
	 * 
	 * @param inputDate
	 * @return String
	 */
	@Override
	protected String getDate(String inputDate) {
		String year = "";
		String month = "01";
		String day = "01";

		// 將畫面輸入日期切成字串陣列
		String[] tempDate = inputDate.split("/");

		if (tempDate.length != dateFormat.length) {
			return "error";
		} else {
			// 判斷現在是要輸入年曆或是月曆
			if (tempDate.length == 1) {
				// 判斷民國年或是西元年
				if (tempDate[0].length() == 3) {
					year = convertYearC(tempDate);
				} else {
					year = convertYearW(tempDate);
				}
			} else {
				for (int i = 0; i < dateFormat.length; i++) {
					if (dateFormat[i].toUpperCase().equals("YYY")) {
						year = convertYearC(tempDate);
					} else if (dateFormat[i].toUpperCase().equals("YYYY")) {
						year = convertYearW(tempDate);
					} else {
						month = convertMonth(tempDate);
					}
				}
			}
			return month + "/" + day + "/" + year;
		}
	}

	/**
	 * 提供AP操作取得畫面上所填入欄位
	 * 
	 * @return String
	 */
	@Override
	public String getUseDate() {
		if (dateFormat != null && !getRawValue().equals("")) {
			String originalDate = "";
			String year = "";
			String month = "";

			originalDate = getRawValue();

			// 將畫面輸入日期切成字串陣列
			String[] tempDate = originalDate.split("/");
			tempDate = fixGetUseDate(tempDate);

			if (tempDate.length != dateFormat.length) {
				return "error";
			} else {
				// 判斷現在是要輸入年曆或是月曆
				if (tempDate.length == 1) {
					// 判斷民國年或是西元年
					if (tempDate[0].length() == 3) {
						year = convertYearC(tempDate);
					} else {
						year = convertYearW(tempDate);
					}
				} else {
					if (getYMPicker().getDateType() == gkYMPicker.YEAR) {
						year = convertYearW(tempDate);
					} else {
						year = convertYearC(tempDate);
					}
					month = convertMonth(tempDate);
				}
			}
			return year + month;

		} else {
			Date useDate = getValue();
			if (useDate == null) {
				return "";
			}

			String d = DateTimeFormat.getFormat("yyyy/MM/dd").format(useDate);

			String[] tempDate = d.split("/");
			return tempDate[0] + tempDate[1];
		}
	}

	@Override
	protected Date convertValue(String userDate) {
		Date returnDate = null;
		if (userDate == null || userDate.equals("error")) {
			return returnDate;
		}
		String year = "";
		String month = "";
		String transDate = "";
		String formatUpCase = this.format.toUpperCase();
		// 2種特殊數據格式yyymm;yyyymm需要處理 此2種格式的format必須為yyy/mm;yyyy/mm
		if (userDate.indexOf("/") == -1) {
			switch (userDate.length()) {
			case 5: // yyymm
				year = userDate.substring(0, 3);
				month = userDate.substring(3, 5);
				if (formatUpCase.indexOf("YYYY") == -1) {
					transDate = formatUpCase.replaceFirst("YYY", year);
				} else {
					year = Integer.toString(1911 + Integer.parseInt(year));
					transDate = formatUpCase.replaceFirst("YYYY", year);
				}
				break;
			case 6: // yyyymm
				year = userDate.substring(0, 4);
				month = userDate.substring(4, 6);
				if (formatUpCase.indexOf("YYYY") == -1) {
					year = (Integer.parseInt(year) - 1911) < 100 ? "0"
							+ (Integer.parseInt(year) - 1911) : ""
							+ (Integer.parseInt(year) - 1911);
					transDate = formatUpCase.replaceFirst("YYY", year);
				} else {
					transDate = formatUpCase.replaceFirst("YYYY", year);
				}
				break;
			default:
				transDate = userDate;
			}
			transDate = transDate.replaceFirst("MM", month);
			userDate = transDate;
		}
		String[] arrDate = new String[3];
		if (validateValue(userDate)) {
			Date rawDate = new Date();
			// 將畫面輸入日期切成字串陣列
			arrDate = userDate.split("/");
			if (arrDate.length == dateFormat.length) {
				// 判斷目前日期顯示格式為民國年或是西元
				if (getYMPicker().getDateType() == gkYMPicker.CHINESE_YEAR) {
					year = convertYearC(arrDate);
				} else {
					year = convertYearW(arrDate);
				}

				month = convertMonth(arrDate).equals("error") ? (rawDate
						.getMonth() + 1) + "" : convertMonth(arrDate);
				returnDate = DateTimeFormat.getFormat("yyyy-MM-dd").parse(
						year + "-" + month + "-" + rawDate.getDay() + 1);
			}
		}
		return returnDate;
	}

	@Override
	protected boolean validateBlur(DomEvent e, Element target) {
		return w == null || (w != null && !w.isVisible());
	}

	@Override
	public void focus() {
		if (rendered) {
			getFocusEl().focus();
			onFocus(new FieldEvent(this));
		}
	}
}

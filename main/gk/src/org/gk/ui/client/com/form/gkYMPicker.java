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

import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.core.CompositeElement;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.messages.XConstants;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * 年月挑選元件，提供設定畫面挑選元件要為西元年或是民國年格式
 * 
 * @author I23979,I24589,I23250
 * @since 2009/11/13
 */
public class gkYMPicker extends LayoutContainer {

	private ContentPanel cp = new ContentPanel();
	private gkYMField field;

	private El calendarEl;
	private CompositeElement mpMonths, mpYears;
	private Html picker = new Html();

	private String[] showYears = new String[10];
	// 準備顯示在畫面上的預設年
	private String focusYear;
	// 準備顯示在畫面上的預設月
	private String focusMonth;

	// 年曆或是月曆
	private boolean yearPicker;
	// 民國或西元格式
	private boolean chineseYear;

	/**
	 * 是否為年曆，預設為否
	 * 
	 * @return boolean
	 */
	public boolean isYearPicker() {
		return yearPicker;
	}

	public void setYearPicker(boolean yearPicker) {
		this.yearPicker = yearPicker;
	}

	/**
	 * 是否為民國年，預設為否
	 * 
	 * @return boolean
	 */
	public boolean isChineseYear() {
		return chineseYear;
	}

	public void setChineseYear(boolean chineseYear) {
		this.chineseYear = chineseYear;
	}

	public gkYMPicker(gkYMField field) {
		cp.add(picker);
		add(cp);
		this.field = field;
	}

	public void updateContent() {
		// 以focusYear為中心，產生年份清單
		showYears = getYears(focusYear);

		if (isChineseYear()) {
			cp.setHeading(Msg.get.chineseYear());
			changeYears();
			// 改變成民國年，則focusYear也需要是民國年，所以取showYears的資料為值
			focusYear = showYears[4];
		} else {
			cp.setHeading(Msg.get.yearTitle());
		}

		createView();
		cp.setAutoWidth(true);
	}

	public void setValue(Date date) {
		DateWrapper dw = new DateWrapper(date);
		focusYear = String.valueOf(dw.getFullYear());
		focusMonth = String.valueOf(dw.getMonth());
	}

	public Date wrapperDate() {
		int year = Integer.parseInt(focusYear);
		int month = Integer.parseInt(focusMonth);
		if (isChineseYear()) {
			year = year + 1911;
		}
		return new DateWrapper(year, month, 1).asDate();
	}

	protected void createView() {
		String html = "";
		if (isYearPicker()) {
			html = getYHtml();
		} else {
			html = getYMHtml();
		}
		focusMonth = focusMonth != null ? focusMonth : ""
				+ new DateWrapper().getMonth();
		focusYear = focusYear != null ? focusYear : ""
				+ new DateWrapper().getFullYear();

		calendarEl = new El(html);

		picker.setHtml(calendarEl.dom.getString());

		if (picker.isRendered()) {
			mpYears = new CompositeElement(Util.toElementArray(new El(picker
					.getElement()).select("td.x-date-mp-year")));
			updateMPYear();
			if (isYearPicker()) {
				return;
			}
			mpMonths = new CompositeElement(Util.toElementArray(new El(picker
					.getElement()).select("td.x-date-mp-month")));
			updateMPMonth();
		} else {
			picker.addListener(Events.Render, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					mpYears = new CompositeElement(Util.toElementArray(new El(
							picker.getElement()).select("td.x-date-mp-year")));
					updateMPYear();
					if (isYearPicker()) {
						return;
					}
					mpMonths = new CompositeElement(Util.toElementArray(new El(
							picker.getElement()).select("td.x-date-mp-month")));
					updateMPMonth();
				}
			});
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		el().addEventsSunk(Event.ONCLICK | Event.ONDBLCLICK | Event.MOUSEEVENTS);
	}

	@Override
	public void onComponentEvent(ComponentEvent ce) {
		super.onComponentEvent(ce);

		int type = ce.getEventTypeInt();
		switch (type) {
		case Event.ONCLICK:
			onClick(ce);
			break;
		case Event.ONDBLCLICK:
			onDoubleClick(ce);
			break;
		case Event.ONMOUSEOVER:
			break;
		case Event.ONMOUSEOUT:
			break;
		}
	}

	protected void onClick(ComponentEvent be) {
		be.preventDefault();
		El target = be.getTargetEl();
		El pn = null;

		if (target.is("a.x-date-mp-next")) {
			// 按下下10年的動作
			setNextYear();
			focusYear = showYears[4];
			updateMPYear();
		} else if (target.is("a.x-date-mp-prev")) {
			// 按下上10年的動作
			setPrevYear();
			focusYear = showYears[4];
			updateMPYear();
		}

		if ((pn = target.findParent("td.x-date-mp-month", 2)) != null) {
			mpMonths.removeStyleName("x-date-mp-sel");
			focusMonth = pn.dom.getPropertyString("xmonth");
			pn.addStyleName("x-date-mp-sel");
		} else if ((pn = target.findParent("td.x-date-mp-year", 2)) != null) {
			mpYears.removeStyleName("x-date-mp-sel");
			focusYear = pn.dom.getPropertyString("xyear");
			pn.addStyleName("x-date-mp-sel");
		} else if ((target.is("button.icsc-date-mp-YMOK"))) {
			// 監聽點選今年 or 本月
			field.select(new Date());
		} else if ((target.is("button.x-date-mp-ok"))) {
			field.select(wrapperDate());
		} else if ((target.is("button.x-date-mp-cancel"))) {
			field.select(null);
		}
	}

	protected void onDoubleClick(ComponentEvent be) {
		be.preventDefault();
		El target = be.getTargetEl();

		if ((target.findParent("td.x-date-mp-month", 2)) != null) {
			field.select(wrapperDate());
		} else if ((target.findParent("td.x-date-mp-year", 2)) != null) {
			field.select(wrapperDate());
		}
	}

	/**
	 * 取得年月曆
	 * 
	 * @return String
	 */
	private String getYMHtml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<table border=0 cellspacing=0 width='100%'>");
		String[] monthNames = XConstants.constants.shortMonths();
		for (int i = 0; i < 6; i++) {
			if (i == 0) {
				buf.append("<tr><td class=x-date-mp-ybtn align=center title='"
						+ Msg.get.preDecade()
						+ "'><a class='x-date-mp-prev'  href=#></a></td>");
				buf.append("<td class='x-date-mp-ybtn  x-date-mp-sep' align=center title='"
						+ Msg.get.nextDecade()
						+ "'><a class='x-date-mp-next' href=#></a></td>");
			} else {
				buf.append("<tr><td class='x-date-mp-year'><a href='#'></a></td>");
				buf.append("<td class='x-date-mp-year x-date-mp-sep'><a href='#'></a></td>");
			}
			buf.append("<td class=x-date-mp-month><a href='#'>" + monthNames[i]
					+ "</a></td>");
			buf.append("<td class=x-date-mp-month><a href='#'>"
					+ monthNames[i + 6] + "</a></td></tr>");
		}

		buf.append("<tr class=x-date-mp-btns><td colspan='4'>");
		buf.append("<button type='button' class='x-date-mp-ok'>" + Msg.get.ok()
				+ "</button>");
		buf.append("<button type='button' class='icsc-date-mp-YMOK'>"
				+ Msg.get.thisMonth() + "</button>");
		buf.append("</td></tr></table>");

		return buf.toString();
	}

	/**
	 * 取得年曆
	 * 
	 * @return String
	 */
	private String getYHtml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<table border=0 cellspacing=0 width='100%'>");
		for (int i = 0; i < 6; i++) {
			if (i == 0) {
				buf.append("<tr><td class=x-date-mp-ybtn align=center title='"
						+ Msg.get.preDecade()
						+ "'><a class='x-date-mp-prev'  href=#></a></td>");
				buf.append("<td class='x-date-mp-ybtn' align=center title='"
						+ Msg.get.nextDecade()
						+ "'><a class='x-date-mp-next' href=#></a></td>");
			} else {
				buf.append("<tr><td class='x-date-mp-year'><a href='#'></a></td>");
				buf.append("<td class='x-date-mp-year'><a href='#'></a></td>");
			}
		}

		buf.append("<tr class=x-date-mp-btns><td colspan='2'>");
		buf.append("<button type='button' class='x-date-mp-ok'>" + Msg.get.ok()
				+ "</button>");
		buf.append("<button type='button' class='icsc-date-mp-YMOK'>"
				+ Msg.get.thisYear() + "</button>");
		buf.append("</td></tr></table>");

		return buf.toString();
	}

	/**
	 * 設定前10年
	 */
	private void setPrevYear() {
		for (int i = 0; i < showYears.length; i++) {
			if (isChineseYear()) {
				int year = Integer.parseInt(showYears[i]) - 10;
				if (year < 1 || year >= 100) {
					showYears[i] = year + "";
				} else if (year < 10) {
					showYears[i] = "00" + year;
				} else if (year < 100) {
					showYears[i] = "0" + year;
				}
			} else {
				showYears[i] = Integer.parseInt(showYears[i]) - 10 + "";
			}
		}
	}

	/**
	 * 設定後10年
	 */
	private void setNextYear() {
		for (int i = 0; i < showYears.length; i++) {
			if (isChineseYear()) {
				int year = Integer.parseInt(showYears[i]) + 10;
				if (year < 1 || year >= 100) {
					showYears[i] = year + "";
				} else if (year < 10) {
					showYears[i] = "00" + year;
				} else if (year < 100) {
					showYears[i] = "0" + year;
				}
			} else {
				showYears[i] = Integer.parseInt(showYears[i]) + 10 + "";
			}
		}
	}

	/**
	 * 以傳入年份為中心，產生年份清單
	 * 
	 * @param nowYear
	 * @return String[]
	 */
	private String[] getYears(String nowYear) {
		String[] years = new String[10];
		int year = 0;
		// 取出來的年份需要加上1900才會是目前的西元年份
		if (nowYear.equals("")) {
			year = new DateWrapper().getFullYear();
		} else {
			year = Integer.parseInt(nowYear);
		}
		for (int i = 0; i < years.length; i++) {
			if (i < 4) {
				years[i] = year - (4 - i) + "";
			} else if (i == 4) {
				years[i] = year + "";
			} else {
				years[i] = year + (i - 4) + "";
			}
		}
		return years;
	}

	/**
	 * 改變年份字串陣列內容為民國年選項
	 */
	private void changeYears() {
		for (int i = 0; i < showYears.length; i++) {
			// 點選圖示後即會更新內容，判斷如果已經計算過就不再計算民國年
			String year;
			if (Integer.parseInt(showYears[i]) - 1911 < 0) {
				year = Integer.parseInt(showYears[i]) + "";
			} else {
				year = (Integer.parseInt(showYears[i]) - 1911) + "";
			}

			if (Integer.parseInt(year) > 0) {
				if (year.length() == 1) {
					showYears[i] = "00" + year;
				} else if (year.length() == 2) {
					showYears[i] = "0" + year;
				} else {
					showYears[i] = year + "";
				}
			} else {
				showYears[i] = year;
			}
		}
	}

	private void updateMPYear() {
		for (int i = 0; i < 10; i++) {
			El td = new El(mpYears.item(i));
			String year;
			if (i % 2 == 0) {
				year = showYears[i / 2];
			} else {
				year = showYears[((i - 1) / 2) + 5];
			}
			td.firstChild().update(year);
			td.dom.setPropertyString("xyear", year);
			td.setStyleName("x-date-mp-sel", year.equals(focusYear));
		}
	}

	private void updateMPMonth() {
		for (int i = 0; i < 12; i++) {
			El td = new El(mpMonths.item(i));
			String month;
			if (i % 2 == 0) {
				month = (i / 2) + "";
			} else {
				month = (((i - 1) / 2) + 6) + "";
			}
			td.dom.setPropertyString("xmonth", month);
			td.setStyleName("x-date-mp-sel", month.equals(focusMonth));
		}
	}
}

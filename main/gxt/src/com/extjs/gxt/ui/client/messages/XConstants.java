package com.extjs.gxt.ui.client.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface XConstants extends Constants {

	XConstants constants = GWT.create(XConstants.class);

	@DefaultStringArrayValue({ "西元前", "西元" })
	String[] eraNames();

	@DefaultStringValue("1")
	String firstDayOfTheWeek();

	@DefaultStringArrayValue({ "日", "一", "二", "三", "四", "五", "六" })
	String[] narrowWeekdays();

	@DefaultStringArrayValue({ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月",
			"9月", "10月", "11月", "12月" })
	String[] shortMonths();

	@DefaultStringArrayValue({ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
			"九月", "十月", "十一月", "十二月" })
	String[] standaloneMonths();
}

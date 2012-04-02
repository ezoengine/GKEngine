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
import java.util.Iterator;
import java.util.List;

import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class gkDateRangeField extends AdapterField {

	private Label label = new Label();
	private gkDateField dateFrom;
	private gkDateField dateTo;
	private gkTimeField timeFrom;
	private gkTimeField timeTo;

	public enum DateRangeType {
		DATES("dates"), DATETIMES("datesTimes"), DATESTIMES("datesTimes");

		private String value;

		private DateRangeType(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}
	}

	// 這邊是生成只有日期起迄的
	public gkDateRangeField(String fieldLabel, String beginDateKey,
			String endDateKey, boolean isSeparate) {
		this(fieldLabel, beginDateKey, endDateKey, "", isSeparate);
	}

	// 這邊也是只有日期起迄的，但是可以設定日期的格式
	public gkDateRangeField(String fieldLabel, String beginDateKey,
			String endDateKey, String dateFormat, boolean isSeparate) {
		super(null);
		LayoutContainer lc = new LayoutContainer();
		setFieldLabel(fieldLabel);
		dateFrom = new gkDateField(dateFormat);
		dateTo = new gkDateField(dateFormat);
		dateFrom.setId(beginDateKey);
		dateTo.setId(endDateKey);
		TableLayout tl;
		label.setText(Msg.get.to());
		label.setWidth("50%");
		if (isSeparate) {
			tl = new TableLayout(2);
		} else {
			tl = new TableLayout(3);
		}
		lc.setLayout(tl);
		lc.add(dateFrom);
		lc.add(label);
		lc.add(dateTo);
		widget = lc;
		setLabelSeparator("");
		componentListen();
		setRequires();
	}

	// 這邊是有2日期+2時間起迄的
	public gkDateRangeField(String fieldLabel, String beginDateKey,
			String beginTimeKey, String endDateKey, String endTimeKey,
			boolean isSeparate) {
		this(fieldLabel, beginDateKey, beginTimeKey, endDateKey, endTimeKey,
				"", isSeparate);
	}

	// 這邊是有2日期+2時間起迄的，而且還可以設定日期的格式
	public gkDateRangeField(String fieldLabel, String beginDateKey,
			String beginTimeKey, String endDateKey, String endTimeKey,
			String dateFormat, boolean isSeparate) {
		super(null);
		LayoutContainer lc = new LayoutContainer();
		setFieldLabel(fieldLabel);
		dateFrom = new gkDateField(dateFormat);
		dateTo = new gkDateField(dateFormat);
		timeFrom = new gkTimeField();
		timeTo = new gkTimeField();
		dateFrom.setId(beginDateKey);
		dateTo.setId(endDateKey);
		timeFrom.setId(beginTimeKey);
		timeTo.setId(endTimeKey);
		TableLayout tl;
		label.setText(Msg.get.to());
		label.setWidth("50%");
		if (isSeparate) {
			tl = new TableLayout(3);
		} else {
			tl = new TableLayout(5);
		}
		lc.setLayout(tl);
		lc.add(dateFrom);
		lc.add(timeFrom);
		lc.add(label);
		lc.add(dateTo);
		lc.add(timeTo);
		widget = lc;
		setLabelSeparator("");
		componentListen();
		setRequires();
	}

	// 這邊是有1日期+2時間起迄的
	public gkDateRangeField(String fieldLabel, String beginDateKey,
			String beginTimeKey, String endTimeKey) {
		this(fieldLabel, beginDateKey, beginTimeKey, endTimeKey, "");
	}

	// 這邊是有1日期+2時間起迄的，且可設定日期格式
	public gkDateRangeField(String fieldLabel, String beginDateKey,
			String beginTimeKey, String endTimeKey, String dateFormat) {
		super(null);
		LayoutContainer lc = new LayoutContainer();
		setFieldLabel(fieldLabel);
		dateFrom = new gkDateField(dateFormat);
		timeFrom = new gkTimeField();
		timeTo = new gkTimeField();
		dateFrom.setId(beginDateKey);
		timeFrom.setId(beginTimeKey);
		timeTo.setId(endTimeKey);
		TableLayout tl = new TableLayout(4);
		label.setText(Msg.get.to());
		label.setWidth("50%");

		lc.setLayout(tl);
		lc.add(dateFrom);
		lc.add(timeFrom);
		lc.add(label);
		lc.add(timeTo);
		widget = lc;
		setLabelSeparator("");
		componentListen();
		setRequires();
	}

	@Override
	protected boolean validateValue(String value) {
		boolean rtn = true;
		// 當兩個的 rawValue都不是空值的時候再檢查
		if ((!"".equals(dateFrom.getRawValue()))
				&& (!"".equals(dateTo.getRawValue()))) {
			// 日期的時候就錯了
			if (dateFrom.getUseDate().compareTo(dateTo.getUseDate()) > 0) {
				dateTo.markInvalid(Msg.get.dateError());
				rtn = false;
			}
			// 這邊表示是同一天，那就要檢查時間有沒有錯
			else if (dateFrom.getUseDate().compareTo(dateTo.getUseDate()) == 0) {
				if ((!"".equals(timeFrom.getRawValue()))
						&& (!"".equals(timeTo.getRawValue()))) {
					if (timeFrom.getTimeValue()
							.compareTo(timeTo.getTimeValue()) > 0) {
						timeTo.markInvalid(Msg.get.timeError());
						rtn = false;
					}
				}
			}
		}
		return rtn;
	}

	// 檢查後面的時間是不是大於前面的
	private void checkTime() {
		if (timeTo == null) {
			return;
		}
		if (dateTo != null) {
			if (dateTo.getRawValue().equals(dateFrom.getRawValue())) {
				if (timeFrom.getTimeValue().compareTo(timeTo.getTimeValue()) > 0) {
					timeTo.markInvalid(Msg.get.timeError());
				}
			}
		} else {
			if (timeFrom.getTimeValue().compareTo(timeTo.getTimeValue()) > 0) {
				timeTo.markInvalid(Msg.get.timeError());
			}
		}
	}

	// 判斷開始日期與結束日期在同一日,若是開始時間23:00，
	// 時間區間為30，則結束時間可選擇23:00、23:30，若不在同一日則無限制
	private void setTimeTo() {
		if ((timeFrom == null) || ("".equals(timeFrom.getRawValue()))) {
			return;
		}
		if (timeTo == null) {
			return;
		}
		List l = timeFrom.getStore().getModels();
		String nowTime = timeFrom.getValue().get("timeValue");
		timeTo.getStore().removeAll();
		if (dateTo == null
				|| dateFrom.getRawValue().equals(dateTo.getRawValue())) {
			for (int i = 0; i < l.size(); i++) {
				ModelData data = (ModelData) l.get(i);
				String setTime = data.get("timeValue");
				if (setTime.compareTo(nowTime) >= 0) {
					timeTo.getStore().add(data);
				}
			}
		} else {
			timeTo.getStore().add(l);
		}
	}

	// 就…專為了兩個日期+兩個時間判斷的驗證
	private void isValidate() {
		// 當兩個的 rawValue都不是空值的時候再檢查
		// if((!"".equals(dateFrom.getRawValue()))&&(!"".equals(dateTo.getRawValue()))){
		boolean chkNull = dateFrom != null && dateTo != null;
		boolean chkBlank = false;
		// 要判断是否为null，否则在只有dateFrom 或 dateTo的时候就会抛出NullPointException
		if (chkNull) {
			chkBlank = !"".equals(dateFrom.getRawValue())
					&& !"".equals(dateTo.getRawValue());
		}
		if (chkNull && chkBlank) {
			// 日期的時候就錯了
			if (dateFrom.getUseDate().compareTo(dateTo.getUseDate()) > 0) {
				dateTo.markInvalid(Msg.get.dateError());
			}
			// 這邊表示是同一天，那就要檢查時間有沒有錯
			else if (dateFrom.getUseDate().compareTo(dateTo.getUseDate()) == 0) {
				checkTime();
			}
		}
	}

	// 所有的監聽事件都寫在這
	private void componentListen() {
		if (dateFrom != null) {
			dateFrom.addListener(Events.OnBlur, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					isValidate();
					setTimeTo();
				}

			});
		}
		if (dateTo != null) {
			dateTo.addListener(Events.OnBlur, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					isValidate();
					setTimeTo();
				}

			});
		}
		if (timeFrom != null) {
			timeFrom.addListener(Events.OnBlur, new Listener() {
				@Override
				public void handleEvent(BaseEvent be) {
					checkTime();
					setTimeTo();
				}
			});
		}
		if (timeTo != null) {
			timeTo.addListener(Events.OnBlur, new Listener() {
				@Override
				public void handleEvent(BaseEvent be) {
					checkTime();
				}
			});
		}
	}

	// 日期預設為本日時間
	public void initDate() {
		Date date = new Date();
		if (dateFrom != null) {
			dateFrom.setValue(date);
		}
		if (dateTo != null) {
			dateTo.setValue(date);
		}
	}

	public void setShowInnerLabel(boolean visible) {
		label.setVisible(visible);
	}

	public void setDefaultDateRange(String beginDate, String endDate) {
		if (dateFrom != null) {
			dateFrom.setUseDate(beginDate);
		}
		if (dateTo != null) {
			dateTo.setUseDate(endDate);
		}
	}

	/**
	 * return 兩日期用","隔開
	 */
	public String getDefaultDateRange() {
		return (dateFrom != null && dateTo != null) ? dateFrom.getUseDate()
				+ "," + dateTo.getUseDate() : "";
	}

	public void setDefaultBeginDate(String beginDate) {
		if (dateFrom != null) {
			dateFrom.setUseDate(beginDate);
		}
	}

	public String getDefaultBeginDate() {
		return dateFrom != null ? dateFrom.getUseDate() : "";
	}

	public void setDefaultEndDate(String endDate) {
		if (dateTo != null) {
			dateTo.setUseDate(endDate);
		}
	}

	public String getDefaultEndDate() {
		return dateTo != null ? dateTo.getUseDate() : "";
	}

	public void setDefaultTimeRange(String beginTime, String endTime) {
		if ((timeFrom != null) && (timeTo != null)) {
			timeFrom.setTimeValue(beginTime);
			timeTo.setTimeValue(endTime);
		}
	}

	/**
	 * return 兩時間用","隔開
	 */
	public String getDefaultTimeRange() {
		return (timeFrom != null && timeTo != null) ? timeFrom.getTimeValue()
				+ "," + timeTo.getTimeValue() : "";
	}

	public void setDefaultBeginTime(String beginTime) {
		if (timeFrom != null) {
			timeFrom.setTimeValue(beginTime);
		}
	}

	public String getDefaultBeginTime() {
		return timeFrom != null ? timeFrom.getTimeValue() : "";
	}

	public void setDefaultEndTime(String endTime) {
		if (timeTo != null) {
			timeTo.setTimeValue(endTime);
		}
	}

	public String getDefaultEndTime() {
		return timeTo != null ? timeTo.getTimeValue() : "";
	}

	public void setTimeIncrement15() {
		if ((timeFrom != null) && (timeTo != null)) {
			timeFrom.set15MinPeriod();
			timeTo.set15MinPeriod();
		}
	}

	public void setDateFieldSize(int width, int height) {
		if (dateFrom != null) {
			dateFrom.setSize(width, height);
		}
		if (dateTo != null) {
			dateTo.setSize(width, height);
		}
	}

	public void setDateFieldWidth(String width) {
		if (dateFrom != null) {
			dateFrom.setWidth(width);
		}
		if (dateTo != null) {
			dateTo.setWidth(width);
		}
	}

	public void setTimeFieldWidth(String width) {
		if (timeFrom != null) {
			timeFrom.setWidth(width);
		}
		if (timeTo != null) {
			timeTo.setWidth(width);
		}
	}

	public void setInnerLabel(String labelText) {
		this.label.setText(labelText);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);

		if (dateFrom != null) {
			dateFrom.setReadOnly(readOnly);
		}
		if (dateTo != null) {
			dateTo.setReadOnly(readOnly);
		}
		if (timeFrom != null) {
			timeFrom.setReadOnly(readOnly);
		}
		if (timeTo != null) {
			timeTo.setReadOnly(readOnly);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (widget instanceof LayoutContainer) {
			LayoutContainer lc = (LayoutContainer) widget;

			for (Iterator<Component> it = lc.iterator(); it.hasNext();) {
				it.next().setEnabled(enabled);
			}
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (widget instanceof LayoutContainer) {
			LayoutContainer lc = (LayoutContainer) widget;

			for (Iterator<Component> it = lc.iterator(); it.hasNext();) {
				it.next().setVisible(visible);
			}
		}
	}

	private void setRequires() {
		if (getFieldLabel() != null && getFieldLabel().length() > 0
				&& getFieldLabel().endsWith("*")) {
			String fieldLabel = getFieldLabel().substring(0,
					getFieldLabel().length() - 1);
			setFieldLabel(fieldLabel);
			if (dateFrom != null) {
				dateFrom.setFieldLabel("*");
			}
			if (dateTo != null) {
				dateTo.setAllowBlank(false);
			}
			if (timeFrom != null) {
				timeFrom.setAllowBlank(false);
			}
			if (timeTo != null) {
				timeTo.setAllowBlank(false);
			}
		}
	}

	public void setEditable(boolean editable) {
		if (dateFrom != null) {
			dateFrom.setEditable(editable);
		}
		if (dateTo != null) {
			dateTo.setEditable(editable);
		}
		if (timeFrom != null) {
			timeFrom.setEditable(editable);
		}
		if (timeTo != null) {
			timeTo.setEditable(editable);
		}
	}

	@Override
	public boolean fireEvent(EventType type) {
		boolean result = super.fireEvent(type);
		LayoutContainer lc = (LayoutContainer) widget;
		for (Component com : lc.getItems()) {
			result &= com.fireEvent(type);
		}
		return result;
	}

	@Override
	public void clear() {
		super.clear();
		LayoutContainer lc = (LayoutContainer) widget;
		for (Component com : lc.getItems()) {
			if (com instanceof Field) {
				((Field) com).clear();
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		LayoutContainer lc = (LayoutContainer) widget;
		for (Component com : lc.getItems()) {
			if (com instanceof Field) {
				((Field) com).reset();
			}
		}
	}
}

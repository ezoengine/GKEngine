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

import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.form.gkComboBox;
import org.gk.ui.client.com.form.gkDateField;
import org.gk.ui.client.com.form.gkLabelField;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkListFieldIC;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.form.gkTimeField;
import org.gk.ui.client.com.form.gkYMField;
import org.gk.ui.client.com.panel.gkFieldAccessIfc;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * <pre>
 * 公用工具類別，透過此工具類別所產生的GXT物件，將會和傳入
 * Map物件中的指定key進行資料繫結。也就是說，當使用者修改了
 * GXT物件的狀態，該狀態就會自動保留在Map。另外，GXT元件
 * 也會訂閱指定事件，所以可以透過EventBus將Map資訊一次
 * 發佈給所有的GXT物件進行變更，所以資料繫結是雙向的。
 * </pre>
 * 
 * @author I21890
 * @date 2009/03
 */
public class BindingUtils {

	/**
	 * 更新指定LayoutContainer的資訊
	 * 
	 * @param lc
	 *            LayoutContainer型別的物件
	 * @param info
	 *            要更新的資訊物件
	 */
	public static void update(LayoutContainer lc, Map info) {
		update(lc.getId(), info);
	}

	/**
	 * 更新指定eventId的資訊
	 * 
	 * @param eventId
	 *            發佈的EventId
	 * @param info
	 *            要更新的資訊物件
	 */
	public static void update(String eventId, Map info) {
		EventBus.get().publish(new EventObject(eventId, info));
	}

	/**
	 * 建立Radio，並讓此Radio物件訂閱eventId事件，當收到此事件 將更新info物件和本身欄位顯示
	 * 
	 * @param eventId
	 * @param info
	 * @param infoKey
	 * @param infoValue
	 * @param core
	 * @return Radio
	 */
	public static Radio createRadio(final String eventId, final Map info,
			final String infoKey, final String infoValue, final CoreIC core) {
		final Radio radio = new Radio() {

			@Override
			public void focus() {
				if (rendered) {
					getFocusEl().focus();
					onFocus(new FieldEvent(this));
				}
			}

			@Override
			protected void onFocus(ComponentEvent ce) {
				if (!hasFocus) {
					if (!readOnly) {
						// 更新form的info資料
						core.getBus().publish(
								new EventObject(eventId + "-radio", new gkMap(
										infoKey, infoValue)));
						// 設定相同name的radio資料
						core.getBus().publish(
								new EventObject(eventId, new gkMap(infoKey,
										infoValue)));
					}
					super.onFocus(ce);
				}
			}

			@Override
			protected void onClick(ComponentEvent be) {
				// if we click the boxLabel, the browser fires an own click
				// event
				// automatically, so we ignore one of it
				if (boxLabelEl != null
						&& boxLabelEl.dom.isOrHasChild(be.getTarget())) {
					return;
				}
				if (readOnly) {
					be.stopEvent();
					return;
				}
				setValue(true);
				fireEvent(Events.Select, be);
			}

			@Override
			public void setValue(Boolean value) {
				if (!hasFocus && value) {
					// 由於可能不經過滑鼠的點選動作來設值，所以這裡更新info
					info.put(infoKey, infoValue);
				}
				super.setValue(value);
			}
		};

		core.subscribe(eventId + "-radio", new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					if (infoValue.equals(infoMap.get(infoKey))) {
						info.put(infoKey, infoValue);
					} else {
						String value = (String) info.get(infoKey);
						if (value != null && value.equals(infoValue)) {
							info.put(infoKey, "");
						}
					}
				}
			}
		});

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					Object value = infoMap.get(infoKey);
					radio.setValue(infoValue.equals(value));
				}
			}
		});

		addListener(Events.Change, radio, infoKey, info);
		initialInfoValue(radio, infoKey, info, "");
		return radio;
	}

	/**
	 * 建立NumberField，此物件的資訊將放在info物件指定的infoKey
	 * 
	 * @param eventId
	 * @param info
	 * @param infoKey
	 * @param core
	 * @return NumberField
	 */
	public static NumberField createNumberField(String eventId, final Map info,
			final String infoKey, CoreIC core) {

		final NumberField nf = new NumberField();
		nf.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				// 複製時非數字會顯示先前的value
				if (nf.getValue() == null) {
					if (nf.getRawValue().equals("")) {
						info.put(infoKey, 0);
					} else {
						Number nv = (Number) info.get(infoKey);
						nf.setValue(nv);
						info.put(infoKey, nv);
					}
				} else {
					info.put(infoKey, nf.getValue());
				}
			}
		});

		subscribe(eventId, nf, infoKey, info, core);
		addListener(Events.Change, nf, infoKey, info);
		initialInfoValue(nf, infoKey, info, "");
		return nf;
	}

	/**
	 * 建立LabelField，此物件的資訊將放在info物件指定的infoKey
	 * 
	 * @param eventId
	 * @param info
	 * @param infoKey
	 * @param core
	 * @return gkLabelField
	 */
	public static gkLabelField createLabelField(String eventId, Map info,
			String infoKey, CoreIC core) {

		gkLabelField label = new gkLabelField();

		subscribe(eventId, label, infoKey, info, core);
		addListener(Events.Change, label, infoKey, info);
		initialInfoValue(label, infoKey, info, "");
		return label;
	}

	public static ComboBox createComboBox(String eventId, final Map info,
			final String infoKey, CoreIC core) {
		final gkComboBox cb = new gkComboBox();

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					Object value = infoMap.get(infoKey);
					ModelData md = null;
					if (value instanceof String) {
						md = cb.getPropertyEditor().convertStringValue(
								(String) value);
					} else {
						md = (ModelData) value;
					}
					cb.setValue(md);
					info.put(infoKey, md == null ? "" : md.get("value"));
				}
			}
		});

		cb.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				updateDirtyField(infoKey, info);
				ModelData md = se.getSelectedItem();
				info.put(infoKey, md == null ? "" : md.get("value"));
			}
		});

		cb.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				updateDirtyField(infoKey, info);
				ModelData value = cb.getValue();
				info.put(infoKey, value == null ? "" : value.get("value"));
			}
		});

		initialInfoValue(cb, infoKey, info, "");
		return cb;
	}

	public static gkListFieldIC createListFieldIC(String eventId,
			final Map info, final String infoKey, CoreIC core) {

		final gkListFieldIC lf = new gkListFieldIC();

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					Object value = infoMap.get(infoKey);
					if (value == null) {
						return;
					}
					// 后端传来的资料可以是：List 和 String[] 两种形式
					if (value instanceof List) {
						lf.setSelectItem((List) value);
						info.put(infoKey, value);
					}
					if (value instanceof String[]) {
						lf.setSelectItem((String[]) value);
					}
				}
			}
		});

		lf.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {

				List selectlist = se.getSelection();
				if (selectlist != null) {
					// .若 selectedItem 有改變時 才填入 dirtyField
					updateDirtyField(infoKey, info);
					// .因應 eventId_infoChange 事件，應該設定 dirtyField
					// 最後才info.put()
					info.put(infoKey, selectlist);
				}
			}
		});

		addListener(Events.Change, lf, infoKey, info);
		initialInfoValue(lf, infoKey, info, new gkList());

		return lf;
	}

	/**
	 * 建立DateField，此物件的資訊將放在info物件指定的infoKey
	 * 
	 * @param eventId
	 * @param info
	 * @param infoKey
	 * @param format
	 * @param core
	 * @return gkDateField
	 */
	public static gkDateField createDateField(String eventId, final Map info,
			final String infoKey, String format, CoreIC core) {

		final gkDateField df = new gkDateField(format);

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					String value = (String) infoMap.get(infoKey);
					if (value != null && !value.equals("")) {
						df.setUseDate(value);
						info.put(infoKey, df.getUseDate());
					} else {
						// 選項欄位value為空時清除欄位中的顯示（當通過事件設定欄位value為空時欄位無法自行設定顯示為空需通過此邏輯清除欄位中的顯示）
						df.clear();
					}
				}
			}
		});

		addListener(Events.Change, df, infoKey, info);
		initialInfoValue(df, infoKey, info, "");

		return df;
	}

	public static gkYMField createYMField(String eventId, final Map info,
			final String infoKey, String format, CoreIC core) {

		final gkYMField yf = new gkYMField(format);

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					String value = (String) infoMap.get(infoKey);
					if (value != null && !value.equals("")) {
						yf.setUseDate(value);
						info.put(infoKey, yf.getUseDate());
					} else {
						// 選項欄位value為空時清除欄位中的顯示（當通過事件設定欄位value為空時欄位無法自行設定顯示為空需通過此邏輯清除欄位中的顯示）
						yf.clear();
					}
				}
			}
		});

		addListener(Events.Change, yf, infoKey, info);
		initialInfoValue(yf, infoKey, info, "");

		return yf;
	}

	/**
	 * 建立CheckBox，此物件的資訊將放在info物件指定的infoKey，value是用List存放已勾選的值
	 * 
	 * @param eventId
	 * @param info
	 * @param infoKey
	 * @param infoValue
	 * @param core
	 * @return CheckBox
	 */
	public static CheckBox createCheckBox(final String eventId, final Map info,
			final String infoKey, final String infoValue, CoreIC core) {

		final CheckBox cb = new CheckBox() {

			@Override
			public void setValue(Boolean b) {
				super.setValue(b);
				List cbList = (List) info.get(infoKey);
				if (b != null && b) {
					// 如果是true而且不在cbList裡面，表示狀態更新了
					if (!cbList.contains(infoValue)) {
						cbList.add(infoValue);
						// 透過put發布InfoChange事件
						info.put(infoKey, cbList);
					}
				} else {
					// 如果在cbList裡面，表示狀態更新了
					if (cbList.contains(infoValue)) {
						cbList.remove(infoValue);
						// 透過put發布InfoChange事件
						info.put(infoKey, cbList);
					}
				}
			}

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);

				if ((boxLabelEl != null && boxLabelEl.dom.isOrHasChild(ce
						.getTarget())) || readOnly) {
					return;
				}
				fireEvent(Events.Select, ce);
			}
		};

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					Object value = infoMap.get(infoKey);
					if (value != null && value instanceof List) {
						List cbList = (List) value;
						cb.setValue(cbList.contains(infoValue));
					}
				}
			}
		});

		addListener(Events.Change, cb, infoKey, info);
		initialInfoValue(cb, infoKey, info, new gkList());
		return cb;
	}

	/**
	 * 建立TimeField，此物件的資訊將放在info物件指定的infoKey
	 * 
	 * @param eventId
	 * @param info
	 * @param infoKey
	 * @param core
	 * @return gkTimeField
	 */
	public static gkTimeField createTimeField(String eventId, final Map info,
			final String infoKey, CoreIC core) {

		final gkTimeField tf = new gkTimeField();

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					String value = (String) infoMap.get(infoKey);
					value = value.replaceAll(":", "");
					tf.setTimeValue(value);
					// 選項欄位value為空時清除欄位中的顯示（當通過事件設定欄位value為空時欄位無法自行設定顯示為空需通過此邏輯清除欄位中的顯示）
					if (value.equals("")) {
						tf.clearSelections();
					}
					info.put(infoKey, value);
				}
			}
		});

		tf.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				updateDirtyField(infoKey, info);
				ModelData md = se.getSelectedItem();
				info.put(infoKey, md == null ? "" : md.get("timeValue"));
			}
		});

		tf.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				updateDirtyField(infoKey, info);
				ModelData value = tf.getValue();
				info.put(infoKey, value == null ? "" : value.get("timeValue"));
			}
		});

		initialInfoValue(tf, infoKey, info, "");
		return tf;
	}

	public static void binding(String eventId, Field field, final Map info,
			final gkFieldAccessIfc access, CoreIC core) {

		// 判斷是否為AdapterField
		if (field instanceof AdapterField) {
			LayoutContainer lc = (LayoutContainer) ((AdapterField) field)
					.getWidget();
			// 取出layoutConatainer裡面的物件
			for (int i = 0; i < lc.getItemCount(); i++) {
				Object obj = lc.getItem(i);
				// 需要是field才作監聽動作
				if (obj instanceof Field) {
					final Field f = (Field) obj;

					// 訂閱事件
					core.subscribe(eventId, new EventProcess() {

						@Override
						public void execute(String eventId, EventObject eo) {
							Map infoMap = eo.getInfoMap();
							if (infoMap.containsKey(f.getId())) {
								access.setValue(infoMap);
								Object value = infoMap.get(f.getId());
								if (value != null) {
									info.put(f.getId(), value);
								}
							}
						}
					});

					// 監聽事件
					f.addListener(Events.Change, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							updateDirtyField(f.getId(), info);
							// .因應 eventId_infoChange 事件，應該設定 dirtyField
							// 最後才info.put()
							info.put(f.getId(), access.getValue(f.getId()));
						}
					});

					// 填完值離開後(onblur)也要把值填回dirtyField
					f.addListener(Events.OnBlur, new Listener() {

						@Override
						public void handleEvent(BaseEvent be) {
							updateDirtyField(f.getId(), info);
							info.put(f.getId(), access.getValue(f.getId()));
						}
					});

					// 判斷widget component是否有預設值
					Object filedValue = access.getValue(f.getId());
					if (filedValue != null) {
						info.put(f.getId(), filedValue);
					}
				}
			}
		}
	}

	/**
	 * 提供其他欄位binding
	 * 
	 * @param eventId
	 * @param field
	 * @param infoKey
	 * @param info
	 * @param core
	 */
	public static void binding(String eventId, Field field, String infoKey,
			Map info, CoreIC core) {
		subscribe(eventId, field, infoKey, info, core);
		addListener(Events.Change, field, infoKey, info);
		addKeyListener(field, infoKey, info);
		initialInfoValue(field, infoKey, info, "");
	}

	/**
	 * 訂閱事件
	 * 
	 * @param eventId
	 * @param field
	 * @param infoKey
	 * @param info
	 * @param core
	 */
	public static void subscribe(String eventId, final Field field,
			final String infoKey, final Map info, CoreIC core) {

		core.subscribe(eventId, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				Map infoMap = eo.getInfoMap();
				if (infoMap.containsKey(infoKey)) {
					Object value = infoMap.get(infoKey);
					if (field instanceof NumberField) {
						// 非空字串或非null才處理
						if (value != null && !value.equals("")) {
							field.setValue(field.getPropertyEditor()
									.convertStringValue("" + value));
						}
					} else {
						if (value instanceof String) {
							field.setValue(field.getPropertyEditor()
									.convertStringValue((String) value));
						} else {
							field.setValue(value);
						}
					}
					info.put(infoKey, value == null ? "" : value);
				}
			}
		});
	}

	/**
	 * 增加監聽事件
	 * 
	 * @param eventType
	 * @param field
	 * @param infoKey
	 * @param info
	 */
	public static void addListener(EventType eventType, final Field field,
			final String infoKey, final Map info) {

		field.addListener(eventType, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				updateDirtyField(infoKey, info);
				if (field instanceof TextField || field instanceof LabelField
						|| field instanceof SliderField) {
					Object value;
					if (field instanceof gkDateField) {
						value = ((gkDateField) field).getUseDate();
					} else {
						value = field.getValue();
					}
					// 因應eventId_infoChange事件，應該設定dirtyField最後才info.put()
					info.put(infoKey, value == null ? "" : value);
				}
			}
		});
	}

	/**
	 * 增加鍵盤的監聽事件
	 * 
	 * @param field
	 * @param infoKey
	 * @param info
	 */
	public static void addKeyListener(final Field field, final String infoKey,
			final Map info) {

		field.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				updateDirtyField(infoKey, info);
				Object value = field.getValue();
				info.put(infoKey, value == null ? "" : value);
			}
		});
	}

	/**
	 * 初始化info內的資料
	 * 
	 * @param field
	 * @param infoKey
	 * @param info
	 * @param defaultValue
	 */
	public static void initialInfoValue(Field field, String infoKey, Map info,
			Object defaultValue) {

		info.put(infoKey, defaultValue);
	}

	/**
	 * 更新info內，dirtyList的infoKey
	 * 
	 * @param infoKey
	 * @param info
	 */
	public static void updateDirtyField(String infoKey, Map info) {
		if (info.containsKey(gkFormPanelIC.Event.DIRTY_FIELD)) {
			List dirtyList = (List) info.get(gkFormPanelIC.Event.DIRTY_FIELD);
			// 先remove, 在add, for 觸發dirtyField事件
			dirtyList.remove(infoKey);
			dirtyList.add(infoKey);
		}
	}
}

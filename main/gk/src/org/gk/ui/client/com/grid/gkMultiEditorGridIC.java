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
package org.gk.ui.client.com.grid;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class gkMultiEditorGridIC extends gkGridIC {

	private List columnKeyList = new gkList();

	private gkGridIC deGridIC;

	private final static String DIRTYFIELD = "dirtyField";
	// for user 可編輯欄位之 key List
	private final static String EDITFIELD = "editField";
	// 供外界設定 addRow
	private boolean isAddRow = true;
	// 是否只取得已勾選的資料
	private boolean isGetSelectedItems = true;
	// 是否自動勾選
	private boolean autoSelect = true;

	@Override
	public void setInitBlankRow(boolean initBlankRow) {
		getOrigenalGridIC().setInitBlankRow(initBlankRow);
	}

	@Override
	public boolean isInitBlankRow() {
		return getOrigenalGridIC().isInitBlankRow();
	}

	/**
	 * 提供事件清單
	 */
	public static interface Event {
		// 自動增列事件
		public final static String AUTOADDROW = ".autoAddRow";
	}

	public String evtAutoAddRow() {
		return getId() + Event.AUTOADDROW;
	}

	@Override
	public Object getInfo() {
		// 取所有资料，需要将isGetSelectedItems设定为false
		setGetSelectedItems(false);
		return getListItem();
	}

	@Override
	public void setInvokeBean(String bean) {
		getOrigenalGridIC().setInvokeBean(bean);
	}

	@Override
	public String getInvokeBean() {
		return getOrigenalGridIC().getInvokeBean();
	}

	@Override
	public void invokeBean(String bean) {
		deGridIC.invokeBean(bean);
	}

	@Override
	public void setLimit(int limitRecords) {
		deGridIC.setLimit(limitRecords);
	}

	@Override
	public void setInfo(Object info) {
		getOrigenalGridIC().setInfo(info);
		// 當設定的資料為空且需要initRow或自動增列任一時，則增加一行空的列
		if ((isInitBlankRow() || getOrigenalGridIC().getAutoNewRow())
				&& getGrid().getStore().getCount() == 0) {
			addInitRow();
		}
	}

	public void setAutoSelect(boolean select) {
		this.autoSelect = select;
	}

	public boolean isAutoSelect() {
		return this.autoSelect;
	}

	private CheckBoxSelectionModel<ModelData> sm = new CheckBoxSelectionModel<ModelData>() {

		@Override
		protected void handleMouseDown(GridEvent<ModelData> e) {
			ModelData m = listStore.getAt(e.getRowIndex());
			// 點選row中任一column都可以讓checkbox打勾，但只有點選checkBox才能取消
			// 符合中冠先前多筆編輯操作的習慣
			int checkBoxColumnIdx = 0;
			if (m != null) {
				if (isSelected(m)) {
					// 如果是选中，不变。如果是点击到checkBox则取消选中
					if (e.getColIndex() == checkBoxColumnIdx) {
						doDeselect(Arrays.asList(m), false);
					}
				} else if (e.isShiftKey() && lastSelected != null) {
					// 按住shift键多选
					if (autoSelect) {
						select(listStore.indexOf(lastSelected),
								e.getRowIndex(), e.isControlKey());
					}
					grid.getView().focusCell(e.getRowIndex(), e.getColIndex(),
							true);
				} else {
					if (autoSelect || e.getColIndex() == checkBoxColumnIdx) {
						doSelect(Arrays.asList(m), true, false);
					}
					// 改用focusCell，避免因為focusRow造成視窗會focus到第一個column而改變
					grid.getView().focusCell(e.getRowIndex(), e.getColIndex(),
							true);
				}
			}
			e.cancelBubble();
		}

		@Override
		protected void onKeyPress(GridEvent<ModelData> e) {
			// 遮蔽按 space 鍵進行 select , unselect問題
		}

		// 在grid里当操作的cell为TextArea时，操作上下方向键不响应selectionModel的动作
		// 因为selectionModel的动作会失去cell的焦点
		@Override
		protected void onKeyDown(GridEvent<ModelData> e) {
			Element target = e.getTarget();
			String tag = target.getTagName();
			if (!"textarea".equalsIgnoreCase(tag)) {
				super.onKeyDown(e);
			}
		}

		@Override
		protected void onKeyUp(GridEvent<ModelData> e) {
			Element target = e.getTarget();
			String tag = target.getTagName();
			if (!"textarea".equalsIgnoreCase(tag)) {
				super.onKeyUp(e);
			}
		}
	};

	public gkMultiEditorGridIC(String id, gkGridIC deGridIC) {
		setId(id);
		this.deGridIC = deGridIC;
		initCheckBox();
		setLayout(new FitLayout());
		add(deGridIC);
		createSubscribeEvent();
	}

	@Override
	public void setListItem(List<gkMap> list) {
		getOrigenalGridIC().setListItem(list);
	}

	@Override
	protected void createSubscribeEvent() {
		// .for 取得是否為最末筆 only ListGrid to add a column
		core.subscribe(evtSelectRow(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				int rowIndex = Integer.parseInt(eo.getInfoMap().get("rowIndex")
						.toString());
				int count = getGrid().getStore().getCount();
				// .是否限制為 dejgListGridIC<否>
				if (rowIndex == (count - 1)
						// && getOrigenalGridIC() instanceof dejgListGridIC
						&& isAddRow
						&& Integer.parseInt(eo.getInfoMap().get("colIndex")
								.toString()) != 0) {
					// .此功能與 dejgCellRenderBuilder.createNewRow() 功能重複
					addRow();
				}
			}
		});

		// .監聽 setListItem若為空 且 store為空 且isInitBlankRow ==true 則 增加一列空白
		// .監聽 setListItem，清除 dirty style
		core.subscribe(evtSetListItem(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				deGridIC.models = eo.getInfoList();
				deGridIC.getGrid().getStore().getLoader().load();
				if (deGridIC.models.size() == 0 && isInitBlankRow()) {
					addRow();
				}
				clearModifiedRecords();
			}
		});
		// .for 訂閱 自動增列事件
		core.subscribe(evtAutoAddRow(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				addRow();
			}
		});

		// .監聽 setBean (dejgPageGridIC)
		core.subscribe(evtSetBean(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				EventBus.get().publish(
						new EventObject(deGridIC.getId()
								+ gkGridIC.Event.SET_BEAN, eo.getInfoString()));
			}
		});

		// .監聽 refresh
		core.subscribe(evtRefresh(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				getGrid().getView().refresh(true);
			}
		});

		// .監聽 getListItem
		core.subscribe(evtGetListItem(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				// 放到infoList,讓源頭取得
				eo.getInfoList().addAll(deGridIC.models);
			}
		});
	}

	/**
	 * 增加checkBox列
	 */
	protected void initCheckBox() {
		Grid grid = this.deGridIC.getGrid();
		ColumnModel cm = grid.getColumnModel();
		List columns = cm.getColumns();

		// .順勢取得 columnConfig 之 id
		// .for 驗證時使用 (selectValidate) 以及 getSelectListItem
		for (Iterator ite = columns.iterator(); ite.hasNext();) {
			ColumnConfig cf = (ColumnConfig) ite.next();
			columnKeyList.add(cf.getId());
		}

		columns.add(0, sm.getColumn());
		// .設定為 SelectionMode.SIMPLE 才有 多筆選項之功能 (for gxt2.1)
		sm.setSelectionMode(SelectionMode.SIMPLE);
		grid.setSelectionModel(sm);
		grid.addPlugin(sm);

		// 將sm中的RowMouseDown事件的執行順序，優先於gkGridIC裡註冊的RowMouseDown事件
		List eventList = new gkList(grid.getListeners(Events.RowMouseDown));
		for (Iterator ite = eventList.iterator(); ite.hasNext();) {
			grid.removeListener(Events.RowMouseDown, (Listener) ite.next());
		}
		for (int i = eventList.size() - 1; i >= 0; i--) {
			grid.addListener(Events.RowMouseDown, (Listener) eventList.get(i));
		}
	}

	@Override
	public ColumnModel createColumnModel() {
		return null;
	}

	/**
	 * 取得原注入 gridIC
	 * 
	 * @return gkGridIC
	 */
	public gkGridIC getOrigenalGridIC() {
		return this.deGridIC;
	}

	public boolean isGetSelectedItems() {
		return isGetSelectedItems;
	}

	public void setGetSelectedItems(boolean isGetSelectedItems) {
		this.isGetSelectedItems = isGetSelectedItems;
	}

	/**
	 * 提供AP 自動增列按鈕
	 * 
	 * @param btnName
	 *            按鈕名稱
	 * @return Button
	 */
	public Button genAutoAddRowButton(String btnName) {
		if (btnName == null || btnName.trim().equals("")) {
			// .增列
			btnName = Msg.get.autoAddRow();
		}
		return new Button(btnName, new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				EventBus.get().publish(new EventObject(evtAutoAddRow()));
			}
		});
	}

	/**
	 * 設定最末列時，是否新增一列 預設為 true
	 * 
	 * @param isAddRow
	 */
	public void setAutoAddRow(boolean isAddRow) {
		this.isAddRow = isAddRow;
	}

	@Override
	public Grid getGrid() {
		return deGridIC.getGrid();
	}

	@Override
	public void setHeaderStyle(String css) {
		deGridIC.setHeaderStyle(css);
	}

	@Override
	public GridView getView() {
		return deGridIC.getView();
	}

	@Override
	public void load() {
		deGridIC.load();
	}

	@Override
	public void load(EventObject eo) {
		deGridIC.load(eo);
	}

	@Override
	public void load(Map info) {
		deGridIC.load(info);
	}

	@Override
	public List getListItem() {
		List storeList;
		// 若為'true'，則只取得已勾選的資料，反之則取得所有資料
		if (isGetSelectedItems) {
			storeList = deGridIC.getGrid().getSelectionModel()
					.getSelectedItems();
		} else {
			storeList = deGridIC.getGrid().getStore().getModels();
		}

		int idx = 0;
		for (Iterator ite = storeList.iterator(); ite.hasNext();) {
			ModelData md = (ModelData) ite.next();
			md.set(INDEX, idx);
			idx++;
		}
		return storeList;
	}

	/**
	 * 在 getSelectListItem時 將 idx 設入 若為 CellEditor 可用store.getModifiedRecords()
	 * 取得 dirtyField 否則 採用 render 方式 需另行透過別法
	 * 
	 * @return List
	 */
	public List<ModelData> getSelectListItem() {
		List selectedList = sm.getSelectedItems();
		ListStore store = deGridIC.getGrid().getStore();

		for (Iterator ite = selectedList.iterator(); ite.hasNext();) {
			ModelData md = (ModelData) ite.next();
			int idx = store.indexOf(md);
			// .set idx
			md.set(INDEX, idx);

			Record rd = store.getRecord(md);
			if (store.getModifiedRecords().contains(rd)) {
				// .set dirtyField
				md.set(DIRTYFIELD, rd.getChanges().keySet());
			}

			// .for 後端處理，若Record 缺少某 columnKey 則補足之
			for (Iterator iteColKey = columnKeyList.iterator(); iteColKey
					.hasNext();) {
				String key = (String) iteColKey.next();
				if (!md.getProperties().containsKey(key)) {
					md.set(key, null);
				}
			}
		}
		return selectedList;
	}

	/**
	 * 設定第幾筆item 打勾
	 * 
	 * @param i
	 *            (start from 0 end with page row size-1)
	 */
	public void setSelectListItem(int i) {
		ListStore store = deGridIC.getGrid().getStore();
		if (i < store.getCount()) {
			ModelData m = store.getAt(i);
			if (!sm.isSelected(m)) {
				sm.select(i, true);
			}
		}
	}

	/**
	 * 設定多筆
	 * 
	 * @param strAry
	 */
	public void setSelectListItem(String[] strAry) {
		for (int i = 0; i < strAry.length; i++) {
			try {
				setSelectListItem(Integer.parseInt(strAry[i]));
			} catch (NumberFormatException nfEx) {
				System.err.print("NumberFormatException:"
						+ nfEx.getLocalizedMessage());
			}
		}
	}

	/**
	 * 設定多筆checked, 透過取得 list中 map.get(idx)來執行
	 * 
	 * @param selectedList
	 */
	public void setSelectListItem(List selectedList) {
		for (Iterator ite = selectedList.iterator(); ite.hasNext();) {
			gkMap deMap = (gkMap) ite.next();
			try {
				setSelectListItem(Integer.parseInt(deMap.get(INDEX).toString()));
			} catch (NumberFormatException nfEx) {
				System.err.print("NumberFormatException:"
						+ nfEx.getLocalizedMessage());
			}
		}
	}

	/**
	 * 清空 checkBox
	 */
	public void clearCheckBox() {
		sm.deselectAll();
	}

	public String getColumnValue(int row, String columnName) {
		ListStore store = deGridIC.getGrid().getStore();
		Object obj = null;

		if (row < store.getCount()) {
			ModelData m = store.getAt(row);
			obj = m.get(columnName);
		}
		obj = obj == null ? "" : obj;
		return obj.toString();
	}

	@Override
	public String getColumnValue(int row, int column) {
		ListStore store = deGridIC.getGrid().getStore();
		ColumnModel cm = deGridIC.getGrid().getColumnModel();
		Object obj = null;
		if (column < cm.getColumnCount()) {
			ColumnConfig cf = cm.getColumn(column);
			if (row < store.getCount()) {

				ModelData m = store.getAt(row);
				obj = m.get(cf.getId());
			}
		}
		obj = obj == null ? "" : obj;
		return obj.toString();
	}

	/**
	 * 取得特定 (row,column) 的 widget
	 * 
	 * @param row
	 * @param column
	 * @return Widget if column not outOfBound else null
	 */
	public Widget getComponet(int row, int column) {
		try {
			return getView().getWidget(row, column);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * 取得特定 row 之 column 的 widget
	 * 
	 * @param row
	 * @param columnName
	 * @return Widget if column defined else null
	 */
	public Widget getComponet(int row, String columnName) {
		try {
			ColumnModel cm = getGrid().getColumnModel();
			return getView().getWidget(row, cm.getIndexById(columnName));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * 驗證 selection items 是否 validated true is validated false is failed
	 * validated
	 * 
	 * @return boolean
	 */
	public boolean selectedValidate() {
		List<ModelData> selectedList = sm.getSelectedItems();
		ListStore store = deGridIC.getGrid().getStore();

		if (core.getInfo() != null && core.getInfo().get(EDITFIELD) != null) {
			((List) core.getInfo().get(EDITFIELD)).clear();
		} else {
			core.setInfo(EDITFIELD, new LinkedList());
		}
		int intEditField = 0;

		boolean validated = true;

		for (Iterator ite = selectedList.iterator(); ite.hasNext(); intEditField++) {
			ModelData md = (ModelData) ite.next();
			int idxRow = store.indexOf(md);

			for (Iterator<String> iteKey = columnKeyList.iterator(); iteKey
					.hasNext();) {
				String key = iteKey.next();

				// .call widget.validate()
				BoxComponent boxCmp = (BoxComponent) getComponet(idxRow, key);

				Field fd;
				if (boxCmp != null && boxCmp instanceof Field) {
					fd = (Field) boxCmp;
				} else {
					continue;
				}

				try {
					if (!fd.isValid()) {
						System.err.print("\n isValid false" + " (" + idxRow
								+ "," + key + ")");
						validated = false;
					} else {
						System.out.print("\n isValid true" + " (" + idxRow
								+ "," + key + ")");
					}

					// .在此執行檢驗欄位是否可編輯 以提供後端使用，應考量 LabelField 等 例外狀況
					// .僅作一次 intEditField==0
					if (!fd.isReadOnly() && (!(fd instanceof LabelField))
							&& intEditField == 0) {
						List aList = (List) core.getInfo().get(EDITFIELD);
						if (!aList.contains(key)) {
							aList.add(key);
						}
					}
				} catch (Exception ex) {
					System.err.print("\n validate ex:" + ex.getMessage());
				}

			}
			System.out.print("\n EDITFIELD:"
					+ core.getInfo().get(EDITFIELD).toString());
		}
		return validated;
	}

	/**
	 * 清除已勾選之 modifiedRecords(小三角消失)
	 */
	public void clearModifiedRecords() {
		getGrid().getStore().commitChanges();
	}

	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		sm.setSelectionMode(selectionMode);
		this.deGridIC.getGrid().setSelectionModel(sm);
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}

	/**
	 * 設定是否顯示checkBox并選中整批資料
	 * 
	 * @param isVisible
	 */
	public void setCheckBox(boolean isVisible) {
		getGrid().getColumnModel().getColumns().get(0).setHidden(!isVisible);
	}

}

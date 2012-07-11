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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public abstract class gkGridIC extends ContentPanel implements IC {

	protected Grid grid;
	protected ListStore store;
	protected BaseListLoader loader;
	protected String headerStyle = "x-grid3-hd-row";
	protected List<gkMap> models = new gkList();
	protected CoreIC core;
	protected int limitRecords = 0; // 前端限定資料筆數
	protected boolean autoNewRow = false; // 設定是否自動增列
	protected String invokeBean = "";
	protected final static String INDEX = "idx"; // row Index
	protected final static String GK_INDEX = "_gk_idx"; // row Index
	protected String filterValue;
	protected int updateBuffer = 500; // 設定filter store的延遲時間

	// 是否只取得已勾選的資料
	private boolean isGetSelectedItems = true;

	public boolean isGetSelectedItems() {
		return isGetSelectedItems;
	}

	public void setGetSelectedItems(boolean isGetSelectedItems) {
		this.isGetSelectedItems = isGetSelectedItems;
	}

	// 初始是否新增一空白列
	private boolean initBlankRow;

	public void setInitBlankRow(boolean initBlankRow) {
		this.initBlankRow = initBlankRow;
	}

	public boolean isInitBlankRow() {
		return initBlankRow;
	}

	// 定義Comparator，將TreeMap的key轉為int再做比較，避免idx按照String型別排序產生錯亂
	protected Comparator comparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			int idxPrev = Integer.parseInt(o1 + "");
			int idxNext = Integer.parseInt(o2 + "");
			return idxPrev < idxNext ? -1 : idxPrev > idxNext ? 1 : 0;
		}
	};
	protected DelayedTask deferredFilter = new DelayedTask(
			new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					reload();
				}
			});

	@Override
	public CoreIC core() {
		return core;
	}

	@Override
	public Object getInfo() {
		if (grid.getSelectionModel() instanceof gkCheckBoxSelectionModel) {
			// 取所有資料，需要將isGetSelectedItems設定為false
			setGetSelectedItems(false);
			return getListItem();
		}
		return getGrid().getStore().getModels();
	}

	@Override
	public void bindEvent() {
		// 為了相容先前沒習慣使用bindEvent方法註冊事件，所以override空實作
	}

	public gkGridIC() {
		core = new CoreIC(this);
		core.init();
	}

	public Grid getGrid() {
		return grid;
	}

	/**
	 * <pre>
	 * 如果是List，表示要置換整個清單的資料 
	 * 如果是Map，表示要更新某筆資料
	 * </pre>
	 */
	@Override
	public void setInfo(Object info) {
		if (info instanceof List) {
			setListItem((List) info);
		}
		// {"0":{"field1":"11", "field2":"22"}}
		// key是rowIdx , value是該筆資料
		else if (info instanceof Map) {
			models.clear(); // 清掉原來的資料
			Map map = new TreeMap(comparator);
			map.putAll((Map) info);
			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				int rowIdx = Integer.parseInt("" + key);
				gkMap value = (gkMap) map.get(key);
				if (rowIdx + 1 > models.size()) {
					models.add(value);
				} else {
					models.get(rowIdx).putAll(value);
				}
			}
			grid.getStore().getLoader().load();
		}
		// 當設定的資料為空且需要initRow或自動增列任一時，則增加一行空的列
		if (grid.getSelectionModel() instanceof gkCheckBoxSelectionModel
				&& (isInitBlankRow() || isAutoNewRow())
				&& getGrid().getStore().getCount() == 0) {
			addInitRow();
		}
	}

	/**
	 * 提供透過API直接設定資料
	 * 
	 * @param list
	 */
	public abstract void setListItem(List<gkMap> list);

	/**
	 * 取得目前Grid所有Row
	 * 
	 * @return List
	 */
	public List getListItem() {
		if (grid.getSelectionModel() instanceof gkCheckBoxSelectionModel) {
			List storeList;
			// 若為true，則只取得已勾選的資料，反之則取得所有資料
			if (isGetSelectedItems) {
				storeList = grid.getSelectionModel().getSelectedItems();
			} else {
				storeList = grid.getStore().getModels();
			}
			int idx = 0;
			for (Iterator ite = storeList.iterator(); ite.hasNext();) {
				ModelData md = (ModelData) ite.next();
				md.set(INDEX, idx);
				idx++;
			}
			return storeList;
		}
		return getGrid().getStore().getModels();
	}

	/**
	 * 取得所有勾選row資料
	 * 
	 * @return Map
	 */
	public Map getSelectedRowItems() {
		List storeList = getGrid().getStore().getModels();
		Map selectedMap = new TreeMap(comparator);
		int idx = 0;
		for (Iterator ite = storeList.iterator(); ite.hasNext();) {
			ModelData md = (ModelData) ite.next();
			md.set(INDEX, idx);
			md.set(GK_INDEX, idx + "," + getView().getLastColIndex());
			if (getGrid().getSelectionModel().isSelected(md)) {
				gkMap map = new gkMap();
				map.putAll(md.getProperties());
				selectedMap.put(idx + "", map);
			}
			idx++;
		}
		return selectedMap;
	}

	/**
	 * 取得點選row資料
	 * 
	 * @return Map
	 */
	public Map getSelectedRowItem() {
		gkMap map = new gkMap();
		int lastRowIdx = getView().getLastRowIndex();
		ModelData md = getGrid().getStore().getAt(lastRowIdx);
		int idx = getGrid().getStore().indexOf(md);
		if (idx >= 0) {
			md.set(INDEX, idx);
			md.set(GK_INDEX, idx + "," + getView().getLastColIndex());
			map.putAll(md.getProperties());
		}
		return map;
	}

	public GridView getView() {
		return grid.getView();
	}

	public void setInvokeBean(String invokeBean) {
		this.invokeBean = invokeBean;
	}

	public String getInvokeBean() {
		return invokeBean;
	}

	public abstract ColumnModel createColumnModel();

	/**
	 * 建立滑鼠右鍵選單，將原本按左鍵會隱藏視窗功能Disable
	 * 
	 * @return Menu
	 */
	protected Menu createMenu() {
		return new Menu() {
			{
				sinkEvents(com.google.gwt.user.client.Event.KEYEVENTS);
			}

			@Override
			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
				if (event.getKeyCode() == KeyCodes.KEY_LEFT) {
					event.stopPropagation();
				} else {
					super.onBrowserEvent(event);
				}
			}
		};
	}

	public void addMenuItem(String txt, ImageResource imgRes, EventProcess ep) {
		addMenuItem(txt, AbstractImagePrototype.create(imgRes), ep);
	}

	/**
	 * 增加滑鼠右鍵選單項目
	 * 
	 * @param txt
	 * @param iconStyle
	 * @param ep
	 */
	public void addMenuItem(String txt, String iconStyle, EventProcess ep) {
		addMenuItem(txt, IconHelper.create(iconStyle), ep);
	}

	/**
	 * 增加滑鼠右鍵選單項目
	 * 
	 * @param txt
	 * @param imgIcon
	 * @param ep
	 */
	protected void addMenuItem(final String txt,
			AbstractImagePrototype imgIcon, final EventProcess ep) {
		if (getContextMenu() == null) {
			setContextMenu(createMenu());
		}
		Menu menu = getContextMenu();
		MenuItem item = new MenuItem();
		item.setText(txt);
		item.setIcon(imgIcon);
		item.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// 如果Tree沒有Item被點選，md將會是 null
				ModelData md = getGrid().getSelectionModel().getSelectedItem();
				gkMap m = new gkMap();
				if (md != null) {
					m.putAll(md.getProperties());
				}
				ep.execute(txt, new EventObject(txt, m));
			}
		});
		menu.add(item);
	}

	/**
	 * 增加CheckBox
	 * 
	 * @param checkBox
	 * @param autoSelect
	 */
	public void addCheckBox(boolean checkBox, boolean autoSelect) {
		gkCheckBoxSelectionModel sm = new gkCheckBoxSelectionModel();
		if (!autoSelect) {
			sm.setAutoSelect(false);
		}
		List columns = grid.getColumnModel().getColumns();
		columns.add(0, sm.getColumn());
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

		if (!checkBox) {
			grid.getColumnModel().getColumns().get(0).setHidden(true);
		}
	}

	public void refreshFooterData() {
		refreshFooterData(getView());
	}

	private native void refreshFooterData(GridView gv)/*-{
		gv.@com.extjs.gxt.ui.client.widget.grid.GridView::refreshFooterData()();
	}-*/;

	protected Grid createGrid(ListStore store, ColumnModel cm) {
		Grid grid = new EditorGrid(store, cm);
		// 複寫selectionModel。在grid裡當操作的cell為TextArea時，操作上下方向鍵不響應selectionModel的動作
		// 因為selectionModel的動作會失去cell的焦點
		grid.setSelectionModel(new GridSelectionModel<ModelData>() {
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

			@Override
			protected void onKeyPress(GridEvent<ModelData> e) {
				// 空白鍵do nothing
				if (!(e.getKeyCode() == 32)) {
					super.onKeyPress(e);
				}
			}
		});

		grid.getView().setRowSelectorDepth(30);// 設定尋找深度30，預設為10
		grid.getView().setCellSelectorDepth(18); // 設定尋找深度18，預設為6
		return grid;
	}

	/**
	 * 設定 SelectionMode，SIMPLE=多筆選項，SINGLE=單筆
	 * 
	 * @param selectionMode
	 */
	public void setSelectionMode(SelectionMode selectionMode) {
		getGrid().getSelectionModel().setSelectionMode(selectionMode);
	}

	/**
	 * 限定資料筆數
	 * 
	 * @param limitRecords
	 */
	public void setLimit(int limitRecords) {
		// 如果傳入的數字為正整數才傳遞給this.limitRecords做限制資料筆數使用
		if ((limitRecords + "").matches("^[1-9]\\d*$")) {
			this.limitRecords = limitRecords;
		}
	}

	/**
	 * 取得限定資料筆數
	 */
	public int getLimit() {
		return limitRecords;
	}

	/**
	 * 設定models為限定的資料筆數，如果總筆數小於所限定的資料筆數，models為資料總筆數
	 */
	protected void setLimitRecords() {
		if (limitRecords != 0) {
			models = models
					.subList(0, models.size() > limitRecords ? limitRecords
							: models.size());
		}
	}

	/**
	 * 是否自動增列
	 * 
	 * @return boolean
	 */
	public boolean isAutoNewRow() {
		return autoNewRow;
	}

	/**
	 * 設定是否自動增列
	 * 
	 * @param autoNewRow
	 */
	public void setAutoNewRow(boolean autoNewRow) {
		this.autoNewRow = autoNewRow;
	}

	/**
	 * 設定流水號
	 * 
	 * @param seqPos
	 */
	public void setSequence(int seqPos) {
		List columns = getGrid().getColumnModel().getColumns();
		if (seqPos > columns.size()) {
			seqPos = columns.size();
		} else if (seqPos < 0) {
			seqPos = 0;
		}
		RowNumberer rowNum = new RowNumberer();
		columns.add(seqPos, rowNum);
		getGrid().addPlugin(rowNum);
	}

	/**
	 * 設定可以拖拉調整資料順序，同時設定grid點選header不可以排序，因為排序後拖拉調整資料順序會不起作用
	 * 
	 * @param order
	 * @param feedback
	 */
	public void setReOrder(boolean order, String feedback) {
		if (order) {
			new GridDragSource(grid);
			GridDropTarget target = new GridDropTarget(grid);
			target.setAllowSelfAsSource(true);
			if (feedback.toLowerCase().equals("insert")) {
				target.setFeedback(Feedback.INSERT);
			} else if (feedback.toLowerCase().equals("append")) {
				target.setFeedback(Feedback.APPEND);
			} else {
				target.setFeedback(Feedback.BOTH);
			}
			// 如果reOrder = true ，則設定grid點選header不可以排序
			Iterator<ColumnConfig> it = grid.getColumnModel().getColumns()
					.iterator();
			while (it.hasNext()) {
				it.next().setSortable(false);
			}
		}
	}

	/**
	 * 新增一行邏輯，判別是否有限制筆數，是否限制增行
	 * 
	 * @param row
	 */
	public void createNewRow(String row) {
		int count = grid.getStore().getCount();
		boolean noLimit = getLimit() == 0 ? true : count < getLimit();
		int rowIndex = Integer.parseInt(row);
		if (count - 1 == rowIndex && noLimit) {
			if (isAutoNewRow()) {
				addRow();
			}
		}
	}

	/**
	 * 初始化時initRow設定 除了使用Timer等待GridView
	 * ready之外，還判斷是否已經有資料(元件init時可能會setInfo)，才initRow
	 */
	public void addInitRow() {
		if (!getGrid().isViewReady()) {
			new Timer() {

				@Override
				public void run() {
					if (getGrid().isViewReady()) {
						try {
							if (getGrid().getStore().getCount() == 0) {
								getGrid().getStore().add(new gkMap());
							}
						} finally {
							cancel();
						}
					}
				}
			}.scheduleRepeating(10);
		} else {
			getGrid().getStore().add(new gkMap());
		}
	}

	/**
	 * 增行方法
	 */
	public void addRow() {
		addRow(new gkMap());
	}

	/**
	 * 增行方法
	 * 
	 * @param map
	 */
	public void addRow(gkMap map) {
		gkMap newMap = (gkMap) gkMap.clone(map);
		if (limitRecords == 0
				|| (limitRecords != 0 && getGrid().getStore().getCount() < limitRecords)) {
			getListItem().add(newMap);
			addRowWhenGridViewIsReady(newMap);
		}
	}

	private void addRowWhenGridViewIsReady(final gkMap map) {
		if (!getGrid().isViewReady()) {
			new Timer() {

				@Override
				public void run() {
					if (getGrid().isViewReady()) {
						try {
							getGrid().getStore().add(map);
						} finally {
							cancel();
						}
					}
				}
			}.scheduleRepeating(10);
		} else {
			getGrid().getStore().add(map);
		}
	}

	/**
	 * 刪除行 如果有選定的行，刪除選定行，否則刪除最後一行
	 */
	public void removeRow() {
		if (!getGrid().getSelectionModel().getSelectedItems().isEmpty()) {
			List<ModelData> list = getGrid().getSelectionModel()
					.getSelectedItems();
			for (Iterator it = list.iterator(); it.hasNext();) {
				getGrid().getStore().remove((ModelData) it.next());
			}
			getGrid().getView().refresh(false);
		} else {
			getGrid().getStore().remove(getGrid().getStore().getCount() - 1);
			getGrid().getView().refresh(false);
		}
	}

	/**
	 * 通用grid監聽。在使用時自行加上
	 */
	public void addListener() {
		grid.addListener(Events.RowMouseDown, new Listener<GridEvent>() {

			@Override
			public void handleEvent(GridEvent ge) {
				onRow(ge);
			}
		});
		grid.addListener(Events.OnKeyPress, new Listener<GridEvent>() {

			@Override
			public void handleEvent(GridEvent ge) {
				int kc = ge.getKeyCode();
				// 在已經點選行的情況下才起作用
				// 當keypress為左括號(時，keycode與KEY_DOWN相同，因此會判斷錯誤
				if (grid.getSelectionModel() != null
						&& (kc == KeyCodes.KEY_UP || kc == KeyCodes.KEY_DOWN)
						&& !ge.getEvent().getShiftKey()) {
					ge.setModel(grid.getSelectionModel().getSelectedItem());
					int rowind = grid.getStore().indexOf(ge.getModel());
					ge.setRowIndex(rowind);
					ge.setColIndex(0);
					onRow(ge);
				}
			}
		});
		grid.addListener(Events.BeforeEdit, new Listener<GridEvent>() {

			@Override
			public void handleEvent(GridEvent ge) {
				int col = ge.getColIndex();
				final int row = ge.getRowIndex();
				ColumnModel cm = grid.getColumnModel();
				final CellEditor ed = cm.getEditor(col);

				// 在BeforeEdit更新rowIndex資料
				grid.getView().setLastRowIndex(row);

				ed.addListener(Events.BeforeComplete,
						new Listener<EditorEvent>() {
							@Override
							public void handleEvent(EditorEvent be) {
								ed.removeListener(Events.BeforeComplete, this);
								Object postProcessValue = be.getValue();
								Object startValue = be.getStartValue();
								Object startValueTrans = ed
										.postProcessValue(startValue);

								if (!postProcessValue.equals(startValueTrans)) {
									// 當值改變時，選取該row
									grid.getSelectionModel().select(row, true);
									if (GXT.isIE || GXT.isGecko) {
										// IE在進行到這裡前，rowIndex又被更新，所以必須再設定一次
										grid.getView().setLastRowIndex(row);
										if (be.getEditor() != null) {
											// 當為null時，表示是由cellColumnConfig
											// 那裡發布的事件，而不需再發布change事件。
											// 這裡發布change事件，由於使用tab操作時，在ie不會觸發browser
											// blur事件。
											ed.getField().fireEvent(
													Events.Change);
										}
									}
								}
							}
						});
			}
		});

		grid.addListener(Events.AfterEdit, new Listener<GridEvent>() {

			@Override
			public void handleEvent(GridEvent ge) {
				// 在編輯完後，將rowIndex更新成最後一筆選取資料
				GridView view = grid.getView();
				GridSelectionModel sm = grid.getSelectionModel();
				ModelData md = sm.getSelectedItem();
				int lastRowIndex = view.findRowIndex(view.getRow(md));
				view.setLastRowIndex(lastRowIndex);
			}
		});
	}

	/**
	 * 點選後發佈該事件
	 * 
	 * @param ge
	 */
	public void onRow(GridEvent<ModelData> ge) {
		GridView view = ge.getGrid().getView();

		int lastColIndex = ge.getColIndex();
		int lastRowIndex = view.findRowIndex(ge.getTarget());

		// 將最後點選的row更新到view裡面的 lastRowIndex.
		// 需要這樣是因為rowAttribute拿的selectItem拿不到資料，
		// 因為那個時候，GridView設定lastRowIndex還沒執行
		view.setLastRowIndex(lastRowIndex);
		view.setLastColIndex(lastColIndex);

		// colIndex=-1表示沒點中，不進行處理
		if (lastColIndex < 0) {
			return;
		}
		// 如果element是input或者是textarea時，在GridSelectionMode模式下不能選中。改為選中此行
		// 在CheckBoxSelectionModel模式下不需要運行此代碼段
		if (isInput(ge.getTarget())
				&& !(grid.getSelectionModel() instanceof gkCheckBoxSelectionModel)) {
			grid.getSelectionModel().select(ge.getRowIndex(), false);
		}

		// 自動增行判斷/在點擊每一行空白處時自動增行
		if (autoNewRow) {
			createNewRow(lastRowIndex + "");
		}
	}

	/**
	 * 實作設定勾選
	 * 
	 * @param selectItem
	 */
	public void setSelectRowItem(Collection selectItem) {
		GridSelectionModel<ModelData> sm = getGrid().getSelectionModel();
		sm.deselectAll();
		Iterator it = selectItem.iterator();
		while (it.hasNext()) {
			sm.select(Integer.parseInt(it.next() + ""), true);
		}
	}

	/**
	 * 給選中的行設定值，model裡不存在的值不設定
	 * 
	 * @param mapItem
	 */
	public void setSelectRowItem(Map mapItem) {
		ModelData md = getGrid().getSelectionModel().getSelectedItem();
		Iterator it = mapItem.keySet().iterator();
		while (it.hasNext() && md != null) {
			String key = it.next().toString();
			if (md.get(key) != null) {
				md.set(key, mapItem.get(key));
			}
		}
		grid.getStore().getLoader().load();
	}

	/**
	 * 判別Element是否是input或者是textarea
	 * 
	 * @param target
	 * @return boolean
	 */
	protected boolean isInput(Element target) {
		String tag = target.getTagName();
		return "input".equalsIgnoreCase(tag)
				|| "textarea".equalsIgnoreCase(tag);
	}

	/**
	 * 設定擴展行。效果為每行前增加'+'圖示，點擊此圖示會顯示擴展行
	 * 
	 * @param html
	 */
	public void setRowExpander(String html) {
		XTemplate tpl = XTemplate.create(html);
		RowExpander expander = new RowExpander();
		expander.setTemplate(tpl);

		ColumnModel cm = getGrid().getColumnModel();
		List columns = cm.getColumns();
		columns.add(0, expander);

		getGrid().addPlugin(expander);
	}

	/**
	 * 依傳入值，過濾store的資料
	 * 
	 * @param filterValue
	 */
	public void filter(String filterValue) {
		this.filterValue = filterValue;
		deferredFilter.delay(updateBuffer);
	};

	public void reload() {
		if (filterValue.length() == 0) {
			clearFilters();
		} else {
			clearFilters();
			StoreFilter filter = new StoreFilter() {
				@Override
				public boolean select(Store store, ModelData parent,
						ModelData item, String property) {

					boolean result = false;
					Collection data = ((gkMap) item).values();
					for (Iterator it = data.iterator(); it.hasNext();) {
						Object value = it.next();
						if (value == null) {
							continue;
						}
						result = value.toString().matches(property);
						if (result) {
							break;
						}
					}
					return result;
				}
			};
			getGrid().getStore().addFilter(filter);
			getGrid().getStore().applyFilters(filterValue);
		}
	}

	/**
	 * 清除store裡的所有filter
	 */
	public void clearFilters() {
		List filters = getGrid().getStore().getFilters();
		if (filters != null) {
			for (int i = 0; i < filters.size(); i++) {
				getGrid().getStore().removeFilter((StoreFilter) filters.get(i));
			}
		}
	}
}

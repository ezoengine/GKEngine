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

import jfreecode.gwt.event.client.bus.EventBus;
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
import com.extjs.gxt.ui.client.data.ListLoader;
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
	protected int limitRecords = 0; // 前端限定资料笔数
	protected boolean autoNewRow = false; // 设定是否自动增列
	protected String invokeBean = "";
	protected final static String INDEX = "idx"; // row Index
	protected final static String GK_INDEX = "_gk_idx"; // row Index
	protected String filterValue;
	protected int updateBuffer = 500; // 設定filter store的延遲時間
	// 定义Comparator，将TreeMap的key转为int再做比较，避免idx按照String型别排序产生错乱
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
		return getGrid().getStore().getModels();
	}

	@Override
	public void bindEvent() {
		// 為了相容先前沒習慣使用bindEvent方法註冊事件，所以override空實作
	}

	public static interface Event {
		public final static String SET_LIST_ITEM = ".setListItem";
		public final static String SET_ITEM = ".setItem";
		public final static String GET_LIST_ITEM = ".getListItem";
		public final static String SELECT_ROW = ".selectRow";
		public final static String REFRESH = ".refresh";
		public final static String TURN = ".turn";
		public final static String SET_BEAN = ".setBean";
		public final static String SET_INFO = ".setInfo";
	}

	public String evtSetListItem() {
		return getId() + Event.SET_LIST_ITEM;
	}

	public String evtSetItem() {
		return getId() + Event.SET_ITEM;
	}

	public String evtGetListItem() {
		return getId() + Event.GET_LIST_ITEM;
	}

	public String evtSelectRow() {
		return getId() + Event.SELECT_ROW;
	}

	public String evtRefresh() {
		return getId() + Event.REFRESH;
	}

	public String evtTurn() {
		return getId() + Event.TURN;
	}

	public String evtSetBean() {
		return getId() + Event.SET_BEAN;
	}

	public String eventId_setInfo() {
		return getId() + Event.SET_INFO;
	}

	public gkGridIC(String id) {
		setId(id);
		core = new CoreIC(this);
		core.init();
		setHeaderVisible(false);
	}

	public gkGridIC() {
		core = new CoreIC(this);
		core.init();
		setHeaderVisible(false);
	}

	public Grid getGrid() {
		return grid;
	}

	public void setHeaderStyle(String css) {
		headerStyle = css;
		// 如果已經render則不會觸發onRender，所以直接刷新header
		if (grid.isRendered()) {
			grid.getView().getHeader().refresh();
		}
	}

	/**
	 * <pre>
	 * 如果是List,表示要置換整個清單的資料 
	 * 如果是Map,表示要更新某筆資料
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
	}

	/**
	 * 提供透過API直接設定資料
	 */
	public abstract void setListItem(List<gkMap> list);

	/**
	 * 取得目前Grid所有Row
	 * 
	 * @return List
	 */
	public List getListItem() {
		return getGrid().getStore().getModels();
	}

	/**
	 * <pre>
	 * 取得所有勾选行资料
	 * 资料格式TreeMap<rowIdx,dejgMap<key, value>>，dejgMap包含rowIdx
	 * </pre>
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
	 * <pre>
	 * 取得点选row资料
	 * 资料格式dejgMap<key, value>，包含rowIdx（idx = int）
	 * </pre>
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

	/**
	 * 讀取資料，傳入EventObject
	 * 
	 * @param eo
	 */
	public void load(EventObject eo) {
		core.getBus().publishRemote(eo, new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				models = eo.getInfoList();
				// 增加限制资料笔数功能
				setLimitRecords();
				ListStore store_temp = grid.getStore();
				ListLoader load_temp = store_temp.getLoader();
				load_temp.load();
			}
		});
	}

	/**
	 * 讀取資料，傳入Map
	 * 
	 * @param info
	 */
	public void load(Map info) {

	}

	public void load(String eventId) {
		load(new EventObject(eventId));
	}

	public void load(String eventId, Map info) {
		load(new EventObject(eventId, info));
	}

	/**
	 * 接收其他component傳來的物件
	 */
	protected void createSubscribeEvent() {

	}

	/**
	 * 調用遠端Bean取得清單資料 此方法將立即調用後端Bean
	 * 
	 * @param bean
	 */
	public void invokeBean(String bean) {
		core.getBus().publishRemote(new EventObject(bean), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				core.getBus().publish(
						new EventObject(evtSetListItem(), eo.getInfoList()));
			}
		});
	}

	public void setInvokeBean(String bean) {
		invokeBean = bean;
	}

	public String getInvokeBean() {
		return invokeBean;
	}

	public void load() {
		invokeBean(invokeBean);
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
				if (event.getKeyCode() == 37) {
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
	 * @param iconStyle
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

	public void refreshFooterData() {
		refreshFooterData(getView());
	}

	private native void refreshFooterData(GridView gv)/*-{
		gv.@com.extjs.gxt.ui.client.widget.grid.GridView::refreshFooterData()();
	}-*/;

	protected Grid createGrid(ListStore store, ColumnModel cm) {
		Grid grid = new EditorGrid(store, cm);
		// 复写selectionModel。在grid里当操作的cell为TextArea时，操作上下方向键不响应selectionModel的动作
		// 因为selectionModel的动作会失去cell的焦点
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
				if (e.getKeyCode() == 32) { // 空白鍵
					// do nothing
				} else {
					super.onKeyPress(e);
				}
			}
		});

		grid.getView().setRowSelectorDepth(30);// 设定寻找深度为30.默认为10
		grid.getView().setCellSelectorDepth(18); // 設定尋找深度18.預設為6
		return grid;
	}

	/**
	 * 取得指定的值，第几栏
	 * 
	 * @param row
	 * @param column
	 * @return String
	 */
	public String getColumnValue(int row, int column) {
		ColumnModel cm = grid.getColumnModel();
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
	 * 設定 SelectionMode.SIMPLE 多筆選項,SINGLE单笔
	 * 
	 * @param selectionMode
	 */
	public void setSelectionMode(SelectionMode selectionMode) {
		getGrid().getSelectionModel().setSelectionMode(selectionMode);
	}

	/**
	 * 限定资料笔数
	 * 
	 * @param limitRecords
	 */
	public void setLimit(int limitRecords) {
		// 如果传入的数字为正整数才传递给this.limitRecords做限制资料笔数使用
		if ((limitRecords + "").matches("^[1-9]\\d*$")) {
			this.limitRecords = limitRecords;
		}
	}

	/**
	 * 取得限定资料笔数
	 */
	public int getLimit() {
		return this.limitRecords;
	}

	/**
	 * 设定models为限定的资料笔数，如果总笔数小于所限定的资料笔数，models为资料总笔数
	 */
	protected void setLimitRecords() {
		if (limitRecords != 0) {
			models = models
					.subList(0, models.size() > limitRecords ? limitRecords
							: models.size());
		}
	}

	/**
	 * 取得是否自动增列
	 * 
	 * @return boolean
	 */
	public boolean getAutoNewRow() {
		return autoNewRow;
	}

	/**
	 * 设定是否自动增列
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
	 * 设定可以拖拉调整资料顺序，同時設定grid點選header不可以排序 因為排序后拖拉調整資料順序會不起作用
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
	 * 新增一行逻辑 判别是否有限制比数，是否限制增行
	 * 
	 * @param row
	 */
	public void createNewRow(String row) {
		boolean notLimit = true;
		if (this instanceof gkPageGridIC) {
			notLimit = getLimit() == 0 ? true : ((gkPageGridIC) this)
					.getTotalSize() < getLimit() ? true : false;
		} else {
			notLimit = getLimit() == 0 ? true
					: grid.getStore().getCount() < getLimit();
		}
		int rowIndex = Integer.parseInt(row);
		if (grid.getStore().getCount() - 1 == rowIndex && notLimit) {
			if (getLimit() != 0 && this instanceof gkPageGridIC) {
				((gkPageGridIC) this).setTotalSize(((gkPageGridIC) this)
						.getTotalSize() + 1);
			}
			if (getAutoNewRow()) {
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
	 * 通用grid监听。在使用时自行加上
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
				// 在已经点选行的情况下才起作用
				if (grid.getSelectionModel() != null
						&& (kc == KeyCodes.KEY_UP || kc == KeyCodes.KEY_DOWN)) {
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
			public void handleEvent(final GridEvent ge) {
				final int col = ge.getColIndex();
				final int row = ge.getRowIndex();
				final ColumnModel cm = grid.getColumnModel();
				final CellEditor ed = cm.getEditor(col);

				// 在BeforeEdit更新rowIndex資料
				GridView view = grid.getView();
				view.setLastRowIndex(row);

				ed.addListener(Events.BeforeComplete,
						new Listener<EditorEvent>() {
							@Override
							public void handleEvent(EditorEvent be) {
								ed.removeListener(Events.BeforeComplete, this);
								Object postProcessValue = be.getValue();
								Object startValue = be.getStartValue();
								GridView view = grid.getView();
								GridSelectionModel sm = grid
										.getSelectionModel();

								Object startValueTrans = ed
										.postProcessValue(startValue);

								if (!postProcessValue.equals(startValueTrans)) {
									// 當值改變時，選取該row
									sm.select(row, true);

									if (GXT.isIE || GXT.isGecko) {
										// IE在進行到這裡前，rowIndex又被更新，所以必須再設定一次
										view.setLastRowIndex(row);
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
		// 如果element是input或者是textarea时，在GridselectionMode模式下不能选中。改为选中此行
		// 在gkMultiEditorGridIC模式下不需要运行此代码段
		if (isInput(ge.getTarget())
				&& !(getParent() instanceof gkMultiEditorGridIC)) {
			grid.getSelectionModel().select(ge.getRowIndex(), false);
		}
		Map info = new gkMap();
		info.put("rowIndex", "" + lastColIndex);
		info.put("colIndex", "" + lastRowIndex);
		info.put("data", ge.getModel().getProperties());
		EventBus.get().publish(new EventObject(evtSelectRow(), info));
		// 自动增行判断/在点击每一行空白处时自动增行
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
	 * 给选中的行设定值 model里不存在的值不设定
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
	 * 判别Element是否是input或者是textarea
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
	 * 设定扩展行。效果为每行前增加'+'图标，点击此图标会显示扩展行
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
	 * @param value
	 */
	public void filter(String value) {
		this.filterValue = value;
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
	 * 
	 */
	public void clearFilters() {
		List filters = getGrid().getStore().getFilters();
		if (filters != null) {
			for (int i = 0; i < filters.size(); i++) {
				getGrid().getStore().removeFilter((StoreFilter) filters.get(i));
			}
		}
	}

	// 初始是否新增一空白列
	private boolean initBlankRow = false;

	public void setInitBlankRow(boolean initBlankRow) {
		this.initBlankRow = initBlankRow;
	}

	public boolean isInitBlankRow() {
		return initBlankRow;
	}
}

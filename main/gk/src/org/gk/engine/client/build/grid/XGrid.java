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
package org.gk.engine.client.build.grid;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventObject;

import org.gk.engine.client.build.panel.XContentPanel;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.event.EventListener;
import org.gk.engine.client.event.IEventConstants;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.engine.client.utils.StringUtils;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.gkGridIC;
import org.gk.ui.client.com.grid.gkListGridIC;
import org.gk.ui.client.com.grid.gkMultiEditorGridIC;
import org.gk.ui.client.com.grid.gkPageGridIC;
import org.gk.ui.client.com.grid.gkPageGridIC.BarPosition;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.AggregationRowConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Node;

/**
 * Grid清單
 * 
 * <pre>
 * XGrid裡面放XGridField
 * </pre>
 * 
 * @author I21890 2010/02/08
 * @since 2010/7/26
 */
public class XGrid extends XContentPanel {

	private final static String MODE = "single|simple|multi";
	// 正整数的正则表达式
	private final static String POSITIVE_INTEGER = "^[1-9]\\d*$";

	protected String adjustForHScroll, autoNewRow, columnHeaderVisible;
	protected String feedback, limit, operation;
	protected String selMode, stripe, xTemplate;
	protected String page, pageSize;
	protected String checkBox, initRow, autoSelect;
	protected String seqPosition;
	protected String dragSource, dropTarget;
	protected String onRow;

	public XGrid(Node node, List<XGridField> widgets) {
		super(node, widgets);
		height = super.getAttribute("height", "300");
		layout = super.getAttribute("layout", "fitlayout");

		// 共通屬性
		adjustForHScroll = super.getAttribute("adjustForHScroll", "true");
		autoNewRow = super.getAttribute("autoNewRow", "false");
		columnHeaderVisible = super.getAttribute("columnHeaderVisible", "true");
		feedback = super.getAttribute("feedback", "insert");
		limit = super.getAttribute("limit", "");
		operation = super.getAttribute("operation", "move");
		selMode = super.getAttribute("selMode", "");
		stripe = super.getAttribute("stripe", "true");
		xTemplate = super.getAttribute("xTemplate", "");
		seqPosition = super.getAttribute("seqPosition", "");
		// 分頁Grid屬性
		page = super.getAttribute("page", "false");
		pageSize = super.getAttribute("pageSize", "");
		// 多筆編輯Grid屬性
		checkBox = super.getAttribute("checkBox", "");
		initRow = super.getAttribute("initRow", "");
		autoSelect = super.getAttribute("autoSelect", "true");
		// 事件屬性
		dragSource = super.getAttribute("drag", "false");
		dropTarget = super.getAttribute("drop", "false");
		onRow = super.getAttribute("onRow", "");
	}

	public String getAdjustForHScroll() {
		return adjustForHScroll;
	}

	public String getAutoNewRow() {
		return autoNewRow;
	}

	public String getColumnHeaderVisible() {
		return columnHeaderVisible;
	}

	public String getFeedback() {
		return feedback;
	}

	public String getLimit() {
		return limit;
	}

	public String getOperation() {
		return operation;
	}

	public String getSelMode() {
		return selMode;
	}

	public String getStripe() {
		return stripe;
	}

	public String getxTemplate() {
		return xTemplate;
	}

	public String getPage() {
		return page;
	}

	public boolean isPage() {
		return Boolean.parseBoolean(page) || page.matches("top|bottom");
	}

	public String getPageSize() {
		return pageSize;
	}

	public String getCheckBox() {
		return checkBox;
	}

	public String getInitRow() {
		return initRow;
	}

	public String getAutoSelect() {
		return autoSelect;
	}

	public String getDragSource() {
		return dragSource;
	}

	public String getDropTarget() {
		return dropTarget;
	}

	public String getOnRow() {
		return onRow;
	}

	@Override
	public void init() {
		if (isPage() && init.startsWith(IEventConstants.HANDLER_BEAN)) {
			gkGridIC grid = (gkGridIC) super.getComponent();
			if (grid != null) {
				grid.setInvokeBean(init);
				init = appendPageBar(init);
			}
		}
		super.init();
	}

	@Override
	public void onInfo(String eventId, String content) {
		EventObject eo = StringUtils.toEventObject(eventId, content);
		bus.publish(new EventObject(id + gkGridIC.Event.SET_LIST_ITEM, eo
				.getInfo()));
	}

	@Override
	public Component build() {
		List fields = new gkList();
		// HeaderGroup
		List header = new gkList();
		// AggregationRow
		List aggRow = new gkList();
		Iterator<UIGen> it = widgets.iterator();
		// grid可放ColumnConfig或HeaderGroupConfig或AggregationRowConfig
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			Object obj = com.getData("columnConfig");
			if (obj instanceof ColumnConfig) {
				fields.add(obj);
			} else if (obj instanceof HeaderGroupConfig) {
				header.add(obj);
			} else if (obj instanceof AggregationRowConfig) {
				aggRow.add(obj);
			}
		}
		// 如果是編輯模式，id改為innerGrid_$id
		gkGridIC grid = createGridIC(
				type.equals("edit") || checkBox.equals("true") ? "innerGrid_"
						+ id : id, fields, header);
		final gkGridIC g = grid;

		// 設定是否要隱藏 ColumnHeader。
		// 改用监听Grid的render事件，因建構階段還取不到ColumnHeader
		grid.getGrid().addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				g.getView().getHeader()
						.setVisible(Boolean.parseBoolean(columnHeaderVisible));
			}
		});

		// 是否新增Aggregation Row
		if (!aggRow.isEmpty()) {
			attachAggregationRow(aggRow, grid);
		}
		// 设定是否自动增行
		if (Boolean.parseBoolean(autoNewRow)) {
			grid.setAutoNewRow(true);
		}
		// 设定限制资料笔数
		if (limit.matches(POSITIVE_INTEGER)) {
			grid.setLimit(Integer.parseInt(limit));
		}
		// 对pageGrid设定每页显示资料笔数pageSize
		if (grid instanceof gkPageGridIC) {
			if (pageSize.matches(IRegExpUtils.POSITIVE_INTEGER)) {
				((gkPageGridIC) grid).setPageSize(Integer.parseInt(pageSize));
			}
		}
		// 设定Expander Row，若设定如果在放在new gkMultiEditorGridIC之前，
		// 则"+"符号在checkbox勾选框之后，反之其后
		if (!xTemplate.equals("")) {
			grid.setRowExpander(xTemplate);
		}

		// 設定init時是否需要新增一空白行 "沿用舊的用法 type='edit'時預設要有initrow，但又可能會設定不要initRow"
		if (type.equals("edit")) {
			grid.setInitBlankRow(true);
		} else {
			grid.setInitBlankRow(false);
		}

		if (!initRow.equals("")) {
			grid.setInitBlankRow(Boolean.parseBoolean(initRow));
		}

		if (type.equals("edit") || checkBox.equals("true")) {
			gkMultiEditorGridIC mgrid = new gkMultiEditorGridIC(id, grid);
			// 設定是否顯示checkBox
			if (!checkBox.equals("")) {
				mgrid.setCheckBox(Boolean.parseBoolean(checkBox));
			}
			if (!autoSelect.equals("")) {
				mgrid.setAutoSelect(Boolean.parseBoolean(autoSelect));
			}
			grid = mgrid;
		}

		// 是否新增流水號
		if (seqPosition.matches("\\d+")) {
			grid.setSequence(Integer.parseInt(seqPosition));
		}
		// 垂直scrollBar是否自动出现
		grid.getView().setAdjustForHScroll(
				Boolean.parseBoolean(adjustForHScroll));
		// 设定SelectionMode
		if (selMode.matches(MODE)) {
			grid.setSelectionMode(SelectionMode.valueOf(selMode.toUpperCase()));
		}
		// 設定每筆Row是否要用顏色區分
		grid.getGrid().setStripeRows(Boolean.parseBoolean(stripe));
		// 如果onRow有設定事件指令，當使用者點選清單中某筆資料時會觸發此事件
		addEventListener(grid.getGrid(), Events.RowMouseDown, onRow);
		// 按上下方向键触发onRow事件
		addKeyListener(grid);
		grid.getView().setForceFit(true);
		initComponent(grid);

		return grid;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		gkGridIC gridIC = (gkGridIC) com;
		// 判断设定grid是否为dragSource
		if (Boolean.parseBoolean(dragSource)) {
			final EventListener evtListener = new EventListener(gridIC.getId(),
					onDrag, XGrid.this);
			new GridDragSource(gridIC.getGrid()) {

				@Override
				protected void onDragDrop(DNDEvent e) {
					String dragSrcComId = e.getDragSource().getComponent()
							.getId()
							+ "";
					String dropTrgComId = e.getDropTarget().getComponent()
							.getId()
							+ "";
					// 判断如果dragSource和dropTarget是同一Component，则设定operation=move
					// 否则依照gul语法中的参数来设定参数值
					if (dragSrcComId.equals(dropTrgComId)) {
						e.setOperation(Operation.MOVE);
					} else {
						e.setOperation(Operation.valueOf(XGrid.this.operation
								.toUpperCase()));
					}
					// 这里做延时处理是因为onDrop定义的事件有可能阻断拖放的完成，如：show:grid
					// 拿掉延時測看看
					final DNDEvent event = e;
					if (!onDrop.equals("")) {
						Scheduler.get().scheduleDeferred(
								new ScheduledCommand() {
									@Override
									public void execute() {
										evtListener.handleEvent(event);
										callSuperOnDragDrop(event);
									}
								});
					} else {
						callSuperOnDragDrop(event);
					}
				}

				private void callSuperOnDragDrop(DNDEvent e) {
					super.onDragDrop(e);
				}
			};
		}
		// 判断设定grid是否为dropTarget
		if (Boolean.parseBoolean(dropTarget)) {
			final EventListener evtListener = new EventListener(gridIC.getId(),
					onDrop, XGrid.this);
			final EventListener evtListener2 = new EventListener(
					gridIC.getId(), onAfterDrop, XGrid.this);
			GridDropTarget target = new GridDropTarget(gridIC.getGrid()) {

				@Override
				protected void onDragDrop(DNDEvent e) {
					Object obj = e.getData();
					// 复制一份新的data，然后设定给DNDEvent，避免operation=copy时rowIndex计算错误
					if (XGrid.this.operation.toUpperCase().equals("COPY")) {
						if (obj instanceof List) {
							List<ModelData> models = new gkList<ModelData>();
							List list = (List) obj;
							for (Iterator iterator = list.iterator(); iterator
									.hasNext();) {
								ModelData md = (ModelData) iterator.next();
								gkMap map = new gkMap(md.getProperties());
								models.add(map);
							}
							e.setData(models);
						}
					}
					final DNDEvent event = e;
					// 这里做延时处理是因为onDrop定义的事件有可能阻断拖放的完成，如：show:grid
					if (!onDrop.equals("")) {
						Scheduler.get().scheduleDeferred(
								new ScheduledCommand() {
									@Override
									public void execute() {
										evtListener.handleEvent(event);
										callSuperOnDragDrop(event);
									}
								});
					} else if (onAfterDrop.equals("")) {
						callSuperOnDragDrop(event);
					} else {
						Scheduler.get().scheduleDeferred(
								new ScheduledCommand() {
									@Override
									public void execute() {
										evtListener2.handleEvent(event);
										callSuperOnDragDrop(event);
									}
								});
					}
				}

				private void callSuperOnDragDrop(DNDEvent e) {
					super.onDragDrop(e);
				}
			};
			target.setAllowSelfAsSource(Boolean.parseBoolean(dragSource)
					&& Boolean.parseBoolean(dropTarget));
			target.setFeedback(Feedback.valueOf(feedback.toUpperCase()));
			target.setAutoScroll(false);
		}
		// 初始時判斷是否需要initRow
		if (gridIC.isInitBlankRow()
				&& gridIC.getGrid().getStore().getCount() == 0) {
			gridIC.addInitRow();
		}

	}

	/**
	 * 根據page屬性決定建立的Grid是不是分頁Grid
	 * 
	 * @param id
	 * @param fields
	 * @param header
	 * @return gkGridIC
	 */
	private gkGridIC createGridIC(String id, final List fields,
			final List header) {
		gkGridIC grid;
		// 若page為true、top或bottom，則產生分頁Grid，若為false，則產生不分頁Grid
		if (isPage()) {
			grid = new gkPageGridIC(BarPosition.valueOf(page.toUpperCase())) {

				@Override
				public ColumnModel createColumnModel() {
					ColumnModel cm = new ColumnModel(fields);
					attachHeaderGroup(header, cm);
					return cm;
				}

				@Override
				protected void load(Object loadConfig, AsyncCallback callback) {
					if (isServerPaging()) {
						BasePagingLoadConfig config = (BasePagingLoadConfig) loadConfig;
						BasePagingLoadResult result;
						List datas = null;
						int total = 0;
						if (isLoadingPage()) {
							setPageOffset(config.getOffset());
							result = new BasePagingLoadResult(datas,
									config.getOffset(), total);
							EventCenter.exec(getId(),
									appendPageBar(getInvokeBean()), XGrid.this,
									null);
						} else {
							Map info = core.getInfo();
							total = (Integer) info.get(TOTALSIZE);
							datas = (List) info.get(DATA);
							result = new BasePagingLoadResult(datas,
									config.getOffset(), total);
							setLoadingPage(true);
						}
						callback.onSuccess(result);
					} else {
						super.load(loadConfig, callback);
					}
				}
			};
		} else {
			grid = new gkListGridIC(id) {

				@Override
				public ColumnModel createColumnModel() {
					ColumnModel cm = new ColumnModel(fields);
					attachHeaderGroup(header, cm);
					return cm;
				}
			};
		}
		return grid;
	}

	/**
	 * 增加HeaderGroup
	 * 
	 * @param header
	 * @param cm
	 */
	private void attachHeaderGroup(List header, ColumnModel cm) {
		for (Iterator<HeaderGroupConfig> it = header.iterator(); it.hasNext();) {
			HeaderGroupConfig config = it.next();
			cm.addHeaderGroup(config.getRow(), config.getColumn(), config);
		}
	}

	/**
	 * 增加AggregationRow
	 * 
	 * @param aggRow
	 * @param grid
	 */
	private void attachAggregationRow(List aggRow, gkGridIC grid) {
		for (Iterator<AggregationRowConfig> it = aggRow.iterator(); it
				.hasNext();) {
			AggregationRowConfig config = it.next();
			grid.getGrid().getColumnModel().addAggregationRow(config);
		}
	}

	/**
	 * 按上下方向键触发onRow事件
	 * 
	 * @param grid
	 */
	private void addKeyListener(final gkGridIC grid) {
		if (onRow.equals("")) {
			return;
		}
		final XGrid xGrid = this;
		grid.getGrid().addListener(Events.OnKeyUp, new Listener<GridEvent>() {

			@Override
			public void handleEvent(GridEvent ge) {
				int kc = ge.getKeyCode();
				if (grid.getGrid().getSelectionModel() != null
						&& (kc == KeyCodes.KEY_UP || kc == KeyCodes.KEY_DOWN)) {
					EventCenter.exec(getId(), onRow, xGrid, ge);
				}
			}
		});
	}

	/**
	 * 若為後端分頁的話，則需附加PageBar的資訊(limit與offset)
	 * 
	 * @param bean
	 * @return String
	 */
	private String appendPageBar(String bean) {
		StringBuffer append = new StringBuffer(bean);
		String[] colon = bean.split(IEventConstants.SPLIT_COLON);
		if (colon.length == 3) {
			append.append(IEventConstants.SPLIT_COMMA).append(id);
			append.append(".").append(IEventConstants.ATTRIB_PAGEBAR);
		} else if (colon.length == 2) {
			append.append(IEventConstants.SPLIT_COLON);
			append.append(id).append(".");
			append.append(IEventConstants.ATTRIB_PAGEBAR);
		}
		return append.toString();
	}
}

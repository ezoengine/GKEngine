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

import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author I21890
 * @since 2009/07/02
 */
public abstract class gkListGridIC extends gkGridIC {

	public gkListGridIC(String id) {
		super(id);
		init();
	}

	public gkListGridIC() {
		super();
		init();
	}

	protected void init() {
		loader = createDataLoader();
		store = new ListStore(loader);
		ColumnModel cm = createColumnModel();
		grid = createGrid(store, cm);
		grid.setAutoWidth(true);
		setLayout(new FitLayout());
		add(grid);
		// 註冊點選row的事件
		addListener();
		createSubscribeEvent();
	}

	@Override
	public void setListItem(final List<gkMap> list) {
		models = list;
		setLimitRecords();
		getGrid().getStore().removeAll();
		getGrid().getStore().add(models);
	}

	@Override
	protected void createSubscribeEvent() {
		// 訂閱設定grid所有資料事件
		core.subscribe(evtSetListItem(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				setListItem(eo.getInfoList());
			}
		});
		// 訂閱設定grid所有資料事件
		core.subscribe(evtSetItem(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				gkMap mp = new gkMap(eo.getInfoMap());
				int rowIndex = Integer.parseInt(mp.get("rowIndex") + "");
				models.get(rowIndex).putAll((gkMap) mp.get("rowData"));
				gkListGridIC.this.grid.getStore().getLoader().load();
			}
		});
		// 訂閱取得grid所有資料事件
		core.subscribe(evtGetListItem(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				// 放到infoList,讓源頭取得
				eo.getInfoList().addAll(models);
			}
		});
		// 更新畫面(不重新讀取資料，以store中既有資料更新)
		core.subscribe(evtRefresh(), new EventProcess() {

			@Override
			public void execute(String eventId, EventObject eo) {
				grid.getView().refresh(true);
			}
		});
	}

	protected BaseListLoader createDataLoader() {
		RpcProxy proxy = new RpcProxy() {

			@Override
			public void load(Object loadConfig, final AsyncCallback callback) {
				// 增加限制资料笔数功能
				setLimitRecords();
				callback.onSuccess(new BaseListLoadResult(models));
			}
		};
		return new BaseListLoader(proxy);
	}

	@Override
	public void load(String eventId, Map info) {
		load(new EventObject(eventId, info));
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}
}
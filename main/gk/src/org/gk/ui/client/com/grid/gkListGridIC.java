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

import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 清單Grid
 * 
 * @author I21890
 * @since 2009/07/02
 */
public abstract class gkListGridIC extends gkGridIC {

	public gkListGridIC() {
		init();
	}

	protected void init() {
		setLayout(new FitLayout());

		loader = createDataLoader();
		store = new ListStore(loader);
		grid = createGrid(store, createColumnModel());
		grid.setAutoWidth(true);

		add(grid);
		// 註冊點選row的事件
		addListener();
	}

	@Override
	public void setListItem(List<gkMap> list) {
		models = list;
		setLimitRecords();
		getGrid().getStore().removeAll();
		getGrid().getStore().add(models);
	}

	protected BaseListLoader createDataLoader() {
		RpcProxy proxy = new RpcProxy() {

			@Override
			public void load(Object loadConfig, AsyncCallback callback) {
				// 增加限制資料筆數功能
				setLimitRecords();
				callback.onSuccess(new BaseListLoadResult(models));
			}
		};
		return new BaseListLoader(proxy);
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}
}
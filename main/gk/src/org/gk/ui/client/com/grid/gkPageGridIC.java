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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.toolbar.gkPageSizePlugin;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class gkPageGridIC extends gkGridIC {

	protected final static String DATA = "data";
	protected final static String TOTALSIZE = "totalSize";
	protected final static String OFFSET = "offset";
	protected final static String LIMIT = "pageSize";

	private final static int PAGESIZE = 10;

	protected PagingToolBar toolbar;
	protected BasePagingLoader pageLoader;

	protected int totalSize;

	protected boolean serverPaging;
	protected boolean loadingPage;

	private BarPosition barPosition;

	/**
	 * 分頁工具列的位置
	 */
	public enum BarPosition {
		TOP, BOTTOM, TRUE
	}

	public gkPageGridIC(BarPosition barPosition) {
		this.barPosition = barPosition;
		init();
	}

	protected void init() {
		setLayout(new FitLayout());

		pageLoader = createDataLoader();
		store = new ListStore(pageLoader);
		grid = createGrid(store, createColumnModel());

		toolbar = new PagingToolBar(PAGESIZE);
		toolbar.addPlugin(new gkPageSizePlugin());
		toolbar.bind(pageLoader);

		add(grid);
		if (barPosition == BarPosition.TOP) {
			setTopComponent(toolbar);
		} else {
			setBottomComponent(toolbar);
		}
		// 註冊點選row的事件
		addListener();
	}

	@Override
	public void bindEvent() {

	}

	@Override
	public void createNewRow(String row) {
		boolean noLimit = getLimit() == 0 ? true : getTotalSize() < getLimit();
		int rowIndex = Integer.parseInt(row);
		if (grid.getStore().getCount() - 1 == rowIndex && noLimit) {
			if (getLimit() != 0) {
				setTotalSize(getTotalSize() + 1);
			}
			if (isAutoNewRow()) {
				addRow();
			}
		}
	}

	@Override
	protected void onResize(int width, int height) {
		String hideMode = getHideMode().value();
		// 元件不可見且有設定hideMode的狀況下，會影響尺寸的設定，因此先暫時移除hideMode，重設尺寸後再加回
		if (!isVisible() && getStyleName().indexOf(hideMode) != -1) {
			removeStyleName(hideMode);
			super.onResize(width, height);
			addStyleName(hideMode);
		} else {
			super.onResize(width, height);
		}
	}

	@Override
	public void setInfo(Object info) {
		if (info instanceof String) {
			setInvokeBean((String) info);
			serverPaging = true;
			loadingPage = true;
			pageLoader.load(0, toolbar.getPageSize());
		} else if (info instanceof List) {
			serverPaging = false;
			setListItem((List) info);
		} else if (info instanceof Map) {
			Map data = (Map) info;
			if (data.containsKey(DATA) && data.containsKey(TOTALSIZE)) {
				core.setInfo(data);
				serverPaging = true;
				loadingPage = false;
				store.getLoader().load();
			} else {
				// key是rowIdx，value是該筆資料
				// 例如：{"0":{"field1":"11", "field2":"22"}}
				Map map = new TreeMap(comparator);
				map.putAll(data);
				List lists = new gkList();
				Iterator it = map.keySet().iterator();
				while (it.hasNext()) {
					Object key = it.next();
					gkMap value = (gkMap) map.get(key);
					value.put(INDEX, key);
					lists.add(value);
				}
				serverPaging = false;
				setListItem(lists);
			}
		}
	}

	public int getPageOffset() {
		return pageLoader.getOffset();
	}

	public void setPageOffset(int offset) {
		pageLoader.setOffset(offset);
	}

	public int getPageSize() {
		return toolbar.getPageSize();
	}

	public void setPageSize(int pageSize) {
		toolbar.setPageSize(pageSize);
	}

	public BasePagingLoader getPageLoader() {
		return pageLoader;
	}

	public PagingToolBar getPagingToolBar() {
		return toolbar;
	}

	/**
	 * 取得要送到後端的分頁資訊
	 * 
	 * @return Map
	 */
	public Map getPageInfo() {
		return new gkMap(OFFSET, pageLoader.getOffset()).add(LIMIT,
				toolbar.getPageSize());
	}

	protected BasePagingLoader createDataLoader() {
		RpcProxy proxy = new RpcProxy() {

			@Override
			protected void load(Object loadConfig, AsyncCallback callback) {
				gkPageGridIC.this.load(loadConfig, callback);
			}
		};

		BasePagingLoader loader = new BasePagingLoader(proxy);
		return loader;
	}

	protected void load(Object loadConfig, AsyncCallback callback) {
		BasePagingLoadConfig config = (BasePagingLoadConfig) loadConfig;
		int limit = config.getLimit();
		int offset = config.getOffset();
		int total = models.size();
		List datas = new gkList();
		for (int i = offset; i < offset + limit && i < total; i++) {
			datas.add(models.get(i));
		}
		BasePagingLoadResult result = new BasePagingLoadResult(datas, offset,
				total);
		callback.onSuccess(result);
	}

	@Override
	public void setListItem(List<gkMap> list) {
		models = list;
		pageLoader.load(0, toolbar.getPageSize());
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}

	/**
	 * 是否後端分頁(預設為false)
	 * 
	 * @return boolean
	 */
	public boolean isServerPaging() {
		return serverPaging;
	}

	/**
	 * 設定是否後端分頁
	 * 
	 * @param serverPaging
	 */
	public void setServerPaging(boolean serverPaging) {
		this.serverPaging = serverPaging;
	}

	/**
	 * 是否根據invokeBean，至後端讀取分頁資料(預設為false)
	 * 
	 * @return boolean
	 */
	public boolean isLoadingPage() {
		return loadingPage;
	}

	/**
	 * 設定是否置後端讀取分頁資料
	 * 
	 * @param loadingPage
	 */
	public void setLoadingPage(boolean loadingPage) {
		this.loadingPage = loadingPage;
	}

	/**
	 * 取得totalSize
	 * 
	 * @return int
	 */
	public int getTotalSize() {
		return totalSize;
	}

	/**
	 * 设定totalSize
	 * 
	 * @param totalSize
	 */
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
}
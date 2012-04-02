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
package org.gk.ui.client.com.tree.dir;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class gkTreeDirPanelIC<M extends ModelData> extends TreePanel<M>
		implements IC {
	protected CoreIC core;
	public static final String PATH = "path";
	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String PARENT_NODE = "parentNode",
			NODE_DATA = "nodeData";
	protected String rootName = "ROOT";

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	protected String beanId = "fileTreeBean";
	private StoreListener<M> storeListener = new StoreListener<M>() {
		@Override
		public void storeAdd(StoreEvent<M> se) {
			onAdd((TreeStoreEvent<M>) se);
		}

		@Override
		public void storeClear(StoreEvent<M> se) {
			onClear((TreeStoreEvent<M>) se);
		}

		@Override
		public void storeDataChanged(StoreEvent<M> se) {
			onDataChanged((TreeStoreEvent<M>) se);
		}

		@Override
		public void storeFilter(StoreEvent<M> se) {
			onFilter((TreeStoreEvent<M>) se);
		}

		@Override
		public void storeRemove(StoreEvent<M> se) {
			onRemove((TreeStoreEvent<M>) se);
		}

		@Override
		public void storeUpdate(StoreEvent<M> se) {
			onUpdate((TreeStoreEvent<M>) se);
		}
	};

	public gkTreeDirPanelIC(String id) {
		super(new TreeStore());
		setDisplayProperty("name");
		setCaching(false);
	}

	public void setBean(String beanId) {
		this.beanId = beanId;
		this.store = new TreeStore<M>(new BaseTreeLoader<M>(createRPCProxy()) {

			@Override
			public boolean hasChildren(M parent) {
				return true;
			};
		});

		this.loader = store.getLoader();
		store.removeAllListeners();
		store.addStoreListener(storeListener);

		store.setKeyProvider(new ModelKeyProvider<M>() {

			@Override
			public String getKey(ModelData model) {

				if (!model.getProperties().containsKey(PATH)) {
					model.set(ID, DOM.createUniqueId());
				} else {
					model.set(ID, model.get(PATH) + "");
				}
				return model.get(ID) + "";
			}
		});

		view.bind(this, store);
		loader.load();
	}

	private RpcProxy createRPCProxy() {
		return new RpcProxy() {
			@Override
			protected void load(final Object loadConfig,
					final AsyncCallback callback) {
				ModelData btm = ((ModelData) loadConfig);
				// 取得當下要展開節點的路徑
				if (btm == null) {

					// 由使用者設定rootPath路徑資料
					String rootPath = getData("_gk_data");
					rootPath = rootPath == null || rootPath.equals("") ? "./"
							: rootPath;
					Map info = new gkMap("name", getRootName()).add("path",
							rootPath);
					callback.onSuccess(new gkList(info));

				} else {
					String path = btm.getProperties().get(PATH) + "";
					EventBus.get().publishRemote(new EventObject(beanId, path),
							new EventProcess() {
								@Override
								public void execute(String eventId,
										EventObject eo) {
									callback.onSuccess(eo.getInfoList());
								}
							});
				}
			}
		};
	}

	@Override
	protected void onClick(TreePanelEvent tpe) {
		if (tpe.getNode() == null)
			return;
		super.onClick(tpe);
	}

	@Override
	public CoreIC core() {
		return core;
	}

	@Override
	public void bindEvent() {
	}

	@Override
	public void setInfo(Object info) {
		if (info instanceof String) {
			setBean((String) info);
		}
	}

	@Override
	public void linkInfo(Object info) {

	}

	@Override
	public Object getInfo() {
		return beanId;
	}

	public void expandNode(final List expandNode) {
		Timer timer = new Timer() {
			gkTreeDirPanelIC tree = gkTreeDirPanelIC.this;

			@Override
			public void run() {
				try {
					if (expandNode.size() == 0) {
						cancel();
					}

					ModelData targetNode = tree.getStore().findModel(
							expandNode.get(expandNode.size() - 1) + "");

					for (Iterator it = expandNode.iterator(); it.hasNext();) {
						String nodeId = it.next() + "";
						ModelData md = tree.getStore().findModel(nodeId);
						if (md == null) {
							cancel();
							break;
						} else if (md.equals(targetNode)) {
							tree.setExpanded(md, true);
							TreePanelSelectionModel sm = tree
									.getSelectionModel();
							sm.select(targetNode, false);
							tree.setSelectionModel(sm);
							cancel();
						} else if (!tree.isExpanded(md)) {
							tree.setExpanded(md, true);
							break;
						}
					}
				} catch (Exception e) {
					cancel();
				}
			}
		};
		timer.scheduleRepeating(200);
	}
}

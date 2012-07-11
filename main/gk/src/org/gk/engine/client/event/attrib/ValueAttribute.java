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
package org.gk.engine.client.event.attrib;

import java.util.Map;

import org.gk.engine.client.Engine;
import org.gk.engine.client.utils.ComponentUtils;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkComboBox;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.form.gkTimeField;
import org.gk.ui.client.com.panel.gkHtmlContainer;
import org.gk.ui.client.com.toolbar.gkButton;
import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;
import org.gk.ui.client.com.utils.DateTimeUtils;
import org.gk.ui.client.com.utils.TreeUtils;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;

/**
 * Value屬性
 * 
 * @author i23250
 * @since 2010/9/30
 */
public class ValueAttribute implements IAttribute {

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof IC) {
			value = ((IC) com).getInfo();
		} else if (com instanceof gkHtmlContainer) {
			gkHtmlContainer hc = (gkHtmlContainer) com;
			value = hc.getHtml();
		} else if (com instanceof Field) {
			Field field = (Field) com;
			if (field instanceof DateField) {
				value = DateTimeUtils.getValue((DateField) field);
			} else if (field instanceof gkTimeField) {
				value = ((gkTimeField) field).getTimeValue();
			} else {
				value = field.getValue();
			}
		} else if (com instanceof gkButton) {
			gkButton field = (gkButton) com;
			value = field.getValue();
		} else if (com instanceof LayoutContainer) {
			// 如果是LayoutContainer,而且第一個元件是Frame，就取得URL字串
			// 這是因為XFrame是使用LayoutContainer包Frame (詳見 XFrame)
			Object obj = ((LayoutContainer) com).getItem(0);
			if (obj instanceof WidgetComponent
					&& ((WidgetComponent) obj).getWidget() instanceof Frame) {
				Frame frame = (Frame) ((WidgetComponent) obj).getWidget();
				value = frame.getUrl();
			}
		} else {
			// 如果都找不到合適的，就直接拿Element取得html字串
			Element ele = com.getElement();
			value = ele.getInnerHTML();
			Component g = ComponentManager.get().get(ele.getAttribute("gid"));
			if (g instanceof Grid) {
				// 針對cellEditor欄位取值
				String[] param = ele.getId().split("_");
				if (param.length != 2) {
					return value;
				}
				String columnName = ((Grid) g).getColumnModel()
						.getColumnById(param[0]).getDataIndex();
				int rowIdx = Integer.parseInt(param[1]);
				ModelData md = (ModelData) ((Grid) g).getStore().getModels()
						.get(rowIdx);
				value = md.get(columnName);
			}
		}
		return value;
	}

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof IC) {
			// 如果是Tree而且資料是Map，就進行該節點的更新
			if (com instanceof gkXMLTreePanelIC && value instanceof Map) {
				TreeUtils.updateSelectNode((gkXMLTreePanelIC) com, (Map) value);
			} else if (com instanceof gkTreeDirPanelIC && value instanceof Map) {
				// 更新節點內容，目前只提供name屬性更動
				Map map = (Map) value;
				assert map.containsKey(gkTreeDirPanelIC.ID) : "can't found nodeId:"
						+ map;
				String id = map.get(gkTreeDirPanelIC.ID).toString();

				if (map.containsKey(gkTreeDirPanelIC.NAME)) {
					// 由於改變name時，同時也更動path，所以採用新增及刪除的方式修改
					String name = map.get(gkTreeDirPanelIC.NAME).toString();
					TreePanel tree = (TreePanel) com;
					TreeStore store = tree.getStore();
					ModelData md = store.findModel(id);
					ModelData parent = store.getParent(md);
					int index = store.indexOf(md);
					gkMap newmd = (gkMap) gkMap.clone((Map) md);

					String oldPath = md.get(gkTreeDirPanelIC.PATH).toString();
					String oldName = md.get(gkTreeDirPanelIC.NAME).toString();
					int last = oldPath.lastIndexOf(oldName);
					String lastPath = oldPath.substring(last);
					lastPath = lastPath.replace(oldName, name);
					String newPath = oldPath.substring(0, last) + lastPath;
					newmd.set(gkTreeDirPanelIC.NAME, name);
					newmd.set(gkTreeDirPanelIC.PATH, newPath);

					store.insert(parent, newmd, index, false);
					store.remove(md);
				}
			} else {
				((IC) com).setInfo(value);
			}
		} else if (com instanceof gkButton) {
			gkButton btn = (gkButton) com;
			btn.setValue((String) value);
		} else if (com instanceof gkHtmlContainer) {
			gkHtmlContainer hc = (gkHtmlContainer) com;
			if (value instanceof Map) {
				hc.setInfo((Map) value);
			} else {
				hc.setHtml((String) value);
			}
		} else if (com instanceof LayoutContainer) {
			String v = (String) value;
			LayoutContainer lc = (LayoutContainer) com;
			// 如果LayoutContainer裡面放的是Frame，就進行url設定
			Object obj = lc.getItem(0);
			if (obj instanceof WidgetComponent
					&& ((WidgetComponent) obj).getWidget() instanceof Frame) {
				((Frame) ((WidgetComponent) obj).getWidget()).setUrl(v);
			} else {
				Engine.get().renderPanel(v, lc, true);
			}
		} else if (com instanceof Field) {
			Field field = (Field) com;
			boolean orgFireChange = field.isFireChangeEventOnSetValue(); // 記錄原先設定
			field.setFireChangeEventOnSetValue(true);
			if (field instanceof DateField) {
				DateTimeUtils.setValue((DateField) field, value + "");
			} else if (field instanceof gkTimeField) {
				((gkTimeField) field).setTimeValue(value + "");
			} else if (field instanceof gkComboBox && value instanceof Map
					&& ((ComboBox) com).getStore().getCount() == 0) {
				// 適用於下拉選單是動態產生未點擊時可直接設定當前的text:value的情境，將map
				// add到store中並setValue
				ComboBox combo = (ComboBox) com;
				Map data = (Map) value;
				combo.getStore().add(new gkMap(data));
				combo.getPropertyEditor().setList(combo.getStore().getModels());
				field.setValue(value);
			} else {
				if (value instanceof String) {
					field.setValue(field.getPropertyEditor()
							.convertStringValue((String) value));
				} else {
					field.setValue(value);
				}
			}
			field.setFireChangeEventOnSetValue(orgFireChange); // 還原原來設定
		} else {
			// 如果都找不到合適的，就直接拿Element設定html字串
			// 會判別如果有gid屬性，就必須找grid更新ModelData
			Element ele = com.getElement();
			ele.setInnerHTML((String) value);
			// gid對於一般欄位是同層，對於labelField則是上一層
			// 所以當拿不到gid就試著找上一層取得gid,都拿不到gid
			// 就不用處理Grid store儲存問題
			String gid = ele.getAttribute("gid");
			if (gid == null || gid.equals("")) {
				gid = ele.getParentElement().getAttribute("gid");
			}
			Component g = ComponentManager.get().get(gid);
			if (g instanceof Grid) {
				// 對於cellEditor欄位處理
				String[] param = ele.getId().split("_");
				if (param.length != 2) {
					return;
				}
				ListStore ds = ((Grid) g).getStore();
				String columnName = ((Grid) g).getColumnModel()
						.getColumnById(param[0]).getDataIndex();
				int rowIdx = Integer.parseInt(param[1]);
				ModelData md = (ModelData) ds.getModels().get(rowIdx);
				md.set(columnName, value);
				StoreEvent evt = new StoreEvent(ds);
				evt.setModel(md);
				evt.setOperation(RecordUpdate.EDIT);
				ds.fireEvent(Store.Update, evt);

				// 設定cellEditor 編輯狀態下，欄位的顯示資料
				Component editorField = ComponentUtils.findComponent(param[0]);
				if (editorField instanceof Field) {
					((Field) editorField).setValue(value);
				}
			}
		}
	}
}

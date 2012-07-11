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
package org.gk.engine.client.build.grid.field;

import java.util.Iterator;
import java.util.Map;

import org.gk.engine.client.Engine;
import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.utils.NodeUtils;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.grid.column.gkICColumnConfig;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 清單中的column放入一個Form，當清單的column可以放Form後，應該沒有甚麼做不到的了...
 * 
 * @author I21890
 * @since 2010/11/15
 */
public class GAdaptFieldBuilder extends GridFieldBuilder {

	public GAdaptFieldBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkICColumnConfig(x) {

			@Override
			public GridCellRenderer<ModelData> createCellRender() {
				GridCellRenderer<ModelData> render = new GridCellRenderer<ModelData>() {

					@Override
					public Object render(ModelData model, String property,
							ColumnData config, int rowIndex, int colIndex,
							ListStore<ModelData> store, Grid<ModelData> grid) {
						config.cellAttr = getColumnAlign();
						return createCell(model, rowIndex, colIndex, grid, x);
					}
				};
				return render;
			}
		};
		return cc;
	}

	/**
	 * 建立IC元件的Cell，IC資訊統一使用Info
	 * 
	 * @param model
	 * @param rowIndex
	 * @param colIndex
	 * @param grid
	 * @param x
	 * @return Object
	 */
	private Object createCell(ModelData model, int rowIndex, int colIndex,
			Grid<ModelData> grid, XField x) {
		Component c = createIC(rowIndex, colIndex, grid, x);
		// 初始從Form的info取得欄位資訊
		Map info = (Map) ((IC) c).getInfo();
		Iterator it = info.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			// 不需要dirtyField
			if (!key.equals(gkFormPanelIC.Event.DIRTY_FIELD)) {
				if (model.get(key) == null) {
					model.set(key, info.get(key));
				}
			}
		}
		((IC) c).setInfo(model);
		((IC) c).linkInfo(model);
		return c;
	}

	private Component createIC(int rowIndex, int colIndex,
			Grid<ModelData> grid, XField x) {
		String gul = x.getContent();
		String vertical = x.getAttribute("vertical", "false");
		String space = x.getAttribute("space", "0");
		String readonly = x.getReadOnly();
		String enable = x.getEnable();

		StringBuffer form = new StringBuffer("<form");
		form.append(" hideLabels='true' id='").append(x.getId()).append("_")
				.append(rowIndex).append("'>");
		form.append("<field type='adapt' ");
		form.append("id='").append(x.getId()).append("_").append(rowIndex)
				.append("_").append(colIndex).append("' ");
		form.append("vertical='").append(vertical).append("' ");
		form.append("space='").append(space).append("' ");
		form.append("adaptingrid='true' "); // 給AdaptFieldBuilder判斷是否在Grid中
		form.append("readonly='").append(readonly).append("' ");
		form.append("enable='").append(enable).append("' ").append(">");
		form.append(gul);
		form.append("</field>");
		form.append("</form>");
		// 將gul裡面field's id 改為 id_rowIndex，自動增列才能正常使用
		gul = setFieldIdAndVerify(form.toString(), rowIndex);

		String id = rowIndex + "_" + colIndex;
		LayoutContainer lc = new LayoutContainer();
		lc.setId(id);
		Engine.get().renderPanel(gul, lc);
		Component c = lc.getItem(0);
		if (c instanceof ContentPanel) {
			((ContentPanel) c).setHeaderVisible(false);
			((ContentPanel) c).setFrame(false);
		}
		return c;
	}

	/**
	 * <pre>
	 * 判斷adapt裡面的gul程式碼有沒有符合下列規範，並建立所有field的id (加入rowIdx)
	 * (1) 只能放 <field ...>
	 * (2) <field 不包含 type='adapt' 的欄位
	 * </pre>
	 * 
	 * @param gul
	 * @param rowIdx
	 * @return String
	 */
	private String setFieldIdAndVerify(String gul, int rowIdx) {
		Document doc = NodeUtils.parseGUL(gul);
		// 拿 <form><field type='adapt'> ...</field></form> 裡面的fields
		NodeList childList = doc.getFirstChild().getFirstChild()
				.getChildNodes();
		int fields = childList.getLength();
		for (int i = 0; i < fields; i++) {
			Node field = childList.item(i);
			String tagName = field.getNodeName();
			Node type = field.getAttributes().getNamedItem("type");
			Node id = field.getAttributes().getNamedItem("id");
			// 只能使用 field 的元件
			if (!tagName.equals("field")) {
				throw new RuntimeException("tagName error:" + tagName);
			}
			// 不能使用type='adapt'
			if (type == null || type.getNodeValue().equals("adapt")) {
				throw new RuntimeException("attribute error:" + type);
			}
			// 沒有設定id就幫忙設定，以便detach後，Engine可以刪除
			if (id == null) {
				((Element) field).setAttribute("id", XDOM.getUniqueId() + "_"
						+ rowIdx);
			} else {
				id.setNodeValue(id.getNodeValue() + "_" + rowIdx);
			}
		}
		return doc + "";
	}
}
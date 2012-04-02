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

import org.gk.engine.client.Engine;
import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.grid.XGridField;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.column.gkICColumnConfig;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

/**
 * <title>清單中的Column放入一個Form</title>
 * 
 * 當清單的column可以放Form後，應該沒有甚麼做不到的了...
 * 
 * @author I21890
 * @since 2010/11/15
 */
public class GICBuilder extends GridFieldBuilder {

	private static final String COLUMNWIDTH = "100%";

	public GICBuilder(String ic) {
		super(ic);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkICColumnConfig(x) {

			/**
			 * 將GUL語法產生出指定的Panel
			 */
			@Override
			public GridCellRenderer<ModelData> createCellRender() {
				GridCellRenderer<ModelData> render = new GridCellRenderer<ModelData>() {

					@Override
					public Object render(ModelData model, String property,
							ColumnData config, int rowIndex, int colIndex,
							ListStore<ModelData> store, Grid<ModelData> grid) {

						return createCell(model, property, store, rowIndex,
								colIndex, grid, x);
					}
				};
				return render;
			}
		};
		return cc;
	}

	/**
	 * 建立IC元件的Cell , IC資訊統一使用Info
	 * 
	 * @param model
	 * @param property
	 * @param store
	 * @param rowIndex
	 * @param colIndex
	 * @param grid
	 * @param x
	 * @return Object
	 */
	private Object createCell(ModelData model, String property,
			ListStore<ModelData> store, int rowIndex, int colIndex,
			Grid<ModelData> grid, XField x) {

		Component c = createIC(rowIndex, colIndex, grid, x);
		ModelData md = store.getAt(rowIndex);
		// 增加判斷，如果modelData是null就生個空的Map來存放ic元件的資訊
		if (md.get(x.getName()) == null) {
			md.set(x.getName(), new gkMap());
		}
		((IC) c).setInfo(md.get(x.getName()));
		// 讓Store的ModelData和表單資訊同步更新
		((IC) c).linkInfo(md.get(x.getName()));
		return c;
	}

	private Component createIC(int rowIndex, int colIndex,
			Grid<ModelData> grid, XField x) {
		String gul = "<page layout='fit'>" + x.getContent() + "</page>";
		String id = grid.getId() + "_" + rowIndex + "_" + colIndex;
		LayoutContainer lc = new LayoutContainer();
		lc.setId(id);
		Engine.get().renderPanel(gul, lc);
		Component c = ((LayoutContainer) lc.getItem(0)).getItem(0);
		if (c instanceof ContentPanel) {
			((ContentPanel) c).setHeaderVisible(false);
			((ContentPanel) c).setFrame(false);
		}
		c.setWidth(COLUMNWIDTH);
		c.setStyleAttribute("padding", "0px");
		return c;
	}
}
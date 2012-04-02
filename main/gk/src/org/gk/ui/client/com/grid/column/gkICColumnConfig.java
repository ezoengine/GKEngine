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
package org.gk.ui.client.com.grid.column;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

/**
 * <title>提供建構ic元件的ColumnConfig</title>
 * 
 * <pre>
 * cellRender必須在Builder才能進行，因為IC元件是透過GUL語法
 * 產生元件，所以在GICBuilder還要透過Engine解析GUL語法後才能
 * 建立元件
 * </pre>
 * 
 * @author I21890
 * @since 2010/11/15
 */
public abstract class gkICColumnConfig extends gkCellColumnConfig {

	public gkICColumnConfig(gkColumnInfo field) {
		super(field);
		setRenderer(createCellRender());
	}

	@Override
	protected Field createField() {
		return null;
	}

	@Override
	protected Object createColumnCell(ModelData model, String property,
			ListStore<ModelData> store, int rowIndex, int colIndex,
			Grid<ModelData> grid) {
		return null;
	}

	@Override
	public void onField(Field field) {

	}

	@Override
	public abstract GridCellRenderer<ModelData> createCellRender();
}
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
package org.gk.engine.client.utils;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

/**
 * 提供Grid相關工具API
 * 
 * @author I21890
 * @since 2011/5/10
 */
public class GridUtils {

	/**
	 * 根據id取得指定的ColumnModel
	 * 
	 * @param grid
	 * @param id
	 * @return
	 */
	public static int getColumnIdx(Grid grid, String id) {
		ColumnModel cm = grid.getColumnModel();
		int size = cm.getColumnCount();
		for (int i = 0; i < size; i++) {
			ColumnConfig cc = cm.getColumn(i);
			if (cc.getId().equals(id))
				return i;
		}
		throw new RuntimeException("column id:" + id + " not found!");
	}
}

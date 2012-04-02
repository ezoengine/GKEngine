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
package org.gk.ui.client.com.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class gkList<E> extends ArrayList {
	private static final long serialVersionUID = 1L;

	public gkList() {
	}

	public gkList(Collection c) {
		addAll(c);
	}

	public gkList(Map... maps) {
		for (Map map : maps)
			add(map);
	}

	public gkList(String... strs) {
		for (String str : strs)
			add(str);
	}

	public gkList(Number... nums) {
		for (Number num : nums)
			add(num);
	}

	public gkList(ColumnConfig... ccs) {
		for (ColumnConfig cc : ccs)
			add(cc);
	}

	public gkList(ModelData... md) {
		for (ModelData cc : md)
			add(cc);
	}
}

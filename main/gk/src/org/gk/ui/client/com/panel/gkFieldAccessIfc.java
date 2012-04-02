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
package org.gk.ui.client.com.panel;

public interface gkFieldAccessIfc {

	/**
	 * 將obj設定到Field的value欄位
	 * 
	 * @param obj
	 */
	public void setValue(Object obj);

	/**
	 * 根據欄位的id，取得該Field的value值(一個Field內有多個Field時，才需要id)
	 * 
	 * @param id
	 * @return Object
	 */
	public Object getValue(String id);
}

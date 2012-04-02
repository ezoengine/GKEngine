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

public interface gkColumnInfo {

	public String getId();

	public String getLabel();

	public String getName();

	public String getColumnWidth();

	public String getValue();

	public boolean isCellEditor();

	public String getInit();

	public Object getFieldObject();

	public String getInputStyle();

	public String getAlign();

	public String getTitle();

	public void execEventCenter(String id, String initCmd, Object obj);

	public void addComponentToStore(String id, Object obj);
}

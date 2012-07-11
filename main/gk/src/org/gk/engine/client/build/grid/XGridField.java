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
package org.gk.engine.client.build.grid;

import org.gk.engine.client.build.EngineDataStore;
import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.grid.field.GridFieldBuilder;
import org.gk.engine.client.event.EventCenter;
import org.gk.ui.client.com.grid.column.gkCellColumnConfig;
import org.gk.ui.client.com.grid.column.gkColumnInfo;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.xml.client.Node;

/**
 * 表單欄位產生元件
 * 
 * <pre>
 * 只要是 <field />就會產生XGridField物件來產生真正的field物件。
 * </pre>
 * 
 * @author I21890
 * @since 2010/02/08
 */
public class XGridField extends XField implements gkColumnInfo {

	private final static String REGALIGN = "center|left|right";
	public final static String COLUMN_CONFIG = "columnConfig";

	protected String hidden, align, sortable, cellEditor;
	protected String columnWidth, columnHeader, columnAlign;

	public XGridField(Node node) {
		super(node, null);
		// 預設field欄位寬度為100%
		width = super.getAttribute("width", "100%");

		hidden = super.getAttribute("hidden", "false");
		align = super.getAttribute("align", "center");
		sortable = super.getAttribute("sortable", "true");
		cellEditor = super.getAttribute("cellEditor", "false");
		columnWidth = super.getAttribute("columnWidth", "100");
		columnHeader = super.getAttribute("columnHeader", label);
		columnAlign = super.getAttribute("columnAlign", "center");
	}

	@Override
	public void init() {
		// do nothing
		// 放在Grid裡面的field應該是欄位render出來後再做init
		// 因此這裡不做初始化
	}

	public void setCellEditor(String cellEditor) {
		this.cellEditor = cellEditor;
	}

	public String getHidden() {
		return hidden;
	}

	@Override
	public String getAlign() {
		return align;
	}

	public String getSortable() {
		return sortable;
	}

	@Override
	public boolean isCellEditor() {
		return Boolean.parseBoolean(cellEditor);
	}

	@Override
	public String getColumnWidth() {
		return columnWidth;
	}

	public String getColumnHeader() {
		return columnHeader;
	}

	public String getColumnAlign() {
		return columnAlign;
	}

	/**
	 * 使用GridFieldBuilder建構欄位，由於UIBuilder需回傳Component
	 * 所以將ColumnConfig包裝在Component裡面
	 * 
	 * @return Component
	 */
	@Override
	public Component build() {
		// Object 有可能是 ColumnConfig 或 Map (存放HeaderGroup資訊)
		final Object cc = GridFieldBuilder.build(this, type);
		if (cc instanceof ColumnConfig) {
			initColumnConfig((ColumnConfig) cc);
		}
		Component c = new Component() {
			{
				setData(COLUMN_CONFIG, cc);
			}
		};
		if (!isCellEditor()) {
			EngineDataStore.addComponent(getId(), c);
		}
		return c;
	}

	/**
	 * 初始化ColumnConfig
	 * 
	 * @param cc
	 */
	protected void initColumnConfig(ColumnConfig cc) {
		boolean isHidden = Boolean.parseBoolean(getHidden());
		cc.setHidden(isHidden);

		if (align.matches(REGALIGN)) {
			cc.setAlignment(HorizontalAlignment.valueOf(align.toUpperCase()));
		}

		// 設定是否可以點選header進行排序
		if (cc.isSortable() != Boolean.parseBoolean(getSortable())) {
			cc.setSortable(!cc.isSortable());
		}

		cc.setHeader(columnHeader);

		// 設定ColumnConfig 內元件是否置中
		if (columnAlign.matches(REGALIGN)) {
			((gkCellColumnConfig) cc).setColumnAlign(columnAlign);
		}
	}

	@Override
	public Object getFieldObject() {
		return this;
	}

	@Override
	public void execEventCenter(String id, String initCmd, Object obj) {
		EventCenter.exec(id, initCmd, obj, null);
	}

	@Override
	public void addComponentToStore(String id, Object obj) {
		EngineDataStore.addComponent(id, (Component) obj);
	}

	@Override
	public boolean removeComponentFromStore(String id) {
		return EngineDataStore.removeComponent(id);
	}
}
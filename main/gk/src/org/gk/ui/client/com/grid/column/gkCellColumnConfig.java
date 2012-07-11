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

import org.gk.ui.client.com.grid.gkGridIC;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public abstract class gkCellColumnConfig extends ColumnConfig {

	protected gkColumnInfo fieldInfo;
	// 用於設定grid cell内的Horizontal
	private String columnAlign = "align='center'";

	/**
	 * 設定grid cell內的Horizontal Align
	 */
	public void setColumnAlign(String align) {
		columnAlign = "align='" + align.toLowerCase() + "'";
	}

	/**
	 * 取得grid cell內的Horizontal Alignment
	 */
	public String getColumnAlign() {
		return columnAlign;
	}

	public gkCellColumnConfig(gkColumnInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
		setId(fieldInfo.getId());
		setHeader(fieldInfo.getLabel());
		setDataIndex(fieldInfo.getName());
		setWidth(Integer.parseInt(fieldInfo.getColumnWidth()));
		if (fieldInfo.isCellEditor()) {
			CellEditor ce = createCellEditor();
			afterCreateCellEditor(ce);
			setEditor(ce);
		} else {
			setRenderer(createCellRender());
		}
	}

	protected CellEditor createCellEditor() {
		final Field field = createField();
		addListener(field);
		onField(field);
		return new CellEditor(field) {

			@Override
			public Object preProcessValue(Object value) {
				if (value == null || value.equals("")) {
					return null;
				}
				return field.getPropertyEditor().convertStringValue(
						value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				return value == null ? "" : value;
			}
		};
	}

	private void afterCreateCellEditor(CellEditor ce) {
		Field field = ce.getField();
		field.setId(fieldInfo.getId());
		if (field instanceof TextField
				&& getHeader().endsWith("<span style='color:red'>*</span>")) {
			((TextField) field).setAllowBlank(false);
		}
		fieldInfo.addComponentToStore(fieldInfo.getId(), field);

		ce.initialValue = fieldInfo.getValue();
		if (!fieldInfo.getInit().equals("")) {
			ce.addListener(Events.StartEdit, new Listener<EditorEvent>() {

				@Override
				public void handleEvent(EditorEvent be) {
					fieldInfo.execEventCenter(fieldInfo.getId(),
							fieldInfo.getInit(), fieldInfo.getFieldObject());
				}
			});
		}
	}

	protected GridCellRenderer<ModelData> createCellRender() {
		GridCellRenderer<ModelData> fieldRender = new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				config.cellAttr = getColumnAlign();
				Object obj = createColumnCell(model, property, store, rowIndex,
						colIndex, grid);
				// 如果是Component就做初始化動作
				if (obj instanceof Component) {
					Component com = (Component) obj;
					final String id = getId() + "_" + rowIndex;
					if (com instanceof MultiField) {
						MultiField mf = (MultiField) com;
						mf.get(0).setId(id);
					} else {
						com.setId(id);
					}
					if (!fieldInfo.getInit().equals("")) {
						com.addListener(Events.Render,
								new Listener<BaseEvent>() {

									@Override
									public void handleEvent(BaseEvent be) {
										fieldInfo.execEventCenter(id,
												fieldInfo.getInit(),
												fieldInfo.getFieldObject());
										fieldInfo.removeComponentFromStore(id);
									}
								});
						fieldInfo.addComponentToStore(id, obj);
					}
				}
				return obj;
			}
		};
		return fieldRender;
	}

	@Override
	public void setHeader(String label) {
		// 如有*結尾，把*設定為紅色
		if (label.endsWith("*")) {
			label = label.substring(0, label.length() - 1)
					+ "<span style='color:red'>*</span>";
		}
		super.setHeader(label);
		fireEvent(Events.HeaderChange, new BaseEvent(label));
	}

	protected void addListener(Field field, Grid<ModelData> grid,
			final int rowIndex, int colIndex, final ListStore<ModelData> store) {
		// 設定必填欄位
		String name = getHeader();
		if (name.endsWith("<span style='color:red'>*</span>")) {
			if (field instanceof TextField) {
				((TextField) field).setAllowBlank(false);
			}
		}

		if (!(grid.getParent() instanceof gkGridIC)) {
			return;
		}
		final gkGridIC gridIC = (gkGridIC) grid.getParent();
		final ModelData modelRow = store.getAt(rowIndex);

		field.addListener(Events.OnMouseDown, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				// 重新計算rowIndex。當grid刪除一行後，其他行的rowIndex還保持以前的所以不能使用
				int row = store.indexOf(modelRow);
				// 增行是否超出限定資料筆數，如果超出不進行增行
				gridIC.createNewRow(String.valueOf(row));
			}
		});

		// 增加Change Listener，在field改變時調用
		field.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				gridIC.getGrid().getSelectionModel().select(rowIndex, true);
				gridIC.refreshFooterData();
			}
		});
	}

	protected void addListener(Field field) {
		field.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				if (GXT.isIE || GXT.isGecko) {
					Field fd = fe.getField();
					if (fd.isInEditor()) {
						CellEditor ed = (CellEditor) fd.getParent();
						EditorEvent e = new EditorEvent(null);
						e.setValue(ed.postProcessValue(fe.getValue()));
						e.setStartValue(ed.preProcessValue(fe.getOldValue()));
						ed.fireEvent(Events.BeforeComplete, e);
					}
				}
			}
		});
	}

	protected abstract Object createColumnCell(ModelData model,
			String property, ListStore<ModelData> store, int rowIndex,
			int colIndex, Grid<ModelData> grid);

	protected abstract Field createField();

	public abstract void onField(Field field);
}

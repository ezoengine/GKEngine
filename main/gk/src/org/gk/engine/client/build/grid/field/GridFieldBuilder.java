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

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcessImpl;

import org.gk.engine.client.IEngine;
import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.exception.InvalidFieldTypeException;
import org.gk.engine.client.i18n.EngineMessages;

import com.extjs.gxt.ui.client.widget.BoxComponent;

/**
 * 建構清單欄位的抽象類別
 * 
 * @author I21890
 */
public abstract class GridFieldBuilder {

	private final static String FIELD_BUILDER = ".gridField";

	private static XGridField createField;
	// 有可能是ColumnConfig或HeadGroup
	private static Object createObject;

	public GridFieldBuilder(String fieldType) {
		String[] events = fieldType.split(",");
		// 訂閱產生畫面元件的事件
		for (int i = 0; i < events.length; i++) {
			String eventId = events[i].toLowerCase() + FIELD_BUILDER;
			IEngine.builder.subscribe(eventId, new EventProcessImpl() {
				@Override
				public void execute(String eventId, EventObject eo) {
					createObject = create();
				}
			});
		}
	}

	public static Object build(XGridField field, String fieldType) {
		String eventId = fieldType.toLowerCase() + FIELD_BUILDER;
		// 準備要發佈給子類別實現的Field
		createField = field;
		// 如果找不到某種field的Builder就丟出此例外
		if (IEngine.builder.publish(eventId) == 0) {
			throw new InvalidFieldTypeException(
					EngineMessages.msg.error_invalidFieldType(field.getType()));
		}
		return createObject;
	}

	public XGridField getField() {
		return createField;
	}

	/**
	 * 建立Field
	 * 
	 * @return Object
	 */
	public abstract Object create();

	/**
	 * 設定GridField的共通屬性
	 * 
	 * @param com
	 * @param xf
	 */
	public void setAttribute(BoxComponent com, XField xf) {
		if (!xf.getWidth().equals("")) {
			com.setWidth(xf.getWidth());
		}

		if (!xf.getHeight().equals("")) {
			com.setHeight(xf.getHeight());
		}

		boolean isEnable = Boolean.parseBoolean(xf.getEnable());
		if (com.isEnabled() != isEnable) {
			com.setEnabled(!com.isEnabled());
		}

		if (!Boolean.parseBoolean(xf.getVisible())) {
			com.setVisible(false);
		}

		com.setBorders(Boolean.parseBoolean(xf.getBorders()));

		if (!xf.getTitle().equals("")) {
			com.setTitle(xf.getTitle());
		}

		Object data = xf.getData();
		if (data.toString().length() > 0) {
			com.setData(XComponent.DATA, data);
		}

		// 此方法提供form和grid中Component共有的属性设定
		xf.initializeComponent(com);
		// 设定ColumnConfig中的元件是否可以resize
		xf.setResizePara(com);
	}
}

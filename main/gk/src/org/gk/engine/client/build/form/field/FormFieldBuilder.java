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
package org.gk.engine.client.build.form.field;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcessImpl;

import org.gk.engine.client.IEngine;
import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.form.XFormField;
import org.gk.engine.client.exception.InvalidFieldTypeException;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * 提供建立特定的欄位
 * 
 * @author I21890
 */
public abstract class FormFieldBuilder {

	private final static String FIELD_BUILDER = ".formField";

	private static XFormField createField;
	private static Component createComponent;

	public FormFieldBuilder(String fieldType) {
		String[] events = fieldType.split(",");
		for (int i = 0; i < events.length; i++) {
			String eventId = events[i].toLowerCase() + FIELD_BUILDER;
			// 訂閱產生畫面元件的事件
			IEngine.builder.subscribe(eventId, new EventProcessImpl() {
				@Override
				public void execute(String eventId, EventObject eo) {
					if (createField == null) {
						createComponent = null;
						return;
					}
					if (createField.getForm() != null) {
						createComponent = create(createField.getForm());
					} else {
						createComponent = create();
					}
					// 做完後要將createField設為null,才不會影響下次createField判斷 (誤抓到上次的!)
					createField = null;
				}
			});
		}
	}

	public static Component build(XFormField field, String fieldType) {
		String eventId = fieldType.toLowerCase() + FIELD_BUILDER;
		// 準備要發佈給子類別實現的Field
		createField = field;
		// 如果找不到某種field的Builder就丟出此例外
		if (IEngine.builder.publish(eventId) == 0) {
			throw new InvalidFieldTypeException(
					EngineMessages.msg.error_invalidFieldType(field.getType()));
		}
		return createComponent;
	}

	public XField getField() {
		return createField;
	}

	/**
	 * 建立Field
	 * 
	 * @return Component
	 */
	public abstract Component create();

	/**
	 * 建立Field(傳入form)
	 * 
	 * @param form
	 * @return Component
	 */
	public abstract Component create(gkFormPanelIC form);
}

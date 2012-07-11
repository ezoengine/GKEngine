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
package org.gk.ui.client.binding;

import java.util.List;
import java.util.Map;

import org.gk.ui.client.com.panel.gkFormPanelIC.Event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.Field;

public class gkFieldBinding {

	protected Field field;

	protected String name;

	protected Map info;

	public gkFieldBinding(Field field, String name, Map info) {
		this.field = field;
		this.name = name;
		this.info = info;

		bindingListener();
		initialInfoValue();
	}

	protected void bindingListener() {
		field.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				updateDirtyField();
				onFieldChange(be);
			}
		});

		field.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				updateDirtyField();
				onComponentKeyUp(event);
			}
		});
	}

	public String getName() {
		return name;
	}

	/**
	 * 初始化info內的value
	 */
	protected void initialInfoValue() {
		info.put(name, "");
	}

	public void execute(Object value) {
		if (value instanceof String) {
			field.setValue(field.getPropertyEditor().convertStringValue(
					(String) value));
		} else {
			field.setValue(value);
		}
		updateInfoValue(value);
	}

	protected void onFieldChange(BaseEvent be) {
		updateInfoValue(field.getValue());
	}

	protected void onComponentKeyUp(ComponentEvent event) {
		updateInfoValue(field.getValue());
	}

	/**
	 * 更新info內的value
	 * 
	 * @param value
	 */
	protected void updateInfoValue(Object value) {
		info.put(name, value == null ? "" : value);
	}

	/**
	 * 更新Dirty Field
	 */
	protected void updateDirtyField() {
		List dirtyList = (List) info.get(Event.DIRTY_FIELD);
		if (dirtyList != null && !dirtyList.contains(name)) {
			dirtyList.add(name);
		}
	}

	/**
	 * 移除Dirty Field
	 */
	public void removeDirtyField() {
		List dirtyList = (List) info.get(Event.DIRTY_FIELD);
		if (dirtyList != null && dirtyList.contains(name)) {
			dirtyList.remove(name);
		}
	}
}

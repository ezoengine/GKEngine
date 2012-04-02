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
package org.gk.engine.client.event.attrib;

import java.util.List;

import org.gk.ui.client.com.form.gkComboBox;
import org.gk.ui.client.com.form.gkListFieldIC;
import org.gk.ui.client.com.grid.gkGridIC;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * List屬性
 * 
 * @author i23250
 * @since 2011/5/27
 */
public class ListAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof gkComboBox) {
			final gkComboBox cb = (gkComboBox) com;
			if (value instanceof List) {
				cb.getStore().removeAll();
				cb.getStore().add((List) value);
				cb.getPropertyEditor().setList((List) value);
				// 如果是由人trigger設定List清單，就進行展開
				if (cb.isTriggerExpand()) {
					// 當所有事件流程跑完後，才做expand的動作
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {

						@Override
						public void execute() {
							if (!cb.isExpanded()) {
								cb.expand();
							}
						}
					});
				}
			}
		} else if (com instanceof gkGridIC) {
			((gkGridIC) com).setInfo(value);
		} else if (com instanceof gkListFieldIC) {
			((gkListFieldIC) com).setInfo(value);
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		Object value = null;
		if (com instanceof ComboBox) {
			ComboBox cb = (ComboBox) com;
			value = cb.getStore().getModels();
		} else if (com instanceof gkGridIC) {
			value = ((gkGridIC) com).getInfo();
		} else if (com instanceof gkListFieldIC) {
			value = ((gkListFieldIC) com).getInfo();
		}
		return value;
	}
}

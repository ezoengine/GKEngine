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

import java.util.Map;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.Field;

public class gkRadioBinding extends gkFieldBinding {

	private String radioValue;

	public gkRadioBinding(Field field, String name, Map info, String radioValue) {
		super(field, name, info);
		this.radioValue = radioValue;
	}

	@Override
	protected void bindingListener() {
		field.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				updateDirtyField();
				Boolean value = (Boolean) fe.getField().getValue();
				Object currentValue = info.get(name);
				if (value != null && value) {
					if (!radioValue.equals(currentValue)) {
						info.put(name, radioValue);
					}
				} else {
					if (radioValue.equals(currentValue)) {
						info.put(name, "");
					}
				}
			}
		});
	}

	@Override
	protected void initialInfoValue() {
		if (info.get(name) == null) {
			super.initialInfoValue();
		}
	}

	@Override
	public void execute(Object value) {
		field.setValue(radioValue.equals(value));
		info.put(name, value);
	}
}

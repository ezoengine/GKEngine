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

import org.gk.ui.client.com.form.gkComboBox;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.Field;

public class gkComboBoxBinding extends gkFieldBinding {

	public gkComboBoxBinding(Field field, String name, Map info) {
		super(field, name, info);
	}

	@Override
	protected void bindingListener() {
		final gkComboBox cb = (gkComboBox) field;
		cb.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				updateDirtyField();
				ModelData md = se.getSelectedItem();
				info.put(name, md == null ? "" : md.get("value"));
			}
		});

		cb.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				updateDirtyField();
				ModelData value = cb.getValue();
				info.put(name, value == null ? "" : value.get("value"));
			}
		});
	}

	@Override
	public void execute(Object value) {
		ModelData md = null;
		gkComboBox cb = (gkComboBox) field;
		if (value instanceof ModelData) {
			md = (ModelData) value;
		} else {
			if (value != null) {
				md = cb.getPropertyEditor()
						.convertStringValue(value.toString());
			}
		}
		cb.setValue(md);
		info.put(name, md == null ? "" : md.get("value"));
	}
}

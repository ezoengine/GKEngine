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

import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.event.IEventConstants;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.column.gkComboBoxColumnConfig;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GComboBoxBuilder extends GridFieldBuilder {

	public GComboBoxBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkComboBoxColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);
				ComboBox cb = (ComboBox) field;

				ListStore ls = new ListStore<ModelData>();
				String content = x.getContent();
				if (!content.equals("")) {
					content = content.replaceAll("[ \t\n]*", "");
					// content = text(畫面顯示)或text(畫面顯示):value(實際值)
					// 如：a,b,c或a:aaa,b:bbb,c:ccc
					String[] comma = content.split(IEventConstants.SPLIT_COMMA);
					for (int i = 0; i < comma.length; i++) {
						String[] colon = comma[i]
								.split(IEventConstants.SPLIT_COLON);
						gkMap data = new gkMap("text", colon[0]);
						if (colon.length == 2) {
							data.put("value", colon[1]);
						} else {
							data.put("value", colon[0]);
						}
						ls.add(data);
					}
				}
				cb.getPropertyEditor().setList(ls.getModels());
				cb.setStore(ls);

				String value = x.getValue();
				if (!value.equals("")) {
					cb.setValue(cb.getPropertyEditor()
							.convertStringValue(value));
				}
			}
		};
		return cc;
	}
}

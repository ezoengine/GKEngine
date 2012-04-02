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
import org.gk.ui.client.com.grid.column.gkRadioColumnConfig;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GRadioBuilder extends GridFieldBuilder {

	public GRadioBuilder(String radio) {
		super(radio);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		final String boxLabel = x.getAttribute("boxLabel", x.getLabel());
		final String boxValue = x.getValue();
		final String checked = x.getAttribute("checked", "false");

		ColumnConfig cc = new gkRadioColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);

				Radio radio = (Radio) field;
				radio.setBoxLabel(boxLabel);
				radio.setData(boxLabel, boxValue);
				radio.setValue(Boolean.parseBoolean(checked));
			}
		};
		return cc;
	}
}

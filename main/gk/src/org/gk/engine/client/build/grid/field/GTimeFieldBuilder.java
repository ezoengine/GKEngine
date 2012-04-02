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
import org.gk.ui.client.com.form.gkTimeField;
import org.gk.ui.client.com.grid.column.gkTimeColumnConfig;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GTimeFieldBuilder extends GridFieldBuilder {

	public GTimeFieldBuilder(String time) {
		super(time);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkTimeColumnConfig(x) {
			@Override
			public void onField(Field field) {
				setAttribute(field, x);
				gkTimeField f = (gkTimeField) field;
				if (!x.getValue().equals("")) {
					f.setTimeValue(x.getValue());
				}
			}
		};
		return cc;
	}
}

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
import org.gk.ui.client.com.form.gkYMField;
import org.gk.ui.client.com.grid.column.gkYMColumnConfig;
import org.gk.ui.client.com.utils.DateTimeUtils;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GYMFiledBuilder extends GridFieldBuilder {

	public GYMFiledBuilder(String ym) {
		super(ym);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkYMColumnConfig(x) {
			@Override
			public void onField(Field field) {
				setAttribute(field, x);
				String value = x.getValue();
				String format = x.getFormat();
				if (field instanceof gkYMField) {
					gkYMField ym = (gkYMField) field;
					if (!format.equals("")) {
						ym.setFormat(format);
					}
					if (!value.equals("")) {
						DateTimeUtils.setValue(ym, value);
					}
				}
			}
		};
		return cc;
	}
}

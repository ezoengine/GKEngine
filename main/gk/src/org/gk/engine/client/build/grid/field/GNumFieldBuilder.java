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
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.com.grid.column.gkNumberColumnConfig;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.i18n.client.NumberFormat;

public class GNumFieldBuilder extends GridFieldBuilder {

	public GNumFieldBuilder(String num) {
		super(num);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkNumberColumnConfig(x) {

			@Override
			public void onField(Field field) {
				String value = x.getValue();
				String format = x.getFormat();
				if (field instanceof NumberField) {
					NumberField nf = (NumberField) field;
					// 设定format
					if (!format.equals("")) {
						nf.setFormat(NumberFormat.getFormat(format));
					}
					// value需符合数字的正則表示式
					if (value.matches(IRegExpUtils.FLOAT)) {
						nf.setValue(Double.valueOf(value));
					}
				}
				setAttribute(field, x);
			}
		};
		return cc;
	}
}
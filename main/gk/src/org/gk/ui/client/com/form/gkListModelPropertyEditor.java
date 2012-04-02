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
package org.gk.ui.client.com.form;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;

public class gkListModelPropertyEditor<Data extends ModelData> extends
		ListModelPropertyEditor<Data> {

	private String valueProperty = "value";

	@Override
	public Data convertStringValue(String value) {
		if (models.isEmpty() && !value.equals("")) {
			ModelData md = new gkMap();
			md.set(displayProperty, value);
			md.set(valueProperty, value);
			return (Data) md;
		}

		for (Data d : models) {
			Object val = d.get(displayProperty);
			Object val2 = d.get(valueProperty);
			if (value.equals(val != null ? val.toString() : null)) {
				return d;
			} else if (value.equals(val2 != null ? val2.toString() : null)) {
				return d;
			}
		}
		return null;
	}
}

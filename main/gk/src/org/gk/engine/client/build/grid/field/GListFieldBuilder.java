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

import java.util.List;
import java.util.Map;

import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.event.IEventConstants;
import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkListFieldIC;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.column.gkListColumnConfig;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GListFieldBuilder extends GridFieldBuilder {

	public GListFieldBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkListColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);
				gkListFieldIC lf = (gkListFieldIC) field;
				String content = x.getContent();
				String value = x.getValue();

				if (!content.equals("")) {
					lf.setInfo(parseInfo(content));
				}

				if (!value.equals("")) {
					lf.setSelectItem(parseInfo(value));
				}
			}
		};
		return cc;
	}

	/**
	 * 解析屬性值
	 * 
	 * @param value
	 * @return List
	 */
	private List parseInfo(String value) {
		value = value.replaceAll("[ \t\n]*", "");
		String[] comma = value.split(IEventConstants.SPLIT_COMMA);
		List list = new gkList();
		for (int i = 0; i < comma.length; i++) {
			String[] colon = comma[i].split(IEventConstants.SPLIT_COLON);
			Map data = new gkMap("text", colon[0]);
			if (colon.length == 2) {
				data.put("value", colon[1]);
			} else {
				data.put("value", colon[0]);
			}
			list.add(data);
		}
		return list;
	}
}

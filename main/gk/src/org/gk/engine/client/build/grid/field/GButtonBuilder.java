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
import org.gk.ui.client.com.grid.column.gkButtonColumnConfig;
import org.gk.ui.client.com.toolbar.gkButton;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GButtonBuilder extends GridFieldBuilder {

	public GButtonBuilder(String btn) {
		super(btn);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		final String confirm = x.getAttribute("confirm", "false");
		final String icon = getField().getAttribute("icon", "icsc-edit");
		final String toggle = getField().getAttribute("toggle", "false");
		final String toggleGroup = getField().getAttribute("toggleGroup", "");
		// Grid內的Button不支援CellEditor，因此 設為false
		x.setCellEditor("false");

		gkButtonColumnConfig cc = new gkButtonColumnConfig(x) {

			@Override
			public Button createButton() {
				Button btn = null;
				if (Boolean.parseBoolean(toggle)) {
					btn = new ToggleButton(x.getLabel());
					if (!toggleGroup.equals("")) {
						((ToggleButton) btn).setToggleGroup(toggleGroup);
					}
				} else {
					if (confirm.toLowerCase().equals("false")) {
						btn = new gkButton(x.getLabel(), false);
					} else {
						btn = new gkButton(x.getLabel(), confirm);
					}
					((gkButton) btn).setValue(x.getValue());
				}

				btn.setIconStyle(icon);
				setAttribute(btn, x);
				return btn;
			}
		};
		return cc;
	}
}
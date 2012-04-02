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
package org.gk.engine.client.build.form.field;

import java.util.Iterator;

import org.gk.engine.client.build.menu.XMenu;
import org.gk.engine.client.gen.UIGen;
import org.gk.ui.client.com.panel.gkFormPanelIC;
import org.gk.ui.client.com.toolbar.gkButton;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.menu.Menu;

public class ButtonBuilder extends FormFieldBuilder {

	public ButtonBuilder(String btn) {
		super(btn);
	}

	@Override
	public Component create() {
		return createButton();
	}

	@Override
	public Component create(gkFormPanelIC form) {
		return createButton();
	}

	private Button createButton() {
		String confirm = getField().getAttribute("confirm", "false");
		String icon = getField().getAttribute("icon", "icsc-edit");
		String toggle = getField().getAttribute("toggle", "false");
		String toggleGroup = getField().getAttribute("toggleGroup", "");

		Button btn = null;
		if (Boolean.parseBoolean(toggle)) {
			btn = new ToggleButton(getField().getLabel());
			if (!toggleGroup.equals("")) {
				((ToggleButton) btn).setToggleGroup(toggleGroup);
			}
		} else {
			if (confirm.toLowerCase().equals("false")) {
				btn = new gkButton(getField().getLabel(), false);
			} else {
				btn = new gkButton(getField().getLabel(), confirm);
			}
			((gkButton) btn).setValue(getField().getValue());
		}
		// 为btn设定图片，在gul语法中设定icon="xxx",xxx可以是css文件中已定义好的选择器（如：icsc-edit）
		// 也可以是自定义的图片路径，若无设定，则默认使用icon="icsc-edit"
		btn.setIconStyle(icon);

		// 如果 <field type='btn'>..</field>有子節點，是為menu，進行建立menu動作
		if (getField().getWidgets().size() > 0) {
			attachWidget(btn);
		}
		return btn;
	}

	private void attachWidget(Button btn) {
		for (Iterator<UIGen> it = getField().getWidgets().iterator(); it
				.hasNext();) {
			UIGen ui = it.next();
			// 如果是Menu物件就透過setMenu方法放到btn中
			if (ui instanceof XMenu) {
				btn.setMenu((Menu) ui.build());
			}
		}
	}
}

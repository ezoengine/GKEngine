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
package org.gk.engine.client.build.menu;

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.xml.client.Node;

public class XMenuItem extends XItem {

	protected String text;
	protected String icon;

	public XMenuItem(Node node, List widgets) {
		super(node, widgets);

		text = super.getAttribute("text", "");
		icon = super.getAttribute("icon", "");
	}

	public String getText() {
		return text;
	}

	public String getIcon() {
		return icon;
	}

	@Override
	public Component build() {
		MenuItem mi = new MenuItem(text);
		super.initComponent(mi);

		mi.setIconStyle(icon);

		Iterator<UIGen> it = widgets.iterator();
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			if (com instanceof Menu) {
				mi.setSubMenu((Menu) com);
			}
		}

		return mi;
	}
}

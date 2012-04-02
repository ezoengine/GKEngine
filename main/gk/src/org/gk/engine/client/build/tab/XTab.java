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
package org.gk.engine.client.build.tab;

import java.util.List;

import org.gk.engine.client.build.panel.XPanel;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.xml.client.Node;

public class XTab extends XPanel {

	protected String name, closable, icon;
	protected String onClick;

	public XTab(Node node, List<UIGen> widgets) {
		super(node, widgets);

		name = super.getAttribute("name", "");
		closable = super.getAttribute("closable", "false");
		icon = super.getAttribute("icon", "");

		onClick = super.getAttribute("onClick", "");
	}

	public String getName() {
		return name;
	}

	public String getClosable() {
		return closable;
	}

	public String getIcon() {
		return icon;
	}

	public String getOnClick() {
		return onClick;
	}

	@Override
	public Component build() {
		TabItem tabItem = new TabItem(name);
		super.build(tabItem);
		return tabItem;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		// TabItem特有的屬性
		TabItem tabItem = (TabItem) com;
		tabItem.setClosable(Boolean.parseBoolean(closable));

		if (!icon.equals("")) {
			tabItem.setIconStyle(icon);
		}

		super.addEventListener(tabItem, Events.Select, onClick);
	}
}

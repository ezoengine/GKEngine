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

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.google.gwt.xml.client.Node;

public class XTabPanel extends XComponent {

	private final static String POSITION = "top|bottom";

	protected String select;
	protected String tabPosition;

	public XTabPanel(Node node, List<XTab> widgets) {
		super(node, widgets);

		select = super.getAttribute("select", "");
		tabPosition = super.getAttribute("tabPosition", "");
	}

	public String getSelect() {
		return select;
	}

	public String getTabPosition() {
		return tabPosition;
	}

	@Override
	public Component build() {
		TabPanel tabPanel = new TabPanel();
		tabPanel.setAnimScroll(true);
		tabPanel.setTabScroll(true);

		this.initComponent(tabPanel);
		Iterator<UIGen> it = widgets.iterator();
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			if (com instanceof TabItem) {
				tabPanel.add((TabItem) com);
			}
		}
		tabPanel.setSelection(tabPanel.getItemByItemId(select));
		return tabPanel;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		TabPanel tabPanel = (TabPanel) com;
		if (tabPosition.matches(POSITION)) {
			tabPanel.setTabPosition(TabPosition.valueOf(tabPosition
					.toUpperCase()));
		}
	}
}

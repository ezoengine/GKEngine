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

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.xml.client.Node;

public class XMenu extends XComponent {

	public XMenu(Node node, List widgets) {
		super(node, widgets);
	}

	@Override
	public Component build() {
		Menu menu = new Menu() {
			{
				sinkEvents(com.google.gwt.user.client.Event.KEYEVENTS);
			}

			@Override
			public void onBrowserEvent(Event event) {
				if (event.getKeyCode() == KeyCodes.KEY_LEFT) {
					event.stopPropagation();
				} else {
					super.onBrowserEvent(event);
				}
			}
		};
		super.initComponent(menu);

		Iterator<UIGen> it = widgets.iterator();
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			if (!(com instanceof Window)) {
				menu.add(com);
			}
		}
		return menu;
	}
}

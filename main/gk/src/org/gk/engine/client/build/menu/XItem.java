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

import java.util.List;

import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.xml.client.Node;

public abstract class XItem extends XComponent {

	protected String onClick;

	public XItem(Node node, List widgets) {
		super(node, widgets);

		onClick = super.getAttribute("onClick", "");
	}

	public String getOnClick() {
		return onClick;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		super.addEventListener(com, Events.Select, onClick);
	}
}

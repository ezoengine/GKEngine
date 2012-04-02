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
package org.gk.engine.client.build.portal;

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.xml.client.Node;

public class XPortal extends XComponent {

	protected String columnWidth;

	public XPortal(Node node, List<XPortlet> widgets) {
		super(node, widgets);

		columnWidth = super.getAttribute("columnWidth", ".33,.33,.33");
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	@Override
	public Component build() {
		String[] colWidth = columnWidth.split(",");
		Portal portal = new Portal(colWidth.length);
		super.initComponent(portal);

		for (int i = 0; i < colWidth.length; i++) {
			portal.setColumnWidth(i, Double.parseDouble(colWidth[i]));
		}

		Iterator<UIGen> it = widgets.iterator();
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			if (com instanceof Portlet) {
				XPortlet xp = (XPortlet) ui;
				portal.add((Portlet) com, Integer.parseInt(xp.getColumn()));
			}
		}
		return portal;
	}
}

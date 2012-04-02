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

import java.util.List;

import org.gk.engine.client.build.panel.XContentPanel;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.xml.client.Node;

public class XPortlet extends XContentPanel {

	protected String column;

	public XPortlet(Node node, List<UIGen> widgets) {
		super(node, widgets);

		height = super.getAttribute("height", "150");
		layout = super.getAttribute("layout", "fitlayout");

		column = super.getAttribute("column", "0");
	}

	public String getColumn() {
		return column;
	}

	@Override
	public Component build() {
		Portlet portlet = new Portlet();
		super.build(portlet);
		return portlet;
	}
}

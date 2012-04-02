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
package org.gk.engine.client.build;

import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ScrollContainer;
import com.google.gwt.xml.client.Node;

/**
 * 
 * @author i23250
 * @since 2010/9/10
 */
public abstract class XScrollPanel extends XComponent {

	protected String scroll;

	private final static String SCROLL_MODE = "auto|autox|autoy|always";

	public XScrollPanel(Node node, List widgets) {
		super(node, widgets);

		scroll = super.getAttribute("scroll", "");
	}

	public String getScroll() {
		return scroll;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		// ScrollContainer特有的屬性
		ScrollContainer sc = (ScrollContainer) com;
		if (scroll.matches(SCROLL_MODE)) {
			sc.setScrollMode(Scroll.valueOf(scroll.toUpperCase()));
		}
	}
}

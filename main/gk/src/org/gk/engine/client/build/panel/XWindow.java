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
package org.gk.engine.client.build.panel;

import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.xml.client.Node;

public class XWindow extends XContentPanel {

	protected String closable, draggable, maximizable, max, modal;
	protected String onHide;

	public XWindow(Node node, List widgets) {
		super(node, widgets);

		width = super.getAttribute("width", "500");
		height = super.getAttribute("height", "500");
		visible = super.getAttribute("visible", "false");
		frame = super.getAttribute("frame", "true");
		resizable = super.getAttribute("resizable", "true");

		closable = super.getAttribute("closable", "");
		draggable = super.getAttribute("draggable", "true");
		maximizable = super.getAttribute("maximizable", "false");
		max = super.getAttribute("max", "false");
		modal = super.getAttribute("modal", "false");

		onHide = super.getAttribute("onHide", "");
	}

	public String getClosable() {
		return closable;
	}

	public String getDraggable() {
		return draggable;
	}

	public String getMaximizable() {
		return maximizable;
	}

	public String getMax() {
		return max;
	}

	public String getModal() {
		return modal;
	}

	public String getOnHide() {
		return onHide;
	}

	@Override
	public Component build() {
		Window window = new Window();
		super.build(window);
		if ("true".equals(visible.toLowerCase())) {
			window.setVisible(true);
		}
		return window;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		Window window = (Window) com;
		// window特有的屬性
		if (!closable.equals("")) {
			window.setClosable(Boolean.parseBoolean(closable));
		}
		window.setDraggable(Boolean.parseBoolean(draggable));
		window.setModal(Boolean.parseBoolean(modal));
		if (window.isResizable() != Boolean.parseBoolean(resizable)) {
			window.setResizable(!window.isResizable());
		}
		window.setMaximizable(Boolean.parseBoolean(maximizable)
				|| Boolean.parseBoolean(max));

		addEventListener(window, Events.Hide, onHide);
	}
}

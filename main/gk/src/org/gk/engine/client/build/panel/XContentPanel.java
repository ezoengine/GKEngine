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

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.form.XHeader;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.xml.client.Node;

public class XContentPanel extends XPanel {

	protected String heading, headerVisible, bodyBorder, collapsible;
	protected String frame, position, bodyClass;
	protected String icon;

	public XContentPanel(Node node, List widgets) {
		super(node, widgets);
		heading = super.getAttribute("heading", "");
		headerVisible = super.getAttribute("headerVisible", "true");
		bodyBorder = super.getAttribute("bodyBorder", "false");
		collapsible = super.getAttribute("collapsible", "false");
		frame = super.getAttribute("frame", "false");
		position = super.getAttribute("position", "");
		bodyClass = super.getAttribute("bodyClass", "_unknow_");
		icon = super.getAttribute("icon", "");
	}

	public String getHeading() {
		return heading;
	}

	public String getHeaderVisible() {
		return headerVisible;
	}

	public String getBodyBorder() {
		return bodyBorder;
	}

	public String getCollapsible() {
		return collapsible;
	}

	public String getFrame() {
		return frame;
	}

	public String getPosition() {
		return position;
	}

	public String getBodyClass() {
		return bodyClass;
	}

	public String getIcon() {
		return icon;
	}

	@Override
	public Component build() {
		ContentPanel cp = new ContentPanel();
		this.initComponent(cp);

		for (Iterator<UIGen> it = widgets.iterator(); it.hasNext();) {
			UIGen ui = it.next();
			if (ui instanceof XHeader) {
				XHeader xHeader = (XHeader) ui;
				xHeader.setHeader(cp.getHeader());
				xHeader.build();
			} else {
				Component com = ui.build();
				super.addComponent(cp, com, ui);
			}
		}
		return cp;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		// ContentPanel特有的屬性
		final ContentPanel cp = (ContentPanel) com;
		if (!heading.equals("")) {
			cp.setHeading(heading);
		}
		cp.setHeaderVisible(Boolean.parseBoolean(headerVisible));
		cp.setBodyBorder(Boolean.parseBoolean(bodyBorder));
		cp.setCollapsible(Boolean.parseBoolean(collapsible));
		cp.setFrame(Boolean.parseBoolean(frame));
		if (!position.equals("")) {
			Point p = getPoint(position);
			cp.setStyleAttribute("position", "relative");
			cp.setPosition(p.x, p.y);
		}
		// 如果有設定BodyClass,就在render後，將body設為需要的 className
		if (!getBodyClass().equals("_unknow_")) {
			cp.addListener(Events.AfterLayout, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					cp.getElement("body").setClassName(getBodyClass());
				}
			});
		}

		if (!icon.equals("")) {
			cp.setIconStyle(icon);
		}
	}

	protected Point getPoint(String xy) {
		String[] posXY = xy.split(",");
		return new Point(Integer.parseInt(posXY[0]), Integer.parseInt(posXY[1]));
	}
}

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
package org.gk.engine.client.build.frame;

import org.gk.engine.client.build.panel.XContentPanel;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.xml.client.Node;

public class XFrame extends XContentPanel {

	protected String url;

	public XFrame(Node node) {
		super(node, null);
		layout = "fitlayout";
		headerVisible = super.getAttribute("headerVisible", "false");

		url = super.getAttribute("url", "frame");
	}

	public String getUrl() {
		return url;
	}

	@Override
	public Component build() {
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		super.initComponent(cp);
		Frame frame = new Frame(url);
		cp.add(frame);
		return cp;
	}
}

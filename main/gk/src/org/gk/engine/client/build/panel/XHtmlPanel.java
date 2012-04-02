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

import org.gk.engine.client.build.XComponent;
import org.gk.ui.client.com.panel.gkHtmlContainer;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.xml.client.Node;

public class XHtmlPanel extends XComponent {

	public XHtmlPanel(Node node) {
		super(node, null);
		Node firstNode = node.getFirstChild();
		if (firstNode != null
				&& firstNode.getNodeType() == Node.CDATA_SECTION_NODE) {
			content = firstNode.getNodeValue();
		} else {
			content = node.getChildNodes() + "";
		}
		if (content == null || content.equals("null")) {
			content = "";
		}
	}

	@Override
	public Component build() {
		gkHtmlContainer hc = new gkHtmlContainer(content);
		super.initComponent(hc);
		return hc;
	}
}

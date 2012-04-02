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
package org.gk.engine.client.build.form;

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.gen.UIGen;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Header;
import com.google.gwt.xml.client.Node;

public class XHeader extends XComponent {

	protected Header header;

	protected String heading;
	protected String icon;

	public XHeader(Node node, List<UIGen> widgets) {
		super(node, widgets);

		heading = super.getAttribute("heading", "");
		icon = super.getAttribute("icon", "");
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public String getHeading() {
		return heading;
	}

	public String getIcon() {
		return icon;
	}

	@Override
	public Component build() {
		// 這個判斷只是為了避免程式出錯而已，header不應該為null
		if (header == null) {
			header = new Header();
		}

		if (!heading.equals("")) {
			header.setText(heading);
		}

		if (!icon.equals("")) {
			header.setIconStyle(icon);
		}

		for (Iterator<UIGen> it = widgets.iterator(); it.hasNext();) {
			UIGen ui = it.next();
			header.addTool(ui.build());
		}

		return header;
	}
}

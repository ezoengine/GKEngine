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
package org.gk.engine.client.build.toolbar;

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.gen.UIGen;
import org.gk.ui.client.com.toolbar.gkToolBarIC;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.xml.client.Node;

public class XToolBar extends XComponent {

	protected String separator, msg, msgWidth;

	public XToolBar(Node node, List<UIGen> widgets) {
		super(node, widgets);

		separator = super.getAttribute("separator", "false");
		msg = super.getAttribute("msg", "");
		msgWidth = super.getAttribute("msgWidth", "40%");
	}

	public String getSeparator() {
		return separator;
	}

	public String getMsg() {
		return msg;
	}

	public String getMsgWidth() {
		return msgWidth;
	}

	@Override
	public Component build() {
		gkToolBarIC toolBar = new gkToolBarIC();
		super.initComponent(toolBar);
		// 設定訊息欄位是否visible
		if (!msg.equals("")) {
			toolBar.setMsgVisible(Boolean.parseBoolean(msg));
		}

		toolBar.setMsgWidth(msgWidth);

		Iterator<UIGen> it = widgets.iterator();
		while (it.hasNext()) {
			UIGen ui = it.next();
			Component com = ui.build();
			if (!(com instanceof Window)) {
				toolBar.add(com);
				if (Boolean.parseBoolean(separator)) {
					toolBar.add(new SeparatorToolItem());
				}
			}
		}
		return toolBar;
	}
}

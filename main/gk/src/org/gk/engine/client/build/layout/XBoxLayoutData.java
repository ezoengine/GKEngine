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
package org.gk.engine.client.build.layout;

import java.util.List;

import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.engine.client.utils.LayoutUtils;
import org.gk.engine.client.utils.NodeUtils;

import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.xml.client.Node;

/**
 * Box佈局資料
 * 
 * @author i23250
 * @since 2012/1/10
 */
public class XBoxLayoutData extends XLayoutData {

	private String parentLayout;

	private String flex;
	private String max;
	private String min;

	public String getFlex() {
		return flex;
	}

	public String getMax() {
		return max;
	}

	public String getMin() {
		return min;
	}

	public XBoxLayoutData(Node node, List subNodes) {
		super(node, subNodes);

		flex = super.getAttribute("flex", "");
		max = super.getAttribute("max", "");
		min = super.getAttribute("min", "");

		Node parentNode = node.getParentNode();
		if (parentNode != null) {
			parentLayout = NodeUtils.getNodeValue(parentNode, "layout", "");
		}
	}

	@Override
	public LayoutData getLayoutData() {
		LayoutData ld = null;
		if (parentLayout.matches(LayoutUtils.HBOXLAYOUT)) {
			ld = createHBoxLayoutData();
		} else if (parentLayout.matches(LayoutUtils.VBOXLAYOUT)) {
			ld = createVBoxLayoutData();
		}
		return ld;
	}

	private LayoutData createHBoxLayoutData() {
		HBoxLayoutData box = new HBoxLayoutData();
		if (flex.matches(IRegExpUtils.FLOAT)) {
			box.setFlex(Double.parseDouble(flex));
		}

		if (max.matches(IRegExpUtils.INTEGER)) {
			box.setMaxHeight(Integer.parseInt(max));
		}

		if (min.matches(IRegExpUtils.INTEGER)) {
			box.setMinHeight(Integer.parseInt(min));
		}
		return box;
	}

	private LayoutData createVBoxLayoutData() {
		VBoxLayoutData box = new VBoxLayoutData();
		if (flex.matches(IRegExpUtils.FLOAT)) {
			box.setFlex(Double.parseDouble(flex));
		}

		if (max.matches(IRegExpUtils.INTEGER)) {
			box.setMaxWidth(Integer.parseInt(max));
		}

		if (min.matches(IRegExpUtils.INTEGER)) {
			box.setMinWidth(Integer.parseInt(min));
		}
		return box;
	}
}

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

import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.google.gwt.xml.client.Node;

/**
 * Form佈局資料
 * 
 * @author i23250
 * @since 2012/1/10
 */
public class XFormData extends XLayoutData {

	protected String width;
	protected String height;

	public XFormData(Node node, List subNodes) {
		super(node, subNodes);

		width = super.getAttribute("width", "");
		height = super.getAttribute("height", "");
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	@Override
	public LayoutData getLayoutData() {
		FormData fd = new FormData();

		if (width.matches(IRegExpUtils.INTEGER)) {
			fd.setWidth(Integer.parseInt(width));
		}

		if (height.matches(IRegExpUtils.INTEGER)) {
			fd.setHeight(Integer.parseInt(height));
		}

		return fd;
	}
}

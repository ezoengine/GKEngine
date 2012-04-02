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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.xml.client.Node;

/**
 * Table佈局資料
 * 
 * @author i23250
 * @since 2012/1/10
 */
public class XTableData extends XLayoutData {

	private static final String HORIZATIONAL = "left|center|right";
	private static final String VERTICAL = "top|middle|bottom";

	protected String width;
	protected String height;
	protected String margin;
	protected String colspan;
	protected String rowspan;
	protected String hAlign;
	protected String vAlign;
	protected String padding;

	public XTableData(Node node, List subNodes) {
		super(node, subNodes);

		width = super.getAttribute("width", "");
		height = super.getAttribute("height", "");
		margin = super.getAttribute("margin", "");
		colspan = super.getAttribute("colspan", "");
		rowspan = super.getAttribute("rowspan", "");
		hAlign = super.getAttribute("hAlign", "");
		vAlign = super.getAttribute("vAlign", "");
		padding = super.getAttribute("padding", "");
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getMargin() {
		return margin;
	}

	public String getColspan() {
		return colspan;
	}

	public String getRowspan() {
		return rowspan;
	}

	public String gethAlign() {
		return hAlign;
	}

	public String getvAlign() {
		return vAlign;
	}

	public String getPadding() {
		return padding;
	}

	@Override
	public LayoutData getLayoutData() {
		TableData td = new TableData();

		if (!width.equals("")) {
			td.setWidth(width);
		}

		if (!height.equals("")) {
			td.setHeight(height);
		}

		if (margin.matches(IRegExpUtils.INTEGER)) {
			td.setMargin(Integer.parseInt(margin));
		}

		if (colspan.matches(IRegExpUtils.INTEGER)) {
			td.setColspan(Integer.parseInt(colspan));
		}

		if (rowspan.matches(IRegExpUtils.INTEGER)) {
			td.setRowspan(Integer.parseInt(rowspan));
		}

		if (hAlign.matches(HORIZATIONAL)) {
			td.setHorizontalAlign(HorizontalAlignment.valueOf(hAlign
					.toUpperCase()));
		}

		if (vAlign.matches(VERTICAL)) {
			td.setVerticalAlign(VerticalAlignment.valueOf(vAlign.toUpperCase()));
		}

		if (padding.matches(IRegExpUtils.INTEGER)) {
			td.setPadding(Integer.parseInt(padding));
		}

		return td;
	}
}

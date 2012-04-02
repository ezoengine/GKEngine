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

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.google.gwt.xml.client.Node;

/**
 * Border佈局資料
 * 
 * @author i23250
 * @since 2012/1/10
 */
public class XBorderLayoutData extends XLayoutData {

	private final static String REGION = "east|west|north|south|center";

	protected String region;
	protected String size, maxSize, minSize;
	protected String floatable;
	protected String collapsible;
	protected String split;
	protected String margins;

	public XBorderLayoutData(Node node, List subNodes) {
		super(node, subNodes);

		region = super.getAttribute("region", "");
		size = super.getAttribute("size", "");
		maxSize = super.getAttribute("maxSize", "");
		minSize = super.getAttribute("minSize", "");
		floatable = super.getAttribute("floatable", "true");
		collapsible = super.getAttribute("collapsible", "false");
		split = super.getAttribute("split", "false");
		margins = super.getAttribute("margins", "");
	}

	public String getRegion() {
		return region;
	}

	public String getSize() {
		return size;
	}

	public String getMaxSize() {
		return maxSize;
	}

	public String getMinSize() {
		return minSize;
	}

	public String getFloatable() {
		return floatable;
	}

	public String getCollapsible() {
		return collapsible;
	}

	public String getSplit() {
		return split;
	}

	public String getMargins() {
		return margins;
	}

	@Override
	public LayoutData getLayoutData() {
		BorderLayoutData bd;
		if (region.matches(REGION)) {
			bd = new BorderLayoutData(
					LayoutRegion.valueOf(region.toUpperCase()));
		} else {
			bd = new BorderLayoutData(LayoutRegion.CENTER);
		}

		if (size.matches(IRegExpUtils.POSITIVE_FLOAT + "%?")) {
			if (size.endsWith("%")) {
				bd.setSize(Float.parseFloat(size.replaceAll("%", "")) / 100);
			} else {
				bd.setSize(Float.parseFloat(size));
			}
		}

		if (maxSize.matches(IRegExpUtils.POSITIVE_INTEGER)) {
			bd.setMaxSize(Integer.parseInt(maxSize));
		}

		if (minSize.matches(IRegExpUtils.POSITIVE_INTEGER)) {
			bd.setMinSize(Integer.parseInt(minSize));
		}

		bd.setFloatable(Boolean.parseBoolean(floatable));
		bd.setCollapsible(Boolean.parseBoolean(collapsible));
		bd.setSplit(Boolean.parseBoolean(split));

		if (checkMargins(margins)) {
			Margins mg;
			String[] split = margins.split(",");
			if (split.length == 4) {
				mg = new Margins(Integer.parseInt(split[0]),
						Integer.parseInt(split[1]), Integer.parseInt(split[2]),
						Integer.parseInt(split[3]));
			} else {
				mg = new Margins(Integer.parseInt(split[0]));
			}
			bd.setMargins(mg);
		}
		return bd;
	}

	private static native boolean checkMargins(String content)/*-{
		return content.match(/\d+|\d+(,\d+){3}/) != null;
	}-*/;
}

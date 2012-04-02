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
package org.gk.engine.client.utils;

import org.gk.engine.client.event.IEventConstants;
import org.gk.engine.client.exception.GKEngineException;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.ui.client.com.panel.gkAccordionLayout;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;

public class LayoutUtils {

	private final static String FLOWLAYOUT = "flowlayout|flow";
	private final static String FITLAYOUT = "fitlayout|fit";
	private final static String HROWLAYOUT = "hrow";
	private final static String VROWLAYOUT = "vrow";
	private final static String BORDERLAYOUT = "border";
	private final static String FORMLAYOUT = "formlayout|form";
	private final static String ACCORDIONLAYOUT = "accordionlayout|accordion|acc";
	private final static String CENTERLAYOUT = "center";

	private final static String TABLELAYOUT = "table(,\\d+)?";
	public final static String VBOXLAYOUT = "vbox(:\\w+(,\\w+)?)?";
	public final static String HBOXLAYOUT = "hbox(:\\w+(,\\w+)?)?";

	private final static String HBOXLAYOUTALIGN = "TOP|MIDDLE|BOTTOM|STRETCH|STRETCHMAX";
	private final static String VBOXLAYOUTALIGN = "LEFT|MIDDLE|RIGHT|STRETCH|STRETCHMAX";
	private final static String BOXLAYOUTPACK = "START|CENTER|END";

	/**
	 * 根據輸入的佈局名稱，取得對應的佈局元件
	 * 
	 * @param layoutName
	 * @return Layout
	 */
	public static Layout getPageLayout(String layoutName) {
		Layout layout = createCommonLayout(layoutName);
		if (layout == null) {
			if (layoutName.matches(TABLELAYOUT)) {
				layout = createTableLayout(layoutName);
			} else if (layoutName.matches(HBOXLAYOUT)) {
				layout = createHBoxLayout(layoutName);
			} else if (layoutName.matches(VBOXLAYOUT)) {
				layout = createVBoxLayout(layoutName);
			}
		}
		// 上述的佈局元件若無法取得，則拋出Exception
		if (layout == null) {
			throw new GKEngineException(
					EngineMessages.msg.error_layoutNotCreate(layoutName));
		}
		return layout;
	}

	/**
	 * 建立一般種類的佈局
	 * 
	 * @param layoutName
	 * @return Layout
	 */
	private static Layout createCommonLayout(String layoutName) {
		Layout layout = null;
		if (layoutName.matches(FLOWLAYOUT)) {
			layout = new FlowLayout();
		} else if (layoutName.matches(FITLAYOUT)) {
			layout = new FitLayout();
		} else if (layoutName.matches(HROWLAYOUT)) {
			layout = new RowLayout(Orientation.HORIZONTAL);
		} else if (layoutName.matches(VROWLAYOUT)) {
			layout = new RowLayout(Orientation.VERTICAL);
		} else if (layoutName.matches(BORDERLAYOUT)) {
			layout = new BorderLayout();
		} else if (layoutName.matches(FORMLAYOUT)) {
			layout = new FormLayout();
		} else if (layoutName.matches(ACCORDIONLAYOUT)) {
			layout = new gkAccordionLayout();
		} else if (layoutName.matches(CENTERLAYOUT)) {
			layout = new CenterLayout();
		}
		return layout;
	}

	/**
	 * 建立Table佈局
	 * 
	 * @param layoutName
	 * @return Layout
	 */
	private static Layout createTableLayout(String layoutName) {
		Layout table = null;
		String[] comma = layoutName.split(IEventConstants.SPLIT_COMMA);
		if (comma.length == 2 && !comma[1].equals("0")) {
			table = new TableLayout(Integer.parseInt(comma[1]));
		} else {
			table = new TableLayout();
		}
		return table;
	}

	/**
	 * 建立HBox佈局
	 * 
	 * @param layoutName
	 * @return Layout
	 */
	private static Layout createHBoxLayout(String layoutName) {
		HBoxLayout hbox = new HBoxLayout();
		String[] colon = layoutName.split(IEventConstants.SPLIT_COLON);
		if (colon.length > 1) {
			String[] comma = colon[1].split(IEventConstants.SPLIT_COMMA);
			for (int i = 0; i < comma.length; i++) {
				String name = comma[i].toUpperCase();
				setHBoxLayoutAlign(hbox, name);
				setBoxLayoutPack(hbox, name);
			}
		}
		return hbox;
	}

	/**
	 * 建立VBox佈局
	 * 
	 * @param layoutName
	 * @return Layout
	 */
	private static Layout createVBoxLayout(String layoutName) {
		VBoxLayout vbox = new VBoxLayout();
		String[] colon = layoutName.split(IEventConstants.SPLIT_COLON);
		if (colon.length > 1) {
			String[] comma = colon[1].split(IEventConstants.SPLIT_COMMA);
			for (int i = 0; i < comma.length; i++) {
				String name = comma[i].toUpperCase();
				setVBoxLayoutAlign(vbox, name);
				setBoxLayoutPack(vbox, name);
			}
		}
		return vbox;
	}

	private static void setHBoxLayoutAlign(HBoxLayout hbox, String name) {
		if (name.matches(HBOXLAYOUTALIGN)) {
			hbox.setHBoxLayoutAlign(HBoxLayoutAlign.valueOf(name));
		}
	}

	private static void setVBoxLayoutAlign(VBoxLayout vbox, String name) {
		if (name.matches(VBOXLAYOUTALIGN)) {
			// VBoxLayoutAlign與BoxLayoutPack的CENTER重複，因此對外使用改為MIDDLE，這邊再轉回CENTER
			if (name.equals("MIDDLE")) {
				name = "CENTER";
			}
			vbox.setVBoxLayoutAlign(VBoxLayoutAlign.valueOf(name));
		}
	}

	private static void setBoxLayoutPack(BoxLayout box, String pack) {
		if (pack.matches(BOXLAYOUTPACK)) {
			box.setPack(BoxLayoutPack.valueOf(pack));
		}
	}
}

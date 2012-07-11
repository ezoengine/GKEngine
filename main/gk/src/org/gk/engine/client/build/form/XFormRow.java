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

import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.panel.XFieldSet;
import org.gk.engine.client.build.panel.XPanel;
import org.gk.engine.client.event.IEventConstants;
import org.gk.engine.client.gen.UIGen;
import org.gk.ui.client.com.form.gkFormRow;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.xml.client.Node;

public class XFormRow extends XPanel {

	protected gkFormPanelIC form;

	protected String widthConfig;
	protected String align;
	protected String widthRate;

	public XFormRow(Node node, List<UIGen> widgets) {
		super(node, widgets);
		// Layout需固定為ColumnLayout，因此帶空字串就好，上層會判斷
		layout = "";
		width = super.getAttribute("width", "100%");
		height = super.getAttribute("height", "26");

		widthConfig = super.getAttribute("widthConfig", "");
		align = super.getAttribute("align", "");
		widthRate = super.getAttribute("widthRate", widthConfig);
	}

	public gkFormPanelIC getForm() {
		return form;
	}

	public void setForm(gkFormPanelIC form) {
		this.form = form;
	}

	public String getWidthConfig() {
		return widthConfig;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getAlign() {
		return align;
	}

	public String getWidthRate() {
		return widthRate;
	}

	@Override
	public Component build() {
		gkFormRow row = new gkFormRow(align);
		super.initComponent(row);

		String[] rate = splitAllWidthRate(widthRate, widgets.size());
		for (int i = 0, j = 0; i < widgets.size(); i++) {
			UIGen ui = (UIGen) widgets.get(i);
			if (ui instanceof XFieldSet) {
				((XFieldSet) ui).setForm(form);
			} else if (ui instanceof XFormField) {
				((XFormField) ui).setForm(form);
			}
			Component com = ui.build();
			if (!(com instanceof Window)) {
				XComponent xc = (XComponent) ui;
				row.add(com, rate[j], xc.getWidth(), xc.getHeight());
				j++;
			}
		}
		return row;
	}

	/**
	 * 以「，」切割全部字串，若比totalSize小，則補上預設值
	 * 
	 * @param widthRate
	 * @param totalSize
	 * @return String[]
	 */
	private String[] splitAllWidthRate(String widthRate, int totalSize) {
		String defaultRate = "20%";
		String[] comma = widthRate.split(IEventConstants.SPLIT_COMMA);
		if (comma.length == 1 && comma[0].equals("")) {
			comma[0] = defaultRate;
		}
		if (totalSize > comma.length) {
			String[] temp = new String[totalSize];
			for (int i = 0; i < totalSize; i++) {
				if (i < comma.length) {
					temp[i] = comma[i];
				} else {
					temp[i] = defaultRate;
				}
			}
			comma = temp;
		}
		return comma;
	}
}

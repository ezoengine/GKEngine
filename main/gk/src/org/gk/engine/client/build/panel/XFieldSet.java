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

import java.util.Iterator;
import java.util.List;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.form.XFormField;
import org.gk.engine.client.build.form.XFormRow;
import org.gk.engine.client.build.layout.XLayoutData;
import org.gk.engine.client.gen.UIGen;
import org.gk.ui.client.com.panel.gkFormPanelIC;
import org.gk.ui.client.com.utils.LayoutUtils;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.xml.client.Node;

public class XFieldSet extends XPanel {

	protected gkFormPanelIC form;

	protected String heading;
	protected String collapsible;
	protected String expand;

	public XFieldSet(Node node, List widgets) {
		super(node, widgets);
		// 預設layout為FormLayout
		layout = super.getAttribute("layout", "form");
		// 預設borders為true
		borders = super.getAttribute("borders", "true");

		heading = super.getAttribute("heading", "");
		collapsible = super.getAttribute("collapsible", "false");
		expand = super.getAttribute("expand", "true");
	}

	public gkFormPanelIC getForm() {
		return form;
	}

	public void setForm(gkFormPanelIC form) {
		this.form = form;
	}

	public String getHeading() {
		return heading;
	}

	public String getCollapsible() {
		return collapsible;
	}

	public String getExpand() {
		return expand;
	}

	@Override
	public Component build() {
		FieldSet fs = new FieldSet();
		this.initComponent(fs);

		for (Iterator<UIGen> it = widgets.iterator(); it.hasNext();) {
			UIGen ui = it.next();
			if (ui instanceof XFormRow) {
				((XFormRow) ui).setForm(form);
			} else if (ui instanceof XFormField) {
				((XFormField) ui).setForm(form);
			}

			Component com = ui.build();
			if (!(com instanceof Window)) {
				if (ui instanceof XLayoutData) {
					XLayoutData xLayout = (XLayoutData) ui;
					fs.add(com, xLayout.getLayoutData());
				} else {
					XComponent xc = (XComponent) ui;
					fs.add(com,
							LayoutUtils.createFormData(com, xc.getWidth(),
									xc.getHeight()));
				}
			}
		}
		return fs;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		FieldSet fs = (FieldSet) com;
		fs.setHeading(heading);

		boolean isCollapsible = Boolean.parseBoolean(collapsible);
		if (fs.isCollapsible() != isCollapsible) {
			fs.setCollapsible(isCollapsible);
		}

		boolean expanded = Boolean.parseBoolean(expand);
		if (fs.isExpanded() != expanded) {
			fs.setExpanded(expanded);
		}
	}
}

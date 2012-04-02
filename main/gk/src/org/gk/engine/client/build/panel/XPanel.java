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

import org.gk.engine.client.build.XScrollPanel;
import org.gk.engine.client.build.layout.XLayoutData;
import org.gk.engine.client.event.EventListener;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.utils.LayoutUtils;

import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.xml.client.Node;

/**
 * 
 * @author I21890
 */
public class XPanel extends XScrollPanel {

	protected String layout, onDrop, onDrag, onAfterDrop;

	public XPanel(Node node, List widgets) {
		super(node, widgets);

		layout = super.getAttribute("layout", "");

		onDrop = super.getAttribute("onDrop", "");
		onDrag = super.getAttribute("onDrag", "");
		onAfterDrop = super.getAttribute("onAfterDrop", "");
	}

	public String getLayout() {
		return layout;
	}

	public String getOnDrop() {
		return onDrop;
	}

	public String getOnDrag() {
		return onDrag;
	}

	@Override
	public Component build() {
		LayoutContainer ly = new LayoutContainer();
		build(ly);
		return ly;
	}

	protected void addComponent(LayoutContainer lc, Component com, UIGen ui) {
		// 如果是Window，就不添加到容器中
		if (!(com instanceof Window)) {
			// 如果元件被layout包起來，放元件到容器時會設定layoutData
			if (ui instanceof XLayoutData) {
				XLayoutData xLayout = (XLayoutData) ui;
				lc.add(com, xLayout.getLayoutData());
			} else {
				lc.add(com);
			}
		}
	}

	protected void build(LayoutContainer lc) {
		initComponent(lc);

		for (Iterator<UIGen> it = widgets.iterator(); it.hasNext();) {
			UIGen ui = it.next();
			Component com = ui.build();
			addComponent(lc, com, ui);
		}
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		// 如果gul 有拖放屬性(onDrop)就進行onDrop監聽
		if (onDrop.length() > 0) {
			final EventListener listener = new EventListener(com.getId(),
					onDrop, XPanel.this);
			DropTarget dt = new DropTarget(com) {

				@Override
				protected void onDragDrop(DNDEvent event) {
					super.onDragDrop(event);
					listener.handleEvent(event);
				}
			};
			dt.setOperation(Operation.COPY);
		}
		// LayoutContainer特有的屬性
		LayoutContainer lc = (LayoutContainer) com;
		if (!layout.equals("")) {
			lc.setLayout(LayoutUtils.getPageLayout(layout));
		}
	}
}

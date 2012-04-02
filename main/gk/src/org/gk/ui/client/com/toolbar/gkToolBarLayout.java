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
package org.gk.ui.client.com.toolbar;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.ToolBarLayout;
import com.extjs.gxt.ui.client.widget.menu.HeaderMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;

/**
 * 此類別針對給ToolBar使用，改寫onLayout，提供設定ToolBar訊息欄位寬度(預設為40%)
 * 
 * @author I21890
 * @since 2009/12/08
 */
public class gkToolBarLayout extends ToolBarLayout {

	private El leftTr;
	private El rightTr;
	private El extrasTr;

	private String msgWidth = "40%";

	/**
	 * 設定訊息寬度
	 * 
	 * @param msgWidth
	 */
	public void setMsgWidth(String msgWidth) {
		this.msgWidth = msgWidth;
	}

	@Override
	protected void initMore() {
		if (more == null) {
			moreMenu = new Menu();
			moreMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {

				@Override
				public void handleEvent(MenuEvent be) {
					clearMenu();
					for (Component c : container.getItems()) {
						if (isHidden(c)) {
							addComponentToMenu(be.getContainer(), c);
						}
					}
					// put something so the menu isn't empty
					// if no compatible items found
					if (be.getContainer().getItemCount() == 0) {
						be.getContainer().add(
								new HeaderMenuItem(getNoItemsMenuText()));
					}
				}

			});

			more = new Button();
			more.addStyleName("x-toolbar-more");
			more.setIcon(GXT.IMAGES.toolbar_more());
			more.setMenu(moreMenu);
			ComponentHelper.setParent(container, more);
			if (GXT.isAriaEnabled()) {
				more.setTitle("More items...");
			}
		}
		Element td = insertCell(more, extrasTr, 100);
		if (more.isRendered()) {
			td.appendChild(more.el().dom);
		} else {
			more.render(td);
		}
		if (container.isAttached()) {
			ComponentHelper.doAttach(more);
		}
	}

	@Override
	protected void onLayout(Container<?> container, El target) {
		if (leftTr == null) {
			target.insertHtml(
					"beforeEnd",
					"<table cellspacing=\"0\" class=\"x-toolbar-ct\" role=\"presentation\"><tbody><tr><td class=\"x-toolbar-left\" align=\"left\"><table cellspacing=\"0\" role=\"presentation\"><tbody><tr class=\"x-toolbar-left-row\"></tr></tbody></table></td><td class=\"x-toolbar-right\" align=\"right\" width=\""
							+ msgWidth
							+ "\">"
							+ "<table cellspacing=\"0\" class=\"x-toolbar-right-ct\" role=\"presentation\" width=\"100%\"><tbody><tr><td><table cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr class=\"x-toolbar-right-row\" role=\"presentation\"></tr></tbody></table></td><td><table cellspacing=\"0\" role=\"presentation\"><tbody><tr class=\"x-toolbar-extras-row\"></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table>");
			leftTr = target.child("tr.x-toolbar-left-row");
			rightTr = target.child("tr.x-toolbar-right-row");
			extrasTr = target.child("tr.x-toolbar-extras-row");

			leftTr.dom.setAttribute("role", "presentation");
			rightTr.dom.setAttribute("role", "presentation");
			extrasTr.dom.setAttribute("role", "presentation");
		}
		El side = leftTr;
		int pos = 0;

		for (int i = 0, len = container.getItemCount(); i < len; i++, pos++) {
			Component c = container.getItem(i);
			if (c instanceof FillToolItem) {
				side = rightTr;
				pos = -1;
			} else if (!c.isRendered()) {
				c.render(insertCell(c, side, pos));
				if (i < len - 1) {
					c.el()
							.setStyleAttribute("marginRight",
									getSpacing() + "px");
				} else {
					c.el().setStyleAttribute("marginRight", "0px");
				}
			} else {
				if (!isHidden(c)
						&& !isValidParent(c.el().dom, side.getChildElement(pos))) {
					Element td = insertCell(c, side, pos);
					td.appendChild(c.el().dom);
					if (i < len - 1) {
						c.el().setStyleAttribute("marginRight",
								getSpacing() + "px");
					} else {
						c.el().setStyleAttribute("marginRight", "0px");
					}
				}
			}
		}
		// strip extra empty cells
		cleanup(leftTr);
		cleanup(rightTr);
		cleanup(extrasTr);
		fitToSize(target);
	}
}

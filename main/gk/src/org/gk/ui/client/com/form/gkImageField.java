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
package org.gk.ui.client.com.form;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.form.ImageField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * Provides a convenient wrapper for ImageField that adds
 * onClick、onMouseOut、onMouseOver Event
 * <dl>
 * <dt><b>Events:</b></dt>
 * <dd><b>onClick</b> : onClick(ComponentEvent ce)<br>
 * <div>Fires after clicked.</div>
 * <ul>
 * <li>event : ComponentEvent</li>
 * </ul>
 * </dd>
 * <dd><b>onMouseOut</b> : onMouseOut(ComponentEvent ce)<br>
 * <div>Fires onMouseOut.</div>
 * <ul>
 * <li>event : ComponentEvent</li>
 * </ul>
 * </dd>
 * <dd><b>onMouseOver</b> : onMouseOver(ComponentEvent ce)<br>
 * <div>Fires onMouseOver.</div>
 * <ul>
 * <li>event : ComponentEvent</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>Field Focus</dd>
 * <dd>Field Blur</dd>
 * <dd>Field Change</dd>
 * <dd>Field Invalid</dd>
 * <dd>Field Valid</dd>
 * <dd>Field KeyPress</dd>
 * <dd>Field SpecialKey</dd>
 * <dd>BoxComponent Move</dd>
 * <dd>BoxComponent Resize</dd>
 * <dd>Component Enable</dd>
 * <dd>Component Disable</dd>
 * <dd>Component BeforeHide</dd>
 * <dd>Component Hide</dd>
 * <dd>Component BeforeShow</dd>
 * <dd>Component Show</dd>
 * <dd>Component Attach</dd>
 * <dd>Component Detach</dd>
 * <dd>Component BeforeRender</dd>
 * <dd>Component Render</dd>
 * <dd>Component BrowserEvent</dd>
 * <dd>Component BeforeStateRestore</dd>
 * <dd>Component StateRestore</dd>
 * <dd>Component BeforeStateSave</dd>
 * <dd>Component SaveState</dd>
 * </dl>
 * 
 * 
 */
public class gkImageField extends ImageField {
	/**
	 * Creates a new Image field.
	 */
	public gkImageField() {
		super();
	}

	/**
	 * Creates a new Image field.
	 * 
	 * @param text
	 *            Image location
	 * 
	 */
	public gkImageField(String text) {
		super(text);
	}

	/**
	 * 增加onClick、onMouseOut、onMouseOver功能
	 */
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		// 增加onClick、onMouseOut、onMouseOver功能
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}

	@Override
	public void onComponentEvent(ComponentEvent ce) {
		super.onComponentEvent(ce);
		FieldEvent fe = (FieldEvent) ce;
		fe.setEvent(ce.getEvent());
		switch (ce.getEventTypeInt()) {
		case Event.ONMOUSEOVER:
			onMouseOver(ce);
			break;
		case Event.ONMOUSEOUT:
			onMouseOut(ce);
			break;
		}
	}

	/**
	 * 滑鼠移入操作
	 * 
	 * @param ce
	 */
	protected void onMouseOver(ComponentEvent ce) {
		// addStyleName("x-btn");
		setStyleAttribute("cursor", "pointer");
	}

	/**
	 * 滑鼠移出操作
	 * 
	 * @param ce
	 */
	protected void onMouseOut(ComponentEvent ce) {

	}

}

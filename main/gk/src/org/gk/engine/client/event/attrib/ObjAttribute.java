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
package org.gk.engine.client.event.attrib;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * 提供將指定的元件放入指定的容器中
 * 
 * @author I21890
 * @since 2010/12/3
 */
public class ObjAttribute implements IAttribute {

	@Override
	public Object getAttributeValue(Component com) {
		com.removeFromParent();
		return com;
	}

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof LayoutContainer) {
			LayoutContainer c = (LayoutContainer) com;
			Layout layout = c.getLayout();
			// 如果是fitLayout就將LayoutContainer裡面所有東西移除
			if (layout instanceof FitLayout) {
				c.removeAll();
			}
			Component attachCom = (Component) value;
			c.add(attachCom);
			c.layout(true);
		} else {
			throw new RuntimeException("Wow!  " + com.getId()
					+ " is not My Type!");
		}
	}
}

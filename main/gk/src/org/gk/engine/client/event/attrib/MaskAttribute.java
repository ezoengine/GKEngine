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

/**
 * <title>增加對元件 mask的控制能力</title>
 * 
 * <pre>
 * -=-=-=-=-=-=-=-
 * 設定指定元件遮罩
 * -=-=-=-=-=-=-=-
 *   1.gk.set('xxx.mask','讀取中...');
 *   2.gk.set('xxx.mask','true');  或 gk.set('xxx.mask',true);
 * -=-=-=-=-=-=-=-
 * 設定指定元件取消遮罩
 * -=-=-=-=-=-=-=-
 * 1.gk.set('xxx.mask','');
 * 2.gk.set('xxx.mask','false');  或 gk.set('xxx.mask',false);
 * </pre>
 * 
 * @author I21890
 * @since 2011/8/8
 */
public class MaskAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		String strMsg = ("" + value).toLowerCase();
		if (strMsg.equals("false") || strMsg.length() == 0) {
			com.unmask();
		} else {
			if (strMsg.equals("true")) {
				com.mask();
			} else {
				com.mask("" + strMsg);
			}
		}
	}

	@Override
	public Object getAttributeValue(Component com) {
		return null;
	}
}

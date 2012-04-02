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

import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * 這類別尚未開始使用! 因為setAttribute並沒有包含BaseEvent物件， 除非增加參數，不然目前無法實現
 * <title>Data屬性</title>
 * 
 * <pre>
 *  考慮觸發某事件後，事件觸發源頭會將資料放在該事件對象中，然後fireEvent
 *  如果有物件Listener就會從事件對象中取得資訊。
 *  舉例來說，當Tree監聽 onMouseOver事件，希望觸發onMouseOver時，知道
 *  目前滑鼠是停留在哪個Tree節點上，所以就會透過 data 這屬性從 事件對象中取得.
 *  因此擴充此屬性專門提供  從事件對象中取得資訊
 * </pre>
 * 
 * @author i21890
 * @since 2011/3/2
 */
public class DataAttribute implements IAttribute {

	/**
	 * 取得事件對象中的資訊
	 */
	@Override
	public Object getAttributeValue(Component com) {
		return com.getData(XComponent.DATA);
	}

	/**
	 * 設定資料到指定元件
	 */
	@Override
	public void setAttributeValue(Component com, Object value) {
		com.setData(XComponent.DATA, value);
	}

}

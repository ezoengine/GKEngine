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
package org.gk.engine.client.gen;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * <title>GK元件</title>
 * 
 * <pre>
 * 當GK引擎解析完GUL語法後，會將語法描述的每個畫面元件產生
 * 對應的元件. 例如
 * panel -> XPanel , grid  -> XGrid , form  -> XForm
 * XPanel,XGrid,XForm等都繼承XComponent，
 * XComponent實做UIGen，提供下列方法
 * 1. init()在完成畫面元件生成後會調用此方法
 * 2. build()產生Component元件
 * <img src='http://icsclink.appspot.com/event/put/x/file.download.go?j={"i":"1278497501990_132033.png"}' />
 * </pre>
 * 
 * @author I21890
 * @since 2010/07/07
 */
public interface UIGen {

	/**
	 * 初始化元件(在完成畫面元件生成後會調用此方法)
	 */
	public void init();

	/**
	 * 產生對應xml要顯示的表單
	 * 
	 * @return Component
	 */
	public Component build();
}

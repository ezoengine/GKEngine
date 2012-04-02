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
package org.gk.engine.client.build.layout;

import java.util.List;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.xml.client.Node;

/**
 * Row佈局資料
 * 
 * <pre>
 * 當面版的 layout設為 vrow 或 hrow時，
 * panel裡面就可以使用 row 這標籤，如下面例子
 * <panel layout='vrow'>
 *   <row data='1,1,0'>
 *    #這裡可以放一個Widget   
 *   </row>
 * </panel>
 * 
 * data 依序用來設定 水平百分比，垂直百分比，margin寬度
 * -1: 按照原來Widget設定的寬度或高度
 * 1: 設為全版面
 * 300: >1 就是設定多少像素
 * .3: 表示 30%
 * </pre>
 * 
 * @author I21890
 * @since 2012/1/10
 */
public class XRowData extends XLayoutData {

	protected String data;

	public XRowData(Node node, List subNodes) {
		super(node, subNodes);

		data = super.getAttribute("data", "1,1,0");
	}

	public String getData() {
		return data;
	}

	@Override
	public LayoutData getLayoutData() {
		String[] dataArray = getData().split(",");
		double args1 = Double.parseDouble(dataArray[0]);
		double args2 = Double.parseDouble(dataArray[1]);
		RowData rd = null;
		if (dataArray.length == 3) {
			int args3 = Integer.parseInt(dataArray[2]);
			rd = new RowData(args1, args2, new Margins(args3));
		} else {
			int top = Integer.parseInt(dataArray[2]);
			int right = Integer.parseInt(dataArray[3]);
			int bottom = Integer.parseInt(dataArray[4]);
			int left = Integer.parseInt(dataArray[5]);
			rd = new RowData(args1, args2,
					new Margins(top, right, bottom, left));
		}
		return rd;
	}
}

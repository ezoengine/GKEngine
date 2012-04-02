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
package org.gk.engine.client.build.grid.field;

import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.event.EventCenter;
import org.gk.ui.client.com.grid.column.gkImageColumnConfig;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.ui.Image;

/**
 * Grid圖片欄位建構器
 * 
 * @author i23250
 * @since 2010/8/5
 */
public class GImageFieldBuilder extends GridFieldBuilder {

	public GImageFieldBuilder(String img) {
		super(img);
	}

	@Override
	public ColumnConfig create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkImageColumnConfig(x) {

			@Override
			public void onField(Field field) {
				// 設定GridField的共通屬性
				setAttribute(field, x);

				// 設定toolTip，onMouseOver顯示原圖大小的tooTip，onMouseOut不顯示toolTip
				String showTip = x.getAttribute("showTip", "false");
				if (Boolean.parseBoolean(showTip)) {
					// 生成一個ToolTipConfig给field用
					ToolTipConfig config = new ToolTipConfig();
					config.setMouseOffset(new int[] { 0, 0 });
					config.setAnchor("left");
					// 產生一個Image，可以透過此Image取得原圖大小
					Image image = new Image(x.getValue());
					config.setTemplate(new Template(getHtml(x.getValue(),
							image.getWidth(), image.getHeight())));
					field.setToolTip(config);
				}
				field.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						EventCenter.exec(x.getId(), x.getOnClick(), x, be);
					}
				});
			}
		};
		return cc;
	}

	private native String getHtml(String imgPath, int width, int height) /*-{
		var html = [
				'<div>',
				'<img width="' + width + '" height="' + height + '" src="'
						+ imgPath + '" style="border: 0px solid #ddd">',
				'</div>' ];
		return html.join("");
	}-*/;
}

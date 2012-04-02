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

import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class gkPageSizePlugin extends Component implements ComponentPlugin {

	/** Text to display after the comboBox */
	private TextBox pageSizeText;

	/** ToolBar item to add before the PageSize */
	private String addBefore = "-";

	/** ToolBar item to be added after the PageSizer */
	private String addAfter = null;

	/** The starting position inside the ToolBar */
	private int position = 10;

	// The host component
	private PagingToolBar toolbar = null;

	@Override
	public void init(Component component) {
		// Host component
		toolbar = (PagingToolBar) component;
		toolbar.remove(toolbar.getItem(3)); // 移除beforePage Label
		this.toolbar.insert(new SeparatorToolItem(), position);
		position++;

		// 筆數欄位
		pageSizeText = new TextBox();
		pageSizeText.setTitle(Msg.get.pageSize());
		pageSizeText.setText(toolbar.getPageSize() + "");
		pageSizeText.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onPageChange();
				}
			}
		});
		// 增加valueChangeHandle，当pageSizeText值改变时，重新设定pageSize的值
		// 更新的动作在点击refresh按钮时才会执行
		pageSizeText.addValueChangeHandler(new ValueChangeHandler() {
			@Override
			public void onValueChange(ValueChangeEvent event) {
				setPageSize();
			}
		});
		pageSizeText.setWidth("30px");
		this.toolbar.insert(new WidgetComponent(pageSizeText), position);
		position++;

		if (this.addAfter != null) {
			if ("-".equals(this.addBefore)) {
				this.toolbar.insert(new SeparatorToolItem(), position);
			} else {
				this.toolbar.insert(new LabelToolItem(this.addAfter), position);
			}
		}
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	protected void onPageChange() {
		setPageSize();
		toolbar.refresh();
	}

	/**
	 * 設定pageSize
	 */
	protected void setPageSize() {
		String value = pageSizeText.getText();
		if (value.equals("") || !Util.isInteger(value)) {
			return;
		}
		toolbar.setPageSize(Integer.parseInt(value));
	}
}

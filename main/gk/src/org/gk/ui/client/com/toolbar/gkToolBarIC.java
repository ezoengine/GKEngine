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

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;
import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

/**
 * IC型工具列
 * 
 * @author i23250
 * @since 2010/07/16
 */
public class gkToolBarIC extends ToolBar implements IC {

	protected CoreIC core;

	private gkToolBarLayout layout;

	private LabelToolItem msgBox;
	private LabelToolItem label = new LabelToolItem(Msg.get.msg());

	private FillToolItem fillItem = new FillToolItem();

	public gkToolBarIC() {
		core = new CoreIC(this);
		core.init();

		layout = new gkToolBarLayout();
		setLayout(layout);
		// 預設訊息為歡迎使用
		msgBox = new LabelToolItem(Msg.get.welcome()) {

			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				// 從target設定到td標籤的寬度
				target.setAttribute("width", "100%");
			}
		};
		// 設定訊息靠左
		msgBox.setStyleAttribute("text-align", "left");
		// 設定訊息背景為白色
		msgBox.setStyleAttribute("background", "#FFFFFF");
		// 設定訊息為紅色
		msgBox.setStyleAttribute("color", "#FF0000");
		// 設定訊息標題靠右
		label.setStyleAttribute("text-align", "right");
		// 訊息自動折行
		msgBox.setStyleAttribute("word-wrap", "break-word");
		msgBox.setStyleAttribute("white-space", "normal");
	}

	/**
	 * 設定訊息欄位是否visible
	 * 
	 * @param visible
	 */
	public void setMsgVisible(boolean visible) {
		if (!visible) {
			msgBox.setVisible(visible);
			label.setLabel("");
		}
	}

	public void setMsgBox(String msg) {
		msgBox.setLabel(msg);
	}

	public String getMsgBox() {
		return msgBox.getLabel();
	}

	/**
	 * 設定訊息欄位寬度
	 * 
	 * @param width
	 */
	public void setMsgWidth(String width) {
		layout.setMsgWidth(width);
	}

	@Override
	public void bindEvent() {

	}

	@Override
	public CoreIC core() {
		return core;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		// 最後才加上訊息欄位，讓訊息欄位可以保持在最右邊
		add(fillItem);
		add(label);
		add(msgBox);
	}

	@Override
	public void setInfo(Object info) {
		setMsgBox((String) info);
	}

	@Override
	public Object getInfo() {
		return msgBox.getLabel();
	}

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}
}

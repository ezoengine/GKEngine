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
package org.gk.ui.client.com.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Msg extends Messages {

	Msg get = GWT.create(Msg.class);

	@DefaultMessage("訊息")
	String msg();

	@DefaultMessage("歡迎使用")
	String welcome();

	@DefaultMessage("確認")
	String ok();

	@DefaultMessage("取消")
	String cancel();

	@DefaultMessage("挑選檔案")
	String pickup();

	@DefaultMessage("開始上傳")
	String startUpload();

	@DefaultMessage("上傳失敗")
	String uploadError();

	@DefaultMessage("格式或數值錯誤")
	String formatError();

	@DefaultMessage("確定要")
	String areuSure();

	@DefaultMessage("pageSize")
	String pageSize();

	@DefaultMessage("到")
	String to();

	@DefaultMessage("結束日期不可小於起始日期")
	String dateError();

	@DefaultMessage("結束時間不可小於起始時間")
	String timeError();

	@DefaultMessage("離開")
	String exit();

	@DefaultMessage("刪除")
	String delete();

	@DefaultMessage("民國")
	String chineseYear();

	@DefaultMessage("西元")
	String yearTitle();

	@DefaultMessage("本月")
	String thisMonth();

	@DefaultMessage("今年")
	String thisYear();

	@DefaultMessage("上十年")
	String preDecade();

	@DefaultMessage("下十年")
	String nextDecade();
}
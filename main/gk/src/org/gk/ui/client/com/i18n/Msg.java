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

	@DefaultMessage("上傳成功")
	String uploadSucceess();

	@DefaultMessage("上傳失敗")
	String uploadError();

	@DefaultMessage("格式或數值錯誤")
	String formatError();

	@DefaultMessage("確定要")
	String areuSure();

	@DefaultMessage("增列")
	String autoAddRow();

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

	@DefaultMessage("檔案名稱")
	String filename();

	@DefaultMessage("進展")
	String progress();

	@DefaultMessage("檔案大小")
	String filesize();

	@DefaultMessage("1月")
	String January();

	@DefaultMessage("2月")
	String February();

	@DefaultMessage("3月")
	String March();

	@DefaultMessage("4月")
	String April();

	@DefaultMessage("5月")
	String May();

	@DefaultMessage("6月")
	String June();

	@DefaultMessage("7月")
	String July();

	@DefaultMessage("8月")
	String August();

	@DefaultMessage("9月")
	String September();

	@DefaultMessage("10月")
	String October();

	@DefaultMessage("11月")
	String November();

	@DefaultMessage("12月")
	String December();

	@DefaultMessage("民國")
	String chineseYear();

	@DefaultMessage("西元")
	String yearTitle();

	@DefaultMessage("月")
	String month();

	@DefaultMessage("本月")
	String thisMonth();

	@DefaultMessage("今年")
	String thisYear();

	@DefaultMessage("上十年")
	String preDecade();

	@DefaultMessage("下十年")
	String nextDecade();
}
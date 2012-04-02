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
package org.gk.engine.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Engine i18n messages
 * 
 * @author i23250
 * @since 2011/11/1
 */
public interface EngineMessages extends Messages {

	EngineMessages msg = GWT.create(EngineMessages.class);

	@DefaultMessage("系統錯誤")
	String systemError();

	@DefaultMessage("警告")
	String warning();

	@DefaultMessage("找不到 {0} 屬性名稱，或不支援設定此屬性")
	String error_attributeNotImplement(String attributeName);

	@DefaultMessage("找不到 {0} 事件種類，或不支援此事件")
	String error_eventTypeNotSupport(String typeName);

	@DefaultMessage("無此事件語法：{0}")
	String error_handlerNotFound(String handlerName);

	@DefaultMessage("元件id重複：{0}")
	String error_idDuplication(String key);

	@DefaultMessage("找不到 {0} 欄位種類，請檢查是否有誤")
	String error_invalidFieldType(String type);

	@DefaultMessage("數值設定有誤：元件id={0}，值={1}")
	String error_invalidValue(String id, Object value);

	@DefaultMessage("找不到此id，或該id所指的元件不是js元件：{0}")
	String error_jsNotFound(String id);

	@DefaultMessage("無法建立此佈局，請檢查是否有誤：{0}")
	String error_layoutNotCreate(String layoutName);

	@DefaultMessage("元件庫中找不到 {0} ，或是不合法的標籤名稱")
	String error_libraryNotFound(String nodeName);

	@DefaultMessage("{0} 必須包含一個元件")
	String error_mustContainOneComponent(String name);
}

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
package org.gk.engine.client.build.form.field;

import org.gk.engine.client.build.XComponent;
import org.gk.ui.client.com.form.gkFileUploadField;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * 檔案上傳元件
 * 
 * @author I21890
 * @since 2010/7/26
 */
public class FileUploadFieldBuilder extends FormFieldBuilder {

	public FileUploadFieldBuilder(String popFile) {
		super(popFile);
	}

	@Override
	public Component create() {
		gkFileUploadField field = new gkFileUploadField(getField().getId(),
				getField().getLabel());
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		gkFileUploadField field = new gkFileUploadField(getField().getId(),
				getField().getLabel());
		initField(field);
		return field;
	}

	private void initField(gkFileUploadField field) {
		field.setData(XComponent.DATA, getField().getData());
		String value = getField().getValue();
		String readOnly = getField().getReadOnly();
		if (!readOnly.equals("")) {
			field.setReadOnly(Boolean.parseBoolean(readOnly));
		}
		if (!value.equals("")) {
			field.setValue(value);
		} else {
			field.setValue("./"); // 設定目前目錄
		}
	}
}

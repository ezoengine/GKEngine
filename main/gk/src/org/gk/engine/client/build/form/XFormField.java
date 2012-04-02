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
package org.gk.engine.client.build.form;

import java.util.List;

import org.gk.engine.client.build.field.XField;
import org.gk.engine.client.build.form.field.FormFieldBuilder;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.xml.client.Node;

/**
 * 表單欄位產生元件
 * 
 * <pre>
 * 當  XFormRow 解析 <formRow>...</formRow>裡面所有tag，
 * 只要是 <field />就會產生XFormField物件來產生真正的field物件。
 * </pre>
 * 
 * @author I21890
 * @since 2010/7/26
 */
public class XFormField extends XField {

	private gkFormPanelIC form;

	public XFormField(Node node, List widgets) {
		super(node, widgets);
	}

	public gkFormPanelIC getForm() {
		return form;
	}

	public void setForm(gkFormPanelIC form) {
		this.form = form;
	}

	/**
	 * 產生真正的Field 要根據Type生成不同的field 調用FormFieldBuilder子類進行元件的建構,
	 * 然後取得FormFieldBuilder完成建構的Component
	 * 
	 * @return Component
	 */
	@Override
	public Component build() {
		Component com = FormFieldBuilder.build(this, type);
		this.initComponent(com);
		return com;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);
		// 此方法提供form和grid中Component共有的属性设定
		super.initializeComponent(com);
	}
}
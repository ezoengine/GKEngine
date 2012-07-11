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

import java.util.Iterator;
import java.util.List;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.engine.client.build.XComponent;
import org.gk.engine.client.build.panel.XContentPanel;
import org.gk.engine.client.build.panel.XFieldSet;
import org.gk.engine.client.event.EventCenter;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.com.panel.gkFormPanelIC;
import org.gk.ui.client.com.utils.LayoutUtils;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.google.gwt.xml.client.Node;

/**
 * 表單元件
 * 
 * @author I21890
 * @since 2010/7/26
 */
public class XForm extends XContentPanel {

	protected String align, labelWidth, hideLabels, onValidate;

	public XForm(Node node, List<XComponent> comList) {
		super(node, comList);
		// 預設frame為true
		frame = super.getAttribute("frame", "true");

		align = super.getAttribute("align", "left");
		labelWidth = super.getAttribute("labelWidth", "");
		hideLabels = super.getAttribute("hideLabels", "");
		onValidate = super.getAttribute("onValidate", "");
	}

	public String getAlign() {
		return align;
	}

	public String getLabelWidth() {
		return labelWidth;
	}

	public String getHideLabels() {
		return hideLabels;
	}

	public String getOnValidate() {
		return onValidate;
	}

	@Override
	public Component build() {
		gkFormPanelIC formPanel = new gkFormPanelIC(id) {

			@Override
			protected void addField() {
				for (Iterator<UIGen> it = widgets.iterator(); it.hasNext();) {
					UIGen ui = it.next();
					if (ui instanceof XHeader) {
						XHeader xHeader = (XHeader) ui;
						xHeader.setHeader(this.getHeader());
						xHeader.build();
					} else {
						if (ui instanceof XFormRow) {
							XFormRow xr = (XFormRow) ui;
							xr.setForm(this);
							// 若沒有個別設定XFormRow的align，才由Form統一設定align
							if (xr.getAlign().equals("")) {
								xr.setAlign(align);
							}
						} else if (ui instanceof XFormField) {
							((XFormField) ui).setForm(this);
						} else if (ui instanceof XFieldSet) {
							((XFieldSet) ui).setForm(this);
						}
						Component com = ui.build();
						XComponent xc = (XComponent) ui;
						// 如果是Window，就不添加到容器中
						if (!(com instanceof Window)) {
							add(com,
									LayoutUtils.createFormData(com,
											xc.getWidth(), xc.getHeight()));
						}
					}
				}
			}
		};
		this.initComponent(formPanel);
		initOnValidateAttribute(formPanel);
		return formPanel;
	}

	@Override
	protected void initComponent(Component com) {
		super.initComponent(com);

		gkFormPanelIC form = (gkFormPanelIC) com;
		if (align.matches("left|top|right")) {
			form.setLabelAlign(LabelAlign.valueOf(align.toUpperCase()));
		}

		if (labelWidth.matches(IRegExpUtils.INTEGER)) {
			form.setLabelWidth(Integer.parseInt(labelWidth));
		}

		if (!hideLabels.equals("")) {
			form.setHideLabels(Boolean.parseBoolean(hideLabels));
		}
	}

	/**
	 * 增加onValidtae屬性，依據form驗證結果，與其他元件交互功能
	 * 
	 * @param formPanel
	 */
	private void initOnValidateAttribute(gkFormPanelIC formPanel) {
		if (!onValidate.equals("")) {
			final XForm xForm = this;
			// 第一次頁面刷新的時候檢查
			EventCenter.exec(id, onValidate, xForm, null);
			// 之後欄位的改動產檢查通過此事件訂閱來處理
			formPanel.core().subscribe(formPanel.evtInfoChange(),
					new EventProcess() {

						@Override
						public void execute(String eventId, EventObject eo) {
							EventCenter.exec(id, onValidate, xForm, null);
						}
					});
		}
	}
}

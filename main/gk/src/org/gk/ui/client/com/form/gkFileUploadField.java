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
package org.gk.ui.client.com.form;

import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBus;
import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.i18n.Msg;
import org.gk.ui.client.com.panel.gkFileUploadPanel;
import org.gk.ui.client.com.utils.StringUtils;
import org.gk.ui.client.icon.Icons;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

public class gkFileUploadField extends AdapterField {

	protected HorizontalPanel hp;
	protected HorizontalPanel linkPanel;
	protected Button uploadButton;
	protected String beanName = "";

	public gkFileUploadField() {
		super(null);
		init();
		widget = hp;
	}

	public gkFileUploadField(String id) {
		super(null);
		setId(id);
		init();
		widget = hp;
	}

	public gkFileUploadField(String id, String label) {
		this(id);
		setFieldLabel(label);
	}

	private void init() {
		hp = new HorizontalPanel();
		linkPanel = new HorizontalPanel();
		linkPanel.setVerticalAlign(VerticalAlignment.MIDDLE);

		uploadButton = new Button();
		uploadButton.setIconStyle("icsc-create");

		uploadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				gkFileUploadPanel filePanel = new gkFileUploadPanel(
						gkFileUploadField.this);
				filePanel.show();
			}
		});

		hp.add(uploadButton);
		hp.add(linkPanel);
	}


	private void initFileUploadBean() {
		String gkData = getData(StringUtils.DATA);
		if (gkData == null || gkData.equals("")) {
			beanName = "handler";
		} else {
			String[] bean = gkData.split(":");
			if (bean.length > 1) {
				if (bean[0].equalsIgnoreCase("bean")) {
					beanName = bean[1];
				}
			}
		}
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		initFileUploadBean();
		if (isRendered()) {
			loadFileList();
		}
	}

	public void loadFileList() {
		EventBus.get().publishRemote(
				new EventObject(beanName
						+ gkFileUploadPanel.Event.GET_FILE_LIST, value + ""),
				new EventProcess() {
					@Override
					public void execute(String eventId, EventObject eo) {
						if (eo.getInfoType().equals("list")) {
							refreshLinkPanel(eo.getInfoList());
						}
					}
				});
	}

	private void refreshLinkPanel(List links) {

		linkPanel.removeAll();

		if (isReadOnly() || !isEnabled()) {
			uploadButton.disable();
		}

		for (int i = 0; i < links.size(); i++) {
			Map map = (Map) links.get(i);
			String fileName = (String) map.get("name");
			String filePath = (String) map.get("path");
			final String id;
			if (filePath != null) {
				id = filePath + fileName;
			} else {
				id = fileName;
			}

			gkImageField image = new gkImageField(Icons.get.cross()
					.getSafeUri().asString());

			if (!isReadOnly() && isEnabled()) {
				image.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (Window.confirm(Msg.get.areuSure()
								+ Msg.get.delete() + "?")) {
							EventBus.get().publishRemote(
									new EventObject(beanName
											+ gkFileUploadPanel.Event.DELETE,
											id), new EventProcess() {

										@Override
										public void execute(String eventId,
												EventObject eo) {
											loadFileList();
										}
									});
						}
					}
				});
			} else {
				image = new gkImageField(Icons.get.text().getSafeUri()
						.asString()) {
					@Override
					protected void onMouseOver(ComponentEvent ce) {
						//
					}
				};
			}

			Anchor anchor = new Anchor(fileName);
			if (isEnabled()) {
				anchor.setHref("event/put/def/" + beanName
						+ gkFileUploadPanel.Event.DOWNLOAD + ".go?id=" + id);
				anchor.setTarget("_blank");
			}
			linkPanel.add(image);
			linkPanel.add(anchor);
		}
		hp.layout();
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.readOnly = readOnly;
		reloadFileList();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		reloadFileList();
	}

	private void reloadFileList() {
		uploadButton.enable();
		if (isRendered()) {
			loadFileList();
		}
	}

}

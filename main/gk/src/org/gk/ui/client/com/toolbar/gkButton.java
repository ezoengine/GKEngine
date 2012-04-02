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

import java.util.Iterator;
import java.util.List;

import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;

/**
 * gkButton
 * 
 * @author I23979
 * @since 2009/12/07
 */
public class gkButton extends Button {

	// 是否要開啟確認視窗
	private boolean showConfirm;
	private String value, confirmText;
	private List listenerList = new gkList();

	public gkButton() {
		init();
	}

	public gkButton(String text, boolean showConfirm) {
		setText(text);
		setShowConfirm(showConfirm);
		init();
	}

	public gkButton(String text, String confirmText) {
		this(text, true);
		if (!confirmText.toLowerCase().equals("true")) {
			setConfirmText(confirmText);
		}
	}

	public String getConfirmText() {
		return confirmText;
	}

	public void setConfirmText(String confirmText) {
		this.confirmText = confirmText;
	}

	private void init() {
		super.addListener(Events.Select, new SelectionListener() {

			@Override
			public void componentSelected(final ComponentEvent ce) {
				// 判斷是否要跳出確認視窗
				if (isShowConfirm()) {
					String msg = confirmText != null ? confirmText : Msg.get
							.areuSure() + getText() + "?";

					MessageBox.confirm(getText(), msg,
							new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									Button btn = be.getButtonClicked();
									// Yes才做事情
									if (btn.getText().equals(
											GXT.MESSAGES.messageBox_yes())) {
										fireSelectionEvents(ce);
									}
								}
							});
				} else {
					fireSelectionEvents(ce);
				}
			}
		});
	}

	public boolean isShowConfirm() {
		return showConfirm;
	}

	public void setShowConfirm(boolean showConfirm) {
		this.showConfirm = showConfirm;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void addSelectionListener(SelectionListener<ButtonEvent> listener) {
		listenerList.add(listener);
	}

	@Override
	public void addListener(EventType eventType, Listener listener) {
		if (eventType == Events.Select) {
			listenerList.add(listener);
		} else {
			super.addListener(eventType, listener);
		}
	}

	private void fireSelectionEvents(ComponentEvent ce) {
		Iterator<Listener> it = listenerList.iterator();
		while (it.hasNext()) {
			it.next().handleEvent(ce);
		}
	}
}

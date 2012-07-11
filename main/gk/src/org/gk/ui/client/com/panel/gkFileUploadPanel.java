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
package org.gk.ui.client.com.panel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jfreecode.gwt.event.client.bus.EventBusImpl;

import org.gk.ui.client.com.form.gkFileUploadField;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.gkListGridIC;
import org.gk.ui.client.com.i18n.Msg;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.HTML;

public class gkFileUploadPanel {

	private Dialog myDialog;
	private gkListGridIC listGrid;
	private Button startUpload;
	private List fileUploadList = Collections.EMPTY_LIST;

	private String fileType;
	private String fileDescript;
	private String maxSize;
	protected String beanName;

	private gkFileUploadField field;

	protected Listener<MessageBoxEvent> callback;

	public interface Event {
		public final static String SAVE = ".save";
		public final static String GET_FILE_LIST = ".getFileList";
		public final static String DOWNLOAD = ".download";
		public final static String DELETE = ".delete";

	}

	public gkFileUploadPanel(gkFileUploadField field) {
		this.field = field;
		initJS(this);
		beanName = field.getBeanName();
	}

	public void show() {
		// 目前flash的版本有session無法取得問題 及關閉 window視窗時會有 Null Reference Exceptions in
		// IE8 的問題
		// 暫時先切到HTML版本使用

		// if (checkSWF()) {
		// // flash模式
		// flashShow();
		// } else {
		// 傳統模式
		normalShow();
		// }
	}

	/**
	 * 傳統上傳顯示視窗
	 */
	private void normalShow() {
		myDialog = createDialog(300, 120, new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		String filePath = "";
		if (((String) field.getValue()).indexOf("..") == -1)
			filePath = (String) field.getValue();
		formPanel.setAction("event/multipart/eventBus/"
				+ (beanName + Event.SAVE) + EventBusImpl.go + "?fileId="
				+ filePath);
		formPanel.setEncoding(Encoding.MULTIPART);
		formPanel.setMethod(Method.POST);

		final HTML uploadStatus = new HTML();
		formPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent be) {
				startUpload.setEnabled(false);
				if (be.getResultHtml() == null
						|| be.getResultHtml().trim().equals("")) {
					uploadStatus.setHTML("<font color=red>"
							+ Msg.get.uploadError() + "</font>");
				} else {
					uploadCompleted("");
				}

			}
		});

		HorizontalPanel hp = new HorizontalPanel();
		HorizontalPanel hp_Msg = new HorizontalPanel();

		final FileUploadField fileUpload = new FileUploadField();

		fileUpload.getMessages().setBrowseText(Msg.get.pickup());
		fileUpload.setName("name");

		// 開始上傳按鈕
		startUpload = new Button(Msg.get.startUpload());
		startUpload.setEnabled(false);
		startUpload.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				fileUpload.setReadOnly(false);
				formPanel.submit();
			};
		});

		fileUpload.addListener(Events.OnChange, new Listener<ComponentEvent>() {

			@Override
			public void handleEvent(ComponentEvent be) {
				// Because the upload path will html5 specification is based on
				// the path to security X:\\fakePath\\aaaa.gul,
				// So just to display only the filename
				String path = fileUpload.getValue();
				path = path.substring(path.lastIndexOf("\\") + 1, path.length());
				fileUpload.setValue(path);
				startUpload.setEnabled(true);
			}
		});

		hp.add(fileUpload);
		hp.add(startUpload);
		hp_Msg.add(uploadStatus);

		formPanel.add(hp);
		formPanel.add(hp_Msg);

		myDialog.add(formPanel);
		myDialog.show();
	}

	private Dialog createDialog(int width, int height, Layout layout) {
		Dialog dialog = new Dialog() {

			@Override
			protected void onButtonPressed(Button button) {
				super.onButtonPressed(button);
				removeFromParent();
			}
		};
		dialog.setBodyBorder(false);
		dialog.setWidth(width);
		dialog.setHeight(height);
		dialog.setLayout(layout);
		dialog.setModal(true);
		dialog.setHideOnButtonClick(true);

		dialog.getButtonById(Dialog.OK).setText(Msg.get.exit());

		return dialog;
	}

	private ColumnConfig createColumnConfig(String id, String name, int width) {
		ColumnConfig cc = new ColumnConfig(id, name, width);
		cc.setSortable(false);
		cc.setMenuDisabled(true);
		return cc;
	}

	private GridCellRenderer createProgressRenderer() {
		GridCellRenderer progressRenderer = new GridCellRenderer() {

			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
				ProgressBar pb2 = new ProgressBar();
				return pb2;
			}
		};
		return progressRenderer;
	}

	// 檔案加入list
	public void addItem(String id, String name, String size) {
		gkMap m = new gkMap();
		m.put("filename", name);
		m.put("filesize", getSize(size));
		m.put("process", "false");
		m.put("id", id);
		m.put("cancelId", id);
		myDialog.getButtonById(Dialog.OK).setEnabled(false);
		startUpload.setEnabled(true);
		listGrid.getListItem().add(m);
		listGrid.getGrid().getStore().add(m);
	}

	// queue結束時..控制ui
	public void queueDone() {
		startUpload.setEnabled(false);
		myDialog.getButtonById(Dialog.OK).setEnabled(true);
	}

	// progressbar的更新
	public void updateProgressBar(String id, String bytescomplete,
			String totalbytes) {
		int index = 0;

		List list = (List) listGrid.getInfo();
		for (int i = 0; i < list.size(); i++) {
			Map data = (Map) list.get(i);
			if (data.get("id").equals(id)) {
				index = i;
				break;
			}
		}
		Double total = Double.valueOf(bytescomplete)
				/ Double.valueOf(totalbytes);

		((ProgressBar) listGrid.getGrid().getView().getWidget(index, 2))
				.updateProgress(total, Math.round(100 * total) + "%");
		if (total > 0.0 && total < 1.0) { // 上傳過程中
			startUpload.setEnabled(false);
			setButtonUI(index, 3, false);
			setButtonUI(index, 4, true);
		} else { // 上傳未開始或完成
			((Map) ((List) listGrid.getInfo()).get(index)).put("process",
					"true");
			setButtonUI(index, 3, true);
			setButtonUI(index, 4, false);
		}
	}

	public void uploadCompleted(String id) {
		field.loadFileList();
	}

	private void setButtonUI(int rowIndex, int colIndex, boolean enable) {
		((Button) listGrid.getGrid().getView().getWidget(rowIndex, colIndex))
				.setEnabled(enable);
	}

	// 取得單位為kb的檔案大小
	private String getSize(String size) {
		return Integer.parseInt(size) / 1000.0 + "";
	}

	private static native void initJS(gkFileUploadPanel hg)/*-{
		$wnd.addItem = function(id, name, size) {
			hg.@org.gk.ui.client.com.panel.gkFileUploadPanel::addItem(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(id, name, size);
		}

		$wnd.updateProgressBar = function(id, bytescomplete, totalbytes) {
			hg.@org.gk.ui.client.com.panel.gkFileUploadPanel::updateProgressBar(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(id, bytescomplete, totalbytes);
		}

		$wnd.queueDone = function(name, size) {
			hg.@org.gk.ui.client.com.panel.gkFileUploadPanel::queueDone()();
		}

		$wnd.uploadCompleted = function(id) {
			hg.@org.gk.ui.client.com.panel.gkFileUploadPanel::uploadCompleted(Ljava/lang/String;)(id);
		}

	}-*/;

	/**
	 * 檢查是否安裝Flash
	 */
	private native boolean checkSWF()/*-{
		try {
			//IE_Flash
			var ie_swf = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
			return (ie_swf) ? true : false;
		} catch (e) {
			try {
				//FF_or_Chrome_Flash()
				var ff_swf = navigator.plugins["Shockwave Flash"];
				return (ff_swf) ? true : false;
			} catch (e) {
				return false;
			}
		}
	}-*/;
}
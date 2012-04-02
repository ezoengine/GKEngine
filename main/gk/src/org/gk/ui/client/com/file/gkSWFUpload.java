package org.gk.ui.client.com.file;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

public class gkSWFUpload extends HTMLPanel {
	public final static String CLASS_VERSION = "$Revision: 1.7 $ $Date: 2011/09/30 08:52:24 $";

	private String id;
	// 單檔大小限制
	private String maxSize = "1GB";
	// 一次上傳檔案數，0為無限制
	private int fileAmt = 0;
	private String fileType = "";
	private String fileDescript = "";
	private int width, height;
	private boolean debug = false;
	private String text;
	private String url;
	private int singleMode;

	public gkSWFUpload(String pickup, int width, int height, String singleMode,
			String fileType, String fileDescript, String maxSize) {
		super("<div id='" + DOM.createUniqueId() + "'></div>");
		id = ((Element) getElement().getFirstChild()).getId();
		this.width = width;
		this.height = height;
		this.text = pickup;
		setSize("" + width, "" + height);
		// 設定挑選檔案視窗模式，0是可挑選多檔
		this.singleMode = Integer.parseInt(singleMode);
		this.fileType = fileType;
		this.fileDescript = fileDescript;
		this.maxSize = maxSize;
	}

	@Override
	public void onAttach() {
		initSWFUpload(id, url, text, width, height, debug, fileAmt, maxSize,
				fileType, fileDescript, singleMode, GWT.getModuleBaseURL());
		super.onAttach();
	}

	public gkSWFUpload setBean(String beanAndMethod) {
		url = "event/multipart/eventBus/" + beanAndMethod;
		return this;
	}

	private native void initSWFUpload(String id, String upload_url,
			String text, int width, int height, boolean debug, int fileAmt,
			String maxSize, String fileType, String fileDescript,
			int singleMode, String moduleBaseUrl)/*-{
		swfup = new $wnd.SWFUpload(
				{
					// Backend Settings
					upload_url : upload_url,
					file_size_limit : maxSize,
					file_types : fileType,
					file_types_description : fileDescript,
					// file_dialog_complete_handler  : postSelectFiles,
					file_upload_limit : fileAmt, // Zero means unlimited
					// Button settings
					button_image_url : moduleBaseUrl
							+ "../../res/swfupload/XPButtonNoText_61x22.png", // Relative to the SWF file
					button_placeholder_id : id,
					button_width : width,
					button_height : height,
					button_text : text,
					button_text_style : '.button { font-family: Helvetica, Arial, sans-serif; font-size: 14pt; } .buttonSmall { font-size: 10pt; }',
					button_text_top_padding : 1,
					button_text_left_padding : 5,
					button_action : singleMode,
					// Flash Settings
					flash_url : moduleBaseUrl
							+ "../../res/swfupload/swfupload.swf",
					custom_settings : {
						upload_target : "divFileProgressContainer"
					},
					// Debug Settings
					debug : debug
				});

		//開啟選取檔案視窗前觸發，移除DataList所有item
		swfup.fileDialogStart = function() {
		}

		//檔案加入上傳佇列時觸發，將檔案加入DataList
		swfup.fileQueued = function(file) {
			$wnd.addItem(file.id, file.name, file.size + '');
		}

		//全部上傳完成
		swfup.fileDialogComplete = function(numFilesSelected, numFilesQueued) {

		}

		//上傳中觸發，用來更新進度條
		swfup.uploadProgress = function(file, bytescomplete, totalbytes) {
			$wnd
					.updateProgressBar(file.id, bytescomplete + '', totalbytes
							+ '');
		}

		//上傳成功
		swfup.uploadSuccess = function(file, server_data) {
			$wnd.updateProgressBar(file.id, '100', '100');
		}

		//取消發生中斷時
		swfup.uploadError = function(file, errorCode, message) {
			if (swfup.getStats().files_queued == 0) {
				$wnd.queueDone();
			}
		}

		//檔案上傳失敗
		swfup.fileQueueError = function(file, errorCode, message) {
		}

		//當上傳完一個檔案時觸發，開啟佇列中的下個檔案上傳
		swfup.uploadComplete = function(file) {

			//upload Complete
			$wnd.uploadCompleted(file.id);

			if (swfup.getStats().files_queued > 0) {
				//上傳完畢後,必須手動觸發下一筆要上傳
				$wnd.startUploadloop(file.id);
			} else {
				$wnd.queueDone();
			}
		}

		//DEBUG 
		swfup.debug = function(message) {
		}

	}-*/;

	// 從上傳佇列中移除一檔案
	public native void deleteItem(String id)/*-{
		swfup.cancelUpload(id);
	}-*/;

	// 開始上傳
	public native void startUpload(String id)/*-{
		swfup.startUpload(id);
	}-*/;

	public native void startUpload()/*-{
		swfup.startUpload();
	}-*/;

	// 取消上傳
	public native void stopUpload(String id)/*-{
		swfup.stopUpload(id);
	}-*/;

	public native void getStats()/*-{
		$wnd.alert(swfup.getStats());
	}-*/;

}

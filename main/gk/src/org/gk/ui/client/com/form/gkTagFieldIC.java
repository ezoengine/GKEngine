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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcess;

import org.gk.ui.client.com.CoreIC;
import org.gk.ui.client.com.IC;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * 提供TagField功能
 * 
 * @author W10447
 * @since 2010-12-20
 */
public class gkTagFieldIC extends AdapterField implements IC {

	protected CoreIC core;
	private LayoutContainer hpAll;
	private LayoutContainer hp;
	private List<String> tagValues;

	private Window w;

	@Override
	public void linkInfo(Object info) {
		throw new RuntimeException("not implemented yet!");
	}

	public interface Event {
		String ADDITEM = ".addItem";
		String DELITEM = ".delItem";
		String SELITEM = ".selItem";
	}

	public String addItem() {
		return getId() + Event.ADDITEM;
	}

	public String delItem() {
		return getId() + Event.DELITEM;
	}

	public String selItem() {
		return getId() + Event.SELITEM;
	}

	@Override
	public CoreIC core() {
		return core;
	}

	@Override
	public Object getInfo() {
		return tagValues;
	}

	@Override
	public void setInfo(Object info) {
		if (info instanceof List) {
			tagValues = (List) info;
			tagsEvents();
		}
	}

	public gkTagFieldIC(String id) {
		super(null);
		setId(id);
		core = new CoreIC(this);
		core.init();
		hpAll = new LayoutContainer();
		widget = hpAll;
		tagValues = new ArrayList<String>();
		init();
		setHideLabel(true);
	}

	public gkTagFieldIC(String id, String label) {
		super(null);
		setId(id);
		core = new CoreIC(this);
		core.init();
		hpAll = new LayoutContainer();
		widget = hpAll;
		tagValues = new ArrayList<String>();
		init();
		setLabelSeparator("");
		setFieldLabel(label);
	}

	@Override
	public void bindEvent() {
		// 監聽增加tag的事件操作
		core.subscribe(addItem(), new EventProcess() {
			@Override
			public void execute(String eventId, EventObject eo) {
				tagsEvents(eo);
			}
		});
		// 監聽刪除tag的事件操作
		core.subscribe(delItem(), new EventProcess() {
			@Override
			public void execute(String eventId, EventObject eo) {
				tagsEvents(eo);
			}
		});
	}

	private void init() {
		setHeight(30);
		hp = new LayoutContainer();// tags panel add tagPanel and buttons
		// 新增按鈕
		final Button addBtn = new Button();
		addBtn.setIconStyle("icsc-create");
		addBtn.setIconAlign(IconAlign.TOP);
		addBtn.setStyleAttribute("margin", "1px 6px 5px 1px;");
		addBtn.setStyleAttribute("float", "left");

		hpAll.add(hp);
		hpAll.add(addBtn);
		// 彈出窗口
		w = new Window();
		w.setHeading("Add " + getFieldLabel());
		w.setMinWidth(10);
		w.setMinHeight(10);
		w.setWidth(195);
		w.setHeight(60);
		w.setResizable(false);
		w.setVisible(false);
		// 彈出窗口中的輸入欄位TextField
		final TextField<String> tf = new TextField<String>();
		tf.setSize(181, 28);
		tf.focus();
		tf.setInputStyleAttribute("border-style", "solid");
		tf.setInputStyleAttribute("display", "inline-block;");

		w.add(tf);// 輸入欄位TextField添加到Window中

		// add icon MouseDown Event
		addBtn.addListener(Events.OnMouseDown, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (w.isVisible()) {
					w.hide();
				}
			}
		});

		// add icon OnMouseUp Event
		addBtn.addListener(Events.OnMouseUp, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (!w.isVisible()) {
					int left = addBtn.getAbsoluteLeft() - 20;
					int top = addBtn.getAbsoluteTop() - 70;
					if (top < 0) {
						top = 1;
						left = addBtn.getAbsoluteLeft() + 30;
					}
					w.setPagePosition(left, top);
					w.show();
				}
			}
		});

		// 文字視窗監聽"Enter"按鍵被按下的事件，將輸入的文字送到後端儲存。
		tf.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyDown(ComponentEvent event) {
				if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
					if (tf.getValue() != null && !tf.getValue().equals("")
							&& !isExit(tf.getValue())) {
						tagValues.add(tf.getValue());
						core.getBus().publish(
								new EventObject(addItem(), tagValues));
						tf.clear();
					}
				}
			}
		});

	}

	private void tagsEvents(EventObject eo) {
		hp.removeAll();
		Iterator list = eo.getInfoList().iterator();
		while (list.hasNext()) {
			// every tag and x icon add to tgPanel container
			LayoutContainer tgPanel = new LayoutContainer();
			String s = list.next().toString();
			// 刪除x
			final LayoutContainer xx = new LayoutContainer();
			xx.setStyleAttribute("background",
					"url(../res/images/icsc/del.png)");
			xx.setSize(9, 9);
			xx.setStyleAttribute("overflow", "hidden");
			xx.setStyleAttribute("float", "left");
			xx.setStyleAttribute("position", "absolute");
			// tag label
			final LabelField anchor = new LabelField(s) {
				@Override
				protected void afterRender() {
					super.afterRender();
					xx.setPagePosition(
							this.getAbsoluteLeft() + this.getOffsetWidth() - 3,
							this.getAbsoluteTop() - 1);
					xx.render(this.getElement());
					this.el().appendChild(xx.getElement());
					xx.setVisible(false);
				}
			};
			anchor.setStyleAttribute("text-decoration", "underline");
			anchor.setStyleAttribute("cursor", "pointer");
			anchor.setStyleAttribute("float", "left");
			anchor.setStyleAttribute("color", "#003EA8");
			anchor.setStyleAttribute("margin", "3px 6px 6px 1px;");
			anchor.setStyleAttribute("white-space", "nowrap");
			anchor.setTitle("Search for tagged with  " + anchor.getText());

			// 每個tag value都要addListener監聽被選取的事件，並將tag本身的value拋出去。
			anchor.sinkEvents(Events.OnClick.getEventCode());
			anchor.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					core.getBus().publish(
							new EventObject(selItem(), anchor.getText()));
				}
			});
			// 每個tag value都要監聽mouseOver的事件，事件發生後，tag value的右上方會出現刪除的icon(灰色)。
			anchor.sinkEvents(Events.OnMouseOver.getEventCode());
			anchor.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setVisible(true);
				}
			});

			// 每個tag value要監聽mouseOut的事件，事件發生後，刪除的icon會消失。
			anchor.sinkEvents(Events.OnMouseOut.getEventCode());
			anchor.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setVisible(false);
				}
			});

			// 刪除的icon要監聽mouseOver的事件，事件發生後，icon的顏色會變成紅色
			xx.sinkEvents(Events.OnMouseOver.getEventCode());
			xx.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setStyleAttribute("background-color", "red");
				}
			});
			// 刪除的icon要監聽mouseOut的事件，事件發生後，icon的顏色會變成灰色。
			xx.sinkEvents(Events.OnMouseOut.getEventCode());
			xx.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setStyleAttribute("background",
							"url(../res/images/icsc/del.png)");
				}
			});

			// 刪除的icon要監聽click的事件，事件發生後，拋出刪除的event，同時將tag
			// value送到後端，將該value自tagField中移除。
			xx.sinkEvents(Events.OnClick.getEventCode());
			xx.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					String delVal = anchor.getText();
					if (delVal != null && isExit(delVal)) {
						tagValues.remove(delVal);
						core.getBus().publish(
								new EventObject(delItem(), tagValues));
					}
					xx.hide();
				}
			});
			tgPanel.add(anchor);
			tgPanel.add(xx);
			hp.add(tgPanel);
		}
		// force Executes the container's layout
		hp.layout();
		hp.show();
	}

	private void tagsEvents() {
		hp.removeAll();
		if (tagValues == null && tagValues.size() == 0) {
			return;
		}
		Iterator list = tagValues.iterator();
		while (list.hasNext()) {
			// every tag and x icon add to tgPanel container
			LayoutContainer tgPanel = new LayoutContainer();
			String s = list.next().toString();
			// 刪除x
			final LayoutContainer xx = new LayoutContainer();
			xx.setStyleAttribute("background",
					"url(../res/images/icsc/del.png)");
			xx.setSize(9, 9);
			xx.setStyleAttribute("overflow", "hidden");
			xx.setStyleAttribute("float", "left");
			xx.setStyleAttribute("position", "absolute");
			// tag label
			final LabelField anchor = new LabelField(s) {
				@Override
				protected void afterRender() {
					super.afterRender();
					xx.setPagePosition(
							this.getAbsoluteLeft() + this.getOffsetWidth() - 3,
							this.getAbsoluteTop() - 1);
					xx.render(this.getElement());
					this.el().appendChild(xx.getElement());
					xx.setVisible(false);
				}
			};
			anchor.setStyleAttribute("text-decoration", "underline");
			anchor.setStyleAttribute("cursor", "pointer");
			anchor.setStyleAttribute("float", "left");
			anchor.setStyleAttribute("color", "#003EA8");
			anchor.setStyleAttribute("margin", "3px 6px 6px 1px;");
			anchor.setStyleAttribute("white-space", "nowrap");
			anchor.setTitle("Search for tagged with  " + anchor.getText());

			// 每個tag value都要addListener監聽被選取的事件，並將tag本身的value拋出去。
			anchor.sinkEvents(Events.OnClick.getEventCode());
			anchor.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					core.getBus().publish(
							new EventObject(selItem(), anchor.getText()));
				}
			});
			// 每個tag value都要監聽mouseOver的事件，事件發生後，tag value的右上方會出現刪除的icon(灰色)。
			anchor.sinkEvents(Events.OnMouseOver.getEventCode());
			anchor.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setVisible(true);
				}
			});

			// 每個tag value要監聽mouseOut的事件，事件發生後，刪除的icon會消失。
			anchor.sinkEvents(Events.OnMouseOut.getEventCode());
			anchor.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setVisible(false);
				}
			});

			// 刪除的icon要監聽mouseOver的事件，事件發生後，icon的顏色會變成紅色
			xx.sinkEvents(Events.OnMouseOver.getEventCode());
			xx.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setStyleAttribute("background-color", "red");
				}
			});
			// 刪除的icon要監聽mouseOut的事件，事件發生後，icon的顏色會變成灰色。
			xx.sinkEvents(Events.OnMouseOut.getEventCode());
			xx.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					xx.setStyleAttribute("background",
							"url(../res/images/icsc/del.png)");
				}
			});

			// 刪除的icon要監聽click的事件，事件發生後，拋出刪除的event，同時將tag
			// value送到後端，將該value自tagField中移除。
			xx.sinkEvents(Events.OnClick.getEventCode());
			xx.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					String delVal = anchor.getText();
					if (delVal != null && isExit(delVal)) {
						tagValues.remove(delVal);
						core.getBus().publish(
								new EventObject(delItem(), tagValues));
					}
					xx.hide();
				}
			});
			tgPanel.add(anchor);
			tgPanel.add(xx);
			hp.add(tgPanel);
		}
		// force Executes the container's layout
		hp.layout();
		hp.show();
	}

	/**
	 * 後端設資料
	 * 
	 * @param values
	 */
	public void setValue(List<String> values) {
		this.tagValues = values;
	}

	/**
	 * 取得所有tags
	 * 
	 * @return List
	 */
	public List<String> getValues() {
		return this.tagValues;
	}

	/**
	 * 檢測tag是否已在tags中
	 * 
	 * @param tag
	 * @return boolean
	 */
	public boolean isExit(String tag) {
		if (this.tagValues.contains(tag))
			return true;
		else
			return false;
	}

	/**
	 * 設定新增彈出窗口的title
	 * 
	 * @param title
	 */
	public void setAddHeading(String title) {
		if (w.isRendered()) {
			w.setHeading(title);
			w.layout();
		} else {
			w.setHeading(title);
		}
	}
}

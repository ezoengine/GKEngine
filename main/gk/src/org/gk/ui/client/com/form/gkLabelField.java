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

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.Element;

public class gkLabelField extends LabelField {

	private String inputStyle;
	private String inputStyles = "";

	@Override
	public void setInputStyleAttribute(String attr, String value) {
		if (rendered) {
			getInputEl().setStyleAttribute(attr, value);
		} else {
			inputStyles += attr + ":" + value + ";";
		}
	}

	@Override
	public void addInputStyleName(String style) {
		if (rendered) {
			El inputEl = getInputEl();
			if (inputEl != null) {
				inputEl.addStyleName(style);
			}
		} else {
			inputStyle = inputStyle == null ? style : inputStyle + " " + style;
		}
	}

	@Override
	public void removeInputStyleName(String style) {
		if (rendered) {
			El inputEl = getInputEl();
			if (inputEl != null) {
				inputEl.removeStyleName(style);
			}
		} else if (inputStyle != null && style != null) {
			String[] s = inputStyle.split(" ");
			inputStyle = "";
			for (int i = 0; i < s.length; i++) {
				if (!s[i].equals(style)) {
					inputStyle += " " + s[i];
				}
			}
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		// 提供上層未提供的inputStyle設定
		if (inputStyle != null) {
			addInputStyleName(inputStyle);
			inputStyle = null;
		}

		// 提供上層未提供的inputStyles設定
		if (inputStyles != null && !inputStyles.equals("")) {
			getInputEl().applyStyles(inputStyles);
			inputStyles = null;
		}
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		if (isFireChangeEventOnSetValue()) {
			fireEvent(Events.Change);
		}
	}
}

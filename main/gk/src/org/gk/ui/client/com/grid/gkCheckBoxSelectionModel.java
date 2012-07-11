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
package org.gk.ui.client.com.grid;

import java.util.Arrays;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;

public class gkCheckBoxSelectionModel extends CheckBoxSelectionModel {

	private boolean autoSelect = true;

	public boolean isAutoSelect() {
		return autoSelect;
	}

	public void setAutoSelect(boolean autoSelect) {
		this.autoSelect = autoSelect;
	}

	public gkCheckBoxSelectionModel() {
		// 設定SelectionMode為SIMPLE才有多筆選項之功能(for gxt2.1)
		selectionMode = SelectionMode.SIMPLE;
	}

	@Override
	protected void handleMouseDown(GridEvent e) {
		ModelData md = listStore.getAt(e.getRowIndex());
		// 點選row中任一column都可以讓checkbox打勾，但只有點選checkBox才能取消，符合中冠先前多筆編輯操作的習慣
		int checkBoxColumnIdx = 0;
		if (md != null) {
			if (isSelected(md)) {
				// 如果是選中，不變。如果是點擊到checkBox則取消選中
				if (e.getColIndex() == checkBoxColumnIdx) {
					doDeselect(Arrays.asList(md), false);
				}
			} else if (e.isShiftKey() && lastSelected != null) {
				// 按住shift鍵多選
				if (autoSelect) {
					select(listStore.indexOf(lastSelected), e.getRowIndex(),
							e.isControlKey());
				}
			} else {
				if (autoSelect || e.getColIndex() == checkBoxColumnIdx) {
					doSelect(Arrays.asList(md), true, false);
				}
			}
		}
		e.cancelBubble();
	}

	@Override
	protected void onKeyPress(GridEvent e) {
		// 遮蔽按space鍵進行select，unselect問題
	}

	@Override
	protected void onKeyDown(GridEvent e) {
		// 在grid裡當操作的cell為TextArea時，操作上下方向鍵不反應selectionModel的動作，因為selectionModel的動作會失去cell的焦點
		String tag = e.getTarget().getTagName();
		if (!"textarea".equalsIgnoreCase(tag)) {
			super.onKeyDown(e);
		}
	}

	@Override
	protected void onKeyUp(GridEvent e) {
		String tag = e.getTarget().getTagName();
		if (!"textarea".equalsIgnoreCase(tag)) {
			super.onKeyUp(e);
		}
	}
}

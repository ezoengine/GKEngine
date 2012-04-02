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
package org.gk.ui.client.com.utils;

import java.util.Iterator;
import java.util.Map;

import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class TreeUtils {

	private static void appendNodeId(TreeNode node, StringBuffer sb) {
		String id = node.getModel().get(gkXMLTreePanelIC.ID);
		if (id == null || id.equals("")) {
			id = node.getModel().get(gkXMLTreePanelIC.NAME);
		}
		sb.append(id);
	}

	public static String getNodeId(TreeNode node) {
		int idx = -1;
		StringBuffer sb = new StringBuffer();
		appendNodeId(node, sb);
		if (node.getParent() != null) {
			idx = node.getParent().indexOf(node);
		}
		return sb.append(":").append(idx) + "";
	}

	public static String getNodeId(TreeNode putNode, TreeNode node, int seq) {
		StringBuffer sb = new StringBuffer();
		int idx = seq;
		if (idx >= 0) {
			idx = putNode.getParent().indexOf(putNode) + seq;
			putNode = putNode.getParent();
		}
		appendNodeId(putNode, sb);
		return sb.append(":").append(idx) + "";
	}

	/**
	 * 取得傳進來TreeNode當下的路徑，以及此節點位於父節點的第幾個位置
	 * 
	 * @param node
	 * @return
	 */
	public static String getPath(TreeNode node) {
		int idx = -1;
		StringBuffer sb = new StringBuffer();
		_getPath(node, sb);
		if (node.getParent() != null) {
			idx = node.getParent().indexOf(node);
		}
		return sb.append(":").append(idx) + "";
	}

	public static String getPath(TreeNode putNode, TreeNode node, int seq) {
		StringBuffer sb = new StringBuffer();
		int idx = seq;
		if (idx >= 0) {
			idx = putNode.getParent().indexOf(putNode) + seq;
			putNode = putNode.getParent();
		}
		_getPath(putNode, sb);
		String nodeName = node.getModel().get(gkXMLTreePanelIC.NAME);
		return sb.append('/').append(nodeName).append(":").append(idx) + "";
	}

	private static void _getPath(TreeNode node, StringBuffer sbuf) {
		String name = node.getModel().get(gkXMLTreePanelIC.NAME);
		sbuf.insert(0, "/" + name);
		if (node.getParent() != null) {
			_getPath(node.getParent(), sbuf);
		}
	}

	public static void updateSelectNode(gkXMLTreePanelIC tree, Map v) {
		ModelData md = tree.getTree().getSelectionModel().getSelectedItem();
		Node parentNode = md.get(gkXMLTreePanelIC.NODE);
		// 建立新增節點的xml資訊
		String name = (String) v.get(gkXMLTreePanelIC.NAME);
		Element newNode = parentNode.getOwnerDocument().createElement(name);
		parentNode.appendChild(newNode);
		// 將設定的資訊抄到node資訊裡面
		Iterator<String> it = v.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!key.equals(gkXMLTreePanelIC.NAME)) {
				newNode.setAttribute(key, (String) v.get(key));
			}
		}
		v.put(gkXMLTreePanelIC.NODE, newNode);
		// TreeStore store = tree.getTree().getStore();
		// store.add(md, v, false);
	}
}

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
package org.gk.engine.client.event.attrib;

import java.util.Iterator;
import java.util.Map;

import org.gk.engine.client.Engine;
import org.gk.ui.client.com.form.gkMap;
import org.gk.ui.client.com.grid.gkGridIC;
import org.gk.ui.client.com.grid.gkMultiEditorGridIC;
import org.gk.ui.client.com.tree.dir.gkTreeDirPanelIC;
import org.gk.ui.client.com.tree.xml.gkXMLTreePanelIC;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Add屬性
 * 
 * @author I21890
 * @since 2010/12/1
 */
public class AddAttribute implements IAttribute {

	@Override
	public void setAttributeValue(Component com, Object value) {
		if (com instanceof ComboBox) {
			ComboBox combo = (ComboBox) com;
			// 如果是comboBox , 可接受 字串或 Map
			if (value instanceof String) {
				combo.getStore().add(
						new gkMap("text", (String) value).add("value", value));
			} else if (value instanceof Map) {
				Map data = (Map) value;
				Iterator<String> it = data.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					combo.getStore().add(
							new gkMap("text", key).add("value", data.get(key)));
				}
			}
			combo.getPropertyEditor().setList(combo.getStore().getModels());
		} else if (com instanceof gkGridIC) {
			// 如果為dejgGridIC，則調用addNewRow方法新增一行
			gkGridIC gridIC = com instanceof gkMultiEditorGridIC ? ((gkMultiEditorGridIC) com)
					.getOrigenalGridIC() : (gkGridIC) com;

			if (value != null && value instanceof gkMap) {
				gridIC.addRow((gkMap) value);
			} else if (value instanceof String) {
				if (((String) value).toLowerCase().equals("true"))
					gridIC.addRow();
			} else {
				gridIC.addRow();
			}
		} else if (com instanceof gkXMLTreePanelIC) {
			addTreeNode((gkXMLTreePanelIC) com, (Map) value);

		} else if (com instanceof TabPanel) {
			TabPanel tabPanel = (TabPanel) com;
			if (value instanceof String) {
				// 其他則表示要經由GUL語法產生面板
				LayoutContainer lc = new LayoutContainer();
				Engine.get().renderPanel((String) value, lc, true);
				TabItem newTab = (TabItem) lc.getItem(0);
				lc.removeAll();
				tabPanel.add(newTab);

				/*
				 * 透過 Engine render 時會將新的 LayoutContainer 給予 id 記錄在
				 * Engine.renderPanelCom，因刪除 Tab 時無法得知 LayoutContainer
				 * id，避免資料不同步問題，目前先不紀錄
				 */
				Engine.get().removeRenderPanelComponent(lc.getId());
			}
		} else if (com instanceof gkTreeDirPanelIC) {
			addTreeDirNode((gkTreeDirPanelIC) com, (Map) value);
		}
	}

	/**
	 * <pre>
	 * Map的key分別為nodeData(存放要插入節點的xml資訊),parentNode(要插入在哪個父節點第幾個位置)
	 * key:parentNode , value: {'nodeId':'??'  ,  'idx':-1}
	 * @param tree
	 * @param value
	 * </pre>
	 */
	private void addTreeNode(gkXMLTreePanelIC tree, Map value) {
		// nodeId
		gkMap addInfo = new gkMap(value);
		assert (addInfo.containsKey(gkXMLTreePanelIC.PARENT_NODE)) : "parentNode not found"
				+ value;
		assert (addInfo.containsKey(gkXMLTreePanelIC.NODE_DATA)) : "nodeData not found"
				+ value;
		// 根據parentNode提供的資訊找到TreeNode
		Map parentNodeInfo = addInfo.get(gkXMLTreePanelIC.PARENT_NODE);
		String parentNodeId = (String) parentNodeInfo.get(gkXMLTreePanelIC.ID);
		ModelData parentMd = tree.getTree().getStore().findModel(parentNodeId);
		assert parentMd != null : "parentNode not found exception:"
				+ parentNodeInfo;
		// 建立新增節點的xml資訊
		String nodeData = (String) addInfo.get(gkXMLTreePanelIC.NODE_DATA);
		Node newXmlNode = XMLParser.parse("<root>" + nodeData + "</root>")
				.getFirstChild();
		tree.preprocessNode(tree.getTree().getStore(), newXmlNode, parentMd);
		Node node = (Node) parentMd.get(gkXMLTreePanelIC.NODE);
		newXmlNode.getParentNode().removeChild(newXmlNode);
		node.appendChild(newXmlNode.getFirstChild());
	}

	/**
	 * <pre>
	 * Map的key分別為nodeData(存放要插入節點的name資訊，也就是新增的檔案名稱),parentNode(要插入在哪個父節點的id)
	 * @param tree
	 * @param value
	 * </pre>
	 */
	private void addTreeDirNode(gkTreeDirPanelIC tree, Map value) {
		gkMap addInfo = new gkMap(value);
		assert (addInfo.containsKey(gkTreeDirPanelIC.PARENT_NODE)) : "parentNode not found"
				+ value;
		assert (addInfo.containsKey(gkTreeDirPanelIC.NODE_DATA)) : "nodeData not found"
				+ value;

		// 根據parentNode提供的資訊找到TreeNode
		Map parentNodeInfo = addInfo.get(gkTreeDirPanelIC.PARENT_NODE);
		String parentNodeId = (String) parentNodeInfo.get(gkTreeDirPanelIC.ID);
		ModelData md = tree.getStore().findModel(parentNodeId);
		assert md != null : "parentNode not found exception:" + parentNodeInfo;

		// 建立新節點的資訊
		String name = addInfo.get(gkTreeDirPanelIC.NODE_DATA) + "";
		String path = (String) md.get(gkTreeDirPanelIC.PATH) + "" + name + "/";
		/*******************************/
		// 本機(未切war)測試專用
		if (path.startsWith("./")) {
			path = path.replace("./", "");
		}
		/*******************************/

		gkMap item = new gkMap();
		item.put(gkTreeDirPanelIC.NAME, name);
		item.put(gkTreeDirPanelIC.PATH, path);
		tree.getStore().add(md, item, false);
	}

	@Override
	public Object getAttributeValue(Component com) {
		return null;
	}
}

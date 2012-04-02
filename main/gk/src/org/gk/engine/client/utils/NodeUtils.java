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
package org.gk.engine.client.utils;

import java.util.Map;

import org.gk.engine.client.exception.GULErrorException;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.FastMap;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class NodeUtils {

	/**
	 * 取得該節點的所有屬性
	 * 
	 * @param node
	 * @return Map
	 */
	public static Map getAttributes(Node node) {
		Map attrMap = new FastMap();
		NamedNodeMap attrs = node.getAttributes();
		for (int index = 0; index < attrs.getLength(); index++) {
			Node attrNode = attrs.item(index);
			attrMap.put(attrNode.getNodeName(), attrNode.getNodeValue());
		}
		return attrMap;
	}

	/**
	 * 取得節點值
	 * 
	 * @param node
	 * @param nameItem
	 * @param def
	 * @return String
	 */
	public static String getNodeValue(Node node, String nameItem, String def) {
		Node n = node.getAttributes().getNamedItem(nameItem);
		if (n == null) {
			// 將原本拿att Name區分大小寫，改成不分大小寫
			n = node.getAttributes().getNamedItem(nameItem.toLowerCase());
			if (n == null)
				return def;
		}
		return n.getNodeValue();
	}

	/**
	 * 解析GUL
	 * 
	 * @param gul
	 * @return Document
	 */
	public static Document parseGUL(String gul) {
		Document doc = null;
		try {
			doc = XMLParser.parse(gul);
			// 檢查是否有parsererror的tag出現，有的話表示出問題了
			NodeList nodes = doc.getElementsByTagName("parsererror");
			if (nodes != null && nodes.getLength() > 0) {
				throw new GULErrorException(nodes.item(0).getChildNodes() + "");
			}
			// 由於IE執行normalize會出錯，故排除IE
			if (!GXT.isIE) {
				doc.normalize();
			}
			// 移除空白，以免換行沒拿到節點內容
			XMLParser.removeWhitespace(doc);
		} catch (Exception e) {
			// 將parse發生的錯誤轉成GULErrorException發送出去
			throw new GULErrorException(e.getMessage());
		}
		return doc;
	}
}

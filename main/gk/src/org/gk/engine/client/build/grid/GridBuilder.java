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
package org.gk.engine.client.build.grid;

import java.util.List;

import org.gk.engine.client.build.Builder;
import org.gk.engine.client.build.gul.XGUL;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.utils.ComLibrary;
import org.gk.ui.client.com.form.gkList;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class GridBuilder extends Builder {

	public GridBuilder(String nodeName) {
		super(nodeName);
	}

	@Override
	public void processNode(List<UIGen> uiGenList, Node node) {
		NodeList nodes = node.getChildNodes();
		List fields = new gkList();
		pickupFieldByNodes(uiGenList, fields, nodes);
		XGrid grid = new XGrid(node, fields);
		uiGenList.add(grid);
	}

	private void pickupFieldByNodes(List<UIGen> uiGenList, List fields,
			NodeList nodes) {
		// 如果子節點是<field/>就放入List
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			String nName = n.getNodeName();
			// 如果是元件庫元件,就將此Node取代成元件庫的元件
			if (ComLibrary.contains(nName)) {
				ComLibrary.overrideNode(n);
				NodeList nlist = ComLibrary.replaceNode(nName, n);
				addField(uiGenList, fields, nlist);
			} else {
				addField(uiGenList, fields, n);
			}
		}
	}

	private void addField(List<UIGen> uiGenList, List fields, NodeList nodes) {
		// 如果子節點是<field/>就放入List
		for (int i = 0; i < nodes.getLength(); i++) {
			addField(uiGenList, fields, nodes.item(i));
		}
	}

	private void addField(List<UIGen> uiGenList, List fields, Node node) {
		String nName = node.getNodeName().toLowerCase();
		// gul標籤支持使用javascript產生需要的欄位
		if (nName.startsWith("gul")) {
			Node outerNode = nProvider.getPreprocessNode().getParentNode();
			String gulSyntax = new XGUL(node).genSyntax(outerNode);
			NodeList nList = XMLParser.parse("<root>" + gulSyntax + "</root>")
					.getFirstChild().getChildNodes();
			pickupFieldByNodes(uiGenList, fields, nList);
		} else if (nName.startsWith("field") || nName.startsWith("header")
				|| nName.startsWith("aggrow")) {
			XGridField field = new XGridField(node);
			if (nName.startsWith("header") || nName.startsWith("aggrow")) {
				field.setType(nName);
			}
			fields.add(field);
		}
	}
}

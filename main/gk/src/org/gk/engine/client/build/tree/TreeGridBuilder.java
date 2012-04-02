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
package org.gk.engine.client.build.tree;

import java.util.ArrayList;
import java.util.List;

import org.gk.engine.client.build.Builder;
import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.gen.UIGen;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class TreeGridBuilder extends Builder {

	public TreeGridBuilder(String nodeName) {
		super(nodeName);
	}

	@Override
	public void processNode(List<UIGen> nodeList, Node node) {
		NodeList nodes = node.getChildNodes();
		List fields = new ArrayList();
		// 如果子節點是<field/>就放入List
		for (int i = 1; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n.getNodeName().startsWith("field")
					|| n.getNodeName().startsWith("group")
					|| n.getNodeName().startsWith("header")
					|| n.getNodeName().startsWith("aggRow")) {
				XGridField field = new XGridField(n);
				if (n.getNodeName().startsWith("header")
						|| n.getNodeName().startsWith("aggRow")) {
					field.setType(n.getNodeName());
				}
				fields.add(field);
			}
		}
		XTreeGrid treeGrid = new XTreeGrid(node, fields);
		nodeList.add(treeGrid);
	}
}

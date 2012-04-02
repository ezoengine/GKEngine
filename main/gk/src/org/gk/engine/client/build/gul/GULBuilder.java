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
package org.gk.engine.client.build.gul;

import java.util.List;

import org.gk.engine.client.build.Builder;
import org.gk.engine.client.gen.UIGen;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * JavaScript Builder
 * 
 * @author Marty
 * @since 2012/03/27
 */
public class GULBuilder extends Builder {

	public GULBuilder(String processNodeName) {
		super(processNodeName);
	}

	@Override
	public void processNode(List<UIGen> nodeList, Node node) {
		XGUL xGUL = new XGUL(node);
		// 跑引擎建構流程解析GUL
		Node outerNode = nProvider.getPreprocessNode().getParentNode();
		Node idNode = outerNode.getAttributes().getNamedItem("id");
		String syntax = xGUL.getGULSyntax(idNode != null ? idNode
				.getNodeValue() : "");
		syntax = "<root>" + syntax + "</root>";
		NodeList nList = XMLParser.parse(syntax).getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			NodeList childNodeList = nList.item(i).getChildNodes();
			parserNode(nodeList, childNodeList);
		}
	}
}

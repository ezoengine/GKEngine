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
package org.gk.engine.client.build;

import java.util.List;

import jfreecode.gwt.event.client.bus.EventObject;
import jfreecode.gwt.event.client.bus.EventProcessImpl;

import org.gk.engine.client.IEngine;
import org.gk.engine.client.gen.UIGen;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public abstract class Builder {

	protected static INodeProvider nProvider;

	public static void attach(INodeProvider np) {
		nProvider = np;
		BuilderFactory.createBuilders();
		BuilderFactory.createLayoutBuilders();
		BuilderFactory.createFormFieldBuilders();
		BuilderFactory.createGridFieldBuilders();
	}

	public Builder(String processNodeName) {
		String[] nName = processNodeName.split(",");
		for (int i = 0; i < nName.length; i++) {
			IEngine.builder.subscribe(nName[i].toLowerCase(),
					new EventProcessImpl(processNodeName) {
						@Override
						public void execute(String eventId, EventObject eo) {
							processNode(eo.getInfoList(),
									nProvider.getPreprocessNode());
						}
					});
		}
	}

	public void parserNode(List<UIGen> nodeList, NodeList nodes) {
		nProvider.parserNode(nodeList, nodes);
	}

	public void parserNode(List<UIGen> nodeList, Node node) {
		nProvider.parserNode(nodeList, node);
	}

	/**
	 * 處理傳入的node，並將結果存入傳入的nodeList
	 * 
	 * @param nodeList
	 * @param node
	 */
	public abstract void processNode(List<UIGen> nodeList, Node node);
}

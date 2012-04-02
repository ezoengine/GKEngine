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
package org.gk.engine.client.build.layout;

import java.util.List;

import org.gk.engine.client.build.EngineDataStore;
import org.gk.engine.client.exception.GKEngineException;
import org.gk.engine.client.gen.UIGen;
import org.gk.engine.client.i18n.EngineMessages;
import org.gk.engine.client.utils.NodeUtils;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.google.gwt.xml.client.Node;

/**
 * 佈局資料
 * 
 * @author i23250
 * @since 2012/1/10
 */
public abstract class XLayoutData implements UIGen {

	private Node node;

	protected String id;
	protected List subNodes;

	public XLayoutData(Node node, List subNodes) {
		this.node = node;
		this.subNodes = subNodes;

		id = getAttribute("id", node.getNodeName() + "-" + hashCode());
		EngineDataStore.addUIGenNode(id, this);
	}

	public String getId() {
		return id;
	}

	@Override
	public Component build() {
		if (subNodes.isEmpty()) {
			throw new GKEngineException(
					EngineMessages.msg.error_mustContainOneComponent(node
							.getNodeName()));
		}

		UIGen ui = (UIGen) subNodes.get(0);
		Component com = ui.build();
		return com;
	}

	@Override
	public void init() {

	}

	/**
	 * 取得屬性值
	 * 
	 * @param nodeName
	 * @param defaultValue
	 * @return String
	 */
	public String getAttribute(String nodeName, String defaultValue) {
		return NodeUtils.getNodeValue(node, nodeName, defaultValue);
	}

	/**
	 * 取得佈局資料(由子類別實作)
	 * 
	 * @return LayoutData
	 */
	public abstract LayoutData getLayoutData();
}

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

import org.gk.engine.client.gen.UIGen;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 提供目前所有節點和準備要處理的節點
 * 
 * @author I21890
 * @since 2009/01/12
 */
public interface INodeProvider {

	/**
	 * 取得目前處理的節點
	 * 
	 * @return Node
	 */
	public Node getPreprocessNode();

	/**
	 * 使用遞迴方式解析XML所有節點，找到節點就做事件發佈，只要有訂閱的就會負責處理該節點如何加入UI元件
	 * 
	 * @param nodeList
	 * @param nodes
	 */
	public void parserNode(List<UIGen> nodeList, NodeList nodes);

	/**
	 * 解析XML節點
	 * 
	 * @param list
	 * @param node
	 */
	public void parserNode(List<UIGen> list, Node node);
}

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

import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.xml.client.Node;

public class XGUL extends XComponent {
	public XGUL(Node node) {
		Node firstNode = node.getFirstChild();
		if (firstNode != null) {
			content = firstNode.getNodeValue();
		}
		if (content == null || content.equals("null")) {
			content = "";
		}
	}

	public String genSyntax(Node outerNode) {
		// 可取得目前解析
		Node idNode = outerNode.getAttributes().getNamedItem("id");
		String syntax = getGULSyntax(idNode != null ? idNode.getNodeValue()
				: "");
		return syntax;
	}

	private native String evalJS(String outerId, String content)/*-{
		var f = "(function(){var outerId='" + outerId + "';" + content + "})()";
		return $wnd.eval(f);
	}-*/;

	@Override
	public Component build() {
		return null;
	}

	public String getGULSyntax(String outerId) {
		return evalJS(outerId, content);
	}
}

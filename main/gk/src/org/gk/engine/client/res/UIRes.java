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
package org.gk.engine.client.res;

import java.util.Map;

import org.gk.ui.client.com.form.gkMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface UIRes extends ClientBundle {
	UIRes get = GWT.create(UIRes.class);

	public static final Map<String, ImageResource> icon = new gkMap()
			.fill("form", UIRes.get.imgForm())
			.fill("folder", UIRes.get.imgFolder())
			.fill("file", UIRes.get.imgFile()).fill("doc", UIRes.get.imgDoc())
			.fill("star", UIRes.get.imgStar())
			.fill("lightning", UIRes.get.imgLightning());

	@Source("form.gif")
	ImageResource imgForm();

	@Source("folder.gif")
	ImageResource imgFolder();

	@Source("file.gif")
	ImageResource imgFile();

	@Source("document.gif")
	ImageResource imgDoc();

	@Source("star.png")
	ImageResource imgStar();

	@Source("lightning.png")
	ImageResource imgLightning();

	@Source("gk_parser-min.js")
	TextResource parserJS();
}

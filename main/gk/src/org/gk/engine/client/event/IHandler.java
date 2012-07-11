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
package org.gk.engine.client.event;

import java.util.List;

import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.event.BaseEvent;

/**
 * 事件處理器介面
 * 
 * @author i23250
 * @since 2010/9/24
 */
public interface IHandler {

	/**
	 * 處理輸入的sources與targets內容
	 * 
	 * @param xComId
	 * @param sources
	 * @param targets
	 * @param xCom
	 * @param be
	 */
	public void process(String xComId, List sources, List targets,
			XComponent xCom, BaseEvent be);
}

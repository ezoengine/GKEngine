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

import org.gk.engine.client.build.XComponent;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * 事件Listener
 * 
 * @author i23250
 * @since 2010/9/24
 */
public class EventListener implements Listener<BaseEvent> {

	private String id;
	private String gulAttribute;
	private XComponent com;

	public EventListener(String id, String gulAttribute, XComponent com) {
		this.id = id;
		this.gulAttribute = gulAttribute;
		this.com = com;
	}

	@Override
	public void handleEvent(BaseEvent be) {
		EventCenter.exec(id, gulAttribute, com, be);
	}
}

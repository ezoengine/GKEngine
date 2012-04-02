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

import java.util.Map;

import org.gk.engine.client.event.attrib.AddAttribute;
import org.gk.engine.client.event.attrib.CellAttribute;
import org.gk.engine.client.event.attrib.ClearAttribute;
import org.gk.engine.client.event.attrib.CollapseAttribute;
import org.gk.engine.client.event.attrib.DataAttribute;
import org.gk.engine.client.event.attrib.DelAttribute;
import org.gk.engine.client.event.attrib.EditableAttribute;
import org.gk.engine.client.event.attrib.EnableAttribute;
import org.gk.engine.client.event.attrib.ExpandAttribute;
import org.gk.engine.client.event.attrib.FilterAttribute;
import org.gk.engine.client.event.attrib.FireAttribute;
import org.gk.engine.client.event.attrib.FocusAttribute;
import org.gk.engine.client.event.attrib.HeadingAttribute;
import org.gk.engine.client.event.attrib.HeightAttribute;
import org.gk.engine.client.event.attrib.IconAttribute;
import org.gk.engine.client.event.attrib.IdAttribute;
import org.gk.engine.client.event.attrib.LabelAttribute;
import org.gk.engine.client.event.attrib.LayoutAttribute;
import org.gk.engine.client.event.attrib.ListAttribute;
import org.gk.engine.client.event.attrib.MarkInvalidAttribute;
import org.gk.engine.client.event.attrib.MaskAttribute;
import org.gk.engine.client.event.attrib.MaxLengthAttribute;
import org.gk.engine.client.event.attrib.NameAttribute;
import org.gk.engine.client.event.attrib.ObjAttribute;
import org.gk.engine.client.event.attrib.PageBarAttribute;
import org.gk.engine.client.event.attrib.ReadOnlyAttribute;
import org.gk.engine.client.event.attrib.ResetAttribute;
import org.gk.engine.client.event.attrib.RowAttribute;
import org.gk.engine.client.event.attrib.SelectAttribute;
import org.gk.engine.client.event.attrib.TitleAttribute;
import org.gk.engine.client.event.attrib.ValidateAttribute;
import org.gk.engine.client.event.attrib.ValueAttribute;
import org.gk.engine.client.event.attrib.VisibleAttribute;
import org.gk.engine.client.event.attrib.WidthAttribute;
import org.gk.ui.client.com.form.gkMap;

import com.extjs.gxt.ui.client.event.Events;

/**
 * 事件工廠
 * 
 * @author i23250
 * @since 2010/9/30
 */
public class EventFactory {

	/**
	 * 建立處理器群組
	 * 
	 * @return Map
	 */
	static Map createHandlerGroup() {
		Map handlerGroup = new gkMap();
		handlerGroup.put(IEventConstants.HANDLER_BEAN, new BeanHandler());
		handlerGroup.put(IEventConstants.HANDLER_COM, new ComHandler());
		handlerGroup.put(IEventConstants.HANDLER_FILE, new FileHandler());
		handlerGroup.put(IEventConstants.HANDLER_HTTP, new HttpHandler());
		handlerGroup.put(IEventConstants.HANDLER_JS, new JSHandler());
		handlerGroup.put(IEventConstants.HANDLER_PUB, new PubHandler());
		handlerGroup.put(IEventConstants.HANDLER_SHOW, new ShowHandler());
		handlerGroup.put(IEventConstants.HANDLER_SUB, new SubHandler());
		return handlerGroup;
	}

	/**
	 * 建立屬性群組
	 * 
	 * @return Map
	 */
	static Map createAttributeGroup() {
		Map attribGroup = new gkMap();
		attribGroup.put(IEventConstants.ATTRIB_ADD, new AddAttribute());
		attribGroup.put(IEventConstants.ATTRIB_CELL, new CellAttribute());
		attribGroup.put(IEventConstants.ATTRIB_CLEAR, new ClearAttribute());
		attribGroup.put(IEventConstants.ATTRIB_COLLAPSE,
				new CollapseAttribute());
		attribGroup.put(IEventConstants.ATTRIB_DATA, new DataAttribute());
		attribGroup.put(IEventConstants.ATTRIB_DEL, new DelAttribute());
		attribGroup.put(IEventConstants.ATTRIB_EDITABLE,
				new EditableAttribute());
		attribGroup.put(IEventConstants.ATTRIB_ENABLE, new EnableAttribute());
		attribGroup.put(IEventConstants.ATTRIB_EXPAND, new ExpandAttribute());
		attribGroup.put(IEventConstants.ATTRIB_FILTER, new FilterAttribute());
		attribGroup.put(IEventConstants.ATTRIB_FIRE, new FireAttribute());
		attribGroup.put(IEventConstants.ATTRIB_FOCUS, new FocusAttribute());
		attribGroup.put(IEventConstants.ATTRIB_HEADING, new HeadingAttribute());
		attribGroup.put(IEventConstants.ATTRIB_HEIGHT, new HeightAttribute());
		attribGroup.put(IEventConstants.ATTRIB_ICON, new IconAttribute());
		attribGroup.put(IEventConstants.ATTRIB_ID, new IdAttribute());
		attribGroup.put(IEventConstants.ATTRIB_LABEL, new LabelAttribute());
		attribGroup.put(IEventConstants.ATTRIB_LAYOUT, new LayoutAttribute());
		attribGroup.put(IEventConstants.ATTRIB_LIST, new ListAttribute());
		attribGroup.put(IEventConstants.ATTRIB_MARKINVALID,
				new MarkInvalidAttribute());
		attribGroup.put(IEventConstants.ATTRIB_MASK, new MaskAttribute());
		attribGroup.put(IEventConstants.ATTRIB_MAXLENGTH,
				new MaxLengthAttribute());
		attribGroup.put(IEventConstants.ATTRIB_NAME, new NameAttribute());
		attribGroup.put(IEventConstants.ATTRIB_OBJ, new ObjAttribute());
		attribGroup.put(IEventConstants.ATTRIB_PAGEBAR, new PageBarAttribute());
		attribGroup.put(IEventConstants.ATTRIB_READONLY,
				new ReadOnlyAttribute());
		attribGroup.put(IEventConstants.ATTRIB_RESET, new ResetAttribute());
		attribGroup.put(IEventConstants.ATTRIB_ROW, new RowAttribute());
		attribGroup.put(IEventConstants.ATTRIB_SELECT, new SelectAttribute());
		attribGroup.put(IEventConstants.ATTRIB_VALIDATE,
				new ValidateAttribute());
		attribGroup.put(IEventConstants.ATTRIB_TITLE, new TitleAttribute());
		attribGroup.put(IEventConstants.ATTRIB_VALUE, new ValueAttribute());
		attribGroup.put(IEventConstants.ATTRIB_VISIBLE, new VisibleAttribute());
		attribGroup.put(IEventConstants.ATTRIB_WIDTH, new WidthAttribute());
		return attribGroup;
	}

	/**
	 * 建立事件種類群組
	 * 
	 * @return Map
	 */
	static Map createEventTypeGroup() {
		Map eventTypeGroup = new gkMap();
		eventTypeGroup.put(IEventConstants.EVENT_ONBLUR, Events.Blur);
		eventTypeGroup.put(IEventConstants.EVENT_ONCHANGE, Events.Change);
		eventTypeGroup.put(IEventConstants.EVENT_ONCLICK, Events.OnClick);
		eventTypeGroup.put(IEventConstants.EVENT_ONFOCUS, Events.Focus);
		eventTypeGroup.put(IEventConstants.EVENT_ONSELECT, Events.Select);
		eventTypeGroup.put(IEventConstants.EVENT_ONTRIGGERCLICK,
				Events.TriggerClick);
		eventTypeGroup.put(IEventConstants.EVENT_ONTWINCLICK,
				Events.TwinTriggerClick);
		return eventTypeGroup;
	}
}

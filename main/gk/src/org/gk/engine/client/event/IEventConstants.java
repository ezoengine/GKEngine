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

/**
 * 事件常數
 * 
 * @author i23250
 * @since 2010/9/30
 */
public interface IEventConstants {

	public final static String HANDLER_BEAN = "bean";
	public final static String HANDLER_COM = "com";
	public final static String HANDLER_FILE = "file";
	public final static String HANDLER_HTTP = "http";
	public final static String HANDLER_JS = "js";
	public final static String HANDLER_PUB = "pub";
	public final static String HANDLER_SHOW = "show";
	public final static String HANDLER_SUB = "sub";

	/**
	 * GUL事件觸發名稱與GXT事件種類對應
	 */
	public final static String EVENT_ONBLUR = "onblur";
	public final static String EVENT_ONCHANGE = "onchange";
	public final static String EVENT_ONCLICK = "onclick";
	public final static String EVENT_ONFOCUS = "onfocus";
	public final static String EVENT_ONSELECT = "onselect";
	public final static String EVENT_ONTRIGGERCLICK = "ontriggerclick";
	public final static String EVENT_ONTWINCLICK = "ontwinclick";

	public final static String ATTRIB_ADD = "add";
	public final static String ATTRIB_CELL = "cell";
	public final static String ATTRIB_CHECKED = "checked";
	public final static String ATTRIB_CLEAR = "clear";
	public final static String ATTRIB_COLLAPSE = "collapse";
	public final static String ATTRIB_DATA = "data";
	public final static String ATTRIB_DEL = "del";
	public final static String ATTRIB_EDITABLE = "editable";
	public final static String ATTRIB_ENABLE = "enable";
	public final static String ATTRIB_EXPAND = "expand";
	public final static String ATTRIB_FILTER = "filter";
	public final static String ATTRIB_FIRE = "fire";
	public final static String ATTRIB_FOCUS = "focus";
	public final static String ATTRIB_HEADING = "heading";
	public final static String ATTRIB_HEIGHT = "height";
	public final static String ATTRIB_ICON = "icon";
	public final static String ATTRIB_ID = "id";
	public final static String ATTRIB_LABEL = "label";
	public final static String ATTRIB_LAYOUT = "layout";
	public final static String ATTRIB_LIST = "list";
	public final static String ATTRIB_MARKINVALID = "markinvalid";
	public final static String ATTRIB_MASK = "mask";
	public final static String ATTRIB_MAXLENGTH = "maxlength";
	public final static String ATTRIB_NAME = "name";
	public final static String ATTRIB_OBJ = "obj";
	public final static String ATTRIB_PAGEBAR = "pagebar";
	public final static String ATTRIB_READONLY = "readonly";
	public final static String ATTRIB_RESET = "reset";
	public final static String ATTRIB_ROW = "row";
	public final static String ATTRIB_SELECT = "select";
	public final static String ATTRIB_TITLE = "title";
	public final static String ATTRIB_VALIDATE = "validate";
	public final static String ATTRIB_VALUE = "value";
	public final static String ATTRIB_VISIBLE = "visible";
	public final static String ATTRIB_WIDTH = "width";

	public final static String SPLIT_COLON = ":";
	public final static String SPLIT_COMMA = ",";
	public final static String SPLIT_DOT = "\\.";

	public final static String TYPE_DATA = "#";
}

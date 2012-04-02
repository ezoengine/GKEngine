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
package org.gk.engine.client.exception;

/**
 * 當發現GUL元件ID重覆時丟出此例外
 * 
 * @author i21890
 * @since 2010/9/18
 */
public class DuplicationIdException extends GKEngineException {

	private static final long serialVersionUID = 1L;

	public DuplicationIdException(String msg) {
		super(msg);
	}
}

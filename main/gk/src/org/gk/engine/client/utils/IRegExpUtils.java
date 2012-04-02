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
package org.gk.engine.client.utils;

/**
 * 常用正規表示式工具
 * 
 * @author i23250
 * @since 2010/10/13
 */
public interface IRegExpUtils {

	/**
	 * 整數的正規表示式
	 */
	public static final String INTEGER = "^-?\\d+$";

	/**
	 * 浮點數的正規表示式
	 */
	public static final String FLOAT = "-?(\\d*\\.)?\\d+";

	/**
	 * 小数的正规表示式
	 */
	public final static String DECIMAL = "^-?\\d+\\.\\d+$";

	/**
	 * 正整数的正规表示式(包含001这种形式)
	 */
	public final static String POSITIVE_INTEGER = "^\\d*[1-9]\\d*$";

	/**
	 * 正浮點數的正規表示式
	 */
	public final static String POSITIVE_FLOAT = "(\\d*\\.)?\\d+";

}

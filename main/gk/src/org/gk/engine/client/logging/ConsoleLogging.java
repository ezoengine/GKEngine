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
package org.gk.engine.client.logging;

/**
 * Console log
 * 
 * @author i23250
 * @since 2011/11/1
 */
public class ConsoleLogging implements ILogging {

	@Override
	public void printLog(Throwable thrown) {
		if (isSupported()) {
			console(getStackTraceAsString(thrown));
		}
	}

	@Override
	public void pringLog(String msg) {
		if (isSupported()) {
			console(msg);
		}
	}

	/**
	 * 將StackTrace轉成格式化字串
	 * 
	 * @param thrown
	 * @return String
	 */
	private static String getStackTraceAsString(Throwable thrown) {
		StringBuffer sb = new StringBuffer(thrown.getClass().getName());
		sb.append(": ").append(thrown.getMessage());

		StackTraceElement[] element = thrown.getStackTrace();
		if (element != null) {
			for (StackTraceElement stack : element) {
				sb.append("\n\tat ").append(stack);
			}
		}
		return sb.toString();
	}

	private native boolean isSupported() /*-{
		return (($wnd.console != null) && ($wnd.console.firebug == null)
				&& ($wnd.console.log != null) && (typeof ($wnd.console.log) == 'function'));
	}-*/;

	private native void console(String message) /*-{
		$wnd.console.log(message);
	}-*/;
}

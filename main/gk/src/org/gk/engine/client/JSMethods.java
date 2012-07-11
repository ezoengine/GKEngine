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
package org.gk.engine.client;

/**
 * Engine提供的JavaScript API
 * 
 * @author i23250
 * @since 2012/5/30
 */
public class JSMethods {

	/**
	 * 註冊JavaScript方法，提供隨時給JavaScript調用
	 */
	native static void hookJSMethods()/*-{
		$wnd.gk = new Object();
		$wnd.gk.set = function(id, value) {
			// 判斷元素是否為 array 原本的value instanceof $wnd.Array 貌似會判斷不出來
			var isArray = Object.prototype.toString.apply(value) === '[object Array]';
			var isObject = typeof (value) == 'object';
			if (isArray || isObject) {
				@org.gk.engine.client.build.js.XJavaScript::setAttributeValue(Ljava/lang/String;ZLcom/google/gwt/core/client/JavaScriptObject;)(id, isArray, value)
			} else {
				try {
					@org.gk.engine.client.build.js.XJavaScript::setAttributeValue(Ljava/lang/String;Ljava/lang/Object;)(id, value);
				} catch (x) {
					// 其他非Object型別的value轉成字串來處理，如number、boolean
					@org.gk.engine.client.build.js.XJavaScript::setAttributeValue(Ljava/lang/String;Ljava/lang/Object;)(id, '' + value);
				}
			}
		}
		$wnd.gk.setAll = function(data) {
			for ( var key in data) {
				$wnd.gk.set(key, data[key]);
			}
		}
		$wnd.gk.get = function(id) {
			var value = @org.gk.engine.client.build.js.XJavaScript::getJSONValue(Ljava/lang/String;)(id);
			if (value == null) {
				return null;
			} else {
				var c = value.charAt(0);
				return c == '[' || c == '{' ? eval('(' + value + ')') : value;
			}
		}
		$wnd.gk.createUniqueId = function(gul) {
			return @org.gk.engine.client.build.js.XJavaScript::createUniqueId(Ljava/lang/String;)(gul);
		}
		$wnd.gk.jsonp = function(url, param) {
			var script = $doc.createElement('script');
			script.type = 'text/javascript';
			if (arguments.length == 2 && param.length > 0) {
				url += "?";
				for (i = 0; i < param.length; i++) {
					url += i == param.length - 1 ? param[i] : param[i] + "&";
				}
			}
			script.src = url;
			$doc.getElementsByTagName('head')[0].appendChild(script);
		}
		$wnd.gk.fire = function(id, eventType) {
			return @org.gk.engine.client.build.js.XJavaScript::fire(Ljava/lang/String;Ljava/lang/String;)(id, eventType);
		}
		$wnd.gk.library = function(gulSyntax) {
			@org.gk.engine.client.utils.ComLibrary::setLibrary(Ljava/lang/String;)(gulSyntax);
		}
		$wnd.gk.getParameter = function(parm) {
			var searchParm = parm + "=";
			var url = $wnd.location.href;
			var index = url.indexOf("?");
			if (index > 0) {
				var queryString = url.substring(index + 1, url.length);
				var parms = queryString.split("&");
				for ( var i = 0; i < parms.length; i++) {
					if (parms[i].indexOf(searchParm) == 0) {
						return parms[i].substring(searchParm.length,
								parms[i].length);
					}
				}
			}
			return null;
		}
		$wnd.gk.event = function(gulAtt, callback) {
			@org.gk.engine.client.Engine::invokeEvent(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(gulAtt, callback);
		}
		$wnd.gk.param = $wnd.gk.getParameter;
		$wnd.gk.getUrlVar = $wnd.gk.getParameter;
		$wnd.gk.row = function(obj) {
			return obj['_gk_idx'].split(',')[0];
		}
		$wnd.gk.col = function(obj) {
			return obj['_gk_idx'].split(',')[1];
		}
		$wnd.gk.column = $wnd.gk.col;
		$wnd.gk.beforeParser = function(gul) {
			return gul;
		}
		$wnd.gk.i18n = function(gul) {
			var split = gul.split(/(?=#D\{)|\'|\"/);
			for (i = 0; i < split.length; i++) {
				// 去掉空白
				var mapper = split[i].replace(/^\s*|\s*$/g, '');
				// 符合「#D{...}」才做替換
				if (mapper.match(/^(#D\{).+\}$/) != null) {
					var code = mapper.substring(mapper.indexOf('{') + 1, mapper
							.indexOf('}'));
					gul = gul.replace(mapper, $wnd.$.i18n.prop(code));
				}
			}
			return gul;
		}
		$wnd.gk.listener = function(userId, midTime) {
			@org.gk.engine.client.Engine::registryWindowFocusBlur(Ljava/lang/String;Ljava/lang/String;)(userId, '' + midTime);
			@org.gk.engine.client.Engine::listener(Ljava/lang/String;Ljava/lang/String;)(userId, '' + midTime);
			return 'done.';
		}
		$wnd.gk.cookie = function(name, value) {
			var numargs = arguments.length;
			if (numargs == 1) {
				return @org.gk.engine.client.Engine::getCookie(Ljava/lang/String;)(name);
			} else if (numargs == 2) {
				@org.gk.engine.client.Engine::setCookie(Ljava/lang/String;Ljava/lang/String;)(name, value);
			}
		}
		$wnd.gk.libraryAttributes = function(name) {
			return @org.gk.engine.client.utils.ComLibrary::getLibraryAttributes(Ljava/lang/String;)(name);
		}
	}-*/;
}

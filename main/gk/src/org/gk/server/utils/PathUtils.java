package org.gk.server.utils;

import java.io.File;

import jfreecode.spring.context.WebSpringContext;

import org.apache.log4j.Logger;

public class PathUtils {
	private static Logger LOG = Logger.getLogger(PathUtils.class);

	public static String concat(String p1, String p2) {
		if (p1.endsWith("/") && p2.startsWith("/")) {
			return p1 + p2.substring(1);
		} else if (!p1.endsWith("/") && !p2.startsWith("/")) {
			return p1 + "/" + p2;
		} else {
			return p1 + p2;
		}
	}

	/**
	 * filePath: 1.webapps/ifrs/... 2.webapps/ROOT/...
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getUrlPath(String filePath) {
		String fp = "filePath:" + filePath;
		int trimStringPos = WebSpringContext.webDirPath().length();
		if (WebSpringContext.isRoot()) {
			trimStringPos += WebSpringContext.rootPath().length();
		}
		filePath = filePath.replace('\\', '/');
		if (filePath.startsWith(WebSpringContext.webDirPath())) {
			filePath = filePath.substring(trimStringPos);
		}
		filePath = filePath.replaceAll("/WEB-INF", "/_WEB-INF");
		LOG.info(fp + ",urlPath:" + filePath);
		return filePath;
	}

	public static String getFilePath(String urlPath) {
		String up = "UrlPath:" + urlPath;
		urlPath = urlPath.replaceAll("/_WEB-INF", "/WEB-INF");
		urlPath = WebSpringContext.isRoot() ? WebSpringContext.rootPath()
				+ urlPath : urlPath;
		if (!urlPath.startsWith(WebSpringContext.webDirPath())) {
			urlPath = PathUtils.concat(WebSpringContext.webDirPath(), urlPath);
		}
		LOG.info(up + ",FilePath:" + urlPath);
		return urlPath;
	}

	public static void main(String[] args) {
		String path = PathUtils.getFilePath(PathUtils.getUrlPath("./2.gul"));
		File file = new File(path);
		System.out.println(PathUtils.getUrlPath(file.getParent()));
	}
}

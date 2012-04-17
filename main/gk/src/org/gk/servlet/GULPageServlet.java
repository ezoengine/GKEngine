package org.gk.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jfreecode.spring.context.WebSpringContext;

import org.apache.log4j.Logger;
import org.gk.server.utils.PathUtils;
import org.jfreecode.utils.FileUtils;

/**
 * <title>讓瀏覽器產生GUL語法的畫面</title>
 * 
 * <pre>
 *  使用情境步驟:
 *   (1) 輸入 http://xx.xx.xx/de/gul/hello.gul
 *   (2) 後端收到後，吐引擎和檔案路徑到前端
 *   (3) 前端JQuery透過ajax取得檔案透過render方法產生畫面
 * </pre>
 * 
 * @author I21890
 */
public class GULPageServlet extends HttpServlet {
	private static Logger LOG = Logger.getLogger(GULPageServlet.class);

	private static final long serialVersionUID = 1L;
	private static String gkEngineDir = "org.gk.GKEngine";

	public static void setGKEngineDir(String dir) {
		gkEngineDir = dir;
	}

	public static String getGKEngineDir() {
		return gkEngineDir;
	}

	/**
	 * 程式進入點，接收Client傳來的request開始進行處理
	 */
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) {
		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("Expires", "-1");
		try {
			req.setCharacterEncoding("UTF-8");
			res.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String gkPath = req.getContextPath() + req.getServletPath();
		LOG.info("gkPath:" + gkPath);
		// 瀏覽器自己發出的request
		if (gkPath.startsWith("/favicon.ico")) {
			return;
		}
		String render = req.getParameter("render");
		if (render == null || render.toLowerCase().equals("false")) {
			LOG.debug("download gk engine.");
			renderPage(req, res, gkPath);
		} else {
			try {
				String filePath = PathUtils.getFilePath(gkPath);
				LOG.info("readFile:" + filePath);
				String gul = FileUtils.get().readStringFromFile("." + filePath);
				if (render.toLowerCase().equals("xml")) {
					StringBuffer sb = new StringBuffer();
					sb.append("var gk = $wnd.gk;\r\n");
					gul = gul.replaceAll("\"", "\\\\\"");
					gul = gul.replace('\n', ' ');
					sb.append("gk.gul(\"" + gul + "\")");
					res.setContentType(" text/javascript");
					res.getWriter().write(sb.toString());
				} else {
					res.setContentType(" text/plain;");
					res.getWriter().write(gul);
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					res.getWriter().write(
							"<js init='js:this'>alert('read File exception: "
									+ gkPath + ",[" + e.getMessage()
									+ "]');</js>");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 輸出GK Engine到前端
	 * 
	 * @param req
	 * @param res
	 * @param gulFilePath
	 */
	protected void renderPage(HttpServletRequest req, HttpServletResponse res,
			String gulFilePath) {
		PrintWriter pw = null;
		String msg = "GULPage context path >" + req.getContextPath();
		LOG.warn(msg);
		try {
			res.setContentType("text/html");
			pw = res.getWriter();
			pw.println("<!doctype html>");
			pw.println("<html><head><link rel='shortcut icon' href='/favicon.ico' >");
			pw.println("<title>EZo UIBuilder</title>");
			pw.println("<meta http-equiv='content-type' content='text/html; charset=UTF-8'>");
			pw.println("<meta name='gwt:property' content='locale="
					+ req.getLocale() + "'>");
			loadJQuery(pw, req.getContextPath());
			loadGKEngine(pw, req.getContextPath());
			pw.println("</head><body>");
			pw.println("<script type='text/javascript'>");
			// when GKEngine is ready , use JQuery to load GUL File.
			if (gulFilePath.toLowerCase().endsWith(".gul")) {
				pw.println("function renderPage(){");
				pw.println("var url = encodeURI('" + gulFilePath + "');");
				pw.println("$.ajax({type: 'GET',url: url + '?render=true',dataType: 'text',");
				pw.println("success: function(gul) {");
				pw.println("gk.ctx='" + req.getContextPath() + "';");
				pw.println("gk.render(gul);");
				pw.println("gk.path='" + gulFilePath + "';");
				pw.println("}});");
				pw.println("}");
			} else {
				pw.println("function renderPage(){");
				pw.println("gk.ctx='" + req.getContextPath() + "';");
				pw.println("gk.render('<page/>')");
				pw.println("gk.path='" + gulFilePath + "';");
				pw.println("}");
			}
			pw.println("</script>");
			pw.println("</body></html>");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * 載入JQuery
	 * 
	 * @param pw
	 * @param contextPath
	 */
	protected void loadJQuery(PrintWriter pw, String contextPath) {
		pw.println("<script type='text/javascript' src='" + contextPath
				+ "/html/gwt/res/jquery/jquery.min.js'></script>");
	}

	/**
	 * 載入GKEngine
	 * 
	 * @param pw
	 * @param contextPath
	 */
	protected void loadGKEngine(PrintWriter pw, String contextPath) {
		String loadingLibrary = WebSpringContext.comLibPath() == null ? ""
				: " id='_gk_' lib='" + contextPath
						+ WebSpringContext.comLibPath() + "'";
		pw.println("<script" + loadingLibrary
				+ " type='text/javascript' language='javascript' src='"
				+ contextPath + "/html/gwt/ap/" + gkEngineDir
				+ "/org.gk.GKEngine.nocache.js'></script>");
	}

}

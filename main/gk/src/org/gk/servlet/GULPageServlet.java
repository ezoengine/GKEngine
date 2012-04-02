package org.gk.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

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
			req.setCharacterEncoding("UTF-8"); // 內定使用UTF-8編碼方式
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
			System.err.println("download gk engine.");
			renderPage(req, res, gkPath);
		} else {
			try {
				String filePath = PathUtils.getFilePath(gkPath);
				LOG.info("readFile:" + filePath);
				String gul = FileUtils.get().readStringFromFile(filePath);
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
			loadI18N(pw, req.getContextPath(), req.getLocale());
			attatchChromeFrameInstall(req.getContextPath(), pw);
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
	 * 載入I18N
	 * 
	 * @param pw
	 * @param contextPath
	 * @param locale
	 */
	protected void loadI18N(PrintWriter pw, String contextPath, Locale locale) {
		pw.println("<script type='text/javascript' language='javascript' src='"
				+ contextPath
				+ "/html/gwt/res/jquery/jquery.i18n.properties.js'></script>");
		pw.println("<script type='text/javascript'>");
		pw.println("$.i18n.properties({");
		pw.println("name:'gk_messages',");
		pw.println("path:'" + contextPath + "/gk/config/properties/',");
		pw.println("mode:'both',");
		pw.println("language:'" + locale + "'");
		pw.println("});");
		pw.println("</script>");
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

	/**
	 * 載入GKEditor
	 * 
	 * @param pw
	 * @param contextPath
	 */
	protected void loadGKEditor(PrintWriter pw, String contextPath) {
		pw.println("<script type='text/javascript' language='javascript' src='"
				+ contextPath
				+ "/html/gwt/ap/org.gk.editor.GKEditor.GKEditor/org.gk.editor.GKEditor.GKEditor.nocache.js'></script>");
	}

	/**
	 * <pre>
	 * 如果有登入的話，就執行gk.listener動作
	 * 這事原本是loginPanel 負責，現在則改為GKEngine預設動作
	 * </pre>
	 * 
	 * @param req
	 * @return String
	 */
	protected String addUserLisener(HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("userId");
		return userId == null ? "" : "gk.listener('" + userId + "',1000);";
	}

	/**
	 * 附加ChromeFrame安裝資訊(瀏覽器為IE時)
	 * 
	 * @param ctxPath
	 * @param pw
	 */
	protected void attatchChromeFrameInstall(String ctxPath, PrintWriter pw) {
		StringBuffer cf = new StringBuffer("<!--[if IE]>\r\n");
		cf.append("<meta http-equiv='X-UA-Compatible' content='chrome=1'>\r\n");
		cf.append("<script type='text/javascript' src='")
				.append(ctxPath)
				.append("/html/gwt/res/chromeframe/CFInstall.min.js'></script>\r\n");
		cf.append("<script type='text/javascript' src='")
				.append(ctxPath)
				.append("/html/gwt/res/chromeframe/jquery.chrome_frame.js'></script>\r\n");
		cf.append("<style>\r\n");
		cf.append("#chrome_msg { display:none; position: fixed; top: 0; left: 0; background: #ece475; border: 2px solid #666; border-top: none; font: bold 11px Verdana, Geneva, Arial, Helvetica, sans-serif; line-height: 100%; width: 100%; text-align: center; padding: 5px 0; margin: 0 auto; }\r\n");
		cf.append("#chrome_msg a, #chrome_msg a:link { color: #a70101; text-decoration: none; }\r\n");
		cf.append("#chrome_msg a:hover { color: #a70101; text-decoration: underline; }\r\n");
		cf.append("#chrome_msg a#msg_hide { float: right; margin-right: 15px; cursor: pointer; }\r\n");
		cf.append("/* IE6 positioning fix */\r\n");
		cf.append("* html #chrome_msg { left: auto; margin: 0 auto; border-top: 2px solid #666;  }\r\n");
		cf.append("</style>\r\n" + "<![endif]-->");
		pw.println("" + cf);
	}
}

<%@ page contentType="text/html;CHARSET=utf-8" %><%@page import="java.io.*"%><%@page import="java.net.URL"%><%
	response.setCharacterEncoding("utf-8");
	String id=request.getParameter("id");
	String url = "http://mis.tse.com.tw/data/"+id+".csv";
	InputStream is = null;
	byte[] buf = new byte[1024];
	int bytes;
	try {
		is = new URL(url).openConnection().getInputStream();
		bytes = is.read(buf);
	} finally {
		is.close();
	}
	String st = new String(buf,0,bytes,"big5");
	out.print("["+st+"]");%>
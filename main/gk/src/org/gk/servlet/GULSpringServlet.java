package org.gk.servlet;

import javax.servlet.ServletException;

import jfreecode.gwt.event.server.EventCenterServlet;
import jfreecode.gwt.event.server.bus.EventStore;
import jfreecode.gwt.event.server.invoke.InvokeIfc;
import jfreecode.gwt.event.server.invoke.InvokeSpringBean;
import jfreecode.gwt.event.server.invoke.exception.InvalidEventIdException;
import jfreecode.gwt.event.server.invoke.exception.SpringBeanNotFoundException;
import jfreecode.spring.context.WebSpringContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GULSpringServlet extends EventCenterServlet {
	private static final long serialVersionUID = 1L;
	private WebApplicationContext springContext;
	private final InvokeIfc invoke = new InvokeSpringBean();

	@Override
	public void init() throws ServletException {
		super.init();
		springContext = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		WebSpringContext.setSpringContext(springContext);
	}

	/**
	 * <pre>
	 * Client發佈事件過來，Server調用SpringBean進行處理 處理有四種例外狀況 
	 * 1.找不到SpringBean元件SpringBeanNotFoundException 
	 * 2.找不到可執行的方法 InvokeSpringBeanException
	 * 3.AP程式處理異常 APException
	 * 繼承InvokeSpringBean改寫可直接調用某檔案的腳本
	 * @throws NotFoundSpringBean
	 * </pre>
	 */
	@Override
	public void doPut(EventStore es) {
		String eventId = es.getEventId();
		//
		if (eventId.indexOf('.') == -1) {
			throw new InvalidEventIdException("{" + eventId
					+ "} format error! EventId format: "
					+ "${BeanName}.${BeanMethod}");
		}
		// EventID包含了 $SpringName.$methodName
		String[] beanProperty = eventId.split("\\.");
		String beanName = beanProperty[0];
		String beanMethod = beanProperty[1];
		checkSpringBeanExist(beanName, beanMethod);
		Object springBean = springContext.getBean(beanName);
		invoke.execute(springBean, beanMethod);
	}

	protected void checkSpringBeanExist(String beanName, String beanMethod) {
		if (!springContext.containsBean(beanName)) {
			throw new SpringBeanNotFoundException("beanName:" + beanName
					+ ",beanMethod:" + beanMethod);
		}
	}
}
//package com.hfi.insurance.common.filter;
//
//import com.hfi.insurance.aspect.HfiLogger;
//import com.hfi.insurance.common.util.SessionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.Charset;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * 过滤器把请求流保存起来 ServletRequest的getReader()和getInputStream()两个方法都注明方法只能被调用一次，
// * 由于RequestBody是流的形式读取，那么流读了一次就没有了，所以只能被调用一次。既然是因为流只能读一次的原因
// * ，那么只要将流的内容保存下来，就可以实现反复读取了。byte数组允许被多次读取，而不会丢失内容。下面使用byte数组将流的内容保存下来。
// * @Order(-1), spring boot 会按照order值的大小，从小到大的顺序来依次过滤。
// * 这个过滤器要在 @LogMdcFilter 之前执行, 防止body数据被读取后失效
// */
//@Component
//@Order(-1)
//@WebFilter(urlPatterns = "/*", filterName = "sessionFilter")
//public class SessionFilter implements Filter {
//	private static final Logger log = HfiLogger.create(SessionFilter.class);
//
//	private static final Set<String> noFilterPath = new HashSet<String>(){{
//		add("/pic/*");
//	}};
//
//	@Override
//	public void init(FilterConfig filterConfig) throws ServletException {
//		log.info("初始化SessionFilter");
//	}
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//		if (! noFilterPath.isEmpty()) {
//			//在set中的是不需要进行转换的
//			for (String path : noFilterPath) {
//				if (StringUtils.isBlank(path))
//					continue;
//				if (httpServletRequest.getRequestURI().contains(path)) {
//					chain.doFilter(request, response);
//					return;
//				}
//			}
//		}
//		// 防止流读取一次后就没有了, 所以需要将流用这个类包装起来, 继续写出去
//		ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
//		chain.doFilter(requestWrapper, response);
//	}
//
//	@Override
//	public void destroy() {
//
//	}
//
//	/**
//	 * 保存流 1:先将RequestBody保存为一个byte数组，
//	 * 然后通过Servlet自带的HttpServletRequestWrapper类覆盖getReader()和getInputStream()方法，
//	 * 使流从保存的byte数组读取。然后再Filter中将ServletRequest替换为ServletRequestWrapper。
//	 *
//	 * 2:BodyReaderHttpServletRequestWrapper类包装ServletRequest，将流保存为byte[]，
//	 * 然后将getReader()和getInputStream()方法的流的读取指向byte[]。
//	 *
//	 */
//	public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
//
//		private final byte[] body;
//
//		public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
//			super(request);
//			String sessionStream = SessionUtils.getBodyString(request);
//			body = sessionStream.getBytes(Charset.forName("UTF-8"));
//		}
//
//		@Override
//		public BufferedReader getReader() throws IOException {
//			return new BufferedReader(new InputStreamReader(getInputStream()));
//		}
//
//		@Override
//		public ServletInputStream getInputStream() throws IOException {
//
//			final ByteArrayInputStream bais = new ByteArrayInputStream(body);
//
//			return new ServletInputStream() {
//
//				@Override
//				public int read() throws IOException {
//					return bais.read();
//				}
//
//				@Override
//				public boolean isFinished() {
//					// TODO Auto-generated method stub
//					return false;
//				}
//
//				@Override
//				public boolean isReady() {
//					// TODO Auto-generated method stub
//					return false;
//				}
//
//				@Override
//				public void setReadListener(ReadListener arg0) {
//					// TODO Auto-generated method stub
//
//				}
//			};
//		}
//	}
//}
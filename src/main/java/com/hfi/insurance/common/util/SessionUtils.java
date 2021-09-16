//package com.hfi.insurance.common.util;
//
//import javax.servlet.ServletInputStream;
//import javax.servlet.ServletRequest;
//import java.io.*;
//import java.nio.charset.Charset;
//
///**
// * 请求帮助类
// * @author Administrator
// *
// */
//public class SessionUtils {
//	/**
//	 * 获取请求Body
//	 *
//	 * @param request
//	 * @return
//	 */
//	public static String getBodyString(final ServletRequest request) {
//		StringBuilder sb = new StringBuilder();
//		InputStream inputStream = null;
//		BufferedReader reader = null;
//		try {
//			inputStream = cloneInputStream(request.getInputStream());
//			reader = new BufferedReader(new InputStreamReader(inputStream,
//					Charset.forName("UTF-8")));
//			String line = "";
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (inputStream != null) {
//				try {
//					inputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (reader != null) {
//				try {
//					reader.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return sb.toString();
//	}
//
//	/**
//	 * Description: 复制输入流</br>
//	 *
//	 * @param inputStream
//	 * @return</br>
//	 */
//	public static InputStream cloneInputStream(ServletInputStream inputStream) {
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int len;
//		try {
//			while ((len = inputStream.read(buffer)) > -1) {
//				byteArrayOutputStream.write(buffer, 0, len);
//			}
//			byteArrayOutputStream.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		InputStream byteArrayInputStream = new ByteArrayInputStream(
//				byteArrayOutputStream.toByteArray());
//		return byteArrayInputStream;
//	}
//}

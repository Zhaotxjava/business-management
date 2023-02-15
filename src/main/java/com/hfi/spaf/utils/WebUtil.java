package com.hfi.spaf.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * WebUtil
 *
 * @author minghan
 */
@Slf4j
@Component
public class WebUtil {

    private WebUtil() {
        super();
    }

    public static WebUtil getInstance() {
        return new WebUtil();
    }

    /**
     * @param src String
     * @return String 字符串
     * @throws
     * @Title escape
     * @Description
     */
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * NumConstant.SIX);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j)) {
                tmp.append(j);
            } else if (j < NumConstant.TWO_HUNDRED_FIFTY_SIX) {
                tmp.append("%");
                if (j < NumConstant.SIXTEEN) {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(j, NumConstant.SIXTEEN));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, NumConstant.SIXTEEN));
            }
        }
        return tmp.toString();
    }

    /**
     * @param src String
     * @return String 字符串
     * @throws
     * @Title unescape
     * @Description
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0;
        int pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf('%', lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + NumConstant.SIX), NumConstant.SIXTEEN);
                    tmp.append(ch);
                    lastPos = pos + NumConstant.SIX;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + NumConstant.THREE), NumConstant.SIXTEEN);
                    tmp.append(ch);
                    lastPos = pos + NumConstant.THREE;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * @param request HttpServletRequest
     * @return boolean 布尔
     * @throws
     * @Title isMobileDevice
     * @Description
     */
    public static boolean isMobileDevice(HttpServletRequest request) {
        if (request == null) {
            return true;
        }
        String requestHeader = request.getHeader("user-agent");
        log.info("===requestHeader===" + requestHeader);
        if (null == requestHeader || "".equalsIgnoreCase(requestHeader)) {
            return true;
        }
        return isMobileDevice(requestHeader);
    }

    /**
     * @param requestHeader String
     * @return boolean 布尔
     * @throws
     * @Title isMobileDevice
     * @Description
     */
    public static boolean isMobileDevice(String requestHeader) {
        /**
         * android : 所有android设备
         * mac os : iphone ipad
         * windows phone:Nokia等windows系统的手机
         */
        String requestHeader2 = requestHeader;
        String[] deviceArray = new String[]{"android", "iphone", "ios", "mac os", "windows phone",};
        if (requestHeader2 == null) {
            return false;
        }
        requestHeader2 = requestHeader2.toLowerCase();
        for (int i = 0; i < deviceArray.length; i++) {
            if (requestHeader2.indexOf(deviceArray[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param
     * @return HttpServletRequest 布尔
     * @throws
     * @Title getThreadRequest
     * @Description
     */
    public HttpServletRequest getThreadRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * @param
     * @return boolean 布尔
     * @throws
     * @Title isMobileDeviceRequest
     * @Description
     */
    public boolean isMobileDeviceRequest() {
        String requestHeader = getThreadRequest().getHeader("user-agent");
        return isMobileDevice(requestHeader);
    }


    /**
     * @param
     * @return HttpSession 布尔
     * @throws
     * @Title getThreadSession
     * @Description
     */
    public HttpSession getThreadSession() {
        return getThreadRequest().getSession(true);
    }

    /**
     * @param
     * @return boolean 布尔
     * @throws
     * @Title isAjaxRequest
     * @Description
     */
    public boolean isAjaxRequest() {
        return isAjaxRequest(getThreadRequest());
    }

    /**
     * @param request HttpServletRequest
     * @return boolean 布尔
     * @throws
     * @Title isAjaxRequest
     * @Description
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith)
                : false;
    }

    /**
     * @param path String
     * @return String 字符串
     * @throws
     * @Title getFullUrlBasedOn
     * @Description
     */
    public String getFullUrlBasedOn(String path) {
        StringBuilder targetUrl = new StringBuilder();
        if (path.startsWith("/")) {
            /** Do not apply context path to relative URLs.*/
            targetUrl.append(getThreadRequest().getContextPath());
        }
        targetUrl.append(path);
        return targetUrl.toString();
    }

    /**
     * @param request HttpServletRequest
     * @return String 字符串
     * @throws
     * @Title getIpAddr2
     * @Description
     */
    public static String getIpAddr2(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * @param request HttpServletRequest
     * @return String 字符串
     * @throws
     * @Title getClientIP
     * @Description
     */
    public static String getClientIP(HttpServletRequest request) {
        String ipAddress = null;
        /** ipAddress = this.getRequest().getRemoteAddr();*/
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress)) {
                /** 根据网卡取本机配置的IP*/
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.info(e.getMessage());
                }
                ipAddress = inet.getHostAddress();
            }
        }

        /** 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割*/
        if (ipAddress != null && ipAddress.indexOf(',') > 0 && ipAddress.length() > NumConstant.FIFTEEN) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
        }
        return ipAddress;
    }

    public static String getServerIP() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
        return "";
    }

    /**
     * @param request HttpServletRequest
     * @return String 字符串
     * @throws
     * @Title getServerPath
     * @Description
     */
    public String getServerPath(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getScheme()).append("://");
        builder.append(request.getServerName()).append(":");
        builder.append(request.getServerPort());
        builder.append(getFullUrlBasedOn("/"));
        return builder.toString();
    }

}

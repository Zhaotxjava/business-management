package com.hfi.insurance.common.filter;


import com.hfi.insurance.common.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
@Order(Integer.MAX_VALUE)
@Slf4j
@WebFilter(urlPatterns = {"/user/*", "/query/*"}, filterName = "logMdcFilter")
public class LogMdcFilter implements Filter {
    private static final String UNIQUE_ID = "logTraceID";
    private static final String REMOTE_IP = "merReqIp";
    private static final Set<String> noFilterPath = new HashSet<String>(){{
        add("/check_health");
        add("/favicon.ico");
    }};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("初始化LogMdcFilter, 利用mdc机制为logback日志增加traceId");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (! noFilterPath.isEmpty()) {
            //在set中的是不需要进行filter的
            for (String path : noFilterPath) {
                if (!StringUtils.isBlank(path) && httpServletRequest.getRequestURI().contains(path)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
        }
        //这个servletRequest是已经被包装过了

//        if (!Objects.isNull(servletRequest.getContentType()) && servletRequest.getContentType().contains("multipart/form-data")){
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
        String logTraceID =  UUID.randomUUID().toString().replace("-", "");


        //MDC, 这次请求的所有日志都会增加logTraceID, 包括但不仅限于日志切面
        String remoteIp = NetUtil.getIpAddress((HttpServletRequest) servletRequest);
        boolean bInsertMDC = insertMDC(logTraceID, remoteIp);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            if(bInsertMDC) {
                MDC.remove(UNIQUE_ID);
                MDC.remove(REMOTE_IP);
            }
        }
    }

    @Override
    public void destroy() {

    }

    private boolean insertMDC(String traceid, String ip) {
        String uniqueId = null;
        if (StringUtils.isNotEmpty(traceid)) {
            uniqueId = traceid;
        } else {
            UUID uuid = UUID.randomUUID();
            uniqueId = uuid.toString().replace("-", "");
        }
        MDC.put(UNIQUE_ID, uniqueId);
        MDC.put(REMOTE_IP, ip);
        return true;
    }

}



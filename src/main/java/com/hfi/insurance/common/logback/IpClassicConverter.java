//package com.hfi.insurance.common.logback;
//
//import ch.qos.logback.classic.pattern.ClassicConverter;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import com.hfi.insurance.aspect.HfiLogger;
//import com.hfi.insurance.common.util.NetUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author rensm
// * @date 2019/6/14
// */
//public class IpClassicConverter extends ClassicConverter {
//    private static final Logger log = HfiLogger.create(IpClassicConverter.class);
//    private static final List<String> ipList = new ArrayList<>();
//    public IpClassicConverter() {
//        log.info("初始化IpClassicConverter, 获取本机ip地址");
//        Set<String> localIpAddr = NetUtil.getLocalIpAddr();
//        for (String s : localIpAddr) {
//            log.info("Local ip: " + s);
//            if ("127.0.0.1".equals(s)){
//                continue;
//            }
//            ipList.add(s);
//        }
//    }
//
//    @Override
//    public String convert(ILoggingEvent event) {
//        if (ipList.isEmpty()){
//            return StringUtils.EMPTY;
//        }else {
//            return ipList.get(0);
//        }
//    }
//}

package com.hfi.insurance.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

/**
 * @author baizhen
 * @date 2020/04/01 11:06
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static CloseableHttpClient httpClient;

    static {
        httpClient =
                HttpClients.custom()
                        .setConnectionManager(ConnectionManagerHolder.cm)
                        .setDefaultRequestConfig(RequestConfigHolder.requestConfig)
                        .setMaxConnPerRoute(100)
                        .setMaxConnTotal(200)
                        .build();
    }

    /**
     * get请求
     *
     * @param url
     * @param headers
     * @param urlParams
     * @return
     */
    public static String doGet(
            String url, Map<String, String> headers, Map<String, String> urlParams) {
        log.info("请求url：{}参数：{}",url, urlParams);
        HttpGet httpGet = new HttpGet(buildUrl(url, urlParams));
        addHeaders(httpGet, headers);
        String data = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            checkResponseStatusCode(response);
            data = getJsonStr(response);
        } catch (IOException e) {
            log.error("an IOException occurred while executing get request", e);
            throw new RuntimeException(e);
        } finally {
            httpGet.releaseConnection();
        }
        return data;
    }

    /**
     * get请求下载文件
     *
     * @param url
     * @return
     */
    public static byte[] doGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        InputStream content = null;
        ByteArrayOutputStream outStream = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            checkResponseStatusCode(response);
            content = response.getEntity().getContent();
            if (content != null) {
                outStream = new ByteArrayOutputStream();
                byte[] streams = new byte[1024];
                int len;
                while ((len = content.read(streams)) != -1) {
                    outStream.write(streams, 0, len);
                }
                outStream.flush();
                return outStream.toByteArray();
            }
        } catch (IOException e) {
            log.error("an IOException occurred while executing get request", e);
            throw new RuntimeException(e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (content != null) {
                    content.close();
                }
            } catch (IOException e) {
                log.error("an IOException occurred while close input stream", e);
                throw new RuntimeException(e);
            }

            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                log.error("an IOException occurred while close output stream", e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * post请求(josn格式请求)
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static String doPost(String url, Map<String, String> headers, String params) {
        log.info("请求参数：{}", params);
        HttpPost httpPost = new HttpPost(url);
        String data = null;
        try {
            StringEntity stringEntity = new StringEntity(params, Consts.UTF_8);
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setEntity(stringEntity);
            addHeaders(httpPost, headers);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            checkResponseStatusCode(response);
            data = getJsonStr(response);
        } catch (IOException e) {
            log.error("an IOException occurred while executing post request", e);
            throw new RuntimeException(e);
        } finally {
            httpPost.releaseConnection();
        }
        return data;
    }

    /**
     * post请求(提交文件)
     *
     * @param url
     * @param bytes
     * @param name
     * @param filename
     * @param headers
     * @param bodys
     * @return
     */
    public static String doPostFile(
            String url,
            byte[] bytes,
            String name,
            String filename,
            Map<String, String> headers,
            Map<String, String> bodys)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder =
                MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .setCharset(CharsetUtils.get("UTF-8"));
        builder.addBinaryBody(name, bytes, ContentType.MULTIPART_FORM_DATA, filename);
        if (MapUtils.isNotEmpty(bodys)) {
            for (Map.Entry<String, String> map : bodys.entrySet()) {
                builder.addTextBody(map.getKey(), map.getValue());
            }
        }
        httpPost.setEntity(builder.build());
        addHeaders(httpPost, headers);
        String data = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            checkResponseStatusCode(response);
            data = getJsonStr(response);
        } catch (IOException e) {
            log.error("an IOException occurred while executing post request", e);
            throw new RuntimeException(e);
        } finally {
            httpPost.releaseConnection();
        }
        return data;
    }

    /**
     * put请求
     *
     * @param url
     * @param headers
     * @param params
     * @param file
     * @return
     */
    public static String doPut(
            String url, Map<String, String> headers, Map<String, String> params, byte[] file) {
        HttpPut httpPut = new HttpPut(buildUrl(url, params));
        String data = null;
        try {
            if (file != null) {
                httpPut.setEntity(new ByteArrayEntity(file));
            }
            addHeaders(httpPut, headers);
            CloseableHttpResponse response = httpClient.execute(httpPut);
            checkResponseStatusCode(response);
            data = getJsonStr(response);
        } catch (IOException e) {
            log.error("an IOException occurred while executing put request", e);
            throw new RuntimeException(e);
        } finally {
            httpPut.releaseConnection();
        }
        return data;
    }

    public static String doGetWithIntegerParam(
            String url, Map<String, String> headers, Map<String, Integer> urlParams) {
        log.info("请求参数：{}", urlParams);
        StringBuilder sbUrl = new StringBuilder();
        if (!StringUtils.isBlank(url)) {
            sbUrl.append(url);
        }
        if (MapUtils.isNotEmpty(urlParams)) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, Integer> query : urlParams.entrySet()) {
                String key = query.getKey();
                int value = query.getValue();
                sbQuery.append(key)
                        .append("=")
                        .append(value)
                        .append("&");
            }
            String params = sbQuery.substring(0, sbQuery.length() - 1);
            if (StringUtils.isNotBlank(url)) {
                sbUrl.append("?").append(params);
            }
        }
        String finalUrl = sbUrl.toString().replace(" ", "");
        HttpGet httpGet = new HttpGet(finalUrl);
        addHeaders(httpGet, headers);
        String data = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            checkResponseStatusCode(response);
            data = getJsonStr(response);
        } catch (IOException e) {
            log.error("an IOException occurred while executing get request", e);
            throw new RuntimeException(e);
        } finally {
            httpGet.releaseConnection();
        }
        return data;
    }

    private static String buildUrl(String url, Map<String, String> querys) {
        StringBuilder sbUrl = new StringBuilder();
        if (!StringUtils.isBlank(url)) {
            sbUrl.append(url);
        }
        if (MapUtils.isNotEmpty(querys)) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                String key = query.getKey();
                String value = query.getValue();
                try {
                    sbQuery.append(key)
                            .append("=")
                            .append(URLEncoder.encode(value, "utf-8"))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    log.error("字符集异常", e);
                    throw new RuntimeException(e.getMessage());
                }
            }
            String params = sbQuery.substring(0, sbQuery.length() - 1);
            if (StringUtils.isNotBlank(url)) {
                sbUrl.append("?").append(params);
            }
        }
        return sbUrl.toString().replace(" ", "");
    }

    public static String getJsonStr(HttpResponse httpResponse) {
        String resp = "";
        HttpEntity entity = httpResponse.getEntity();
        try {
            resp = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
        } catch (IOException e) {
            log.error("an IOException occurred while get content", e);
            throw new RuntimeException(e.getMessage());
        }
        return resp;
    }

    private static void checkResponseStatusCode(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < HttpStatus.SC_OK || statusCode > HttpStatus.SC_MULTI_STATUS) {
            throw new RuntimeException("Http 请求失败,状态码：" + statusCode);
        }
    }

    public static void addHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> head : headers.entrySet()) {
                request.addHeader(head.getKey(), head.getValue());
            }
        }
    }

    private static class RequestConfigHolder {
        public static final RequestConfig requestConfig;

        static {
            requestConfig =
                    RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                            .setExpectContinueEnabled(Boolean.TRUE)
                            .setTargetPreferredAuthSchemes(
                                    Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                            .setSocketTimeout(10000)
                            .setConnectTimeout(20000)
                            .setConnectionRequestTimeout(10000)
                            .build();
        }
    }

    private static class ConnectionManagerHolder {
        public static final PoolingHttpClientConnectionManager cm;

        static {
            try {
                X509TrustManager trustManager =
                        new X509TrustManager() {
                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] xcs, String str) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] xcs, String str) {
                            }
                        };
                SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
                ctx.init(null, new TrustManager[]{trustManager}, null);
                SSLConnectionSocketFactory socketFactory =
                        new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
                Registry<ConnectionSocketFactory> socketFactoryRegistry =
                        RegistryBuilder.<ConnectionSocketFactory>create()
                                .register("http", PlainConnectionSocketFactory.INSTANCE)
                                .register("https", socketFactory)
                                .build();
                cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                cm.setValidateAfterInactivity(2000);
                cm.setMaxTotal(100);
                cm.setDefaultMaxPerRoute(200);
            } catch (KeyManagementException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

package com.cgp.common.utils;

import com.cgp.common.enums.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * HttpClient工具
 *
 * @author Chen
 * @date 2020-05-15
 */
@SuppressWarnings("unused")
@Slf4j
public class HttpClientUtil {

    private HttpClientUtil() {
    }


    private static CloseableHttpClient httpClient;

    /*
     * 信任SSL证书
     * java实现https请求绕过证书检测
     */
    static {
        try {
            SSLContext sslContext = SSLContextBuilder.create().setProtocol(SSLConnectionSocketFactory.SSL).loadTrustMaterial((x, y) -> true).build();
            // 设置请求和传输超时时间
            RequestConfig config = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).setCookieSpec(CookieSpecs.STANDARD).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLContext(sslContext).setSSLHostnameVerifier((x, y) -> true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ignored) {
        }

    }

    /**
     * GET请求
     *
     * @param url     url地址
     * @param params  参数列表
     * @param headers 请求头
     * @return {@link String}
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return method(HttpMethod.GET, url, params, null, headers);
    }

    /**
     * GET请求
     *
     * @param url    url地址
     * @param params 参数列表
     * @return {@link String}
     */
    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, null);
    }

    /**
     * GET请求
     *
     * @param url url地址
     * @return {@link String}
     */
    public static String doGet(String url) {
        return doGet(url, null, null);
    }

    /**
     * 单纯的http请求
     *
     * @param url url
     */
    public static void simpleGet(String url) {
        String result = "";
        CloseableHttpResponse response;
        try {
            // 创建uri对象
            URIBuilder builder = new URIBuilder(url);
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(url);
            // 执行请求
            httpClient.execute(httpGet);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * post请求
     *
     * @param url    url地址
     * @param params 参数
     * @return {@link String}
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers) {
        return method(HttpMethod.POST_FORM, url, params, null, headers);
    }

    /**
     * post请求
     *
     * @param url    url地址
     * @param params 参数
     * @return {@link String}
     */
    public static String doPost(String url, Map<String, String> params, Map<String, String> headers) {
        return method(HttpMethod.POST, url, params, null, headers);
    }

    /**
     * post请求传输json参数
     *
     * @param url  url地址
     * @param json json参数
     * @return {@link String}
     */
    public static String postJson(String url, String json) {
        return method(HttpMethod.POST_JSON, url, null, json, null);
    }

    /**
     * post请求传输xml参数
     *
     * @param url url地址
     * @param xml xml参数
     * @return {@link String}
     */
    public static String postXml(String url, String xml) {
        return method(HttpMethod.POST_XML, url, null, xml, null);
    }

    /**
     * put请求传输json参数
     *
     * @param url  url地址
     * @param json json参数
     * @return {@link String}
     */
    public static String put(String url, String json) {
        return method(HttpMethod.PUT, url, null, json, null);
    }

    /**
     * delete请求传输json参数
     *
     * @param url  url地址
     * @param headers 请求头
     * @return {@link String}
     */
    public static String delete(String url, Map<String, String> headers) {
        return method(HttpMethod.DELETE, url, null, null, headers);
    }

    /**
     * 原生请求方式
     *
     * @param method  方法
     * @param url     url
     * @param params  参数个数
     * @param headers 头
     * @return {@link String}
     */
    private static String method(HttpMethod method, String url, Map<String, String> params, String json, Map<String, String> headers) {
        String result = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri对象
            URIBuilder builder = new URIBuilder(url);
            // 设置参数
            if (params != null && !params.isEmpty() && method != HttpMethod.POST_FORM) {
                for (String key : params.keySet()) {
                    builder.setParameter(key, params.get(key));
                }
            }

            URI uri = builder.build();

            switch (method) {
                case GET:
                    response = getPostAndDelete(headers, new HttpGet(uri));
                    break;
                case POST:
                    response = getPostAndDelete(headers, new HttpPost(uri));
                    break;
                case POST_FORM:
                    response = postForm(params, headers, uri);
                    break;
                case POST_JSON:
                    response = postJsonAndPut(new HttpPost(url), json, headers, ContentType.APPLICATION_JSON.toString());
                    break;
                case POST_XML:
                    response = postJsonAndPut(new HttpPost(url), json, headers, ContentType.TEXT_XML.toString());
                    break;
                case PUT:
                    response = postJsonAndPut(new HttpPut(url), json, headers, ContentType.APPLICATION_JSON.toString());
                    break;
                case DELETE:
                    response = getPostAndDelete(headers, new HttpDelete(url));
                    break;
                default:
            }
            assert response != null;
            result = getContent(response);
        } catch (URISyntaxException | IOException e) {
            if (e instanceof SocketTimeoutException) {
                log.info("请求超时");
            } else {
                log.info(e.getMessage());
            }
        }
        return result;
    }

    private static CloseableHttpResponse postJsonAndPut(HttpEntityEnclosingRequestBase http,
                                                        String json,
                                                        Map<String, String> headers,
                                                        String contentType) throws IOException {
        CloseableHttpResponse response;
        StringEntity entity = new StringEntity(json, Consts.UTF_8);
        http.setEntity(entity);
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                http.setHeader(key, headers.get(key));
            }
        }
        http.setHeader("Content-type", contentType);
        return httpClient.execute(http);
    }

    private static CloseableHttpResponse getPostAndDelete(Map<String, String> headers, HttpRequestBase http) throws IOException {
        CloseableHttpResponse response;
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                http.setHeader(key, headers.get(key));
            }
        }
        return httpClient.execute(http);
    }


    private static CloseableHttpResponse postForm(Map<String, String> params, Map<String, String> headers, URI uri) throws IOException {
        CloseableHttpResponse response;
        HttpPost postForm = new HttpPost(uri);
        if (params != null) {
            List<NameValuePair> paramList = setHttpParams(params);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
            postForm.setEntity(entity);
        }
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                postForm.setHeader(key, headers.get(key));
            }
        }
        return httpClient.execute(postForm);
    }


    /**
     * 获取响应HTTP实体内容
     *
     * @param response 响应体
     * @return {@link String}
     */
    private static String getContent(CloseableHttpResponse response) {
        // 获取响应实体
        HttpEntity entity = response.getEntity();
        String result = "";
        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                // 进行UTF-8编码处理,并用String接收响应实体
                result = EntityUtils.toString(entity, Consts.UTF_8);
                // 消耗响应实体，并关闭相关资源占用
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 设置请求参数
     *
     * @param paramMap 参数映射
     * @return {@link List<NameValuePair>}
     */
    private static List<NameValuePair> setHttpParams(Map<String, String> paramMap) {
        List<NameValuePair> formParams = new ArrayList<>();
        Set<Map.Entry<String, String>> set = paramMap.entrySet();
        for (Map.Entry<String, String> entry : set) {
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return formParams;
    }

    private static final Pattern PATTERN = Pattern.compile("<dd class=\"fz24\">(.*?)</dd>");

    /**
     * 获取外网ip地址
     *
     * @return {@link String}
     */
    public static String getOutsideIpAddress() {
        String content = doGet("http://ip.chinaz.com");
        Matcher m = PATTERN.matcher(content);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * 将map参数转换为参数链
     *
     * @param map 地图
     * @return {@link String}
     */
    public static String convertParamChain(Map<String, String> map) {
        if (map.isEmpty()) {
            return "";
        }
        String string = map.toString();
        String replace = string.replace(", ", "&").replace("{", "?").replace("}", "");
        try {
            return URLEncoder.encode(replace, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }

    }

}

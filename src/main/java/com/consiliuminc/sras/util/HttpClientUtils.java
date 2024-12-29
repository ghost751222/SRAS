package com.consiliuminc.sras.util;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;

import java.net.URI;

public class HttpClientUtils {

    public static HttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }

    public static HttpGet getHttpGetMethod(String uri) {
        return new HttpGet(uri);
    }

    public static HttpPost getHttpPostMethod(String uri) {
        return new HttpPost(uri);
    }

    public static void addHeaderContentJson(AbstractHttpMessage httpMessage) {
        httpMessage.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()));
    }

    public static class HttpGet extends HttpEntityEnclosingRequestBase {

        public static final String METHOD_NAME = "GET";

        public HttpGet(String uri) {
            setURI(URI.create(uri));
            addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }

    }
}

package com.mbh.mbutils.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created By MBH on 2016-06-09.
 */
public class MBHttpUtils {
    public static class Header {
        String name, value;

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static String getResponseStringJsonHeader(String link, Header[] headers, int timeOutSeconds) {
        String finalResult = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOutSeconds, TimeUnit.SECONDS)
                    .writeTimeout(timeOutSeconds, TimeUnit.SECONDS)
                    .readTimeout(timeOutSeconds, TimeUnit.SECONDS)
                    .build();

            Request.Builder requestBuilder = getRequestBuilderWithHeaders(headers);

            requestBuilder.url(link);

            Request okrequest = requestBuilder.build();

            Response okresponse = okHttpClient.newCall(okrequest).execute();

            if (okresponse.isSuccessful()) {
                ResponseBody body = okresponse.body();
                finalResult = body.string();
            }
        } catch (Exception e) {
            finalResult = null;
        }
        return finalResult;
    }

    public static Request.Builder getRequestBuilderWithHeaders(Header[] headers) {

        Request.Builder requestBuilder = new Request.Builder();

        if (headers.length < 1) return requestBuilder;

        Header firstHeader = headers[0];
        requestBuilder.header(firstHeader.name, firstHeader.value);
        for (int i = 1; i < headers.length; i++) {
            Header header = headers[i];
            requestBuilder.addHeader(header.name, header.value);
        }

        return requestBuilder;
    }

    public static String getResponseStringJsonHeader(String link) {
        return getResponseStringJsonHeader(link, getDefaultJsonHeaders(), 30);
    }

    public static Header[] getDefaultJsonHeaders() {
        Header[] headers = new Header[2];
        headers[0] = new Header("Accept", "application/json");
        headers[1] = new Header("Content-type", "application/json");
        return headers;
    }
}

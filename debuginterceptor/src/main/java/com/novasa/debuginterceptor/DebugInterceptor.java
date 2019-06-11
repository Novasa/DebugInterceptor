package com.novasa.debuginterceptor;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by mikkelschlager on 07/12/2016.
 */

@SuppressWarnings({"SameParameterValue", "unused", "DefaultLocale"})
public class DebugInterceptor implements Interceptor {

    private static final String TAG = DebugInterceptor.class.getSimpleName();

    private boolean mPrintRequestHeaders = true;
    private boolean mPrintRequestBody = true;
    private boolean mPrintResponseHeaders = true;
    private boolean mPrintResponseBody = false;
    private boolean mPrintResponseErrorBody = true;

    private String[] mPrintRequestBodyParamsInResponse;
    private Gson mGson;

    /**
     * Default = true
     */
    public DebugInterceptor setPrintRequestHeaders(boolean print) {
        mPrintRequestHeaders = print;
        return this;
    }

    /**
     * Default = true
     */
    public DebugInterceptor setPrintRequestBody(boolean print) {
        mPrintRequestBody = print;
        return this;
    }

    /**
     * Default = true
     */
    public DebugInterceptor setPrintResponseHeaders(boolean print) {
        mPrintResponseHeaders = print;
        return this;
    }

    /**
     * Default = false
     */
    public DebugInterceptor setPrintResponseBody(boolean print) {
        mPrintResponseBody = print;
        return this;
    }

    /**
     * Default = true
     */
    public DebugInterceptor setPrintResponseErrorBody(boolean print) {
        mPrintResponseErrorBody = print;
        return this;
    }

    /**
     * Example use: If the rest method name is in the request body, and you want to print it in the response.
     */
    public DebugInterceptor setPrintRequestBodyParamsInResponse(String... printRequestBodyParamsInResponse) {
        mPrintRequestBodyParamsInResponse = printRequestBodyParamsInResponse;
        mGson = new Gson();
        return this;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        final Request request = chain.request();

        printRequest(request);

        final Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            printError(request, e);
            throw e;
        }

        printResponse(response);
        return response;
    }

    private void printRequest(final Request request) {
        try {
            final String method = request.method();
            final String url = request.url().toString();

            final StringBuilder sb = new StringBuilder("[REQUEST]")
                    .append(String.format(" | METHOD: %s", method))
                    .append(String.format(" | URL: %s", url));

            if (mPrintRequestHeaders) {
                final String headers = headersToString(request.headers());
                sb.append(String.format("\n| HEADERS: %s", headers));
            }

            if (mPrintRequestBody) {
                final String body = parseRequestBody(request.body());
                sb.append(String.format("\n| BODY: %s", body));
            }

            d(sb.toString());

        } catch (Exception e) {
            e(e, "DebugInterceptor threw exception");
        }
    }


    private final static Charset UTF8 = Charset.forName("UTF-8");

    private void printResponse(final Response response) {
        try {
            final String method = response.request().method();
            final String url = response.request().url().toString();
            final int statusCode = response.code();
            final String statusText = response.message();
            final long time = response.receivedResponseAtMillis() - response.sentRequestAtMillis();

            final StringBuilder sb = new StringBuilder("[RESPONSE]")
                    .append(String.format(" | METHOD: %s", method))
                    .append(String.format(" | URL: %s", url))
                    .append(String.format(" | STATUS: %d (%s)", statusCode, !TextUtils.isEmpty(statusText) ? statusText : StatusCodes.STATUS.get(statusCode)))
                    .append(String.format(" | TIME: %d ms", time));

            if (mPrintRequestBodyParamsInResponse != null) {
                try {
                    final String requestBody = parseRequestBody(response.request().body());
                    final Map params = mGson.fromJson(requestBody, Map.class);
                    for (final String p : mPrintRequestBodyParamsInResponse) {
                        if (params.containsKey(p)) {
                            final Object v = params.get(p);
                            sb.append(String.format(" | %s: %s", p, v));
                        }
                    }
                } catch (Exception e) {
                    e(e);
                }
            }

            if (mPrintResponseHeaders) {
                final String headers = headersToString(response.headers());
                sb.append(String.format("\n| HEADERS: %s", headers));
            }

            final boolean success = response.isSuccessful();

            if (success && mPrintResponseBody || !success && mPrintResponseErrorBody) {
                final String body = parseResponseBody(response.body());
                sb.append(String.format("\n| BODY: %s", body));
            }

            if (success) {
                d(sb.toString());

            } else {
                e(sb.toString());
            }

        } catch (Exception e) {
            e(e, "DebugInterceptor threw exception");
        }
    }

    private void printError(final Request request, final Exception e) {
        try {
            final String method = request.method();
            final String url = request.url().toString();
            final String exception = e.getClass().getName();
            final String message = e.getMessage();

            final StringBuilder sb = new StringBuilder("[ERROR]")
                    .append(String.format(" | METHOD: %s", method))
                    .append(String.format(" | URL: %s", url))
                    .append(String.format(" | EXCEPTION: %s", exception))
                    .append(String.format(" | MESSAGE: %s", message));

            e(sb.toString());

        } catch (Exception e2) {
            e(e2, "DebugInterceptor threw exception");
        }
    }

    private String headersToString(Headers headers) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, size = headers.size(); i < size; i++) {
            sb.append("\n|   ").append(headers.name(i)).append(": ").append(headers.value(i));
        }
        return sb.toString();
    }

    private String parseRequestBody(RequestBody body) {
        try {
            final String bodyString;

            if (body != null) {
                final Buffer buffer = new Buffer();
                body.writeTo(buffer);
                return buffer.readUtf8();

            } else {
                return "[NONE]";
            }

        } catch (Exception e) {
            e(e);
        }

        return null;
    }

    private String parseResponseBody(ResponseBody body) {
        try {
            if (body != null) {

                // Log the response body without consuming it
                final BufferedSource source = body.source();

                // Buffer the entire body.
                source.request(Long.MAX_VALUE);
                final Buffer buffer = source.getBuffer();

                Charset charset = null;
                final MediaType contentType = body.contentType();

                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        e("Couldn't decode the response body; charset is likely malformed.");
                    }
                }

                if (charset == null) {
                    charset = UTF8;
                }

                final Buffer clone = buffer.clone();

                final long size = clone.size();
                final String content = clone.readString(charset);

                return String.format("size: %d bytes, content: %s", size, !TextUtils.isEmpty(content) ? content : "[NONE]");

            } else {
                return "[NONE]";
            }

        } catch (Exception e) {
            e(e);
        }

        return null;
    }

    private void d(String m, Object... p) {
        Log.d(TAG, format(m, p));
    }

    private void e(String m, Object... p) {
        Log.e(TAG, format(m, p));
    }

    private void e(Exception e) {
        Log.e(TAG, "", e);
    }

    private void e(Exception e, String m, Object... p) {
        Log.e(TAG, format(m, p), e);
    }

    private String format(String m, Object... p) {
        if (p != null && p.length > 0) {
            return String.format(m, p);

        } else {
            return m;
        }
    }
}

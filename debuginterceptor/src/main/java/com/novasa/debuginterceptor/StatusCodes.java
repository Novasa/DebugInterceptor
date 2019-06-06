package com.novasa.debuginterceptor;

import android.util.SparseArray;

/**
 * <p>List taken from <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">Wikipedia</a>
 */
public final class StatusCodes {

    public static final SparseArray<String> STATUS;

    static {
        STATUS = new SparseArray<>();

        STATUS.put(100, "Continue");
        STATUS.put(101, "Switching Protocols");
        STATUS.put(102, "Processing");
        STATUS.put(103, "Early Hints");

        STATUS.put(200, "OK");
        STATUS.put(201, "Created");
        STATUS.put(202, "Accepted");
        STATUS.put(203, "Non - Authoritative Information");
        STATUS.put(204, "No Content");
        STATUS.put(205, "Reset Content");
        STATUS.put(206, "Partial Content");
        STATUS.put(207, "Multi - Status");
        STATUS.put(208, "Already Reported");
        STATUS.put(226, "IM Used");

        STATUS.put(300, "Multiple Choices");
        STATUS.put(301, "Moved Permanently");
        STATUS.put(302, "Found");
        STATUS.put(303, "See Other");
        STATUS.put(304, "Not Modified");
        STATUS.put(305, "Use Proxy");
        STATUS.put(306, "Switch Proxy");
        STATUS.put(307, "Temporary Redirect");
        STATUS.put(308, "Permanent Redirect");

        STATUS.put(400, "Bad Request");
        STATUS.put(401, "Unauthorized");
        STATUS.put(402, "Payment Required");
        STATUS.put(403, "Forbidden");
        STATUS.put(404, "Not Found");
        STATUS.put(405, "Method Not Allowed");
        STATUS.put(406, "Not Acceptable");
        STATUS.put(407, "Proxy Authentication Required");
        STATUS.put(408, "Request Timeout");
        STATUS.put(409, "Conflict");
        STATUS.put(410, "Gone");
        STATUS.put(411, "Length Required");
        STATUS.put(412, "Precondition Failed");
        STATUS.put(413, "Payload Too Large");
        STATUS.put(414, "URI Too Long");
        STATUS.put(415, "Unsupported Media Type");
        STATUS.put(416, "Range Not Satisfiable");
        STATUS.put(417, "Expectation Failed");
        STATUS.put(418, "I 'm a teapot");
        STATUS.put(421, "Misdirected Request");
        STATUS.put(422, "Unprocessable Entity");
        STATUS.put(423, "Locked");
        STATUS.put(424, "Failed Dependency");
        STATUS.put(425, "Too Early");
        STATUS.put(426, "Upgrade Required");
        STATUS.put(428, "Precondition Required");
        STATUS.put(429, "Too Many Requests");
        STATUS.put(431, "Request Header Fields Too Large");
        STATUS.put(451, "Unavailable For Legal Reasons");

        STATUS.put(500, "Internal Server Error");
        STATUS.put(501, "Not Implemented");
        STATUS.put(502, "Bad Gateway");
        STATUS.put(503, "Service Unavailable");
        STATUS.put(504, "Gateway Timeout");
        STATUS.put(505, "HTTP Version Not Supported");
        STATUS.put(506, "Variant Also Negotiates");
        STATUS.put(507, "Insufficient Storage");
        STATUS.put(508, "Loop Detected");
        STATUS.put(510, "Not Extended");
        STATUS.put(511, "Network Authentication Required");
    }
}
package net.smartcosmos.test.util;

import org.apache.commons.codec.binary.Base64;

public class ResourceTestUtil {

    public static String basicAuth(String username, String password) {

        byte[] bytes = (username + ":" + password).getBytes();

        return "Basic " + Base64.encodeBase64String(bytes);
    }
}

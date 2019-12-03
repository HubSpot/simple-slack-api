package com.ullink.slack.simpleslackapi.impl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.WebSocketContainer;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import com.ullink.slack.simpleslackapi.WebSocketContainerProvider;

public class DefaultWebSocketContainerProvider implements WebSocketContainerProvider
{
    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private String proxyAddress;
    private int proxyPort;
    private String proxyUser;
    private String proxyPassword;

    DefaultWebSocketContainerProvider(String proxyAdress, int proxyPort, String proxyUser, String proxyPassword) {
        this.proxyAddress = proxyAdress;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
    }

    @Override
    public WebSocketContainer getWebSocketContainer()
    {
        ClientManager clientManager = ClientManager.createClient();
        if (proxyAddress != null)
        {
            clientManager.getProperties().put(ClientProperties.PROXY_URI, "http://" + proxyAddress + ":" + proxyPort);
        }
        if (proxyUser != null)
        {
            Map<String, String> headers = new HashMap<>();
            headers.put("Proxy-Authorization", "Basic " + encodeToString((proxyUser + ":" + proxyPassword).getBytes(Charset.forName("UTF-8")), false));
            clientManager.getProperties().put(ClientProperties.PROXY_HEADERS, headers);
        }
        return clientManager;
    }

    // copied form older tyrus Base64Utils
    private static char[] encodeToChar(byte[] sArr, boolean lineSep)
    {
        // Check special case
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0)
            return new char[0];

        int eLen = (sLen / 3) * 3;              // Length of even 24-bits.
        int cCnt = ((sLen - 1) / 3 + 1) << 2;   // Returned character count
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0); // Length of returned array
        char[] dArr = new char[dLen];

        // Encode even 24-bits
        for (int s = 0, d = 0, cc = 0; s < eLen;) {
            // Copy next three bytes into lower 24 bits of int, paying attention to sign.
            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8 | (sArr[s++] & 0xff);

            // Encode the int into four chars
            dArr[d++] = CA[(i >>> 18) & 0x3f];
            dArr[d++] = CA[(i >>> 12) & 0x3f];
            dArr[d++] = CA[(i >>> 6) & 0x3f];
            dArr[d++] = CA[i & 0x3f];

            // Add optional line separator
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }

        // Pad and encode last bits if source isn't even 24 bits.
        int left = sLen - eLen; // 0 - 2.
        if (left > 0) {
            // Prepare the int
            int i = ((sArr[eLen] & 0xff) << 10) | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            // Set last four chars
            dArr[dLen - 4] = CA[i >> 12];
            dArr[dLen - 3] = CA[(i >>> 6) & 0x3f];
            dArr[dLen - 2] = left == 2 ? CA[i & 0x3f] : '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }

    public static String encodeToString(byte[] sArr, boolean lineSep)
    {
        return new String(encodeToChar(sArr, lineSep));
    }
}

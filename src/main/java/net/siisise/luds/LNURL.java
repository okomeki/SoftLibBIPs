/*
 * Copyright 2023 okome.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.siisise.luds;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.siisise.bips.Bech32;

/**
 * LUD-01 Base LNURL encoding and decoding.
 * Bech32 エンコードされたWALLET HTTPS/Onion URL
 * https://github.com/lnurl/luds/blob/luds/01.md
 */
public class LNURL {
    
    /**
     * lnurl で保存する.
     */
    final String lnurl;

    /**
     * 
     * @param url WALLET / SERVICE URL/LNURL
     */
    public LNURL(String url) {
        if ( url.startsWith("http")) { // URL と想定
            lnurl = encode(url);
        } else if ( url.toLowerCase().startsWith("lnurl1")) {
            lnurl = url;
        }
        throw new java.lang.IllegalStateException();
    }
    
    public LNURL(URL url) {
        lnurl = encode(url.toString());
    }
    
    public static boolean isLNURL(String src) {
        Bech32 b32 = new Bech32("lnurl");
        try {
            b32.decode(src);
            return true;
        } catch (java.lang.IllegalStateException e) {
            return false;
        }
    }

    /**
     * URL を LNURL に変換する.
     * @param url WALLET / SERVICE URL us-ascii 文字のみの想定
     * その他は %エンコード か utf-8 どちらがいいのか.
     * @return LNURL
     */
    public static String encode(String url) {
        Bech32 b32 = new Bech32("lnurl");
        
        return b32.encode(url.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * LNURL を URL など元の文字列に変換する.
     * @param lnurl LNURL
     * @return WALLET / SERVICE URL
     * @throws java.lang.IllegalStateException 仮 不正書式.
     */
    public static String decode(String lnurl) {
        Bech32 b32 = new Bech32("lnurl");
        return new String(b32.decode(lnurl), StandardCharsets.UTF_8);
    }
    
    @Override
    public String toString() {
        return lnurl;
    }
    
    public String toQR() {
        return lnurl.toUpperCase();
    }
    
    /**
     * 中のURL
     * @return
     * @throws MalformedURLException 
     */
    public URL toURL() throws MalformedURLException {
        return new URL(decode(lnurl));
    }
    
}

package com.fanshanhong.nettydemo.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-28 22:24
 * @Modify:
 */
public class CharsetTest {
    public static void main(String[] args) throws Exception {
        Charset charset = Charset.forName("utf-8");

        // decoder：解码。字节数组  转成成字符串。
        CharsetDecoder decoder = charset.newDecoder();

        // encoder：编码。将字符、字符串转成  字节数组
        CharsetEncoder encoder = charset.newEncoder();

        CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap("aaa".getBytes("UTF-8")));
        ByteBuffer byteBuffer = encoder.encode(charBuffer);


        // 查看所有支持的字符编码集合
        SortedMap<String, Charset> availableCharsets = Charset.availableCharsets();


        // 遍历方式1
        Set<Map.Entry<String, Charset>> entries = availableCharsets.entrySet();
        for (Map.Entry<String, Charset> entry : entries) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
        }

        // 遍历方式2
        Set<String> keys = availableCharsets.keySet();
        for (String key : keys) {
            System.out.println(key + " :   " + availableCharsets.get(key));
        }
    }
}

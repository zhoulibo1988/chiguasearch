package com.dyhdyh.magnetw.util;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * author  dengyuhan
 * created 2018/3/7 14:00
 */
public class GsonUtil {
    private static Gson gson = new Gson();

    public static <T> T fromJson(InputStream inputStream, TypeToken<T> token) {
        try {
			return gson.fromJson(new InputStreamReader(inputStream,"UTF-8"), token.getType());
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
    }
}

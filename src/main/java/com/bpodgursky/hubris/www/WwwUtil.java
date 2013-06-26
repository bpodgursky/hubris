package com.bpodgursky.hubris.www;

import com.google.gson.Gson;
import javax.servlet.http.Cookie;

public class WwwUtil {
  public static String getCookie(String cookie, Cookie[] cookies) {
    for (Cookie candidate : cookies) {
      if (candidate.getName().equals(cookie)) {
        return candidate.getValue();
      }
    }
    return null;
  }

  public static <T> String asJson(T object) {
    return new Gson().toJson(object);
  }
}

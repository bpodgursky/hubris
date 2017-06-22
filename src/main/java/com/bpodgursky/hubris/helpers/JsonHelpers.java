package com.bpodgursky.hubris.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonHelpers {
  public static <T> T getOrNull(Gson gson, JsonObject obj, String key, Class<T> klass) {
    return obj.has(key) ? gson.fromJson(obj.get(key), klass) : null;
  }
}

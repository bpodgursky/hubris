package com.bpodgursky.hubris.common;

import com.google.common.collect.Maps;
import org.json.JSONObject;

import java.util.Map;

public class HubrisUtil {
  public static <K, V> Map<K, JSONObject> asJsonMap(Map<K, V> map) {
    Map<K, JSONObject> rmap = Maps.newHashMap();
    for (Map.Entry<K, V> entry : map.entrySet()) {
      rmap.put(entry.getKey(), new JSONObject(entry.getValue()));
    }
    return rmap;
  }
}

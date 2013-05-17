package com.bpodgursky.hubris.universe;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {

  public final Integer from;
  public final Long at;
  public final String body;

  public Comment(Integer from, Long at, String body) {
    this.from = from;
    this.at = at;
    this.body = body;
  }

  public String toString() {

    JSONObject json = new JSONObject();
    try {
      json.put("from", from);
      json.put("at", at);
      json.put("body", body);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json.toString();
  }
}

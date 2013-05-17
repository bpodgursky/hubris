package com.bpodgursky.hubris.event;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Message extends GameEvent {

  public final Integer fromPlayer;
  public final List<Integer> toPlayer;
  public final String subject;
  public final String status;
  public final String body;

  public Message(Integer from, List<Integer> to, Long timestamp, String key, String subject, String body, String status) {
    super(key, timestamp);

    this.fromPlayer = from;
    this.toPlayer = to;
    this.subject = subject;
    this.body = body;
    this.status = status;
  }

  public String toString() {

    JSONObject json = new JSONObject();
    try {
      json.put("fromPlayer", fromPlayer);
      json.put("toPlayer", toPlayer);
      json.put("timestamp", at);
      json.put("key", key);
      json.put("body", body);
      json.put("subject", subject);
      json.put("status", status);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json.toString();
  }
}

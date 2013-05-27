package com.bpodgursky.hubris.server;

import com.bpodgursky.hubris.client.SingleGameClient;

public class StateUpdatesProducer {
  private final SingleGameClient client;

  public StateUpdatesProducer(SingleGameClient client) {
    this.client = client;
  }
}

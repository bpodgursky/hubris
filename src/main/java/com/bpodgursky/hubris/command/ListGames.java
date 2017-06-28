package com.bpodgursky.hubris.command;

import java.util.Map;

public class ListGames extends GameRequest {
  public ListGames(RequestType requestType, Integer player, String userName, Long game) {
    super(RequestType.ListGames, 0, "", 0L);
  }

  @Override
  protected void addRequestParams(Map<String, String> params) throws Exception {
    params.put("type", "init_player");
  }
}

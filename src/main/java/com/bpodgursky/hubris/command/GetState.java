package com.bpodgursky.hubris.command;

import com.bpodgursky.hubris.universe.GameState;

import java.util.Map;

public class GetState extends GameRequest {

  public GetState(Integer player, String userName, Long game) {
    super(RequestType.GetState, player, userName, game);
  }

  @Override
  protected void addRequestParams(Map<String, String> params) throws Exception {
    params.put("order", "full_universe_report");
  }
}

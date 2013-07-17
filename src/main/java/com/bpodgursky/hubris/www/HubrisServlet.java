package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.db.HubrisDb;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public abstract class HubrisServlet extends HttpServlet {
  protected final HubrisDb db;

  public HubrisServlet() {
    super();
    db = new HubrisDb.Factory().getProduction();
  }

  public NpCookies getCookies(HttpServletRequest request) {
    String uuid = WwwUtil.getCookie("uuid", request.getCookies());

    if (uuid != null) {
      return db.npCookies().fetchOneByUuid(uuid);
    }
    else {
      return null;
    }
  }
}

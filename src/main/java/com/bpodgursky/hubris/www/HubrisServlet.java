package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.db.HubrisDb;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import static com.bpodgursky.hubris.db.CookiesPersistence.CookiesResult;

public abstract class HubrisServlet extends HttpServlet {
  protected final HubrisDb db;

  public HubrisServlet() {
    super();
    db = HubrisDb.get();
  }

  public CookiesResult getCookies(HttpServletRequest request) {
    String uuid = WwwUtil.getCookie("uuid", request.getCookies());

    if (uuid != null) {
      try {
        return db.cookiesPersistence().findByUuid(uuid);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      return null;
    }
  }
}

package com.bpodgursky.hubris.www;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bpodgursky.hubris.db.CookiesPersistence.CookiesResult;

public class HubrisDefaultServlet extends HubrisServlet {
  private static final Logger LOG = LoggerFactory.getLogger(HubrisDefaultServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uuid = WwwUtil.getCookie("uuid", req.getCookies());
    CookiesResult cookies = getCookies(req);

    if (cookies == null) {
      req.getRequestDispatcher("login.jsp").forward(req, resp);
    }
    else {
      req.getRequestDispatcher("/_games/index.jsp").forward(req, resp);
    }
  }
}

package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HubrisDefaultServlet extends HubrisServlet {
  private static final Logger LOG = LoggerFactory.getLogger(HubrisDefaultServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String uuid = WwwUtil.getCookie("uuid", req.getCookies());
    NpCookies cookies = getCookies(req);

    if (cookies == null) {
      req.getRequestDispatcher("login.jsp").forward(req, resp);
    }
    else {
      req.getRequestDispatcher("/_games/index.jsp").forward(req, resp);
    }
  }
}

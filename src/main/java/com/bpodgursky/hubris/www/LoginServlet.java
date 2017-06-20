package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.db.models.hubris.tables.pojos.NpCookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.bpodgursky.hubris.account.LoginClient.LoginResponse;

public class LoginServlet extends HubrisServlet {
  private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    LoginClient client = new LoginClient();

    if ("login".equals(req.getParameter("state"))) {
      String username = req.getParameter("username");
      String password = req.getParameter("password");
      LoginResponse response = client.login(username, password);

      if (response.getResponseType() == LoginClient.LoginResponseType.INVALID_LOGIN) {
        req.setAttribute("error", "Invalid login credentials.");
        req.getRequestDispatcher("login.jsp").forward(req, resp);
      }
      else if (response.getResponseType() == LoginClient.LoginResponseType.SUCCESS) {
        String authToken = req.getParameter("auth_token");
        String uuid = UUID.randomUUID().toString();
        setCookies(uuid, response.getCookies());
        NpCookies existingCookies = db.npCookies().fetchOneByUsername(username);

        if (existingCookies == null) {
          NpCookies cookiesRow = new NpCookies();
          cookiesRow.setCookies(response.getCookies());
          cookiesRow.setUsername(username);
          cookiesRow.setUuid(uuid);
          db.npCookies().insert(cookiesRow);

          resp.addCookie(new Cookie("uuid", uuid));
        }
        else {
          existingCookies.setCookies(response.getCookies());
          db.npCookies().update(existingCookies);

          resp.addCookie(new Cookie("uuid", existingCookies.getUuid()));
        }

        req.getRequestDispatcher("/_games/index.jsp").forward(req, resp);
      }
    }
  }

  protected String getCookies(String uuid) {
    return getCookies().get(uuid);
  }

  protected void setCookies(String uuid, String cookies) {
    getCookies().put(uuid, cookies);
  }

  protected Map<String, String> getCookies() {
    return (Map<String, String>) getServletContext().getAttribute("cookies");
  }

  protected Map<String, LoginResponse> getLoginResponses() {
    return (Map<String, LoginResponse>)getServletContext().getAttribute("login_clients");
  }
}

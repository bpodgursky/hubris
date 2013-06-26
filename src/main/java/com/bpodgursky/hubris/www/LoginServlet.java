package com.bpodgursky.hubris.www;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.db.HubrisDb;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bpodgursky.hubris.account.LoginClient.LoginResponse;

public class LoginServlet extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    LoginClient client = new LoginClient();
    if ("login".equals(req.getParameter("state"))) {
      String username = req.getParameter("username");
      String password = req.getParameter("password");
      LoginResponse response = client.login(username, password);

      if (response.getResponseType() == LoginClient.LoginResponseType.NEEDS_TWO_FACTOR_AUTH_TOKEN) {
        String uuid = createPreviousResponse(response);

        req.setAttribute("uuid", uuid);
        req.setAttribute("username", username);
        req.getRequestDispatcher("two_factor_auth.jsp").forward(req, resp);
      }
      else if (response.getResponseType() == LoginClient.LoginResponseType.INVALID_LOGIN) {
        req.setAttribute("error", "Invalid login credentials.");
        req.getRequestDispatcher("login.jsp").forward(req, resp);
      }
      else if (response.getResponseType() == LoginClient.LoginResponseType.SUCCESS) {
        throw new RuntimeException("Login successful.");
      }
    }
    else if ("two_factor".equals(req.getParameter("state"))) {
      String authToken = req.getParameter("auth_token");
      String uuid = req.getParameter("uuid");
      String username = req.getParameter("username");
      LoginResponse previousResponse = getPreviousResponse(uuid);
      LoginResponse twoFactorAuthResponse = client.submitTwoFactorAuthToken(previousResponse, authToken);

      if (twoFactorAuthResponse.getResponseType() == LoginClient.LoginResponseType.INVALID_TWO_FACTOR_AUTH_TOKEN) {
        req.setAttribute("error", "Invalid two-factor auth token.");
        req.getRequestDispatcher("login.jsp").forward(req, resp);
      }
      else if (twoFactorAuthResponse.getResponseType() == LoginClient.LoginResponseType.SUCCESS) {
        clearResponse(uuid);
        setCookies(uuid, twoFactorAuthResponse.getCookies());
        resp.addCookie(new Cookie("uuid", uuid));
        try {
          HubrisDb.get().cookiesPersistence().create(uuid, username, twoFactorAuthResponse.getCookies());
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        req.getRequestDispatcher("/").forward(req, resp);
      }
    }
  }

  protected void clearResponse(String uuid) {
    getLoginResponses().remove(uuid);
  }

  protected String createPreviousResponse(LoginResponse response) {
    String uuid = UUID.randomUUID().toString();
    getLoginResponses().put(uuid, response);
    return uuid;
  }

  protected LoginResponse getPreviousResponse(String uuid) {
    return getLoginResponses().get(uuid);
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

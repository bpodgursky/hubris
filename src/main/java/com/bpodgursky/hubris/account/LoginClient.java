package com.bpodgursky.hubris.account;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.bpodgursky.hubris.common.HubrisConstants;
import com.gistlabs.mechanize.MechanizeAgent;
import com.gistlabs.mechanize.document.Document;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class LoginClient {
  public enum LoginResponseType {
    SUCCESS, INVALID_LOGIN;
  }

  public static class LoginResponse {
    private final LoginResponseType responseType;
    private final String cookies;
    private final MechanizeAgent agent;
    private final Document page;

    public LoginResponse(LoginResponseType responseType, String cookies, MechanizeAgent agent, Document page) {
      this.responseType = responseType;
      this.cookies = cookies;
      this.agent = agent;
      this.page = page;
    }

    public LoginResponseType getResponseType() {
      return responseType;
    }

    public String getCookies() {
      return cookies;
    }

    protected MechanizeAgent getAgent() {
      return agent;
    }

    protected Document getPage() {
      return page;
    }
  }

  public LoginResponse login(String username, String password) {
    CookieStore cookies = new BasicCookieStore();
    HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookies).build();
    HttpPost request = new HttpPost(HubrisConstants.loginUrl);
    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("type", "login"));
    nvps.add(new BasicNameValuePair("alias", username));
    nvps.add(new BasicNameValuePair("password", password));
    try {
      request.setEntity(new UrlEncodedFormEntity(nvps));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    try {
      HttpResponse httpResponse = client.execute(request);
      String body = EntityUtils.toString(httpResponse.getEntity());
      JsonArray response = new JsonParser().parse(body).getAsJsonArray();
      LoginResponseType responseType;
      String cookieStr = "";

      if (response.get(0).getAsString().equals("meta:login_success")) {
        responseType = LoginResponseType.SUCCESS;

        for (Cookie cookie : cookies.getCookies()) {
          if (cookie.getName().equals("auth")) {
            cookieStr = "auth=" + cookie.getValue();
          }
        }
      } else {
        responseType = LoginResponseType.INVALID_LOGIN;
      }


      return new LoginResponse(responseType, cookieStr, null, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

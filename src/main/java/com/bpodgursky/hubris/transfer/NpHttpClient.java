package com.bpodgursky.hubris.transfer;

import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.common.HubrisConstants;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

public class NpHttpClient {
  private final String cookies;

  public NpHttpClient(String cookies) {
    this.cookies = cookies;
  }

  /**
   * Sends a post request to URL with the provided data. Returns the response as
   * a String if nothing went wrong (i.e., return code = 200). Otherwise, returns
   * null.
   *
   * @param url
   * @param data
   * @return
   */
  public String post(String url, String data) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int code = post(url, data, out);

    if (code == HttpURLConnection.HTTP_OK) {
      return out.toString();
    } else {
      return null;
    }
  }

  /**
   * Posts to the specified URL with no data
   *
   * @param url
   * @return
   */
  public String post(String url) {
    return post(url, "");
  }

  /**
   * Posts to the specified URL, writing output to the provided {@link OutputStream}.
   * Returns the return code.
   *
   * @param url
   * @param out
   * @return
   */
  public int post(String url, OutputStream out) {
    return post(url, "", out);
  }

  /**
   * Posts to the specified URL with the specified data, writing output to the provided
   * {@link OutputStream}
   *
   * @param url
   * @param data
   * @param out
   * @return
   */
  public int post(String url, String data, OutputStream out) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

      setProperties(connection);
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestMethod("POST");
      connection.getOutputStream().write(data.getBytes());

      int returnCode = connection.getResponseCode();

      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      PrintStream outs = new PrintStream(out);
      String line;
      while ((line = in.readLine()) != null) {
        outs.println(line);
      }
      in.close();
      outs.close();
      connection.disconnect();

      return returnCode;
    } catch (ProtocolException e) {
      throw new RuntimeException(e); // this should never happen
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: better error handling
    }
  }

  /**
   * Sends a GET request to the specified URL, returning the response as a String
   * if everything went smoothly (i.e., HTTP return code == 200). Returns null if
   * something went wrong.
   *
   * @param url
   * @return
   */
  public String get(String url) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int code = get(url, out);

    if (code == HttpURLConnection.HTTP_OK) {
      return out.toString();
    } else {
      return null;
    }
  }

  /**
   * Sends a GET request to the specified URL, writing the output to the specified
   * {@link OutputStream}. Returns the return code.
   *
   * @param url
   * @param out
   * @return
   */
  public int get(String url, OutputStream out) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      setProperties(connection);
      connection.setRequestMethod("GET");

      int returnCode = connection.getResponseCode();

      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      PrintStream outs = new PrintStream(out);
      String line;
      while ((line = in.readLine()) != null) {
        outs.println(line);
      }
      in.close();
      outs.close();
      connection.disconnect();

      return returnCode;
    } catch (ProtocolException e) {
      throw new RuntimeException(e); // this should never happen
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: better error handling
    }
  }

  /**
   * Sets a bunch of default properties
   *
   * @param conn
   */
  private void setProperties(URLConnection conn) {
    conn.setRequestProperty("User-Agent", HubrisConstants.userAgent);
    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    conn.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
    conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
    conn.setRequestProperty("Keep-Alive", "115");
    conn.setRequestProperty("Connection", "keep-alive");
    conn.setRequestProperty("Cookie", cookies);
  }

  public static void main(String[] args) {
    System.out.println("Logging in...");
    LoginClient loginClient = new LoginClient();
    LoginClient.LoginResponse response = loginClient.login("rapleaf.np.test@gmail.com", "rapleaf_np");

    if (response.getResponseType() != LoginClient.LoginResponseType.SUCCESS) {
    }
    NpHttpClient client = new NpHttpClient(response.getCookies());
    System.out.println(client.get("http://np.ironhelmet.com/account"));
  }
}

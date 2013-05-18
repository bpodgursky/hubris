package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.account.ExceptionThrowingTwoFactorCallback;
import com.bpodgursky.hubris.account.LoginClient;
import com.bpodgursky.hubris.account.StdinTwoFactorAuthCallback;
import com.bpodgursky.hubris.account.TwoFactorAuthCallback;
import jline.console.ConsoleReader;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ClientSettings {
  private static final ConsoleReader CONSOLE;
  static {
    try {
      CONSOLE = new ConsoleReader();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String username;
  private String password;
  private boolean promptPassword;
  private boolean promptTwoFactorAuth;
  private String cookiesCacheFile;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isPromptPassword() {
    return promptPassword;
  }

  public void setPromptPassword(boolean promptPassword) {
    this.promptPassword = promptPassword;
  }

  public String getCookiesCacheFile() {
    return cookiesCacheFile;
  }

  public void setCookiesCacheFile(String cookiesCacheFile) {
    this.cookiesCacheFile = cookiesCacheFile;
  }

  public boolean isPromptTwoFactorAuth() {
    return promptTwoFactorAuth;
  }

  public void setPromptTwoFactorAuth(boolean promptTwoFactorAuth) {
    this.promptTwoFactorAuth = promptTwoFactorAuth;
  }

  public boolean hasCookieCache() {
    return getCookiesCacheFile() != null && new File(getCookiesCacheFile()).exists();
  }

  public TwoFactorAuthCallback getTwoFactorAuthCallback() {
    if (isPromptTwoFactorAuth()) {
      return new StdinTwoFactorAuthCallback();
    }
    else {
      return new ExceptionThrowingTwoFactorCallback("Two-factor auth needed, but prompt not enabled");
    }
  }

  public void writeCookiesToCache(String cookies) throws IOException {
    if (getCookiesCacheFile() == null) {
      throw new RuntimeException("No cookie cache file specified, but tried to access it");
    }
    FileUtils.writeStringToFile(new File(getCookiesCacheFile()), cookies);
  }

  public String readCookiesFromCache() throws IOException {
    if (getCookiesCacheFile() == null) {
      throw new RuntimeException("No cookie cache file specified, but tried to access it");
    }
    return FileUtils.readFileToString(new File(getCookiesCacheFile()));
  }

  public String getCookies() throws IOException {
    String cookies;

    if (getUsername() == null) {
      throw new RuntimeException("Missing required setting: username");
    }

    if (!hasCookieCache() || (cookies = readCookiesFromCache()).isEmpty()) {
      LoginClient client = new LoginClient();

      String password;
      if (getPassword() != null) {
        password = getPassword();
      }
      else {
        password = CONSOLE.readLine("Please enter your password...: ", '*');
      }
      LoginClient.LoginResponse loginResponse = client.login(getUsername(), password, getTwoFactorAuthCallback());

      if (loginResponse.getResponseType() != LoginClient.LoginResponseType.SUCCESS) {
        throw new RuntimeException("Failed to login.");
      }
      writeCookiesToCache(loginResponse.getCookies());
      cookies = loginResponse.getCookies();
    }

    return cookies;
  }

  public static ClientSettings loadFromYaml(String filename) throws FileNotFoundException {
    return new Yaml().loadAs(new FileReader(filename), ClientSettings.class);
  }
}

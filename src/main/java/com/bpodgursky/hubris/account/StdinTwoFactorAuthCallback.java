package com.bpodgursky.hubris.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StdinTwoFactorAuthCallback implements TwoFactorAuthCallback {
  @Override
  public String requestAuthToken() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Please enter two-factor auth token: ");
    try {
      return reader.readLine();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

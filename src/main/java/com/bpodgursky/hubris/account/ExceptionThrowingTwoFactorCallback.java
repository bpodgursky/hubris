package com.bpodgursky.hubris.account;

public class ExceptionThrowingTwoFactorCallback implements TwoFactorAuthCallback {
  private final RuntimeException e;

  public ExceptionThrowingTwoFactorCallback(RuntimeException e) {
    this.e = e;
  }

  public ExceptionThrowingTwoFactorCallback(String message) {
    this.e = new RuntimeException(message);
  }

  @Override
  public String requestAuthToken() {
    throw e;
  }
}

package com.bpodgursky.hubris.account;

/**
 * Allows {@link LoginClient} to query the caller for a two-factor authentication code if it encounters
 * a need for one.
 */
public interface TwoFactorAuthCallback {
  public String requestAuthToken();
}

package com.bpodgursky.hubris.account;

import com.bpodgursky.hubris.common.HubrisConstants;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class logs into Neptune's Pride using a Google account. The result
 * is a cookie that one can attach to requests to np.ironhelmet.com 
 * 
 * @author mullins
 *
 */
public class LoginClient {
	/** 
	 * This is the URL that we should POST to with the login information.
	 * It shouldn't change unless Google messes with things.
	 */
	private static final String LOGIN_POST_URL = "https://accounts.google.com/ServiceLoginAuth";
	
	/**
	 * This is the URL that we request to login. It should match the URL
	 * that the "Login" link on the NP homepage gives.
	 */
	private static final String LOGIN_FORM_URL = "https://accounts.google.com/ServiceLogin?service=ah&passive=true&continue=https://appengine.google.com/_ah/conflogin%3Fcontinue%3Dhttp://np.ironhelmet.com/account&ltmpl=gm&shdf=ChsLEgZhaG5hbWUaD05lcHR1bmUncyBQcmlkZQwSAmFoIhSumdba3pSTs95XxzwvB117zKmLpygBMhSNzRCSN0XUIqypF_-Oap-STk48lg";
	
	/**
	 * Another URL that gets used in the Google login process. One gets
	 * a cookie from the earlier step and submits it to this URL to 
	 * continue.
	 */
	private static final String CONF_LOGIN_URL = "https://appengine.google.com/_ah/conflogin";
	
	/**
	 * This is the URL we want to end up at eventually (after 9999999999999999999 redirects)
	 */
	private static final String DEST_URL = HubrisConstants.accountHomeUrl;
	
	/**
	 * Google embeds some hidden input fields in the login form. Use this
	 * pattern to extract them.
	 */
	private static final Pattern STATE_PATTERN = Pattern.compile("name=\"state\"[\\s\\n\\r]*value=\"([^\"]+)\"");

	
	/**
	 * If there is a persisted cookie, this deletes it.
	 */
	public void logout(String username) {
		HubrisConstants.preferences.remove(getCookieKey(username));
	}
	
	/**
	 * Tries to get the persisted cookie from the application settings. If it fails,
	 * attempts a login with the provided username and password and persists the
	 * cookie.
	 * 
	 * @return
	 */
	public String getPersistedCookie(String username) {
		return HubrisConstants.preferences.get(getCookieKey(username), null);
	}
	
	/**
	 * Logs into NP, returning a {@link LoginResponse}
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public LoginResponse login(String username, String password) {
		// There are several redirects that happen during this login process, and
		// we have to extract/set cookies at each step, so we don't want to 
		// automatically follow redirects because the default implementation doesn't
		// extract cookies.
		boolean follow = HttpURLConnection.getFollowRedirects();
		HttpURLConnection.setFollowRedirects(false);
		
		// TODO: better error handling
		try {
			// ===================================================================
			// First, we have to request the login page and scrape some cookies.
			
			HttpURLConnection loginConn = (HttpURLConnection)new URL(LOGIN_FORM_URL).openConnection();
			setRequestProperties(loginConn);
			loginConn.getResponseCode(); // submit request
			
			String loginCookies = extractCookies(loginConn); // why do we need these? ugh.
			String galx = extractGalxFromCookies(loginCookies); //bleh
			
			// ===================================================================
			// This is where we submit login credentials to google. We then get
			// redirected several times, eventually back to the NP homepage, where
			// we get a cookie that allows us to stay logged in.
			String postParams = buildPostParams(username, password, galx);
			
			URL url = new URL(LOGIN_POST_URL);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			setRequestProperties(conn);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", loginCookies);
			setPostData(conn, postParams);

			// ===================================================================
			// If everything went according to plan, we should get three
			// consecutive google redirects. If this doesn't happen, something
			// went wrong.
			
			// If there isn't a redirect after the first request, this almost
			// definitely means that an invalid username/password pair was
			// given.
			if (conn.getHeaderFields().get("Location") == null) {
				return new LoginResponse(LoginResponseType.INVALID_LOGIN);
			}
			
			conn = followRedirectionWithCookies(conn); // Google redir 1
			conn = followRedirectionWithCookies(conn); // Google redir 2
			conn = followRedirectionWithCookies(conn); // Google redir 3
			
			if (conn.getHeaderField("Location") == null) {
				throw new RuntimeException("Expected redirect #4, but didn't get one.");
			}
			
			// If the user needs to "Approve" NP, then we'll get another redirect
			// here, but not to DEST_URL - to another Google page.
			if (! conn.getHeaderField("Location").equals(DEST_URL)) {
				// Scrape some form fields before submitting
				conn = followRedirectionWithCookies(conn);
				String state = extractStateString(getResponse(conn));
				String params = buildPostParamsForAllowForm(state);
				String cookies = conn.getRequestProperty("Cookie");
				
				// Submit form
				conn = (HttpURLConnection)new URL(CONF_LOGIN_URL).openConnection();
				setRequestProperties(conn);
				conn.setRequestProperty("Cookie", cookies);
				setPostData(conn, params);
				
				// Another damn redirect. After this, we should be in the same 
				// state we would be if we didn't have to approve.
				conn = followRedirectionWithCookies(conn);
			}
			
			// If we followed this redirect, we'd end up on the NP homepage, logged
			// in. However, at ths point, we have the login cookie, which is all we
			// need.
			String cookie = extractCookies(conn);
			persistCookie(username, cookie);
			
			return new LoginResponse(cookie);
		}
		catch (Exception e) { // TODO: possibly better error handling?
			e.printStackTrace();
			return new LoginResponse(LoginResponseType.UNKNOWN_ERROR)
				.setErrorDetail(e.toString());
		}
		finally {
			// Start following redirects again if that's how things were before we
			// got here.
			HttpURLConnection.setFollowRedirects(follow);
		}
	}
	
	/**
	 * Gets the persistence key for the given username.
	 * 
	 * @param username
	 * @return
	 */
	private static String getCookieKey(String username) {
		return "np_login_cookie_".concat(username);
	}
	
	/**
	 * Persists a cookie in application settings.
	 * 
	 * @param cookie
	 */
	private static void persistCookie(String username, String cookie) {
		HubrisConstants.preferences.put(getCookieKey(username), cookie);
	}
	
	/**
	 * Puts response in a string 
	 * 
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	private static String getResponse(URLConnection conn) throws IOException {
		BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		StringBuilder out = new StringBuilder();
		while ((line = read.readLine()) != null) {
			out.append(line);
			out.append("\n");
		}
		
		return out.toString();
	}
	
	/**
	 * The "Allow" page has a state string in a hidden input field. This method 
	 * extracts it.
	 * 
	 * @param source
	 * @return
	 */
	private static String extractStateString(String source) {
		Matcher matcher = STATE_PATTERN.matcher(source);
		
		if (! matcher.find()) {
			throw new RuntimeException("Expected hidden `state' field in source");
		}
		else {
			return matcher.group(1);
		}
	}
	
	/**
	 * Google sets a cookie called "GALX" that they also expect to be submitted as a
	 * POST parameter... need to extract it
	 * 
	 * @param cookies
	 * @return
	 */
	private static String extractGalxFromCookies(String cookies) {
		Matcher matcher = Pattern.compile("GALX=([^;]+)").matcher(cookies);
		
		if (! matcher.find()) {
			throw new RuntimeException("Expected GALX cookie in: (" + cookies + ")");
		}
		else {
			return matcher.group(1);
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static HttpURLConnection followRedirectionWithCookies(URLConnection conn)
	throws MalformedURLException, IOException {
		if (conn.getHeaderField("Location") == null) {
			throw new IllegalArgumentException("Cannot follow redirection. No `Location' header present.");
		}
		
		String cookies = extractCookies(conn);
		String url     = conn.getHeaderField("Location");
		
		// If this connection sent any cookies, persist them here too.
		if (conn.getRequestProperty("Cookie") != null) {
			cookies = cookies + ";" + conn.getRequestProperty("Cookie");
		}
		
		HttpURLConnection redirConn = (HttpURLConnection)new URL(url).openConnection();
		setRequestProperties(redirConn);
		redirConn.setRequestProperty("Cookie", cookies);
		redirConn.getResponseCode();
		
		return redirConn;
	}
	
	/**
	 * Builds the parameter list to send to Google
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private static String buildPostParams(String username, String password, String galx) {
		return "continue=https%3A%2F%2Fappengine.google.com%2F_ah%2F"
				+"conflogin%3Fcontinue%3Dhttp%3A%2F%2Fnp.ironhelmet.com%2Faccount&"
				+"service=ah&GALX="+galx+"&Email="+username
				+"&Passwd="+password+"&PersistentCookie=yes";
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	private static String buildPostParamsForAllowForm(String state) {
		return "authuser=0&state="+state+"&submit_true=Allow&persist=y";
	}
	
	/**
	 * Extracts all of the Set-Cookie header values and builds the cookie
	 * values.
	 * 
	 * @param conn
	 * @return
	 */
	private static String extractCookies(URLConnection conn) {
		List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
		if (cookies == null) {
			return "";
		}
		
		List<String> parsedCookies = new ArrayList<String>();
		
		for (String s : cookies) {
			parsedCookies.add(StringUtils.join(HttpCookie.parse(s), ";"));
		}
		
		return StringUtils.join(parsedCookies, ";");
	}
	
	/**
	 * Sets default request properties
	 * 
	 * 
	 */
	private static void setRequestProperties(URLConnection conn) {
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.18) Gecko/20110628 Ubuntu/10.10 (maverick) Firefox/3.6.18");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
		conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		conn.setRequestProperty("Keep-Alive", "115");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	}
	
	/**
	 * Sets the POST data for a {@link URLConnection}
	 * 
	 * @param conn
	 * @param data
	 * @throws IOException 
	 */
	private static void setPostData(URLConnection conn, String data) throws IOException {
		conn.setDoOutput(true);
		
		PrintStream out = new PrintStream(conn.getOutputStream());
		out.print(data);
		out.flush();
	}
	
	/**
	 * Types of responses to the login process
	 * 
	 * @author mullins
	 *
	 */
	public static enum LoginResponseType {
		SUCCESS, INVALID_LOGIN, UNKNOWN_ERROR;
	}
	
	/**
	 * Encapsulates login response information
	 * 
	 * @author mullins
	 *
	 */
	public static class LoginResponse {
		private final String cookie;
		private String errorDetail;
		private final LoginResponseType type;

		public LoginResponse(String cookie) {
			this.type = LoginResponseType.SUCCESS;
			this.cookie = cookie;
		}
		
		public LoginResponse(LoginResponseType type) {
			this.type = type;
			this.cookie = null;
		}
		
		@Override
		public String toString() {
			return "LoginResponse [cookie=" + cookie + ", errorDetail="
					+ errorDetail + ", type=" + type + "]";
		}

		public LoginResponseType getResponseType() {
			return type;
		}
		
		public String getCookie() {
			return cookie;
		}
		
		public String getErrorDetail() {
			return errorDetail;
		}
		
		public LoginResponse setErrorDetail(String s) {
			this.errorDetail = s;
			return this;
		}
	}
}

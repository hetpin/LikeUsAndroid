package Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

public class GetData {
	private static DefaultHttpClient client;
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public GetData() {
		System.setProperty("http.proxyHost", "my.proxyhost.com");
		System.setProperty("http.proxyPort", "1234");
	}

	public String getJSONFromUrl(String url) {

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpPost = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		return json;

	}

	public String getJSON(String url, int timeout) {
		try {
			URL u = new URL(url);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setConnectTimeout(timeout);
			c.setReadTimeout(timeout);
			c.connect();
			int status = c.getResponseCode();

			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(
						c.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				return sb.toString();
			}

		} catch (MalformedURLException ex) {
		} catch (IOException ex) {
		}
		return null;
	}

	public static String getInfo(String url, List<NameValuePair> params)
			throws IOException {
		String target = url;
		if (params != null) {
			target += parseParamsToUrl(params);
		}
		HttpGet httpGet = new HttpGet(target);
		// NOTE: This setting for get an json request to a webpage
		httpGet.setHeader("Accept", "application/json");
		httpGet.setHeader("Content-type", "application/json");
		return getResultFromService(httpGet);
	}

	public static String parseParamsToUrl(List<NameValuePair> params) {
		String combinedParams = "";
		if (!params.isEmpty()) {
			combinedParams += "?";
			for (NameValuePair p : params) {
				String paramString = "";
				try {
					paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (combinedParams.length() > 1) {
					combinedParams += "&" + paramString;
				} else {
					combinedParams += paramString;
				}
			}
		}
		return combinedParams;
	}

	public static String postInfo(String url, List<NameValuePair> params,
			HttpEntity httpEntity) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		if (params != null && !params.isEmpty()) {
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (httpEntity != null) {
			httpPost.setEntity(httpEntity);
		}
		return getResultFromService(httpPost);
	}

	public static String postInfo(String url, List<NameValuePair> params,
			HttpEntity httpEntity, int timeout) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		if (params != null && !params.isEmpty()) {
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (httpEntity != null) {
			httpPost.setEntity(httpEntity);
		}
		return getResultFromService(httpPost);
	}

	private static String getResultFromService(HttpRequestBase httpRequestBase)
			throws IOException {
		// Log.e("getResultFromService", "0");
		HttpClient httpclient = getThreadSafeClient();
		// Log.e("getResultFromService", "1");
		HttpResponse response;
		String result = null;
		response = httpclient.execute(httpRequestBase);
		// Log.e("getResultFromService", "2");
		InputStream instream = response.getEntity().getContent();
		// Log.e("getResultFromService", "3");
		result = convertStreamToString(instream);
		// Log.e("getResultFromService", "4");
		instream.close();
		// Log.e("getResultFromService", "5");
		return result;
	}

	private static String convertStreamToString(InputStream instream) {
		// Log.e("convertStreamToString", "convertStreamToString");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				instream));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				// Log.e("convertStreamToString", "line");
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static DefaultHttpClient getThreadSafeClient() {
		if (client != null) {
			return client;
		}
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, LConfig.TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, LConfig.TIMEOUT);
		client = new DefaultHttpClient(httpParams);
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
				mgr.getSchemeRegistry()), params);
		return client;
	}

}

package com.timcircle.keeper.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class HttpUtil {

	public static String sendGet(String urlString) throws Exception {
		URL url = new URL(urlString);
		HttpURLConnection httpClient = null; 
		if (urlString.toLowerCase().startsWith("https://")) {
			httpClient = (HttpsURLConnection) url.openConnection();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			((HttpsURLConnection) httpClient).setSSLSocketFactory(sc.getSocketFactory());
		} else {
			httpClient = (HttpURLConnection) new URL(urlString).openConnection();
		}

		// optional default is GET
		httpClient.setRequestMethod("GET");

		// add request header
		httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");		
		httpClient.setRequestProperty("Accept","*/*");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream(), "UTF-8"))) {

			StringBuilder response = new StringBuilder();
			String line;

			while ((line = in.readLine()) != null) {
				response.append(line);
			}

			int responseCode = httpClient.getResponseCode();
			if (responseCode != 200) {
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
				throw new Exception("[" + responseCode + "]" + responseCode);
			}

			// print result
			return response.toString();
		}
	}

	public static String sendPost(String urlString, String content) throws Exception {
		URL url = new URL(urlString);
		
		HttpURLConnection conn = null; 
		if (urlString.toLowerCase().startsWith("https://")) {
			conn = (HttpsURLConnection) url.openConnection();
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
		} else {
			conn = (HttpURLConnection) new URL(urlString).openConnection();
		}
		
        conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setDoInput(true); //??�許輸入流�?�即??�許下�??
        conn.setDoOutput(true); //??�許輸出流�?�即??�許上傳
        conn.setUseCaches(false); //設置?��?��使用緩�??

        OutputStream os = conn.getOutputStream();
        DataOutputStream wr = new DataOutputStream(os);       
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
        writer.write(content);
        writer.flush();
        writer.close();
        os.close();
        //Get Response
        InputStream is = conn.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        reader.close();
        return response.toString();
	}
}

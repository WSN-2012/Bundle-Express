/*
 * Copyright 2012 KTH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package se.kth.ssvl.tslab.wsn.app.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

public class HTTPRequest {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public HTTPRequest() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public String makeHttpRequest(String url, String method,
			List<NameValuePair> params) throws ClientProtocolException,
			IOException {
		HttpGet httpGet = null;

		// Making HTTP request

		// check for request method
		if (method == "POST") {
			// request method is POST
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} else if (method == "GET") {
			// request method is GET
			Log.d("get: ", "> ");

			DefaultHttpClient httpClient = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			Log.d("WEB", "URL=" + url);
			httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			Log.d("HTTPresponse: ", "> " + is);
		}

		/*
		 * BufferedReader reader = new BufferedReader(new InputStreamReader( is,
		 * "UTF-8"), 8);
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"), 8);

		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");

		}
		is.close();
		json = sb.toString();

		Log.d("BufferData: ", "> " + json);

		// return JSON String
		return json;

	}

}

package com.longau.wallstreetcn.news;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CountryNews extends Thread{
	private static String urlRegex = ".*?\"type\":\"(.*?)\".*?\"contentHtml\":\"<p>(.*?)<\\\\/p>\".*?\"categorySet\":\"(.*?)\".*?";
	public CountryNews(){
	}
	//读取整个页面
	private static String httpRequest(String requestUrl) { // requestUrl
        // 请求地址，返回html字符串
		StringBuffer buffer = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		HttpURLConnection httpUrlConn = null;
		try {
			// 建立get请求
			URL url = new URL(requestUrl);
			httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			// 获取输入流
			inputStream = httpUrlConn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			// 从输入流获取结果
			buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				str = new String(str.getBytes(), "UTF-8");
				buffer.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpUrlConn != null) {
				httpUrlConn.disconnect();
			}
		}
		return buffer.toString();
	}

	public static List<Map> getTodayTemperatureInfo(String DUrl) {
		String html = httpRequest(DUrl);
		List<Map> resultList = htmlFiter(html);
		return resultList;
	}
	// 过滤掉无用的信息    (可修改部分)
	public static List<Map> htmlFiter(String html) {
		List<Map> list = new ArrayList<Map>();
		// 查找目标
		Pattern p = Pattern.compile(urlRegex);
		Matcher m = p.matcher(html);
		while (m.find()) {
			Map<String, String> map_save = new HashMap<String, String>();
			map_save.put("type", m.group(1));
			map_save.put("content", m.group(2));
			map_save.put("categoryset", m.group(3));
			list.add(map_save);
		}
		return list;
	}
}


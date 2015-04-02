package com.wxj.json;

import java.util.Map;

import com.google.gson.Gson;
import com.mongodb.util.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class jsonTest {
	public static void main(String[] args) {
		String path = "http://api.wallstreetcn.com/v2/livenews?limit=60&cid[]=11&callback=jQuery213026424095147464915_1419906064244&page=2&_=1419906064246";
		String JsonContext = new Util().ReadFile("e:\\a.json");
		// String JsonContext = new Util().ReadFile(path);
		JSONArray jsonArray = JSONArray.fromObject(JsonContext);
		int size = jsonArray.size();
		System.out.println("Size: " + size);
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			System.out.println("title=" + jsonObject.get("title"));
			System.out.println("type=" + jsonObject.get("type"));
			System.out.println("categorySet=" + jsonObject.get("categorySet"));
		}
	}

	// public static void main(String[] args) {
	// String str =
	// "{'id':'225084','status':'published','title':'hsha','type':'news'}";
	// Gson gs = new Gson();
	// JSONObject ob = (JSONObject) JSON.parse(str);
	// // System.out.println(ob.get("type"));
	// Map<String, String> map = gs.fromJson(str,Map.class);
	// System.out.println(map.get("type"));
	// JSONObject ob1 = new JSONObject();
	// ob1.put("type",str);
	// }

}

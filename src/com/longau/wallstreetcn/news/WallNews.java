package com.longau.wallstreetcn.news;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.longau.wallstreetcn.bean.CategorySet;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class WallNews {
	public static void main(String[] args) {
		Total total = new Total(1000);
		Spider spider = new Spider(total);
//		Spider spider2 = new Spider(total);
//		Spider spider3 = new Spider(total);
//		Spider spider4 = new Spider(total);
	}

}

class Total {
	private static String url1 = "http://api.wallstreetcn.com/v2/livenews?&page=";
	private String DUrl = "";
	private int i;

	public Total(int i) {
		this.i = i;

	}

	boolean getURL() {
		if (this.i > 0) {
			String DUrl = url1 + i;
			return true;
		} else {
			return false;
		}

	}

}

class Spider extends Thread {
	private static String DataBaseName = "test";
	private static String CollectionName = "wangstreetcn3";

	private static String url1 = "http://api.wallstreetcn.com/v2/livenews?&page=";
	private static String[] ruleList_district = { "9", "10", "11", "12", "13",
			"14", "15", "16", "17" };
	private static String[] ruleList_property = { "1", "2", "3", "4" };
	private static String[] ruleList_centralbank = { "5" };
	private static int NUM = 1000;
	private Total t;

	public Spider(Total total) {
		this.t = total;
		this.start();
	}

	// map值的存放
	public Map<String, String> GetMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "外汇");
		map.put("2", "股市");
		map.put("3", "商品");
		map.put("4", "债市");
		map.put("9", "中国");
		map.put("10", "美国");
		map.put("11", "欧元区");
		map.put("12", "日本");
		map.put("13", "英国");
		map.put("14", "澳洲");
		map.put("15", "加拿大");
		map.put("16", "瑞士");
		map.put("17", "其他地区");
		map.put("5", "央行");

		return map;
	}

	// 对读取的unicode格式的内容转为中文
	public static String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	public void run() {
		// 链接数据库
		try {
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB(DataBaseName);
			DBCollection collection = db.getCollection(CollectionName);
			db.requestStart();
			// 调用抓取的方法获取内容
			for (int ii = 1; ii < NUM; ii++) {
				String DUrl = url1 + ii;
				if(this.t.getURL()){
				List<Map> info = CountryNews.getTodayTemperatureInfo(DUrl);
				System.out.println(DUrl);
				for (Map<String, String> getregex : info) {

					
					BasicDBObject dbObject = new BasicDBObject();
					String content = new String(
							(getregex.get("content")).getBytes(), "UTF-8");
					String content1 = News.unicodeToString(content);

					Date date = new Date();
					DateFormat time = DateFormat.getDateTimeInstance();
					String time_str = time.format(date);

					CategorySet category = new CategorySet();

					String district = category.setDis(
							getregex.get("categoryset"), ruleList_district,
							GetMap());
					String property = category.setDis(
							getregex.get("categoryset"), ruleList_property,
							GetMap());
					String centralbank = category.setDis(
							getregex.get("categoryset"), ruleList_centralbank,
							GetMap());
					String source = "wangstreetcn";
					dbObject.put("content", content1); // 具体内容
					dbObject.put("createdtime", time_str); // 创建时间
					dbObject.put("source", source); // 信息来源
					dbObject.put("district", district); // 所属地区
					dbObject.put("property", property); // 资产类别
					dbObject.put("centralbank", centralbank); // 资产类别
					dbObject.put("type", getregex.get("type")); // 信息类型
					collection.insert(dbObject);
				}
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
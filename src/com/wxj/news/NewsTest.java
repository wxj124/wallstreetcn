package com.wxj.news;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
 /**
  * 
  * @author Administrator
  *
  */
class Num {
	private int total=0;        //抓取页面的开始
	private String url = "http://api.wallstreetcn.com/v2/livenews?&page=";   
	public synchronized String getNum() {
		if (this.total < 100) {    //抓取页数
			String DUrl = this.url + this.total;
			this.total = this.total + 1;
			return DUrl;
		} else {
			return "";
		}
	}
	public Num(int t) {
		this.total = t;
	}
}

class Booth extends Thread {
	private static String DataBaseName = "test";
	private static String CollectionName = "wangstreetcn_id";
	private static String[] ruleList_district = { "9", "10", "11", "12", "13",
			"14", "15", "16", "17" };
	private static String[] ruleList_property = { "1", "2", "3", "4" };
	private static String[] ruleList_centralbank = { "5" };
	private Num n;

	public Booth(Num n) {
		this.n = n;
		this.start();
	}

	public DBCollection getConn() throws UnknownHostException {
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB(DataBaseName);
		DBCollection collection = db.getCollection(CollectionName);
		db.requestStart();
		return collection;
	}

	@Override
	public void run() {
		int base = 0;
		// super.run();
		while (true) {
			try {
                 DBCollection collection = getConn();
				// 调用抓取的方法获取内容
				String DUrl = this.n.getNum();

				if (!DUrl.equals("")) {
					List<Map> info = CountryNews.getTodayTemperatureInfo(DUrl);
					System.out.println(DUrl);

					if (!info.equals("")) {
						for (Map<String, String> getregex : info) {

							BasicDBObject dbObject = new BasicDBObject();
							String cid = getregex.get("cid");     //抓取
							String content = new String(
									(getregex.get("content")).getBytes(),   //抓取
									"UTF-8");
							String content1 = unicodeToString(content);

							Date date = new Date();
							DateFormat time = DateFormat.getDateTimeInstance();
							String time_str = time.format(date);

							CategorySet category = new CategorySet();

							String district = category.setDis(
									getregex.get("categoryset"),     //抓取
									ruleList_district, GetMap());
							String property = category.setDis(
									getregex.get("categoryset"),
									ruleList_property, GetMap());
							String centralbank = category.setDis(
									getregex.get("categoryset"),
									ruleList_centralbank, GetMap());
							String source = "wangstreetcn";
							dbObject.put("content", content1); // 具体内容
							dbObject.put("createdtime", time_str); // 创建时间
							dbObject.put("source", source); // 信息来源
							dbObject.put("district", district); // 所属地区
							dbObject.put("property", property); // 资产类别
							dbObject.put("centralbank", centralbank); // 资产类别
							dbObject.put("type", getregex.get("type")); // 信息类型
							dbObject.put("cid", cid);
							collection.insert(dbObject);
						}
					} else {
						break;
					}
				} else {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	//unicode转中文

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
}

public class NewsTest {
	public static void main(String[] args) {
		// //取最大的10个id
		// DBCursor cursor = collection.find();
		// while(cursor.hasNext()){
		// List<Object> id_sort = new ArrayList<>();
		// id_sort.add(cursor.next().get("cid")); //得到所有的cid
		// // String[] strs = (String[]) id_sort.toArray(new
		// String[id_sort.size()]);
		// // int[] order_int=new int[strs.length];
		// // for(int i=0;i<strs.length;i++){
		// // order_int[i]=Integer.parseInt(strs[i]);
		// // }
		// for (Object object : id_sort) {
		// int a = Integer.parseInt(String.valueOf(object));
		// if(a > base){ //比较取最大
		// base=a;
		// }
		// }
		// }
		Num r = new Num(1);
		Booth b1 = new Booth(r);
		Booth b2 = new Booth(r);
		Booth b3 = new Booth(r);
	}
}
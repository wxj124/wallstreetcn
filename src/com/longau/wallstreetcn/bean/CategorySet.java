package com.longau.wallstreetcn.bean;
import java.util.Map;
public class CategorySet extends Thread{
	public CategorySet() {
	}
	//对x,x,x格式的内容进行分隔筛选
	public String setDis(String categorySet, String[] ruleList, Map<String, String> map) {
		StringBuffer disStr = new StringBuffer(); // 存放结果集
		String[] strArray = null;
		strArray = categorySet.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
		// 获取需要的信息
		int length_strArray = strArray.length;
		int length_ruleList = ruleList.length;
		
		if (length_strArray > 0) {
			for (int iArr = 0; iArr < length_strArray; iArr++) {
				String a = strArray[iArr];
					for (int iRul=0; iRul < length_ruleList; iRul++) {
						if (a.equals(ruleList[iRul])) {
							disStr.append(map.get(a));
							disStr.append(",");
							break;
						}
					}
			}
		}
		if(disStr.length()>1) {
			disStr = disStr.deleteCharAt(disStr.length()-1);
		}
		return disStr.toString();
	}
}

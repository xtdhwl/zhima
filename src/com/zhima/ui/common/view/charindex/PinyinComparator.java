package com.zhima.ui.common.view.charindex;

import java.util.Comparator;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.zhima.data.model.ContactEntry;

/**
 * 拼音工具类
 * @ClassName: PinyinComparator
 * @Description: TODO
 * @author yusonglin
 * @date 2012-12-29 下午4:48:10
*/
public class PinyinComparator implements Comparator{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		ContactEntry c1 = (ContactEntry) o1;
		ContactEntry c2 = (ContactEntry) o2;
		 String str1 = PingYinUtil.getPingYin(c1.getTitle());
	     String str2 = PingYinUtil.getPingYin(c2.getTitle());
	     return str1.compareTo(str2);
	}

	/**  
     * 汉字转换位汉语拼音首字母，英文字符不变  
     * @param chines 汉字  
     * @return 拼音  
     */     
    public static String converterToFirstSpell(String chines){   
    	
         String pinyinName = "";      
        try {
			char[] nameChar = chines.toCharArray();      
			 HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
			 defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
			 defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
			for (int i = 0; i < nameChar.length; i++) {      
			    if (nameChar[i] > 128) {      
			        try {      
			             pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
			         } catch (BadHanyuPinyinOutputFormatCombination e) {      
			             e.printStackTrace();      
			         }      
			     }else{      
			         pinyinName += nameChar[i];      
			     }      
			 }
		} catch (Exception e) {
			e.printStackTrace();
			return pinyinName;
		}      
        return pinyinName;      
     }      
}

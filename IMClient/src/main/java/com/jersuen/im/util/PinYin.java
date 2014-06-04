package com.jersuen.im.util;


import java.util.ArrayList;
import java.util.Locale;

/**
 * 汉字转拼音
 * @author JerSuen
 */
public class PinYin {
	//汉字返回拼音，字母原样返回，都转换为小写
	public static String getPinYin(String input) {
		ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (HanziToPinyin.Token token : tokens) {
				if (HanziToPinyin.Token.PINYIN == token.type) {
					sb.append(token.target);
				} else {
					sb.append(token.source);
				}
			}
		}
		return sb.toString().toLowerCase(Locale.CHINESE);
	}
}

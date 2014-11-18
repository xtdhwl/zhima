package com.zhima.ui.scancode.result;

/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

/**
 * Memory格式名片解析
 * @ClassName: ZhimaMemoryParser
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-8 下午3:16:57
*/
public final class ZhimaMemoryParser extends ResultParser {

	private static final String TAG = "ZhimaMeCardParser";
	private static final String BYTE_ORDER_MARK = "\ufeff";


	public ZhimaMemoryParser() {
		
	}

	public static String getMassagedText(Result result) {
		return result.getText();
	}
	
	public static boolean isMemory(Result result){
		String rawText = getMassagedText(result);
		if (!rawText.contains("MEMORY") || !rawText.contains("\r\n")) {
			return false;
		}
		return true;
	}

	@Override
	public com.zhima.ui.scancode.result.AddressBookParsedResult parse(Result result) {
		String rawText = getMassagedText(result);
//		// MEMORY is mandatory; seems like a decent indicator, as does
//		// end-of-record separator CR/LF
//		if (!rawText.contains("MEMORY") || !rawText.contains("\r\n")) {
//			return null;
//		}

		if(isMemory(result)){
			return null;
		}
		
		// NAME1 and NAME2 have specific uses, namely written name and
		// pronunciation, respectively.
		// Therefore we treat them specially instead of as an array of names.
		String name = matchSinglePrefixedField("NAME1:", rawText, '\r', true);
		String pronunciation = matchSinglePrefixedField("NAME2:", rawText,
				'\r', true);

		String[] phoneNumbers = matchMultipleValuePrefix("TEL", 3, rawText,
				true);
		String[] emails = matchMultipleValuePrefix("MAIL", 3, rawText, true);
		String note = matchSinglePrefixedField("MEMORY:", rawText, '\r', false);
		String address = matchSinglePrefixedField("ADD:", rawText, '\r', true);
		String[] addresses = address == null ? null : new String[] { address };
		return new com.zhima.ui.scancode.result.AddressBookParsedResult(maybeWrap(name), pronunciation,
				phoneNumbers, null, emails, null, null, note, addresses, null,
				null, null, null, null,null,null);
	}

	private static String[] matchMultipleValuePrefix(String prefix, int max,
			String rawText, boolean trim) {
		List<String> values = null;
		for (int i = 1; i <= max; i++) {
			String value = matchSinglePrefixedField(prefix + i + ':', rawText,
					'\r', trim);
			if (value == null) {
				break;
			}
			if (values == null) {
				values = new ArrayList<String>(max); // lazy init
			}
			values.add(value);
		}
		if (values == null) {
			return null;
		}
		return values.toArray(new String[values.size()]);
	}

	static String[] matchPrefixedField(String prefix, String rawText,
			char endChar, boolean trim) {
		List<String> matches = null;
		int i = 0;
		int max = rawText.length();
		while (i < max) {
			i = rawText.indexOf(prefix, i);
			if (i < 0) {
				break;
			}
			i += prefix.length(); // Skip past this prefix we found to start
			int start = i; // Found the start of a match here
			boolean more = true;
			while (more) {
				i = rawText.indexOf((int) endChar, i);
				if (i < 0) {
					// No terminating end character? uh, done. Set i such that
					// loop terminates and break
					i = rawText.length();
					more = false;
				} else if (rawText.charAt(i - 1) == '\\') {
					// semicolon was escaped so continue
					i++;
				} else {
					// found a match
					if (matches == null) {
						matches = new ArrayList<String>(3); // lazy init
					}
					String element = unescapeBackslash(rawText.substring(start,
							i));
					if (trim) {
						element = element.trim();
					}
					matches.add(element);
					i++;
					more = false;
				}
			}
		}
		if (matches == null || matches.isEmpty()) {
			return null;
		}
		return matches.toArray(new String[matches.size()]);
	}

	static String matchSinglePrefixedField(String prefix, String rawText,
			char endChar, boolean trim) {
		String[] matches = matchPrefixedField(prefix, rawText, endChar, trim);
		return matches == null ? null : matches[0];
	}

}

package com.zhima.ui.scancode.result;

/*
 * Copyright 2007 ZXing authors
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
 * Implements the "MECARD" address book entry format.
 * 
 * Supported keys: N, SOUND, TEL, EMAIL, NOTE, ADR, BDAY, URL, plus ORG
 * Unsupported keys: TEL-AV, NICKNAME
 * 
 * Except for TEL, multiple values for keys are also not supported; the first
 * one found takes precedence.
 * 
 * Our understanding of the MECARD format is based on this document:
 * 
 * http://www.mobicode.org.tw/files/OMIA%20Mobile%20Bar%20Code%20Standard%20v3.2
 * .1.doc
 * 
 * @author Sean Owen
 */
public class ZhimaMeCardParser extends ResultParser {

	private static final String TAG = "ZhimaMeCardParser";
	private static final String BYTE_ORDER_MARK = "\ufeff";
	private static String characterSet;



	public ZhimaMeCardParser() {
	}

	public static String getMassagedText(Result result) {
		return result.getText();
	}
	
	public static boolean isMeCard(Result result){
		String rawText = getMassagedText(result);
		if (!rawText.startsWith("MECARD:")) {
			return false;
		}
		return true;
	}

	@Override
	public com.zhima.ui.scancode.result.AddressBookParsedResult parse(Result result) {
		String rawText = getMassagedText(result);
		if(!isMeCard(result)){
			return null;
		}
		String[] rawName = matchDoCoMoPrefixedField("N:", rawText, true);
		if (rawName == null) {
			return null;
		}
		String name = parseName(rawName[0]);
		String pronunciation = matchSingleDoCoMoPrefixedField("SOUND:",
				rawText, true);
		String[] phoneNumbers = matchDoCoMoPrefixedField("TEL:", rawText, true);
		String[] emails = matchDoCoMoPrefixedField("EMAIL:", rawText, true);
		String note = matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
		String[] addresses = matchDoCoMoPrefixedField("ADR:", rawText, true);
		String birthday = matchSingleDoCoMoPrefixedField("BDAY:", rawText, true);
		if (birthday != null && !isStringOfDigits(birthday, 8)) {
			// No reason to throw out the whole card because the birthday is
			// formatted wrong.
			birthday = null;
		}
		String url = matchSingleDoCoMoPrefixedField("URL:", rawText, true);

		// Although ORG may not be strictly legal in MECARD, it does exist in
		// VCARD and we might as well
		// honor it when found in the wild.
		String org = matchSingleDoCoMoPrefixedField("ORG:", rawText, true);
		String title = matchSingleDoCoMoPrefixedField("TIL:",rawText,true);

		return new com.zhima.ui.scancode.result.AddressBookParsedResult(maybeWrap(name), pronunciation,
				phoneNumbers, null, emails, null, null, note, addresses, null,
				org, birthday, title, url,null,null);
	}

	private static String parseName(String name) {
		int comma = name.indexOf((int) ',');
		if (comma >= 0) {
			// Format may be last,first; switch it around
			return name.substring(comma + 1) + ' ' + name.substring(0, comma);
		}
		return name;
	}

	static String[] matchDoCoMoPrefixedField(String prefix, String rawText,
			boolean trim) {
		return matchPrefixedField(prefix, rawText, ';', trim);
	}

	static String matchSingleDoCoMoPrefixedField(String prefix, String rawText,
			boolean trim) {
		return matchSinglePrefixedField(prefix, rawText, ';', trim);
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

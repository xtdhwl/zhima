/*
 * Copyright (C) 2011 ZXing authors
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

package com.zhima.ui.scancode.encode;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.telephony.PhoneNumberUtils;

/**
 * Encodes contact information according to the vCard format.
 *
 * @author Sean Owen
 */
public final class VCardContactEncoder extends ContactEncoder {

  private static final Pattern RESERVED_VCARD_CHARS = Pattern.compile("([\\\\,;])");
  private static final Pattern NEWLINE = Pattern.compile("\\n");
  private static final Formatter VCARD_FIELD_FORMATTER = new Formatter() {
    @Override
    public String format(String source) {
      return NEWLINE.matcher(RESERVED_VCARD_CHARS.matcher(source).replaceAll("\\\\$1")).replaceAll("");
    }
  };
  private static final char TERMINATOR = '\n';
  
  /**
 * @Title: encode
 * @Description: TODO 添加元素
 * @param name
 * @param organization
 * @param addresse
 * @param phone
 * @param email
 * @param url
 * @param note
 * @param title
 * @param element1
 * @param element2
 * @return
 */
public String encode(String name,
                       String organization,
                       String addresse,
                       String phone,
                       String email,
                       String url,
                       String note,
                       String title,
                       String element1,
                       String element2){
	  
	  ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> addresses = new ArrayList<String>();
		ArrayList<String> phones = new ArrayList<String>();
		ArrayList<String> emails = new ArrayList<String>();
		names.add(name);
		addresses.add(addresse);
		phones.add(phone);
		emails.add(email);
	  
	  return encode(names, organization, addresses, phones, emails, url, note, title, element1, element2)[0];
	  
  }

  /**
   * names 姓名
   * organization 公司名称
   * addresses 地址
   * phones 电话
   * emails 邮箱
   * url 网址
   * note 其他
   */
  @Override
  public String[] encode(Iterable<String> names,
                         String organization,
                         Iterable<String> addresses,
                         Iterable<String> phones,
                         Iterable<String> emails,
                         String url,
                         String note,
                         String title,
                         String element1,
                         String element2) {
    StringBuilder newContents = new StringBuilder(100);
    StringBuilder newDisplayContents = new StringBuilder(100);
    newContents.append("BEGIN:VCARD").append(TERMINATOR);
    appendUpToUnique(newContents, newDisplayContents, "N", names, 1, null);
    append(newContents, newDisplayContents, "ORG", organization);
    appendUpToUnique(newContents, newDisplayContents, "ADR", addresses, 1, null);
    appendUpToUnique(newContents, newDisplayContents, "TEL", phones, Integer.MAX_VALUE, new Formatter() {
      @Override
      public String format(String source) {
        return PhoneNumberUtils.formatNumber(source);
      }
    });
    appendUpToUnique(newContents, newDisplayContents, "EMAIL", emails, Integer.MAX_VALUE, null);
    append(newContents, newDisplayContents, "URL", url);
    append(newContents, newDisplayContents, "NOTE", note);
    append(newContents, newDisplayContents, "TITLE", title);
    append(newContents, newDisplayContents, "ZM_ELEMENT1", element1);
    append(newContents, newDisplayContents, "ZM_ELEMENT2", element2);
    newContents.append("END:VCARD").append(TERMINATOR);
    return new String[] { newContents.toString(), newDisplayContents.toString() };
  }
  
  private static void append(StringBuilder newContents, 
                             StringBuilder newDisplayContents,
                             String prefix, 
                             String value) {
    doAppend(newContents, newDisplayContents, prefix, value, VCARD_FIELD_FORMATTER, TERMINATOR);
  }
  
  private static void appendUpToUnique(StringBuilder newContents, 
                                       StringBuilder newDisplayContents,
                                       String prefix, 
                                       Iterable<String> values, 
                                       int max,
                                       Formatter formatter) {
    doAppendUpToUnique(newContents,
                       newDisplayContents,
                       prefix,
                       values,
                       max,
                       formatter,
                       VCARD_FIELD_FORMATTER,
                       TERMINATOR);
  }

}

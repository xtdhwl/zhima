package com.zhima.ui.scancode.result;

import com.google.zxing.Result;

public class VCardHandler {

	/**
	 * 扫描编码
	 */
//	private String characterSet;
	private Result result;

//	public String getCharacterSet() {
//		return characterSet;
//	}
//
//	public void setCharacterSet(String characterSet) {
//		this.characterSet = characterSet;
//	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	/**
	* @Title: isVCardResult
	* @Description: Result是否为VCard or MeVCard
	* @param result
	* @return 是返回true，否则返回false
	 */
	public static boolean isVCardResult(Result result) {
//		ParsedResult parserResult = null;
//		//vcard
//		VCardResultParser vcardParser = new VCardResultParser();
//		parserResult = vcardParser.parse(result);
//		
//		if(parserResult == null){
//			//meVcard
//			AddressBookDoCoMoResultParser meVcardParser = new AddressBookDoCoMoResultParser();
//			parserResult = meVcardParser.parse(result);
//		}
//		//vcard 或 mevcard不为空认为是vcard
//		if(parserResult != null){
//			return true;
//		}
		//meCard
		if(ZhimaMeCardParser.isMeCard(result)){
			return true;
		}
		//vcard
		if(ZhimaVcardParser.isVCard(result)){
			return true;
		}
		//card
		if(ZhimaCardParser.isCard(result)){
			return true;
		}
		return false;
	}

	////-----------------------------------------
	//	//序列化
	//	@Override
	//	public int describeContents() {
	//		return 0;
	//	}
	//
	//	@Override
	//	public void writeToParcel(Parcel out, int flags) {
	//		out.writeString(characterSet);
	//		out.writeValue(resultList);
	//	}
	//
	//	Parcelable.Creator<VCardHandler> CREATOR = new Parcelable.Creator<VCardHandler>() {
	//		@Override
	//		public VCardHandler createFromParcel(Parcel source) {
	//			return new VCardHandler(source);
	//		}
	//
	//		@Override
	//		public VCardHandler[] newArray(int size) {
	//			return new VCardHandler[size];
	//		}
	//	};
	//
	//	private VCardHandler(Parcel in) {
	//		characterSet = in.readString();
	//		resultList = new ArrayList<Result>();
	//		in.readList(resultList, null);
	//	}
}

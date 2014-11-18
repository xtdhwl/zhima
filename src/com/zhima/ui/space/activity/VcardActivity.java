package com.zhima.ui.space.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.scancode.result.AddressBookParsedResult;
import com.zhima.ui.scancode.result.VCardHandler;
import com.zhima.ui.scancode.result.VCardStore;
import com.zhima.ui.scancode.result.ZhimaCardParser;
import com.zhima.ui.scancode.result.ZhimaMeCardParser;
import com.zhima.ui.scancode.result.ZhimaMemoryParser;
import com.zhima.ui.scancode.result.ZhimaVcardParser;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 扫码结果__名片
 * @ClassName: VcardActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-8 上午11:41:58
 */
public class VcardActivity extends BaseActivity {

	private static final String TAG = "VcardActivity";

	private View mChildrenView;

	public static final String ACTIVITY_EXTRA_VCARD = "activity_extra_vcard";
	private VCardHandler mVCardHandler;
	private VCardStore mVCardStroe;
	private AddressBookParsedResult mAddressBook;

	private TextView mNameText;
	private TextView mPhoneText;
	private TextView mCompanyText;
	private TextView mPositionText;
	private TextView mEmailText;
	private TextView mAddressText;
	private TextView mElement1Text;
	private TextView mElement2Text;

	private OnClickListener mOnclickListener;

	private String mName;
	private String mPhone;
	private String mPosition;
	private String mEmail;
	private String mAddress;
	private String mElement1;
	private String mElement2;

//	private String characterSet;
	private String mCompany;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		setSideBar();
		setTopbar();
		findView();
		setUpView();

	}

	private void initData() {

		Intent intent = getIntent();
		mVCardHandler = (VCardHandler) intent.getSerializableExtra(ACTIVITY_EXTRA_VCARD);
		mVCardStroe = VCardStore.getInstance();
		mVCardHandler = mVCardStroe.getVCardHandler();

//		characterSet ="" /*mVCardHandler.getCharacterSet()*/;

		Result result = mVCardHandler.getResult();
		ZhimaVcardParser vCardResultParser = new ZhimaVcardParser();
		String text = vCardResultParser.getMassagedText(result);
		if (ZhimaVcardParser.isVCard(result)) {
			mAddressBook = vCardResultParser.parse(result);
		} else if (ZhimaCardParser.isCard(result)) {
			ZhimaCardParser zhimaCardParser = new ZhimaCardParser();
			mAddressBook = zhimaCardParser.parse(result);
		} else if (ZhimaMeCardParser.isMeCard(result)) {
			ZhimaMeCardParser zhimaMeCardParser = new ZhimaMeCardParser();
			mAddressBook = zhimaMeCardParser.parse(result);
		} else if (ZhimaMemoryParser.isMemory(result)) {
			ZhimaMemoryParser zhimaMemoryParser = new ZhimaMemoryParser();
			mAddressBook = zhimaMemoryParser.parse(result);
		}

		mOnclickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		};
	}

	private void setSideBar() {
		mChildrenView = View.inflate(this, R.layout.space_vcard_activity, null);

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "名片", View.GONE, null);

//		mSideBarView = new SideBarView(this, this, "名片");
//		mSideBarView.setChildView(mChildrenView);
//		setContentView(mSideBarView.getContentView());
//
//		final View tranView = (View) mSideBarView.getContentView().findViewById(R.id.view_transparent);
//		tranView.setVisibility(View.GONE);
//		tranView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mSideBarView.scrollView();
//			}
//		});
//		tranView.setClickable(false);
//		mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {
//
//			@Override
//			public void onStateChange(boolean isMenuOut) {
//				if (isMenuOut) {
//					tranView.setVisibility(View.VISIBLE);
//					tranView.setClickable(true);
//				} else {
//					tranView.setVisibility(View.GONE);
//					tranView.setClickable(false);
//				}
//			}
//		});
	}

	private void setTopbar() {
		ZhimaTopbar topBar = getTopbar();
		View rightView = View.inflate(this, R.layout.topbar_rightview, null);

		RelativeLayout buttonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		buttonLayout1.setVisibility(View.VISIBLE);
		buttonLayout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				HaloToast.show(getApplicationContext(), "保存至通讯录");
//				try {
//					insertToAndroid("余松霖", "15321782508");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}

				Intent intent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
						Uri.parse("content://com.android.contacts"), "contacts"));
				intent.putExtra(Intents.Insert.NAME, mName);
				intent.putExtra(Intents.Insert.PHONE, mPhone);
				intent.putExtra(Intents.Insert.JOB_TITLE, mPosition);
				intent.putExtra(Intents.Insert.EMAIL, mEmail);
				intent.putExtra(Intents.Insert.COMPANY, mCompany);
				intent.putExtra(Intents.Insert.POSTAL, mAddress);
				startActivity(intent);
			}
		});

		ImageView button1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		button1.setImageResource(R.drawable.topbar_save);

		topBar.setRightLayoutVisible(true);
		topBar.addRightLayoutView(rightView);

	}

	private void findView() {
		mNameText = (TextView) mChildrenView.findViewById(R.id.space_vcard_name);
		mPhoneText = (TextView) mChildrenView.findViewById(R.id.space_vcard_phone);
		mCompanyText = (TextView) mChildrenView.findViewById(R.id.space_vcard_company);
		mPositionText = (TextView) mChildrenView.findViewById(R.id.space_vcard_position);
		mEmailText = (TextView) mChildrenView.findViewById(R.id.space_vcard_email);
		mAddressText = (TextView) mChildrenView.findViewById(R.id.space_vcard_address);
		mElement1Text = (TextView) mChildrenView.findViewById(R.id.space_vcard_element1);
		mElement2Text = (TextView) mChildrenView.findViewById(R.id.space_vcard_element2);
	}

	private void setUpView() {
		if (mAddressBook != null) {
			mName = mAddressBook.getNames() != null ? (mAddressBook.getNames().length > 0 ? !TextUtils
					.isEmpty(mAddressBook.getNames()[0]) ? mAddressBook.getNames()[0] : "空" : "空") : "空";
			mPhone = mAddressBook.getPhoneNumbers() != null ? (mAddressBook.getPhoneNumbers().length > 0 ? !TextUtils
					.isEmpty(mAddressBook.getPhoneNumbers()[0]) ? mAddressBook.getPhoneNumbers()[0] : "空" : "空") : "空";
			mCompany = TextUtils.isEmpty(mAddressBook.getOrg()) ? "空" : mAddressBook.getOrg();
			mPosition = TextUtils.isEmpty(mAddressBook.getTitle()) ? "空" : mAddressBook.getTitle();
			mEmail = mAddressBook.getEmails() != null ? (mAddressBook.getEmails().length > 0 ? !TextUtils
					.isEmpty(mAddressBook.getEmails()[0]) ? mAddressBook.getEmails()[0] : "空" : "空") : "空";
			mAddress = mAddressBook.getAddresses() != null ? (mAddressBook.getAddresses().length > 0 ? !TextUtils
					.isEmpty(mAddressBook.getAddresses()[0]) ? mAddressBook.getAddresses()[0] : "空" : "空") : "空";
			mElement1 = TextUtils.isEmpty(mAddressBook.getElement1()) ? "空" : mAddressBook.getElement1();
			mElement2 = TextUtils.isEmpty(mAddressBook.getElement2()) ? "空" : mAddressBook.getElement2();

			mNameText.setText(mName);
			mPhoneText.setText(mPhone);
			mCompanyText.setText(mCompany);
			mPositionText.setText(mPosition);
			mEmailText.setText(mEmail);
			mAddressText.setText(mAddress);
			mElement1Text.setText(mElement1);
			mElement2Text.setText(mElement2);

			mNameText.setOnClickListener(mOnclickListener);
			mPhoneText.setOnClickListener(mOnclickListener);
			mCompanyText.setOnClickListener(mOnclickListener);
			mPositionText.setOnClickListener(mOnclickListener);
			mEmailText.setOnClickListener(mOnclickListener);
			mAddressText.setOnClickListener(mOnclickListener);
			mElement1Text.setOnClickListener(mOnclickListener);
			mElement2Text.setOnClickListener(mOnclickListener);

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mVCardStroe = null;
	}

	private long insertToAndroid(String name, String tel) throws Exception {

		ContentValues values = new ContentValues();
		Uri rawContactUri = this.getBaseContext().getContentResolver().insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.GIVEN_NAME, name);
		this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, tel);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		values.put(Email.DATA, "nathan.wu@hisoft.com");
		values.put(Email.TYPE, Email.TYPE_WORK);
		this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		values.clear();

		return rawContactId;
	}

}

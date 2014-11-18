package com.zhima.ui.contact.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.zhima.R;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.service.ContactService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnLeftBtClickListener;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;

public class ContactActivity extends BaseActivity {

	private static final CharSequence CONTACT_TITLE = "通讯录";
	private static final CharSequence SEARCH_TEXT = "搜索通讯录";
	protected static final String TAG = "ContactActivity";

	/** 搜索框布局 */
	private RelativeLayout mEditLayout;
	/** 右边搜索按钮 */
	private ImageView rightButton1;
	/** 页面标题 */
	private TextView titleText;
	/** 搜索框 */
	private EditText mRetrievalEdit;
	/** 提示布局 */
	private LinearLayout mEditHintLayout;
	/** 删除关键字按钮 */
	private ImageView mRemoveImage;
	/** 搜索关键字 popup布局view */
	private LinearLayout mRetrievalKeyLayout;

	/** 搜索框下边搜索view */
	private PopupWindow mRetrievalPop;
	private TextView mRetrievalKeyText;

	private ZhimaTopbar mTopbar;

	private ContactService mContactService;
	private ListView mContactView;
	private ArrayList<ContactEntry> mContactList;
	private ContactAdatper mContactAdatper;

	/** 搜索关键字 */
	protected String mSearchText = "";
	/**搜索标志位*/
	private boolean isSearchFlag = false;
	private View mChildrenView;
	private SideBarView mSideBarView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChildrenView = View.inflate(this, R.layout.contact_activity, null);
		setSideBar();
		setTopbar();
		init();
		findView();
		getContactData();

	}

	private void init() {
		mContactService = ContactService.getInstance(this);
	}

	private void getContactData() {
		mContactList = mContactService.getContactList();
		setContactView();
	}

	//返回检查
	private void setSideBar() {
		mSideBarView = new SideBarView(this, this, "通讯录");
		mSideBarView.setChildView(mChildrenView);
		mSideBarView.setOnLeftBtClickListener(new OnLeftBtClickListener() {
			@Override
			public boolean onLeftBtClick() {
				if (mEditLayout.getVisibility() == View.VISIBLE) {
					isSearchFlag = false;
					hideSearch();
					getContactData();
					return true;
				}
				return false;
			}
		});
		setContentView(mSideBarView.getContentView());
	}

	/**更新联系人*/
	private void setContactView() {
		if (mContactAdatper == null) {
			mContactAdatper = new ContactAdatper(this, R.layout.contact_item, mContactList);
			mContactView.setAdapter(mContactAdatper);
			mContactView.setOnItemClickListener(openContactClick);
			mContactView.setOnItemLongClickListener(deteleContactClick);
			mContactAdatper.setOnClickerListener(callPhoneClick);
		} else {
			mContactAdatper.setData(mContactList);
			mContactAdatper.notifyDataSetChanged();
		}
		if (mContactAdatper.getCount() < 1) {
			showContactEmpty(true);
		} else {
			showContactEmpty(false);
		}
	}

	/**删除联系人*/
	private OnItemLongClickListener deteleContactClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			MessageDialog dialog = new MessageDialog(ContactActivity.this, mContactView);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.show();
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					ContactEntry entry = mContactAdatper.getItem(position);
					mContactService.deleteContactById(entry.getId(), false, ContactActivity.this);
					getContactData();
				}

				@Override
				public void onLeftBtClick() {
				}
			});
			return false;
		}
	};

	private OnItemClickListener openContactClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ContactEntry entry = (ContactEntry) parent.getAdapter().getItem(position);
			ActivitySwitcher.openSpaceActivity(ContactActivity.this, entry.getObjectId(), entry.getObjectType(),null,false);
		}

	};

	/**搜索方法 */
	private void getSearchDataRefresh() {
		mContactList = mContactService.searchContactList(mSearchText);
		isSearchFlag = true;
		//搜索结果为0
		setContactView();
		if (mContactList.size() == 0) {
			showSearchEmpty(true);
		} else {
			showSearchEmpty(false);
		}
	}

	/**联系点击事件*/
	private View.OnClickListener callPhoneClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ContactEntry entry = (ContactEntry) view.getTag();
			switch (view.getId()) {
			case R.id.img_phone:
				Uri uri = Uri.parse("tel:" + entry.getTelephone());
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
				break;
			default:
			}
		}
	};

	/**搜索框编辑监听*/
	private TextWatcher searchEditWatcher = new TextWatcher() {

		private String mRetrievalText;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			mRetrievalText = mRetrievalEdit.getText().toString().trim();

			if (mRetrievalText == null || "".equals(mRetrievalText)) {
				mEditHintLayout.setVisibility(View.VISIBLE);
				mRemoveImage.setVisibility(View.GONE);
				dismissPop();
			} else {
				mEditHintLayout.setVisibility(View.GONE);
				mRemoveImage.setVisibility(View.VISIBLE);
				mRetrievalKeyText.setText("搜索   \"" + mRetrievalText + "\"");
				mRetrievalPop.setFocusable(false);
				showPopwindow();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	/**监听输入框确定按钮*/
	private OnEditorActionListener searchEdit = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				mSearchText = mRetrievalEdit.getText().toString();
				hideSearch();
				getSearchDataRefresh();
			}
			return false;
		}

	};

	/**清空输入框*/
	private View.OnClickListener removeSearchClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mRetrievalEdit.setText("");
		}
	};

	/**显示搜索框*/
	private View.OnClickListener showSearchClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mEditLayout.setVisibility(View.VISIBLE);
			titleText.setVisibility(View.GONE);
			rightButton1.setVisibility(View.GONE);
			mTopbar.setRightLayoutVisible(false);
			mRetrievalEdit.setFocusable(true);
			mRetrievalEdit.setText("");
			ImeHelper.showIME(mRetrievalEdit);
		}
	};

	/**开始搜索 */
	private View.OnClickListener sendSearchClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mSearchText = mRetrievalEdit.getText().toString();
			hideSearch();
			getSearchDataRefresh();
		}
	};

	/**隐藏Pop输入框,初始化搜索框*/
	private void hideSearch() {
		dismissPop();
		mRetrievalEdit.setText("");
		mEditLayout.setVisibility(View.GONE);
		titleText.setVisibility(View.VISIBLE);
		rightButton1.setVisibility(View.VISIBLE);
		mTopbar.setRightLayoutVisible(true);
		//隐藏输入法
		ImeHelper.hideIME(mRetrievalEdit);
	}

	private void dismissPop() {
		if (mRetrievalPop != null && mRetrievalPop.isShowing()) {
			mRetrievalPop.dismiss();
		}
	}

	private void showPopwindow() {
		if (mRetrievalPop != null && !mRetrievalPop.isShowing()) {
			mRetrievalPop.showAsDropDown(mTopbar);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mSideBarView.onKeyBack()) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	//如果现在为搜索状态先回到未搜索状态
	@Override
	public void onBackPressed() {
		if (isSearchFlag) { //如果搜索过
			isSearchFlag = false;
			getContactData();
		} else {
			if (mRetrievalPop != null && mRetrievalPop.isShowing()) {
				hideSearch(); //如果Pop在显示中
			} else {
				//TODO 焦点.优化判断输入法弹出 
				if (mRetrievalEdit.isFocused()) {
					hideSearch();
				} else {
					super.onBackPressed();
				}
			}
		}
	}

	private void findView() {
		mContactView = (ListView) mChildrenView.findViewById(R.id.lsvt_contact);
	}

	private void setTopbar() {

		View view = View.inflate(this, R.layout.retrieval_popupwindow_view, null);
		mRetrievalPop = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
		mRetrievalPop = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
		mRetrievalPop.setBackgroundDrawable(new ColorDrawable(0x00000000));
		mRetrievalPop.setAnimationStyle(R.style.customDialog_anim_style);
		mRetrievalPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		mRetrievalPop.setOutsideTouchable(false);

		mRetrievalKeyLayout = (LinearLayout) view.findViewById(R.id.layout_retrieval_key);
		mRetrievalKeyText = (TextView) view.findViewById(R.id.txt_retrieval_key);
		mRetrievalKeyLayout.setOnClickListener(sendSearchClick);

		mTopbar = getTopbar();
		mTopbar.setLeftLayoutVisible(true);
		mTopbar.setRightLayoutVisible(true);

		View leftView = null;
		if (mSideBarView != null) {
			leftView = mSideBarView.getLeftView();
		} else {
			leftView = View.inflate(this, R.layout.topbar_leftview, null);
		}
		View rightView = View.inflate(this, R.layout.topbar_rightview, null);

		//		mTopbar.addLeftLayoutView(leftView);
		mTopbar.addRightLayoutView(rightView);

		mEditLayout = (RelativeLayout) leftView.findViewById(R.id.layout_retrieval_edit_box);
		titleText = (TextView) leftView.findViewById(R.id.txt_topbar_title);
		titleText.setText(CONTACT_TITLE);
		mEditHintLayout = (LinearLayout) leftView.findViewById(R.id.layout_retrieval_edit_hint);
		mRetrievalEdit = (EditText) leftView.findViewById(R.id.edt_retrieval);
		mRemoveImage = (ImageView) leftView.findViewById(R.id.img_retrieval_remove);
		TextView hint = (TextView) leftView.findViewById(R.id.txt_hint);
		hint.setText(SEARCH_TEXT);

		mRetrievalEdit.addTextChangedListener(searchEditWatcher);
		mRetrievalEdit.setOnEditorActionListener(searchEdit);

		mRemoveImage.setOnClickListener(removeSearchClick);

//		mRetrievalEdit.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);

		//
		rightButton1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		rightButton1.setVisibility(View.VISIBLE);
		rightButton1.setImageResource(R.drawable.search_icon);

		RelativeLayout rightButtonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		rightButtonLayout1.setVisibility(View.VISIBLE);
		rightButtonLayout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditLayout.setVisibility(View.VISIBLE);
				titleText.setVisibility(View.GONE);
				rightButton1.setVisibility(View.GONE);
				mTopbar.setRightLayoutVisible(false);
				mRetrievalEdit.setFocusable(true);
				ImeHelper.showIME(mRetrievalEdit);
				mRetrievalEdit.setText("");
			}
		});

		////------------------------------------------------------
		final View tranView = (View) mSideBarView.getContentView().findViewById(R.id.view_transparent);
		tranView.setVisibility(View.GONE);
		tranView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSideBarView.scrollView();
			}
		});
		tranView.setClickable(false);
		mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {

			@Override
			public void onStateChange(boolean isMenuOut) {
				if (isMenuOut) {
					tranView.setVisibility(View.VISIBLE);
					tranView.setClickable(true);
				} else {
					tranView.setVisibility(View.GONE);
					tranView.setClickable(false);
				}
			}
		});
		//----------------------------------------------------------------

	}

	private void showSearchEmpty(boolean bl) {
		TextView txt = (TextView) mChildrenView.findViewById(R.id.txt_empty);
		txt.setText("未搜索到结果");
		txt.setVisibility(bl ? View.VISIBLE : View.GONE);
		ImageView iv = (ImageView) findViewById(R.id.img_search);
		iv.setVisibility(bl ? View.VISIBLE : View.GONE);

		mContactView.setVisibility(bl ? View.GONE : View.VISIBLE);
	}

	private void showContactEmpty(boolean bl) {
		TextView txt = (TextView) mChildrenView.findViewById(R.id.txt_empty);
		txt.setText("通讯录没有内容");
		txt.setVisibility(bl ? View.VISIBLE : View.GONE);
		ImageView iv = (ImageView) mChildrenView.findViewById(R.id.img_search);
		iv.setVisibility(View.GONE);

		mContactView.setVisibility(bl ? View.GONE : View.VISIBLE);
	}
}

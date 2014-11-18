package com.zhima.ui.usercenter.watchdog.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.PersonRecordType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.InboxProtocolHandler.GetMessageCountProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.InboxProtocolHandler.GetFriendTalkListByUserProtocol;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.InboxService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.CustomDialog.OnOnlyBtClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaDatePickerDialog;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName WatchdogMainActivity
 * @Description 个人管家主页面
 * @author jiang
 * @date 2013-1-22 下午02:36:35
 */
/**
 * @ClassName WatchdogMainActivity
 * @Description TODO
 * @author jiang
 * @date 2013-2-2 下午11:54:26
 */
public class WatchdogMainActivity extends BaseActivity implements OnClickListener, OnGestureListener {
	private static final String TAG = "WatchdogMainActivity";
	/** 需要显示的view的标志位（以区别当前显示的view模块） */
	public static final int TYPE_INBOX = 1001;
	public static final int TYPE_CARD = 1002;
	public static final int TYPE_CONTACT = 1003;
	public static final int TYPE_COLLECT = 1004;
	public static final int TYPE_SCANCODE_RECORD = 1005;
	/** 当前显示view类型的标志位 */
	private int mCurrentType = TYPE_CARD;

	/**
	 * 标记一些模块显示是否显示为搜索后结果的状态
	 * （此段代码的主要作用：当列表显示为搜索状态时，再次点击某个模块入口，会清空搜索条件，并请求数据刷新view，即把全部数据显示出来
	 * ，如果不为搜索结果状态，则view不重新绘制）
	 */
	private final int CARDVIEW_SEARCH = 1101;
	private final int CARDVIEW_ALL = 1102;
	private final int COLLECT_SEARCH = 1103;
	private final int COLLECT_ALL = 1104;
	private final int SCANRECORD_SEARCH = 1105;
	private final int SCANRECORD_ALL = 1106;
	private int mCardMode = CARDVIEW_ALL;
	private int mCollectMode = COLLECT_ALL;
	private int mScanRecordMode = SCANRECORD_ALL;

	/** 暂时放这里的（修改侧边栏相关的） */
	private RelativeLayout rl_inbox;
	private RelativeLayout rl_card;
	private RelativeLayout rl_collect;
	private RelativeLayout rl_contact;
	private RelativeLayout rl_scan_record;
	private TextView text_inbox;
	private TextView text_card;
	private TextView text_collect;
	private TextView text_contact;
	private TextView text_scan_record;

	private ScrollView leftView;

	/** 收藏的topbar下拉菜单默认选择显示类别 */
	private int initCollectViewSort = R.id.sort_ido;
	/** 联系人的topbar下拉菜单默认选择显示类别 */
	private int initContactViewSort = R.id.sort_contact_space;

	/** 结果的条目数 */
	private TextView textCount;
	/** 未读消息数目 */
	private TextView unReadCount;
	/** 切换菜单显示隐藏 */
	private ImageView modeIcon;
	/** 搜索框 */
	private EditText searchEditText;
	/** inbox的layout */
	private FrameLayout inboxListLayout;
	/** 卡片夹layout */
	private FrameLayout cardListLayout;
	/** 扫码记录layout */
	private FrameLayout scanRecordLayout;
	/** 收藏layout */
	private FrameLayout mCollectLayout;
	/** 通讯录layout */
	private FrameLayout mContactLayout;

	/** 手势操作 */
	private GestureDetector gestureDetector;

	private UserInboxListView inboxListMain;
	private UserCardListView cardListMain;
	private ScanCodeRecordView scanCodeRecodeMain;
	private UserContactView userContactView;
	private UserCollectView userCollectView;

	float downXValue;
	long downTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_manager_main);

		int type = getIntent().getIntExtra(ACTIVITY_EXTRA, TYPE_INBOX);

		findView();
		setTopbar(type);
		init(type);
	}

	/**
	 * @Title: init
	 * @Description:初始化一些变量
	 * @param
	 * @return void
	 */
	private void init(int type) {
		// if()
		if (AccountService.getInstance(this).isLogin()) {
			rl_inbox.setVisibility(View.VISIBLE);
//			unReadCount.setVisibility(View.VISIBLE);
		} else {
			rl_inbox.setVisibility(View.GONE);
//			unReadCount.setVisibility(View.GONE);
		}
		refreshWatchDogData();
		if (type == TYPE_INBOX) {
			if (AccountService.getInstance(this).isLogin()) {
				if (inboxListMain == null) {
					inboxListMain = new UserInboxListView(this, findViewById(R.id.layout_inbox));
				}
				setResultCount(inboxListMain.getListItemCount());
				setVisibility(TYPE_INBOX);
			} else {
				if (cardListMain == null) {
					cardListMain = new UserCardListView(this, findViewById(R.id.layout_card_list));
				}
				setResultCount(cardListMain.getListItemCount());
				setVisibility(TYPE_CARD);
			}
		} else if (type == TYPE_CARD) {
			if (cardListMain == null) {
				cardListMain = new UserCardListView(this, findViewById(R.id.layout_card_list));
			}
			setResultCount(cardListMain.getListItemCount());
			setVisibility(TYPE_CARD);
		} else if (type == TYPE_CONTACT) {
			if (userContactView == null) {
				userContactView = new UserContactView(this, mContactLayout);
			}
			setResultCount(userContactView.changeList(UserContactView.SPACE_CONTACT));
			setVisibility(TYPE_CONTACT);
		} else if (type == TYPE_COLLECT) {
			if (userCollectView == null) {
				userCollectView = new UserCollectView(this, mCollectLayout);
				userCollectView.requestServiceData(PersonRecordType.IDOL);
			}
			setResultCount(userCollectView.getListItemCount());
			setVisibility(TYPE_COLLECT);
		} else if (type == TYPE_SCANCODE_RECORD) {
			if (scanCodeRecodeMain == null) {
				scanCodeRecodeMain = new ScanCodeRecordView(this, findViewById(R.id.layout_scan_code));
			}
			setResultCount(scanCodeRecodeMain.getListItemCount());
			setVisibility(TYPE_SCANCODE_RECORD);
		}

		gestureDetector = new GestureDetector(new MyGestureDetector());
		rl_inbox.setOnClickListener(this);
		rl_card.setOnClickListener(this);
		rl_collect.setOnClickListener(this);
		rl_contact.setOnClickListener(this);
		rl_scan_record.setOnClickListener(this);
	}

	/**
	 * @Title: setResultCount
	 * @Description:设置显示条目数
	 * @param
	 * @return void
	 */
	public void setResultCount(int count) {
		String countStr = String.format(getString(R.string.watchdog_result_count), count);
		textCount.setText(countStr);
	}

	/**
	 * @Title: findView
	 * @Description:实例化view
	 * @param
	 * @return void
	 */
	private void findView() {
		modeIcon = (ImageView) findViewById(R.id.change_mode_icon);
		leftView = (ScrollView) findViewById(R.id.view_left_menu);
		textCount = (TextView) findViewById(R.id.text_result_count);
		unReadCount = (TextView) findViewById(R.id.inbox_unread_count);
		searchEditText = (EditText) findViewById(R.id.edit_search);

		inboxListLayout = (FrameLayout) findViewById(R.id.layout_inbox);
		cardListLayout = (FrameLayout) findViewById(R.id.layout_card_list);
		scanRecordLayout = (FrameLayout) findViewById(R.id.layout_scan_code);
		mCollectLayout = (FrameLayout) this.findViewById(R.id.layout_userCenter_collect);
		mContactLayout = (FrameLayout) this.findViewById(R.id.layout_userCenter_contact);

		rl_inbox = (RelativeLayout) findViewById(R.id.itemlayout_inbox);
		rl_card = (RelativeLayout) findViewById(R.id.itemlayout_card);
		rl_collect = (RelativeLayout) findViewById(R.id.itemlayout_collect);
		rl_contact = (RelativeLayout) findViewById(R.id.itemlayout_contact);
		rl_scan_record = (RelativeLayout) findViewById(R.id.itemlayout_scan_record);
		text_inbox = (TextView) findViewById(R.id.itemText_inbox);
		text_card = (TextView) findViewById(R.id.itemText_card);
		text_collect = (TextView) findViewById(R.id.itemText_collect);
		text_contact = (TextView) findViewById(R.id.itemText_contact);
		text_scan_record = (TextView) findViewById(R.id.itemText_scan_record);

	}

	/**
	 * @Title: setTopbar
	 * @Description:设置topbar
	 * @param
	 * @return void
	 */
	private void setTopbar(int type) {
		// TODO Auto-generated method stub
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		TextView textRight5 = (TextView) mTopbar.findViewById(R.id.img_topbar_rightButton5);
		TextView textTitle = (TextView) mTopbar.findViewById(R.id.txt_topbar_title);
		image1.setImageResource(R.drawable.topbar_overflow);
		textRight5.setText(R.string.watchdog_date);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(this);
		mTopbar.findViewById(R.id.layout_topbar_rightButton5).setOnClickListener(this);
		if (type == TYPE_INBOX) {
			textTitle.setText(R.string.watchdog_inbox);
			mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.GONE);
			mTopbar.findViewById(R.id.layout_topbar_rightButton5).setVisibility(View.GONE);
		} else if (type == TYPE_CARD) {
			textTitle.setText(R.string.watchdog_card_holder);
			mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.GONE);
			mTopbar.findViewById(R.id.layout_topbar_rightButton5).setVisibility(View.GONE);
		} else if (type == TYPE_CONTACT) {
			textTitle.setText(R.string.watchdog_contacts);
			mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
			mTopbar.findViewById(R.id.layout_topbar_rightButton5).setVisibility(View.GONE);
		} else if (type == TYPE_COLLECT) {
			textTitle.setText(R.string.watchdog_favorite);
			mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
			mTopbar.findViewById(R.id.layout_topbar_rightButton5).setVisibility(View.GONE);
		} else if (type == TYPE_SCANCODE_RECORD) {
			textTitle.setText(R.string.watchdog_scan_record);
			mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.GONE);
			mTopbar.findViewById(R.id.layout_topbar_rightButton5).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_topbar_rightButton1:
			final ZhimaPopupMenu popupMenu = new ZhimaPopupMenu(WatchdogMainActivity.this);
			if (mCurrentType == TYPE_COLLECT) {
				popupMenu.setSelection(initCollectViewSort);
				popupMenu.setMenuItems(R.menu.favorite_sort);
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(ZhimaMenuItem item, int position) {
						switch (item.getId()) {
						case R.id.sort_ido:
							initCollectViewSort = R.id.sort_ido;
							userCollectView.requestServiceData(PersonRecordType.IDOL);
							setResultCount(userCollectView.getListItemCount());
							break;
						case R.id.sort_couples:
							initCollectViewSort = R.id.sort_couples;
							userCollectView.requestServiceData(PersonRecordType.WEDDING);
							setResultCount(userCollectView.getListItemCount());
							break;
						case R.id.sort_other:
							initCollectViewSort = R.id.sort_other;
							userCollectView.requestServiceData(PersonRecordType.FAVORITE);
							setResultCount(userCollectView.getListItemCount());
							break;
						}
					}
				});
			} else if (mCurrentType == TYPE_CONTACT) {
				popupMenu.setSelection(initContactViewSort);
				popupMenu.setMenuItems(R.menu.contact_sort);
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(ZhimaMenuItem item, int position) {
						switch (item.getId()) {
						case R.id.sort_contact_personal:
							initContactViewSort = R.id.sort_contact_personal;
							setResultCount(userContactView.changeList(UserContactView.PERSONAL_CONTACT));
							break;
						case R.id.sort_contact_space:
							initContactViewSort = R.id.sort_contact_space;
							setResultCount(userContactView.changeList(UserContactView.SPACE_CONTACT));
							break;
						}
					}
				});
			}

			popupMenu.show(v);

			break;
		case R.id.layout_topbar_rightButton5:
			final ZhimaDatePickerDialog dateDialog = new ZhimaDatePickerDialog(this,
					findViewById(R.id.layout_topbar_rightButton1));
			dateDialog.setOnOnlyBtClickListener(R.string.finish, new OnOnlyBtClickListener() {

				@Override
				public void onOnlyBtClick() {
					// TODO Auto-generated method stub
					long beginTime = getBeginTime(dateDialog.getDateSignStr());
					long endTime = getEndTime(dateDialog.getDateSignStr());
					if (mCurrentType == TYPE_SCANCODE_RECORD) {
						scanCodeRecodeMain.searchByTime(beginTime, endTime);
						mScanRecordMode = SCANRECORD_SEARCH;
					}
				}
			});
			dateDialog.show();
			break;
		case R.id.itemlayout_inbox:
			if (inboxListMain == null) {
				inboxListMain = new UserInboxListView(this, findViewById(R.id.layout_inbox));
			}
			setResultCount(inboxListMain.getListItemCount());
			setVisibility(TYPE_INBOX);
			break;
		case R.id.itemlayout_card:
			if (cardListMain == null) {
				cardListMain = new UserCardListView(this, findViewById(R.id.layout_card_list));
			}
			if (mCardMode == CARDVIEW_SEARCH) {
				cardListMain.requestServiceData();
				mCardMode = CARDVIEW_ALL;
			}
			setResultCount(cardListMain.getListItemCount());
			setVisibility(TYPE_CARD);
			break;
		case R.id.itemlayout_collect:
			if (userCollectView == null) {
				userCollectView = new UserCollectView(this, mCollectLayout);
				userCollectView.requestServiceData(PersonRecordType.IDOL);
			}
			if (mCollectMode == COLLECT_SEARCH) {
				userCollectView.requestServiceData(PersonRecordType.IDOL);
				mCollectMode = COLLECT_ALL;
				initCollectViewSort = R.id.sort_ido;
			}
			setResultCount(userCollectView.getListItemCount());
			setVisibility(TYPE_COLLECT);
			break;
		case R.id.itemlayout_contact:
			if (userContactView == null) {
				userContactView = new UserContactView(this, mContactLayout);
			}
			int contactCount = userContactView.changeList(UserContactView.SPACE_CONTACT);
			setResultCount(contactCount);
			setVisibility(TYPE_CONTACT);
			break;
		case R.id.itemlayout_scan_record:
			if (scanCodeRecodeMain == null) {
				scanCodeRecodeMain = new ScanCodeRecordView(this, findViewById(R.id.layout_scan_code));
			}
			if (mScanRecordMode == SCANRECORD_SEARCH) {
				scanCodeRecodeMain.requestServiceData();
				mScanRecordMode = SCANRECORD_ALL;
			}
			setResultCount(scanCodeRecodeMain.getListItemCount());
			setVisibility(TYPE_SCANCODE_RECORD);
			break;
		}
	}

	/**
	 * @Title: getBeginTime
	 * @Description:获取某一天的开始时的毫秒数
	 * @param dateStr
	 *            (2012-10-01此种格式的)
	 * @return long
	 */
	private long getBeginTime(String dateStr) {
		long begingTime;
		try {
			Date sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr + " 00:00:00");
			begingTime = sdf.getTime();
		} catch (ParseException e) {
			begingTime = 0;
		}
		return begingTime;
	}

	/**
	 * @Title: getEndTime
	 * @Description:获取某一天的结束时的毫秒数
	 * @param dateStr
	 *            (2012-10-01此种格式的)
	 * @return long
	 */
	private long getEndTime(String dateStr) {
		long endTime;
		try {
			Date sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr + " 23:59:59");
			endTime = sdf.getTime();
		} catch (ParseException e) {
			endTime = 0;
		}
		return endTime;
	}

	/**
	 * @Title: setVisibility
	 * @Description:控制各个子模块view的显示
	 * @param type
	 *            --代表该模块类型的int值(本activity变量中有定义)
	 * @return void
	 */
	private void setVisibility(int type) {
		mCurrentType = type;
		setLeftItemView(type);
		inboxListLayout.setVisibility(type == TYPE_INBOX ? View.VISIBLE : View.GONE);
		cardListLayout.setVisibility(type == TYPE_CARD ? View.VISIBLE : View.GONE);
		mContactLayout.setVisibility(type == TYPE_CONTACT ? View.VISIBLE : View.GONE);
		scanRecordLayout.setVisibility(type == TYPE_SCANCODE_RECORD ? View.VISIBLE : View.GONE);
		mCollectLayout.setVisibility(type == TYPE_COLLECT ? View.VISIBLE : View.GONE);
		searchEditText.setVisibility(type == TYPE_INBOX || type == TYPE_CONTACT ? View.GONE : View.VISIBLE);
		// 搜索框的enter监听（系统自带输入法）
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (mCurrentType == TYPE_CARD) {
						mCardMode = CARDVIEW_SEARCH;
						cardListMain.searchCardList(searchEditText.getText().toString());
					} else if (mCurrentType == TYPE_COLLECT) {
						mCollectMode = COLLECT_SEARCH;
						userCollectView.searchCollect(searchEditText.getText().toString());
					} else if (mCurrentType == TYPE_SCANCODE_RECORD) {
						mScanRecordMode = SCANRECORD_SEARCH;
						scanCodeRecodeMain.searchScanRecordByString(searchEditText.getText().toString());
					}

				}
				return false;
			}
		});
		// 搜索框的enter监听（第三方输入法）
		searchEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (mCurrentType == TYPE_CARD) {
						mCardMode = CARDVIEW_SEARCH;
						cardListMain.searchCardList(searchEditText.getText().toString());
					} else if (mCurrentType == TYPE_COLLECT) {
						mCollectMode = COLLECT_SEARCH;
						userCollectView.searchCollect(searchEditText.getText().toString());
					} else if (mCurrentType == TYPE_SCANCODE_RECORD) {
						mScanRecordMode = SCANRECORD_SEARCH;
						scanCodeRecodeMain.searchScanRecordByString(searchEditText.getText().toString());
					}
					return true;
				}
				return false;
			}
		});
		setTopbar(type);
	}

	private void setLeftItemView(int type) {
		int normalColor = getResources().getColor(R.color.watchdog_left_item_text);
		rl_inbox.setSelected(type == TYPE_INBOX);
		rl_card.setSelected(type == TYPE_CARD);
		rl_collect.setSelected(type == TYPE_COLLECT);
		rl_contact.setSelected(type == TYPE_CONTACT);
		rl_scan_record.setSelected(type == TYPE_SCANCODE_RECORD);
		text_inbox.setTextColor(type == TYPE_INBOX ? Color.WHITE : normalColor);
		text_card.setTextColor(type == TYPE_CARD ? Color.WHITE : normalColor);
		text_collect.setTextColor(type == TYPE_COLLECT ? Color.WHITE : normalColor);
		text_contact.setTextColor(type == TYPE_CONTACT ? Color.WHITE : normalColor);
		text_scan_record.setTextColor(type == TYPE_SCANCODE_RECORD ? Color.WHITE : normalColor);
	}

	/**
	 * @Title: refreshWatchDogData
	 * @Description:刷新个人管家模块的数据（在这里做主要是保证子模块之间是单例实现，并且进入管家模块都是最新数据）
	 * @param
	 * @return void
	 */
	private void refreshWatchDogData() {
		InboxService mInboxService = InboxService.getInstance(this);
		mInboxService.getMessageList(true, this);

		UserService mUserService = UserService.getInstance(this);
		mUserService.getMyCouponList(true, this);

		mUserService.getUserRecordList(PersonRecordType.IDOL, getBeginTime(), getEndTime(), 0, true, this);
		mUserService.getUserRecordList(PersonRecordType.FAVORITE, getBeginTime(), getEndTime(), 0, true, this);
		mUserService.getUserRecordList(PersonRecordType.WEDDING, getBeginTime(), getEndTime(), 0, true, this);
		mUserService.getUserRecordList(PersonRecordType.SCANNING, getBeginTime(), getEndTime(), 0, true, this);
	}

	/**
	 * @Title: getBeginTime
	 * @Description:自定义的开始时间（最早的时间）
	 * @param
	 * @return long
	 */
	private long getBeginTime() {
		long begingTime;
		try {
			Date sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2012-10-01 01:01:00");
			begingTime = sdf.getTime();
		} catch (ParseException e) {
			begingTime = 0;
		}
		return begingTime;
	}

	/**
	 * @Title: getEndTime
	 * @Description:结束时间（为当前时间）
	 * @param
	 * @return long
	 */
	private long getEndTime() {
		long now = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		c.add(Calendar.DAY_OF_YEAR, 1);
		return c.getTimeInMillis();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(AccountService.getInstance(this).isLogin()){
			InboxService.getInstance(this).getMessageCount(0, this);
		}else{
//			unReadCount.setVisibility(View.GONE);
		}
		super.onResume();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_MESSAGE_COUNT_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetMessageCountProtocol p = (GetMessageCountProtocol) protocol;
					int count = p.getUnReadCount();
					if (count <= 0) {
						unReadCount.setVisibility(View.GONE);
					} else {
						unReadCount.setVisibility(View.VISIBLE);
						unReadCount.setText(String.valueOf(p.getUnReadCount()));
					}
				} else {
					HaloToast.show(this, protocol.getProtocolErrorMessage(), 0);
				}
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
		super.onHttpStart(protocol);
	}

	/**
	 * @ClassName MyGestureDetector
	 * @Description 手势操作
	 * @author jiang
	 * @date 2013-1-27 下午09:23:09
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					leftView.setVisibility(View.GONE);
					modeIcon.setVisibility(View.VISIBLE);
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					leftView.setVisibility(View.VISIBLE);
					modeIcon.setVisibility(View.INVISIBLE);
				}
			} catch (Exception e) {
				Log.e(TAG, "onFling error", e);
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);

	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

}

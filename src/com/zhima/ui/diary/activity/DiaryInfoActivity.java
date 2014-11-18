package com.zhima.ui.diary.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.DiaryService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.VideoWebView;
import com.zhima.ui.common.view.WebViewPager;
import com.zhima.ui.common.view.WebViewPager.OnPageChangeListener;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.diary.adapter.DiaryInfoViewPager;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.share.SharePopupMenu;

/**
 * @ClassName: DiaryActivity
 * @Description: 日志详情
 * @author luqilong
 * @date 2013-1-19 下午8:18:32
 */
public class DiaryInfoActivity extends BaseActivity {

	protected static final String TAG = DiaryInfoActivity.class.getSimpleName();

	private long mmZMObjId;
	//用户id
	private long mUserId;
	private long mSpaceId;
	private int position;
	private boolean mIsSpaceDiary;
	private DiaryService mDiaryService;

	private RefreshListData<ZMDiary> mCacheDiaryList;
	private ArrayList<ZMDiary> mDiaryDataList;

	private WebViewPager mViewPager;
	private DiaryInfoViewPager mPagerAdapter;
	private int toastCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_info_activity);
		setTopbar();
		mViewPager = (WebViewPager) this.findViewById(R.id.webvpager);

		getIntentData();
		setDiaryView();
		setCurrentWebView(position);
		checkUser(mUserId);
		setListener();
	}

	/**
	 * 获取itent数据
	 */
	public void getIntentData() {
		mDiaryService = DiaryService.getInstance(this);
		Intent it = getIntent();
		mUserId = it.getLongExtra(DiaryConsts.user_Id, ZMConsts.INVALID_ID);
		mSpaceId = it.getLongExtra(DiaryConsts.space_id, ZMConsts.INVALID_ID);
		//查看单个日志
		mIsSpaceDiary = mSpaceId > 0 ? true : false;

		if (mIsSpaceDiary) {
			mCacheDiaryList = mDiaryService.getCacheDiaryList(mSpaceId, mIsSpaceDiary);
		} else {
			mCacheDiaryList = mDiaryService.getCacheDiaryList(mUserId, mIsSpaceDiary);
		}

		mDiaryDataList = mCacheDiaryList.getDataList();
		position = it.getIntExtra(DiaryConsts.position, 0);
		mmZMObjId = it.getLongExtra("id", -1);
	}

	private void setListener() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (mPagerAdapter != null) {
					mPagerAdapter.setLoadDataItem(-1);
					VideoWebView webView = getCurrentWebView(position);
					if (webView != null) {
						setWebViewContent(webView, (ZMDiary) webView.getTag(), true);
					} else {
						HaloToast.show(getApplicationContext(), R.string.load_failed);
					}
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (mDiaryDataList != null) {
					int size = mDiaryDataList.size();
					if (position > 0 && (position + 1) == size && positionOffset == 0 && positionOffsetPixels == 0) {
						if (toastCount > 3) {
							HaloToast.show(getApplicationContext(), "这是最后一页");
							toastCount = 0;
						} else {
							toastCount++;
						}
					} else if (size > 0 && position == 0 && positionOffset == 0 && positionOffsetPixels == 0) {
						if (toastCount > 3) {
							HaloToast.show(getApplicationContext(), "这是第一页");
							toastCount = 0;
						} else {
							toastCount++;
						}
					} else {
						toastCount = 0;
					}
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
//				Logger.getInstance(TAG).debug("onPageScrollStateChanged state:" + state);
			}
		});
	}

	/**
	 * 设置view数据
	 */
	private void setDiaryView() {
		mDiaryDataList = mCacheDiaryList.getDataList();
		if (mPagerAdapter == null) {
			mPagerAdapter = new DiaryInfoViewPager(this, mDiaryDataList);
			mViewPager.setAdapter(mPagerAdapter);
		} else {
			mPagerAdapter.setData(mDiaryDataList);
			mPagerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int position = mViewPager.getCurrentItem();
		VideoWebView curronWebView = getCurrentWebView(position);
		if (curronWebView != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK && curronWebView.canGoBack()) {
				curronWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	//---------------------------------------------------------------------------
	//http

	private void getServiceDiaryDada(VideoWebView myWebView, ZMDiary diary) {
		long keyId = mIsSpaceDiary ? mSpaceId : mUserId;
		mDiaryService.getDiaryDetail(diary, keyId, new MyGetDiaryHttpCallback(myWebView, diary));
	}

	private class delectDiaryHttpCallBack implements IHttpRequestCallback {
		private ZMDiary zmDiary;

		public delectDiaryHttpCallBack(ZMDiary zmDiary) {
			super();
			this.zmDiary = zmDiary;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 删除日志成功
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.delete_success);
					mDiaryDataList.remove(zmDiary);
					if (mDiaryDataList.size() <= 0) {
						finish();
					} else {
						setDiaryView();
					}
				} else {
					//删除日志失败
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed);
			}
		}
	}

	private class MyGetDiaryHttpCallback implements IHttpRequestCallback {
		private VideoWebView myWebView;
		private ZMDiary zmDiary;

		public MyGetDiaryHttpCallback(VideoWebView webView, ZMDiary zmDiary) {
			super();
			this.myWebView = webView;
			this.zmDiary = zmDiary;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				if (protocol.getProtocolType() == ProtocolType.GET_DIARY_PROTOCOL) {
					// 获取详情
					if (protocol.isHandleSuccess()) {
						GetDiaryProtocol p = (GetDiaryProtocol) protocol;
						ZMDiary diary = p.getDiary();
						setWebViewContent(myWebView, diary, false);
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
					}
				}
			} else {
				// TODO 网络访问错误
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}

	}

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			final ZMDiary zmDiary = getCurrentDiary();
			if (zmDiary != null) {
				String name = "";
				String id = "";
				if (mIsSpaceDiary) {
					ZMObject cacheZMObject = ScanningcodeService.getInstance(DiaryInfoActivity.this).getCacheZMObject(
							mmZMObjId);
					if (cacheZMObject != null) {
						name = cacheZMObject.getName();
						id = cacheZMObject.getZMID();
					}
				} else {
					User user = UserService.getInstance(DiaryInfoActivity.this).getUser(mUserId);
					if (user != null) {
						name = user.getNickname();
						id = String.valueOf(user.getZMUserId());
					}
				}
				final String sms_message = String.format(getText(R.string.diary_sms_message).toString(), name, id);
				final String shareContent = sms_message;

				if (UserService.getInstance(DiaryInfoActivity.this).isMySelf(mUserId)) {
					ZhimaPopupMenu menu = new ZhimaPopupMenu(DiaryInfoActivity.this);
					menu.setMenuItems(R.menu.diary_share);
					menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public void onMenuItemClick(ZhimaMenuItem item, int position) {

							switch (item.getId()) {
							case R.id.sms:
								SharePopupMenu.smsShare(DiaryInfoActivity.this, DiaryInfoActivity.this, v, sms_message,
										shareContent);
								break;
							case R.id.sina_weibo:
								SharePopupMenu.sinaShare(DiaryInfoActivity.this, DiaryInfoActivity.this, v,
										sms_message, shareContent);
								break;
							case R.id.qq_weibo:
								SharePopupMenu.qqShare(DiaryInfoActivity.this, DiaryInfoActivity.this, v, sms_message,
										shareContent);
								break;
							case R.id.renren_space:
								SharePopupMenu.renrenShare(DiaryInfoActivity.this, DiaryInfoActivity.this, v,
										sms_message, shareContent);
							case R.id.delete:
								ZMDiary currentDiary = getCurrentDiary();
								if (currentDiary != null) {
									startWaitingDialog(null, R.string.loading);
									mDiaryService.deleteDiary(currentDiary.getId(), mIsSpaceDiary,
											new delectDiaryHttpCallBack(zmDiary));
								} else {
									HaloToast.show(getApplicationContext(), R.string.load_failed);
								}
								break;
							}
						}
					});
					menu.show(v);
				} else {
					SharePopupMenu.show(DiaryInfoActivity.this, DiaryInfoActivity.this, v, sms_message, shareContent);
				}
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}

	};

	/** 标题栏:删除 */
	private View.OnClickListener deleteTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MessageDialog dialog = new MessageDialog(DiaryInfoActivity.this, v);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.show();
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					ZMDiary zmDiary = getCurrentDiary();
					if (zmDiary != null) {
						startWaitingDialog(null, R.string.loading);
						long keyId = mIsSpaceDiary ? zmDiary.getSyncId() : zmDiary.getDiaryId();
						mDiaryService.deleteDiary(keyId, mIsSpaceDiary, new delectDiaryHttpCallBack(zmDiary));
					} else {
						ErrorManager.showErrorMessage(getApplicationContext());
					}
				}

				@Override
				public void onLeftBtClick() {
				}
			});
		}
	};

	/** 标题栏:转发 */
	private View.OnClickListener forwardTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ZMDiary zmDiary = getCurrentDiary();
			if (zmDiary != null) {
				Intent it = new Intent(DiaryInfoActivity.this, DiaryForwardActivity.class);
				it.putExtra(DiaryConsts.user_Id, mUserId);
				it.putExtra(DiaryConsts.diary_Id, zmDiary.getSyncId() > 0 ? zmDiary.getSyncId() : zmDiary.getDiaryId());//zmDiary.getDiaryId()
				it.putExtra(DiaryConsts.space_id, mSpaceId);
				startActivity(it);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 标题栏:评论列表 */
	private View.OnClickListener commentTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ZMDiary zmDiary = getCurrentDiary();
			if (zmDiary != null) {
				Intent it = new Intent(DiaryInfoActivity.this, DiaryCommentListActivity.class);
				it.putExtra(DiaryConsts.user_Id, mUserId);
				it.putExtra(DiaryConsts.diary_Id, zmDiary.getSyncId() > 0 ? zmDiary.getSyncId() : zmDiary.getDiaryId());
				it.putExtra(DiaryConsts.space_id, mSpaceId);
				startActivity(it);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/**
	 * 设置当前的view
	 */
	private void setCurrentWebView(int position) {
		if (mPagerAdapter != null) {
			if (mPagerAdapter.getCount() > position) {
				mViewPager.setCurrentItem(position);
				if (mPagerAdapter != null) {
					mPagerAdapter.setLoadDataItem(position);
				}
			}
		}
	}

	/**
	 * 获取当前的webview
	 */
	private VideoWebView getCurrentWebView(int position) {
		VideoWebView web = (VideoWebView) mViewPager.findViewById(position);
		return web;
	}

	/**
	 * 获取当期的diary
	 */
	private ZMDiary getCurrentDiary() {
		int position = mViewPager.getCurrentItem();
		if (mDiaryDataList.size() > position) {
			return mDiaryDataList.get(position);
		}
		return null;
	}

	public void setWebViewContent(VideoWebView myWebView, ZMDiary diary, boolean isLoadServices) {
		String html = diary.getHtml();
		if (TextUtils.isEmpty(html)) {
			if (isLoadServices) {
				if (!isDialogShowing()) {
					startWaitingDialog(null, R.string.loading);
				}
				getServiceDiaryDada(myWebView, diary);
			}
//			myWebView.loadData("", "text/html", "utf-8");
		} else {
			myWebView.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
//			myWebView.loadData(getConvertHtml(str), mimeType, encoding);
		}
	}

//	private String getConvertHtml(String html) {
//		String convert = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body>"
//				+ html + "</body></html>";
//		return convert;
//	}

	public void checkUser(long userId) {
		if (UserService.getInstance(this).isMySelf(userId)) {
			refreshTopbar(true);
		} else {
			refreshTopbar(false);
		}
	}

	public void refreshTopbar(boolean bl) {
		ZhimaTopbar topbar = getTopbar();

		boolean isLogin = AccountService.getInstance(this).isLogin();

		if (isLogin) {
			topbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(forwardTopbarClick);
			((ImageView) topbar.findViewById(R.id.img_topbar_rightButton2)).setImageResource(R.drawable.topbar_forward);
			topbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
		}
		//公用topbar
		topbar.findViewById(R.id.layout_topbar_rightButton3).setOnClickListener(commentTopbarClick);
		((ImageView) topbar.findViewById(R.id.img_topbar_rightButton3)).setImageResource(R.drawable.topbar_reply);
		topbar.findViewById(R.id.layout_topbar_rightButton3).setVisibility(View.VISIBLE);

		topbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(shareTopbarClick);
		((ImageView) topbar.findViewById(R.id.img_topbar_rightButton1)).setImageResource(R.drawable.topbar_overflow);
		topbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

	}

	private void setTopbar() {
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
//

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("日志详情");
	}

}

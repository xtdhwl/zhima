package com.zhima.ui.usercenter.watchdog.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetMyCouponListProtocol;
import com.zhima.base.protocol.UserProtocolHandler.SearchCardProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomListView;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName UserCardListView
 * @Description 卡片夹
 * @author jiang
 * @date 2013-1-27 上午10:34:13
 */
public class UserCardListView {
	private Context mContext;
	private View mContentView;
	private CustomListView<UserCoupon> couponListView;
	private RefreshListData<UserCoupon> refreshListDate;
	private UserService mUserService;
	private LinearLayout mLoadingbr;
	private int listCount;
	/** 区别搜索模式和查看列表模式的标识符 */
	private int mCurrentMode = ALL_MODE;
	/** 查看列表全部数据时（不是搜索结果） */
	private static final int ALL_MODE = 1;
	/** 列表搜索结果状态 */
	private static final int SEARCH_MODE = 2;

	/** 搜索的关键词 */
	private String mKeyString = "";
	public static long lastTimestamp = 0;
	/** 是否最后一页 */
	private boolean isLastPage = false;
	/** 是否结束请求服务器 */
	private boolean isRequestFinish = false;

	public UserCardListView(Context context, View view) {
		// super(context);
		this.mContext = context;
		mContentView = view;

		init();
		requestServiceData();
	}

	/**
	 * @Title: getView
	 * @Description:获取到当前的view
	 * @param
	 * @return View
	 */
	public View getView() {
		return mContentView;
	}

	/**
	 * @Title: getListItemCount
	 * @Description:获取列表条目数
	 * @param
	 * @return int
	 */
	public int getListItemCount() {
		return listCount;
	}

	/**
	 * @Title: searchCardList
	 * @Description:关键字搜索卡片夹
	 * @param
	 * @return void
	 */
	public void searchCardList(String keyString) {
		mKeyString = keyString;
		couponListView.mAlreadyLoadData.clear();
		couponListView.updateListView();
		couponListView.isLoading(true);
		couponListView.addLoading();
		mCurrentMode = SEARCH_MODE;
		mUserService.searchCard(mKeyString, true, callBack);
	}

	/**
	 * @Title: init
	 * @Description:初始化变量和view
	 * @param
	 * @return void
	 */
	private void init() {
		mUserService = UserService.getInstance(mContext);
		mLoadingbr = (LinearLayout) mContentView.findViewById(R.id.card_case_view_loading);
		couponListView = (CustomListView<UserCoupon>) mContentView.findViewById(R.id.list_card_case);
		couponListView.setOnItemClickListener(onClick);
		couponListView.setOnItemLongClickListener(longClick);
	}

	/**
	 * @Title: requestServiceData
	 * @Description:请求数据并填充
	 * @param
	 * @return void
	 */
	public void requestServiceData() {
		mCurrentMode = ALL_MODE;
		refreshListDate = mUserService.getMyCacheCouponList();
		couponListView.addLoading();
		setListBatchLoad();
		showContentEmpty(false, false);

		couponListView.mAlreadyLoadData.clear();
		couponListView.updateListView();
		couponListView.setVisibility(View.GONE);
		if (refreshListDate.isEmpty()) {
			mLoadingbr.setVisibility(View.VISIBLE);
			mUserService.getMyCouponList(true, callBack);
		} else {
			addCacheData();
		}
	}

	/**
	 * @Title: addCacheData
	 * @Description:添加本地缓存数据到view
	 * @param
	 * @return void
	 */
	private void addCacheData() {
		ArrayList<UserCoupon> newDataList = refreshListDate.getDataList();
		listCount = newDataList.size();
		for (int i = couponListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
			couponListView.mAlreadyLoadData.add(newDataList.get(i));
		}
		couponListView.updateListView();
		couponListView.setVisibility(View.VISIBLE);
		couponListView.deleteLoading();
	}

	/**
	 * @Title: setListBatchLoad
	 * @Description:设置分批加载
	 * @param
	 * @return void
	 */
	private void setListBatchLoad() {
		couponListView.setBatchLoad(new ZhimaAdapter<UserCoupon>(mContext, R.layout.space_joke_list_item,
				couponListView.mAlreadyLoadData) {

			@Override
			public Object createViewHolder(View view, UserCoupon data) {
				// TODO Auto-generated method stub
				ViewHolder holder = new ViewHolder();
				holder.mImageView = (ImageView) view.findViewById(R.id.img_joke_image);
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_joke_title);
				holder.mDateText = (TextView) view.findViewById(R.id.txt_joke_content);
				holder.mImageView = (ImageView) view.findViewById(R.id.img_joke_image);
				holder.mReplyText = (RelativeLayout) view.findViewById(R.id.layout_reply);
				return holder;
			}

			@Override
			public void bindView(UserCoupon data, int position, View view) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				UserCoupon userCoupon = (UserCoupon) data;
				String caption = userCoupon.getName();
				if (caption.equals("")) {
					caption = "无标题" + position;
				} else {
					// if (userCoupon.getTargetType() ==
					// TargetType.TARGET_TYPE_UNKNOWN_SPACE) {
					// if (!TextUtils.isEmpty(userCoupon.getContent())) {
					// caption = userCoupon.getContent();
					// }
					// }
				}
				holder.mTitleText.setText(caption);
				holder.mReplyText.setVisibility(View.GONE);
				// 发布日期
				holder.mDateText.setText(new SimpleDateFormat("MM-dd HH:mm").format(userCoupon.getReceiveTime()));
				HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mImageView,
						((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
			}

			@Override
			public void getFirstData() {
				// TODO Auto-generated method stub
			}

			@Override
			public void getData() {
				// TODO Auto-generated method stub
				if (isLastPage) {
					if (!isRequestFinish) {
						HaloToast.show(mContext, R.string.no_more_data);
					}
					isRequestFinish = true;
					return;
				}
				couponListView.isLoading(true);
				couponListView.addLoading();
				if (mCurrentMode == ALL_MODE) {
					mUserService.getMyCouponList(false, callBack);
				} else {
					mUserService.searchCard(mKeyString, false, callBack);
				}

			}

			class ViewHolder {
				ImageView mImageView;
				TextView mTitleText;
				TextView mDateText;
				RelativeLayout mReplyText;
			}

		});
	}

	/**
	 * @Title: showDialog
	 * @Description:弹出删除的对话框
	 * @param position
	 *            被点击的item的position
	 * @return void
	 */
	private void showDialog(final int position) {
		MessageDialog dialog = new MessageDialog(mContext, mContentView);
		dialog.setTitle(R.string.dialog_title);
		dialog.setMessage(R.string.delete_msg);
		dialog.setRightBtText(R.string.delete);
		dialog.setLeftBtText(R.string.cancel);
		dialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {
				if (refreshListDate != null) {
					if (!refreshListDate.isEmpty()) {
						UserCoupon userCoupon = refreshListDate.getDataList().get(position);
						mUserService.deleteMyCoupon(userCoupon.getActionId(), callBack);

					}
				}
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		dialog.show();
	}

	/** item事件的点击监听 */
	private OnItemClickListener onClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			// TODO Auto-generated method stub
			if (refreshListDate != null) {
				if (!refreshListDate.isEmpty()) {
					if (position >= 0 && position < refreshListDate.getDataList().size()) {
						ArrayList<UserCouponParcelable> newList = new ArrayList<UserCouponParcelable>();
						for(int i=0;i<refreshListDate.getDataList().size();i++){
							UserCoupon coupon = new UserCoupon();
							coupon = refreshListDate.getDataList().get(i);
							UserCouponParcelable userCoupon = new UserCouponParcelable();
							userCoupon.name = coupon.getName();
							userCoupon.description = coupon.getDescription();
							userCoupon.imageUrl = coupon.getImageUrl();
							userCoupon.mId = coupon.getActionId();
							userCoupon.beginTime = coupon.getBeginTime();
							userCoupon.deadlineTime = coupon.getDeadlineTime();
							newList.add(userCoupon);
						}
						Intent intent = new Intent(mContext, MyCouponInfoActivity.class);
						intent.putExtra(BaseActivity.ACTIVITY_EXTRA, position);
						intent.putParcelableArrayListExtra(BaseActivity.ACTIVITY_EXTRA2, newList);
						mContext.startActivity(intent);
					}
				}
			}
		}
	};

	/** item的长按响应 */
	private OnItemLongClickListener longClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
			// TODO Auto-generated method stub
			showDialog(position);
			return false;
		}
	};

	/** 服务器请求的回调 */
	private IHttpRequestCallback callBack = new IHttpRequestCallback() {

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub
			isLastPage = false;
			isRequestFinish = false;
		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub
			if (protocol.isHttpSuccess()) {
				if (protocol.getProtocolType() == ProtocolType.GET_MYCOUPON_LIST_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						GetMyCouponListProtocol p = (GetMyCouponListProtocol) protocol;
						if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
							couponListView.deleteLoading();
							couponListView.updateListView();
							((WatchdogMainActivity) mContext).setResultCount(0);
							isLastPage = true;
						} else {
							refreshListDate = p.getDataList();
							ArrayList<UserCoupon> newDataList = refreshListDate.getDataList();
							listCount = newDataList.size();
							((WatchdogMainActivity) mContext).setResultCount(listCount);
							for (int i = couponListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
								couponListView.mAlreadyLoadData.add(newDataList.get(i));
								if (i != 0 && i == newDataList.size() - 1) {
									lastTimestamp = newDataList.get(i).getReceiveTime();
								}
							}
							couponListView.updateListView();

							if (p.getDataList().isLastPage()) {
								couponListView.deleteLoading();
								isLastPage = true;
							}
						}

					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
						couponListView.deleteLoading();
					}
					if (couponListView.mAlreadyLoadData.isEmpty()) {
						showContentEmpty(true, false);
					} else {
						showContentEmpty(false, false);
					}
				} else if (protocol.getProtocolType() == ProtocolType.DELETE_MYCOUPON_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						HaloToast.show(mContext, mContext.getText(R.string.delete_success).toString());
						couponListView.mAlreadyLoadData.clear();
						couponListView.addLoading();
						mUserService.getMyCouponList(true, callBack);
					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
					}
				} else if (protocol.getProtocolType() == ProtocolType.SEARCH_CARD_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						SearchCardProtocol p = (SearchCardProtocol) protocol;
						if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
							couponListView.deleteLoading();
							couponListView.updateListView();
							((WatchdogMainActivity) mContext).setResultCount(0);
							isLastPage = true;
						} else {
							refreshListDate = p.getDataList();
							ArrayList<UserCoupon> newDataList = refreshListDate.getDataList();
							listCount = newDataList.size();
							((WatchdogMainActivity) mContext).setResultCount(listCount);
							for (int i = couponListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
								couponListView.mAlreadyLoadData.add(newDataList.get(i));
								if (i != 0 && i == newDataList.size() - 1) {
									lastTimestamp = newDataList.get(i).getReceiveTime();
								}
							}
							couponListView.updateListView();

							if (p.getDataList().isLastPage()) {
								couponListView.deleteLoading();
								isLastPage = true;
							}
						}

					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
						couponListView.deleteLoading();
					}
					if (couponListView.mAlreadyLoadData.isEmpty()) {
						showContentEmpty(true, false);
					} else {
						showContentEmpty(false, false);
					}
				}

			} else {
				couponListView.deleteLoading();
				if (couponListView.mAlreadyLoadData.isEmpty()) {
					showContentEmpty(true, true);
				} else {
					showContentEmpty(false, false);
				}
			}
			mLoadingbr.setVisibility(View.GONE);
			couponListView.setVisibility(View.VISIBLE);
			couponListView.isLoading(false);
			couponListView.setVisibility(View.VISIBLE);
		}
	};

	/**
	 * @Title: showContentEmpty
	 * @Description:如果数据为空的时候，显示提示
	 * @param bl
	 *            是否显示提示 currentViewType 当前view的类型
	 * @return void
	 */
	private void showContentEmpty(boolean bl, boolean noNet) {
		TextView txt = (TextView) mContentView.findViewById(R.id.card_case_txt_empty);
		txt.setVisibility(bl ? View.VISIBLE : View.GONE);
		if (noNet) {
			txt.setText(R.string.myzhima_request_failed);
		} else {
			txt.setText(R.string.no_favorite_card);
		}

		couponListView.setVisibility(bl ? View.GONE : View.VISIBLE);
	}
}

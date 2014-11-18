package com.zhima.ui.usercenter.watchdog.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.PersonRecordType;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.TargetType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetUserRecordListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserRecordEntry;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomListView;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName FavoriteView
 * @Description TODO
 * @author jiang
 * @date 2012-8-29 下午06:19:12
 */
public class FavoriteView extends View {
	private Context mContext;
	private View mContentView;
	public static int FAVORITE = 0;
	public static int FAVORITE_IDO = 1;
	public static int SCANCODE_RECORD = 2;
	private CustomListView<UserRecordEntry> favoriteListView;
	private RefreshListData<UserRecordEntry> refreshListDate;
	private UserService mUserService;
	private Button commodityBtn, idoBtn;
	private LinearLayout mLoadingbr;
	private int CurrentViewType = -1;
	public static long lastTimestamp = 0;
	/** 是否最后一页 */
	private boolean isLastPage = false;
	/** 是否结束请求服务器 */
	private boolean isRequestFinish = false;
	private View elseView;

	public FavoriteView(Context context) {
		super(context);
		this.mContext = context;
		mContentView = View.inflate(mContext, R.layout.myzhima_favorite_view, null);
		elseView = View.inflate(mContext, R.layout.customlist_else_view, null);

		init();

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
	 * @Title: init
	 * @Description:初始化变量和view
	 * @param
	 * @return void
	 */
	private void init() {
		mUserService = UserService.getInstance(mContext);
		mLoadingbr = (LinearLayout) mContentView.findViewById(R.id.basic_view_loading);
		favoriteListView = (CustomListView<UserRecordEntry>) mContentView.findViewById(R.id.list_favorite);
		favoriteListView.setOnItemClickListener(onClick);
		favoriteListView.setOnItemLongClickListener(longClick);
		commodityBtn = (Button) mContentView.findViewById(R.id.btn_office);
		idoBtn = (Button) mContentView.findViewById(R.id.btn_commerce);
		idoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				setTabLine(false);
				requestServiceData(FAVORITE_IDO);
			}
		});
		commodityBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				setTabLine(true);
				requestServiceData(FAVORITE);
			}
		});
		setTabLine(true);
	}

	/**
	 * @Title: requestServiceData
	 * @Description:请求数据并填充
	 * @param
	 * @return void
	 */
	public void requestServiceData(int viewTag) {
		favoriteListView.mAlreadyLoadData.clear();
		favoriteListView.updateListView();
		favoriteListView.isLoading(false);
		showContentEmpty(false, CurrentViewType, false);
		if (viewTag == FAVORITE) {
			CurrentViewType = PersonRecordType.FAVORITE;
			refreshListDate = mUserService.getCacheUserRecordList(PersonRecordType.FAVORITE);
			favoriteListView.addLoading();
			setListBatchLoad(PersonRecordType.FAVORITE);
			if (refreshListDate.isEmpty()) {
				favoriteListView.setVisibility(View.GONE);
				// int count = favoriteListView.getFooterViewsCount();
				// if(count>0){
				// for(int i =0;i<count-1;i++){
				// favoriteListView.removeFooterView(elseView);
				// favoriteListView.isAddSecondFooterView(false);
				// }
				// }
				mLoadingbr.setVisibility(View.VISIBLE);
				mUserService.getUserRecordList(PersonRecordType.FAVORITE, getBeginTime(), getEndTime(), 0, true,
						callBack);
			} else {
				addCacheData();
			}
			MyZhimaViewflowAdapter.ISFIRST_FAVORITE = false;
		} else if (viewTag == SCANCODE_RECORD) {
			CurrentViewType = PersonRecordType.SCANNING;
			refreshListDate = mUserService.getCacheUserRecordList(PersonRecordType.SCANNING);
			favoriteListView.addLoading();
			setListBatchLoad(PersonRecordType.SCANNING);
			if (refreshListDate.isEmpty()) {
				favoriteListView.setVisibility(View.GONE);
				mLoadingbr.setVisibility(View.VISIBLE);
				mUserService.getUserRecordList(PersonRecordType.SCANNING, getBeginTime(), getEndTime(), 0, true,
						callBack);
				favoriteListView.addLoading();
			} else {
				addCacheData();
			}
			MyZhimaViewflowAdapter.ISFIRST_RECORD = false;
		} else if (viewTag == FAVORITE_IDO) {
			CurrentViewType = PersonRecordType.IDOL;
			refreshListDate = mUserService.getCacheUserRecordList(PersonRecordType.IDOL);
			favoriteListView.addLoading();
			setListBatchLoad(PersonRecordType.IDOL);
			if (refreshListDate.isEmpty()) {
				int count = favoriteListView.getFooterViewsCount();
				if (count > 0) {
					for (int i = 0; i < count - 1; i++) {
						favoriteListView.removeFooterView(elseView);
					}
				}
				favoriteListView.setVisibility(View.GONE);
				mLoadingbr.setVisibility(View.VISIBLE);
				mUserService.getUserRecordList(PersonRecordType.IDOL, getBeginTime(), getEndTime(), 0, true, callBack);
			} else {
				addCacheData();
			}
		}
	}

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

	private long getEndTime() {
		long now = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		c.add(Calendar.DAY_OF_YEAR, 1);
		return c.getTimeInMillis();
	}

	/**
	 * @Title: addCacheData
	 * @Description:添加本地缓存数据到view
	 * @param
	 * @return void
	 */
	private void addCacheData() {
		ArrayList<UserRecordEntry> newDataList = refreshListDate.getDataList();
		for (int i = favoriteListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
			favoriteListView.mAlreadyLoadData.add(newDataList.get(i));
		}
		favoriteListView.updateListView();
		favoriteListView.setVisibility(View.VISIBLE);
		favoriteListView.deleteLoading();
	}

	/**
	 * @Title: setListBatchLoad
	 * @Description:设置分批加载
	 * @param
	 * @return void
	 */
	private void setListBatchLoad(final int recordType) {
		favoriteListView.setBatchLoad(new ZhimaAdapter<UserRecordEntry>(mContext, R.layout.space_joke_list_item,
				favoriteListView.mAlreadyLoadData) {

			@Override
			public Object createViewHolder(View view, UserRecordEntry data) {
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
			public void bindView(UserRecordEntry data, int position, View view) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				UserRecordEntry favoite = (UserRecordEntry) data;
				String caption = favoite.getTitle();
				if (caption.equals("")) {
					caption = "无标题" + position;
				} else {
					if (favoite.getTargetType() == TargetType.TARGET_TYPE_UNKNOWN_SPACE) {
						if (!TextUtils.isEmpty(favoite.getContent())) {
							caption = favoite.getContent();
						}
					}
				}
				holder.mTitleText.setText(caption);
				holder.mReplyText.setVisibility(View.GONE);
				// 发布日期
				holder.mDateText.setText(new SimpleDateFormat("MM-dd HH:mm").format(favoite.getCreateTime()));
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
				favoriteListView.isLoading(true);
				favoriteListView.addLoading();

				mUserService
						.getUserRecordList(recordType, getBeginTime(), getEndTime(), lastTimestamp, false, callBack);
			}

			class ViewHolder {
				ImageView mImageView;
				TextView mTitleText;
				TextView mDateText;
				RelativeLayout mReplyText;
			}

		});
	}

	/** item事件的点击监听 */
	private OnItemClickListener onClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			// TODO Auto-generated method stub
			if (refreshListDate != null) {
				if (!refreshListDate.isEmpty()) {
					if (position >= 0 && position < refreshListDate.getDataList().size()) {
						UserRecordEntry userRecord = refreshListDate.getDataList().get(position);
						if (userRecord.getTargetType() == TargetType.TARGET_TYPE_UNKNOWN_SPACE) {
							ActivitySwitcher.openSpaceActivity(mContext, userRecord.getTargetType(),
									userRecord.getContent());
						} else {
							ActivitySwitcher.openSpaceActivity(mContext, userRecord.getContent(),
									userRecord.getTargetId(), userRecord.getTargetType(), false);
						}
					}
				}
			}
		}
	};

	/**
	 * @Title: showDialog
	 * @Description:弹出删除的对话框
	 * @param position 被点击的item的position
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
						UserRecordEntry userRecord = refreshListDate.getDataList().get(position);
						if (CurrentViewType == PersonRecordType.SCANNING) {
							mUserService.deleteUserRecord(userRecord.getRecordType(), userRecord.getId(), callBack);
						} else {
							mUserService.deleteUserRecord(PersonRecordType.FAVORITE, userRecord.getId(), callBack);
						}

					}
				}
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		dialog.show();
	}

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
				if (protocol.getProtocolType() == ProtocolType.GET_USER_RECORD_LIST_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						GetUserRecordListProtocol p = (GetUserRecordListProtocol) protocol;
						if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
							favoriteListView.deleteLoading();
							favoriteListView.updateListView();
							isLastPage = true;
							favoriteListView.addFooterView(elseView, null, false);
							favoriteListView.isAddSecondFooterView(true);
						} else {
							refreshListDate = p.getDataList();
							ArrayList<UserRecordEntry> newDataList = refreshListDate.getDataList();
							for (int i = favoriteListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
								favoriteListView.mAlreadyLoadData.add(newDataList.get(i));
								if (i != 0 && i == newDataList.size() - 1) {
									lastTimestamp = newDataList.get(i).getCreateTime();
								}
							}
							favoriteListView.updateListView();

							if (p.getDataList().isLastPage()) {
								favoriteListView.deleteLoading();
								isLastPage = true;
								favoriteListView.addFooterView(elseView, null, false);
								favoriteListView.isAddSecondFooterView(true);
							}
						}

					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
						favoriteListView.deleteLoading();
					}
					if (favoriteListView.mAlreadyLoadData.isEmpty()) {
						showContentEmpty(true, CurrentViewType, false);
					} else {
						showContentEmpty(false, CurrentViewType, false);
					}
				} else if (protocol.getProtocolType() == ProtocolType.DELETE_USER_RECORD_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						HaloToast.show(mContext, mContext.getText(R.string.delete_success).toString());
						favoriteListView.mAlreadyLoadData.clear();
						favoriteListView.addLoading();
						mUserService
								.getUserRecordList(CurrentViewType, getBeginTime(), getEndTime(), 0, true, callBack);
					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
					}
				}

			} else {
				favoriteListView.deleteLoading();
				if (favoriteListView.mAlreadyLoadData.isEmpty()) {
					showContentEmpty(true, CurrentViewType, true);
				} else {
					showContentEmpty(false, CurrentViewType, false);
				}
			}
			mLoadingbr.setVisibility(View.GONE);
			favoriteListView.setVisibility(View.VISIBLE);
			favoriteListView.isLoading(false);
			favoriteListView.setVisibility(View.VISIBLE);
		}
	};

	/**
	 * @Title: setTabLine
	 * @Description:根据type设置官方公告和商户公告显示下划线
	 * @param isLeft 是否是左边的item
	 * @return void
	 */
	public void setTabLine(boolean isLeft) {
		if (isLeft) {
			CurrentViewType = PersonRecordType.FAVORITE;
			commodityBtn.setBackgroundColor(Color.WHITE);
			idoBtn.setBackgroundResource(R.drawable.favorite_title_bg_first);
		} else {
			CurrentViewType = PersonRecordType.IDOL;
			commodityBtn.setBackgroundResource(R.drawable.favorite_title_bg_first);
			idoBtn.setBackgroundColor(Color.WHITE);
		}
	}

	/**
	 * @Title: showContentEmpty
	 * @Description:如果数据为空的时候，显示提示
	 * @param bl 是否显示提示 currentViewType 当前view的类型
	 * @return void
	 */
	private void showContentEmpty(boolean bl, int currentViewType, boolean noNet) {
		TextView txt = (TextView) mContentView.findViewById(R.id.txt_empty);
		txt.setVisibility(bl ? View.VISIBLE : View.GONE);
		if (noNet) {
			txt.setText(R.string.myzhima_request_failed);
		} else {
			if (currentViewType == PersonRecordType.FAVORITE) {
				txt.setText(R.string.no_favorite_goods);
			} else if (currentViewType == PersonRecordType.IDOL) {
				txt.setText(R.string.no_favorite_ido);
			} else if (currentViewType == PersonRecordType.SCANNING) {
				txt.setText(R.string.no_favorite_record);
			}
		}

		favoriteListView.setVisibility(bl ? View.GONE : View.VISIBLE);
	}
}

package com.zhima.plugin.space.couples.controller;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceFocusImagesProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.SpaceDoPraiseProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.adapter.AlbumAdapter;
import com.zhima.plugin.space.couples.CouplesHeaderViewPlugin;
import com.zhima.plugin.space.couples.adapter.CouplesAlbumAdapter;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.diary.activity.DiaryEditActivity;
import com.zhima.ui.diary.activity.DiaryShareActivity;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.retrieval.activity.RetrievalMainActivity;
import com.zhima.ui.space.zmspace.activity.CoupleIntroActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpaceDiaryListActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpaceVedioImageActivity;
import com.zhima.ui.space.zmspace.activity.CoupleIntroActivity;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.IntentSpanStringBuilder;
import com.zhima.ui.tools.UserUtils;

public class CouplesHeaderPluginController extends BaseViewPluginController {

	private int maxListSize = SystemConfig.VIEW_PLUGIN_HEADER_MAX_COUNT;
	private int mDefalutAlbumResId = R.drawable.couples_album_default;

	private ZMCouplesObject mZMCouplesObject;
	private CouplesHeaderViewPlugin viewPlugin;

	private RefreshListData<ZMObjectImage> mAlbumRefreshList;
	// private ArrayList<ZMObjectImage> mDataList;
	private CouplesAlbumAdapter mCouplesAlbumAdapter;

	private ZMSpaceService mZMSpaceService;
	private HttpImageLoader mHttpImageLoader;

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	ImageView mDragItemView;
	/** WindowManager对象 */
	private WindowManager mWindowManager;

	private boolean isDurationt = false;
	private boolean isUp;

	private boolean isStartActivity = false;
	private int mProgress;

	public CouplesHeaderPluginController(BaseActivity activity, CouplesHeaderViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		this.viewPlugin = viewPlugin;
		mHttpImageLoader = HttpImageLoader.getInstance(mParentActivity);
		mZMSpaceService = ZMSpaceService.getInstance(mParentActivity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMCouplesObject = (ZMCouplesObject) zmObject;

		mAlbumRefreshList = mZMSpaceService.getCacheFocusImages(mZMCouplesObject);
		if (mAlbumRefreshList.isEmpty()) {
			mZMSpaceService.getFocusImages(mZMCouplesObject, false, this);
		} else {
			setAlbumView(mAlbumRefreshList.getDataList());
		}

		setView();
		setListener();

	}

	private void setListener() {
		setSeekBarListener();
		viewPlugin.setOnClickListener(mMultimediaClickListener);
		viewPlugin.setPhotoClicker(photoClickListener);
		viewPlugin.setNameClickListener(nameClickListener);
	}

	// 设置基本信息
	private void setView() {
		viewPlugin.setName(mZMCouplesObject.getName());
		viewPlugin.setZMID("ID:" + mZMCouplesObject.getZMID());
		viewPlugin.setSignature(mZMCouplesObject.getLoveVow());
		viewPlugin.setDefalutBackgroundResource(mDefalutAlbumResId);

		User user = mZMCouplesObject.getUser();
		if (user != null) {
			String name = user.getNickname();
			if (!TextUtils.isEmpty(name)) {
				int color = mParentActivity.getResources().getColor(R.color.black_color);
				SpannableString createClickSpanString = IntentSpanStringBuilder.createClickSpanString(mParentActivity,
						name, name, color, false, new MyClickableSpan(user.getUserId()));
				viewPlugin.setOwnerView(createClickSpanString);
			}
		}

		mHttpImageLoader.loadImage(mZMCouplesObject.getImageUrl(), viewPlugin.getPhotoView(),
				mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
	}

	// 设置图片展示区
	private void setAlbumView(ArrayList<ZMObjectImage> data) {
		if (mCouplesAlbumAdapter == null) {
			mCouplesAlbumAdapter = new CouplesAlbumAdapter(mParentActivity, data, 1, maxListSize);
			viewPlugin.setAdapter(mCouplesAlbumAdapter);
		} else {
			mCouplesAlbumAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 加赞
	 */
	private void addPraiseClick() {
		if (mZMCouplesObject != null) {
			startWaitingDialog();
			mZMSpaceService.doPraise(mZMCouplesObject, this);
		} else {
			ErrorManager.showErrorMessage(mParentActivity);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SPACE_FOCUS_IMAGES_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetSpaceFocusImagesProtocol p = (GetSpaceFocusImagesProtocol) protocol;
					mAlbumRefreshList = p.getDataList();
					setAlbumView(mAlbumRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					SpaceDoPraiseProtocol p = (SpaceDoPraiseProtocol) protocol;
					HaloToast.show(mParentActivity, "加赞成功");
				} else {
					HaloToast.show(mParentActivity, protocol.getProtocolErrorMessage());
				}
			}
		} else {
			if (protocol.getProtocolType() == ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL) {
				ErrorManager.showErrorMessage(mParentActivity);
			}
		}
	}

	/**
	 * 点击进入详情列表
	 */
	private OnClickListener mMultimediaClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mParentActivity, ZmSpaceVedioImageActivity.class);
			intent.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMCouplesObject.getId());
			startActivity(intent);
		}
	};

	/**
	 * 点击个人简介
	 */
	private OnClickListener photoClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent it = new Intent(mParentActivity, CoupleIntroActivity.class);
			it.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMCouplesObject.getId());
			startActivity(it);
		}
	};
	/**
	 * 拥有者空间
	 */
	private OnClickListener nameClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UserUtils userUtils = new UserUtils(mParentActivity);
			User user = mZMCouplesObject.getUser();
			if (user != null) {
				userUtils.switchAcitivity(user.getUserId(),false);
			}
		}
	};

	private class MyClickableSpan extends ClickableSpan {
		long id;

		public MyClickableSpan(long id) {
			super();
			this.id = id;
		}

		@Override
		public void onClick(View widget) {
			UserUtils user = new UserUtils(mParentActivity);
			user.switchAcitivity(id, false);
		}

	}

	// ------------------------------------------------------
	private void setSeekBarListener() {
		viewPlugin.getSeekBar().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				// TODO Auto-generated method stub
				int x = (int) ev.getRawX();
				int y = (int) ev.getRawY();
				if (!isDurationt) {
					switch (ev.getAction()) {

					case MotionEvent.ACTION_DOWN:
						xDistance = yDistance = 0f;
						xLast = ev.getX();
						yLast = ev.getY();
						startDragging((int) x, (int) y);
						break;
					case MotionEvent.ACTION_MOVE:
						dragView((int) x, (int) y - 100);

						final float curX = ev.getX();
						final float curY = ev.getY();

						if (curY - yLast > 0) {
							isUp = false;
						} else {
							isUp = true;
						}
						xDistance += Math.abs(curX - xLast);
						yDistance += Math.abs(curY - yLast);
						xLast = curX;
						yLast = curY;

						break;
					case MotionEvent.ACTION_UP:
						// stopDragging();
						float pxfromDip = GraphicUtils.dip2px(mParentActivity, 100);

						if (xDistance > yDistance) {
							// scrollDragging((int) x, (int) y, 300, 0, 0);
							// return false;
						} else if (yDistance > pxfromDip) {
							stopDragging();
							if (isUp) {
								// up进入日志页面
								if (!isStartActivity) {
									isStartActivity = true;
									Intent diaryIntent = new Intent(mParentActivity, ZmSpaceDiaryListActivity.class);
									diaryIntent.putExtra(mParentActivity.ACTIVITY_EXTRA, mZMCouplesObject.getId());
									startActivity(diaryIntent);
								}
								// scrollDragging((int) x, (int) y, 0, 300, 0);
							} else {
								// down进入影像界面
								if (!isStartActivity) {
									isStartActivity = true;
									Intent vedioImageIntent = new Intent(mParentActivity,
											ZmSpaceVedioImageActivity.class);
									vedioImageIntent.putExtra(mParentActivity.ACTIVITY_EXTRA, mZMCouplesObject.getId());
									startActivity(vedioImageIntent);
//									mParentActivity.overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
									mParentActivity.overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
									// scrollDragging((int) x, (int) y, 0, 800,
									// 0);
								}
							}
							//
						}
						stopDragging();
						break;
					case MotionEvent.ACTION_CANCEL:
						stopDragging();

						break;
					default:
						break;
					}
				}

				return false;
			}
		});

		viewPlugin.getSeekBar().setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				if (mProgress < 15) {
					if (!isStartActivity) {
						isStartActivity = true;
						Intent retrievalIntent = new Intent(mParentActivity, RetrievalMainActivity.class);
						retrievalIntent.putExtra(mParentActivity.ACTIVITY_EXTRA, mZMCouplesObject.getId());
						startActivity(retrievalIntent);
						mParentActivity.overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
					}
				} else if (mProgress > 85) {
					// TODO 加赞
					addPraiseClick();
					viewPlugin.getSeekBar().setProgress(50);
				} else {
					viewPlugin.getSeekBar().setProgress(50);
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
				mProgress = progress;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		viewPlugin.getSeekBar().setProgress(50);
		isStartActivity = false;
	}

	private ImageView getDragView() {
		ImageView iv = new ImageView(mParentActivity);
		iv.setBackgroundResource(R.drawable.seekbar_center_icon);
		return iv;
	}

	private void startDragging(int x, int y) {
//		System.out.println("x:" + x + ",y:" + y);
		stopDragging();
		mDragItemView = getDragView();
		WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowParams.x = x - mDragItemView.getWidth() / 2;
		mWindowParams.y = y - mDragItemView.getHeight() / 2;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		// mDragBitmap = bm;

		mWindowManager = (WindowManager) mParentActivity.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(mDragItemView, mWindowParams);
	}

	private void dragView(int x, int y) {
		// 这里的判断是当我们开启SmoothScrollRunnable，handler在发送异步消息的时候，y条件以满足。会调用stop（）；所以需要判断或优化
		if (mDragItemView != null) {
			WindowManager.LayoutParams mWindowParams = (WindowManager.LayoutParams) mDragItemView.getLayoutParams();
			mWindowParams.x = x - mDragItemView.getWidth() / 2;
			mWindowParams.y = y - mDragItemView.getHeight() / 2;
			mWindowManager.updateViewLayout(mDragItemView, mWindowParams);
		}
	}

	private void stopDragging() {
		if (mDragItemView != null && mWindowManager != null) {
			mWindowManager.removeView(mDragItemView);
			mDragItemView = null;
		}
		isDurationt = false;
	}

	// private final Handler mHandler = new Handler();

	// private void scrollDragging(int startX, int startY, int dx, int dy, int
	// durationt) {
	// startDragging(startX, startY);
	// SmoothScrollRunnable mCurrentSmoothScrollRunnable = new
	// SmoothScrollRunnable(mHandler, startX, startY, dx, dy);
	// mHandler.post(mCurrentSmoothScrollRunnable);
	// }

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DURATION_MS = 1900;
		static final int ANIMATION_FPS = 1000 / 1000;

		private final Interpolator mInterpolator;
		private final int mScrollToX;
		private final int mScrollFromX;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final Handler mHandler;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(Handler handler, int startX, int startY, int dx, int dy) {
			mHandler = handler;
			mScrollFromX = startX;
			mScrollToX = dx;
			mScrollFromY = startY;
			mScrollToY = dy;
			mInterpolator = new AccelerateDecelerateInterpolator();
		}

		@Override
		public void run() {
			isDurationt = true;
			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY)
						* mInterpolator.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				dragView(mScrollFromX, mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY > mCurrentY) {
				mHandler.postDelayed(this, ANIMATION_FPS);
			} else {
				stop();
			}
		}

		public void stop() {

			mContinueRunning = false;
			mHandler.removeCallbacks(this);
			stopDragging();
		}
	}
}

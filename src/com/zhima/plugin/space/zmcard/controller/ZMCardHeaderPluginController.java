package com.zhima.plugin.space.zmcard.controller;

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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.zhima.data.model.ZMCardObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.adapter.AlbumAdapter;
import com.zhima.plugin.space.zmcard.activity.ZMCardHeaderViewPlugin;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.retrieval.activity.RetrievalMainActivity;
import com.zhima.ui.space.zmspace.activity.ZmCardIntroActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpaceDiaryListActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpaceVedioImageActivity;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.IntentSpanStringBuilder;
import com.zhima.ui.tools.UserUtils;

public class ZMCardHeaderPluginController extends BaseViewPluginController {

	private int maxListSize = SystemConfig.VIEW_PLUGIN_HEADER_MAX_COUNT;
	//TODO 设置默认图片
	private int mDefalutAlbumResId = R.drawable.organization_album_default;

	private ZMCardObject mZMcardObject;
	private ZMCardHeaderViewPlugin viewPlugin;

	private RefreshListData<ZMObjectImage> mAlbumRefreshList;
	private ArrayList<ZMObjectImage> mDataList;
	private AlbumAdapter mCouplesAlbumAdapter;

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

	public ZMCardHeaderPluginController(BaseActivity activity, ZMCardHeaderViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		this.viewPlugin = viewPlugin;
		mHttpImageLoader = HttpImageLoader.getInstance(mParentActivity);
		mZMSpaceService = ZMSpaceService.getInstance(mParentActivity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMcardObject = (ZMCardObject) zmObject;

		mAlbumRefreshList = mZMSpaceService.getCacheFocusImages(mZMcardObject);
		if (mAlbumRefreshList.isEmpty()) {
			mZMSpaceService.getFocusImages(mZMcardObject, false, this);
		} else {
			setAlbumView(mAlbumRefreshList.getDataList());
		}
		setView();
		setListener();

	}

	private void setListener() {
		setSeekBarListenr();
		viewPlugin.setOnClickListener(mMultimediaClickListener);
		viewPlugin.setPhotoClicker(photoClickListener);
		viewPlugin.setNameClickListener(nameClickListener);
	}

	//设置基本信息
	private void setView() {
		viewPlugin.setName(mZMcardObject.getName());
		viewPlugin.setZMID("ID:" + mZMcardObject.getZMID());
		viewPlugin.setSignature(mZMcardObject.getSignature());
		viewPlugin.setDefalutBackgroundResource(mDefalutAlbumResId);

		User user = mZMcardObject.getUser();
		if (user != null) {
			String name = user.getNickname();
			if (!TextUtils.isEmpty(name)) {
				int color = mParentActivity.getResources().getColor(R.color.black_color);
				SpannableString createClickSpanString = IntentSpanStringBuilder.createClickSpanString(mParentActivity,
						name, name, color, false, new MyClickableSpan(user.getUserId()));
				viewPlugin.setOwnerView(createClickSpanString);
			}
		}

		mHttpImageLoader.loadImage(mZMcardObject.getImageUrl(), viewPlugin.getPhotoView(),
				mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
	}

	//设置图片展示区
	private void setAlbumView(ArrayList<ZMObjectImage> dataList) {
		mDataList = mAlbumRefreshList.getDataList();
		if (mCouplesAlbumAdapter == null) {
			mCouplesAlbumAdapter = new AlbumAdapter(mParentActivity, mDataList, mDefalutAlbumResId, 1, maxListSize);
			viewPlugin.setAdapter(mCouplesAlbumAdapter);
		} else {
			mCouplesAlbumAdapter.notifyDataSetChanged();
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
			intent.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMcardObject.getId());
			startActivity(intent);
		}
	};

	/**
	 * 点击个人简介
	 */
	private OnClickListener photoClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent it = new Intent(mParentActivity, ZmCardIntroActivity.class);
			it.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMcardObject.getId());
			startActivity(it);
		}
	};

	/**
	 * 拥有者空间
	 */
	private OnClickListener nameClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UserUtils ut = new UserUtils(mParentActivity);
			User user = mZMcardObject.getUser();
			if (user != null) {
				ut.switchAcitivity(user.getUserId(), false);
			}
		}
	};

	/**
	 * 加赞
	 */
	private void addPraiseClick() {
		if (mZMcardObject != null) {
			startWaitingDialog();
			mZMSpaceService.doPraise(mZMcardObject, this);
		} else {
			ErrorManager.showErrorMessage(mParentActivity);
		}
	}

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

	//------------------------------------------------------
	private void setSeekBarListenr() {
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
						float pxfromDip = GraphicUtils.dip2px(mParentActivity, 120);

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
									diaryIntent.putExtra(mParentActivity.ACTIVITY_EXTRA, mZMcardObject.getId());
									startActivity(diaryIntent);
								}
								// scrollDragging((int) x, (int) y, 0, 300, 0);
							} else {
								// down进入影像界面
								if (!isStartActivity) {
									isStartActivity = true;
									Intent vedioImageIntent = new Intent(mParentActivity,
											ZmSpaceVedioImageActivity.class);
									vedioImageIntent.putExtra(mParentActivity.ACTIVITY_EXTRA, mZMcardObject.getId());
									startActivity(vedioImageIntent);
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
				viewPlugin.getSeekBar().setClickable(false);
				viewPlugin.getSeekBar().setFocusable(false);
				if (mProgress < 15) { 
					if (!isStartActivity) {
						isStartActivity = true;
						Intent retrievalIntent = new Intent(mParentActivity, RetrievalMainActivity.class);
						retrievalIntent.putExtra(mParentActivity.ACTIVITY_EXTRA, mZMcardObject.getId());
						startActivity(retrievalIntent);
					}
				} else if (mProgress > 90) {
					//TODO 加赞
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
		iv.setBackgroundResource(R.drawable.card_space_center_gif);
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

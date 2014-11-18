package com.zhima.ui.space.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ViewPluginKind;
import com.zhima.base.error.ViewPluginException;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.data.model.ZMCardObject;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMOrganizationObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.LayoutBaseViewPlugin;
import com.zhima.plugin.TopbarBaseViewPlugin;
import com.zhima.plugin.ViewPluginActivityHandler;
import com.zhima.plugin.ViewPluginConfig;
import com.zhima.plugin.ViewPluginDefine;
import com.zhima.plugin.ViewPluginFactory;
import com.zhima.plugin.ViewPluginManager;
import com.zhima.plugin.ViewPluginStyle;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.space.zmspace.activity.ZmSpacePlaza2Activity;
import com.zhima.ui.space.zmspace.activity.ZmSpacePlazaActivity;

/**
 * @ClassName: ViewPluginBaseActivity
 * @Description: 插件
 * @author luqilong
 * @date 2013-1-3 上午10:04:58
 * 
 */
public class ViewPluginBaseActivity extends BaseActivity {

	private static final String TAG = ViewPluginBaseActivity.class.getSimpleName();

	private ZMObject zmObject;
	private ViewPluginConfig mPluginConfig;
	private ScanningcodeService mScanningcodeService;

	// 插件view管理器
	private ViewPluginManager mViewPluginManager;
	// 样式
	private ViewPluginStyle mViewPluginStyle = null;

	private final int NONE = 0;
	private final int DRAG = 1;
	private final int ZOOM = 2;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist;

	int mode = NONE;
	private boolean isStartActivity = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPluginManager = new ViewPluginManager(this);
		this.setPluginHandler(new ViewPluginActivityHandler(this));
		// 得到对象
		Intent intent = getIntent();
		long remoteId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		int zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);

		if (remoteId != -1 && zmObjectType != -1) {
			mScanningcodeService = ScanningcodeService.getInstance(this);
			zmObject = mScanningcodeService.getCacheZMObject(remoteId, zmObjectType);
			// XXX 进入空间必须初始化zmObject
			if (zmObject != null) {
				refreshView();
			} else {
				if(!SystemConfig.DEBUG){
					mScanningcodeService.getScanningInfo(remoteId, zmObjectType, this);
				}
			}
		}

	}

	private void refreshView() {
		String json = null;
		if (zmObject instanceof ZMCouplesObject) {
			json = ViewPluginFactory.createCoupleConfigJson();
		} else if (zmObject instanceof ZMOrganizationObject) {
			json = ViewPluginFactory.createFameConfigJson();
		} else if (zmObject instanceof ZMCardObject) {
			json = ViewPluginFactory.createZMCardConfigJson();
		}
		if (json == null) {
			throw new ViewPluginException("插件模块配置信息为null" + ",zmObject:" + zmObject);
		}
		getPluginConfig(json);
		// 获取所有插件配置
		List<ViewPluginDefine> viewPluginTypes = mPluginConfig.getPlugins();
		// 根据插件信息创建插件
		createPluginView(viewPluginTypes);
	
		//设置view
		View layout = mViewPluginManager.getLayout();
		setContentView(layout);
		loadDataPluginView();
		initTouchListener();
	}

	// 请求数据
	private void loadDataPluginView() {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin viewPlugin = mViewPluginManager.get(i);
			//TODO refreshed 控制
			viewPlugin.getPluginController().loadData(zmObject, false);
		}
	}

	// 根据插件类型创建插件
	private void createPluginView(List<ViewPluginDefine> viewPluginTypes) {
		mViewPluginManager.clear();

		// 样式
		TypedArray typedArray = null;
		// 从配置文件得到样式配置
		int styleResId = mPluginConfig.getStyle();
		if (styleResId != -1) {
			mViewPluginStyle = new ViewPluginStyle(this);
			mViewPluginStyle.loadStyle(styleResId);
			typedArray = mViewPluginStyle.getStyle();
		} else {
			Logger.getInstance(TAG).error("没有样式配置文件");
		}

		int size = viewPluginTypes.size();
		// 得到插件的个数
		Logger.getInstance(TAG).debug("view plugin size :" + size);
		for (int i = 0; i < size; i++) {
			ViewPluginDefine type = viewPluginTypes.get(i);
			BaseViewPlugin plugin = ViewPluginFactory.createViewPlugin(this, type);
			if (plugin != null) {
				// 设置样式
				if (typedArray != null) {
					plugin.setBaseSytle(typedArray);
				}
				mViewPluginManager.addViewPlugin(plugin);
			} else {
				Logger.getInstance(TAG).error("生成空间模块失败：" + type);
			}
		}

		if (!mViewPluginManager.isHasTopbar()) {
			TopbarBaseViewPlugin topbarViewPlugin = getDefaluTopbarViewPlugin();
			// 设置样式
			if (typedArray != null) {
				topbarViewPlugin.setStyle(typedArray);
			}
			mViewPluginManager.addTopbarViewPlugin(topbarViewPlugin);
			Logger.getInstance(TAG).debug("生成默认topbar");
		}

		if (!mViewPluginManager.isHasLayout()) {
			LayoutBaseViewPlugin layoutViewPlugin = getLayoutViewPlugin();
			// 设置样式
			if (typedArray != null) {
				layoutViewPlugin.setStyle(typedArray);
			}
			mViewPluginManager.addLayoutViewPlugin(layoutViewPlugin);
			Logger.getInstance(TAG).debug("生成默认layout");
		}
		mViewPluginManager.requestLayout();

	}

	// 创建默认的LayoutViewPlugin
	private LayoutBaseViewPlugin getLayoutViewPlugin() {
		// TODO 默认配置
		ViewPluginDefine defaultLayoutViewPlugin = new ViewPluginDefine();
		defaultLayoutViewPlugin.setName("默认空间");
		defaultLayoutViewPlugin.setPosition(Integer.MAX_VALUE);
		defaultLayoutViewPlugin.setTitle("空间");
		defaultLayoutViewPlugin.setType(ViewPluginDefine.PluginType.default_layout);
		return (LayoutBaseViewPlugin) ViewPluginFactory.createViewPlugin(this, defaultLayoutViewPlugin);
	}

	// 创建默认的TopbarViewPlugin
	private TopbarBaseViewPlugin getDefaluTopbarViewPlugin() {
		// TODO 默认配置
		ViewPluginDefine defaultTopbarViewPlugin = new ViewPluginDefine();
		defaultTopbarViewPlugin.setName("默认标题栏");
		defaultTopbarViewPlugin.setPosition(Integer.MAX_VALUE);
		defaultTopbarViewPlugin.setTitle("空间");
		defaultTopbarViewPlugin.setType(ViewPluginDefine.PluginType.default_topbar);
		return (TopbarBaseViewPlugin) ViewPluginFactory.createViewPlugin(this, defaultTopbarViewPlugin);
	}

	// 通过JSON得到插件配置
	private void getPluginConfig(String json) {
		mPluginConfig = new ViewPluginConfig();
		// XXX 解析配置文件
		try {
			JSONObject jsonObject = new JSONObject(json);
			// 解析样式
			if (!jsonObject.isNull("style")) {
				String style = jsonObject.getString("style");
				mPluginConfig.setStyle(ViewPluginKind.getStyle(style));
			} else {
				// 没有样式
				mPluginConfig.setStyle(-1);
			}
		} catch (JSONException e1) {
			Logger.getInstance(TAG).debug("解析配置文件失败");
		}

		// 通过Json解析模块
		try {
			List<ViewPluginDefine> plugins = getJsonProductPlugin(json);
			mPluginConfig.addPlugins(plugins);
		} catch (JSONException e) {
			throw new ViewPluginException("解析模块配置信息失败:" + json + ",error:" + e.getMessage());
			// Logger.getInstance(TAG).debug("解析模块失败" + e.getMessage());
		}
	}

	private List<ViewPluginDefine> getJsonProductPlugin(String json) throws JSONException {
		List<ViewPluginDefine> plugins = new ArrayList<ViewPluginDefine>();

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("items");

		int size = jsonArray.length();
		for (int i = 0; i < size; i++) {
			try {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				plugins.add(ViewPluginDefine.create(jsonObj));
			} catch (JSONException e) {
				Logger.getInstance(TAG).error("解析JSON模块时错误：" + e.getMessage());
				if (SystemConfig.DEBUG) {
					throw new ViewPluginException("解析JSON模块时错误：" + ",error:" + e.getMessage() + ",json:" + json);
				}
			}
		}
		// 排序
		Collections.sort(plugins, comp);
		return plugins;
	}

	// 插件排序:根据position
	private Comparator<ViewPluginDefine> comp = new Comparator<ViewPluginDefine>() {
		@Override
		public int compare(ViewPluginDefine o1, ViewPluginDefine o2) {
			int postion1 = o1.getPosition();
			int postion2 = o2.getPosition();
			if (postion1 > postion2) {
				return 1;
			} else if (postion1 == postion2) {
				return 0;
			} else {
				return -1;
			}
		}

	};

	// ---------------------------------------------------------

	@Override
	protected void onStart() {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onStart();
		}
		super.onStart();
	}

	@Override
	public void onPause() {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onPause();
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onStop();
		}
		super.onStop();
	}

	@Override
	public void onResume() {
		isStartActivity = false;
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onResume();
		}
		super.onResume();
	}

	@Override
	public void onRestart() {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onRestart();
		}
		super.onRestart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onDestroy() {
		int count = mViewPluginManager.size();
		for (int i = 0; i < count; i++) {
			BaseViewPlugin plugin = mViewPluginManager.get(i);
			plugin.getPluginController().onDestroy();
		}
		// XXX 释放
		mViewPluginManager.clear();
		if (mViewPluginStyle != null) {
			mViewPluginStyle.recycle();
		}
		super.onDestroy();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取zmobject
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
					zmObject = getzmProtocol.getZMObject();
					refreshView();
				}
			}
		} else {
			// TODO 网络访问错误
			HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 请求服务器前

	}

	// ----------------------------------------------------------------------
	// activity页面处理
//	/**
//	 * @Title: setSidebar
//	 * @Description:设置侧边栏
//	 * @param
//	 * @return void
//	 */
//	private void setSidebar() {
//		TopbarBaseViewPlugin topbarViewPlugin = mViewPluginManager.getTopbarPlugin();
//		String title = topbarViewPlugin.getTitle();
//		ZhimaTopbar topbar = topbarViewPlugin.getTopbar();
//		Logger.getInstance(TAG).debug("topbar title:" + topbar);
//		// XXX SideBarView得到ZhimaTopbar
//		// --》mChildView.findViewById(R.id.ztop_bar_layout);需要修改
//		// mViewPluginManager.getTopbarPlugin(); 这样不依赖id
//		mSideBarView = new SideBarView(this, this, title);
//		mSideBarView.setChildView(mViewPluginManager.getLayout());
//		setContentView(mSideBarView.getContentView());
//		final View tranView = mSideBarView.getContentView().findViewById(R.id.view_transparent_main);
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
//	}

//	private void setTopbar() {

//		TopbarBaseViewPlugin topbarViewPlugin = mViewPluginManager.getTopbarPlugin();
//		String title = topbarViewPlugin.getTitle();
//		ZhimaTopbar topbar = topbarViewPlugin.getTopbar();
//
//		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
//		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
//		topbar.addRightLayoutView(ll_right);
//		topbar.addLeftLayoutView(ll_left);
//		topbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				finish();
//			}
//		});
//		((TextView) topbar.findViewById(R.id.txt_topbar_title)).setText(title);
//	}

	/**
	 * @Title: initTouchListener
	 * @Description:设置整个view的手势操作监听，添加缩放手势的事件
	 * @param
	 * @return void
	 */
	private void initTouchListener() {
		// TODO Auto-generated method stub
		findViewById(R.id.layout_default).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				// 设置拖拉模式
				case MotionEvent.ACTION_DOWN:
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				// 设置多点触摸模式
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				// 若为DRAG模式，则点击移动图片
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						// // 设置位移
						//
						// matrix.postTranslate(event.getX() - start.x,
						//
						// event.getX() - start.x);

					}
					// 若为ZOOM模式，则多点触摸缩放
					else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							if (!isStartActivity) {
								isStartActivity = true;
								Intent intent = new Intent(ViewPluginBaseActivity.this, ZmSpacePlaza2Activity.class);
								intent.putExtra(ACTIVITY_EXTRA, zmObject.getId());
								startActivity(intent);
							}
							// matrix.set(savedMatrix);
							//
							// float scale = newDist / oldDist;
							//
							// // 设置缩放比例和图片中点位置
							//
							// matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}
				return true;
			}
		});
	}

	/**
	 * @Title: spacing
	 * @Description:计算移动距离
	 * @param
	 * @return float
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * @Title: midPoint
	 * @Description:计算中点位置
	 * @param
	 * @return void
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);

	}

	// -----------------------------------------------------------
	// 周期回调
	// @Override
	// public void onBackPressed() {
	// int count = mViewPluginManager.size();
	// for (int i = 0; i < count; i++) {
	// BaseViewPlugin viewPlugin = mViewPluginManager.get(i);
	// viewPlugin.getPluginController().onBackPressed();
	// }
	// super.onBackPressed();
	// }

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// boolean bl = false;
	// int count = mViewPluginManager.size();
	// for (int i = 0; i < count; i++) {
	// BaseViewPlugin plugin = mViewPluginManager.get(i);
	// //如果bl为false，则接受模块的返回信息，如果其中的模块返回true,则认为拦截不再接受返回的结果
	// if (bl) {
	// plugin.getPluginController().onTouchEvent(event);
	// } else {
	// bl = plugin.getPluginController().onTouchEvent(event);
	// }
	// }
	// return bl;
	// }

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// boolean bl = false;
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (mSideBarView != null && mSideBarView.onKeyBack()) {
	// return false;
	// }
	// }
	// int count = mViewPluginManager.size();
	// for (int i = 0; i < count; i++) {
	// BaseViewPlugin plugin = mViewPluginManager.get(i);
	// //如果bl为false，则接受模块的返回信息，如果其中的模块返回true,则认为拦截不再接受返回的结果
	// if (bl) {
	// plugin.getPluginController().onKeyDown(keyCode, event);
	// } else {
	// bl = plugin.getPluginController().onKeyDown(keyCode, event);
	// }
	// }
	// //XXX 测试.模块占用拦截了keyDown
	// return bl ? bl : super.onKeyDown(keyCode, event);
	// }
	//
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// int count = mViewPluginManager.size();
	// for (int i = 0; i < count; i++) {
	// BaseViewPlugin plugin = mViewPluginManager.get(i);
	// plugin.getPluginController().onConfigurationChanged(newConfig);
	// }
	// };
}

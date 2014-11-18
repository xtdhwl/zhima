package com.zhima.ui.common.view;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.zhima.R;
import com.zhima.base.logger.Logger;

/**
 * @ClassName:ZhimaPopupMenu
 * @Description:PopupMenu菜单
 * @author liqilong
 * @date 2012-8-9 上午11:09:14
 */
public class ZhimaPopupMenu {
	private static final String TAG = "ZhimaPopupMenu";

	private Context mContext;

	private PopupMenuHelper mPopupWindow;
	private List<ZhimaMenuItem> mZhimaMenuItems = new ArrayList<ZhimaMenuItem>();

	private ListView mListView;
	private ZhimaMenuAdapter mAdapter;
	private OnMenuItemClickListener mMenuItemClickListener;
	private OnDismissListener mDismissListener;

	private int mOldItem = -1;
	//设置Popup最大 宽度
	private int mPopupMaxWidth;
	private FrameLayout mMeasureParent;

	private Drawable mBackgroudDrawable;
	private Drawable mListSelectDrawable;
	private Drawable mListDividerDrawable;
	/**
	 * 保存选择的id
	 */
	private int mSelectId = -1;

	public ZhimaPopupMenu(Context context) {
		mContext = context;
		mListView = (ListView) View.inflate(context, R.layout.popup_menu, null);
//		mListView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return event.getAction() == MotionEvent.ACTION_MOVE ? true : false;
//			}
//		});

		Resources res = context.getResources();
		mPopupMaxWidth = res.getDisplayMetrics().widthPixels;
//		mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels /2,
//				res.getDimensionPixelSize(R.dimen.zhima_popupmenu_max_width));
		mBackgroudDrawable = mContext.getResources().getDrawable(R.drawable.menu_dropdown_panel_holo_dark);
	}

	/**
	 * 适配数据
	 */
	public void setMenuItems(int menu) {
		if (menu < 0) {
			return;
		}
		mOldItem = menu;
		mZhimaMenuItems = parseXml(menu);
		setPopupView();
	}

	public void dismiss() {
		mPopupWindow.dismiss();
	}

	public void show(View anchor) {
		// if(checkView(anchor))
		mPopupWindow.showAsDropDown(anchor);
	}

	public void showAtLocation(View parent, int gravity, int x, int y) {
		// if(checkView(parent))
		mPopupWindow.showAtLocation(parent, gravity, x, y);
	}

	public void showAsDropDown(View anchor, int xoff, int yoff) {
		// if(checkView(anchor))
		mPopupWindow.showAsDropDown(anchor, xoff, yoff);
	}

	/**
	 * @Title: setSelection
	 * @Description: 选择菜单(选择后icon只会显示选择的).注意要在setMenuItems前调用
	 * @param position
	 */
	public void setSelection(int id) {
		mSelectId = id;
	}

	private void setPopupView() {
		mAdapter = new ZhimaMenuAdapter(mContext, mZhimaMenuItems);
		mListView.setAdapter(mAdapter);
		mPopupWindow = new PopupMenuHelper(mListView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		isMeasure = true;
		mPopupWindow.setContentWidth(Math.min(measureContentWidth(mAdapter), mPopupMaxWidth));
		isMeasure = false;
		mPopupWindow.setBackgroundDrawable(mBackgroudDrawable);

		if (mListSelectDrawable != null) {
			mListView.setSelector(mListSelectDrawable);
		}

		if (mListDividerDrawable != null) {
			mListView.setDivider(mListDividerDrawable);
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPopupWindow.dismiss();
				if (mMenuItemClickListener != null) {
					mMenuItemClickListener.onMenuItemClick(mZhimaMenuItems.get(position), position);
				}
			}
		});

		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (mDismissListener != null) {
					mDismissListener.onDismiss();
				}
			}
		});

	}

	public void setSytle(TypedArray typedArray) {
		if (typedArray == null) {
			Logger.getInstance(TAG).debug("popup menu style 为null");
			return;
		}
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginPopupMenuBackground)) {
			mBackgroudDrawable = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginPopupMenuBackground);
			if (mPopupWindow != null) {
				mPopupWindow.setBackgroundDrawable(mBackgroudDrawable);
			}
		}
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginPopupMenuSelector)) {
			mListSelectDrawable = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginPopupMenuSelector);
			if (mListView != null) {
				mListView.setSelector(mListSelectDrawable);
			}
		}
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginPopupMenuDivider)) {
			mListDividerDrawable = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginPopupMenuDivider);
			if (mListView != null) {
				mListView.setDivider(mListDividerDrawable);
			}
		}

	}

	public void setBackground(Drawable drawable) {
		mBackgroudDrawable = drawable;
		mPopupWindow.setBackgroundDrawable(mBackgroudDrawable);
	}

	/**
	 * @Title: isShowing
	 * @Description: 是否在显示
	 * @return boolean
	 */
	public boolean isShowing() {
		return mPopupWindow != null && mPopupWindow.isShowing();
	}

	/**
	 * 设置 PopupMenu的Item点击事件
	 */
	public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
		mMenuItemClickListener = listener;
	}

	/**
	 * 设置PopupMenu Dismiss监听事件
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		mDismissListener = listener;
	}

	/**
	 * MenuItem点击事件
	 */
	public interface OnMenuItemClickListener {
		public void onMenuItemClick(ZhimaMenuItem item, int position);
		// public boolean onMenuItemClick(View item);
	}

	private List<ZhimaMenuItem> parseXml(int menu) {
		List<ZhimaMenuItem> resultList = null;
		try {
			XmlResourceParser xpp = mContext.getResources().getXml(menu);
			xpp.next();
			int eventType = xpp.getEventType();
			resultList = new ArrayList<ZhimaMenuItem>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String elemName = xpp.getName();
					if ("item".equals(elemName)) {
						String resId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "id");
						String txtId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "title");
						String iconId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "icon");
						ZhimaMenuItem item = new ZhimaMenuItem(mContext);
						int id = Integer.valueOf(resId.replace("@", ""));
						item.setId(id);
						if (txtId.lastIndexOf("@") == 0) {
							int txt = Integer.valueOf(txtId.replace("@", ""));
							item.setTitle(mContext.getResources().getString(txt));
						} else {
							item.setTitle(txtId);
						}
						if (iconId != null) {
							int icon = Integer.valueOf(iconId.replace("@", ""));
							item.setIcon(mContext.getResources().getDrawable(icon));
						} else {
							item.setIcon(null);
						}
						if (mSelectId != -1) {
							if (mSelectId == id) {
								item.setSelection(true);
							} else {
								item.setSelection(false);
							}
						} else {
							item.setSelection(true);
						}

						item.initView();
						resultList.add(item);
					}
				}
				eventType = xpp.next();
			}
			return resultList;
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.toString());
		}
		return null;
	}

	private class ZhimaMenuAdapter extends BaseAdapter {
		private List<ZhimaMenuItem> menuList;
		private Context mContex;

		public ZhimaMenuAdapter(Context mContex, List<ZhimaMenuItem> menuList) {
			super();
			this.menuList = menuList;
			this.mContex = mContex;
		}

		@Override
		public int getCount() {
			return menuList.size();
		}

		@Override
		public Object getItem(int position) {
			return menuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ZhimaMenuItem item = menuList.get(position);
			View view = null;
			if (!isMeasure) {
				View actionView = item.getActionView();

//				LayoutParams p = actionView.getLayoutParams();
//				if (p == null) {
//					
//					actionView.setLayoutParams(p);
//				}
				View title = actionView.findViewById(R.id.txt_title);
				View icon = actionView.findViewById(R.id.img_icon);

				LinearLayout.LayoutParams titleParams = (LayoutParams) title.getLayoutParams();
				LinearLayout.LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();

				titleParams.weight = 2;
				titleParams.width = android.view.ViewGroup.LayoutParams.FILL_PARENT;

				iconParams.weight = 4;
				iconParams.width = android.view.ViewGroup.LayoutParams.FILL_PARENT;

				title.setLayoutParams(titleParams);
				icon.setLayoutParams(iconParams);

				icon.setMinimumWidth(58);
				icon.setMinimumHeight(58);

				view = actionView;
			} else {
				view = item.getActionView();
			}
			return view;
		}
	}

	boolean isMeasure = false;

	private int measureContentWidth(BaseAdapter adapter) {
		// Menus don't tend to be long, so this is more sane than it looks.
		int width = 0;
		View itemView = null;
		int itemType = 0;
		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		final int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			final int positionType = adapter.getItemViewType(i);
			if (positionType != itemType) {
				itemType = positionType;
				itemView = null;
			}
			if (mMeasureParent == null) {
				mMeasureParent = new FrameLayout(mContext);
			}
			itemView = adapter.getView(i, itemView, mMeasureParent);
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
			width = Math.max(width, itemView.getMeasuredWidth());
		}
		return width + 40;
	}
}
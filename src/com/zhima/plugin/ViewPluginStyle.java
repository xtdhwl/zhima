package com.zhima.plugin;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

/**
 * @ClassName: ViewPluginStyle
 * @Description: viewPlugin样式器,注意要释放recycle
 * @author luqilong
 * @date 2013-1-10 下午4:53:58
 */
public class ViewPluginStyle {

	private Context mContext;
	private ViewPStyle vpStyle;

	public ViewPluginStyle(Context context) {
		this.mContext = context;
	}

	public void loadStyle(int styleResId) {
		if (vpStyle != null) {
			vpStyle.recycle();
			vpStyle = null;
		}
		vpStyle = (ViewPStyle) View.inflate(mContext, styleResId, null);
	}

	public TypedArray getStyle() {
		return vpStyle.getTypeArray();
	}

	public void recycle() {
		vpStyle.recycle();
	}

}

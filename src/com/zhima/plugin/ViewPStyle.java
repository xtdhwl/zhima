package com.zhima.plugin;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.zhima.R;

/**
 * @ClassName: VPStyle
 * @Description: 解析viewPlugin样式配置。注意要释放TypedArray
 * @author luqilong
 * @date 2013-1-10 下午4:52:32
 */
public class ViewPStyle extends View {
	private TypedArray typeArray;

	public ViewPStyle(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPStyle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		typeArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPluginStyle, defStyle, 0);
	}

	public TypedArray getTypeArray() {
		return typeArray;
	}

	public void recycle() {
		if (typeArray != null) {
			typeArray.recycle();
		}
	}

}

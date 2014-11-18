package com.zhima.ui.tools;

import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;
/**
* @ClassName: IntentSpan
* @Description: 点击启动Activity
* @author liubingsr
* @date 2013-1-22 下午8:07:01
*
*/
public class IntentSpan extends ClickableSpan {
	private Intent mIt;
	private Context mContext;
	
	public IntentSpan(Context context, Intent it) {
		mContext = context;
		mIt = it;
	}
	@Override
	public void onClick(View view) {
		mContext.startActivity(mIt);
	}
}

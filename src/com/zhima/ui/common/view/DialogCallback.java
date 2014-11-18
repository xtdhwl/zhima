package com.zhima.ui.common.view;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
/**
* @ClassName: Callback
* @Description: 对DialogInterface.OnClickListener的封装
* @author liubingsr
* @date 2012-6-12 上午11:23:59
*
*/
public abstract class DialogCallback implements OnClickListener {
    protected Object mData;

    public DialogCallback() {}

    public DialogCallback(Object data) {
        mData = data;
    }

    public void setData(Object data) {
        mData = data;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        callback(which == -1);
    }
    
    public void onCancel(DialogInterface dialog) {} 

    public abstract void callback(boolean okEvent);
}

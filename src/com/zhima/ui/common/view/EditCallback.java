package com.zhima.ui.common.view;

import android.view.View;

import com.zhima.base.utils.ImeHelper;

public class EditCallback extends DialogCallback {
    @Override
    public void callback(boolean okEvent){
        if(okEvent) {
        	ImeHelper.hideIME((View)mData);
        }
    }
}

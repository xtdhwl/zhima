package com.zhima.ui.common.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhima.R;

public class MsgDialog {
	
	private Dialog mDialog;
	
	private Context mContext;

	private View mContentView;
	
	/** 标题 */
	private TextView mTitleText;
	/** 消息 */
	private TextView mMessageText;
	/** 左边button */
	private TextView mLeftButton;
	/** 右边button */
	private TextView mRightButton;
	/** 监听器 */
	private OnBtClickListener mListener;
	
	private OnOnlyBtClickListener mOnlyListener;

	public MsgDialog(Context context){
		
		this.mContext = context;
		mDialog = new Dialog(mContext,R.style.Custom_dialog_style);
		mContentView = View.inflate(mContext, R.layout.message_dialog, null);
		
		mTitleText = (TextView) mContentView.findViewById(R.id.txt_messageDialog_title);
		mMessageText = (TextView) mContentView.findViewById(R.id.txt_messageDialog_message);
		mLeftButton = (TextView) mContentView.findViewById(R.id.txt_messageDialog_leftButton);
		mRightButton = (TextView) mContentView.findViewById(R.id.txt_messageDialog_rightButton);
		
		
		WindowManager.LayoutParams localLayoutParams = mDialog.getWindow().getAttributes();
		mDialog.onWindowAttributesChanged(localLayoutParams);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(true);
		mContentView.setMinimumWidth(10000);
		mDialog.setContentView(mContentView);
		localLayoutParams.gravity = Gravity.CENTER;
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		mTitleText.setText(title);
	}
	
	/**
	 * 设置标题
	 * @param resId 资源Id
	 */
	public void setTitle(int resId){
		mTitleText.setText(resId);
	}
	
	/**
	 * 设置消息内容
	 * @param message
	 */
	public void setMessage(String message){
		mMessageText.setText(message);
	}
	
	/**
	 * 设置消息内容
	 * @param resId 资源Id
	 */
	public void setMessage(int resId){
		mMessageText.setText(resId);
	}
	
	/**
	 * 设置左边button名称
	 * @param text
	 */
	public void setLeftBtText(String text){
		mLeftButton.setText(text);
		
	}
	
	/**
	 * 设置只有一个Button
	 * @param name 名称
	 */
	public void setOnlyButton(String name){
		mRightButton.setVisibility(View.GONE);
		mLeftButton.setText(name);
	}
	
	public void setOnlyButton(int resId){
		mRightButton.setVisibility(View.GONE);
		mLeftButton.setText(resId);
	}
	
	/**
	 * 设置左边button名称
	 * @param resId
	 */
	public void setLeftBtText(int resId){
		mLeftButton.setText(resId);
	}
	
	/**
	 * 设置右边button名称
	 * @param text
	 */
	public void setRightBtText(String text){
		mRightButton.setText(text);
	}
	
	/**
	 * 设置右边button名称
	 * @param resId
	 */
	public void setRightBtText(int resId){
		mRightButton.setText(resId);
	}
	
	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnBtClickListener(OnBtClickListener listener){
		this.mListener = listener;
		mLeftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onLeftBtClick();
				mDialog.dismiss();
			}
		});
			mRightButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mListener.onRightBtClick();
					mDialog.dismiss();
				}
			});
	}
	
	/**
	 * 设置一个Button监听
	 * @param listener
	 */
	public void setOnOnlyBtClickListener(OnOnlyBtClickListener listener){
		this.mOnlyListener = listener;
		mLeftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOnlyListener.onLeftBtClick();
				mDialog.dismiss();
			}
		});
	}
	
	public void show(){
		if(mDialog!=null){
			mDialog.show();
		}
	}
	
	/**
	 * 接口
	 * @ClassName: OnBtClickListener
	*/
	public interface OnBtClickListener{
		void onLeftBtClick();
		void onRightBtClick();
	}
	
	/**
	 * 只有一个button时调用的接口
	 * @ClassName: OnBtClickListener
	 */
	public interface OnOnlyBtClickListener{
		void onLeftBtClick();
	}
	
}

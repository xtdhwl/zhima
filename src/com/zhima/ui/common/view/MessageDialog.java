package com.zhima.ui.common.view;



import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhima.R;

/**
 * 消息对话框
 * @ClassName: MessageDialog
 * @Description: 如果只有一个button  setOnlyButton就行
 * @author yusonglin
 * @date 2012-8-10 下午1:57:50
*/
public class MessageDialog extends PopupWindow {

	private Context mContext;
	/** 父view */
	private View mParentView;
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
	
	private boolean isonlyButton = false;
	private View mContentView;
	
	public MessageDialog(Context context,View parentView) {
		super(context);
		this.mContext = context;
		this.mParentView = parentView;
		mContentView = View.inflate(mContext, R.layout.message_dialog, null);
		mTitleText = (TextView) mContentView.findViewById(R.id.txt_messageDialog_title);
		mMessageText = (TextView) mContentView.findViewById(R.id.txt_messageDialog_message);
		mLeftButton = (TextView) mContentView.findViewById(R.id.txt_messageDialog_leftButton);
		mRightButton = (TextView) mContentView.findViewById(R.id.txt_messageDialog_rightButton);
		setContentView(mContentView);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.light_black_tran)));
		
		setAnimationStyle(R.style.customDialog_anim_style);
		setOutsideTouchable(true);
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
				dismiss();
			}
		});
			mRightButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mListener.onRightBtClick();
					dismiss();
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
				dismiss();
			}
		});
	}
	
	/**
	 * 显示
	 */
	public void show(){
		showAtLocation(mParentView, Gravity.CENTER,0,0);
//		mContentView.startAnimation(new AnimationUtils().loadAnimation(mContext, R.anim.custom_dialog_anim_show));
	}
	
	
//	@Override
//	public void dismiss() {
//		Animation loadAnimation = new AnimationUtils().loadAnimation(mContext, R.anim.custom_dialog_anim_dismiss);
//		
//		mContentView.startAnimation(loadAnimation);
//		loadAnimation.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				mHandler.sendEmptyMessage(0);
//			}
//		});
//	}
//	
//	private Handler mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			MessageDialog.super.dismiss();
//		}
//	};
	
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

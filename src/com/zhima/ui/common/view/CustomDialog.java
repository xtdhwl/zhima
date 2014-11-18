package com.zhima.ui.common.view;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhima.R;

/**
 * 时间地址对话框
 * @ClassName: TimeAreaDialog
 * @Description: 主内容布局可以自己添加
 * @author yusonglin
 * @date 2012-8-11 上午9:23:13
*/
public class CustomDialog extends PopupWindow {

	private Context mContext;
	/** 父view */
	private View mParentView;
	/** 标题 */
	private TextView mTitleText;
	/**图标*/
	private ImageView mImageView;
	
	/** 中间不确定的布局 */
	private LinearLayout mUnsureLayout;
	
	/** 左边button */
	private TextView mLeftButton;
	/** 右边button */
	private TextView mRightButton;
	
	private View mMiddleLine;
	
	/** 监听器 */
	private OnBtClickListener mListener;
	
	
	private OnOnlyBtClickListener mOnOnlyBtClickListener;
	private View mContentView;
	
	public CustomDialog(Context context,View parentView) {
		super(context);
		this.mContext = context;
		this.mParentView = parentView;
		mContentView = View.inflate(mContext, R.layout.time_area_dialog, null);
		mTitleText = (TextView) mContentView.findViewById(R.id.txt_timeAreaDialog_title);
		mUnsureLayout = (LinearLayout) mContentView.findViewById(R.id.layout_unsure_view);
		mLeftButton = (TextView) mContentView.findViewById(R.id.txt_timeAreaDialog_leftButton);
		mRightButton = (TextView) mContentView.findViewById(R.id.txt_timeAreaDialog_rightButton);
		mImageView = (ImageView) mContentView.findViewById(R.id.img_dialog);
		
		mMiddleLine = mContentView.findViewById(R.id.view_dialog_middleline);
		
		setContentView(mContentView);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setFocusable(true);
		setAnimationStyle(R.style.customDialog_anim_style);
		
		setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.light_black_tran)));
	
	}
	
	/**
		* @Title: setIconVisible
		* @Description:设置图标的显示
		* @param 
		*/
	public void setIconVisible(boolean isVisible){
		if(isVisible){
			mImageView.setVisibility(View.VISIBLE);
		}else{
			mImageView.setVisibility(View.GONE);
		}
	}
	
	/**
		* @Title: setIconSrc
		* @Description:设置图标的图片
		* @param 
		*/
	public void setIconSrc(int id){
		mImageView.setImageResource(id);
	}
	
	/**
	 * 设置中间的布局
	 * @param view
	 */
	public void setMiddleLayout(View view){
		mUnsureLayout.addView(view);
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
	 * 设置左边button名称
	 * @param text
	 */
	public void setLeftBtText(String text){
		mLeftButton.setText(text);
	}
	
	/**
	 * 设置右边button名称
	 * @param text
	 */
	public void setRightBtText(String text){
		mRightButton.setText(text);
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
	
	public interface OnOnlyBtClickListener{
		void onOnlyBtClick();
	}
	
	public void setOnOnlyBtClickListener(String name,OnOnlyBtClickListener onOnlyBtClickListener){
		this.mOnOnlyBtClickListener = onOnlyBtClickListener;
		
		mLeftButton.setVisibility(View.GONE);
		mMiddleLine.setVisibility(View.GONE);
		mRightButton.setText(name);
		
		mRightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOnOnlyBtClickListener.onOnlyBtClick();
				dismiss();
			}
		});
	}
	
	public void setOnOnlyBtClickListener(int res,OnOnlyBtClickListener onOnlyBtClickListener){
		this.mOnOnlyBtClickListener = onOnlyBtClickListener;
		
		mLeftButton.setVisibility(View.GONE);
		mMiddleLine.setVisibility(View.GONE);
		mRightButton.setText(res);
		
		mRightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOnOnlyBtClickListener.onOnlyBtClick();
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
//			CustomDialog.super.dismiss();
//		}
//	};
	
	/**
	 * 接口
	 * @ClassName: OnBtClickListener
	*/
	public interface OnBtClickListener{
		void onRightBtClick();
		void onLeftBtClick();
	}
}

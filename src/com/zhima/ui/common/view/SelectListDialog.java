package com.zhima.ui.common.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhima.R;

/**
 * 选项对话框
 * @ClassName: SelectListDialog
 * @Description: TODO
 * @author yusonglin
 * @date 2012-8-10 下午1:53:45
*/
public class SelectListDialog {

	/**  */
	private Context mContext;
	/** 选项list */
	private ListView mListView;
	/** 标题 */
	private TextView mTitleText;
	/** 监听器 */
	private OnBtItemClicklistener mListener;
	private View mContentView;
	
	private Dialog mDialog;
	
	public SelectListDialog(Context context) {
		this.mContext = context;
		mDialog = new Dialog(mContext,R.style.Custom_dialog_style);
		mContentView = View.inflate(mContext, R.layout.button_dialog, null);
		
		mListView = (ListView) mContentView.findViewById(R.id.lstv_button_dialog);
		mTitleText = (TextView) mContentView.findViewById(R.id.txt_button_dialog_title);
		
		WindowManager.LayoutParams localLayoutParams = mDialog.getWindow().getAttributes();
		
		mDialog.onWindowAttributesChanged(localLayoutParams);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(true);
		mContentView.setMinimumWidth(10000);
		mDialog.setContentView(mContentView);
		localLayoutParams.gravity = Gravity.CENTER;
		
//		setContentView(mContentView);
//		setWidth(LayoutParams.WRAP_CONTENT);
//		setHeight(LayoutParams.WRAP_CONTENT);
//		setFocusable(true);
//		setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.light_black_tran)));
//		setAnimationStyle(R.style.customDialog_anim_style);
//		setOutsideTouchable(false);
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
	 * 设置选项数组
	 * @param names 名称数组
	 */
	public void setoptionNames(String[] names) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
			(mContext, R.layout.button_dialog_item, R.id.txt_button_dialog_item,names);
		mListView.setAdapter(adapter);
	}
	
	/**
	 * 设置选项数组
	 * @param names 名称数组
	 */
	public void setoptionNames(int[] resIds) {
		List<String> names = new ArrayList<String>();
		for(int i=0;i<resIds.length;i++){
			names.add(mContext.getResources().getString(resIds[i]));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
			(mContext, R.layout.button_dialog_item, R.id.txt_button_dialog_item,names);
		mListView.setAdapter(adapter);
	}
	
	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnBtItemClickListener(OnBtItemClicklistener listener){
		this.mListener = listener;
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.onItemClick(parent, view, position, id);
				mDialog.dismiss();
			}
		});
	}
	
	/**
	 * 显示
	 */
	public void show(){
		if(mDialog!=null){
			mDialog.show();
		}
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
//			SelectListDialog.super.dismiss();
//		}
//	};
	
	/**
	 * 监听接口
	 * @ClassName: OnBtItemClicklistener
	*/
	public interface OnBtItemClicklistener{
		void onItemClick(AdapterView<?> parent, View view,
				int position, long id);
	}
}

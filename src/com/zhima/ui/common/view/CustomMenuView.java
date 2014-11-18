package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zhima.R;

/**
 * @ClassName: CustomMenuView
 * @Description: 自定义Popupwindow 选项菜单
 * @author yusonglin
 * @date 2012-7-16 上午 
 */
public class CustomMenuView extends PopupWindow {

	private Context mContext;
	/**
	 * 主要布局
	 */
	private View mContentView;
	/**
	 * 显示在布局上不固定个数的按钮 集合
	 */
	private ListView mListView;
	/**
	 * 布局上最下边的一个按钮
	 */
	private Button mBottomButton;
	/**
	 * 按钮点击事件监听器
	 */
	private OnBtClickListener mOnBtClickListener;
	/**
	 * 按钮的名字数组
	 */
	private String[] mBtTitles;
	
	
	public CustomMenuView(Context context) {
		super(context);
		this.mContext = context;
		mContentView = View.inflate(mContext, R.layout.custom_popmenu, null);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setFocusable(true);
		setContentView(mContentView);
		setOutsideTouchable(true);
		//一定要设置背景图片
		setBackgroundDrawable(new ColorDrawable(0x00000000));
		setAnimationStyle(R.style.mypopwindow_anim_style);
	}

	/**
	 * 设置布局上边不固定按钮的listview的适配器
	 * @param buttonTitles 各个按钮名称数组
	 */
	public void setListButton(String[] titles){
		this.mBtTitles = titles;
		mListView = (ListView) mContentView.findViewById(R.id.lstv_button);
		mListView.setAdapter(new MyAdapter());
	}
	
	/**
	 * 设置最下边按钮的名称和点击事件
	 * @param buttonTitle 按钮名称
	 */
	public void setBottomButton(String bottomBtTitle){
		mBottomButton = (Button) mContentView.findViewById(R.id.btn_confirm);
		mBottomButton.setText(bottomBtTitle);
		mBottomButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOnBtClickListener.onClick(mBtTitles.length, v);
			}
		});
	}
	
	/**
	 * 设置按钮监听器
	 * @param listener
	 */
	public void setOnBtClickListener(OnBtClickListener onBtClickListener){
		this.mOnBtClickListener = onBtClickListener;
	}
	
	/**
	 * @interfaceName OnBtClickListener
	 */
	public interface OnBtClickListener{
		void onClick(int position,View v);
	}
	
	/**
	 * @className MyAdapter
	 * 布局上显示不固定按钮listview的适配器
	 */
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBtTitles.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBtTitles[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Button button = (Button) View.inflate(mContext, R.layout.custom_popmenu_item, null);
			button.setText(mBtTitles[position]);
			//button.seton
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mOnBtClickListener.onClick(position, v);
				}
			});
			
			return button;
		}
		
		
	}
}

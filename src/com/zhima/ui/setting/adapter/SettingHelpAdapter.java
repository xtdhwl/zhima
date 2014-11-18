package com.zhima.ui.setting.adapter;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.service.AppLaunchService;
import com.zhima.ui.main.activity.MainActivity;

public class SettingHelpAdapter extends BaseAdapter {

	private Context mContext;
	private Activity mActivity;
	private int[] mImageIds = {R.drawable.help1,R.drawable.help2,R.drawable.help3,R.drawable.help4,R.drawable.help5};
	private boolean isFirstInstall ;
	
	public SettingHelpAdapter(Context context,Activity activity,boolean isFirstInstall){
		this.mContext = context;
		this.mActivity = activity;
		this.isFirstInstall = isFirstInstall;
	}
	
	@Override
	public int getCount() {
		return mImageIds.length;
	}

	@Override
	public Object getItem(int position) {
		return mImageIds[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = View.inflate(mContext, R.layout.setting_help_item,null);
		
//		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_setting_helpItem);
		Button button = (Button) view.findViewById(R.id.btn_setting_helpItem);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.img_setting_helpItem);
		
		imageView.setImageBitmap(readBitMap(mContext, mImageIds[position]));
		
//		layout.setBackgroundResource(mImageIds[position]);
		
		if(position == 4){
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isFirstInstall){
						Intent intent = new Intent(mContext,MainActivity.class);
						mContext.startActivity(intent);
						SettingHelper.setString(mContext, Field.VERSION, AppLaunchService.getInstance(mContext).getVersionName());
					}
					mActivity.finish();
				}
			});
			button.setVisibility(View.VISIBLE);
		}else{
			button.setVisibility(View.GONE);
		}
		
		return view;
	}
	
	public Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}

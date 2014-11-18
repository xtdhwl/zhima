package com.zhima.ui.usercenter.data.friend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.logger.Logger;
import com.zhima.data.model.User;
import com.zhima.data.service.RegionService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

public class FriendListAdapter extends ZhimaAdapter<User> {

	private static final String TAG = FriendListAdapter.class.getSimpleName();

	public FriendListAdapter(Context context, int layoutId, List<User> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, User data) {
		ViewHolder holder = new ViewHolder();
		holder.mPhotoImg = (ImageView) view.findViewById(R.id.img_photo);
		holder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		holder.mGenderText = (TextView) view.findViewById(R.id.txt_gender);
		holder.mAddressView = (TextView) view.findViewById(R.id.txt_address);
		return holder;
	}

	@Override
	public void bindView(User data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		holder.mNameText.setText(data.getNickname());
		holder.mGenderText.setText(GenderType.getGenderLabel(data.getGender()));

		try {
			String region = RegionService.getInstance(mContext).getRegionStr(data.getCityId());
			String[] split = region.split("-");
			String r = "";
			for (String s : split) {
				if (TextUtils.isEmpty(r)) {
					r += s;
				} else {
					r += "-" + s;
				}
			}
			holder.mAddressView.setText("地址:" + r);
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mPhotoImg,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
	}

	class ViewHolder {
		ImageView mPhotoImg;
		TextView mNameText;
		TextView mGenderText;
		TextView mAddressView;
	}

	public void setData(ArrayList<User> array) {
		mArray = array;
	}

}

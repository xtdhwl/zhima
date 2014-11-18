package com.zhima.ui.contact.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.TargetType;
import com.zhima.data.model.ContactEntry;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: ContactAdatper
 * @Description: 通讯录
 * @author luqilong
 * @date 2012-9-25 下午12:02:15
 */
public class ContactAdatper extends ZhimaAdapter<ContactEntry> {

	public ContactAdatper(Context context, int layoutId, List<ContactEntry> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, ContactEntry data) {
		ViewHolder holder = new ViewHolder();
		holder.mPhotoImg = (ImageView) view.findViewById(R.id.img_photo);
		holder.mNameText = (TextView) view.findViewById(R.id.txt_name);
		holder.mTypeText = (TextView) view.findViewById(R.id.txt_type);
		holder.mPhoneText = (TextView) view.findViewById(R.id.txt_phone);
		holder.mPhoneImg = (ImageView) view.findViewById(R.id.img_phone);
		holder.mContent = (ViewGroup) view.findViewById(R.id.layout_content);
		return holder;
	}

	@Override
	public void bindView(ContactEntry data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mPhotoImg,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
		holder.mNameText.setText(data.getTitle());
		holder.mPhoneText.setText(data.getTelephone());
		holder.mTypeText.setText(mContext.getText(R.string.type) + " : "
				+ TargetType.getTargetType(data.getObjectType()));

		if (TextUtils.isEmpty(data.getTelephone())) {
			holder.mPhoneImg.setClickable(false);
			holder.mPhoneImg.setImageResource(R.drawable.phone_h);
		}else{
			holder.mPhoneImg.setClickable(true);
			holder.mPhoneImg.setImageResource(R.drawable.phone_sele);
			bindClicker(data, holder.mPhoneImg);
		}
	}

	class ViewHolder {
		ImageView mPhotoImg;
		TextView mNameText;
		TextView mPhoneText;
		TextView mTypeText;
		ImageView mPhoneImg;
		ViewGroup mContent;
	}

	public void setData(ArrayList<ContactEntry> array) {
		mArray = array;
	}

}

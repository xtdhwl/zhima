package com.zhima.ui.space.zmspace.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.ZMSpaceVideo;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 芝麻空间影像列表 Adapter
 * @ClassName: ZmSpaceVedioAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-31 下午2:24:19
*/
public class ZmSpaceVedioAdapter extends ZhimaAdapter<ZMSpaceVideo> {

	public ZmSpaceVedioAdapter(Context context, int layoutId,
			List<ZMSpaceVideo> array) {
		super(context, layoutId, array);
		
	}

	@Override
	public Object createViewHolder(View view, ZMSpaceVideo data) {
		ViewHolder viewHolder = new ViewHolder();
		ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mPhotoImage = photoImage;
		return viewHolder;
	}

	@Override
	public void bindView(ZMSpaceVideo data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
		
	}

	private static class ViewHolder {
		public ImageView mPhotoImage;

	}

	public void setData(ArrayList<ZMSpaceVideo> mIdolAcqierementList) {
		mArray = mIdolAcqierementList;
	}
	
}

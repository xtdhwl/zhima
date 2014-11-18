package com.zhima.ui.space.zmspace.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.model.ZMSpaceVideo;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 芝麻空间相册 Adapter
 * @ClassName: ZmSpaceVedioAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-31 下午2:24:19
*/
public class ZmSpaceImageAdapter extends ZhimaAdapter<ZMObjectImage> {

	public ZmSpaceImageAdapter(Context context, int layoutId,
			List<ZMObjectImage> array) {
		super(context, layoutId, array);
		
	}

	@Override
	public Object createViewHolder(View view, ZMObjectImage data) {
		ViewHolder viewHolder = new ViewHolder();
		ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
		viewHolder.mPhotoImage = photoImage;
		return viewHolder;
	}

	@Override
	public void bindView(ZMObjectImage data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mPhotoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
		
	}

	private static class ViewHolder {
		public ImageView mPhotoImage;

	}

	public void setData(ArrayList<ZMObjectImage> mIdolAcqierementList) {
		mArray = mIdolAcqierementList;
	}
	
}

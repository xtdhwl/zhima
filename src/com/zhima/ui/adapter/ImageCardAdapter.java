package com.zhima.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:ImageCardAdapter
 * @Description:单张图片CardAdapter
 * @author liqilong
 * @date 2012-8-13 下午2:02:47
 * 
 */
// R.layout.space_imger_card_item
public class ImageCardAdapter extends ZhimaAdapter<ZMObjectImage> {
	private Context mContext;

	public ImageCardAdapter(Context context, int layoutId, List<ZMObjectImage> array) {
		super(context, layoutId, array);
		mContext = context;
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

		ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), photoImage,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

	}

	private static class ViewHolder {
		public ImageView mPhotoImage;

	}

}

package com.zhima.plugin.space.couples.adapter;

import java.util.List;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.SpaceVPAdapter;
import com.zhima.ui.tools.HttpImageLoader;

public class CouplesAlbumAdapter extends SpaceVPAdapter<ZMObjectImage> {
	private HttpImageLoader mHttpImageLoader;
	private BaseActivity activity;

	public CouplesAlbumAdapter(BaseActivity activity, List<ZMObjectImage> array, int previewCount, int maxListSize) {
		super(activity, array, previewCount, maxListSize);
		this.activity = activity;
		mHttpImageLoader = HttpImageLoader.getInstance(activity);
	}

	@Override
	public Object getView(ViewGroup container, ZMObjectImage data, int position) {

		ImageView iv = new ImageView(mContext);
		//适配
		int widht = GraphicUtils.mScreenWidth;

		int layoutWidht = widht;
		int layoutHeight = widht / 4 *3;

		iv.setLayoutParams(new LinearLayout.LayoutParams(layoutWidht, 900));
		iv.setScaleType(ScaleType.FIT_XY);
		//XXX ImageScaleType类型需要修改
		mHttpImageLoader.loadImage(data.getImageUrl(), iv, activity.getActivityId(), R.drawable.female_default,
				ImageScaleType.LARGE);

		container.addView(iv, 0);
		return iv;
	}

}

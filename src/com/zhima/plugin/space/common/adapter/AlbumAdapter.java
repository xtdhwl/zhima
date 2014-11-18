package com.zhima.plugin.space.common.adapter;

import java.util.List;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.SpaceVPAdapter;
import com.zhima.ui.tools.HttpImageLoader;

public class AlbumAdapter extends SpaceVPAdapter<ZMObjectImage> {
	private HttpImageLoader mHttpImageLoader;
	private BaseActivity activity;
	private int mDefaultDrawable;

	public AlbumAdapter(BaseActivity activity, List<ZMObjectImage> array, int defaultDrawable, int previewCount,
			int maxListSize) {
		super(activity, array, previewCount, maxListSize);
		this.activity = activity;
		mHttpImageLoader = HttpImageLoader.getInstance(activity);
		mDefaultDrawable = defaultDrawable;
	}

	@Override
	public Object getView(ViewGroup container, ZMObjectImage data, int position) {

		ImageView iv = new ImageView(mContext);

		GraphicUtils.mScreenWidth = activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
		GraphicUtils.mScreenHeight = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();

		int layoutWidht = GraphicUtils.mScreenWidth;
		int layoutHeight = GraphicUtils.mScreenWidth / 4 * 3;

		iv.setLayoutParams(new LinearLayout.LayoutParams(layoutWidht, layoutHeight));
		mHttpImageLoader.loadImage(data.getImageUrl(), iv, activity.getActivityId(), mDefaultDrawable,
				ImageScaleType.LARGE);

		container.addView(iv, 0);
		return iv;
	}

}

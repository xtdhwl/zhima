package com.zhima.ui.space.zmspace.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.SpaceQueryResult;
import com.zhima.data.model.ZMObject;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.space.zmspace.activity.ZmSpacePlazaActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 芝麻空间广场adapter
 * @ClassName: ZmSpaceQueryResultAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-2-3 下午8:14:48
*/
public class ZmSpacePlazaAdapter extends ZhimaAdapter<ZMObject> {
	private BaseActivity mActivity;
	public ZmSpacePlazaAdapter(Context context, int layoutId,
			List<ZMObject> array) {
		super(context, layoutId, array);
		mActivity = (BaseActivity) context;
	}


	@Override
	public Object createViewHolder(View view, ZMObject data) {
		ViewHolder holder = new ViewHolder();
		holder.mHeadImage = (ImageView) view.findViewById(R.id.img_zmspace_plaza_item_headImage);
		holder.mSpaceNameText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_spaceName);
		holder.mSpaceTypeText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_spaceType);
		holder.mDescriptionText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_description);
		holder.mPopularityText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_popularity);
		return holder;
		
	}


	@Override
	public void bindView(final ZMObject data, int position, View view) {
		//TODO
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		
		
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(),holder.mHeadImage,
				mActivity.getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
		holder.mSpaceNameText.setText(data.getName());
		com.zhima.data.model.ZMSpaceKind spaceKind = data.getSpaceKind();
		holder.mSpaceTypeText.setText("类型："+(spaceKind!=null?spaceKind.getName():""));
		holder.mDescriptionText.setText("简介："+data.getDescription());
		holder.mPopularityText.setVisibility(View.GONE);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivitySwitcher.openSpaceActivity(mActivity, data.getId(), data.getZMObjectType(), null, false);
			}
		});
	}
	
	class ViewHolder{
		ImageView mHeadImage;
		TextView mSpaceNameText;
		TextView mSpaceTypeText;
		TextView mDescriptionText;
		TextView mPopularityText;//人气
	}

	public void setData(ArrayList<ZMObject> dataList) {
		//TODO
		this.mArray = dataList;
	}

}

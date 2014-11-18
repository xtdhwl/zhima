package com.zhima.plugin.space.reputationseal.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMSpace;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.space.activity.PreviewActivity;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.usercenter.activity.PersonalDataActivity;

/**
 * 誉玺空间日志 adapter
 * @ClassName: RepDiaryPluginAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-30 下午3:09:22
*/
public class RepDiaryPluginAdapter extends ZhimaAdapter<ZMDiary> {
	
	private static final String TAG = "RepDiaryPluginAdapter";
	private BaseActivity mActivityParent;
	//countFlag 解决ScrollView里面套ListView,异步加载ImageView问题(第一次不执行异步加载,第二次才执行异步加载)
	private int countFlag = 0;
	private int mItemtCount = -1;

	public RepDiaryPluginAdapter(BaseActivity activityParent, int layoutId,
			List<ZMDiary> array) {
		super(activityParent, layoutId, array);
		mActivityParent = activityParent;
	}
	
	@Override
	public int getCount() {
		if (mItemtCount < 0) {
			return super.getCount();
		} else {
			return Math.min(mItemtCount, mArray.size());
		}
	}
	
	/**
	 * 指定Itemt个数.(控制显示频道显示个数)
	 */
	public void setItemtCount(int itemt) {
		countFlag = 0;
		mItemtCount = itemt;
	}
	
	@Override
	public Object createViewHolder(View view, ZMDiary data) {
		ViewHolder holder = new ViewHolder();
		holder.mTitleText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_title);
		holder.mDateText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_date);
		holder.mContentText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_content);
		holder.mDiaryImage = (ImageView) view.findViewById(R.id.img_personalData_diaryItem_image);
		holder.mToSpaceText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_toSpace);
		holder.mforwordText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_forwordCount);
		holder.mcommentText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_commentCount);

		holder.mForwardContentLayout = (LinearLayout) view
				.findViewById(R.id.layout_personalData_diaryItem_forwardContent);
		holder.mFromAndDateLayout = (LinearLayout) view
				.findViewById(R.id.layout_personalData_diaryItem_fromAndDate);
		holder.mFromText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_from);
		holder.mPublishDateText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_publishedDate);
		return holder;
	}

	@Override
	public void bindView(ZMDiary data, final int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);

		final ZMDiary diary = data;
		//TODA 设置日志内容

//		holder.mTitleText.getPaint().setFakeBoldText(true);

		if (diary != null) {

			holder.mTitleText.setText(diary.getTitle());
			holder.mDateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(diary.getPostTime()));
			if(!diary.isOrginalExists()){
				holder.mContentText.setText("原始内容已删除");
			}else{
				holder.mContentText.setText(FaceHelper.getInstance(mContext).getSpannableString(
						diary.getContent()));
			}

			if (diary.getImageUrl() != null && !"".equals(diary.getImageUrl())) {
				HttpImageLoader.getInstance(mContext).loadImage(diary.getImageUrl(), holder.mDiaryImage,
						mActivityParent.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
				holder.mDiaryImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//TODO 进入图片浏览
//						HaloToast.show(getApplicationContext(), "进入图片大图浏览");
						Intent it = new Intent(mActivityParent, PreviewActivity.class);
						it.putExtra(PreviewActivity.ACTIVITY_URL, diary.getImageUrl());
						it.putExtra("type", ImageScaleType.LARGE);
						mActivityParent.startActivity(it);
					}
				});
				holder.mDiaryImage.setVisibility(View.VISIBLE);
			} else {
				holder.mDiaryImage.setVisibility(View.GONE);
			}

			List<ZMSpace> spaceList = diary.getSpaceList();
			if (spaceList != null) {
				StringBuffer buffer = new StringBuffer();
				for (ZMSpace space : spaceList) {
					buffer.append("@" + space.getName() + " ");
				}
				holder.mToSpaceText.setText(buffer.toString());
				holder.mToSpaceText.setVisibility(View.VISIBLE);
			} else {
				holder.mToSpaceText.setVisibility(View.GONE);
			}
			holder.mforwordText.setText(diary.getForwardCount() + "");
			holder.mcommentText.setText(diary.getReplyCount() + "");

			if (diary.isOriginal()) {
				holder.mForwardContentLayout.setBackgroundResource(R.color.transparent);
				holder.mFromAndDateLayout.setVisibility(View.GONE);
			} else {
				holder.mForwardContentLayout.setBackgroundResource(R.color.user_center_forword_diary_bg);
				holder.mFromAndDateLayout.setVisibility(View.VISIBLE);
				if(diary.getRawAuthor()!=null){
					final User rawAuthor = diary.getRawAuthor();
					holder.mFromText.setText(rawAuthor.getNickname());
					holder.mFromText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO
//						HaloToast.show(getApplicationContext(), "二货");
							if(diary.isSync()){
								//TODO 跳转到其它空间
//								ActivitySwitcher.openSpaceActivity(mActivityParent, diary.getAuthor(), targetType)
							}else{
								final boolean isMySelf = UserService.getInstance(mActivityParent).isMySelf(rawAuthor.getUserId());
								UserUtils util = new UserUtils(mActivityParent);
								util.switchAcitivity(rawAuthor.getUserId(), true);
							}
						}
					});
				}
				holder.mPublishDateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(diary
						.getRawCreatedOn()));
			}
		}/*
		 * else{ if(position == 2 || position == 5 || position == 6){
		 * holder.mForwardContentLayout.setBackgroundResource(R.color.
		 * user_center_forword_diary_bg);
		 * holder.mFromAndDateLayout.setVisibility(View.VISIBLE); }else{
		 * holder
		 * .mForwardContentLayout.setBackgroundResource(R.color.transparent
		 * ); holder.mFromAndDateLayout.setVisibility(View.GONE); } }
		 */

		//TODO 可以再controller里边跳转
//		view.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//TODO
//				Intent intent = new Intent(mActivityParent, DiaryInfoActivity.class);
//				intent.putExtra(DiaryConsts.user_Id, diary.getAuthor().getUserId());
//				intent.putExtra(DiaryConsts.space_id, diary.getId());
//				intent.putExtra(DiaryConsts.position, position);
//				mActivityParent.startActivity(intent);
//			}
//		});
	}
	
	class ViewHolder {
		TextView mTitleText;//标题
		TextView mDateText;//日期
		TextView mContentText;//主内容
		ImageView mDiaryImage;
		TextView mToSpaceText;//发往哪几个空间
		TextView mforwordText;//转发数
		TextView mcommentText;//评论数

		LinearLayout mForwardContentLayout;
		LinearLayout mFromAndDateLayout;
		TextView mFromText;
		TextView mPublishDateText;
	}

	public void setData(ArrayList<ZMDiary> mAcqierementList) {
		mArray = mAcqierementList;
	}
	
}

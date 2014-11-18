package com.zhima.ui.common.view.charindex;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.ContactEntry;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.IntentHelper;

/**
 * 
 * @ClassName: ZMCharIndexAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-2 上午10:32:03
*/
public class ZMCharIndexAdapter extends BaseAdapter implements SectionIndexer {

	private BaseActivity mContext;
	private ArrayList<ContactEntry> mNicks;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//TODO
			super.handleMessage(msg);
			
		}
	};

	public ZMCharIndexAdapter(BaseActivity mContext, ArrayList<ContactEntry> nicks) {
		this.mContext = mContext;
		this.mNicks = nicks;
		
		// 排序(实现了中英文混排)
		
//		Arrays.sort(strs, new PinyinComparator());
		Collections.sort(mNicks, new PinyinComparator());
	}

	@Override
	public int getCount() {
		return mNicks.size();
	}

	@Override
	public Object getItem(int position) {
		return mNicks.get(position).getTitle();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ContactEntry contact = mNicks.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.char_index_listview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
			viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
			viewHolder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
			viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.contactitem_phone);
			viewHolder.mPhoneImage = (ImageView) convertView.findViewById(R.id.contactitem_select_cb);
			viewHolder.mItemView = convertView.findViewById(R.id.contactitem_layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String catalog = "*";
		try {
			catalog = PinyinComparator.converterToFirstSpell(contact.getTitle()).substring(0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
			if (position == 0) {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			} else {
				String lastCatalog = PinyinComparator.converterToFirstSpell(mNicks.get(position-1).getTitle()).substring(0, 1);
//			String lastCatalog = PinyinComparator.converterToFirstSpell(
//					mNicks[position - 1]).substring(0, 1);
				
				if (catalog.equals(lastCatalog)) {
					viewHolder.tvCatalog.setVisibility(View.GONE);
				} else {
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
			}
			
			HttpImageLoader.getInstance(mContext).loadImage(contact.getImageUrl(),viewHolder.ivAvatar,
					((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
			
//		viewHolder.ivAvatar.setImageResource(R.drawable.default_image);
			viewHolder.tvPhone.setText(contact.getTelephone());
			viewHolder.tvNick.setText(contact.getTitle());
			
			viewHolder.mPhoneImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO
//				HaloToast.show(mContext, "打电话"+contact.getTitle());
					if(contact.getTelephone()!=null){
						IntentHelper.callPhone(mContext, contact.getTelephone());
					}
				}
			});
			viewHolder.mItemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO
//				HaloToast.show(mContext, "进入企业空间"+contact.getTitle());
					ActivitySwitcher.openSpaceActivity(mContext, contact.getObjectId(), contact.getObjectType(), null, false);
				}
			});
			
			return convertView;
		
	}

	static class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView tvPhone;// 电话
		ImageView mPhoneImage;//电话按钮
		
		View mItemView;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mNicks.size(); i++) {
			String l = PinyinComparator.converterToFirstSpell(mNicks.get(i).getTitle()).substring(0, 1);
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}

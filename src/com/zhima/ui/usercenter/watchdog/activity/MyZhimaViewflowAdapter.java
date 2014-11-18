package com.zhima.ui.usercenter.watchdog.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zhima.R;
import com.zhima.ui.common.view.TitleProvider;

/**
 * @ClassName MyZhimaViewflowAdapter
 * @Description 我的芝麻主页左右滑动控件的adapter
 * @author jiang
 * @date 2012-9-26 下午05:21:33
 */
public class MyZhimaViewflowAdapter extends BaseAdapter implements TitleProvider{

	private static final String[] titles = {"收藏夹","卡片夹","扫码历史"};
	private Context mContext;
	
	private FavoriteView mFavoriteView;
	private CardCaseView mCardCaseView;
	private FavoriteView mScanRecordView;
	
	public static boolean ISFIRST_FAVORITE = true;
	public static boolean ISFIRST_CARD = true;
	public static boolean ISFIRST_RECORD = true;
	
	public MyZhimaViewflowAdapter(Context context){
		this.mContext = context;
		
		ISFIRST_FAVORITE = true;
		ISFIRST_CARD = true;
		ISFIRST_RECORD = true;
	}
	
	public void getServerData(int viewPositon){
		switch (viewPositon) {
		case 0:
			if(ISFIRST_FAVORITE){
				mFavoriteView.requestServiceData(FavoriteView.FAVORITE);
				mFavoriteView.setTabLine(true);
			}
			break;
		case 1:
			if(ISFIRST_CARD){
				mCardCaseView.requestServiceData();
			}
			break;
		case 2:
			if(ISFIRST_RECORD){
				mScanRecordView.requestServiceData(FavoriteView.SCANCODE_RECORD);
			}
			break;
		}
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(position == 0){
			if(mFavoriteView!=null){
				return mFavoriteView.getView();
			}
			mFavoriteView = new FavoriteView(mContext);
			mFavoriteView.getView().findViewById(R.id.layout_change_mode).setVisibility(View.VISIBLE);
			getServerData(FavoriteView.FAVORITE);
			return mFavoriteView.getView();
		}else if(position == 1){
			if(mCardCaseView!=null){
				return mCardCaseView.getView();
			}
			mCardCaseView = new CardCaseView(mContext);
			return mCardCaseView.getView();
		}else if(position == 2){
			if(mScanRecordView!=null){
				return mScanRecordView.getView();
			}
			mScanRecordView = new FavoriteView(mContext);
			mScanRecordView.getView().findViewById(R.id.layout_change_mode).setVisibility(View.GONE);
			return mScanRecordView.getView();
		}
		return new View(mContext);
	}

	@Override
	public String getTitle(int position) {
		// TODO Auto-generated method stub
		return titles[position];
	}

}

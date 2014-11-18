package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zhima.R;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.ContactService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.charindex.CharSideBar;
import com.zhima.ui.common.view.charindex.ZMCharIndexAdapter;

/**
 * 个人管家 通讯录 View
 * @ClassName: UserContactView
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-18 下午1:53:07
*/
public class UserContactView  {

	private static final int LOAD_DATA_END = 100;
	
	public static final int SPACE_CONTACT = 1000;
	public static final int PERSONAL_CONTACT = 1001;
	
	private BaseActivity mContext;
	private View mContactView;
	private ArrayList<ContactEntry> dataList;
	private ListView lvContact;
	private ZMCharIndexAdapter zmCharIndexAdapter;
	
	
	protected ArrayList<ContactEntry> spaceList;
	protected ArrayList<ContactEntry> personalList;
	protected ArrayList<ContactEntry> listData;

	private LinearLayout emptyLayout;

	private View contentView;

	private CharSideBar indexBar;;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//TODO
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_DATA_END:
				listData = spaceList;
				setEmptyLayoutVisible();
				zmCharIndexAdapter = new ZMCharIndexAdapter(mContext,listData);
				lvContact.setAdapter(zmCharIndexAdapter);
				indexBar.setListView(lvContact); 
				break;
			}
		}
	};


	private UserContactView(){};
	
	public UserContactView(BaseActivity context,View view){
		this.mContext = context;
		this.mContactView = view;
		lvContact = (ListView)mContactView.findViewById(R.id.lstv_userCenter_contact_list);
		emptyLayout = (LinearLayout) mContactView.findViewById(R.id.layout_empty);
		contentView = mContactView.findViewById(R.id.layout_conten_view);
		indexBar = (CharSideBar) mContactView.findViewById(R.id.sb_userCenter_contact_sidebar); 
		initData();
		
	}
	
	private void initData() {
		new Thread(){
			public void run() {
				RefreshListData<ContactEntry> spaceListData = ContactService.getInstance(mContext).getSpaceContactRefreshData();
				RefreshListData<ContactEntry> personalListData = ContactService.getInstance(mContext).getPersonContactRefreshData();
				
				spaceList = spaceListData.getDataList();
				personalList = personalListData.getDataList();
						
//				createData();
				
				Message msg = new Message();
				msg.what = LOAD_DATA_END;
				mHandler.sendMessage(msg);
			};
		}.start();
	}
	
	
	public int changeList(int contactCode){
		if(contactCode == SPACE_CONTACT){
			if(listData != spaceList){
				listData = spaceList;
				zmCharIndexAdapter.notifyDataSetChanged();
			}
		}else if(contactCode == PERSONAL_CONTACT){
			if(listData != personalList){
				listData = personalList;
				zmCharIndexAdapter.notifyDataSetChanged();
			}
		}
		setEmptyLayoutVisible();
		
		return listData!=null?listData.size():0;
	}
	
	private void setEmptyLayoutVisible() {
		//TODO
		if(listData!=null && listData.size() != 0){
			emptyLayout.setVisibility(View.GONE);
			contentView.setVisibility(View.VISIBLE);
		}else{
			emptyLayout.setVisibility(View.VISIBLE);
			contentView.setVisibility(View.GONE);
		}
	}
	
	private void createData() {
		//TODO
		ContactEntry e1 = new ContactEntry();
		e1.setTitle("为人体后");
		e1.setTelephone("11111");
		
		ContactEntry e2 = new ContactEntry();
		e2.setTitle("etyrty");
		e2.setTelephone("22222");
		
		ContactEntry e3 = new ContactEntry();
		e3.setTitle("染发膏");
		e3.setTelephone("33333");
		
		ContactEntry e4 = new ContactEntry();
		e4.setTitle("耳机坛跟ie人 ");
		e4.setTelephone("444444");
		
		ContactEntry e5 = new ContactEntry();
		e5.setTitle("&*&**");
		e5.setTelephone("55555");
		
		ContactEntry e6 = new ContactEntry();
		e6.setTitle("认同感会计师的规划");
		e6.setTelephone("66666");
		
		ContactEntry e7 = new ContactEntry();
		e7.setTitle("才下课就不会");
		e7.setTelephone("77777");
		
		ContactEntry e8 = new ContactEntry();
		e8.setTitle("怕无色如何");
		e8.setTelephone("88888");
		
		ContactEntry e9 = new ContactEntry();
		e9.setTitle("为人体65465后");
		e9.setTelephone("888");
		
		ContactEntry e0 = new ContactEntry();
		e0.setTitle("44565465");
		e0.setTelephone("99999");
		
		ContactEntry e18 = new ContactEntry();
		e18.setTitle("人了可通过及染色");
		e18.setTelephone("0098907");
		
		ContactEntry e187 = new ContactEntry();
		e187.setTitle("恶58坚实的佛光");
		e187.setTelephone("493875375637925725");
		
		
		
		spaceList.add(e1);
		spaceList.add(e2);
		spaceList.add(e3);
		spaceList.add(e4);
		spaceList.add(e5);
		spaceList.add(e6);
		spaceList.add(e7);
		spaceList.add(e8);
		spaceList.add(e9);
		spaceList.add(e0);
		spaceList.add(e18);
		spaceList.add(e187);
	}
	
}

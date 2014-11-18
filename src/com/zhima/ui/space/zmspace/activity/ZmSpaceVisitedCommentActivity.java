package com.zhima.ui.space.zmspace.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetVisitorMessageListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.VisitorMessage;
import com.zhima.data.service.RegionService;
import com.zhima.data.service.UserService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.tools.UserUtils.GetUserFinishListener;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 看看谁来过_访客留言列表
 * @ClassName: ZmSpaceVisitedCommentActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-24 下午10:05:20
*/
public class ZmSpaceVisitedCommentActivity extends BaseActivity {

	private ImageView mHeadImage;
	private TextView mNameText;
	private TextView mAddressText;
	private ListView mCommentList;
	
	/** 访客id */
	private long mVisitedId;
	private ArrayList<VisitorMessage> visitorMsgList;
	private ZhimaAdapter<VisitorMessage> mZhimaAdapter;
	private long mUserId;
	private User mVisitor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_zmspace_visited_comment_activity);
		
		initData();
		setTopBar();
		findView();
		setUpView();
//		createData();
		getVisitorMessageList();
	}

	private void createData() {
		//TODO
		visitorMsgList = new ArrayList<VisitorMessage>();
//		VisitorMessage msg1 = new VisitorMessage();
//		msg1.setContent("setContentsetContentsetContentsetContentsetContentsetContentsetContent");
//		msg1.setPostTime(476845356);
//		VisitorMessage msg2 = new VisitorMessage();
//		msg2.setContent("setContentsetContentsetContentntentsetContentntentsetContentsetContentsetContentsetContentsetContent");
//		msg2.setPostTime(658465356);
//		VisitorMessage msg3 = new VisitorMessage();
//		msg3.setContent("setContentsetContenntsetContentsetContentsetContentsetContent");
//		msg3.setPostTime(345436345);
//		VisitorMessage msg5 = new VisitorMessage();
//		msg5.setContent(StringHelper.toDBC("setContentsetCoeoriid人家干涩i热肉体噩噩日欧体i瑞瑞诶哦让他就ie肉碱鹅肉俄日哦天太热i喔他及诶哦 tContentsetContentsetContentsetContent"));
//		msg5.setPostTime(345436345);
//		
//		visitorMsgList.add(msg1);
//		visitorMsgList.add(msg5);
//		visitorMsgList.add(msg2);
//		visitorMsgList.add(msg3);
		
		setAdapter();
	}
	
	private void initData() {
		//TODO
		mVisitedId = getIntent().getLongExtra(ACTIVITY_EXTRA, -1);
		mUserId = getIntent().getLongExtra(ACTIVITY_EXTRA2, -1);
		
//		System.out.println("mVisitedId:"+mVisitedId);
		
		mVisitor = UserService.getInstance(this).getUser(mUserId);
	}

	/**
	 * 从服务器获取访客留言列表
	 * @Title: getVisitorMessageList
	 * @Description: TODO
	 */
	private void getVisitorMessageList() {
		//TODO
		ZMSpaceService.getInstance(this).getVisitorMessageList(mVisitedId, true, this);
	}
	
	private void setTopBar() {
		//TODO
		ViewInitTools.setTopBar( this, "访客留言", View.GONE, null);
	}

	private void findView() {
		//TODO
		mHeadImage = (ImageView) this.findViewById(R.id.img_zmspace_visited_headImage);
		mNameText = (TextView) this.findViewById(R.id.txt_zmspace_visited_name);
		mAddressText = (TextView) this.findViewById(R.id.txt_zmspace_visited_address);
		mCommentList = (ListView) this.findViewById(R.id.lstv_zmspace_visited_commentList);
	}

	private void setUpView() {
		//TODO
//		if(mVisitor!=null){
//			HttpImageLoader.getInstance(this).loadImage(mVisitor.getImageUrl(),mHeadImage,
//					getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
//			mNameText.setText(mVisitor.getNickname());
//			mAddressText.setText(GenderType.getGenderLabel(mVisitor.getGender())+" "+RegionService.getInstance(this).getRegionById(mVisitor.getCityId()));
//		}
		if(mUserId!=-1){
			
			UserUtils util = new UserUtils(this);
			util.getUserInfo(mUserId, new GetUserFinishListener() {
				
				@Override
				public void getUserFinish(User user, boolean isMySelf) {
					//TODO
					HttpImageLoader.getInstance(ZmSpaceVisitedCommentActivity.this).loadImage(user.getImageUrl(),mHeadImage,
							getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
					mNameText.setText(user.getNickname());
					mAddressText.setText(GenderType.getGenderLabel(user.getGender())+" "+RegionService.getInstance(ZmSpaceVisitedCommentActivity.this).getRegionStr(user.getCityId()));
				}
			});
			mHeadImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UserUtils util = new UserUtils(ZmSpaceVisitedCommentActivity.this);
					util.switchAcitivity(mUserId, false);
				}
			});
		}
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if(protocol.isHttpSuccess()){
			if(protocol.getProtocolType() == ProtocolType.GET_SPACE_VISITOR_MESSAGE_LIST_PROTOCOL){
				GetVisitorMessageListProtocol p = (GetVisitorMessageListProtocol) protocol;
				RefreshListData<VisitorMessage> dataList = p.getDataList();
				visitorMsgList = dataList.getDataList();
//				mZhimaAdapter.notifyDataSetChanged();
				setAdapter();
			}
		}else{
			HaloToast.show(this, R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
	
	private void setAdapter() {
		//TODO
		
		mZhimaAdapter = new ZhimaAdapter<VisitorMessage>(this,R.layout.space_zmspace_visited_comment_item, visitorMsgList) {

			@Override
			public Object createViewHolder(View view, VisitorMessage data) {
				ViewHolder holder = new ViewHolder();
				holder.mComtentText = (TextView) view.findViewById(R.id.txt_zmspace_visited_comment_comtent);
				holder.mTimeText = (TextView) view.findViewById(R.id.txt_zmspace_visited_comment_time);
				return holder;
			}

			@Override
			public void bindView(VisitorMessage data, int position, View view) {
				ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
				
				viewHolder.mComtentText.setText(data.getContent());
				viewHolder.mTimeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.getPostTime()));
			}
			
			
			@Override
			public int getCount() {
				//TODO
				return Math.min(visitorMsgList.size(),3);
			}



			class ViewHolder{
				TextView mComtentText;
				TextView mTimeText;
			}
		};
		
		mCommentList.setAdapter(mZhimaAdapter);
	}
	
	@Override
	public void onDestroy() {
		//TODO
		super.onDestroy();
		if(visitorMsgList!=null){
			visitorMsgList = null;
			mZhimaAdapter = null;
		}
	}
	
}

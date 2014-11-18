package com.zhima.ui.tools;

import android.content.Intent;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.FriendsProtocolHandler.CheckFriendProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetUserInfoProtocol;
import com.zhima.data.model.User;
import com.zhima.data.service.FriendService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.usercenter.activity.PersonalCenterMainActivity;
import com.zhima.ui.usercenter.activity.PersonalDataActivity;

/**
 * 获取用户信息  查询是否好友 工具类
 * @ClassName: UserUtils
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-23 下午8:21:43
*/
public class UserUtils implements IHttpRequestCallback {
	
	private BaseActivity mActivity;
	private GetUserFinishListener mGetUserFinishListener;
	private CheckFriendListener mCheckFriendListener;
	
	private UserUtils(){}
	public UserUtils(BaseActivity activity){
		this.mActivity = activity;
	}
	
	/**
	 * 获取用户信息
	 * @Title: getUserInfo
	 * @Description: TODO
	 * @param userId
	 * @param listener
	 */
	public void getUserInfo(long userId,GetUserFinishListener listener){
		this.mGetUserFinishListener = listener;
		UserService.getInstance(mActivity).getUserInfo(userId, this);
	}
	
	/** 
	 * 查询是否是好友 
	 * @Title: checkIsMyfriend 
	 * @Description: TODO 
	 * @param userId 
	 * @param listener 
	 */
	public void checkIsMyfriend(long userId,CheckFriendListener listener){
		this.mCheckFriendListener = listener;
		FriendService.getInstance(mActivity).checkFriend(userId, this);
	}
	
	/**
	 * 个人中心跳转
	 * @Title: switchAcitivity
	 * @Description: TODO
	 * @param userId
	 */
	public void switchAcitivity(final long userId,final boolean isFinish){
		
		if(UserService.getInstance(mActivity).getUser(userId) !=null){
			final boolean isMySelf = UserService.getInstance(mActivity).isMySelf(userId);
			startIntent(userId, isMySelf, isFinish);
		}else{
			getUserInfo(userId, new GetUserFinishListener() {
				
				@Override
				public void getUserFinish(User user, boolean isMySelf) {
					startIntent(userId, isMySelf, isFinish);
				}
			});
		}
	} 
	
	private void startIntent(final long userId,final boolean isMySelf,final boolean isFinish) {
		//TODO
		if(isMySelf){
			Intent intent = new Intent(mActivity,PersonalCenterMainActivity.class);
			intent.putExtra(PersonalDataActivity.USER_ID, userId);
			intent.putExtra(PersonalDataActivity.IS_MYSELF, isMySelf);
			intent.putExtra(PersonalDataActivity.IS_MYFRIEND, false);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mActivity.startActivity(intent);
			if(isFinish){
				mActivity.finish();
			}
		}else{
			checkIsMyfriend(userId, new CheckFriendListener() {
				
				@Override
				public void checkFriend(boolean isFriend) {
					Intent intent = new Intent(mActivity,PersonalDataActivity.class);
					intent.putExtra(PersonalDataActivity.USER_ID, userId);
					intent.putExtra(PersonalDataActivity.IS_MYSELF, isMySelf);
					intent.putExtra(PersonalDataActivity.IS_MYFRIEND, isFriend);
					
					mActivity.startActivity(intent);
					if(isFinish){
						mActivity.finish();
					}
				}
			});
		}
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		//TODO
		mActivity.startWaitingDialog("", "请稍等");
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		//TODO
		if(protocol.isHttpSuccess()){
			if(protocol.getProtocolType() == ProtocolType.GET_USERINFO_PROTOCOL){
				GetUserInfoProtocol p = (GetUserInfoProtocol) protocol;
				User user = p.getUser();
				boolean isMySelf = UserService.getInstance(mActivity).isMySelf(user.getId());
				if(mGetUserFinishListener != null){
					mGetUserFinishListener.getUserFinish(user,isMySelf);
				}
			}else if(protocol.getProtocolType() == ProtocolType.CHECK_FRIEND_PROTOCOL){
				CheckFriendProtocol p = (CheckFriendProtocol) protocol;
				boolean isFriend = p.isFriend();
				if(mCheckFriendListener != null){
					mCheckFriendListener.checkFriend(isFriend);
				}
			}
		}else{
			HaloToast.show(mActivity, R.string.network_request_failed);
		}
		mActivity.dismissWaitingDialog();
	}
	
	public interface GetUserFinishListener{
		void getUserFinish(User user,boolean isMySelf);
	}
	
	public interface CheckFriendListener{
		void checkFriend(boolean isFriend);
	}
}

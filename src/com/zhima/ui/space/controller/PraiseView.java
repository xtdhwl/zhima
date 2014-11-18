package com.zhima.ui.space.controller;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.data.model.PraiseInfo;

/**
 * @ClassName:PraiseView
 * @Description:自定义赞view
 * @author liqilong
 * @date 2012-8-28 上午9:40:48
 * 
 */
public class PraiseView extends LinearLayout {
	private Context mContext;
	private HashMap<Praise, View> mViewMap;
	private PraiseInfo mPraiseInfo;
	private OnItemClickListener mOnItemClickListener;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			View view = (View) msg.obj;
			view.findViewById(R.id.txt_one).setVisibility(View.INVISIBLE);
		}

	};

	public PraiseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setOrientation(LinearLayout.HORIZONTAL);
		mViewMap = new HashMap<Praise, View>();
	}

	public PraiseView(Context context) {
		this(context, null);
	}

	/**
	 * @Title: addPraise
	 * @Description: 添加赞View
	 * @param praiseType
	 * @return PraiseView
	 */
	public PraiseView addPraiseView(int... praiseTypes) {
//		mViewMap.clear();
//		this.removeAllViews();
		
		int length = praiseTypes.length;
		for (int i = 0; i < length; i++) {
			int praiseType = praiseTypes[i];
			Praise praise = new Praise();
			String str = mContext.getText(PraiseKind.getPraiseLabel(praiseType)).toString();
			praise.setType(praiseType);
			praise.setStr(str);
			//TODO 默认值是多少
//			praise.setCount(100);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

			View view = View.inflate(mContext, R.layout.space_praise_view_item, null);
			TextView iv = (TextView) view.findViewById(R.id.txt_praise);
			//分割线隐藏
			if (i == length - 1) {
				view.findViewById(R.id.line).setVisibility(View.GONE);
			}
			iv.setText(str);
			iv.setOnClickListener(new praiseClick(praise));
			this.addView(view, params);
			mViewMap.put(praise, view);

		}
		return this;
	}

	/**
	 * @Title: setPraise
	 * @Description: 设置赞View 计数
	 * @param praiseType
	 * @param count
	 * @return boolean
	 */
	public boolean setPraise(int praiseType, int count) {
		for (Map.Entry<Praise, View> map : mViewMap.entrySet()) {
			Praise praise = map.getKey();
			if (praise.type == praiseType) {
				View view = map.getValue();
				TextView praiseText = (TextView) view.findViewById(R.id.txt_praise);
				praiseText.setText(praise.getStr() + count);
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: addPraiseInfo
	 * @Description: 添加赞,并根据类型显示加赞成功
	 * @param praiseInfo 赞信息
	 * @param praiseType 显示的赞类型
	 * void
	 */
	public void addPraiseInfo(PraiseInfo praiseInfo, int praiseType) {
		for (Map.Entry<Praise, View> map : mViewMap.entrySet()) {
			Praise praise = map.getKey();
			if (praise.type == praiseType) {
				int count = praiseInfo.getPraiseCount(praiseType);
				praise.setCount(count);
				
				Animation	aimation = AnimationUtils.loadAnimation(mContext, R.anim.praise_scale);
				//这是View
				View view = map.getValue();
				TextView praiseText = (TextView) view.findViewById(R.id.txt_praise);
				TextView addText = (TextView) view.findViewById(R.id.txt_one);
				addText.setVisibility(View.VISIBLE);
				addText.startAnimation(aimation);
				praiseText.setText(praise.getStr() + count);
				
				Message msg = new Message();
				msg.obj = addText;
				mHandler.sendMessageDelayed(msg, 2000);
				break;
			}
		}
	}

	/**
	 * @Title: addPraiseInfo
	 * @Description: 添加赞
	 * @param praiseInfo
	 * @return
	 * boolean
	 */
	public void addPraiseInfo(PraiseInfo praiseInfo) {
		for (Map.Entry<Praise, View> map : mViewMap.entrySet()) {
			Praise praise = map.getKey();
			View view = map.getValue();
			TextView praiseText = (TextView) view.findViewById(R.id.txt_praise);
			int count = praiseInfo.getPraiseCount(praise.getType());
			praise.setCount(count);
			praiseText.setText(praise.getStr() + count);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private class praiseClick implements OnClickListener {
		Praise praise;

		public praiseClick(Praise praise) {
			this.praise = praise;
		}

		@Override
		public void onClick(View v) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClickListener(praise);
			}
		}

	}

	public interface OnItemClickListener {
		void onItemClickListener(Praise praise);
	}

	/**
	 * 赞
	 */
	public class Praise {
		public int type;
		public String str;
		public int count;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

	}
}

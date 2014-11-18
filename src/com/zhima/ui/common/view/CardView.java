package com.zhima.ui.common.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.logger.Logger;

/**
 * @ClassName:CardView
 * @Description:卡片View.在空间模块展示商品
 * @author liqilong
 * @date 2012-8-9 下午7:17:55
 * 
 */
public class CardView extends RelativeLayout {
	private static final String TAG = "CardView";
	/** item默认个数 */
	private static final int ITEM_COUNT = 3;
	protected Context mContext;
	// 在xml中设置卡片个数 和 标题
	/** 设置卡片个数 */
	private int mItemCount;
	/** 标题栏内容 */
	private String mTitle;
	/** 标题栏TextView */
	private TextView mTitleText;
	/** 卡片 GridView */
	private GridView mCardGradView;
	/** CardAdapter代理 */
	private ProxyBaseAdapter mProxyBaseAdapter;
	/** 箭头Image */
	private ImageView mArrowImag;

	private OnCardItemClickListener mOnItemClickListener;
	private View mDivideView;

	public CardView(Context context) {
		this(context, null);
	}

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.card_view, this);

		mDivideView = findViewById(R.id.view_line);
		mTitleText = (TextView) findViewById(R.id.txt_title);
		mCardGradView = (GridView) findViewById(R.id.grdv);
		mCardGradView.setVerticalFadingEdgeEnabled(false);
		mArrowImag = (ImageView) findViewById(R.id.arrow);
		mArrowImag.setVisibility(View.GONE);

		setLinstener();
		Resources resoutces = context.getResources();
		TypedArray a = resoutces.obtainAttributes(attrs, R.styleable.CardView);
		mTitle = a.getString(R.styleable.CardView_title);
		if (mTitle != null) {
			mTitleText.setText(mTitle);
		}
		mItemCount = a.getInteger(R.styleable.CardView_itemCount, ITEM_COUNT);
		mCardGradView.setNumColumns(mItemCount);
		mCardGradView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return event.getAction() == MotionEvent.ACTION_MOVE ? true : false;
			}
		});

		//在xml设置GradView Padding 和margin
		int leftPadding = -1;
		int topPadding = -1;
		int rightPadding = -1;
		int bottomPadding = -1;
		int padding = a.getDimensionPixelSize(R.styleable.CardView_card_padding, -1);
		if (padding >= 0) {
			leftPadding = padding;
			topPadding = padding;
			rightPadding = padding;
			bottomPadding = padding;
		} else {
			leftPadding = a.getDimensionPixelSize(R.styleable.CardView_card_paddingLeft, 0);
			topPadding = a.getDimensionPixelSize(R.styleable.CardView_card_paddingTop, 0);
			rightPadding = a.getDimensionPixelSize(R.styleable.CardView_card_paddingRight, 0);
			bottomPadding = a.getDimensionPixelSize(R.styleable.CardView_card_paddingBottom, 0);
		}
		mCardGradView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

		int leftMargin;
		int topMargin;
		int rightMargin;
		int bottomMargin;
		// 得到card margin
		int margin = a.getDimensionPixelSize(R.styleable.CardView_card_layout_margin, -1);
		if (margin >= 0) {
			leftMargin = margin;
			topMargin = margin;
			rightMargin = margin;
			bottomMargin = margin;
		} else {
			leftMargin = a.getDimensionPixelSize(R.styleable.CardView_card_layout_marginLeft, 0);
			topMargin = a.getDimensionPixelSize(R.styleable.CardView_card_layout_marginTop, 0);
			rightMargin = a.getDimensionPixelSize(R.styleable.CardView_card_layout_marginRight, 0);
			bottomMargin = a.getDimensionPixelSize(R.styleable.CardView_card_layout_marginBottom, 0);
		}
		RelativeLayout.LayoutParams llParams = (android.widget.RelativeLayout.LayoutParams) mCardGradView
				.getLayoutParams();
		llParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

		int horizontalSpacing = a.getDimensionPixelSize(R.styleable.CardView_card_horizontalSpacing, -1);
		if (horizontalSpacing >= 0) {
			mCardGradView.setHorizontalSpacing(horizontalSpacing);
		}
		int verticalSpacing = a.getDimensionPixelSize(R.styleable.CardView_card_verticalSpacing, -1);
		if (verticalSpacing >= 0) {
			mCardGradView.setVerticalSpacing(verticalSpacing);
		}
		a.recycle();
	}

	// 设置卡片点击事件
	private void setLinstener() {
		mCardGradView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClickListener(view, position, false);
				}
			}
		});
		// 监听点击监听
		mArrowImag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClickListener(view, -1, true);
				}
			}
		});
	}

	/**
	 * @Title: setTitle
	 * @Description: 设置Title
	 * @param title 标题
	 */
	public void setTitle(String title) {
		mTitle = title;
		mTitleText.setText(title);
	}

	/**
	 * @Title: getView
	 * @Description: 根据index获取卡片中的view
	 * @param index 索引
	 */
	public View getView(int index) {
		return mCardGradView.getChildAt(index);
	}
	
	/**
	* @Title: getDivideLineView
	* @Description: 返回头部分割线
	* @return
	 */
	public View getDivideLineView(){
		return mDivideView;
	}
	
	/**
	* @Title: getArrowView
	* @Description: 返回更多箭头View
	* @return
	 */
	public ImageView getArrowView(){
		return mArrowImag;
	}

	/**
	 * @Title: setAdapter
	 * @Description: 设置Adapter数据
	 * @param CardBaseAdapter适配器
	 */
	public void setAdapter(BaseAdapter adapter) {
		if (adapter == null || adapter.isEmpty()) {
			return;
		}

		mProxyBaseAdapter = new ProxyBaseAdapter(adapter, mItemCount);
		mProxyBaseAdapter.registerDataSetObserver(adapterDataObserver);
		showArrow();
		mCardGradView.setAdapter(mProxyBaseAdapter);
	}

	/**
	 * @Title: setOnItemClickListener
	 * @Description: 设置Item点击事件
	 * @param l
	 *            void
	 */
	public void setOnItemClickListener(OnCardItemClickListener l) {
		mOnItemClickListener = l;
	}

	// Adapter数据监听
	private DataSetObserver adapterDataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			Logger.getInstance(TAG).debug("adapterDataObserver onChanged() ");
			showArrow();
		}

		public void onInvalidated() {
			Logger.getInstance(TAG).debug("adapterDataObserver onInvalidated() ");
		};
	};

	/*
	 * private class srcDataObserver extends DataSetObserver{ BaseAdapter
	 * adapter; public srcDataObserver(BaseAdapter adapter){ this.adapter =
	 * adapter; }
	 * 
	 * @Override public void onChanged() {
	 * mProxyBaseAdapter.setTargetAdapter(adapter); showArrow(); }
	 * 
	 * @Override public void onInvalidated() { super.onInvalidated(); }
	 * 
	 * }
	 */

	private boolean isShowArrow() {
		// 是否显示箭头,true为显示,不显示为false
		if (mProxyBaseAdapter == null) {
			return false;
		}
		return mProxyBaseAdapter.getTargetItemCount() > mItemCount;
	}

	/**
	 * 设置箭头是否显示
	 */
	private void showArrow() {
		if (isShowArrow()) {
			mArrowImag.setVisibility(View.VISIBLE);
		} else {
			mArrowImag.setVisibility(View.GONE);
		}
	}

	public void setArrowVisibility(int state) {
		mArrowImag.setVisibility(state);
	}

	/**
	 * @ClassName:OnCardItemClickListener
	 * @Description:Item点击事件监听器.view为当前的Item.position为当前Item的位置(如果为箭头则返回-1).isArrow.如果当前Item为箭头则返回true否则返回false.
	 * @author liqilong
	 * @date 2012-8-10 下午2:25:48
	 * 
	 */
	public interface OnCardItemClickListener {
		void onItemClickListener(View view, int position, boolean isArrow);
	}

	// 数据发生改变
	public interface OnChangeListener {
		void onChangeListener();
	}

	public GridView getGridView() {
		return mCardGradView;
	}

	public TextView getTitleView() {
		return mTitleText;
	}

	/**
	 * @ClassName:ProxyBaseAdapter
	 * @Description:ProxyBaseAdapter,CardView 中的Adapter代理
	 * @author liqilong
	 * @date 2012-8-15 上午9:31:15
	 * 
	 */
	private class ProxyBaseAdapter extends BaseAdapter {

		private BaseAdapter mTargetAdapter;
		private int mMaxItem;

		public ProxyBaseAdapter(BaseAdapter mProxyAdapter, int maxItem) {
			super();
			this.mTargetAdapter = mProxyAdapter;
			mMaxItem = maxItem;
		}

		@Override
		public int getCount() {
			return Math.min(mMaxItem, mTargetAdapter.getCount());
		}

		// 得到原Adapter 中的Item个数,来判断是否显示箭头
		/**
		 * return 原Adapter中的实际个数
		 */
		public int getTargetItemCount() {
			return mTargetAdapter.getCount();
		}

		/**
		 * 设置原适配Adapter
		 */
		public void setTargetAdapter(BaseAdapter adapter) {
			mTargetAdapter = adapter;
		}

		@Override
		public Object getItem(int position) {
			return mTargetAdapter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return mTargetAdapter.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mTargetAdapter.getView(position, convertView, parent);
		}

		@Override
		public boolean hasStableIds() {
			return mTargetAdapter.hasStableIds();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			mTargetAdapter.registerDataSetObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			mTargetAdapter.unregisterDataSetObserver(observer);
		}

		@Override
		public void notifyDataSetChanged() {
			mTargetAdapter.notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetInvalidated() {
			mTargetAdapter.notifyDataSetInvalidated();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return mTargetAdapter.areAllItemsEnabled();
		}

		@Override
		public boolean isEnabled(int position) {
			return mTargetAdapter.isEnabled(position);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return mTargetAdapter.getDropDownView(position, convertView, parent);
		}

		@Override
		public int getItemViewType(int position) {
			return mTargetAdapter.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			return mTargetAdapter.getViewTypeCount();
		}

		@Override
		public boolean isEmpty() {
			return mTargetAdapter.isEmpty();
		}
	}
}

package com.zhima.ui.common.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.zhima.R;

public class ThreeList extends LinearLayout {

	protected ListAdapter mAdapter;
	private boolean mDataChanged = false;
	private LinearLayout ll1;
	private LinearLayout ll2;
	private LinearLayout ll3;

	private View view1;
	private View view2;

	private OnItemClickListener mOnItemClickListener;

	public ThreeList(Context context) {
		this(context, null);
	}

	public ThreeList(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.three_list, this);

		ll1 = (LinearLayout) findViewById(R.id.layout_1);
		ll2 = (LinearLayout) findViewById(R.id.layout_2);
		ll3 = (LinearLayout) findViewById(R.id.layout_3);

		view1 = findViewById(R.id.view_1);
		view2 = findViewById(R.id.view_2);

		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		ll1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(null, ThreeList.this, 0, -1);
				}
			}
		});
		ll2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(null, ThreeList.this, 1, -2);
				}
			}
		});
		ll3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(null, ThreeList.this, 2, -3);
				}
			}
		});
	}

	public ListAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		setData();
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
//			System.out.println("onChanged");
			setData();
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
//			System.out.println("onInvalidated");
			setData();
			invalidate();
			requestLayout();
		}

	};

	private void reset() {
		ll1.removeAllViews();
		ll2.removeAllViews();
		ll3.removeAllViews();

	}

	private void setData() {
		reset();
		int count = mAdapter.getCount() > 3 ? 3 : mAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View view = mAdapter.getView(i, null, this);
			switch (i) {
			case 0:
				ll1.addView(view);
				break;
			case 1:
				ll2.addView(view);
				break;
			case 2:
				ll3.addView(view);
				break;
			}
		}
		if (count == 0) {
			//如果item = 0;
			ll1.removeAllViews();
			ll2.removeAllViews();
			ll3.removeAllViews();
			ll1.setVisibility(View.GONE);
			ll2.setVisibility(View.GONE);
			ll3.setVisibility(View.GONE);

			view1.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
		} else if (count == 1) {
			//如果item = 1;
			ll1.setVisibility(View.VISIBLE);

			ll2.removeAllViews();
			ll3.removeAllViews();
			ll2.setVisibility(View.GONE);
			ll3.setVisibility(View.GONE);

			view1.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
		} else if (count == 2) {
			//如果item = 2;
			ll1.setVisibility(View.VISIBLE);
			ll2.setVisibility(View.VISIBLE);

			ll3.removeAllViews();
			ll3.setVisibility(View.GONE);

			view1.setVisibility(View.VISIBLE);
			view2.setVisibility(View.GONE);
		} else if (count == 3) {
			//如果item = 3;
			ll1.setVisibility(View.VISIBLE);
			ll2.setVisibility(View.VISIBLE);
			ll3.setVisibility(View.VISIBLE);

			view1.setVisibility(View.VISIBLE);
			view2.setVisibility(View.VISIBLE);
		}

	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}
}

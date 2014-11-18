package com.zhima.ui.usercenter.data.lattice.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.LatticeProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.space.activity.PreviewActivity;
import com.zhima.ui.usercenter.data.lattice.adapter.LatticePagerAdapter;

public class LatticeInfoActivity extends BaseActivity {

	protected static final String TAG = LatticeInfoActivity.class.getSimpleName();
	private static final int request_edit_code = 0;

	private User mUser;
	private ViewPager mViewPager;
	private RefreshListData<LatticeProduct> mCacheUserLatticeProduct;
	private List<LatticeProduct> mLatticeProductList;
	private LatticePagerAdapter mLatticePagerAdapter;

	private int mPosition;
	private long mUserId;
//	private long mLatticePosition;mSelectPosition

	private UserService mUserService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_center_lattice_pager_activity);
		setTopbar();
		findView();
		init();

		Intent it = getIntent();
		mUserId = it.getLongExtra(ACTIVITY_EXTRA, -1);
		mPosition = it.getIntExtra(ACTIVITY_EXTRA2, -1);
		mUser = mUserService.getUser(mUserId);
		getServiceData();
		setListener();
		checkUser(mUserId);
	}

	private void setListener() {
		// TODO Auto-generated method stub

	}

	private void init() {
		mUserService = UserService.getInstance(this);
	}

	private void getServiceData() {
		mCacheUserLatticeProduct = mUserService.getCacheUserLatticeProduct(mUserId);
		if (mCacheUserLatticeProduct.isEmpty()) {
//			mUserService.getLatticeProductList(mUserId, false, this);
		} else {
			setLatticeView();
			setSelection();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == request_edit_code) {
				setLatticeView();
			}
		}
	}

	private void setLatticeView() {
		mLatticeProductList = mCacheUserLatticeProduct.getDataList();
		if (mLatticePagerAdapter == null) {
			mLatticePagerAdapter = new LatticePagerAdapter(this, mLatticeProductList);
			mLatticePagerAdapter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.img_photo:
						LatticeProduct lattice = (LatticeProduct) v.getTag();
						Intent it = new Intent(LatticeInfoActivity.this, PreviewActivity.class);
						it.putExtra(PreviewActivity.ACTIVITY_URL, lattice.getImageUrl());
						it.putExtra("type", ImageScaleType.LARGE);
						startActivity(it);
						break;
					}
				}
			});
			mViewPager.setAdapter(mLatticePagerAdapter);
		} else {
			mLatticePagerAdapter.setData(mLatticeProductList);
			mLatticePagerAdapter.notifyDataSetChanged();
		}
	}

	private void setSelection() {
		if (mViewPager.getAdapter().getCount() > mPosition) {
			mViewPager.setCurrentItem(mPosition);
		}
	}

	private class delectLatticeProductHttpCallBack implements IHttpRequestCallback {
		private LatticeProduct lattice;

		public delectLatticeProductHttpCallBack(LatticeProduct lattice) {
			super();
			this.lattice = lattice;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 删除格子铺成功
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.delete_success);
					mLatticeProductList.remove(lattice);
					if (mLatticeProductList.size() <= 0) {
						finish();
					} else {
						setLatticeView();
					}
				} else {
					//删除格子铺失败
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			LatticeProduct data = getCurrentLatticeProduct();
			if (data != null && mUser != null) {
				String sms_message = String.format(getText(R.string.lattice_sms_message).toString(),
						mUser.getNickname(), mUser.getZMUserId());
				String content = sms_message;
				SharePopupMenu.show(LatticeInfoActivity.this, LatticeInfoActivity.this, v, sms_message, content);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	private LatticeProduct getCurrentLatticeProduct() {
		int itemPosition = mViewPager.getCurrentItem();
		if (mLatticeProductList.size() > itemPosition) {
			return mLatticeProductList.get(itemPosition);
		}
		return null;
	}

	/** 标题栏:删除 */
	private View.OnClickListener deleteTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MessageDialog dialog = new MessageDialog(LatticeInfoActivity.this, v);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.show();
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					LatticeProduct latticeProduct = getCurrentLatticeProduct();
					if (latticeProduct != null) {
						startWaitingDialog(null, R.string.loading);
						mUserService.deleteLatticeProduct(latticeProduct.getId(), new delectLatticeProductHttpCallBack(
								latticeProduct));
					} else {
						ErrorManager.showErrorMessage(getApplicationContext());
					}
				}

				@Override
				public void onLeftBtClick() {
				}
			});
		}
	};
	/** 标题栏:编辑 */
	private View.OnClickListener editTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			LatticeProduct latticeProduct = getCurrentLatticeProduct();
			if (latticeProduct != null) {
				Intent it = new Intent(LatticeInfoActivity.this, LatticeEditActivity.class);
				it.putExtra(ACTIVITY_EXTRA, latticeProduct);
				startActivityForResult(it, request_edit_code);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	private void findView() {
		mViewPager = (ViewPager) findViewById(R.id.vpage);
	}

	public void checkUser(long userId) {
		if (mUserService.isMySelf(userId)) {
			refreshTopbar(true);
		} else {
			refreshTopbar(false);
		}
	}

	public void refreshTopbar(boolean bl) {
		ZhimaTopbar topbar = getTopbar();
		if (bl) {

			topbar.findViewById(R.id.layout_topbar_rightButton3).setOnClickListener(deleteTopbarClick);
			topbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(editTopbarClick);

			ImageView img3 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton3);
			ImageView img2 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton2);

			img3.setImageResource(R.drawable.rubbish);
			img2.setImageResource(R.drawable.couples_topbar_bless_edit);

			topbar.findViewById(R.id.layout_topbar_rightButton3).setVisibility(View.VISIBLE);
			topbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);

		}

		topbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(shareTopbarClick);
		ImageView img1 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton1);
		img1.setImageResource(R.drawable.topbar_share);
		topbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("格子铺展示");
	}
}

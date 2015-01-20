package com.android;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public final class WaterRefreshListView extends ListView implements
		OnScrollListener {
	// 下拉刷新状态
	private enum RefreshState {
		Initial, // 可拉伸状态
		Refreshing// 刷新状态
	}

	// 下拉刷新的接口
	public interface OnRefreshListener {
		void onRefresh();
	}

	private Context mContext;
	private OnScrollListener mOnScrollListener;// 滚动监听
	private OnRefreshListener mOnRefreshListener;// 下拉刷新监听

	private ProgressBar headerProgressBar;// 顶部加载控件
	private WaterView waterView;// 顶部水滴控件
	private View headerView;// 顶部加载控件

	private RefreshState refreshState = RefreshState.Initial;// 初始化为可拉伸状态
	private int headerViewHeight;// 顶部高度
	private int slimeViewHeight;// 水滴高度

	private float newY;// Y轴新高度
	private float oldY = 0;// Y轴原来高度

	private int LastHeight;// 顶部视图原来高度
	boolean ishasrefresh = false;// 是否已经刷新过了
	private boolean isrefreshBack = false;// 是否是下拉回弹的

	RoundImageView im_face;
	private boolean mIsTop = true;
	private boolean mIsGoingtoRefresh = false;

	public WaterRefreshListView(Context context) {
		super(context);
		mContext = context;
		this.init(context);
	}

	public WaterRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.init(context);
	}

	public WaterRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		this.init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */

	private void init(Context context) {
		headerView = LayoutInflater.from(context).inflate(R.layout.header_view,
				null);
		waterView = (WaterView) headerView.findViewById(R.id.waterView);
		im_face = (RoundImageView) headerView.findViewById(R.id.im_face);
		headerProgressBar = (ProgressBar) headerView
				.findViewById(R.id.progressBar);
		addHeaderView(headerView);

		headerView.setVisibility(View.VISIBLE);
		super.setOnScrollListener(this);
		measureView(headerView);
		measureView(im_face);
		measureView(waterView);
		headerViewHeight = headerView.getMeasuredHeight();
		slimeViewHeight = waterView.getMeasuredHeight();
		reset();
	}

	/**
	 * 绘制子试图
	 * 
	 * @param child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (this.mOnScrollListener != null
				&& refreshState != RefreshState.Refreshing) {
			this.mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
		switch (scrollState) {
		// 当不滚动时
		case OnScrollListener.SCROLL_STATE_IDLE:
			// 判断滚动到底部
			if (this.getLastVisiblePosition() == (this.getCount() - 1)) {
			}
			// 判断滚动到顶部

			if (this.getFirstVisiblePosition() == 0) {
				mIsTop = true;
			} else {
				mIsTop = false;
			}

			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 手指按下时，记录初始高度为headerViewHeight
			Log.d("SlimeRefreshListView", "ACTION_DOWN");
			oldY = event.getY();
			newY = event.getY();
			Log.d("LastHeight", LastHeight + "");
			LastHeight = headerViewHeight;
			break;
		case MotionEvent.ACTION_MOVE:// 手指移动过程中，更新头部高度和水滴形状
			Log.d("SlimeRefreshListView", "ACTION_MOVE");
			applyHeaderLayout(event);// 传递事件处理
			break;
		case MotionEvent.ACTION_UP:// 手指抬起，headerview回复到原来的初始位置
			Log.d("SlimeRefreshListView", "ACTION_UP");
			if (mIsTop && mIsGoingtoRefresh) {
				onRefresh();// 触发刷新事件
				mIsGoingtoRefresh = false;
			}
			changeHeadView();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 初始化参数
	 */
	private void reset() {// headerview回复到原来的初始高度

		// 复位后修复headerview的高度和padding
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				headerViewHeight);
		headerView.setPadding(0, 110, 0, 0);
		headerView.setLayoutParams(params);

		// 复位后修复im_face的高度和MarginBottom
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) im_face
				.getLayoutParams();
		lp.setMargins(0, 0, 0, 20);
		im_face.setLayoutParams(lp);

		ishasrefresh = true;

		// 复位后修复waterView的参数
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, slimeViewHeight);
		waterView.setLayoutParams(params2);
		waterView.setReset();
		if (isrefreshBack) {// 如果是刷新回来之后，再加一个小反弹
			final Animation rock = AnimationUtils.loadAnimation(mContext,
					R.anim.rock);
			final Animation rock1 = AnimationUtils.loadAnimation(mContext,
					R.anim.rock1);
			waterView.startAnimation(rock1);
			// 添加抖动动画
			new Handler().postDelayed(new Runnable() {
				public void run() {
					waterView.startAnimation(rock);
				}
			}, 100);
			isrefreshBack = false;
		}

	}

	/**
	 * 移动过程中 在可拉动距离之内，更新水滴的形状 ,超过拉动的距离，开始更新
	 * 
	 * @param ev
	 */
	private void applyHeaderLayout(MotionEvent ev) {
		if (refreshState == RefreshState.Initial && mIsTop) {
			int historicalY = (int) ev.getY();
			if (historicalY - oldY < 220 && historicalY < 800) {// 在可拉动距离之内，更新水滴的形状
				if (historicalY > newY + 5) {
					Log.d("SlimeRefreshListView", "开始绘制");
					waterView.setDraw();
					newY = historicalY;
					int h = LastHeight + 5;
					/* 绘制水滴效果 */
					LayoutParams params = new LayoutParams(
							LayoutParams.MATCH_PARENT, h);
					LastHeight = LastHeight + 5;
					headerView.setLayoutParams(params);

					LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, LastHeight + 5);
					waterView.setLayoutParams(params2);

					// 未在刷新中，更改状态
					isrefreshBack = false;
					ishasrefresh = false;
					mIsGoingtoRefresh = false;
				}
			} else {// 超过拉动的距离，开始更新
				if (!ishasrefresh && historicalY - oldY > 220// 触发刷新动画
						&& historicalY < 800) {// 避免一次拉到头多次触发更新事件
					// 正在刷新中，更改状态
					ishasrefresh = true;
					mIsGoingtoRefresh = true;
					isrefreshBack = true;
					refreshState = RefreshState.Refreshing;// 更改刷新状态
					headerProgressBar.setVisibility(View.VISIBLE);// 显示加载转圈

					// 音量控制,初始化定义
					AudioManager mAudioManager = (AudioManager) mContext
							.getSystemService(Context.AUDIO_SERVICE);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 4,
							0);
					// 音效
					MediaPlayer m = MediaPlayer.create(mContext, R.raw.water);
					m.setVolume(0.2f, 0.2f);
					m.start();

					// 震动
					Vibrator mVibrator01 = (Vibrator) mContext
							.getSystemService(Service.VIBRATOR_SERVICE);
					mVibrator01.vibrate(new long[] { 10, 10 }, -1);

					// 动画
					Animation rock = AnimationUtils.loadAnimation(mContext,
							R.anim.rock);
					final Animation rock1 = AnimationUtils.loadAnimation(
							mContext, R.anim.rock1);
					waterView.startAnimation(rock);
					// 在手指未离开过程中的抖动动画
					new Handler().postDelayed(new Runnable() {
						public void run() {
							waterView.startAnimation(rock1);
						}
					}, 100);

					// 拉伸到刷新状态时，固定herderview的高度和padding
					LayoutParams params = new LayoutParams(
							LayoutParams.MATCH_PARENT, 400);// 拉伸到刷新状态时，固定herderview的高度和padding
					headerView.setPadding(0, 110, 0, 0);
					headerView.setLayoutParams(params);

					// 拉伸到刷新状态时，固定im_face的高度和Margins
					LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) im_face
							.getLayoutParams();
					lp.setMargins(0, 0, 0, 150);
					im_face.setLayoutParams(lp);

					// 拉伸到刷新状态时，固定waterView的参数
					LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, slimeViewHeight);
					waterView.setReset();
					waterView.setLayoutParams(params2);

				} else {
					mIsGoingtoRefresh = false;
				}
			}
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		// setSelection(0);
	}

	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		this.mOnScrollListener = l;
	}

	public void onRefreshComplete() {
		headerProgressBar.setVisibility(View.INVISIBLE);
		refreshState = RefreshState.Initial;
	}

	private void changeHeadView() {// 更新HeadView状态
		if (refreshState == RefreshState.Initial) {
			Log.d("SlimeRefreshListView", "RefreshState.Initial");
			reset();
			// setSelection(0);
		} else if (refreshState == RefreshState.Refreshing) {
			Log.d("SlimeRefreshListView", "RefreshState.Refreshing");
			reset();
		}
	}

	private void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}
}

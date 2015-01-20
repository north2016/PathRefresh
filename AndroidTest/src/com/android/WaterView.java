package com.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("HandlerLeak")
public class WaterView extends ImageView {
	private Paint mPaint;
	private int r = 6;
	private int height = 0;
	private int x = 5;
	private boolean isDraw = true;
	private int innerColor = Color.parseColor("#ff00cc");
	private int background = Color.parseColor("#00ccff");


	public WaterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0);
		mPaint.setStyle(Style.STROKE); // 外框灰色
		mPaint.setColor(background);
	}

	public WaterView(Context context) {
		super(context);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(background);
		if (isDraw) {
			int Width = canvas.getWidth() / 2;

			Paint blackPaint = new Paint();
			blackPaint.setAntiAlias(true);
			blackPaint.setColor(innerColor); // 填充的颜色
			blackPaint.setStyle(Style.FILL);
			blackPaint.setStrokeWidth(0);

			// 圆形
			RectF rectF = new RectF(Width - 12, 0, Width + 12, 24);
			RectF rectF2 = new RectF(Width - r, height, Width + r, height + r
					* 2);

			Path path1 = new Path(); // 梯形
			path1.moveTo(Width - 12, 12);
			path1.lineTo(Width + 12, 12);
			path1.lineTo(Width + r, height + r);
			path1.lineTo(Width - r, height + r);
			path1.lineTo(Width - 12, 12);

			/*// 弧形
			Path quad = new Path();
			quad.moveTo(Width - 12, 12);
			quad.quadTo(Width - r, 100, Width - r, height + r);

			Path quad2 = new Path();
			quad2.moveTo(Width + 12, 12);
			quad2.quadTo(Width + r, 100, Width + r, height + r);*/




			// 里边颜色

			canvas.drawOval(rectF, blackPaint);
			canvas.drawOval(rectF2, blackPaint);
			canvas.drawPath(path1, blackPaint);
			
			// 白线
			Paint p = new Paint();
			p.setColor(innerColor);
			p.setStrokeWidth(2);
			canvas.drawLine(Width, r, Width, 500, p);
			
			// canvas.drawBitmap(Image, Width - 12, 12, mPaint);

			// 弧形
			if (height > 15) {

			
		/*		// 弧线
				canvas.drawPath(quad, whitePaint);
				canvas.drawPath(quad2, whitePaint);*/
				

				// 亮点
				Paint Paint = new Paint();
				Paint.setAntiAlias(true);
				Paint.setColor(Color.parseColor("#ffffff")); // 填充的颜色
				Paint.setStyle(Style.FILL);
				Paint.setStrokeWidth(0);

				RectF rectF3 = new RectF(Width + 4, 10, Width + 6 + height / 30,
						14 + height / 10);
				canvas.drawOval(rectF3, Paint);
			}
		}

	}

	public void setDraw() {
		height = height + 3;
		if (height > x * 2) {
			r = r - 1;
			if (r < 0) {
				r = 0;
			}
			x = height-2;
		}
		if (height < 120) {
			handler.sendEmptyMessage(1000);
		} else if (height == 120) {
			isDraw = false;
			handler.sendEmptyMessage(1000);
		}
	}

	public void setReset() {
		if (isDraw) {
			r = 6;
			height = 0;
			x = 5;
			isDraw = false;
			handler.sendEmptyMessage(1000);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1000:
				invalidate();// 请求重绘View树，即draw()过程，
				// 假如视图发生大小没有变化就不会调用layout()过程，并且只绘制那些“需要重绘的”视图
				isDraw = true;
				break;
			default:
				break;
			}
		}
	};
}
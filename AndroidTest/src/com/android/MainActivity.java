package com.android;

import com.android.WaterRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static Toast mToast;
	String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats",
			"Abertam", "Abondance", "Ackawi", "Acorn", "Adelost",
			"Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
			"Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert",
			"American Cheese", "Ami du Chambertin", "Anejo Enchilado",
			"Anneau du Vic-Bilh", "Anthoriro" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final WaterRefreshListView listView = (WaterRefreshListView) findViewById(R.id.listView);
		ListAdapter mListAdapter=new ListAdapter(MainActivity.this, mStrings);
		listView.setAdapter(mListAdapter);
		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 刷新
				Log.d("MainActivity", "进入刷新页面");
				showToast(MainActivity.this,"正在刷新");
				new Thread() {
					public void run() {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								listView.onRefreshComplete(); // 刷新完毕；
								showToast(MainActivity.this,"刷新结束");
							}
						});
					};
				}.start();
			}
		});

	}
	public static void showToast(Context context, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, 500);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}
}

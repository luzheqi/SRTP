package edu.cqu.strp.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cqu.srtp.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import edu.cqu.srtp.common.MainService;
import edu.cqu.srtp.common.Task;
import edu.cqu.srtp.controller.BookDetailActivity;
import edu.cqu.srtp.data.DataProvider;
import edu.cqu.srtp.domains.BookItem;

import android.R.integer;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MyListview {

	private Activity activity;
	private ScrollView root;
	public ScrollView getRoot() {
		return root;
	}

	public void setRoot(ScrollView root) {
		this.root = root;
	}

	private List<BookItem> books;
	private ImageLoader imgLoader;
	private ImageLoaderConfiguration config;
	private Integer type;

	public MyListview(Activity activity, Integer type) {
		this.activity = activity;
		this.init();
		config=ImageLoaderConfiguration.createDefault(activity);
		imgLoader=ImageLoader.getInstance();
		imgLoader.init(config);
		this.type = type;
		return;
	}

	private void rePaint(){

		Random random = new Random();

		LinearLayout left = (LinearLayout) root.findViewById(R.id.leftListview);
		LinearLayout right = (LinearLayout) root.findViewById(R.id.rightListview);

		ImageView adImage = (ImageView) root.findViewById(R.id.ads);
		adImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(activity, BookDetailActivity.class);
				intent.putExtra("book", books.get(0));
				activity.startActivity(intent);
			}
		});
		imgLoader.displayImage(DataProvider.IMAGE_BASE_URL + books.get(0).getPicUrl(), adImage);

		for (int i = 1; i < books.size(); i++) {
			LinearLayout cell = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.recommend_cell, null);
			((TextView)cell.findViewById(R.id.book_name)).setText(books.get(i).getName());

			Integer rand = random.nextInt(30);

			((TextView)cell.findViewById(R.id.book_update)).setText(rand.toString()+"话");
			imgLoader.displayImage(DataProvider.IMAGE_BASE_URL + books.get(i).getPicUrl(), ((ImageView)cell.findViewById(R.id.picture)));
			final int copyOfI = i;
			cell.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, BookDetailActivity.class);
					intent.putExtra("book", books.get(copyOfI));
					activity.startActivity(intent);
				}
			});
			if(i%2 == 0){
				left.addView(cell);
			}else{
				right.addView(cell);
			}
			LayoutParams layoutParams = (LayoutParams) cell.getLayoutParams();
			layoutParams.setMargins(0, 20, 0, 0);
		}

		return;
	};

	public void addBooks(List<BookItem> books){
		this.books = books;

		return;
	}

	public void refresh(){

		return;
	};

	private void init(){
		this.root = (ScrollView) activity.getLayoutInflater().inflate(R.layout.recommend, null);
		root.setOnTouchListener(new OnTouchListener() {

			boolean flag1 = true;
			boolean flag2 = true;


			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					int scrollY = v.getScrollY();
					int height = v.getHeight();
					int scrollViewMeasuredHeight = root.getChildAt(0).getMeasuredHeight();

					if (flag1 && flag2) {
						if (scrollY == 0) {
							Toast.makeText(activity, "begin", Toast.LENGTH_SHORT).show();
							flag1 = false;
							flag2 = false;
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									flag1 = true;
								}
							}).start();
						}

						if ((scrollY + height) == scrollViewMeasuredHeight) {
							Toast.makeText(activity, "end", Toast.LENGTH_SHORT).show();
							flag1 = false;
							flag2 = false;
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									flag1 = true;
								}
							}).start();
						}
					}else if(flag1 && scrollY != 0 && (scrollY + height) != scrollViewMeasuredHeight){
						flag2 = true;
					}

					break;

				default:
					break;
				}
				return false;
			}
		});

		//init books
		Task task = new Task() {

			@Override
			public void run() {
				if (type == 0) {
					MyListview.this.books = DataProvider.getRecommendBook(1);
				}else{
					MyListview.this.books = DataProvider.getPopularBook(1);
				}
			}

			@Override
			public void refresh() {
				if (books != null && books.size() != 0) {
					rePaint();
				}
			}
		};
		MainService.addTask(task);
		return;
	}
}

package com.cyno.reminder.adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.cyno.reminder.R;
import com.cyno.reminder.constants.GlobalConstants;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.Task;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class TaskListAdapter extends CursorRecyclerViewAdapter<TaskListAdapter.ViewHolder>{

	private final String dateformat = "dd MMM, yyyy , HH:mm";
	private OnClickListener onclickListner;
	private OnLongClickListener onLongClickListner;
	private Context context;
	private Bitmap mDummyBitmap;

	public TaskListAdapter(Context context,Cursor cursor , OnClickListener onclick , OnLongClickListener onLongClick){
		super(context,cursor);
		this.onclickListner = onclick;
		this.onLongClickListner = onLongClick;
		this.context = context;
//        mDummyBitmap = getDummyBitmap();
	}

	private Bitmap getDummyBitmap() {
		return BitmapFactory.decodeResource(context.getResources(), R.drawable.splash);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener {
		public TextView tvTitle;
		public TextView tvDate;
		public TextView tvDateScribble;
		public TextView tvDaysLeft;
		public TextView tvDaysLeftText;
		public ImageView ivImage;
		public ImageView ivScribble;
		public View taskView;
		public View scribbleView;
		public View root;


		public ViewHolder(View view , OnClickListener onclick , OnLongClickListener onLongClick) {
			super(view);
			tvTitle = (TextView) view.findViewById(R.id.tv_item_task_list_title);
			tvDate = (TextView) view.findViewById(R.id.tv_item_task_list_date);
			tvDaysLeft = (TextView) view.findViewById(R.id.tv_item_task_list_days_left_number);
			tvDaysLeftText= (TextView) view.findViewById(R.id.tv_item_task_list_daysleft);
			ivImage= (ImageView) view.findViewById(R.id.task_item_image);
			ivScribble= (ImageView) view.findViewById(R.id.image_list_item_scribble);

			taskView = view.findViewById(R.id.task_item_root);
			scribbleView = view.findViewById(R.id.scribble_item_root);
			tvDateScribble = (TextView) view.findViewById(R.id.tv_item_task_list_date_scribble);

			view.setOnClickListener(onclick);
			view.setOnLongClickListener(onLongClick);
			root = view;
		}

		@Override
		public boolean onLongClick(View v) {
			return false;
		}

		@Override
		public void onClick(View v) {

		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_list, parent, false);
		View itemView = View.inflate(this.context, R.layout.item_task_list, null);
		ViewHolder vh = new ViewHolder(itemView , onclickListner , onLongClickListner);
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
		//        MyListItem myListItem = MyListItem.fromCursor(cursor);

		int category = Integer.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY)));

		final int caseScribble = GlobalConstants.TYPE_SCRIBBLE;

		switch (category){
			case caseScribble:
				setScribbleLayout(viewHolder , cursor);
				break;
			default:
				setTaskView(viewHolder,cursor);
				break;
		}

		viewHolder.root.setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID)));

	}

	private void setScribbleLayout(ViewHolder viewHolder, Cursor cursor) {
		viewHolder.scribbleView.setVisibility(View.VISIBLE);
		viewHolder.taskView.setVisibility(View.GONE);
		viewHolder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_NAME)));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_DATE))));
		SimpleDateFormat format  = new SimpleDateFormat(dateformat, Locale.getDefault());
		viewHolder.tvDateScribble.setText(format.format(cal.getTime()));

		byte[] data = cursor.getBlob(cursor.getColumnIndex(TasksTable.COL_SCRIBBLE));
		Bitmap bmp = null;
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inMutable = false;
//		bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//		viewHolder.ivScribble.setImageBitmap(bmp);

		loadBitmap(data, viewHolder.ivScribble);

	}


	private void setTaskView(ViewHolder viewHolder , Cursor cursor){
		viewHolder.scribbleView.setVisibility(View.GONE);
		viewHolder.taskView.setVisibility(View.VISIBLE);

		long date = Long.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_DATE)));
		long diff = date - System.currentTimeMillis();

		viewHolder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_NAME)));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_DATE))));
		SimpleDateFormat format  = new SimpleDateFormat(dateformat, Locale.getDefault());
		viewHolder.tvDate.setText(format.format(cal.getTime()));
		int status = Integer.valueOf(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_STATUS)));
		if(status == Task.STATUS_DONE){
			viewHolder.tvDaysLeft.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.tvDaysLeftText.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.tvDaysLeft.setVisibility(View.GONE);
			viewHolder.tvDaysLeftText.setText(R.string.done);
			viewHolder.ivImage.setImageResource(R.drawable.label_light_green);
		}else if(diff < 0){
			viewHolder.tvDaysLeft.setTextColor(context.getResources().getColor(R.color.warn));
			viewHolder.tvDaysLeftText.setTextColor(context.getResources().getColor(R.color.warn));
			viewHolder.tvDaysLeft.setVisibility(View.GONE);
			viewHolder.tvDaysLeftText.setText(R.string.overdue);
			viewHolder.ivImage.setImageResource(R.drawable.label);
		}else{
			viewHolder.tvDaysLeft.setTextColor(context.getResources().getColor(R.color.primary_dark));
			viewHolder.tvDaysLeftText.setTextColor(context.getResources().getColor(R.color.primary_dark));
			viewHolder.tvDaysLeft.setVisibility(View.VISIBLE);
			viewHolder.tvDaysLeft.setText(String.valueOf(getDaysLeft(diff , viewHolder.tvDaysLeftText)));
			viewHolder.ivImage.setImageResource(R.drawable.label_primary);
		}
		((View)viewHolder.tvDaysLeft.getParent()).setTag(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TASK_ID)));

	}

	private long getDaysLeft(long diff , TextView tv) {
		long diffr = diff/(1000*3600*24);
		if(diffr != 0){
			tv.setText(R.string.Days_left);
			return diffr;
		}else{
			tv.setText(R.string.hours_left);
			return diff/(1000*3600);
		}
	}

	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private ImageView imageViewReference;
		private byte[] data;

		public BitmapWorkerTask(ImageView imageView, byte[] data) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
//            imageViewReference = new WeakReference<ImageView>(imageView);
			imageViewReference = imageView;
			this.data = data;
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			return decodeSampledBitmap(data);
		}

		private Bitmap decodeSampledBitmap(byte[] data) {
			Bitmap bmp = null;
			BitmapFactory.Options options = new BitmapFactory.Options();

			if(Build.VERSION.SDK_INT >= 11)
				options.inMutable = false;
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
			return bmp;
		}

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            imageViewReference.setVisibility(View.GONE);
//
//        }

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
//                final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask =
						getBitmapWorkerTask(imageViewReference);
				if (this == bitmapWorkerTask && imageViewReference != null) {
//                    imageViewReference.setVisibility(View.VISIBLE);
					imageViewReference.setImageBitmap(bitmap);
				}
			}
		}
	}

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
							 BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference =
					new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	public void loadBitmap(byte[] data, ImageView imageView) {
		if (cancelPotentialWork(data, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView , data);
			final AsyncDrawable asyncDrawable =
					new AsyncDrawable(context.getResources(), mDummyBitmap , task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute();
		}
	}

	public static boolean cancelPotentialWork(byte[] data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final byte[] bitmapData = bitmapWorkerTask.data;
			// If bitmapData is not yet set or it differs from the new data
			if (bitmapData.length == 0 || bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}


}
package ch.cern.alice.alimonalisa.adapters;

import java.util.ArrayList;
import java.util.List;

import ch.cern.alice.alimonalisa.CommonConstants;
import ch.cern.alice.alimonalisa.NotificationDetailsActivity;
import ch.cern.alice.alimonalisa.R;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;
import ch.cern.alice.alimonalisa.services.NotificationService;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.joda.time.format.*;

public class NotificationAdapter extends BaseAdapter implements
		OnItemClickListener {

	private List<Notification> notifications;
	private Context context;
	private LayoutInflater inflater;

	public NotificationAdapter(Context context) {
		notifications = new ArrayList<Notification>();
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public NotificationAdapter(List<Notification> notifications, Context context) {
		this.notifications = notifications;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return notifications.size();
	}

	@Override
	public Object getItem(int arg0) {
		return notifications.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return notifications.get(arg0).getId();
	}

	class NotificationHolder {
		public LinearLayout notificationLayout;
		// image for the type of notification
		public ImageView notificationCategory;
		// the title for the problem
		public TextView title;
		// the date of the problem
		public TextView date;
		// status of the problem, solved or not solved
		public TextView status;
		// the arrow for expanding
		public ImageView arrow;
		// line for separating
		public View line;

		public ImageView isImportant;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Notification n = notifications.get(position);
		NotificationHolder holder = null;

		if (convertView == null) {
			holder = new NotificationHolder();

			holder.notificationLayout = (LinearLayout) inflater.inflate(
					R.layout.notification_item, null);

			holder.notificationCategory = (ImageView) holder.notificationLayout
					.findViewById(R.id.problem_type);

			holder.title = (TextView) holder.notificationLayout
					.findViewById(R.id.title);

			holder.date = (TextView) holder.notificationLayout
					.findViewById(R.id.date);

			holder.status = (TextView) holder.notificationLayout
					.findViewById(R.id.status);

			holder.arrow = (ImageView) holder.notificationLayout
					.findViewById(R.id.expander);

			holder.line = (View) holder.notificationLayout
					.findViewById(R.id.line);

			holder.isImportant = (ImageView) holder.notificationLayout
					.findViewById(R.id.importantFlag);

			convertView = holder.notificationLayout;
			convertView.setTag(holder);
		}

		holder = (NotificationHolder) convertView.getTag();

		if (n.getCategory() == Category.STORAGE) {
			holder.notificationCategory.setImageResource(R.drawable.storage);
		} else if (n.getCategory() == Category.SERVICE) {
			holder.notificationCategory.setImageResource(R.drawable.services);
		} else if (n.getCategory() == Category.NETWORK) {
			holder.notificationCategory.setImageResource(R.drawable.network);
		} else if (n.getCategory() == Category.PROXY) {
			holder.notificationCategory.setImageResource(R.drawable.proxy);
		} else if (n.getCategory() == Category.INFO) {
			holder.notificationCategory.setImageResource(R.drawable.info);
		}

		holder.title.setText(n.getTitle());
		holder.date.setText(DateTimeFormat.shortDateTime().print(
				n.getStartTime()));

		String isSolved;
		if (n.getStatus() == false) {
			isSolved = "Not Solved";
		} else {
			isSolved = "Solved";
		}
		holder.status.setText("Status: " + isSolved);

		return convertView;
	}

	public void add(Notification notification) {
		notifications.add(notification);
		notifyDataSetChanged();
	}

	public void addAll(List<Notification> n) {
		notifications.addAll(n);
		notifyDataSetChanged();
	}

	public void clear() {
		notifications.clear();
		notifyDataSetChanged();
	}

	public void refresh() {
		notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(context, NotificationService.class);
		intent.setAction(CommonConstants.ONE_NOTIFICATION_ACTION);
		intent.putExtra(CommonConstants.NOTIFICATION_ID, notifications.get(position).getId());
		context.startService(intent);
	}
}

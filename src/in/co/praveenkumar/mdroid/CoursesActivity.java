package in.co.praveenkumar.mdroid;

import in.co.praveenkumar.R;
import in.co.praveenkumar.mdroid.helpers.BaseActivity;
import in.co.praveenkumar.mdroid.helpers.FolderDetails;
import in.co.praveenkumar.mdroid.parser.CoursesParser;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class CoursesActivity extends BaseActivity {
	private ArrayList<String> cNms;
	private ArrayList<String> cIds;
	private int cCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.courses);

		// Get html data and extract courses
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		String html = extras.getString("html");
		CoursesParser cParser = new CoursesParser(html);
		cNms = cParser.getCourseNames();
		cIds = cParser.getCourseIds();
		cCount = cParser.getCourseCount();
		String uName = cParser.getUserName();

		// Say hello to user
		TextView uNameTV = (TextView) findViewById(R.id.user_name);
		uNameTV.setText("Hello " + uName.toLowerCase(Locale.getDefault()) + "!");

		// Create folder for each course. Also replace course names with values
		// obtained back This is required because a few not allowed characters
		// are present in default names from Moodle
		FolderDetails FD = new FolderDetails(cNms);
		cNms = FD.createCourseFolders();

		listCoursesInListView();

	}

	private void listCoursesInListView() {
		// Set title
		setTitle("Courses (" + cCount + ")");

		ListView listView = (ListView) findViewById(R.id.courses_list);
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, cNms,
				cIds);

		// Assign adapter to ListView
		listView.setAdapter(adapter);
	}

	private class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final ArrayList<String> courseName;
		private final ArrayList<String> courseID;

		public MySimpleArrayAdapter(Context context,
				ArrayList<String> courseName, ArrayList<String> courseID) {
			super(context, R.layout.course_listview_layout, courseName);
			this.context = context;
			this.courseName = courseName;
			this.courseID = courseID;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.course_listview_layout,
					parent, false);
			// Because position can't be used for it is not final
			final int pos = position;

			// Set course name
			final TextView cNmeView = (TextView) rowView
					.findViewById(R.id.course_name);
			cNmeView.setText(courseName.get(position));

			// Set onClickListeners on buttons
			final Button forumsBtn = (Button) rowView
					.findViewById(R.id.forums_btn);
			final Button filesBtn = (Button) rowView
					.findViewById(R.id.files_btn);

			forumsBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					openActivity(0, courseID.get(pos), courseName.get(pos));
				}
			});
			filesBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					openActivity(1, courseID.get(pos), courseName.get(pos));
				}
			});

			return rowView;
		}
	}

	private void openActivity(int selAct, String cId, String cName) {
		// 0: forums, else: files
		if (selAct == 0) {
			Intent i = new Intent(this, ForumActivity.class);
			i.putExtra("cId", cId);
			i.putExtra("cName", cName);
			startActivityForResult(i, 3);
		} else {
			Intent i = new Intent(this, FilesActivity.class);
			i.putExtra("cId", cId);
			i.putExtra("cName", cName);
			startActivityForResult(i, 3);
		}

	}
}
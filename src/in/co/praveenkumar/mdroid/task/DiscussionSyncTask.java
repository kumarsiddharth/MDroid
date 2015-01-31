package in.co.praveenkumar.mdroid.task;

import in.co.praveenkumar.mdroid.model.MoodleDiscussion;
import in.co.praveenkumar.mdroid.moodlerest.MoodleRestDiscussion;

import java.util.ArrayList;
import java.util.List;

public class DiscussionSyncTask {
	String mUrl;
	String token;
	long siteid;
	String error;

	/**
	 * 
	 * @param mUrl
	 * @param token
	 * @param siteid
	 * 
	 * @author Praveen Kumar Pendyala (praveen@praveenkumar.co.in)
	 */
	public DiscussionSyncTask(String mUrl, String token, long siteid) {
		this.mUrl = mUrl;
		this.token = token;
		this.siteid = siteid;
	}

	/**
	 * Sync all the discussion topics of a forum.
	 * 
	 * @return syncStatus
	 * 
	 * @author Praveen Kumar Pendyala (praveen@praveenkumar.co.in)
	 */
	public Boolean syncDiscussions(int forumid) {
		ArrayList<String> forumids = new ArrayList<String>();
		forumids.add(forumid + "");
		return syncDiscussions(forumids);
	}

	/**
	 * Sync all the discussion topics in the list of forums.
	 * 
	 * @return syncStatus
	 * 
	 * @author Praveen Kumar Pendyala (praveen@praveenkumar.co.in)
	 */
	public Boolean syncDiscussions(ArrayList<String> forumids) {
		MoodleRestDiscussion mrd = new MoodleRestDiscussion(mUrl, token);
		ArrayList<MoodleDiscussion> mTopics = mrd.getDiscussions(forumids);

		/** Error checking **/
		// Some network or encoding issue.
		if (mTopics == null) {
			error = "Network issue!";
			return false;
		}

		// Moodle exception
		if (mTopics.size() == 0) {
			error = "No data received";
			// No additional debug info as that needs context
			return false;
		}

		List<MoodleDiscussion> dbTopics;
		MoodleDiscussion topic = new MoodleDiscussion();
		for (int i = 0; i < mTopics.size(); i++) {
			topic = mTopics.get(i);
			topic.setSiteid(siteid);

			dbTopics = MoodleDiscussion.find(MoodleDiscussion.class,
					"discussionid = ? and siteid = ?", topic.getDiscussionid()
							+ "", siteid + "");
			if (dbTopics.size() > 0)
				topic.setId(dbTopics.get(0).getId());
			// set notifications if enabled
			 else{
				 
				// new MDroidNotification(siteid,
				// MDroidNotification.TYPE_COURSE_CONTENT,
				// course.getShortname() + " has new contents",
				// module.getName() + " added to " + course.getFullname())
			 }

			topic.save();
		}

		return true;
	}
}

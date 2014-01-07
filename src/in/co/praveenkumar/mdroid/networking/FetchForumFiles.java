package in.co.praveenkumar.mdroid.networking;

import in.co.praveenkumar.mdroid.FilesActivity.UIupdater;
import in.co.praveenkumar.mdroid.parser.FilesInForumsParser;

import java.util.ArrayList;

public class FetchForumFiles {
	private ArrayList<String> threadIds = new ArrayList<String>();
	private ArrayList<String> fFileIDs = new ArrayList<String>();
	private ArrayList<String> fFileNames = new ArrayList<String>();
	private int nFiles = 0;
	private UIupdater UU;

	public FetchForumFiles(UIupdater UU) {
		this.UU = UU;
	}

	public void fetchFiles(String cId) {
		// Get forum threadIds. Use FetchForum class can do this.
		// We will each thread and looking for files in them.
		FetchForum FF = new FetchForum();
		FF.fetchForum(cId);
		threadIds = FF.getThreadIds();

		// Get html content of each Thread. FetchForumThread class can do this.
		for (int i = 0; i < threadIds.size(); i++) {
			// Display progress on UI.
			// This is a bg thread task. This msg will be pushed
			// to UI thread by UIupdater class
			UU.setForumProgress(i + 1, threadIds.size(), fFileIDs, fFileNames,
					nFiles);

			FetchForumThread FFT = new FetchForumThread();
			FFT.fetchThread(threadIds.get(i));
			String tHtml = FFT.getThreadContent();

			// Now get files and ids from each thread
			FilesInForumsParser FFP = new FilesInForumsParser(tHtml);
			fFileIDs.addAll(FFP.getFileIds());
			fFileNames.addAll(FFP.getFileNames());
			nFiles += FFP.getFileCount();
		}

	}

	public ArrayList<String> getFileIds() {
		return fFileIDs;
	}

	public ArrayList<String> getFileNames() {
		return fFileNames;
	}

	public int getFilesCount() {
		return nFiles;
	}

}
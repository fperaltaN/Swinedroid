package com.legind.swinedroid;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import com.legind.swinedroid.xml.OverviewXMLHandler;

import com.legind.Dialogs.ErrorMessageHandler.ErrorMessageHandler;

public class ServerView extends Activity implements Runnable {
	private ServerDbAdapter mDbHelper;
	private TextView mSometextText;
	private Long mRowId;
	private OverviewXMLHandler mOverviewXMLHandler;
	private int mPortInt;
	private String mHostText;
	private String mUsernameText;
	private String mPasswordText;
	private ErrorMessageHandler mEMH;
	private ProgressDialog pd;
	private final String LOG_TAG = "com.legind.swinedroid.ServerView";
	private final int DOCUMENT_RETRIEVED = 0;
	private final int IO_ERROR = 1;
	private final int XML_ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOverviewXMLHandler = new OverviewXMLHandler();
		mDbHelper = new ServerDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.server_view);

		pd = ProgressDialog.show(this, "", "Loading. Please wait...", true);

		mEMH = new ErrorMessageHandler(Swinedroid.LA,
				findViewById(R.id.server_edit_error_layout_root));

		mSometextText = (TextView) findViewById(R.id.sometext);

		mRowId = savedInstanceState != null ? savedInstanceState
				.getLong(ServerDbAdapter.KEY_ROWID) : null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(ServerDbAdapter.KEY_ROWID)
					: null;
		}

		if (mRowId != null) {
			Cursor server = mDbHelper.fetchServer(mRowId);
			startManagingCursor(server);
			mHostText = server.getString(server
					.getColumnIndexOrThrow(ServerDbAdapter.KEY_HOST));
			mPortInt = server.getInt(server
					.getColumnIndexOrThrow(ServerDbAdapter.KEY_PORT));
			mUsernameText = server.getString(server
					.getColumnIndexOrThrow(ServerDbAdapter.KEY_USERNAME));
			mPasswordText = server.getString(server
					.getColumnIndexOrThrow(ServerDbAdapter.KEY_PASSWORD));
		}

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ServerDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void run() {
		try {
			mOverviewXMLHandler.createElement(this, mHostText, mPortInt, mUsernameText, mPasswordText, "overview");
		} catch (IOException e) {
			Log.e(LOG_TAG, e.toString());
			handler.sendEmptyMessage(IO_ERROR);
		} catch (SAXException e) {
			Log.e(LOG_TAG, e.toString());
			handler.sendEmptyMessage(XML_ERROR);
		}
		handler.sendEmptyMessage(DOCUMENT_RETRIEVED);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			pd.dismiss();
			switch(message.what){
				case IO_ERROR:
					mEMH.DisplayErrorMessage("Could not connect to server.  Please ensure that your settings are correct and try again later.");
				break;
				case XML_ERROR:
					mEMH.DisplayErrorMessage("Server responded with an invalid XML document.  Please try again later.");
				break;
				case DOCUMENT_RETRIEVED:
					mSometextText.setText(mOverviewXMLHandler.currentElement.something);
				break;
			}
			if(message.what != DOCUMENT_RETRIEVED){
				mDbHelper.close();
				finish();
			}

		}
	};
}

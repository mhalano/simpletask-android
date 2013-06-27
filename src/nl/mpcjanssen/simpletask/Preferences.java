/**
 * This file is part of Todo.txt Touch, an Android app for managing your todo.txt file (http://todotxt.com).
 *
 * Copyright (c) 2009-2012 Todo.txt contributors (http://todotxt.com)
 *
 * LICENSE:
 *
 * Todo.txt Touch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Todo.txt Touch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with Todo.txt Touch.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Todo.txt contributors <todotxt@yahoogroups.com>
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2009-2012 Todo.txt contributors (http://todotxt.com)
 */
package nl.mpcjanssen.simpletask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import nl.mpcjanssen.simpletask.util.Util;
import nl.mpcjanssen.todotxtholo.R;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	final static String TAG = Preferences.class.getSimpleName();
	public static final int RESULT_LOGOUT = RESULT_FIRST_USER + 1 ;
	public static final int RESULT_ARCHIVE = RESULT_FIRST_USER + 2 ;

	private void broadcastIntentAndClose(String intent, int result) {
		
		Intent broadcastIntent = new Intent(intent);
		sendBroadcast(broadcastIntent);
		
		// Close preferences screen
		setResult(result);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}
	
	public static class TodoTxtPrefFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.todotxt_preferences);
		}
	}	
	public static class ArchivePrefFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.archive_preferences);
		}
		
		@Override
		public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {
			if(preference.getKey().equals("archive_now")) {
				Log.v("PREFERENCES", "Archiving completed items from preferences");
				((Preferences)this.getActivity()).broadcastIntentAndClose(
						Constants.INTENT_ACTION_ARCHIVE,
						Preferences.RESULT_ARCHIVE);			
			}
			return true;
		}
	}
	public static class DropboxPrefFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.dropbox_preferences);
		}
		
		@Override
		public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {
			if(preference.getKey().equals("logout_dropbox")) {
				Log.v("PREFERENCES", "Logging out from Dropbox");				
				((Preferences)this.getActivity()).broadcastIntentAndClose(
						Constants.INTENT_ACTION_LOGOUT,
						Preferences.RESULT_LOGOUT);			
			}
			return true;
		}
	}
	public static class AboutPrefFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.about_preferences);
			PreferenceActivity act = (PreferenceActivity) getActivity();
			PackageInfo packageInfo;
			String git_version;
			final Preference versionPref = findPreference("app_version");
			try {
				packageInfo = act.getPackageManager().getPackageInfo(act.getPackageName(),
						0);
				versionPref.setSummary("v" + packageInfo.versionName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			versionPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					ClipboardManager clipboard = (ClipboardManager)
					        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
					Util.showToastShort(getActivity(), R.string.version_copied);
					ClipData clip = ClipData.newPlainText("version info", versionPref.getSummary());
					clipboard.setPrimaryClip(clip);
					return true;
				}
			}) ;
		}
	}
}
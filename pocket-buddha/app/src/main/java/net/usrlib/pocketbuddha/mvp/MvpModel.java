package net.usrlib.pocketbuddha.mvp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import net.usrlib.pocketbuddha.provider.WordContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rgr-myrg on 6/15/16.
 */
public class MvpModel implements Parcelable {
	public static final String NAME = MvpModel.class.getSimpleName();
	public static final int DB_QUERY_LOADER_ID  = 1;
	public static final int DB_SEARCH_LOADER_ID = 2;

	public static final String POSITION_KEY = "adapterPosition";

	public static final String TITLE_COLUMN = "title";
	public static final String PALI_COLUMN  = "pali";
	public static final String ENGLISH_COLUMN   = "english";
	public static final String MP3_LINK_COLUMN  = "mp3Link";
	public static final String IMAGE_URL_COLUMN = "imageUrl";
	public static final String AUTHOR_COLUMN    = "author";
	public static final String SUBJECT_COLUMN   = "subject";
	public static final String FAVORITE_COLUMN  = "favorite";
	public static final String TIMESTAMP_COLUMN = "timestamp";

	private String title;
	private String pali;
	private String english;
	private String mp3Link;
	private String imageUrl;
	private String author;
	private String subject;
	private boolean favorite;

	public MvpModel() {}

	public MvpModel(
			final String title,
			final String pali,
			final String english,
			final String mp3Link,
			final String imageUrl,
			final String author,
			final String subject,
			final boolean favorite) {
		this.title = title;
		this.pali  = pali;
		this.english  = english;
		this.mp3Link  = mp3Link;
		this.imageUrl = imageUrl;
		this.author   = author;
		this.subject  = subject;
		this.favorite = favorite;
	}

	public MvpModel(Parcel parcel) {
		String[] data = new String[8];

		parcel.readStringArray(data);

		this.title = data[0];
		this.pali  = data[1];
		this.english  = data[2];
		this.mp3Link  = data[3];
		this.imageUrl = data[4];
		this.author   = data[5];
		this.subject  = data[6];
		this.favorite = Boolean.valueOf(data[7]);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[]{
				this.title,
				this.pali,
				this.english,
				this.mp3Link,
				this.imageUrl,
				this.author,
				this.subject,
				String.valueOf(this.favorite)
		});
	}

	public static final Creator<MvpModel> CREATOR = new Creator<MvpModel>() {
		@Override
		public MvpModel createFromParcel(Parcel parcel) {
			return new MvpModel(parcel);
		}

		@Override
		public MvpModel[] newArray(int size) {
			return new MvpModel[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();

		values.put(TITLE_COLUMN, title);
		values.put(PALI_COLUMN, pali);
		values.put(ENGLISH_COLUMN, english);
		values.put(MP3_LINK_COLUMN, mp3Link);
		values.put(IMAGE_URL_COLUMN, imageUrl);
		values.put(AUTHOR_COLUMN, author);
		values.put(SUBJECT_COLUMN, subject);
		values.put(FAVORITE_COLUMN, favorite);

		return values;
	}

	public static ContentValues[] fromJsonStringAsContentValues(final String jsonString)
			throws JSONException {
		final JSONArray jsonArray  = new JSONArray(jsonString);
		final ContentValues[] list = new ContentValues[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			list[i] = fromJsonObject(jsonArray.getJSONObject(i)).toContentValues();
		}

		return list;
	}

	public static MvpModel fromJsonObject(final JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		MvpModel item = null;

		try {
			item = new MvpModel(
					jsonObject.getString(TITLE_COLUMN),
					jsonObject.getString(PALI_COLUMN),
					jsonObject.getString(ENGLISH_COLUMN),
					jsonObject.getString(MP3_LINK_COLUMN),
					jsonObject.getString(IMAGE_URL_COLUMN),
					jsonObject.getString(AUTHOR_COLUMN),
					jsonObject.getString(SUBJECT_COLUMN),
					jsonObject.getBoolean(FAVORITE_COLUMN)
			);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return item;
	}

	public static MvpModel fromDbCursor(final Cursor cursor) {
		if (cursor == null) {
			return null;
		}

		final int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN));

		return new MvpModel(
				cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)),
				cursor.getString(cursor.getColumnIndex(PALI_COLUMN)),
				cursor.getString(cursor.getColumnIndex(ENGLISH_COLUMN)),
				cursor.getString(cursor.getColumnIndex(MP3_LINK_COLUMN)),
				cursor.getString(cursor.getColumnIndex(IMAGE_URL_COLUMN)),
				cursor.getString(cursor.getColumnIndex(AUTHOR_COLUMN)),
				cursor.getString(cursor.getColumnIndex(SUBJECT_COLUMN)),
				cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN)) == 1
		);
	}

	public String getTitle() {
		return title;
	}

	public String getPali() {
		return pali;
	}

	public String getEnglish() {
		return english;
	}

	public String getMp3Link() {
		return mp3Link;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getAuthor() {
		return author;
	}

	public String getSubject() {
		return subject;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public static class LoaderHelper implements LoaderManager.LoaderCallbacks<Cursor> {
		private Context mContext = null;
		private Uri mQueryUri = null;
		private MvpView mMvpView = null;

		public LoaderHelper(Context context, Uri queryUri, MvpView mvpView) {
			this.mContext  = context;
			this.mQueryUri = queryUri;
			this.mMvpView  = mvpView;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(
					mContext,
					mQueryUri,
					null, null, null, null
			);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mMvpView.onTransactionCursorReady(data);

			mContext  = null;
			mQueryUri = null;
			mMvpView  = null;
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	}

	public static class Dictionary {
		public static final String PALI_NAME_COLUMN  = "name";
		public static final String PALI_TERM_COLUMN  = "term";
		public static final String DEFINITION_COLUMN = "definition";
		public static final String FAVORITE_COLUMN   = "favorite";
		public static final String TIMESTAMP_COLUMN  = "timestamp";

		private String name;
		private String term;
		private String definition;
		private boolean favorite;

		public Dictionary(String name, String term, String definition, boolean favorite) {
			this.name = name;
			this.term = term;
			this.definition = definition;
			this.favorite = favorite;
		}

		public String getName() {
			return name;
		}

		public String getTerm() {
			return term;
		}

		public String getDefinition() {
			return definition;
		}

		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();

			values.put(PALI_NAME_COLUMN, name);
			values.put(PALI_TERM_COLUMN, term);
			values.put(DEFINITION_COLUMN, definition);
			values.put(FAVORITE_COLUMN, favorite);

			return values;
		}

		public static ContentValues[] fromJsonStringAsContentValues(final String jsonString)
				throws JSONException {
			final JSONArray jsonArray  = new JSONArray(jsonString);
			final ContentValues[] list = new ContentValues[jsonArray.length()];

			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					list[i] = fromJsonObject(jsonArray.getJSONObject(i)).toContentValues();
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
			}

			return list;
		}

		public static Dictionary fromJsonObject(final JSONObject jsonObject) throws JSONException {
			if (jsonObject == null) {
				return null;
			}

			Dictionary item = null;

			item = new Dictionary(
					jsonObject.getString(PALI_NAME_COLUMN),
					jsonObject.getString(PALI_TERM_COLUMN),
					jsonObject.getString(DEFINITION_COLUMN),
					false
			);

			return item;
		}

		public static Dictionary fromDbCursor(final Cursor cursor) {
			if (cursor == null) {
				return null;
			}

			final int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN));

			return new Dictionary(
					cursor.getString(cursor.getColumnIndex(PALI_NAME_COLUMN)),
					cursor.getString(cursor.getColumnIndex(PALI_TERM_COLUMN)),
					cursor.getString(cursor.getColumnIndex(DEFINITION_COLUMN)),
					cursor.getInt(cursor.getColumnIndex(FAVORITE_COLUMN)) == 1
			);
		}
	}
}
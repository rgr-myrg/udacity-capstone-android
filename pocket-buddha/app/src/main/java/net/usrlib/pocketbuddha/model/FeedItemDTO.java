package net.usrlib.pocketbuddha.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by home on 6/6/16.
 */
public class FeedItemDTO implements Parcelable {
	public static final String NAME = FeedItemDTO.class.getSimpleName();

	public static final String TITLE_KEY     = "title";
	public static final String PALI_KEY      = "pali";
	public static final String ENGLISH_KEY   = "english";
	public static final String MP3_LINK_KEY  = "mp3Link";
	public static final String IMAGE_URL_KEY = "imageUrl";
	public static final String AUTHOR_KEY    = "author";
	public static final String SUBJECT_KEY   = "subject";
	public static final String FAVORITE_KEY  = "favorite";

	private String title;
	private String pali;
	private String english;
	private String mp3Link;
	private String imageUrl;
	private String author;
	private String subject;
	private boolean favorite;

	public FeedItemDTO() {}

	public FeedItemDTO(
			final String title,
			final String pali,
			final String english,
			final String mp3Link,
			final String imageUrl,
			final String author,
			final String subject,
			final boolean favorite) {
		this.title    = title;
		this.pali     = pali;
		this.english  = english;
		this.mp3Link  = mp3Link;
		this.imageUrl = imageUrl;
		this.author   = author;
		this.subject  = subject;
		this.favorite = favorite;
	}

	public FeedItemDTO(Parcel parcel) {
		String[] data = new String[8];

		parcel.readStringArray(data);

		this.title    = data[0];
		this.pali     = data[1];
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

	public static final Creator<FeedItemDTO> CREATOR = new Creator<FeedItemDTO>() {
		@Override
		public FeedItemDTO createFromParcel(Parcel parcel) {
			return new FeedItemDTO(parcel);
		}

		@Override
		public FeedItemDTO[] newArray(int size) {
			return new FeedItemDTO[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();

		values.put(TITLE_KEY, title);
		values.put(PALI_KEY, pali);
		values.put(ENGLISH_KEY, english);
		values.put(MP3_LINK_KEY, mp3Link);
		values.put(IMAGE_URL_KEY, imageUrl);
		values.put(AUTHOR_KEY, author);
		values.put(SUBJECT_KEY, subject);
		values.put(FAVORITE_KEY, favorite);

		return values;
	}

//	public static List<FeedItemDTO> fromJsonStringAsList(final String jsonString) throws JSONException {
//		final List<FeedItemDTO> list = new ArrayList<>();
//		final JSONArray jsonArray = new JSONArray(jsonString);
//
//		for (int i = 0; i < jsonArray.length(); i++) {
//			list.add(
//					fromJsonObject(jsonArray.getJSONObject(i))
//			);
//		}
//
//		return list;
//	}

	public static ContentValues[] fromJsonStringAsContentValues(final String jsonString)
			throws JSONException {
		final JSONArray jsonArray  = new JSONArray(jsonString);
		final ContentValues[] list = new ContentValues[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			list[i] = fromJsonObject(jsonArray.getJSONObject(i)).toContentValues();
		}

		return list;
	}

	public static FeedItemDTO fromJsonObject(final JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		FeedItemDTO item = null;

		try {
			item = new FeedItemDTO(
					jsonObject.getString(TITLE_KEY),
					jsonObject.getString(PALI_KEY),
					jsonObject.getString(ENGLISH_KEY),
					jsonObject.getString(MP3_LINK_KEY),
					jsonObject.getString(IMAGE_URL_KEY),
					jsonObject.getString(AUTHOR_KEY),
					jsonObject.getString(SUBJECT_KEY),
					jsonObject.getBoolean(FAVORITE_KEY)
			);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return item;
	}

	public static FeedItemDTO fromDbCursor(final Cursor cursor) {
		if (cursor == null) {
			return null;
		}

		final int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE_KEY));

		return new FeedItemDTO(
				cursor.getString(cursor.getColumnIndex(TITLE_KEY)),
				cursor.getString(cursor.getColumnIndex(PALI_KEY)),
				cursor.getString(cursor.getColumnIndex(ENGLISH_KEY)),
				cursor.getString(cursor.getColumnIndex(MP3_LINK_KEY)),
				cursor.getString(cursor.getColumnIndex(IMAGE_URL_KEY)),
				cursor.getString(cursor.getColumnIndex(AUTHOR_KEY)),
				cursor.getString(cursor.getColumnIndex(SUBJECT_KEY)),
				cursor.getInt(cursor.getColumnIndex(FAVORITE_KEY)) == 1
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
}

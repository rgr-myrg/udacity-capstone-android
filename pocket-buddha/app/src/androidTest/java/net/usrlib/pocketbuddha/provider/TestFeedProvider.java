package net.usrlib.pocketbuddha.provider;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by rgr-myrg on 6/10/16.
 */
public class TestFeedProvider extends AndroidTestCase {
	public void testProviderRegistry() {
		PackageManager manager = mContext.getPackageManager();
		final ComponentName componentName = new ComponentName(
				mContext.getPackageName(),
				FeedProvider.class.getName()
		);
		try {
			// Throws an exception if the provider isn't registered.
			ProviderInfo providerInfo = manager.getProviderInfo(componentName, 0);

			// Make sure that the registered authority matches the authority from the Contract.
			assertEquals("Provider registered with authority: "
					+ providerInfo.authority
					+ " instead of authority: " + FeedContract.CONTENT_AUTHORITY,
					providerInfo.authority,
					FeedContract.CONTENT_AUTHORITY
			);
		} catch (PackageManager.NameNotFoundException e) {
			assertTrue(
					"Provider not registered at" + mContext.getPackageName(),
					false
			);
		}
	}

	public void testGetType() {
		// CONTENT_URI = content://net.usrlib.pocketbuddha.provider/items
		// contentType = vnd.android.cursor.dir/net.usrlib.pocketbuddha.provider/items

		String contentType = mContext.getContentResolver().getType(FeedContract.ItemsEntry.CONTENT_URI);

		Log.d("TEST", "CONTENT_URI" + FeedContract.ItemsEntry.CONTENT_URI);
		Log.d("TEST", "contentType" + contentType);

		assertEquals(
				"getContentResolver().getType should return the type for this Uri",
				FeedContract.ItemsEntry.CONTENT_TYPE,
				contentType
		);
	}
}

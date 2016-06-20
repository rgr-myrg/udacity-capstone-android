package net.usrlib.pocketbuddha.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

/**
 * Created by rgr-myrg on 6/5/16.
 */
public class BitmapUtil {
	public static final int DEFAULT_COLOR_TONE = 0xFF333333;
	public static final int MAX_COLOR_COUNT = 12;
	public static final int COLOR_ALPHA = 255;
	public static final double COLOR_SHADE_RATIO = 0.9;

	public static int getColorFromBitmap(final Bitmap bitmap) {
		if (bitmap == null) {
			return -1;
		}

		final Palette palette = (new Palette.Builder(bitmap))
				.maximumColorCount(MAX_COLOR_COUNT)
				.generate();

		if (palette == null) {
			return -1;
		}

		int color = DEFAULT_COLOR_TONE;

		final int mutedColor = palette.getVibrantColor(DEFAULT_COLOR_TONE);

		color = Color.argb(
				COLOR_ALPHA,
				(int) (Color.red(mutedColor) * COLOR_SHADE_RATIO),
				(int) (Color.green(mutedColor) * COLOR_SHADE_RATIO),
				(int) (Color.blue(mutedColor) * COLOR_SHADE_RATIO)
		);

		return color;
	}
}

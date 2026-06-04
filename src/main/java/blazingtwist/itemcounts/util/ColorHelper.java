package blazingtwist.itemcounts.util;

import net.minecraft.util.Mth;

public class ColorHelper {

	public static int lerpColor(float delta, int fromColor, int toColor) {
		int red = Mth.lerpInt(delta, getRed(fromColor), getRed(toColor));
		int green = Mth.lerpInt(delta, getGreen(fromColor), getGreen(toColor));
		int blue = Mth.lerpInt(delta, getBlue(fromColor), getBlue(toColor));
		return setRGB(red, green, blue);
	}

	public static int getRed(int color) {
		return (color & 0xff0000) >> 16;
	}

	public static int getGreen(int color) {
		return (color & 0xff00) >> 8;
	}

	public static int getBlue(int color) {
		return (color & 0xff);
	}

	public static int setRGB(int red, int green, int blue) {
		return ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
	}

}

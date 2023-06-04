package blazingtwist.itemcounts.util;

public class ColorHelper {

	public static int lerpColor(float delta, int fromColor, int toColor) {
		int red = lerpInt(delta, getRed(fromColor), getRed(toColor));
		int green = lerpInt(delta, getGreen(fromColor), getGreen(toColor));
		int blue = lerpInt(delta, getBlue(fromColor), getBlue(toColor));
		return setRGB(red, green, blue);
	}

	public static int lerpInt(float delta, int start, int end) {
		return start + (int) Math.floor(delta * (end - start));
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

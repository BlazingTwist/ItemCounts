package blazingtwist.itemcounts.config;

import net.minecraft.client.font.TextRenderer;

public enum TextAnchorOption {
	@AutoConfigEnum LEFT(TextAnchorOption::applyAnchorLeft),
	@AutoConfigEnum CENTER(TextAnchorOption::applyAnchorCenter),
	@AutoConfigEnum RIGHT(TextAnchorOption::applyAnchorRight);

	private final AnchorFunction anchorFunction;

	TextAnchorOption(AnchorFunction anchorFunction) {
		this.anchorFunction = anchorFunction;
	}

	public float applyAnchorOffset(float currentPosition, String text, TextRenderer renderer) {
		return anchorFunction.apply(currentPosition, text, renderer);
	}

	private static float applyAnchorLeft(float currentPosition, String text, TextRenderer renderer) {
		return currentPosition;
	}

	private static float applyAnchorCenter(float currentPosition, String text, TextRenderer renderer) {
		return currentPosition - (renderer.getWidth(text) / 2f);
	}

	private static float applyAnchorRight(float currentPosition, String text, TextRenderer renderer) {
		return currentPosition - renderer.getWidth(text);
	}

	@FunctionalInterface
	private interface AnchorFunction {
		float apply(float currentPosition, String text, TextRenderer renderer);
	}
}

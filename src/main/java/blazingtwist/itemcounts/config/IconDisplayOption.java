package blazingtwist.itemcounts.config;

import java.util.function.Function;

public enum IconDisplayOption {
	@AutoConfigEnum NEVER(renderedText -> false),
	@AutoConfigEnum ALWAYS(renderedText -> true),
	@AutoConfigEnum WHEN_TEXT_VISIBLE(renderedText -> renderedText);

	private final Function<Boolean, Boolean> acceptanceCriteria;

	IconDisplayOption(Function<Boolean, Boolean> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean shouldShowIcon(boolean didRenderText) {
		return acceptanceCriteria.apply(didRenderText);
	}
}

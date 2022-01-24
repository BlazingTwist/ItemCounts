package blazingtwist.itemcounts.config;

import java.util.function.Function;

public enum IconDisplayOption {
	NEVER(renderedText -> false),
	ALWAYS(renderedText -> true),
	WHEN_TEXT_VISIBLE(renderedText -> renderedText);

	private final Function<Boolean, Boolean> acceptanceCriteria;

	IconDisplayOption(Function<Boolean, Boolean> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public boolean shouldShowIcon(boolean didRenderText){
		return acceptanceCriteria.apply(didRenderText);
	}
}

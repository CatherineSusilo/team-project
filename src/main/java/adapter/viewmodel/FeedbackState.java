package adapter.viewmodel;

/**
 * Feedback under one quiz question.
 *
 * @param correct whether to show it as correct (green) or not (red)
 * @param verdict e.g. "Correct." or "Not answered yet."
 * @param explanation shown after the verdict; empty for skipped questions
 */
public record FeedbackState(boolean correct, String verdict, String explanation) {
}

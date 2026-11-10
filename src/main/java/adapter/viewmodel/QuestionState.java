package adapter.viewmodel;

import java.util.List;

/**
 * One quiz question as the quiz page shows it.
 *
 * @param prompt the question text
 * @param type SINGLE, MULTIPLE or IMAGE
 * @param choices choice labels, or image resource paths for IMAGE questions
 */
public record QuestionState(String prompt, String type, List<String> choices) {
}

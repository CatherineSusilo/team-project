package usecase.checkquiz;

import java.util.List;
import java.util.Set;

/**
 * The student's quiz answers.
 *
 * @param moduleTitle the module the quiz belongs to
 * @param answers for each question in order, the indexes of the selected choices
 * @param submit false to only check the answers (Check Answers), true to also save progress
 *     and move on (Continue)
 */
public record CheckQuizInputData(String moduleTitle, List<Set<Integer>> answers,
    boolean submit) {
}

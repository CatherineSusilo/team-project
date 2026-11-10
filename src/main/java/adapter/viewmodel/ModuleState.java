package adapter.viewmodel;

import java.util.List;

/**
 * One module as the dashboard shows it. Every field is ready to display.
 *
 * @param title display name
 * @param dueDate submission deadline
 * @param status "Completed" or "To do"
 * @param lastModified when it was last edited, or "NOT STARTED"
 * @param started whether the student has worked on it, which shows Continue
 * @param overview short description of the pre-lab
 * @param questions the pre-procedure quiz
 */
public record ModuleState(String title, String dueDate, String status, String lastModified,
    boolean started, String overview, List<QuestionState> questions) {
}

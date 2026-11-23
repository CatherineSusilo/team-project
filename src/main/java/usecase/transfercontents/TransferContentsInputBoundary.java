package usecase.transfercontents;

/**
 * Moves something into a container (or mixes it) and applies the chemistry.
 */
public interface TransferContentsInputBoundary {

  /**
   * Executes the transfer contents use case.
   *
   * @param input what the student did
   */
  void execute(TransferContentsInputData input);
}

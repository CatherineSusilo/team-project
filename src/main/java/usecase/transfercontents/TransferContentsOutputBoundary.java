package usecase.transfercontents;

/**
 * Receives the result of a transfer.
 */
public interface TransferContentsOutputBoundary {

  /**
   * Shows the container after the transfer.
   *
   * @param output   what the container looks like now
   */
  void prepareSuccessView(TransferContentsOutputData output);

  /**
   * Shows why the transfer didn't happen. Nothing on the bench changed.
   *
   * @param error what the student did wrong, in plain words
   */
  void prepareFailView(String error);
}

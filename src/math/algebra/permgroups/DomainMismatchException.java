package math.algebra.permgroups;

public class DomainMismatchException extends RuntimeException {
  private final Permutation p;
  private final Permutation q;

  public DomainMismatchException(Permutation p, Permutation q) {
    this.p = p;
    this.q = q;
  }

  private static final long serialVersionUID = 1L;
}

package math.numbertheory;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

import java.util.BitSet;
import java.util.List;

import math.numbertheory.Factorization.Factor;

public final class Factorization extends ForwardingList<Factor> {
  public static final class Factor {
    private final int prime;
    private final int exponent;
    private transient int product = 0;

    Factor(int prime, int exponent) {
      this.prime = prime;
      this.exponent = exponent;
    }

    public int getExponent() {
      return exponent;
    }

    public int getPrime() {
      return prime;
    }

    public int getProduct() {
      if (product == 0) {
        int acc = 1;
        int base = prime;
        int exp = exponent;
        while (true) {
          switch (exp) {
            case 0:
              return product = acc;
            case 1:
              return product = base * acc;
            default:
              if ((exp & 1) != 0) {
                acc *= base;
              }
              base *= base;
              exp >>= 1;
          }
        }
      }
      return product;
    }
  }
  private static final class FactoredOut {
    private int factor;
    private int exponent;
    private int quotient;

    private FactoredOut(int factor, int exponent, int quotient) {
      this.factor = factor;
      this.exponent = exponent;
      this.quotient = quotient;
    }
  }

  private static final BitSet SMALL_PRIMES = new BitSet(1000);

  static {
    SMALL_PRIMES.set(2, 1000);
    for (int i = 2; i * i < 1000; i = SMALL_PRIMES.nextSetBit(i + 1)) {
      for (int j = 2 * i; j < 1000; j += i) {
        SMALL_PRIMES.clear(j);
      }
    }
  }

  public static Factorization factorize(int n) {
    int n0 = n;
    ImmutableList.Builder<Factor> builder = ImmutableList.builder();
    for (int p = 2; p < 1000 && p * p <= n; p = SMALL_PRIMES.nextSetBit(p + 1)) {
      FactoredOut factored = factorOut(n, p);
      if (factored.exponent > 0) {
        builder.add(new Factor(factored.factor, factored.exponent));
        n = factored.quotient;
      }
    }
    for (int p = 1001; p * p <= n; p++) {
      FactoredOut factored = factorOut(n, p);
      if (factored.exponent > 0) {
        builder.add(new Factor(factored.factor, factored.exponent));
        n = factored.quotient;
      }
    }
    if (n > 1) {
      builder.add(new Factor(n, 1));
    }
    return new Factorization(builder.build(), n0);
  }

  private static FactoredOut factorOut(int n, int p) {
    if (n % p != 0) {
      return new FactoredOut(p, 0, n);
    }
    FactoredOut f = factorOut(n, p * p);
    f.factor = p;
    f.exponent *= 2;
    if (f.quotient % p == 0) {
      f.exponent++;
      f.quotient /= p;
    }
    return f;
  }

  private final List<Factor> factors;
  private final int n;

  private Factorization(List<Factor> factors, int n) {
    this.factors = factors;
    this.n = n;
  }

  public int getProduct() {
    return n;
  }

  @Override protected List<Factor> delegate() {
    return factors;
  }
}

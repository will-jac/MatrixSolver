/******************************************************************************
 *  Compilation:  javac Rational.java
 *  Execution:    java Rational
 *
 *  @author https://introcs.cs.princeton.edu/java/92symbolic/Rational.java.html
 *      Edited by Jack Williams to extend Number
 *  Immutable ADT for Rational numbers. 
 * 
 *  Invariants
 *  -----------
 *   - gcd(num, den) = 1, i.e, the rational number is in reduced form
 *   - den >= 1, the denominator is always a positive integer
 *   - 0/1 is the unique representation of 0
 *
 *  We employ some tricks to stave of overflow, but if you
 *  need arbitrary precision rationals, use BigRational.java.
 *
 ******************************************************************************/

public class Rational extends Number implements Comparable<Rational> {
    /**
     * Version 1
     */
    private static final long serialVersionUID = 7092925719285588225L;
    /** Unique representation of zero */
    public static Rational zero = new Rational(0, 1);
    /** Unique representation of one */
    public static Rational one = new Rational(1, 1);
    /** Numerator */
    private int num;   // the numerator
    /** Denominator */
    private int den;   // the denominator

    /** create and initialize a new Rational object */
    public Rational(int numerator, int denominator) {

        if (denominator == 0) {
            throw new ArithmeticException("denominator is zero");
        }

        // reduce fraction
        int g = gcd(numerator, denominator);
        num = numerator   / g;
        den = denominator / g;

        // needed only for negative numbers
        if (den < 0) { den = -den; num = -num; }
    }
    /** 
     * creates a Rational representation of the double object.
     * WARNING: will approximate values to best fit a fraction.
     */
    public Rational(Double value) {
        if (value.equals(0.0)) {
            num = 0;
            den = 1;
        }
        // if value is an integer (ie 2.0)
        else if (value - (double) value.intValue() == 0) {
            num = value.intValue();
            den = 1;
        }
        else
        {
            Rational rvalue = valueOf(value);
            num = rvalue.num;
            den = rvalue.den;
        }
    }
    
    public Rational(Rational a) {
       num = a.num;
       den = a.den;
    }
    
    /** @return the numerator and denominator of (this) */
    public int numerator()   { return num; }
    public int denominator() { return den; }

    /** @return double precision representation of (this) */
    public double toDouble() {
        return (double) num / den;
    }

    /** @return string representation of (this) */
    @Override
    public String toString() { 
        if (den == 1) return num + "";
        else          return num + "/" + den;
    }

    /** @return { -1, 0, +1 } if a < b, a = b, or a > b */
    public int compareTo(Rational b) {
        Rational a = this;
        int lhs = a.num * b.den;
        int rhs = a.den * b.num;
        if (lhs < rhs) return -1;
        if (lhs > rhs) return +1;
        return 0;
    }

    /** Checks if this Rational object's arithmatic value is
     *  equal to the arithmatic value of y - does NOT determine
     *  if the two are the same object, just if they represent the same
     *  value (ie (new Rational(0, 1)).equals(0.0) == true)
     *  
     *  TODO: Implement what has been described above. Currently,
     *  this method checks that y is a Rational, so that isn't happening
     *  
     *  @return is this Rational object arithmatically equal to y? */
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Rational b = (Rational) y;
        return compareTo(b) == 0;
    }
    
    /** hashCode consistent with equals() and compareTo()
     * (better to hash the numerator and denominator and combine)
     * @return the hashcode of (this).toString().hashCode()
     */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /** @return a new rational (r.num + s.num) / (r.den + s.den) */
    public static Rational mediant(Rational r, Rational s) {
        return new Rational(r.num + s.num, r.den + s.den);
    }

    /** @return gcd(|m|, |n|) */
    private static int gcd(int m, int n) {
        if (m < 0) m = -m;
        if (n < 0) n = -n;
        if (0 == n) return m;
        else return gcd(n, m % n);
    }

    /** @return lcm(|m|, |n|) */
    private static int lcm(int m, int n) {
        if (m < 0) m = -m;
        if (n < 0) n = -n;
        return m * (n / gcd(m, n));    // parentheses important to avoid overflow
    }

    /** @return a * b, staving off overflow as much as possible by cross-cancellation */
    public Rational times(Rational b) {
        Rational a = this;

        // reduce p1/q2 and p2/q1, then multiply, where a = p1/q1 and b = p2/q2
        Rational c = new Rational(a.num, b.den);
        Rational d = new Rational(b.num, a.den);
        return new Rational(c.num * d.num, c.den * d.den);
    }


    /** @return a + b, staving off overflow */
    public Rational plus(Rational b) {
        Rational a = this;

        // special cases
        if (a.compareTo(zero) == 0) return b;
        if (b.compareTo(zero) == 0) return a;

        // Find gcd of numerators and denominators
        int f = gcd(a.num, b.num);
        int g = gcd(a.den, b.den);

        // add cross-product terms for numerator
        Rational s = new Rational((a.num / f) * (b.den / g) + (b.num / f) * (a.den / g),
                                  lcm(a.den, b.den));

        // multiply back in
        s.num *= f;
        return s;
    }

    /** @return -a */
    public Rational negate() {
        return new Rational(-num, den);
    }

    /** @return |a| */
    public Rational abs() {
        if (num >= 0) return this;
        else return negate();
    }

    /** @return a - b */
    public Rational minus(Rational b) {
        Rational a = this;
        return a.plus(b.negate());
    }

    /** @return 1 / (this) */
    public Rational reciprocal() { 
        return new Rational(den, num);  
    }

    /** @return a / b */
    public Rational divides(Rational b) {
        Rational a = this;
        return a.times(b.reciprocal());
    }
    
    /** @return a new, clean rational */
    public Rational clone() {
        return new Rational(num, den);
    }

    // methods to extend the number class
    
    /** @return the double representation of this rational */
    @Override
    public double doubleValue()
    {
        return (double) num / (double) den;
    }
    
    /** @retutn the float representation of this rational */
    @Override
    public float floatValue()
    {
        return (float) num / (float) den;
    }

    /** @return the int representation of this rational, truncated */
    @Override
    public int intValue()
    {
        return num / den;
    }
    
    /** @return the long representation of this rational */
    @Override
    public long longValue()
    {
        return (long) num / (long) den;
    }
    
    
    // rational representation of doubles
    
    public static long getMantissaBits(double value) {
        // select the 52 lower bits which make up the mantissa
        return Double.doubleToLongBits(value) & 0xFFFFFFFFFFFFFL;
    }

    public static long getMantissa(double value) {
        // add the "hidden" 1 of normalized doubles
        return (1L << 52) + getMantissaBits(value);
    }

    public static long getExponent(double value) {
        int exponentOffset = 52;
        long lowest11Bits = 0x7FFL;
        long shiftedBiasedExponent = Double.doubleToLongBits(value) & (lowest11Bits << exponentOffset);
        long biasedExponent = shiftedBiasedExponent >> exponentOffset;
        // remove the bias
        return biasedExponent - 1023;
    }

    public static Rational valueOf(double value) {
        long mantissa = getMantissa(value);
        long exponent = getExponent(value) - 52;
        int numberOfTrailingZeros = Long.numberOfTrailingZeros(mantissa);
        mantissa >>= numberOfTrailingZeros;
        exponent += numberOfTrailingZeros;
        // apply the sign to the numerator
        long numerator = (long) Math.signum(value) * mantissa;
        if(exponent < 0)
            return new Rational( (int) numerator, (int) (1L << -exponent));
        else
            return new Rational( (int) (numerator << exponent), 1);
    }
}
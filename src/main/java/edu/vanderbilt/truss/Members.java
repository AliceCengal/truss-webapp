package edu.vanderbilt.truss;

/**
 * Factory for the Member interface.
 *
 * Created by athran on 4/30/14.
 */
public class Members {

    public static Member create(final int id,
                                final int left,
                                final int right,
                                final double area,
                                final double e) {
        return new Member() {
            @Override public int id()            { return id; }
            @Override public int jointLeft()     { return left; }
            @Override public int jointRight()    { return right; }
            @Override public double area()       { return area; }
            @Override public double elasticity() { return e; }
        };
    }

}

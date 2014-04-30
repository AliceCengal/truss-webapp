package edu.vanderbilt.truss;

/**
 * Factory for the Joint interface.
 *
 * Created by athran on 4/30/14.
 */
public class Joints {

    public static Joint create(final int     id,
                               final double  xx,
                               final double  yy,
                               final boolean xRestrain,
                               final boolean yRestrain,
                               final double  xLoad,
                               final double  yLoad) {
        return new Joint() {
            @Override public int id() {
                return id;
            }

            @Override public double xCoor() {
                return xx;
            }

            @Override public double yCoor() {
                return yy;
            }

            @Override public boolean isRestraintX() {
                return xRestrain;
            }

            @Override public boolean isRestraintY() {
                return yRestrain;
            }

            @Override public double loadX() {
                return xLoad;
            }

            @Override public double loadY() {
                return yLoad;
            }
        };
    }

}

package edu.vanderbilt.truss;

/**
 * Describes the general interface of the Joint object
 *
 * Created by athran on 4/17/14.
 */
public interface Joint {

    int     id();
    double  xCoor();
    double  yCoor();
    boolean isRestraintX();
    boolean isRestraintY();
    double  loadX();
    double  loadY();

}

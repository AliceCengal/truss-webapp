package edu.vanderbilt.truss;

/**
 * Describes the general interface of the Member object
 *
 * Created by athran on 4/17/14.
 */
public interface Member {

    int    id();
    int    jointLeft();
    int    jointRight();
    double area();
    double elasticity();

}

package edu.vanderbilt.truss;

import java.util.Set;

/**
 * Structural representation of the computation's input
 *
 * Created by athran on 4/17/14.
 */
public interface InputStruct {

    String      studentId();
    String      inputSetId();
    Set<Joint>  joints();
    Set<Member> members();

}

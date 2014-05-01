package edu.vanderbilt.truss;

import java.util.Set;

/**
 * Structural representation of the computation's result
 *
 * Created by athran on 4/17/14.
 */
public interface ResultStruct {

    Set<JointResultStruct> jointSet();
    Set<MemberResultStruct> memberSet();
    String message();
    boolean isSuccessful();

}

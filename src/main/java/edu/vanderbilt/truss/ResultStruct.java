package edu.vanderbilt.truss;

import java.util.Set;

/**
 * Structural representation of the computation's result.
 *
 * The computation results are in the Sets of result structs. The computation may or
 * may not be successful. The `message()` method will give the error message when
 * `isSuccessful()` return false.
 *
 * Created by athran on 4/17/14.
 */
public interface ResultStruct {

    Set<JointResultStruct> jointSet();
    Set<MemberResultStruct> memberSet();
    String message();
    boolean isSuccessful();

}

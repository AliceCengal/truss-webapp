package edu.vanderbilt.truss;

import java.util.HashSet;
import java.util.Set;

/**
 * Create the result objects
 *
 * Created by athran on 5/1/14.
 */
public class ResultFactory {

    public static JointResultStruct jointResult(final int id,
                                                final double xd,
                                                final double yd,
                                                final double xr,
                                                final double yr) {
        return new JointResultStruct() {
            @Override public int id() { return id; }
            @Override public double displacementX() { return xd; }
            @Override public double displacementY() { return yd; }
            @Override public double reactionX() { return xr; }
            @Override public double reactionY() { return yr; }
        };
    }

    public static MemberResultStruct memberResult(final int id,
                                                  final double force) {
        return new MemberResultStruct() {
            @Override public int id() { return id; }
            @Override public double memberForce() { return force; }
        };
    }

    public static ResultStruct dummyResult() {
        SimpleResult r = new SimpleResult();
        r.joints.add(jointResult(1, 0.288359, -0.046875, 0.0, 17.5));
        r.members.add(memberResult(1, -12.0));
        return r;
    }

    private static class SimpleResult implements ResultStruct {

        Set<JointResultStruct> joints = new HashSet<JointResultStruct>();
        Set<MemberResultStruct> members = new HashSet<MemberResultStruct>();

        @Override
        public Set<JointResultStruct> jointSet() {
            return joints;
        }

        @Override
        public Set<MemberResultStruct> memberSet() {
            return members;
        }
    }

}

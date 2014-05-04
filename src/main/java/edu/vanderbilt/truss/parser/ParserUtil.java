package edu.vanderbilt.truss.parser;

import java.util.HashSet;
import java.util.Set;

import edu.vanderbilt.truss.InputStruct;
import edu.vanderbilt.truss.Joint;
import edu.vanderbilt.truss.Member;

import static edu.vanderbilt.truss.struct.Joint.compatCreateJoint;
import static edu.vanderbilt.truss.struct.Member.compatCreateMember;

/**
 * Factory for the InputSetParser
 *
 * Created by athran on 4/17/14.
 */
public final class ParserUtil {

    private ParserUtil() {}

    public static InputSetParser getParser(String inputSetFilePath) {
        return new InputSetParser() {
            @Override
            public InputStruct parse() {
                return new DummyInputStruct();
            }
        };
    }

    private static class DummyInputStruct implements InputStruct {

        private Set<Joint> jointSet = new HashSet<Joint>();
        private Set<Member> memberSet = new HashSet<Member>();

        DummyInputStruct() {
            jointSet.add(compatCreateJoint(1, 0, 120, false, false, 0, 0));
            jointSet.add(compatCreateJoint(2, 144, 120, false, false, -12, 0));
            jointSet.add(compatCreateJoint(3, 0, 60, false, false, 0, 0));
            jointSet.add(compatCreateJoint(4, 144, 60, false, false, -18, 0));
            jointSet.add(compatCreateJoint(5, 0, 0, false, true, 0, 0));
            jointSet.add(compatCreateJoint(6, 144, 0, true, true, 0, 0));

            memberSet.add(compatCreateMember(1, 1, 2, 0.96, 30000));
            memberSet.add(compatCreateMember(2, 1, 3, 0.96, 30000));
            memberSet.add(compatCreateMember(3, 1, 4, 1.43, 30000));

            memberSet.add(compatCreateMember(4, 2, 4, 0.96, 30000));
            memberSet.add(compatCreateMember(5, 3, 4, 1.43, 30000));
            memberSet.add(compatCreateMember(6, 3, 5, 0.96, 30000));

            memberSet.add(compatCreateMember(7, 3, 6, 1.88, 30000));
            memberSet.add(compatCreateMember(8, 4, 6, 0.96, 30000));
            memberSet.add(compatCreateMember(9, 5, 6, 0.96, 30000));
        }

        @Override
        public String studentId() {
            return "John Doe";
        }

        @Override
        public String inputSetId() {
            return "Problem#1";
        }

        @Override
        public Set<Joint> joints() {
            return jointSet;
        }

        @Override
        public Set<Member> members() {
            return memberSet;
        }
    }

}

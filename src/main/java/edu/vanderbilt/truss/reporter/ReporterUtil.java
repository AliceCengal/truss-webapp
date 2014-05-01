package edu.vanderbilt.truss.reporter;

import edu.vanderbilt.truss.JointResultStruct;
import edu.vanderbilt.truss.MemberResultStruct;
import edu.vanderbilt.truss.ResultStruct;
import edu.vanderbilt.truss.NumberFormatter;

import java.io.PrintStream;

/**
 * Factory for the ResultSetReporter
 *
 * Created by athran on 4/17/14.
 */
public final class ReporterUtil {

    private ReporterUtil() {}

    public static ResultSetReporter getReporter(PrintStream stdOut) {
        SimpleReporter r = new SimpleReporter();
        r.stdOut = stdOut;
        return r;
    }

    private static class SimpleReporter implements ResultSetReporter {

        PrintStream stdOut;

        private static final NumberFormatter FORMAT3 = new NumberFormatter(10, 3);
        private static final NumberFormatter FORMAT6 = new NumberFormatter(10, 6);

        @Override
        public void report(ResultStruct result) {

            stdOut.println("Joint Displacements:");
            for (JointResultStruct j : result.jointSet()) {
                stdOut.print("\t");
                stdOut.print(j.id());
                stdOut.print("\t");
                stdOut.print(FORMAT6.round(j.displacementX()));
                stdOut.print("\t");
                stdOut.println(FORMAT6.round(j.displacementY()));
            }

            stdOut.println();
            stdOut.println("Member Forces:");
            for (MemberResultStruct m : result.memberSet()) {
                stdOut.print("\t");
                stdOut.print(m.id());
                stdOut.print("\t");
                stdOut.println(FORMAT3.round(m.memberForce()));
            }

            stdOut.println();
            stdOut.println("Reactions:");
            for (JointResultStruct j : result.jointSet()) {
                stdOut.print("\t");
                stdOut.print(j.id());
                stdOut.print("\t");
                stdOut.print(FORMAT3.round(j.reactionX()));
                stdOut.print("\t");
                stdOut.println(FORMAT3.round(j.reactionY()));
            }
        }
    }

}

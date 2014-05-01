package edu.vanderbilt.truss.engine;

import edu.vanderbilt.truss.InputStruct;
import edu.vanderbilt.truss.ResultFactory;
import edu.vanderbilt.truss.ResultStruct;
import edu.vanderbilt.truss.legacy.LegacyInputStruct;
import edu.vanderbilt.truss.legacy.LegacyJoint;
import edu.vanderbilt.truss.legacy.LegacyMember;
import edu.vanderbilt.truss.parser.InputSetParser;
import edu.vanderbilt.truss.reporter.ResultSetReporter;

/**
 * Factory for the TrussEngine (GET IT!?!?)
 *
 * Created by athran on 4/17/14.
 */
public final class EngineUtil {

    /**
     * Create an Engine initialized with the components.
     */
    public static TrussEngine getEngine(InputSetParser inputParser,
                                        ResultSetReporter resultReporter) {
        HackyEngine e = new HackyEngine();
        e.injectInputData(inputParser.parse());
        e.injectOutput(resultReporter);
        return e;
    }

    /**
     * Creates an uninitialized Engine. Initialize it with its proper
     * component before calling `compute()`
     */
    public static TrussEngine getEngine() {
        return new HackyEngine();
    }

    /**
     * Warning: This class contains Deep Magic. Do not touch it unless you know what
     * you're doing. Make sure all changes are well documented. Run the test after
     * every change, making sure that the results computed by this engine stays
     * consistent.
     */
    private static class HackyEngine implements TrussEngine {

        LegacyInputStruct input;
        ResultSetReporter reporter;
        String message;

        @Override
        public void injectInputData(InputStruct input) {
            this.input = LegacyInputStruct.createFromNewStruct(input);
        }

        @Override
        public void injectOutput(ResultSetReporter reporter) {
            this.reporter = reporter;
        }

        @Override public void compute() {
            if (input == null) {
                throw new IllegalStateException("Please inject a data source");
            }
            if (reporter == null) {
                throw new IllegalStateException("Please inject an output channel");
            }

            if (!checkData()) {
                reporter.report(new ResultFactory.ResultBuilder()
                                        .setMessage(message)
                                        .build());
            }
            calculateBWAndDirCos();
            calculateUnrestrainedStiffnessMx();
            calculateSupportRestraintsAndFormLoad();
            calculateSubstitution();
            reporter.report(calculate());
        }

        private boolean checkData() {
            if (input.joints.size() < 2) {
                message = MSG_MORE_JOINTS;
                return false;
            }
            if (input.members.size() == 0) {
                message = MSG_NO_MEMBER;
                return false;
            }
            if (input.members.size() + input.supportRestraintCount < 2 * input.joints.size()) {
                message = MSG_UNSTABLE;
                return false;
            }
            return true;
        }

        @SuppressWarnings("ForLoopReplaceableByForEach")
        private void calculateBWAndDirCos() {
            for (int i = 0; i < input.members.size(); ++i) {
                final int j1 = input.members.get(i).j1;
                final int j2 = input.members.get(i).j2;
                final int maxb = 2 * (Math.abs(j2 - j1) + 1);
                if (maxb > input.maxb) {
                    input.maxb = maxb;
                }
                final double x = input.joints.get(j1).x;
                final double y = input.joints.get(j1).y;
                final double x2 = input.joints.get(j2).x;
                final double y2 = input.joints.get(j2).y;
                final double n = x2 - x;
                final double n2 = y2 - y;
                final double sqrt = Math.sqrt(n * n + n2 * n2);
                input.members.get(i).cosx = n / sqrt;
                input.members.get(i).cosy = n2 / sqrt;
                input.members.get(i).length = sqrt;
            }
        }

        private void calculateUnrestrainedStiffnessMx() {
            input.xkMatrix = new double[2 * input.joints.size()][input.maxb];
            final double[][] array = new double[4][4];
            for (int i = 0; i < 2 * input.joints.size(); ++i) {
                for (int j = 0; j < input.maxb; ++j) {
                    input.xkMatrix[i][j] = 0.0;
                }
            }
            for (int k = 0; k < input.members.size(); ++k) {
                final LegacyMember member = input.members.get(k);
                final double n = member.area * member.elasticity / member.length;
                final double n2 = member.cosx * member.cosx * n;
                final double n3 = member.cosy * member.cosy * n;
                final double n4 = member.cosx * member.cosy * n;
                array[0][0] = n2;
                array[0][1] = n4;
                array[0][2] = -n2;
                array[0][3] = -n4;
                array[1][1] = n3;
                array[1][2] = -n4;
                array[1][3] = -n3;
                array[2][2] = n2;
                array[2][3] = n4;
                array[3][3] = n3;
                for (int l = 0; l < 4; ++l) {
                    for (int n5 = l; n5 < 4; ++n5) {
                        array[n5][l] = array[l][n5];
                    }
                }
                for (int n6 = 1; n6 <= 2; ++n6) {
                    for (int n7 = 1; n7 <= 2; ++n7) {
                        final int n8 = 2 * (member.j1 + 1) - (2 - n6);
                        final int n9 = 2 * (member.j1 + 1) - (2 - n7) - n8 + 1;
                        if (n9 > 0) {
                            input.xkMatrix[n8 - 1][n9 - 1] += array[n6 - 1][n7 - 1];
                        }
                    }
                }
                for (int n11 = 1; n11 <= 2; ++n11) {
                    for (int n12 = 3; n12 <= 4; ++n12) {
                        final int n13 = 2 * (member.j1 + 1) - (2 - n11);
                        final int n14 = 2 * (member.j2 + 1) - (4 - n12) - n13 + 1;
                        if (n14 > 0) {
                            input.xkMatrix[n13 - 1][n14 - 1] += array[n11 - 1][n12 - 1];
                        }
                    }
                }
                for (int n16 = 3; n16 <= 4; ++n16) {
                    for (int n17 = 1; n17 <= 2; ++n17) {
                        final int n18 = 2 * (member.j2 + 1) - (4 - n16);
                        final int n19 = 2 * (member.j1 + 1) - (2 - n17) - n18 + 1;
                        if (n19 > 0) {
                            input.xkMatrix[n18 - 1][n19 - 1] += array[n16 - 1][n17 - 1];
                        }
                    }
                }
                for (int n21 = 3; n21 <= 4; ++n21) {
                    for (int n22 = 3; n22 <= 4; ++n22) {
                        final int n23 = 2 * (member.j2 + 1) - (4 - n21);
                        final int n24 = 2 * (member.j2 + 1) - (4 - n22) - n23 + 1;
                        if (n24 > 0) {
                            input.xkMatrix[n23 - 1][n24 - 1] += array[n21 - 1][n22 - 1];
                        }
                    }
                }
            }
        }

        private void calculateSupportRestraintsAndFormLoad() {
            final double[] array = new double[2];
            input.wVector = new double[2 * input.joints.size()];
            for (int i = 1; i <= input.joints.size(); ++i) {
                final LegacyJoint joint = input.joints.get(i - 1);
                array[0] = joint.jrx;
                array[1] = joint.jry;
                input.wVector[2 * i - 2] = joint.wx;
                input.wVector[2 * i - 1] = joint.wy;
                for (int j = 1; j <= 2; ++j) {
                    if (array[j - 1] != 0.0) {
                        final int supportRestraintCount = 2 * i - (2 - j);
                        input.supportRestraintCount = supportRestraintCount;
                        for (int k = 2; k <= input.maxb; ++k) {
                            input.xkMatrix[supportRestraintCount - 1][k - 1] = 0.0;
                            --input.supportRestraintCount;
                            if (input.supportRestraintCount > 0) {
                                input.xkMatrix[input.supportRestraintCount - 1][k - 1] = 0.0;
                            }
                        }
                        input.xkMatrix[supportRestraintCount - 1][0] = 1.0;
                        input.wVector[supportRestraintCount - 1] = 0.0;
                    }
                }
            }
        }

        private void calculateSubstitution() {
            for (int i = 1; i <= 2 * input.joints.size(); ++i) {
                int n = i;
                if (Math.abs(input.xkMatrix[i - 1][0]) < 1.0E-5) {
                    message = MSG_UNSTABLE;
                    return;
                }
                for (int j = 2; j <= input.maxb; ++j) {
                    ++n;
                    if (input.xkMatrix[i - 1][j - 1] != 0.0) {
                        final double n2 = input.xkMatrix[i - 1][j - 1] / input.xkMatrix[i - 1][0];
                        int n3 = 0;
                        for (int k = j; k <= input.maxb; ++k) {
                            ++n3;
                            if (input.xkMatrix[i - 1][k - 1] != 0.0) {
                                input.xkMatrix[n - 1][n3 - 1] -= n2 * input.xkMatrix[i - 1][k - 1];
                            }
                        }
                        input.xkMatrix[i - 1][j - 1] = n2;
                        input.wVector[n - 1] -= n2 * input.wVector[i - 1];
                    }
                }
                input.wVector[i - 1] /= input.xkMatrix[i - 1][0];
            }
            int n7 = 2 * input.joints.size();
            while (--n7 > 0) {
                int n8 = n7;
                for (int l = 2; l <= input.maxb; ++l) {
                    ++n8;
                    if (input.xkMatrix[n7 - 1][l - 1] != 0.0) {
                        input.wVector[n7 - 1] -= input.xkMatrix[n7 - 1][l - 1] * input.wVector[n8 - 1];
                    }
                }
            }
        }

        private ResultStruct calculate() {
            final double[] memberForces = new double[input.members.size()];
            for (int i = 1; i <= input.members.size(); ++i) {
                final LegacyMember member = input.members.get(i - 1);
                memberForces[i - 1] = member.area * member.elasticity /
                        member.length * (
                        member.cosx * (input.wVector[2 * (member.j2 + 1) - 2]
                                - input.wVector[2 * (member.j1 + 1) - 2])
                                + member.cosy * (input.wVector[2 * (member.j2 + 1) - 1]
                                - input.wVector[2 * (member.j1 + 1) - 1]));
            }
            final double[] reactionX = new double[input.joints.size()];
            final double[] reactionY = new double[input.joints.size()];
            final int[] restraintX = new int[input.joints.size()];
            final int[] restraintY = new int[input.joints.size()];

            for (int j = 0; j < input.joints.size(); ++j) {
                restraintX[j] = input.joints.get(j).jrx;
                restraintY[j] = input.joints.get(j).jry;
                if (restraintX[j] != 0) {
                    reactionX[j] = -input.joints.get(j).wx;
                }
                if (restraintY[j] != 0) {
                    reactionY[j] = -input.joints.get(j).wy;
                }
            }

            for (int k = 0; k < input.members.size(); ++k) {
                final int j2 = input.members.get(k).j1;
                final int j3 = input.members.get(k).j2;
                if (restraintX[j2] != 0) {
                    reactionX[j2] -= input.members.get(k).cosx * memberForces[k];
                }
                if (restraintY[j2] != 0) {
                    reactionY[j2] -= input.members.get(k).cosy * memberForces[k];
                }
                if (restraintX[j3] != 0) {
                    reactionX[j3] += input.members.get(k).cosx * memberForces[k];
                }
                if (restraintY[j3] != 0) {
                    reactionY[j3] += input.members.get(k).cosy * memberForces[k];
                }
            }

            // building the result set
            ResultFactory.ResultBuilder result = new ResultFactory.ResultBuilder();

            for (int i = 0; i < input.members.size(); ++i) {
                result.addMember(ResultFactory.memberResult(memberIdForIndex(i),
                                                            memberForces[i]));
            }

            for (int i = 0; i < input.joints.size(); ++i) {
                if (Math.abs(reactionX[i]) < 1.0E-10) {
                    reactionX[i] = 0.0;
                }
                if (Math.abs(reactionY[i]) < 1.0E-10) {
                    reactionY[i] = 0.0;
                }
                result.addJoint(ResultFactory.jointResult(jointIdForIndex(i),
                                                          input.wVector[2 * i],
                                                          input.wVector[2 * i + 1],
                                                          reactionX[i],
                                                          reactionY[i]));
            }

            return result.build();
        }

        private int memberIdForIndex(int index) {
            return index + 1;
        }

        private int jointIdForIndex(int index) {
            return index + 1;
        }

        private static final String MSG_MORE_JOINTS = "DATA ERROR: Need at least 2 Joints.";
        private static final String MSG_NO_MEMBER = "DATA ERROR: No Members.";
        private static final String MSG_UNSTABLE = "DATA ERROR: Unstable truss.";

    }

}

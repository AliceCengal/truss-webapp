package edu.vanderbilt.truss.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.vanderbilt.truss.InputStruct;
import edu.vanderbilt.truss.parser.ParserUtil;

public class Truss2D {

    private static final int JOINT_SECTION    = 1;
    private static final int MATERIAL_SECTION = 2;
    private static final int MEMBER_SECTION   = 3;
    private static final int SUPPORT_SECTION  = 4;
    private static final int LOAD_SECTION     = 5;

    private static final NumberFormatter FORMAT3 = new NumberFormatter(10, 3);
    private static final NumberFormatter FORMAT6 = new NumberFormatter(10, 6);

    LegacyInputStruct input;
    BufferedReader dataInput;
    PrintStream dataOutput;

    public Truss2D(final PrintStream stdout) {
        this.dataOutput = stdout;
    }

    /**
     * Run the calculations. Must be called after `injectData()`
     *
     * @throws IOException
     * @throws NumberFormatException
     */
    public void run() throws IOException, NumberFormatException {
        if (!this.checkData()) {
            return;
        }
        this.calculateBWAndDirCos();
        this.calculateUnrestrainedStiffnessMx();
        this.calculateSupportRestraintsAndFormLoad();
        this.calculateSubstitution();
        this.calculate();
    }

    public void injectData(InputStream dataSource) throws IOException, NumberFormatException {
        dataInput = new BufferedReader(new InputStreamReader(dataSource));

        // for now we use the original parser. I plan to write a new parser to
        // transform the original input sheet into an InputStruct, then feed
        // that into the other injectData method.
        //scanInputData();
        this.injectData(ParserUtil.getParser("").parse());
    }

    public void injectData(InputStruct newInputStruct) {
        input = LegacyInputStruct.createFromNewStruct(newInputStruct);
    }

    private boolean scanInputData() throws IOException, NumberFormatException {
        int sectionIndex = 0;
        int sourceLine = 0;
        this.dataInput.readLine();
        ++sourceLine;
        this.dataInput.readLine();
        ++sourceLine;
        this.dataInput.readLine();
        ++sourceLine;
        try {
            while (this.dataInput.ready()) {
                final String line = this.dataInput.readLine();
                ++sourceLine;
                if (line.length() != 0) {
                    if (!Character.isDigit(line.charAt(0))) {
                        ++sectionIndex;
                    } else {
                        final StringTokenizer stringTokenizer = new StringTokenizer(line, ", ");
                        switch (sectionIndex) {
                            default: {
                                continue;
                            }
                            case JOINT_SECTION: {
                                int jointId = Integer.parseInt(stringTokenizer.nextToken());
                                --jointId;
                                final Float jointX = new Float(stringTokenizer.nextToken());
                                final Float jointY = new Float(stringTokenizer.nextToken());
                                final LegacyJoint joint = new LegacyJoint();
                                joint.x = jointX;
                                joint.y = jointY;
                                convolutedInsert(input.joints, joint, jointId);
                                continue;
                            }
                            case MATERIAL_SECTION: {
                                int materialId = Integer.parseInt(stringTokenizer.nextToken());
                                --materialId;
                                final Float elasticity = new Float(stringTokenizer.nextToken());
                                convolutedInsert(input.materialData, elasticity, materialId);
                                continue;
                            }
                            case MEMBER_SECTION: {
                                int memberId = Integer.parseInt(stringTokenizer.nextToken());
                                --memberId;
                                final Integer leftJointId = new Integer(stringTokenizer.nextToken());
                                final Integer rightJointId = new Integer(stringTokenizer.nextToken());
                                final LegacyMember member = new LegacyMember();
                                member.j1 = leftJointId - 1;
                                member.j2 = rightJointId - 1;
                                member.area = new Float(stringTokenizer.nextToken());
                                if (input.materialData.size() > 1) {
                                    member.elasticity = input.materialData.get(
                                            Integer.parseInt(stringTokenizer.nextToken()) - 1);
                                } else {
                                    member.elasticity = input.materialData.get(0);
                                }
                                convolutedInsert(input.members, member, memberId);
                                continue;
                            }
                            case SUPPORT_SECTION: {
                                int jointId = Integer.parseInt(stringTokenizer.nextToken());
                                --jointId;
                                final MyPoint supportData =
                                        new MyPoint(Integer.parseInt(stringTokenizer.nextToken()),
                                                    Integer.parseInt(stringTokenizer.nextToken()));

                                convolutedInsert(input.restraintData, supportData, jointId);
                                input.joints.get(jointId).restraint = true;
                                continue;
                            }
                            case LOAD_SECTION: {
                                int jointId = Integer.parseInt(stringTokenizer.nextToken());
                                --jointId;
                                final Float loadX = new Float(stringTokenizer.nextToken());
                                final Float loadY = new Float(stringTokenizer.nextToken());
                                input.joints.get(jointId).wx = loadX;
                                input.joints.get(jointId).wy = loadY;
                                input.joints.get(jointId).load = true;
                            }
                        }
                    }
                }
            }
        }
        catch (NumberFormatException ex) {
            this.dataOutput.println("\nDATA ERROR: Wrong input in line: " + sourceLine + "! ");
            return false;
        }
        catch (NoSuchElementException ex2) {
            this.dataOutput.println("\nDATA ERROR: Missing input in line: " + sourceLine + "! ");
            return false;
        }
        if (sectionIndex != 5) {
            this.dataOutput.println("\nDATA ERROR: Missing input data at end of file!\n");
            return false;
        }

        final MyPoint pointNull = new MyPoint(0, 0);
        for (int i = 0; i < input.restraintData.size(); ++i) {
            if (input.restraintData.get(i) == null) {
                input.restraintData.set(i, pointNull);
            }
        }
        input.supportRestraintCount = 0;
        for (int j = 0; j < input.restraintData.size(); ++j) {
            if (input.restraintData.get(j) != null) {
                input.supportRestraintCount += input.restraintData.get(j).x
                        + input.restraintData.get(j).y;
                input.joints.get(j).jrx = input.restraintData.get(j).x;
                input.joints.get(j).jry = input.restraintData.get(j).y;
            }
        }
        return true;
    }
    
    private boolean checkData() {
        if (input.joints.size() < 2) {
            this.dataOutput.println("\nDATA ERROR: Need at least 2 Joints.\n\n");
            return false;
        }
        if (input.members.size() == 0) {
            this.dataOutput.println("\nDATA ERROR: No Members.\n\n");
            return false;
        }
        if (input.members.size() + input.supportRestraintCount < 2 * input.joints.size()) {
            this.dataOutput.println("\nDATA ERROR: Unstable truss.\n\n");
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
                this.dataOutput.println("Unstable!");
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

    private void calculate() throws IOException {
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

        // SUCCESSFUL OUTPUT
        // requires: input.wVector input.joints memberForces input.members
        //           reactionX reactionY
        this.dataOutput.println("Joint Displacements:");
        for (int l = 1; l <= input.joints.size(); ++l) {
            this.dataOutput.println("\t" + l + "\t" +
                                            FORMAT6.round(input.wVector[2 * l - 2]) + "\t" +
                                            FORMAT6.round(input.wVector[2 * l - 1]));
        }
        this.dataOutput.println("\nMember Forces:");
        for (int n5 = 0; n5 < input.members.size(); ++n5) {
            this.dataOutput.println("\t" + (n5 + 1) + "\t" + FORMAT3.round(memberForces[n5]));
        }
        this.dataOutput.println("\nReactions:");
        for (int n6 = 0; n6 < input.joints.size(); ++n6) {
            if (Math.abs(reactionX[n6]) < 1.0E-10) {
                reactionX[n6] = 0.0;
            }
            if (Math.abs(reactionY[n6]) < 1.0E-10) {
                reactionY[n6] = 0.0;
            }
            if (reactionX[n6] != 0.0 || reactionY[n6] != 0.0) {
                this.dataOutput.println("\t" + (n6 + 1) + "\t" +
                                                FORMAT3.round(reactionX[n6]) + "\t" +
                                                FORMAT3.round(reactionY[n6]));
            }
        }
    }
    


    private static <U> void convolutedInsert(List<U> list, U element, int index) {
        // assume index >= 0
        if (index < list.size()) {
            list.set(index, element);

        } else {
            for (int fillerIndex = list.size();
                    fillerIndex < index;
                    ++fillerIndex) {
                list.add(null);
            }
            list.add(element);
        }
    }

}

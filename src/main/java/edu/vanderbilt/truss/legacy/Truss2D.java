package edu.vanderbilt.truss.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.vanderbilt.truss.InputStruct;

public class Truss2D {

    List<Float> materialData;
    List<MyPoint> restraintData;
    int supportRestraintCount;
    int maxb;
    double[][] xkMatrix;
    double[] wVector;

    List<LegacyJoint> joints;
    List<LegacyMember> members;
    BufferedReader dataInput;
    PrintStream dataOutput;

    public Truss2D(final PrintStream stdout) {
        this.dataOutput = stdout;

        this.materialData = new LinkedList<Float>();
        this.restraintData = new LinkedList<MyPoint>();
        this.joints = new LinkedList<LegacyJoint>();
        this.members = new LinkedList<LegacyMember>();
        this.supportRestraintCount = 0;
        this.maxb = 0;
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
        scanInputData();
    }

    public void injectData(InputStruct inputStruct) {

    }

    private boolean scanInputData() throws IOException, NumberFormatException {
        int n = 0;
        int n2 = 0;
        this.dataInput.readLine();
        ++n2;
        this.dataInput.readLine();
        ++n2;
        this.dataInput.readLine();
        ++n2;
        try {
            while (this.dataInput.ready()) {
                final String line = this.dataInput.readLine();
                ++n2;
                if (line.length() != 0) {
                    if (!Character.isDigit(line.charAt(0))) {
                        ++n;
                    }
                    else {
                        final StringTokenizer stringTokenizer = new StringTokenizer(line, ", ");
                        switch (n) {
                            default: {
                                continue;
                            }
                            case 1: {
                                int int1 = Integer.parseInt(stringTokenizer.nextToken());
                                --int1;
                                final Float n3 = new Float(stringTokenizer.nextToken());
                                final Float n4 = new Float(stringTokenizer.nextToken());
                                final LegacyJoint obj = new LegacyJoint();
                                obj.x = n3;
                                obj.y = n4;
                                convolutedInsert(joints, obj, int1);
                                continue;
                            }
                            case 2: {
                                int int2 = Integer.parseInt(stringTokenizer.nextToken());
                                --int2;
                                final Float obj2 = new Float(stringTokenizer.nextToken());
                                convolutedInsert(materialData, obj2, int2);
                                continue;
                            }
                            case 3: {
                                int int3 = Integer.parseInt(stringTokenizer.nextToken());
                                --int3;
                                final Integer n5 = new Integer(stringTokenizer.nextToken());
                                final Integer n6 = new Integer(stringTokenizer.nextToken());
                                final LegacyMember obj3 = new LegacyMember();
                                obj3.j1 = n5 - 1;
                                obj3.j2 = n6 - 1;
                                obj3.area = new Float(stringTokenizer.nextToken());
                                if (this.materialData.size() > 1) {
                                    obj3.elasticity = this.materialData.get(
                                            Integer.parseInt(stringTokenizer.nextToken()) - 1);
                                } else {
                                    obj3.elasticity = this.materialData.get(0);
                                }
                                convolutedInsert(members, obj3, int3);
                                continue;
                            }
                            case 4: {
                                int int4 = Integer.parseInt(stringTokenizer.nextToken());
                                --int4;
                                final MyPoint obj4 =
                                        new MyPoint(Integer.parseInt(stringTokenizer.nextToken()),
                                                    Integer.parseInt(stringTokenizer.nextToken()));

                                convolutedInsert(restraintData, obj4, int4);
                                this.joints.get(int4).restraint = true;
                                continue;
                            }
                            case 5: {
                                int int5 = Integer.parseInt(stringTokenizer.nextToken());
                                --int5;
                                final Float n7 = new Float(stringTokenizer.nextToken());
                                final Float n8 = new Float(stringTokenizer.nextToken());
                                this.joints.get(int5).wx = n7;
                                this.joints.get(int5).wy = n8;
                                this.joints.get(int5).load = true;
                            }
                        }
                    }
                }
            }
        }
        catch (NumberFormatException ex) {
            this.dataOutput.println("\nDATA ERROR: Wrong input in line: " + n2 + "! ");
            return false;
        }
        catch (NoSuchElementException ex2) {
            this.dataOutput.println("\nDATA ERROR: Missing input in line: " + n2 + "! ");
            return false;
        }
        if (n != 5) {
            this.dataOutput.println("\nDATA ERROR: Missing input data at end of file!\n");
            return false;
        }
        new MyCoord(0.0, 0.0);
        final MyPoint obj5 = new MyPoint(0, 0);
        for (int i = 0; i < this.restraintData.size(); ++i) {
            if (this.restraintData.get(i) == null) {
                this.restraintData.set(i, obj5);
            }
        }
        this.supportRestraintCount = 0;
        for (int j = 0; j < this.restraintData.size(); ++j) {
            if (this.restraintData.get(j) != null) {
                this.supportRestraintCount += restraintData.get(j).x + restraintData.get(j).y;
                joints.get(j).jrx = restraintData.get(j).x;
                joints.get(j).jry = restraintData.get(j).y;
            }
        }
        return true;
    }
    
    private boolean checkData() {
        if (joints.size() < 2) {
            this.dataOutput.println("\nDATA ERROR: Need at least 2 Joints.\n\n");
            return false;
        }
        if (members.size() == 0) {
            this.dataOutput.println("\nDATA ERROR: No Members.\n\n");
            return false;
        }
        if (members.size() + this.supportRestraintCount < 2 * joints.size()) {
            this.dataOutput.println("\nDATA ERROR: Unstable truss.\n\n");
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void calculateBWAndDirCos() {
        for (int i = 0; i < members.size(); ++i) {
            final int j1 = members.get(i).j1;
            final int j2 = members.get(i).j2;
            final int maxb = 2 * (Math.abs(j2 - j1) + 1);
            if (maxb > this.maxb) {
                this.maxb = maxb;
            }
            final double x = joints.get(j1).x;
            final double y = joints.get(j1).y;
            final double x2 = joints.get(j2).x;
            final double y2 = joints.get(j2).y;
            final double n = x2 - x;
            final double n2 = y2 - y;
            final double sqrt = Math.sqrt(n * n + n2 * n2);
            members.get(i).cosx = n / sqrt;
            members.get(i).cosy = n2 / sqrt;
            members.get(i).length = sqrt;
        }
    }
    
    @SuppressWarnings({"MismatchedReadAndWriteOfArray", "ForLoopReplaceableByForEach"})
    private void calculateUnrestrainedStiffnessMx() {
        this.xkMatrix = new double[2 * joints.size()][this.maxb];
        final double[][] array = new double[4][4];
        for (int i = 0; i < 2 * joints.size(); ++i) {
            for (int j = 0; j < this.maxb; ++j) {
                this.xkMatrix[i][j] = 0.0;
            }
        }
        for (int k = 0; k < members.size(); ++k) {
            final LegacyMember member = members.get(k);
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
                        final double[] array2 = this.xkMatrix[n8 - 1];
                        final int n10 = n9 - 1;
                        array2[n10] += array[n6 - 1][n7 - 1];
                    }
                }
            }
            for (int n11 = 1; n11 <= 2; ++n11) {
                for (int n12 = 3; n12 <= 4; ++n12) {
                    final int n13 = 2 * (member.j1 + 1) - (2 - n11);
                    final int n14 = 2 * (member.j2 + 1) - (4 - n12) - n13 + 1;
                    if (n14 > 0) {
                        final double[] array3 = this.xkMatrix[n13 - 1];
                        final int n15 = n14 - 1;
                        array3[n15] += array[n11 - 1][n12 - 1];
                    }
                }
            }
            for (int n16 = 3; n16 <= 4; ++n16) {
                for (int n17 = 1; n17 <= 2; ++n17) {
                    final int n18 = 2 * (member.j2 + 1) - (4 - n16);
                    final int n19 = 2 * (member.j1 + 1) - (2 - n17) - n18 + 1;
                    if (n19 > 0) {
                        final double[] array4 = this.xkMatrix[n18 - 1];
                        final int n20 = n19 - 1;
                        array4[n20] += array[n16 - 1][n17 - 1];
                    }
                }
            }
            for (int n21 = 3; n21 <= 4; ++n21) {
                for (int n22 = 3; n22 <= 4; ++n22) {
                    final int n23 = 2 * (member.j2 + 1) - (4 - n21);
                    final int n24 = 2 * (member.j2 + 1) - (4 - n22) - n23 + 1;
                    if (n24 > 0) {
                        final double[] array5 = this.xkMatrix[n23 - 1];
                        final int n25 = n24 - 1;
                        array5[n25] += array[n21 - 1][n22 - 1];
                    }
                }
            }
        }
    }
    
    private void calculateSupportRestraintsAndFormLoad() {
        final double[] array = new double[2];
        this.wVector = new double[2 * joints.size()];
        for (int i = 1; i <= joints.size(); ++i) {
            final LegacyJoint joint = joints.get(i - 1);
            array[0] = joint.jrx;
            array[1] = joint.jry;
            this.wVector[2 * i - 2] = joint.wx;
            this.wVector[2 * i - 1] = joint.wy;
            for (int j = 1; j <= 2; ++j) {
                if (array[j - 1] != 0.0) {
                    final int supportRestraintCount = 2 * i - (2 - j);
                    this.supportRestraintCount = supportRestraintCount;
                    for (int k = 2; k <= this.maxb; ++k) {
                        this.xkMatrix[supportRestraintCount - 1][k - 1] = 0.0;
                        --this.supportRestraintCount;
                        if (this.supportRestraintCount > 0) {
                            this.xkMatrix[this.supportRestraintCount - 1][k - 1] = 0.0;
                        }
                    }
                    this.xkMatrix[supportRestraintCount - 1][0] = 1.0;
                    this.wVector[supportRestraintCount - 1] = 0.0;
                }
            }
        }
    }
    
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private void calculateSubstitution() {
        for (int i = 1; i <= 2 * joints.size(); ++i) {
            int n = i;
            if (Math.abs(this.xkMatrix[i - 1][0]) < 1.0E-5) {
                this.dataOutput.println("Unstable!");
                return;
            }
            for (int j = 2; j <= this.maxb; ++j) {
                ++n;
                if (this.xkMatrix[i - 1][j - 1] != 0.0) {
                    final double n2 = this.xkMatrix[i - 1][j - 1] / this.xkMatrix[i - 1][0];
                    int n3 = 0;
                    for (int k = j; k <= this.maxb; ++k) {
                        ++n3;
                        if (this.xkMatrix[i - 1][k - 1] != 0.0) {
                            final double[] array = this.xkMatrix[n - 1];
                            final int n4 = n3 - 1;
                            array[n4] -= n2 * this.xkMatrix[i - 1][k - 1];
                        }
                    }
                    this.xkMatrix[i - 1][j - 1] = n2;
                    final double[] wVector = this.wVector;
                    final int n5 = n - 1;
                    wVector[n5] -= n2 * this.wVector[i - 1];
                }
            }
            final double[] wVector2 = this.wVector;
            final int n6 = i - 1;
            wVector2[n6] /= this.xkMatrix[i - 1][0];
        }
        int n7 = 2 * joints.size();
        while (--n7 > 0) {
            int n8 = n7;
            for (int l = 2; l <= this.maxb; ++l) {
                ++n8;
                if (this.xkMatrix[n7 - 1][l - 1] != 0.0) {
                    final double[] wVector3 = this.wVector;
                    final int n9 = n7 - 1;
                    wVector3[n9] -= this.xkMatrix[n7 - 1][l - 1] * this.wVector[n8 - 1];
                }
            }
        }
    }
    
    @SuppressWarnings({"MismatchedReadAndWriteOfArray", "UnnecessaryLocalVariable"})
    private void calculate() throws IOException {
        final double[] array = new double[members.size()];
        for (int i = 1; i <= members.size(); ++i) {
            final LegacyMember member = members.get(i - 1);
            array[i - 1] = member.area * member.elasticity /
                    member.length * (
                    member.cosx * (this.wVector[2 * (member.j2 + 1) - 2]
                    - this.wVector[2 * (member.j1 + 1) - 2])
                    + member.cosy * (this.wVector[2 * (member.j2 + 1) - 1]
                    - this.wVector[2 * (member.j1 + 1) - 1]));
        }
        final double[] array2 = new double[joints.size()];
        final double[] array3 = new double[joints.size()];
        final double[] array4 = new double[joints.size()];
        final double[] array5 = new double[joints.size()];
        final NumberFormatter numberFormatter = new NumberFormatter(10, 3);
        final NumberFormatter numberFormatter2 = new NumberFormatter(10, 6);
        for (int j = 0; j < joints.size(); ++j) {
            array4[j] = joints.get(j).jrx;
            array5[j] = joints.get(j).jry;
            if (array4[j] != 0.0) {
                array2[j] = -joints.get(j).wx;
            }
            if (array5[j] != 0.0) {
                array3[j] = -joints.get(j).wy;
            }
        }
        for (int k = 0; k < members.size(); ++k) {
            final int j2 = members.get(k).j1;
            final int j3 = members.get(k).j2;
            if (array4[j2] != 0.0) {
                final double[] array6 = array2;
                final int n = j2;
                array6[n] -= members.get(k).cosx * array[k];
            }
            if (array5[j2] != 0.0) {
                final double[] array7 = array3;
                final int n2 = j2;
                array7[n2] -= members.get(k).cosy * array[k];
            }
            if (array4[j3] != 0.0) {
                final double[] array8 = array2;
                final int n3 = j3;
                array8[n3] += members.get(k).cosx * array[k];
            }
            if (array5[j3] != 0.0) {
                final double[] array9 = array3;
                final int n4 = j3;
                array9[n4] += members.get(k).cosy * array[k];
            }
        }
        this.dataOutput.println("Joint Displacements:");
        for (int l = 1; l <= joints.size(); ++l) {
            this.dataOutput.println("\t" + l + "\t" +
                                            numberFormatter2.round(this.wVector[2 * l - 2]) + "\t" +
                                            numberFormatter2.round(this.wVector[2 * l - 1]));
        }
        this.dataOutput.println("\nMember Forces:");
        for (int n5 = 0; n5 < members.size(); ++n5) {
            this.dataOutput.println("\t" + (n5 + 1) + "\t" + numberFormatter.round(array[n5]));
        }
        this.dataOutput.println("\nReactions:");
        for (int n6 = 0; n6 < joints.size(); ++n6) {
            if (Math.abs(array2[n6]) < 1.0E-10) {
                array2[n6] = 0.0;
            }
            if (Math.abs(array3[n6]) < 1.0E-10) {
                array3[n6] = 0.0;
            }
            if (array2[n6] != 0.0 || array3[n6] != 0.0) {
                this.dataOutput.println("\t" + (n6 + 1) + "\t" +
                                                numberFormatter.round(array2[n6]) + "\t" +
                                                numberFormatter.round(array3[n6]));
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

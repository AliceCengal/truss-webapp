package edu.vanderbilt.truss.legacy;

import edu.vanderbilt.truss.InputStruct;
import edu.vanderbilt.truss.Joint;
import edu.vanderbilt.truss.Member;

import java.util.LinkedList;
import java.util.List;

/**
 * Data structure to be fed to the legacy engine.
 *
 * Created by athran on 4/30/14.
 */
public class LegacyInputStruct {

    public List<Float> materialData = new LinkedList<Float>();
    public List<MyPoint> restraintData = new LinkedList<MyPoint>();
    public int supportRestraintCount = 0;
    public int maxb = 0;
    public double[][] xkMatrix;
    public double[] wVector;

    public List<LegacyJoint> joints = new LinkedList<LegacyJoint>();
    public List<LegacyMember> members = new LinkedList<LegacyMember>();

    public static LegacyInputStruct createFromNewStruct(InputStruct struct) {
        LegacyInputStruct old = new LegacyInputStruct();

        old.materialData.add(30000f);

        // Assume that the Joints are ordered by their ids
        for (Joint joint : struct.joints()) {
            LegacyJoint legJoint = new LegacyJoint();
            legJoint.x = joint.xCoor();
            legJoint.y = joint.yCoor();

            if (joint.isRestraintX() || joint.isRestraintY()) {
                legJoint.restraint = true;
                convolutedInsert(old.restraintData,
                                 new MyPoint((joint.isRestraintX()? 1 : 0),
                                             (joint.isRestraintY()? 1 : 0)),
                                 joint.id() - 1);
                legJoint.jrx = (joint.isRestraintX()? 1 : 0);
                legJoint.jry = (joint.isRestraintY()? 1 : 0);
            } else {
                legJoint.restraint = false;
            }

            if (joint.loadX() != 0.0 || joint.loadY() != 0.0) {
                legJoint.load = true;
                legJoint.wx = joint.loadX();
                legJoint.wy = joint.loadY();
            } else {
                legJoint.load = false;
            }

            convolutedInsert(old.joints,
                             legJoint,
                             joint.id() - 1);
        }

        // Assume that the members are ordered by their ids
        for (Member member : struct.members()) {
            LegacyMember legMember = new LegacyMember();
            legMember.elasticity = member.elasticity();
            legMember.area = member.area();
            legMember.j1 = member.jointLeft() - 1;
            legMember.j2 = member.jointRight() - 1;
            convolutedInsert(old.members,
                             legMember,
                             member.id() - 1);
        }

        for (int ind = 0; ind < old.restraintData.size(); ++ind) {
            if (old.restraintData.get(ind) == null) {
                old.restraintData.set(ind, new MyPoint(0,0));
            }
        }

        for (LegacyJoint legJoint : old.joints) {
            old.supportRestraintCount += legJoint.jrx + legJoint.jry;
        }

        return old;
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

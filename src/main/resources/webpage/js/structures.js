'use strict';

var Joint = (function() {
    function Joint(id, x, y) {
        this.id = id;
        this.x = x;
        this.y = y;

        this.isRestraintX = false;
        this.isRestraintY = false;
        this.loadX = 0.0;
        this.loadY = 0.0;

        this.positionString = "";
        this.loadString = "";
        this.restraintString = "";
    }

    Joint.prototype.compareId = function(other) {
        if (this.id > other.id) { return -1; }
        else if (this.id < other.id) { return 1; }
        else return 0;
    };

    Joint.prototype.copy = function(other) {
        this.id = other.id;
        this.x = other.x;
        this.y = other.y;
        this.isRestraintX = other.isRestraintX;
        this.isRestraintY = other.isRestraintY;
        this.loadX = other.loadX;
        this.loadY = other.loadY;
    };

    return Joint;
})();

var Member = (function() {
    function Member(id, left, right, area) {
        this.id = id;
        this.jointLeft = left;
        this.jointRight = right;
        this.area = area;
        this.elasticity = 0.0;
    }

    Member.prototype.copy = function(other) {
        this.id = other.id;
        this.jointLeft = other.jointLeft;
        this.jointRight = other.jointRight;
        this.area = other.area;
        this.elasticity = other.elasticity;
    }

    return Member;
})();

var BeamType = (function() {
    function BeamType(id, area, elasticity) {
        this.id = id;
        this.area = area;
        this.elasticity = elasticity;
    }
    return BeamType;
})();

var InputSet = (function() {
    function InputSet() {
        this.studentId = "";
        this.inputSetId = "";
        this.jointSet = [];
        this.memberSet = [];
        this.beamSet = [];
    }

    InputSet.prototype.copyFrom = function(other) {
        this.studentId = other.studentId;
        this.inputSetId = other.inputSetId;

        for (var oj in other.jointSet) {
            var nj = new Joint(0,0,0);
            nj.copy(other.jointSet[oj]);
            this.jointSet.push(nj);
        }

        for (var om in other.memberSet) {
            var nm = new Member(0, 0, 0, 0);
            nm.copy(other.memberSet[om]);
            this.memberSet.push(nm);
        }

        for (var m in this.memberSet) {
            var catalogued = false;
            for (var bs in this.beamSet) {
                if (this.memberSet[m].area == this.beamSet[bs].area &&
                    this.memberSet[m].elasticity == this.beamSet[bs].elasticity) {
                    this.memberSet[m].beamId = this.beamSet[bs].id;
                    catalogued = true;
                }
            }
            if (!catalogued) {
                this.memberSet[m].beamId = this.beamSet.length+1;
                this.beamSet.push(new BeamType(this.beamSet.length+1,
                                               this.memberSet[m].area,
                                               this.memberSet[m].elasticity))
            }
        }

        console.log(this);
    }

    InputSet.prototype.listBeamTypes = function() {
        return this.beamSet;
    }

    InputSet.prototype.beamTypeIdForMember = function(member) {
        return member.beamId;
    }

    var invalid = function(name) { return name === undefined || name === ""; };

    InputSet.prototype.properName = function() {
        if (invalid(this.studentId) && invalid(this.inputSetId)) {
            return "Untitled";
        } else if (invalid(this.studentId)) {
            return this.inputSetId;
        } else if (invalid(this.inputSetId)) {
            return this.studentId + ": Untitled";
        } else {
            return this.studentId + ": " + this.inputSetId;
        }
    }

    return InputSet;
})();
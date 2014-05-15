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

    var trimsStr = function(s) { return s.trim(); };

    Joint.prototype.readFromEditingStrings = function() {
        var posStrs = this.positionString.split(",").map(trimStr);
        if (posStrs.length === 2) {
            this.x = parseFloat(posStrs[0]);
            this.y = parseFloat(posStrs[1]);
        }

        var loadStrs = this.loadString.split(",").map(trimStr);
        if (loadStrs.length === 2) {
            this.loadX = parseFloat(loadStrs[0]);
            this.loadY = parseFloat(loadStrs[1]);
        }

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

    InputSet.prototype.catalogueMember = function(member) {
        var catalogued = false;
        for (var bs in this.beamSet) {
            if (member.area == this.beamSet[bs].area &&
                member.elasticity == this.beamSet[bs].elasticity) {
                member.beamId = this.beamSet[bs].id;
                catalogued = true;
            }
        }
        if (!catalogued) {
            member.beamId = this.beamSet.length+1;
            this.beamSet.push(new BeamType(this.beamSet.length+1,
                                           member.area,
                                           member.elasticity));
        }
    }

    InputSet.prototype.catalogueBeams = function() {
        for (var m in this.memberSet) {
            this.catalogueMember(this.memberSet[m]);
        }
    };

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

        this.catalogueBeams()
        console.log(this);
    }

    InputSet.prototype.addMember = function(member) {
        this.memberSet.push(member);
        this.catalogueMember(member);
    }

    InputSet.prototype.addJoint = function(joint) {
        this.jointSet.push(joint);
    }

    InputSet.prototype.listBeamTypes = function() {
        return this.beamSet;
    }

    InputSet.prototype.beamTypeIdForMember = function(member) {
        return member.beamId;
    }

    InputSet.prototype.addBeamSpec = function(bs) {
        this.beamSet.push(bs);
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

var TrussDiagram = (function() {
    function TrussDiagram(inputSet) {
        this.inputSet = inputSet;
        d3.select(".truss-diagram").selectAll("circle")
            .data(this.inputSet.jointSet)
            .enter().append("circle")
            .attr("cx", jointX)
            .attr("cy", jointY)
            .attr("r", 10);
    }

    var margin = 50;
    var jointX = function(j, i) { return j.x + margin };
    var jointY = function(j, i) { return j.y + margin };

    TrussDiagram.prototype.update = function() {
        d3.selectAll("circle")
            .attr("cx", jointX)
            .attr("cy", jointY)
            .attr("r", 10)
            .style("fill", "steelblue");
        console.log("Diagram Updated");
        console.log(this.inputSet);
    }

    return TrussDiagram;
})();

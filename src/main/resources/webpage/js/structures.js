'use strict';

var Joint = (function () {
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

    Joint.prototype.compareId = function (other) {
        if (this.id > other.id) { return -1; }
        if (this.id < other.id) { return 1; }
        return 0;
    };

    Joint.prototype.copy = function (other) {
        this.id = other.id;
        this.x = other.x;
        this.y = other.y;
        this.isRestraintX = other.isRestraintX;
        this.isRestraintY = other.isRestraintY;
        this.loadX = other.loadX;
        this.loadY = other.loadY;

    };

    Joint.prototype.trimStr = function (s) { return s.trim(); };

    Joint.prototype.readFromEditingStrings = function () {
        var posStrs = this.positionString.split(",").map(this.trimStr);
        if (posStrs.length === 2) {
            this.x = parseFloat(posStrs[0]);
            this.y = parseFloat(posStrs[1]);
        }

        var loadStrs = this.loadString.split(",").map(this.trimStr);
        if (loadStrs.length === 2) {
            this.loadX = parseFloat(loadStrs[0]);
            this.loadY = parseFloat(loadStrs[1]);
        }

    };

    return Joint;
})();

var Member = (function () {
    function Member(id, left, right, area) {
        this.id = id;
        this.jointLeft = left;
        this.jointRight = right;
        this.area = area;
        this.elasticity = 0.0;
    }

    Member.prototype.copy = function (other) {
        this.id = other.id;
        this.jointLeft = other.jointLeft;
        this.jointRight = other.jointRight;
        this.area = other.area;
        this.elasticity = other.elasticity;
    };

    return Member;
})();

var BeamType = (function () {
    function BeamType(id, area, elasticity) {
        this.id = id;
        this.area = area;
        this.elasticity = elasticity;
    }
    return BeamType;
})();

var InputSet = (function () {
    function InputSet() {
        this.studentId = "";
        this.inputSetId = "";
        this.jointSet = [];
        this.memberSet = [];
        this.beamSet = [];
    }

    InputSet.prototype.catalogueMember = function (member) {
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

    InputSet.prototype.catalogueBeams = function () {
        for (var m in this.memberSet) {
            this.catalogueMember(this.memberSet[m]);
        }
    };

    InputSet.prototype.copyFrom = function (other) {
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

    InputSet.prototype.addMember = function (member) {
        this.memberSet.push(member);
        this.catalogueMember(member);
    }

    InputSet.prototype.addJoint = function (joint) {
        this.jointSet.push(joint);
    }

    InputSet.prototype.listBeamTypes = function () {
        return this.beamSet;
    }

    InputSet.prototype.beamTypeIdForMember = function (member) {
        return member.beamId;
    }

    InputSet.prototype.addBeamSpec = function (bs) {
        this.beamSet.push(bs);
    }

    InputSet.prototype.jointForId = function (id) {
        for (var j in this.jointSet) {
            if (this.jointSet[j].id === id) {
                return this.jointSet[j];
            }
        }
        return null;
    }

    var invalid = function (name) { return name === undefined || name === ""; };

    InputSet.prototype.properName = function () {
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

var TrussDiagram = (function () {
    function TrussDiagram(inputSet) {
        this.inputSet = inputSet;
        var svg = d3.select(".truss-diagram");
        svg.selectAll("*").remove();
        svg.selectAll("circle")
            .data(this.inputSet.jointSet)
            .enter().append("circle")
            .attr("class", "joint-diagram")
            .attr("cx", this.jointX)
            .attr("cy", this.jointY)
            .attr("r", 10);

        svg.selectAll("line")
            .data(this.inputSet.memberSet)
            .enter().append("line")
            .attr("class", "member-diagram")
            .attr("x1", this.memberX1())
            .attr("y1", this.memberY1())
            .attr("x2", this.memberX2())
            .attr("y2", this.memberY2())
            .attr("stroke", "green")
            .attr("stroke-width", "2");
    }

    var margin = 50;

    TrussDiagram.prototype.jointX = function () {
        var _this = this;
        return function(j, i) {
            return (j.x * _this.scaleX()) + margin;
        };
    };

    TrussDiagram.prototype.jointY = function (j, i) {
        var _this = this;
        return function(j, i) {
            return (j.y * _this.scaleY()) + margin;
        };
    };

    TrussDiagram.prototype.scaleX = function () { return 2; };
    TrussDiagram.prototype.scaleY = function () { return 2; };

    TrussDiagram.prototype.memberX1 = function () {
        var _this = this;
        return function (m, i) {
            //console.log(m);
            return _this.inputSet.jointForId(m.jointLeft).x + margin;
        };
    }

    TrussDiagram.prototype.memberX2 = function () {
        var _this = this;
        return function (m, i) {
            return _this.inputSet.jointForId(m.jointRight).x + margin;
        }
    }

    TrussDiagram.prototype.memberY1 = function () {
        var _this = this;
        return function (m, i) {
            return _this.inputSet.jointForId(m.jointLeft).y + margin;
        }
    }

    TrussDiagram.prototype.memberY2 = function () {
        var _this = this;
        return function (m, i) {
            return _this.inputSet.jointForId(m.jointRight).y + margin;
        }
    }

    TrussDiagram.prototype.update = function () {
        d3.selectAll("circle")
            .attr("cx", this.jointX)
            .attr("cy", this.jointY)
            .attr("r", 10);
        //console.log("Diagram Updated");
        //console.log(this.inputSet);

        d3.selectAll("line")
            .attr("x1", this.memberX1())
            .attr("y1", this.memberY1())
            .attr("x2", this.memberX2())
            .attr("y2", this.memberY2())


    }

    return TrussDiagram;
})();

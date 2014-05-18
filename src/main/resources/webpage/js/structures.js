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
    }

    // Compare Id with another Joint. Zero if equal,
    // +1 if in ascending order from left to right,
    // negative otherwise.
    //
    // Joint -> Boolean
    Joint.prototype.compareId = function (other) {
        if (this.id > other.id) { return -1; }
        if (this.id < other.id) { return 1; }
        return 0;
    };

    // Copies the data from another Joint object, according
    // to the specs outlined in the server API.
    //
    // Joint -> Unit
    Joint.prototype.copy = function (other) {
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

var Member = (function () {
    function Member(id, left, right, area) {
        this.id = id;
        this.jointLeft = left;
        this.jointRight = right;
        this.area = area;
        this.elasticity = 0.0;
    }

    // Copy the data from another Member, according to
    // the specs outlined in the server API.
    //
    // Member -> Unit
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

    // Initializes this InputSet with a Member and a Joint with
    // dummy values.
    //
    // Unit -> InputSet
    InputSet.prototype.populate = function() {
        this.addJoint(new Joint(1, 0, 0));
        this.addMember(new Member(1, 1, 1, 0.0));
        return this;
    }

    // Reads the crosssection area and elasticity values from
    // the Member and adds it into the beamSet if it is a new
    // value. Also tags the Member with an Id.
    //
    // Member -> Unit
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

    // Catalogue all the Members in memberSet for their area and
    // elasticity values.
    //
    // Unit -> Unit
    InputSet.prototype.catalogueBeams = function () {
        for (var m in this.memberSet) {
            this.catalogueMember(this.memberSet[m]);
        }
    };

    // Copy data from another InputSet, according to the specs
    // outlined in the server API. Also populates the other fields
    // in this class according to the data read in.
    //
    // InputSet -> Unit
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

    // Adds a member into the memberSet. Also updates the beamSet.
    //
    // Member -> Unit
    InputSet.prototype.addMember = function (member) {
        this.memberSet.push(member);
        this.catalogueMember(member);
    }

    // Adds a Joint into the jointSet.
    //
    // Joint -> Unit
    InputSet.prototype.addJoint = function (joint) {
        this.jointSet.push(joint);
    }

    // Returns an array of BeamSets that correspond to the properties
    // of all the Members in memberSet. There are no duplications.
    //
    // Unit -> [BeamSet]
    InputSet.prototype.listBeamTypes = function () {
        return this.beamSet;
    }

    // Return a beam Id that corresponds to the Member.
    //
    // Member -> Int
    InputSet.prototype.beamTypeIdForMember = function (member) {
        return member.beamId;
    }

    // Add a BeamSpec to beamSet.
    //
    // BeamSpec -> Unit
    InputSet.prototype.addBeamSpec = function (bs) {
        this.beamSet.push(bs);
    }

    // Return a Joint in the jointSet that has this Id.
    // Return null if no such Joint exist.
    //
    // Int -> Joint
    InputSet.prototype.jointForId = function (id) {
        for (var j in this.jointSet) {
            if (this.jointSet[j].id === id) {
                return this.jointSet[j];
            }
        }
        return null;
    }

    var invalid = function (name) { return name === undefined || name === ""; };

    // Return a name string derived from strudentId and inputSetId.
    //
    // Unit -> String
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

// http://stackoverflow.com/a/3895521
var range = function(start, end, step) {
    var range = [];
    var typeofStart = typeof start;
    var typeofEnd = typeof end;

    if (step === 0) {
        //throw TypeError("Step cannot be zero.");
        return range;
    }

    if (typeofStart == "undefined" || typeofEnd == "undefined") {
        throw TypeError("Must pass start and end arguments.");
    } else if (typeofStart != typeofEnd) {
        throw TypeError("Start and end arguments must be of same type.");
    }

    typeof step == "undefined" && (step = 1);

    if (end < start) {
        step = -step;
    }

    if (typeofStart == "number") {

        while (step > 0 ? end >= start : end <= start) {
            range.push(start);
            start += step;
        }

    } else if (typeofStart == "string") {

        if (start.length != 1 || end.length != 1) {
            throw TypeError("Only strings with one character are supported.");
        }

        start = start.charCodeAt(0);
        end = end.charCodeAt(0);

        while (step > 0 ? end >= start : end <= start) {
            range.push(String.fromCharCode(start));
            start += step;
        }

    } else {
        throw TypeError("Only string and number types are supported");
    }

    return range;

}
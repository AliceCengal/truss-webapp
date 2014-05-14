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
    }
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

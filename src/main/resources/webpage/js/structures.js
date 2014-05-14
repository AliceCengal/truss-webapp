
var Joint = (function() {
    function Joint(id, x, y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    return Joint;
})();

var Member = (function() {
    function Member(id, left, right, beamType) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.beamType = beamType;
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

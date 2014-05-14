
var trussApp = angular.module('TrussApp', []);

trussApp.controller('JointTableCtrl', function($scope) {
    $scope.joints = [
        new Joint(1, 0, 0),
        new Joint(2, 10, 10),
        new Joint(3, 0, 20)
    ];
});

trussApp.controller('MemberTableCtrl', function($scope) {
    $scope.members = [
        new Member(1, 1, 2, 1),
        new Member(2, 2, 3, 1),
        new Member(3, 1, 3, 1)
    ];
});

trussApp.controller('BeamTableCtrl', function($scope) {
    $scope.beamTypes = [
        new BeamType(1, 1.96, 30000),
        new BeamType(2, 0.88, 30000)
    ];
});




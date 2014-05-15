'use strict';

var trussApp = angular.module('TrussApp', ['TrussServices']);

trussApp.controller('InputPaneCtrl', function($scope, $http) {
    $scope.inputSet = new InputSet();

    $scope.clickSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            $scope.inputSet.copyFrom(data);
        });
    };

});

trussApp.controller('JointTableCtrl', function($scope, Input) {
    $scope.joints = Input.jointSet;
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

trussApp.controller('SampleBtnCtrl', function($scope, Input) {
    $scope.clickSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            Input.copyFrom(data);
        });
    };
});
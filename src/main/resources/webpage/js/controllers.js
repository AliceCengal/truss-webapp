'use strict';

var trussApp = angular.module('TrussApp', ['TrussServices']);

trussApp.controller('InputPaneCtrl', function($scope, $http) {

    $scope.inputSet = new InputSet();
    $scope.inputSet.addJoint(new Joint(1, 0, 0));
    $scope.inputSet.addMember(new Member(1, 1, 1, 0.0));

    $scope.diagram = new TrussDiagram($scope.inputSet);

    $scope.isEditingTitle = false;

    var compareId = function(a, b) {
        if (a.id < b.id) { return -1; }
        else if (a.id > b.id) { return 1; }
        else return 0;
    }

    $scope.clickSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            $scope.inputSet = new InputSet();
            $scope.inputSet.copyFrom(data);
            $scope.inputSet.jointSet.sort(compareId);
            $scope.inputSet.memberSet.sort(compareId);
            $scope.diagram = new TrussDiagram($scope.inputSet);
        });
    };

    $scope.clearInputPane = function() {
        $scope.inputSet = new InputSet();
        $scope.inputSet.addJoint(new Joint(1, 0, 0));
        $scope.inputSet.addMember(new Member(1, 1, 1, 0.0));
        $scope.diagram = new TrussDiagram($scope.inputSet);
    };

    $scope.clickTitle = function() {
        $scope.isEditingTitle = true;
    };

    $scope.titleInput = "";

    $scope.doneTitleEditing = function() {
        $scope.isEditingTitle = false;
        $scope.inputSet.inputSetId = $scope.titleInput;
        $scope.titleInput = "";
    };

    $scope.$watch(function() {
        $scope.diagram.update();
    });

    $scope.submitJoint = function() {
        //$scope.inputSet.jointSet.push($scope.editingJoint);
        console.log($scope.inputSet.jointSet);
        //$scope.editingJoint = new Joint($scope.inputSet.jointSet + 1, 0, 0);
    };

    $scope.addJoint = function() {
        $scope.inputSet.addJoint(new Joint($scope.inputSet.jointSet.length + 1, 0, 0));
        $scope.diagram = new TrussDiagram($scope.inputSet);
    }

    $scope.addMember = function() {
        var firstBeam = $scope.inputSet.listBeamTypes()[0];
        var m = new Member($scope.inputSet.memberSet.length + 1, 1, 1, firstBeam.area);
        m.elasticity = firstBeam.elasticity;
        $scope.inputSet.addMember(m);
    }

    $scope.addBeamSpec = function() {
        $scope.inputSet.addBeamSpec(new BeamType($scope.inputSet.listBeamTypes().length + 1, 0, 0));
    }

});

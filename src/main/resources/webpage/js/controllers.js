'use strict';

var trussApp = angular.module('TrussApp', ['TrussServices']);

trussApp.controller('InputPaneCtrl', function($scope, $http) {

    $scope.inputSet = new InputSet();
    $scope.inputSet.addJoint(new Joint(1, 0, 0));
    $scope.inputSet.addMember(new Member(1, 1, 1, 0.0));

    $scope.isEditingTitle = false;

    var compareId = function(a, b) {
        if (a.id < b.id) { return -1; }
        else if (a.id > b.id) { return 1; }
        else return 0;
    }

    /// BEGIN Navbar buttons

    $scope.clickSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            $scope.inputSet = new InputSet();
            $scope.inputSet.copyFrom(data);
            $scope.inputSet.jointSet.sort(compareId);
            $scope.inputSet.memberSet.sort(compareId);
        });
    };

    $scope.clearInputPane = function() {
        $scope.inputSet = new InputSet();
        $scope.inputSet.addJoint(new Joint(1, 0, 0));
        $scope.inputSet.addMember(new Member(1, 1, 1, 0.0));
    };

    /// END Navbar buttons

    /// BEGIN Editing the title

    $scope.clickTitle = function() {
        $scope.isEditingTitle = true;
    };

    $scope.titleInput = "";

    $scope.doneTitleEditing = function() {
        $scope.isEditingTitle = false;
        $scope.inputSet.inputSetId = $scope.titleInput;
        $scope.titleInput = "";
    };

    /// END Editing the title

    /// BEGIN Adding new rows to the tables

    $scope.addJoint = function() {
        $scope.inputSet.addJoint(new Joint($scope.inputSet.jointSet.length + 1, 0, 0));
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

    /// END Adding new rows to the tables

    /// BEGIN Diagram

    $scope.zoom = 1;

    $scope.jointCenter = function() {
        var len = $scope.inputSet.jointSet.length;
        return $scope.inputSet.jointSet
            .map(function(j) { return [j.x, j.y]; })
            .map(function(xy) { return [xy[0]/len, xy[1]/len]; })
            .reduce(function(a, b) { return [(a[0] + b[0]), (a[1] + b[1])]; });
    }

    $scope.diagramDimension = function() {
        var svg = d3.select(".truss-diagram")[0][0];
        return [svg.clientWidth, svg.clientHeight];
    }

    $scope.correctHorizontal = function(x) {
        return ($scope.diagramDimension()[0])/2 + (x - $scope.jointCenter()[0]) * $scope.zoom;
    }

    $scope.correctVertical = function(y) {
        return ($scope.diagramDimension()[1])/2 - (y - $scope.jointCenter()[1]) * $scope.zoom;
    }

    $scope.zoomPlus = function() {
        $scope.zoom = $scope.zoom * 1.2;
    }

    $scope.zoomMinus = function() {
        $scope.zoom = $scope.zoom * 0.8333333333;
    }

    /*$scope.$watch(function() {
        console.log($scope.diagramCenter());
    }) */

});

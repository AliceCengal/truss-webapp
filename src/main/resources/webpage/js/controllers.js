'use strict';

var trussApp = angular.module('TrussApp', ['TrussServices']);

trussApp.controller('InputPaneCtrl', function($scope, $http) {

    $scope.inputSet = new InputSet();

    $scope.isEditingTitle = false;

    var compareId = function(a, b) {
        if (a.id < b.id) { return -1; }
        else if (a.id > b.id) { return 1; }
        else return 0;
    }

    $scope.clickSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            $scope.inputSet.copyFrom(data);
            $scope.inputSet.jointSet.sort(compareId);
            $scope.inputSet.memberSet.sort(compareId);
            $scope.editingJoint.id = $scope.inputSet.jointSet.length + 1;
        });
    };

    $scope.clearInputPane = function() {
        $scope.inputSet = new InputSet();
        $scope.editingJoint.id = $scope.inputSet.jointSet.length + 1;
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
        //console.log("digest called");
    });

    $scope.editingJoint = new Joint(0, 0, 0);

    $scope.submitJoint = function() {
        $scope.inputSet.jointSet.push($scope.editingJoint);
        $scope.editingJoint = new Joint(0, 0, 0);
    };

});

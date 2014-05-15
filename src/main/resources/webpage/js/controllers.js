'use strict';

var trussApp = angular.module('TrussApp', ['TrussServices']);

trussApp.controller('InputPaneCtrl', function($scope, $http) {

    $scope.inputSet = new InputSet();

    $scope.isEditingTitle = false;

    $scope.clickSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            $scope.inputSet.copyFrom(data);
        });
    };

    $scope.clearInputPane = function() {
        $scope.inputSet = new InputSet();
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
});

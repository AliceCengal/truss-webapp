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

    $scope.clearInputPane = function() {
        $scope.inputSet = new InputSet();
    }

});

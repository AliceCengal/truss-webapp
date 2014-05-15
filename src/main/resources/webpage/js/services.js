'use strict';

var trussService = angular.module('TrussServices', []);

trussService.factory('Input', function($http) {
    var inputModel = new InputSet();
    return inputModel;
});

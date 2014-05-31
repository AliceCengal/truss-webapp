'use strict';

var trussApp = angular.module('TrussApp', [
        'ngRoute',
        'TrussControllers',
        'TrussServices']);

trussApp.config(function($routeProvider) {
    $routeProvider.
        when('/calcmobile', {
            templateUrl: 'partials/calc-mobile.html',
            controller: 'InputPaneCtrl'
        }).
        when('/calculator', {
            templateUrl: 'partials/calculator.html',
            controller: 'InputPaneCtrl'
        }).
        when('/datatables', {
            templateUrl: 'partials/datatables.html',
            controller: 'DatatableCtrl'
        }).
        when('/login', {
            templateUrl: 'partials/login.html',
            controller: 'LoginCtrl'
        }).
        otherwise({
            redirectTo: '/calculator'
        });
});

'use strict';

var trussService = angular.module('TrussServices', []);

trussService.factory('Cache', function($http) {

    var cache = {
        hasValue:false;
    };

    cache.push = function(val) {
        cache.val = val;
        cache.hasValue = true;
    };

    cache.get = function() { return cache.val; };

    cache.clear = function() { cache.hasValue = false; }

    return cache;
});


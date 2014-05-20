'use strict';

var trussControllers = angular.module('TrussControllers', []);

trussControllers.controller('InputPaneCtrl', function($scope, $http) {

    // The InputSet that is being edited.
    // Start off with a blank InputSet with some dummy values.
    //
    // InputSet
    $scope.inputSet = (new InputSet()).populate();

    var compareId = function(a, b) {
        if (a.id < b.id) { return -1; }
        else if (a.id > b.id) { return 1; }
        else return 0;
    }

    /// BEGIN Navbar buttons

    // Load a sample InputSet from the server.
    //
    // Unit -> Unit
    $scope.loadSample = function() {
        console.log("Sample Clicked");
        $http.get("api/sample/foo").success(function(data) {
            $scope.inputSet = new InputSet();
            $scope.inputSet.copyFrom(data);
            $scope.inputSet.jointSet.sort(compareId);
            $scope.inputSet.memberSet.sort(compareId);
        });
    };

    // Clears all info and initialize the page with a new
    // empty InputSet.
    //
    // Unit -> Unit
    $scope.clearInputSet = function() {
        $scope.inputSet = (new InputSet()).populate();
    };

    /// END Navbar buttons

    /// BEGIN Editing the title

    // Boolean
    $scope.isEditingTitle = false;

    // Unit -> Unit
    $scope.clickTitle = function() {
        $scope.isEditingTitle = true;
    };

    // The new title being edited. Initialized to empty.
    //
    // String
    $scope.titleInput = "";

    // Grabs titleInput and set the title value from inputSet
    // to that. Then set isEditingTitle to false and reset
    // the editing mode.
    //
    // Unit -> Unit
    $scope.doneTitleEditing = function() {
        $scope.isEditingTitle = false;
        $scope.inputSet.inputSetId = $scope.titleInput;
        $scope.titleInput = "";
    };

    /// END Editing the title

    /// BEGIN Adding new rows to the tables

    // Add a new Joint to the inputSet, with its Id set
    // to one higher than the current highest.
    //
    // Unit -> Unit
    $scope.addJoint = function() {
        $scope.inputSet.addJoint(new Joint($scope.inputSet.jointSet.length + 1, 0, 0));
    }

    $scope.removeJoint = function() {
        $scope.inputSet.jointSet.pop();
    }

    // Add a new Memner to the inputSet, with its Id set
    // to ine higher than the current highest.
    //
    // Unit -> Unit
    $scope.addMember = function() {
        var firstBeam = $scope.inputSet.listBeamTypes()[0];
        var m = new Member($scope.inputSet.memberSet.length + 1, 1, 1, firstBeam.area);
        m.elasticity = firstBeam.elasticity;
        $scope.inputSet.addMember(m);
    }

    $scope.removeMember = function() {
        $scope.inputSet.memberSet.pop();
    }

    // Add a new BeamSpec to the inputSet, with its Id set
    // to one higher than the current highest.
    //
    // Unit -> Unit
    $scope.addBeamSpec = function() {
        $scope.inputSet.addBeamSpec(
                new BeamType($scope.inputSet.listBeamTypes().length + 1, 0, 0));
    }

    $scope.removeBeamSet = function() {
        $scope.inputSet.beamSet.pop();
    }

    /// END Adding new rows to the tables

    /// BEGIN Diagram

    // Scaling factor used to calculate the Joints' position on the diagram.
    $scope.zoom = 1;

    // Return a pair containing the coordinate of the topological center
    // of all the joints. No side-effect.
    //
    // Unit -> [Int, Int]
    $scope.jointCenter = function() {
        var len = $scope.inputSet.jointSet.length;
        // arithmetic mean
        return $scope.inputSet.jointSet
            .map(function(j) { return [j.x, j.y]; })
            .map(function(xy) { return [xy[0]/len, xy[1]/len]; })
            .reduce(function(a, b) { return [(a[0] + b[0]), (a[1] + b[1])]; });
    }

    // Return a pair containing the width and height of the SVG element.
    // No side effect.
    //
    // Unit -> [Int, Int]
    $scope.diagramDimension = function() {
        var svg = d3.select(".truss-diagram")[0][0];
        return [svg.clientWidth, svg.clientHeight];
    }

    // Do a transformation from the Joint coordinate system to the SVG
    // coordinate system. Takes in a Joint x and returns its horizontal
    // position on the SVG. No side effect.
    //
    // Int -> Int
    $scope.correctHorizontal = function(x) {
        return ($scope.diagramDimension()[0])/2 +
                (x - $scope.jointCenter()[0]) * $scope.zoom;
    }

    // Do a transformation from the Joint coordinate system to the SVG coordinate
    // system. Takes in a Joint y and returns its vertical position on the SVG.
    // No side effect.
    //
    // Int -> Int
    $scope.correctVertical = function(y) {
        return ($scope.diagramDimension()[1])/2 -
                (y - $scope.jointCenter()[1]) * $scope.zoom;
    }

    // Do the reverse transformation from `correctHorizontal()`. No side effect.
    //
    // Int -> Int
    $scope.decorrectHorizontal = function(X) {
        return $scope.jointCenter()[0] +
                (X - $scope.diagramDimension()[0]/2)/$scope.zoom
    }

    // Do the reverse transformation from `correctVertical()`. No side effect.
    //
    // Int -> Int
    $scope.decorrectVertical = function(Y) {
        return $scope.jointCenter()[1] -
                (Y - $scope.diagramDimension()[1]/2)/$scope.zoom
    }

    // How factor multiplied to or divided from the zoom everytime
    // `zoomPlus()` or `zoomMinus()` are called.
    //
    // Double
    $scope.zoomFactor = 1.2;

    // Unit -> Unit
    $scope.zoomPlus = function() {
        $scope.zoom = $scope.zoom * $scope.zoomFactor;
    }

    // Unit -> Unit
    $scope.zoomMinus = function() {
        $scope.zoom = $scope.zoom / $scope.zoomFactor;
    }

    // Unit -> Int
    $scope.longerDimension = function() {
        var dims = $scope.diagramDimension();
        return (dims[0] > dims[1])? dims[0] : dims[1];
    }

    // Unit -> Double
    $scope.gridInterval = function() {
        return $scope.longerDimension() / 9.5;
    }

    // Unit -> [Double]
    $scope.horizontalAxisSet = function() {
        var interval = $scope.gridInterval();
        var width = $scope.diagramDimension()[0];
        return range(width/2, 0, interval)
            .concat(range(width/2 + interval, width, interval));
    }

    // Unit -> [Double]
    $scope.verticalAxisSet = function() {
        var interval = $scope.gridInterval();
        var width = $scope.diagramDimension()[1];
        return range(width/2, 0, interval)
            .concat(range(width/2 + interval, width, interval));
    }

    /// END Diagram

    /// BEGIN Tab Navigation

    // Boolean
    $scope.isShowingDiagram = true;

    // Unit -> Unit
    $scope.showDiagram = function() {
        if (!$scope.isShowingDiagram) {
            $scope.isShowingDiagram = true;
        }
    }

    // Unit -> Unit
    $scope.showResult = function() {
        if ($scope.isShowingDiagram) {
            $scope.isShowingDiagram = false;
        }
        $scope.computeResult();
    }

    /// END Tab Navigation

    /// BEGIN Result Tab

    // Boolean
    $scope.isWaitingForResult = false;

    // ResultSet
    $scope.resultSet = {};

    // Base 10 logarithm
    //
    // Number -> Number
    $scope.log10 = function(x) { return Math.log(x)/Math.LN10; }

    // Rounds a number such that it would have a set
    // number of significant figures.
    //
    // Number -> Number
    $scope.sigFig = function(x) {
        if (x === 0.0) { return 0.0; }
        var ordinal = $scope.log10(Math.abs(x));
        if (-3 < ordinal && ordinal < 3) {
            return x.toPrecision(3);
        } else {
            return x.toExponential(3);
        }
    }

    // Takes the InputSet and do a server call to get a ResultSet.
    //
    // Unit -> Unit
    $scope.computeResult = function() {
        $scope.isWaitingForResult = true;
        $http.post("api/computation", JSON.stringify($scope.inputSet))
            .success(function(data) {
                $scope.resultSet = data;
                $scope.resultSet.jointResultSet.sort(compareId);
                $scope.resultSet.memberResultSet.sort(compareId);
                $scope.isWaitingForResult = false;
                console.log($scope.resultSet);
            });
    }

    /// END Result Tab

    /*$scope.$watch(function() {
        console.log($scope.diagramCenter());
    }) */

});

trussControllers.controller('DatatableCtrl', function($scope) {});

trussControllers.controller('LoginCtrl', function($scope) {});

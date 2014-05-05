
var sampleData = {
     "userId": "John Boyd",
     "inputSetId": "Problem1",
     "jointSet": [
             {
                 "id":           1,
                 "x":            0,
                 "y":            120,
                 "isRestraintX": false,
                 "isRestraintY": false,
                 "loadX":        0,
                 "loadY":        0
             },
             {
                 "id":           2,
                 "x":            144,
                 "y":            120,
                 "isRestraintX": false,
                 "isRestraintY": false,
                 "loadX":        -12,
                 "loadY":        0
             },
             {
                 "id":           3,
                 "x":            0,
                 "y":            60,
                 "isRestraintX": false,
                 "isRestraintY": false,
                 "loadX":        0,
                 "loadY":        0
             },
             {
                 "id":           4,
                 "x":            144,
                 "y":            60,
                 "isRestraintX": false,
                 "isRestraintY": false,
                 "loadX":        -18,
                 "loadY":        0
             },
             {
                 "id":           5,
                 "x":            0,
                 "y":            0,
                 "isRestraintX": false,
                 "isRestraintY": true,
                 "loadX":        0,
                 "loadY":        0
             },
             {
                 "id":           6,
                 "x":            144,
                 "y":            0,
                 "isRestraintX": true,
                 "isRestraintY": true,
                 "loadX":        0,
                 "loadY":        0
             }
     ],
     "memberSet": [
         {
             "id":         1,
             "jointLeft":  1,
             "jointRight": 2,
             "area":       0.96,
             "elasticity": 30000
         },
         {
             "id":         2,
             "jointLeft":  1,
             "jointRight": 3,
             "area":       0.96,
             "elasticity": 30000
         },
         {
             "id":         3,
             "jointLeft":  1,
             "jointRight": 4,
             "area":       1.43,
             "elasticity": 30000
         },
         {
             "id":         4,
             "jointLeft":  2,
             "jointRight": 4,
             "area":       0.96,
             "elasticity": 30000
         },
         {
             "id":         5,
             "jointLeft":  3,
             "jointRight": 4,
             "area":       1.43,
             "elasticity": 30000
         },
         {
             "id":         6,
             "jointLeft":  3,
             "jointRight": 5,
             "area":       0.96,
             "elasticity": 30000
         },
         {
             "id":         7,
             "jointLeft":  3,
             "jointRight": 6,
             "area":       1.888,
             "elasticity": 30000
         },
         {
             "id":         8,
             "jointLeft":  4,
             "jointRight": 6,
             "area":       0.96,
             "elasticity": 30000
         },
         {
             "id":         9,
             "jointLeft":  5,
             "jointRight": 6,
             "area":       0.96,
             "elasticity": 30000
         }
     ]
 };

var testComputation = function() {
    var url = "http://localhost:8080/api/computation"

    $.ajax({
        url: url,
        type: 'post',
        data: JSON.stringify(sampleData),
        contentType: 'application/json',
        dataType: 'json',
        success: function(json) {
            console.log(json);
        },
        error: function( xhr, status, errorThrown ) {
            console.log( "Error: " + errorThrown );
            console.log( "Status: " + status );
        }
    });

}



$(document).ready(function() {
    testComputation();
});



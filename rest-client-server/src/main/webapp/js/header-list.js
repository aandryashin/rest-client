angular.module('rest').directive('headerList', function () {

    return {
        restrict: 'E',
        templateUrl: 'header-list.html',
        scope: {
            headers : '=',
            edit: '='
        },
        controller: function($scope) {
           $scope.newHeader = {name: '', value: ''};
           $scope.addHeader = function() {
                if($scope.newHeader.name){
                    $scope.headers.push({name: $scope.newHeader.name, value: $scope.newHeader.value});
                    $scope.newHeader = {name: '', value: ''};
                }
           };

           $scope.removeHeader = function(index) {
               $scope.headers.splice(index, 1);
           };

        }
    };
});
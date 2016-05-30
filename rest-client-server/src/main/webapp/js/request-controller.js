angular.module('rest').controller('RequestController', function ($scope, $http, $routeParams) {

    var url = '/api/call/';
    if ($routeParams.id !== undefined) {
        url += $routeParams.id;
    }

    $http({method: 'GET', url: url}).success(function(data){
        $scope.call = data;
    });

    $scope.submit = function() {
        $scope.loading = true;
        $http({method: 'POST', url: '/api/request', data: $scope.call.request}).success(function(data){
            $scope.call = data;
        });
    }
});
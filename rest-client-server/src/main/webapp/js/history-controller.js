angular.module('rest').controller('HistoryController', function ($scope, $http) {

    $scope.itemsByPage=20;
    $scope.rowCollection = [];

    $http({method: 'GET', url: '/api/calls'}).success(function(calls){
        var message;
        for (var i = 0; i < calls.length; i++) {
            if (calls[i].response !== undefined && calls[i].response != null) {
                message = calls[i].response.status.code + ' ' + calls[i].response.status.message
            } else if (calls[i].error !== undefined && calls[i].error != null) {
                message = calls[i].error.message;
            }
            $scope.rowCollection.push({
                id: calls[i].id,
                date: calls[i].timestamp,
                method: calls[i].request.method,
                url: calls[i].request.url,
                status: message
            });
        }
    });

    // Workaround of this issue: https://github.com/lorenzofox3/Smart-Table/issues/156
    $scope.displayedCollection = [].concat($scope.rowCollection);

//    $scope.remove = function(index) {
//        console.log(index + ' # ' + $scope.displayedCollection[index].url);
//        $http({method: 'DELETE', url: '/api/call/' + $scope.displayedCollection[index].id}).success(function(){
//            $scope.displayedCollection.splice(index, 1);
//        });
//    }
});

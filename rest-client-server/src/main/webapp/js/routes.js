angular.module('rest').config(['$routeProvider', function($routeProvider) {

  $routeProvider
    .when('/', {
      redirectTo: 'request',
     })
    .when('/request', {
      templateUrl: 'request-index.html',
      controller: 'RequestController',
     })
    .when('/history', {
      templateUrl: 'history-index.html',
      controller: 'HistoryController',
    });

}]);
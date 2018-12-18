app.controller("cartController", function ($scope, cartService) {


    /*获取用户名*/
    $scope.getUsername=function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;

        });
    }
});
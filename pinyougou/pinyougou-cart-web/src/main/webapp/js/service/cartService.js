app.service("cartService", function ($http) {

    /*获取用户名*/
    this.getUsername=function () {
        return $http.get("../cart/getUsername.do?t=" + Math.random());
    }
});
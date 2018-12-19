//查询所有收货地址列表
app.service("addressService", function ($http) {

    this.findAddressList = function () {
        return $http.get("address/findAddressList.do");

    };
});
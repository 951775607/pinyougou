app.service("payService", function ($http) {

    //生成支付二维码
    this.createNative = function (outTradeNo) {
        return $http.get("pay/createNative.do?outTradeNo=" + outTradeNo + "&t=" + Math.random());

    };
    //查询支付状态
    this.queryPayStatus = function (outTradeNo) {
        return $http.get("pay/queryPayStatus.do?outTradeNo=" + outTradeNo + "&t=" + Math.random());

    };
});
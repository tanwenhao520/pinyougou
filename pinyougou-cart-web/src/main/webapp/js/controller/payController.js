pinyougou.controller("payController", function ($scope,$location, cartService,payService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };
    $scope.money = 0;
    $scope.createNative = function () {
        $scope.outTradeNo = $location.search()["outTradeNo"];
        payService.createNative($scope.outTradeNo).success(function (response) {
            if("SUCCESS" == response.result_code){
                $scope.money = (response.totalFee/100).toFixed(2);
                //生成支付地址的二维码
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:"Q",
                    value:response.code_url
                });
                //查询支付状态
                queryPayStatus($scope.outTradeNo);
            }else{
                alert("生成二维码失败!")
            }
        })
}

    queryPayStatus  = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if(response.success){
                    location.href = "paysuccess.html#?money="+$scope.money;
            }else{
                if("二维码超时"==response.message){
                    $scope.createNative();
                }else{
                    location.href = "payfail.html";
                }

            }
        })
    }

    $scope.getMoeny = function () {
       $scope.money =  $location.search()["money"];
    }
});
pinyougou.controller("orderInfoController",function ($scope,cartService,addressService) {
   
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        })
    }


    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            // 计算购买总数和总价格
            $scope.totalValue = cartService.sumTotalValue(response);
        });
    };
    //查询地址
    $scope.findAddressList = function () {
        addressService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for(var i=0 ; i < $scope.addressList.length;i++){
                var address = $scope.addressList[i];
                if(address.isDefault == "1"){
                    $scope.address = address;
                    break;
                }
            }
        })
    }
    // 判断地址是否选中的地址
    $scope.isAddressSelected = function (address) {
        if($scope.address == address){
            return true;
        }
        return false;
    }

    // 选中地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    //默认支付方式 1微信支付，2货到付款
    $scope.order = {"paymentType":"1"};
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        cartService.submitOrder($scope.order).success(function (response) {
            if(response.success){
                if($scope.order.paymentType=="1"){
                    // 携带支付业务 id ，跳转到支付页面
                    location.href = "pay.html#?outTradeNo=" + response.message;
                } else {
                    location.href = "paysuccess.html";
                }
            }else{
                alert(response.message);
            }
        })
    }
})
    

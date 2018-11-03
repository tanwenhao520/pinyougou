pinyougou.controller("baseController",function ($scope) {
    //查询所有，因为前期测试需要，后期不需要所以注解
 /*   $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response
        });
    }*/
    /*
    *AngularJS分页功能
    * */
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    }

    /*
    * Angular每次点击分页调用这个方法，用于调用真正的分页并查询方法
    * */
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    /*
    *前期测试分页的方法
    * */
     /*  $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }*/




    /**
     * 定义一个空数组，用来接收id，如果没点击则删除id，传递给批量删除方法
     * @type {Array}
     */
    $scope.selectIds = [];

    $scope.updateSelection = function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }

    $scope.jsonToString = function (jsonList,key) {
        var str = "";
        var jsonArray = JSON.parse(jsonList);
        for (var i = 0; i < jsonArray.length; i++) {
            var jsonObj = jsonArray[i];
            if (str.length > 0) {
                str += "," + jsonObj[key];
            } else {
                str = jsonObj[key];
            }
        }
        return str
    }
})

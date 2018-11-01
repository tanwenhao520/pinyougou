pinyougou.controller("brandController",function ($scope,$controller,brandService) {

    $controller("baseController", {$scope:$scope});
    /**
     *分页并查询，先把请求参数设为{} ,不然为后端接收的参数无法从json转换为对象
     * @type {{}}
     */
    $scope.searchEntity = {}

    $scope.search = function (page, rows) {
        brandService.search(page, rows,$scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;

        })
    }

    /**
     * 判断是否有id如果有则，执行修改功能，如果没有则执行添加
     */
    $scope.save = function () {
        var obj = "";
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity)
        }else{
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if(response.success){
                alert(response.message);
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        })
    }
    /*
    * 当点击修改时执行按id查询，把id传给输入框从而让上一个方法使用修改功能而不是新增
    * */
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response
        })
    }

})

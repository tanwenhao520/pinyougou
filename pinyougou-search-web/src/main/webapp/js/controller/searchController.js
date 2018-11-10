pinyougou.controller("searchController", function ($scope, searchService) {

    //提交到后台的查询条件对象
    $scope.searchMap = {"keywords":"","category":"","brand":"","spec":{}};
    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;

        });

    };
 
    $scope.addSearchItem = function (key,value) {
        if("category" == key || "brand" == key){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }
});
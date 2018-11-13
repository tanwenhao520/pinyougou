pinyougou.controller("searchController", function ($scope, searchService,$location) {

    //提交到后台的查询条件对象
    $scope.searchMap = {"keywords":"","category":"","price":"","brand":"","spec":{},"pageNo":1,"pageSize":40,"sotrField":'',"sotr":''};
    //搜索
    $scope.search = function () {
        var pageNo = $scope.searchMap.pageNo;
        searchService.search($scope.searchMap).success(function (response) {

            $scope.resultMap = response;
            $scope.buildPageInfo()
            $scope.searchMap.pageNo = pageNo;
            console.log($scope.searchMap.pageNo + "----------");
        });

    };

    $scope.addSearchItem = function (key,value) {
        if("category" == key || "brand" == key || "price" == key){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        $scope.searchMap.pageNo = 1;
        $scope.search();
    }

    $scope.removeSearchItem = function (key,value) {
        if("category" == key || "brand" == key || "price" == key){
            $scope.searchMap[key] = "";
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo = 1;
        $scope.search();
    }

    $scope.buildPageInfo = function () {
        console.log($scope.searchMap.pageNo + "**********")
    // 定义要在页面显示的页号的集合
            $scope.pageNoList = [];
    // 定义要在页面显示的页号的数量
            var showPageNoTotal = 5;
    // 起始页号
            var startPageNo = 1;
    // 结束页号
            var endPageNo = $scope.resultMap.totalPages;
    // 如果总页数大于要显示的页数才有需要处理显示页号数；否则直接显示所有页号
            if($scope.resultMap.totalPages > showPageNoTotal){
    // 计算当前页左右间隔页数
                var interval = Math.floor(showPageNoTotal/2);
    // 根据间隔得出起始、结束页号
                startPageNo = parseInt($scope.searchMap.pageNo) - interval;
                endPageNo = parseInt($scope.searchMap.pageNo) + interval;
    // 处理页号越界
                if(startPageNo > 0){
                    if(endPageNo > $scope.resultMap.totalPages) {
                        startPageNo = startPageNo - (endPageNo - $scope.resultMap.totalPages);
                        endPageNo = $scope.resultMap.totalPages;
                    }
                } else {
                    endPageNo = endPageNo - (startPageNo - 1);
                    startPageNo = 1;
                }
            }
    // 分页导航条上的前、后的那三个点
            $scope.frontDot = false;
            $scope.backDot = false;
            if(1 < startPageNo) {
                $scope.frontDot = true;
            }
            if(endPageNo < $scope.resultMap.totalPages){
                $scope.backDot = true;
            }
    // 设置要显示的页号
            for (var i =startPageNo; i <= endPageNo; i++) {
                $scope.pageNoList.push(i);
            }
        };
    // 判断是否为当前页
        $scope.isCurrentPage = function(pageNo){
            var tmp = parseInt($scope.searchMap.pageNo);
            console.log(tmp + "----" + pageNo);
            return tmp==pageNo;
        };
    // 根据页号查询
        $scope.queryByPage = function (pageNo) {
            if(0<pageNo && pageNo <= $scope.resultMap.totalPages){
                $scope.searchMap.pageNo = pageNo;
                $scope.search();
            }
        };

        $scope.sortSearch = function (key,value) {
            $scope.searchMap.sotrField = key;
            $scope.searchMap.sotr=value;
            $scope.search();
        }

        $scope.loadKeywords = function () {
            $scope.searchMap.keywords = $location.search()["keywords"];
            $scope.search();
        }

});
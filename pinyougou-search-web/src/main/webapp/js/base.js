var pinyougou = angular.module("pinyougou",[]);


//定义过滤器
pinyougou.filter("trustHtml",["$sce",function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data)
    }
}])
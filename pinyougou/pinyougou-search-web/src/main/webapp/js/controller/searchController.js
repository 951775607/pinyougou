app.controller("searchController", function ($scope, searchService) {

    //定义提交到后台搜索对象
    $scope.searchMap = {"keywords":"","brand":"","category":"","price":"","spec":{}, "pageNo":1, "pageSize":20, "sortField":"","sort":""};

    //添加过滤条件
    $scope.addSearchItem = function (key, value) {
        if ("brand" == key || "category" == key) {
            //如果点击的是品牌说着分类的话
            $scope.searchMap[key] = value;
        } else {
            //规格
            $scope.searchMap.spec[key] = value;
        }
        //点击过滤条件后重新搜索
        $scope.search();
    };


    //搜索
    $scope.search = function () {

        searchService.search($scope.searchMap).success(function (reponse) {
            $scope.resultMap = reponse;
        });

    };

});


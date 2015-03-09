/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.treeRoot = null;

        $scope.tmpDatatypes =[].concat($rootScope.datatypes);
        $scope.datatypeCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.datatype ? parent.datatype.children: parent.children : $scope.treeRoot != null ? $scope.treeRoot:[];
                },
                getTemplate: function (node) {
                    return 'DatatypeEditTree.html';
                }
            });
            $scope.loading = false;
        };
//

//        $scope.orderByPosition = function(obj){
//            if(obj.children){
//                $filter('orderBy')(obj.children, 'position');
//                angular.forEach(obj.children, function (component) {
//                    $scope.orderByPosition(component);
//                });
//            }else if(obj.datatype){
//                $scope.orderByPosition(obj.datatype);
//            }
//        };

        $scope.select = function (datatype) {
            $rootScope.datatype = datatype;
            $rootScope.datatype["type"] = "datatype";
            $scope.treeRoot = [$rootScope.datatype];
            $scope.datatypeCopy = {};
            $scope.datatypeCopy = angular.copy(datatype,$scope.datatypeCopy);
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.clone = function () {
             $scope.loadingSelection = true;
            //TODO: call server
            $scope.datatypeCopy = datatype;
            angular.copy(datatype);
            $scope.treeRoot = {children:[$rootScope.datatype ]};
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.reset = function () {
            $scope.loadingSelection = true;
            $scope.message = "Datatype " + $scope.datatypeCopy.label + " reset successfully";
            angular.extend($rootScope.datatype, $scope.datatypeCopy);
            $scope.treeRoot = null;
            $scope.loadingSelection = false;
        };

        $scope.delete = function () {
            //TODO: Check that there is no reference
            $scope.loadingSelection = true;
            var index = $rootScope.datatypes.indexOf($rootScope.datatype);
            if (index > -1) $rootScope.datatypes.splice(index, 1);
            $scope.message = "Datatype " + $rootScope.datatype.label + " deleted successfully";
            $rootScope.datatype = null;
            $scope.datatypeCopy = null;
            $scope.treeRoot = null;
        };

        $scope.hasChildren = function(node){
            return node && node != null && node.datatype && node.datatype.children != null && node.datatype.children.length >0;
        };


        $scope.validateLabel = function (label, name) {
           if(label && !label.startsWith(name)){
               return false;
           }
            return true;
        };


        $scope.flavors = function(name){





        };

        $scope.goToTable = function(table){

        };

    });




angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope,$filter) {
         $scope.formName = "form_"+ new Date().getTime();
    });
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
        $scope.tmpDatatypes =[].concat($rootScope.datatypes);
        $scope.datatypeCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.datatype ? parent.datatype.children: parent.children : $rootScope.datatype != null ? [$rootScope.datatype]:[];
                },
                getTemplate: function (node) {
                    return 'DatatypeEditTree.html';
                }
                ,
                options: {
                    initialState: 'expanded'
                }
            });

            $scope.$watch(function () {
                return $rootScope.notifyDtTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                }
            });

            $scope.loading = false;
        };

        $scope.select = function (datatype) {
            waitingDialog.show('Loading datatype ' + datatype.label + "...", {dialogSize: 'sm', progressType: 'info'});
            $rootScope.datatype = datatype;
            $rootScope.datatype["type"] = "datatype";
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
            waitingDialog.hide();
        };

        $scope.clone = function () {
            waitingDialog.show('Cloning datatype ' + $rootScope.datatype.label + "...", {dialogSize: 'sm', progressType: 'info'});
            $http.post($rootScope.api('/api/datatypes/'+ $rootScope.datatype.id + '/clone'), {timeout: 60000}).then(function (clone) {
                $rootScope.datatypes.push(clone);
                $rootScope.datatype = clone;
                waitingDialog.hide();
            }, function (error) {
                $scope.error = error;
                waitingDialog.hide();
            });
        };

        $scope.close = function(){
            $rootScope.datatype = null;
             if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

//        $scope.reset = function () {
//            $scope.loadingSelection = true;
//            $scope.message = "Datatype " + $scope.datatypeCopy.label + " reset successfully";
//            angular.extend($rootScope.datatype, $scope.datatypeCopy);
//             $scope.loadingSelection = false;
//        };

        $scope.delete = function () {
            //TODO: Check that there is no reference
            waitingDialog.show('Deleting datatype ' + $rootScope.datatype.label + "...", {dialogSize: 'sm', progressType: 'danger'});
            $http.post($rootScope.api('/api/datatypes/'+ $rootScope.datatype.id + '/delete'), {timeout: 60000}).then(function (response) {
                $scope.loadingSelection = true;
                var index = $rootScope.datatypes.indexOf($rootScope.datatype);
                if (index > -1) $rootScope.datatypes.splice(index, 1);
                $scope.message = "Datatype " + $rootScope.datatype.label + " deleted successfully";
                $rootScope.datatype = null;
                $scope.datatypeCopy = null;
                waitingDialog.hide();
            }, function (error) {
                $scope.error = error;
                waitingDialog.hide();
            });
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


        $scope.onDatatypeChange = function(node){
            $scope.refreshTree();
            node.datatypeLabel = null;
            $rootScope.recordChange(node,'datatype');
         };

        $scope.refreshTree = function(){
            if ($scope.params)
                $scope.params.refresh();
        };

        $scope.goToTable = function(table){
        	$rootScope.table = table;
            $rootScope.notifyTableTreeUpdate = new Date().getTime();
            $rootScope.selectProfileTab(4);
        };

    });




angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope,$filter) {
         $scope.formName = "form_"+ new Date().getTime();
    });
/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('SegmentListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.tmpSegments =[].concat($rootScope.segments);
        $scope.segmentCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.fields ? parent.fields: parent.datatype ? parent.datatype.children:parent.children : $rootScope.segment != null ? [$rootScope.segment]:[];
                },
                getTemplate: function (node) {
                    return 'SegmentEditTree.html';
                }
                ,
                options: {
                    initialState: 'expanded'
                }
            });


            $scope.$watch(function () {
                return $rootScope.notifySegTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                }
            });

            $scope.loading = false;
        };
//
        $scope.select = function (segment) {
            waitingDialog.show('Loading Segment ' + segment.name + "...", {dialogSize: 'sm', progressType: 'info'});
            $rootScope.segment = segment;
            $rootScope.segment["type"] = "segment";
//             $scope.segmentCopy = {};
//            $scope.segmentCopy = angular.copy(segment,$scope.segmentCopy);
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
            waitingDialog.hide();
        };

        $scope.reset = function () {
//            $scope.loadingSelection = true;
//            $scope.message = "Segment " + $scope.segmentCopy.label + " reset successfully";
//            angular.extend($rootScope.segment, $scope.segmentCopy);
//             $scope.loadingSelection = false;
        };

        $scope.close = function(){
            $rootScope.segment = null;
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.hasChildren = function(node){
            return node && node != null && ((node.fields && node.fields.length >0 ) || (node.datatype && node.datatype.children && node.datatype.children.length > 0));
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
    .controller('SegmentRowCtrl', function ($scope,$filter) {
        $scope.formName = "form_"+ new Date().getTime();
    });
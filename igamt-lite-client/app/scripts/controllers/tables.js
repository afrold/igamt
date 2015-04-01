/**
 * Created by Jungyub on 4/01/15.
 */


angular.module('igl').controller('TableListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.tmpTables =[].concat($rootScope.tables);
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                 	return parent && parent.codes ? parent.codes : $rootScope.table != null ? [$rootScope.table]:[];             	
                }
            });

            $scope.$watch(function () {
                return $rootScope.notifyTableTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                    
                }
            });
            $scope.loading = false;
        };

        $scope.select = function (table) {
        	$scope.loadingSelection = true;
            $rootScope.table = table;
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };
        
        /*
         * 	@Column(name = "VERSION")
	private String version;
	@Column(name = "CODESYS")
	private String codesys;
	@Column(name = "OID")
	private String oid;
	@Column(name = "TABLETYPE")
	private String tableType;
	@Column(name = "STABILITY")
	private String stability;
	@Column(name = "EXTENSIBILITY")
	private String extensibility;
	

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "TABLE_CODE", joinColumns = @JoinColumn(name = "IGTABLE"), inverseJoinColumns = @JoinColumn(name = "CODE"))
	private final Set<Code> codes = new HashSet<Code>();
         */

        $scope.addTable = function () {
//        	$scope.loadingSelection = true;
        	$rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
        	$rootScope.newTable = {id:$rootScope.newTableFakeId, type: '', mappingAlternateId: '', mappingId:'', name:'', version:'', codesys:'', oid:'', tableType:'', stability:'', extensibility:'', codes:[]};
        	
        	$rootScope.recordChange($rootScope.newTable,'table');
        	
        	$scope.tmpTables.push($rootScope.newTable);
//        	$rootScope.recordChange($rootScope.table,'codes');
//        	if ($scope.params) $scope.params.refresh();
//        	$scope.loadingSelection = false;
        }
        
        $scope.addCode = function () {
        	$scope.loadingSelection = true;
        	$rootScope.newCodeFakeId = $rootScope.newCodeFakeId - 1;
        	$rootScope.table.codes.push({id:$rootScope.newCodeFakeId, type: 'code', code:'', label:'', codesys:'', source:'', codeUsage:''});
        	$rootScope.recordChange($rootScope.table,'codes');
        	if ($scope.params) $scope.params.refresh();
        	$scope.loadingSelection = false;
        };
        
        $scope.deleteCode = function (code) {
        	$scope.loadingSelection = true;
        	$rootScope.table.codes.splice($rootScope.table.codes.indexOf(code),1);
        	$rootScope.recordChange($rootScope.table,'codes');
        	if ($scope.params) $scope.params.refresh();
        	$scope.loadingSelection = false;
        };
        
        $scope.close = function(){
            $rootScope.table = null;
             if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };
    });

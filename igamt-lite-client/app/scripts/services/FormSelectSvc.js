angular.module('igl')
		.factory('FormsSelectSvc', function($rootScope, $timeout, ngTreetableParams, DatatypeService) {

	var svc = this;

	var tableWidth;
	var scrollbarWidth;
	var csWidth;
	var predWidth;
	var commentWidth;
	svc.datatypesParams;

    svc.selectDocumentMetaData = function () {
      $scope.subview = "EditDocumentMetadata.html";
      $scope.loadingSelection = true;
      $timeout(
        function () {
          $scope.loadingSelection = false;
        }, 100);
    };

		svc.selectDatatype = function (datatype) {
        if (datatype && datatype != null) {
            $rootScope.datatype = datatype;
                    tableWidth = null;
                    scrollbarWidth = $rootScope.getScrollbarWidth();
                    csWidth = getDynamicWidth(1, 3, 890);
                    predWidth = getDynamicWidth(1, 3, 890);
                    commentWidth = getDynamicWidth(1, 3, 890);
        }
        return "EditDatatypes.html";
    };

//    svc.datatypesParams = new ngTreetableParams({
//        getNodes: function (parent) {
//            return DatatypeService.getNodes(parent);
//        },
//        getTemplate: function (node) {
//            return DatatypeService.getTemplate(node);
//        }
//    });

    function getDynamicWidth(a, b, otherColumsWidth) {
        var tableWidth = getTableWidth();
        if (tableWidth > 0) {
            var left = tableWidth - otherColumsWidth;
            return {"width": a * parseInt(left / b) + "px"};
        }
        return "";
    };

    function getTableWidth() {
        if (tableWidth === null || tableWidth == 0) {
            tableWidth = $("#nodeDetailsPanel").width();
        }
        return tableWidth;
    };

	return svc;
})

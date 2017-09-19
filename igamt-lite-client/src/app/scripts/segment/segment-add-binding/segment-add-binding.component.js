/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('AddBindingForSegment', function($scope, $modalInstance, $rootScope, segment) {
  $scope.segment = segment;
  $scope.selectedMessageForBinding = null;
  $scope.selectedSegRefForBinding = null;
  $scope.segRefsList = [];
  $scope.pathForBinding = null;

  $scope.init = function() {
    $scope.selectedMessageForBinding = null;
    $scope.selectedSegRefForBinding = null;
    $scope.segRefsList = [];
    $scope.pathForBinding = null;

  };

  $scope.checkDuplicated = function(positionPath) {
    for (var i = 0; i < $rootScope.references.length; i++) {
      var ref = $rootScope.references[i];
      if (ref.positionPath == positionPath) return true;
    }
    return false;
  };


  $scope.selectMessage = function() {
    $scope.selectedSegRefForBinding = null;
    $scope.segRefsList = [];
    $scope.pathForBinding = null;

    $scope.travelMessage($scope.selectedMessageForBinding.children, $scope.selectedMessageForBinding.name + '-' + $scope.selectedMessageForBinding.identifier, $scope.selectedMessageForBinding.name + '-' + $scope.selectedMessageForBinding.identifier);
  };

  $scope.travelMessage = function(children, positionPath, namePath) {
    angular.forEach(children, function(child) {
      if (child.type === 'group') {
        var groupNames = child.name.split(".");
        var groupName = groupNames[groupNames.length - 1];
        $scope.travelMessage(child.children, positionPath + '.' + child.position, namePath + '.' + groupName);
      } else {
        var s = $rootScope.segmentsMap[child.ref.id];
        if (s.name === $scope.segment.name) {
          var segRef = {};
          segRef.obj = child;
          segRef.path = namePath + '.' + s.name;
          segRef.positionPath = positionPath + '.' + child.position;
          $scope.segRefsList.push(segRef);
        }
      }

    });
  }

  $scope.save = function() {
    var segmentLink = {};
    segmentLink.id = $scope.segment.id;
    segmentLink.name = $scope.segment.name;
    segmentLink.ext = $scope.segment.ext;
    segmentLink.isChanged = true;
    segmentLink.isNew = true;

    $scope.selectedSegRefForBinding = JSON.parse($scope.selectedSegRefForBinding);

    var ref = angular.copy($scope.selectedSegRefForBinding.obj);
    ref.path = $scope.selectedSegRefForBinding.path;
    ref.positionPath = $scope.selectedSegRefForBinding.positionPath;
    ref.target = angular.copy($scope.selectedMessageForBinding);
    ref.segmentLink = segmentLink;
    $rootScope.references.push(ref);

    $modalInstance.close();
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});

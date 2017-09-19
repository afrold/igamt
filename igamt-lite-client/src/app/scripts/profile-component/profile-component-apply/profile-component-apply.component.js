/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('applyPcToCtrl',
  function($scope, $rootScope, $modalInstance, pc, PcService, messages, $http, SegmentLibrarySvc, ngTreetableParams, CompositeMessageService) {
    if (pc.appliedTo === null) {
      pc.appliedTo = [];
    }
    $scope.applyToList = [];
    $scope.messages = messages;
    $scope.msgs = [];
    for (var i = 0; i < $scope.messages.length; i++) {
      $scope.msgs.push({
        id: $scope.messages[i].id,
        name: $scope.messages[i].name
      });
    }
    for (var j = 0; j < pc.appliedTo.length; j++) {
      for (var i = 0; i < $scope.msgs.length; i++) {
        if (pc.appliedTo[j].id === $scope.msgs[i].id) {
          $scope.msgs.splice(i, 1);
        }
      }
    };
    $scope.ApplyToComponentParams = new ngTreetableParams({
      getNodes: function(parent) {
        if ($scope.msgs !== undefined) {

          if (parent) {
            if (parent.children) {

              return parent.children;
            }

          } else {
            return $scope.msgs;
          }

        }
      },
      getTemplate: function(node) {
        return 'applyTable';
      }
    });
    $scope.addApplyToMsg = function(node) {
      $scope.applyToList.push(node);
      var index = $scope.msgs.indexOf(node);
      if (index > -1) $scope.msgs.splice(index, 1);
      if ($scope.ApplyToComponentParams) {
        $scope.ApplyToComponentParams.refresh();
      }
    };
    $scope.removeSelectedApplyTo = function(applyTo) {
      var index = $scope.applyToList.indexOf(applyTo);
      if (index > -1) $scope.applyToList.splice(index, 1);
      $scope.msgs.push(applyTo);
      if ($scope.ApplyToComponentParams) {
        $scope.ApplyToComponentParams.refresh();
      }
    };
    $scope.apply = function() {

      for (var j = 0; j < $scope.applyToList.length; j++) {
        $rootScope.profileComponent.appliedTo.push({
          id: $scope.applyToList[j].id,
          name: $scope.applyToList[j].name
        });
        for (var i = 0; i < $rootScope.messages.children.length; i++) {
          if ($rootScope.messages.children[i].id === $scope.applyToList[j].id) {
            if (!$rootScope.messages.children[i].appliedPc) {
              $rootScope.messages.children[i].appliedPc = [];
            }
            $rootScope.messages.children[i].appliedPc.push({
              id: $scope.applyToList[j].id,
              name: $scope.applyToList[j].name
            });
          }
        }


      }

      var processFields = function(fields) {
        for (var i = 0; i < fields.length; i++) {
          fields[i].datatype = $rootScope.datatypesMap[fields[i].datatype.id];
          if (fields[i].datatype.components.length > 0) {
            fields[i].datatype.components = processFields(fields[i].datatype.components);
          }

        }
        return fields;
      };
      var processMessage = function(message) {
        for (var i = 0; i < message.children.length; i++) {
          if (message.children[i].type === "segmentRef") {
            message.children[i].ref = $rootScope.segmentsMap[message.children[i].ref.id];
            message.children[i].ref.fields = processFields(message.children[i].ref.fields);
          } else if (message.children[i].type === "group") {
            processMessage(message.children[i]);
          }
        }
        return message;
      };

      var message = angular.copy($rootScope.messages.children[0]);

      var processedMsg = processMessage(message);

      CompositeMessageService.create(processedMsg).then(function(result) {
        $modalInstance.close();
      });


    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };
  });

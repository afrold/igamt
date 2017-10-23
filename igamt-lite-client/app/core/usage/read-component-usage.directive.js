angular.module('igl').directive('readComponentUsage', function() {
  return {
    restrict: 'EA', //E = element, A = attribute, C = class, M = comment
    scope: {
      //@ reads the attribute value, = provides two-way binding, & works with functions
      node: '='
    },
    replace: false,
    template: '<span ng-if="predicate == null">\n' +
    '                {{::node.usage}}\n' +
    '            </span>\n' +
    '            <div ng-if="predicate != null">\n' +
     '                    C({{::predicate.trueUsage}}/{{::predicate.falseUsage}})\n' +
     '\n' +
    '            </div>',
    controller: function($scope, $rootScope){

      var getPredicate = function() {
        $scope.parent = $rootScope.parentsMap && $rootScope.parentsMap[$scope.node.id] ? $rootScope.parentsMap[$scope.node.id] : null;
        if ($scope.parent != null && $scope.parent.predicates != null && $scope.parent.predicates.length > 0) {
          for (var i = 0, len1 = $scope.parent.predicates.length; i < len1; i++) {
            if ($scope.parent.predicates[i].constraintTarget.indexOf($scope.node.position + '[') === 0) {
              return $scope.parent.predicates[i];
            }
          }
        }
        return null;
      };

      $scope.predicate  = getPredicate();
    }
   }
 });

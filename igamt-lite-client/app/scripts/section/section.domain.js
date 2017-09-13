
angular.module('igl').factory('Section', function($http, $q) {
  var Section = function() {
    this.data = null;
    this.type = null;
    this.sections = [];
  };
  return Section;
});

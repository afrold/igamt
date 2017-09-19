/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('labelController', function($scope) {
  $scope.getLabel = function(element) {
    if(element){
      if (element.type === 'table') {
        if (!element.ext || element.ext == "") {
          return element.bindingIdentifier;
        } else {
          return element.bindingIdentifier + "_" + element.ext;
        }
      }
      if (!element.ext || element.ext == "") {
        return element.name;
      } else {
        return element.name + "_" + element.ext;
      }

    }


  };
  $scope.hasHl7Version = function(element) {
    if (element&&element.hl7Version) {
      return element.hl7Version;
    }
  };

  $scope.getDescriptionLabel = function(element) {
    if(!element){
      return "UNFOUND";
    }
    if (element.type === 'table') {

      if (element.name && element.name !== '') {
        return "-" + element.name;
      } else {
        return "";
      }

    } else {
      return "-" + element.description;
    }

  }

  $scope.getScopeLabel = function(leaf) {
    if (leaf) {
      if (leaf.scope === 'HL7STANDARD') {
        return 'HL7';
      } else if (leaf.scope === 'USER') {
        return 'USR';
      } else if (leaf.scope === 'INTERMASTER') {
        return 'DRV';
      } else if (leaf.scope === 'MASTER') {
        return 'MAS';
      } else if (leaf.scope === 'PRELOADED') {
        return 'PRL';
      } else if (leaf.scope === 'PHINVADS') {
        return 'PVS';
      } else {
        return "";
      }

    }
  };


});

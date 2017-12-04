/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddCSVTableOpenCtrl', function ($scope, $mdDialog, selectedTableLibary, $rootScope, $http, $cookies, TableLibrarySvc, TableService, IgDocumentService) {
  $scope.loading = false;
  $scope.selectedTableLibary = selectedTableLibary;
  $scope.importedTable = null;
  $scope.selectedFileName = null;
  $scope.data = null;
  $scope.isInValild = false;
  $scope.erorrMessages = [];
  $scope.validateForSelectedFile = function (files) {
    $scope.isInValild = false;
    var f = document.getElementById('csvValueSetFile').files[0];
    var reader = new FileReader();
    reader.onloadend = function (e) {
      $scope.data = Papa.parse(e.target.result);

      if ($scope.data.errors.length > 0) {
        $scope.isInValild = true;
        angular.forEach($scope.data.errors, function (e) {
          $scope.erorrMessages.push(e.message);
        });
      }

      var index = 0;
      $scope.importedTable = {};
      $scope.importedTable.scope = 'USER';
      $scope.importedTable.codes = [];
      $scope.importedTable.libIds = [];
      $scope.importedTable.sourceType=='INTERNAL';
      var duplicatedCodeSystems = [];
      angular.forEach($scope.data.data, function (row) {
        index = index + 1;

        if (index > 1 && index < 11) {
          if (row[1] != '') {
            switch (row[0]) {
              case 'Mapping Identifier':
                $scope.importedTable.bindingIdentifier = row[1];
                break;
              case 'Name':
                $scope.importedTable.name = row[1];
                break;
              case 'Description':
                $scope.importedTable.description = row[1];
                break;
              case 'OID':
                $scope.importedTable.oid = row[1];
                break;
              case 'Version':
                $scope.importedTable.version = row[1];
                break;
              case 'Extensibility':
                $scope.importedTable.extensibility = row[1];
                break;
              case 'Stability':
                $scope.importedTable.stability = row[1];
                break;
              case 'Content Definition':
                $scope.importedTable.contentDefinition = row[1];
                break;
              case 'Comment':
                $scope.importedTable.comment = row[1];
            }
          }
        } else if (index > 13) {

          var code = {};
          code.value = row[0];
          code.label = row[1];
          code.codeSystem = row[2];
          code.codeUsage = row[3];
          code.comments = row[4];

          if(code.codeSystem && code.codeSystem !== '') duplicatedCodeSystems.push(code.codeSystem);
          if (code.value != null && code.value != "") {
            $scope.importedTable.codes.push(code);
          }
        }
      });

      var uniqueCodeSystems = [];
      $.each(duplicatedCodeSystems, function(i, el){
        if($.inArray(el, uniqueCodeSystems) === -1) uniqueCodeSystems.push(el);
      });
      $scope.importedTable.codeSystems = uniqueCodeSystems;



      if($scope.importedTable.codes) $scope.importedTable.numberOfCodes = $scope.importedTable.codes.length ;

      if ($scope.importedTable.bindingIdentifier == null || $scope.importedTable.bindingIdentifier == '') {
        $scope.isInValild = true;
        $scope.erorrMessages.push('No Binding Identifier');
      }

      if ($scope.importedTable.name == null || $scope.importedTable.name == '') {
        $scope.isInValild = true;
        $scope.erorrMessages.push('No Name');
      }

      if($scope.importedTable.codes && $scope.importedTable.codes.length > 500) {
        $scope.isInValild = true;
        $scope.erorrMessages.push('For internally managed value sets, IGAMT imposes a maximum limit of 500”. Please see alternative methods to reference large external value sets');
      }

      var errorElm = $("#errorMessageForCSV");
      var csvSaveButton = $("#csvSaveButton");
      errorElm.empty();

      if ($scope.isInValild) {
        errorElm.append('<span style=\"color:red;\">' + files[0].name + ' is invalid!</span>');
        angular.forEach($scope.erorrMessages, function (e) {
          errorElm.append("<li style=\"color:red;\">" + e + "</li>");
          csvSaveButton.prop('disabled', true);
        });
      } else {
        errorElm.append('<span>' + files[0].name + ' is valid!</span>');
        csvSaveButton.prop('disabled', false);
      }

    };

    reader.readAsBinaryString(f);
  };

  $scope.cancel = function () {
    $mdDialog.hide();
  };


  $scope.save = function () {
    $scope.importedTable.bindingIdentifier = $rootScope.createNewFlavorName($scope.importedTable.bindingIdentifier);
    $scope.importedTable.libIds.push($scope.selectedTableLibary.id);
    $scope.importedTable.newTable = true;

    TableService.save($scope.importedTable).then(function (result) {
      var newTable = result;
      var newLink = {};
      newLink.bindingIdentifier = newTable.bindingIdentifier;
      newLink.id = newTable.id;

      TableLibrarySvc.addChild($scope.selectedTableLibary.id, newLink).then(function (link) {
        $scope.selectedTableLibary.children.splice(0, 0, newLink);
        $rootScope.tables.splice(0, 0, newTable);
        $rootScope.table = newTable;
        $rootScope.tablesMap[newTable.id] = newTable;


        if ($rootScope.filteredTablesList && $rootScope.filteredTablesList != null) {
          $rootScope.filteredTablesList.push(newTable);
          $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
        }
        $rootScope.$broadcast('event:openTable', newTable);
        $mdDialog.hide();
      }, function (error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });

    }, function (error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });


  };

  function positionElements(chidren) {
    var sorted = _.sortBy(chidren, "sectionPosition");
    var start = sorted[0].sectionPosition;
    _.each(sorted, function (sortee) {
      sortee.sectionPosition = start++;
    });
    return sorted;
  }
});

angular.module('igl').controller('SearchController', function ($scope, SearchService, $mdDialog) {
    $scope.types = [
        {
            name:'Dataypes',
            value:'datatypes',
            fields:[
                {
                    name:'Name',
                    param:'name',
                    required:true
                },
                {
                    name:'HL7 Version',
                    param:'hl7version',
                    required:false
                }
            ]
        },
        {
            name:'Value Sets',
            value:'valueSets',
            fields:[
                {
                    name:'Scope',
                    param:'scope',
                    values:['HL7STANDARD','PHINVADS'],
                    required:true
                },
                {
                    name:'Binding Identifier',
                    param:'bindingIdentifier',
                    required:true
                },
                {
                    name:'HL7 Version',
                    param:'hl7version',
                    required:false
                }
            ]
        },
        {
            name:'Segments',
            value:'segments',
            fields:[
                {
                    name:'Name',
                    param:'name',
                    required:true
                },
                {
                    name:'HL7 Version',
                    param:'hl7version',
                    required:false
                }
            ]
        },        {
            name:'Message',
            value:'message',
            fields:[
                {
                    name:'Type',
                    param:'messageType',
                    required:true
                },
                {
                    name:'Event',
                    param:'event',
                    required:true
                },
                {
                    name:'HL7 Version',
                    param:'hl7version',
                    required:false
                }
            ]
        }
    ];

    $scope.updateResult = function(data){
        $scope.data = data;
    }

    $scope.doSearch = function(){
        return SearchService.search($scope.searchParameters,$scope.updateResult);
    }

    $scope.showAdvanced = function(entity) {
        //alert("Display "+entity.name);
        $scope.selectedEntity = entity;
        $mdDialog.show({
          controller: SearchContentDialogController,
          templateUrl: 'views/searchContentDialog.html',
          parent: angular.element(document.body),
          clickOutsideToClose:true,
          locals : {
              entity : entity
          }
        });
      };

    function SearchContentDialogController($scope, $mdDialog, entity) {
        $scope.populate = function (data){
            $scope.entityContent = data;
        };

        SearchService.getContent(entity,$scope.populate);

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});

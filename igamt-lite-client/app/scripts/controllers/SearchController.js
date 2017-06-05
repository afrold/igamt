angular.module('igl').controller('SearchController', function ($scope, SearchService, $mdDialog,$sce) {
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
                    param:'hl7Version',
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
                    param:'hl7Version',
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
                    param:'hl7Version',
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
                    param:'hl7Version',
                    required:true
                }
            ]
        }
    ];

    $scope.updateResult = function(data){
        if(data.length >0){
            $scope.data = data;
        } else {
            $scope.showErrorMessage = true;
        }
    }

    $scope.doSearch = function(){
        return SearchService.search($scope.searchParameters,$scope.updateResult,$scope.searchError);
    }

    $scope.isFormValid = function(){
        if($scope.searchParameters){
            if($scope.searchParameters.fields){
                $scope.searchParameters.fields.forEach(function(field){
                    if(field.required){
                        if(!field.value || field.value === ''){
                            return false;
                        }
                    }
                });
                return true;
            }
        }
        return false;
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
            $scope.entityContent = $sce.trustAsHtml(data);
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

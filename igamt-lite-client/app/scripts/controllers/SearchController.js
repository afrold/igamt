angular.module('igl').controller('SearchController', function ($scope, SearchService, $mdDialog,$location) {
    $scope.types = [
        {
            name:'Dataype',
            value:'datatype',
            fields:[
                {
                    name:'Name',
                    param:'name',
                    required:true
                },
                {
                    name:'HL7 Version',
                    param:'hl7Version',
                    required:true
                }
            ]
        },
        {
            name:'Value Set',
            value:'valueSet',
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
            name:'Segment',
            value:'segment',
            fields:[
                {
                    name:'Name',
                    param:'name',
                    required:true
                },
                {
                    name:'HL7 Version',
                    param:'hl7Version',
                    required:true
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

    $scope.init = function () {
        SearchService.listHL7Versions($scope.setHl7Versions);
    }

    $scope.setHl7Versions = function(hl7Versions){
        $scope.types.forEach(function(type){
            type.fields.forEach(function(field){
                if(field.param==='hl7Version'){
                    field.values = hl7Versions;
                }
            });
        });
        $scope.searchParameters = $scope.types[0];
    }

    $scope.updateResult = function(data){
        if(data.html != ''){
            $scope.data = data;
            var basePath = $scope.url = $location.absUrl().substring(0,$location.absUrl().length - ($location.url().length+1));
            $scope.shareHTML = basePath + SearchService.getExportUrl($scope.data.dataModel,'html');
            $scope.shareJSON = basePath + SearchService.getExportUrl($scope.data.dataModel,'json');
        } else {
            $scope.showErrorMessage = true;
        }
    }

    $scope.doSearch = function(){
        delete $scope.data;
        $scope.showErrorMessage = false;
        return SearchService.search($scope.searchParameters,$scope.updateResult,$scope.searchError);
    }

    $scope.isFormValid = function(){
        if($scope.searchParameters){
            if($scope.searchParameters.fields){
                var isValid = true;
                $scope.searchParameters.fields.forEach(function(field){
                    if(field.required){
                        if(!field.value || field.value === ''){
                            isValid = false;
                        }
                    }
                });
                return isValid;
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

    $scope.init();
});

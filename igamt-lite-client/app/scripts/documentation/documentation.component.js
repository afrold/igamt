/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DocumentationController', function($scope, $rootScope, Restangular, $filter, $http, $mdDialog, $timeout,DocumentationService,userInfoService) {


  $scope.editMode=false;
  $rootScope.newOne=false;
  $scope.activeId=null;
  $rootScope.documentation=null;

  $scope.init=function(){

    DocumentationService.findAll().then(function(result){
      $rootScope.documentationsMap={};
      $rootScope.documentations=result;
      $rootScope.decisions=[];
      $rootScope.FAQs=[];
      $rootScope.UserGuides=[];
      $rootScope.usersNotes=[];
      $rootScope.releaseNotes=[];
      $rootScope.glossary=[];

        angular.forEach(result, function(documentation){

        $rootScope.documentationsMap[documentation.id]=documentation;

        if(documentation.type==='decision'){

          $rootScope.decisions.push(documentation);

        }else if(documentation.type==='userGuide'){

          $rootScope.UserGuides.push(documentation);

        }else if(documentation.type==='FAQ'){

          $rootScope.FAQs.push(documentation);

        }else if(documentation.type==='releaseNote'){

          $rootScope.releaseNotes.push(documentation);

        }else if(documentation.type==='glossary'){

          $rootScope.glossary.push(documentation);
        }

      });


      if(userInfoService.isAuthenticated()){

        DocumentationService.findUserNotes().then(function(result){
          $rootScope.usersNotes=result;
          angular.forEach(result, function(documentation){
            $rootScope.documentationsMap[documentation.id]=documentation;
          });
        });
      }
        $rootScope.decisions= _.sortBy($rootScope.decisions, 'position');
        $rootScope.UserGuides=  _.sortBy($rootScope.UserGuides, 'position');
        $rootScope.FAQs= _.sortBy($rootScope.FAQs, 'position');
        $rootScope.releaseNotes= _.sortBy($rootScope.releaseNotes, 'position');
        $rootScope.glossary= _.sortBy($rootScope.glossary, 'position');
        $rootScope.usersNotes= _.sortBy($rootScope.usersNotes, 'position');

    });

  };

    $scope.reorder = function (list, type) {
        var positionList = [];
        for (i = 0; i < list.length; i++) {
            list[i].position = i + 1;
            positionList.push({"id": list[i].id, "position": list[i].position});

        }


        DocumentationService.reorder(positionList).then(function (response) {
            $rootScope.msg().text = "OrderChanged";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;

        }, function (error) {
            $rootScope.msg().text = "OrderChangedFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

        });
    };


  $scope.deleteDocumentation=function(documentation){
    DocumentationService.delete(documentation).then(function(){
        for(i=0; i<$rootScope.documentations.length;i++){

          if(documentation.id == $rootScope.documentations[i].id){
            $rootScope.documentations.splice(i, 1);
            $rootScope.documentation=null;

          }
        }

        $rootScope.clearChanges();
        $rootScope.msg().text = documentation.type+"DeleteSuccess";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
      }, function(error) {
        $rootScope.msg().text = documentation.type+"DeleteFaild";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      }

    );
  }


  $scope.edit=function(){


    $scope.editMode=true;

  }



  $scope.processAddDocumentation= function(type){

    var newId=new ObjectId().toString();
    $rootScope.documentationToAdd={
        id: newId,
        title:"New",
        type:type,
        content:""

    };

    $scope.editMode=true;
    $scope.activeId=newId;
    $rootScope.newOne=true;
    if(type==='decision'){
      $rootScope.decisions.push($rootScope.documentationToAdd);
      $rootScope.documentations=$rootScope.decisions;
      $rootScope.documentationToAdd.title="New Decision";

    }else if(type==='userGuide'){
      $rootScope.UserGuides.push($rootScope.documentationToAdd);
      $rootScope.documentations=$rootScope.UserGuides;
      $rootScope.documentationToAdd.title="New User Guide";


    }else if(type=='FAQ'){
      $rootScope.FAQs.push($rootScope.documentationToAdd);
      $rootScope.documentations=$rootScope.FAQs;
      $rootScope.documentationToAdd.title="New FAQ";

    }else if (type=='UserNote'){
      $rootScope.usersNotes.push($rootScope.documentationToAdd);
      $rootScope.documentations=$rootScope.usersNotes;
      $rootScope.documentationToAdd.title="New User Note";
      $rootScope.documentationToAdd.owner=userInfoService.getAccountID();

    }else if(type=='releaseNote'){
      $rootScope.releaseNotes.push($rootScope.documentationToAdd);
      $rootScope.documentations=$rootScope.releaseNotes;
      $rootScope.documentationToAdd.title="New Release Note";
    }
    else if(type =='glossary'){
      $rootScope.glossary.push($rootScope.documentationToAdd);
      $rootScope.documentations=$rootScope.glossary;
      $rootScope.documentationToAdd.title="New Term";
    }
    //$rootScope.documentations.push($rootScope.documentationToAdd);
    $rootScope.documentationsMap[$rootScope.documentationToAdd.id]=$rootScope.documentationToAdd;
    //$scope.editDocumentation($rootScope.documentationToAdd);
      $rootScope.documentationToAdd.position=$rootScope.documentations.length+1;

      $rootScope.documentation=angular.copy($rootScope.documentationToAdd);
    $rootScope.currentData=$rootScope.documentation;
    //$rootScope.$emit("event:initEditArea");


  };
  $scope.processEditDocumentation = function(documentation){
    if(documentation.type==='decision'){
      $rootScope.documentations=$rootScope.decisions;
    }else if(documentation.type==='userGuide'){
      $rootScope.documentations=$rootScope.UserGuides;
    }else if(documentation.type==='FAQ'){
      $rootScope.documentations=$rootScope.FAQs;
    }else if(documentation.type=='UserNote'){
      $rootScope.documentations=$rootScope.usersNotes;
    }else if(documentation.type=='releaseNote'){
      $rootScope.documentations=$rootScope.releaseNotes;
    }
    else if(documentation.type=='glossary'){
        $rootScope.documentations=$rootScope.glossary;
    }
    $scope.activeId=documentation.id;
    //$rootScope.$emit("event:initEditArea");
    console.log(documentation);
    $rootScope.documentation=angular.copy(documentation);
    $rootScope.currentData=$rootScope.documentation;

    $scope.editMode=false;
    $rootScope.newOne=false;


  }




  $scope.editDocumentation=function(documentation){
    if ($rootScope.hasChanges()) {

      $rootScope.openConfirmLeaveDlg().result.then(function() {
        $scope.processEditDocumentation(documentation);
      });
    } else {

      $scope.processEditDocumentation(documentation);

    }
  };


  $scope.addDocumentation=function(type){
    if ($rootScope.hasChanges()) {

      $rootScope.openConfirmLeaveDlg().result.then(function() {
        $scope.processAddDocumentation(type);
      });
    } else {

      $scope.processAddDocumentation(type);

    }
  };


  $scope.saveDocumentation=function(documentation){
    DocumentationService.save(documentation).then(function(saved){

        console.log("befor");
        $rootScope.documentation=saved.data;

        console.log($rootScope.documentationsMap[documentation.id]);
        console.log($rootScope.documentations);
        $rootScope.documentationsMap[documentation.id]= saved.data;

        console.log("After")
        console.log($rootScope.documentationsMap[documentation.id]);
        console.log($rootScope.documentations);
        console.log(saved);


        angular.forEach($rootScope.documentations, function(d){
          if(d.id==$rootScope.documentation.id){
            d.title=$rootScope.documentationsMap[saved.data.id].title;
            d.content=$rootScope.documentationsMap[saved.data.id].content;
            d.dateUpdated=$rootScope.documentationsMap[saved.data.id].dateUpdated;
            d.username=$rootScope.documentationsMap[saved.data.id].username;
          }
        });

        if($scope.editForm) {
          $scope.editForm.$setPristine();
          $scope.editForm.$dirty = false;
        }
        $scope.editMode=false;
        $rootScope.newOne=false;
        $rootScope.clearChanges();
        $rootScope.msg().text = documentation.type+"SaveSuccess";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
      }, function(error) {
        $rootScope.msg().text = documentation.type+"SaveFaild";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      }
    );
  };

  $scope.resetDocumentation=function(d){
    console.log(d);
    console.log($rootScope.documentationsMap);
    $rootScope.documentation= angular.copy($rootScope.documentationsMap[d.id]);
    if($scope.editForm) {
      $scope.editForm.$setPristine();
      $scope.editForm.$dirty = false;
    }
  };





  $scope.confirmDeleteDocumentation = function(documentation) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'confirmDocumentationDeleteCtrl.html',
      controller: 'confirmDocumentationDeleteCtrl',
      locals: {
        documentationToDelete: function() {
          return documentation;
        }
      }
    });
    modalInstance.then(function(res) {
      if(res && res!== 'cancel'){
          $scope.deleteDocumentation(documentation);
      }
    });
  }
});

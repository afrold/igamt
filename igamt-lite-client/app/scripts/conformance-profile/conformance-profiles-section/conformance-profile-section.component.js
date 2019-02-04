/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ConformanceProfilesSectionCtrl', function ($scope, $rootScope,IgDocumentService, $timeout, blockUI) {



    $scope.save = function () {
        if ($rootScope.igdocument != null) {
            var messages = $rootScope.section;
            console.log($rootScope.section);
            IgDocumentService.saveConformanceProfileSection($rootScope.igdocument.id,messages['sectionDescription'], messages['config']).then(function (result) {
                $scope.saving = false;
                $scope.saved = true;
                $rootScope.section.dateUpdated = result.date;
                $rootScope.igdocument.dateUpdated = $rootScope.section.dateUpdated;
                $rootScope.igdocument.profile.messages['config'] = messages['config'];
                $rootScope.igdocument.profile.messages['sectionTitle'] = messages['sectionTitle'];
                $rootScope.igdocument.profile.messages['sectionContents'] = messages['sectionContents'];
                $rootScope.igdocument.profile.messages['sectionDescription'] = messages['sectionDescription'];

                if ($scope.editForm) {
                    $scope.editForm.$setPristine();
                    $scope.editForm.$dirty = false;
                }
                $rootScope.clearChanges();
                $rootScope.msg().text = "sectionSaved";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;

            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
                $scope.saved = false;
                $scope.saving = false;
            });
        }
    };

    $scope.resetSection = function () {
        $rootScope.section['sectionTitle'] = $rootScope.igdocument.profile.messages['sectionTitle'];
        $rootScope.section['sectionContents'] = $rootScope.igdocument.profile.messages['sectionContents'];
        $rootScope.section['sectionDescription'] = $rootScope.igdocument.profile.messages['sectionDescription'];
        if ($scope.editForm) {
            $scope.editForm.$dirty = false;

            $scope.editForm.$setPristine();
        }
        $rootScope.clearChanges();
    };


    $scope.toggle=function(ack,message){

        if($rootScope.section.config.ackBinding[message.id]&&$rootScope.section.config.ackBinding[message.id] == ack.id){

            $rootScope.section.config.ackBinding[message.id] = null;

        }else{

            $rootScope.section.config.ackBinding[message.id] = ack.id;

        }
        $rootScope.recordChanged();

    };

    $scope.isAck=function (ack) {
        return ack.messageType.toLowerCase()=='ack';
    };

    $scope.isVaries=function (ack) {
        return ack.event.toLowerCase()=='varies'||ack.event==null ||ack.event=="";
    };


});

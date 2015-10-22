angular.module('igl').controller('HL7VMessagesDlgCtrl',
		function($scope, $rootScope, $modalInstance, $http, HL7VersionSvc, MessageSelectionSvc) {

			var profileVersions = [];
			
			var hl7Version = HL7VersionSvc.hl7Version;
			var messageIdsCurrent = MessageSelectionSvc.messageIds;
			
			 $http.get('api/profiles/hl7/messageListByVersion/' + hl7Version + '/' + messageIdsCurrent, {
				 	timeout : 60000
				 }).then(function (response) {
					 var messagesUpdated = angular.fromJson(response.data);
					 MessageSelectionSvc.messages = messagesUpdated;
					 $scope.messagesByVersion = messagesUpdated;
			 });
			
			$scope.trackSelections = function(bool, id) {
				if (bool) {
					profileVersions.push(id);
				} else {
					for (var i = 0; i < profileVersions.length; i++) {
						if (profileVersions[i].id == id) {
							profileVersions.splice(i, 1);
						}
					}
				}
			};

			$scope.ok = function() {
				$modalInstance.close(profileVersions);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});

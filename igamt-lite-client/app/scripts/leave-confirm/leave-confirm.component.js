/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmLeaveDlgCtrl', function($scope, $mdDialog, $rootScope, $http, SectionSvc, FilteringSvc, MessageService, SegmentService, SegmentLibrarySvc, DatatypeLibrarySvc, DatatypeService, IgDocumentService, ProfileSvc, TableService, TableLibrarySvc, DocumentationService, PcService, CompositeProfileService,valid) {
  $scope.continue = function() {
    $rootScope.clearChanges();
    $mdDialog.hide('continue');
  };
  $scope.valid=valid;

  $scope.discard = function() {
    var data = $rootScope.currentData;
      $rootScope.saveError=false;
    if (data.type && data.type === "message") {
      MessageService.reset();

    } else if (data.type && data.type === "segment") {
      SegmentService.reset();
    } else if (data.type && data.type === "datatype") {
      DatatypeService.reset();
    } else if (data.type === "decision" || data.type === "FAQ" || data.type === "userGuide" || data.type === 'UserNote' || data.type === 'releaseNote') {
      if ($rootScope.newOne) {

        for (i = 0; i < $rootScope.documentations.length; i++) {
          if (data.id == $rootScope.documentations[i].id) {
            $rootScope.documentations.splice(i, 1);
          }
        }
      }
      $rootScope.documentation = null;


    }
    $scope.continue();
    $rootScope.addedSegments = [];
    $rootScope.addedDatatypes = [];
    $rootScope.addedTables = [];

  };

  $scope.error = null;
  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };

  $scope.save = function() {
    if ($rootScope.editForm) {
      $rootScope.editForm.$setPristine();
      $rootScope.editForm.$dirty = false;
    }
    $rootScope.clearChanges();

    var data = $rootScope.currentData;
    if ($rootScope.libraryDoc && $rootScope.libraryDoc != null) {
      if (data.datatypeLibId && data.date) {
        DatatypeLibrarySvc.saveMetaData($rootScope.libraryDoc.datatypeLibrary.id, data);
      }

    }


    if (data.type === "decision" || data.type === "FAQ" || data.type === "userGuide" || data.type === 'UserNote' || data.type === 'releaseNote') {
      DocumentationService.save(data).then(function(saved) {
        $rootScope.documentation = saved.data;
        $rootScope.documentationsMap[data.id] = saved.data;
        angular.forEach($rootScope.documentations, function(d) {
          if (d.id == $rootScope.documentation.id) {
            d.title = $rootScope.documentationsMap[saved.data.id].title;
            d.content = $rootScope.documentationsMap[saved.data.id].content;
            d.dateUpdated = $rootScope.documentationsMap[saved.data.id].dateUpdated;
            d.username = $rootScope.documentationsMap[saved.data.id].username;
          }
        });
        $scope.continue();
        if ($scope.editForm) {
          $scope.editForm.$setPristine();
          $scope.editForm.$dirty = false;
        }
        $scope.editMode = false;
        $scope.newOne = false;
        $rootScope.clearChanges();
        $rootScope.msg().text = data.type + "SaveSuccess";;
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
      }, function(error) {
        $rootScope.msg().text = data.type + "SaveFaild";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      });


    }
    ////console.log(data);

    if (data.type && data.type === "section") {
      ////console.log($rootScope.originalSection);
      ////console.log(data);

      SectionSvc.update($rootScope.igdocument.id, data).then(function(result) {
        ////console.log($rootScope.igdocument);
        SectionSvc.merge($rootScope.originalSection, data);
        $scope.continue();
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    } else if (data.type && data.type === "messages") {
      ////console.log($rootScope.originalSection);
      ////console.log(data);
      SectionSvc.update($rootScope.igdocument.id, $rootScope.section).then(function(result) {
        ////console.log($rootScope.igdocument);
        SectionSvc.merge($rootScope.originalSection, $rootScope.section);
        $scope.continue();
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    } else if (data.type && data.type === "segments") {
      ////console.log($rootScope.originalSection);
      ////console.log(data);

      SectionSvc.update($rootScope.igdocument.id, $rootScope.section).then(function(result) {
        ////console.log($rootScope.igdocument);
        SectionSvc.merge($rootScope.originalSection, $rootScope.section);
        $scope.continue();
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    } else if (data.type && data.type === "datatypes") {
      ////console.log($rootScope.originalSection);
      ////console.log(data);

      SectionSvc.update($rootScope.igdocument.id, $rootScope.section).then(function(result) {
        ////console.log($rootScope.igdocument);
        SectionSvc.merge($rootScope.originalSection, $rootScope.section);
        $scope.continue();
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    } else if (data.type && data.type === "tables") {
      ////console.log($rootScope.originalSection);
      ////console.log(data);

      TableLibrarySvc.saveSection($rootScope.igdocument.profile.tableLibrary.id, $rootScope.section).then(function(result) {
        $rootScope.section.dateUpdated = result.date;
        $rootScope.igdocument.dateUpdated =  $rootScope.section.dateUpdated;
        $rootScope.igdocument.profile.tableLibrary['exportConfig'] =  $rootScope.section.exportConfig;
        $rootScope.igdocument.profile.tableLibrary['sectionTitle'] =  $rootScope.section['sectionTitle'];
        $rootScope.igdocument.profile.tableLibrary['sectionContents'] =  $rootScope.section['sectionContents'];
        $rootScope.igdocument.profile.tableLibrary['sectionDescription'] =  $rootScope.section['sectionDescription'];
        $scope.continue();
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    } else if (data.type && data.type === "message") {
      var message = $rootScope.message;
      ////console.log($rootScope.message);
      MessageService.save(message).then(function(result) {
        var index = MessageService.findIndex(message.id);
        if (index < 0) {
          $rootScope.igdocument.profile.messages.children.splice(0, 0, message);
        }
        MessageService.saveNewElements().then(function() {
          MessageService.merge($rootScope.messagesMap[message.id], message);
          $scope.continue();
        }, function(error) {
          $rootScope.msg().text = "Sorry an error occured. Please try again";
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
        });
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });

    } else if (data.type && data.type === "profilecomponent") {
      PcService.save($rootScope.igdocument.profile.profileComponentLibrary.id, $rootScope.profileComponent).then(function(result) {
        for (var i = 0; i < $rootScope.igdocument.profile.profileComponentLibrary.children.length; i++) {
          if ($rootScope.igdocument.profile.profileComponentLibrary.children[i].id === $rootScope.profileComponent.id) {
            $rootScope.igdocument.profile.profileComponentLibrary.children[i].name = $rootScope.profileComponent.name;
            $rootScope.igdocument.profile.profileComponentLibrary.children[i].comment = $rootScope.profileComponent.comment;
            $rootScope.igdocument.profile.profileComponentLibrary.children[i].description = $rootScope.profileComponent.description;
          }

        }
        for (var i = 0; i < $rootScope.profileComponents.length; i++) {
          if ($rootScope.profileComponents[i].id === $rootScope.profileComponent.id) {
            $rootScope.profileComponents[i] = $rootScope.profileComponent;
          }
        }
        $rootScope.profileComponentsMap[$rootScope.profileComponent.id] = $rootScope.profileComponent;

        $scope.changes = false;
        $scope.continue();

      });
    } else if (data.type && data.type === "compositeprofilestructure") {
      CompositeProfileService.save($rootScope.compositeProfileStructure).then(function(result) {

          $rootScope.compositeProfileStructure = result;
          $rootScope.$emit("event:updateIgDate");
          for (var i = 0; i < $rootScope.igdocument.profile.compositeProfiles.children.length; i++) {
            if ($rootScope.igdocument.profile.compositeProfiles.children[i].id === result.id) {
              $rootScope.igdocument.profile.compositeProfiles.children[i] = result;
            }
          }
          $rootScope.compositeProfilesStructureMap[result.id] = result;

          $scope.continue();

        },
        function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
    } else if (data.type && data.type === "segment") {
      if (data.scope === 'USER' || (data.status && data.status === 'UNPUBLISHED')) {
        var segment = $rootScope.segment;
        var ext = segment.ext;
        if (segment.libIds === undefined) segment.libIds = [];
        if (segment.libIds.indexOf($rootScope.igdocument.profile.segmentLibrary.id) == -1) {
          segment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
        }
        SegmentService.save($rootScope.segment).then(function(result) {
            $rootScope.saveError=false;
          var oldLink = SegmentLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.segmentLibrary.children);
          var newLink = SegmentService.getSegmentLink(result);
          SegmentLibrarySvc.updateChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function(link) {
            SegmentService.saveNewElements().then(function() {
              SegmentService.merge($rootScope.segmentsMap[result.id], result);
              if (oldLink && oldLink != null) {
                oldLink.ext = newLink.ext;
                oldLink.name = newLink.name;
              }
              $scope.continue();
            }, function(error) {
              $rootScope.msg().text = "Sorry an error occured. Please try again";
              $rootScope.msg().type = "danger";
              $rootScope.msg().show = true;
            });
          }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
          });
        }, function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      } else {
        $rootScope.saveBindingForSegment();
        $scope.continue();
      }


    } else if (data.type && data.type === "datatype") {
      if (data.scope === 'USER' || (data.status && data.status === 'UNPUBLISHED')) {
        var datatype = $rootScope.datatype;
        var ext = datatype.ext;
        var libId = "";
        var children = [];
        DatatypeService.save(datatype).then(function(result) {
          if ($rootScope.libraryDoc && $rootScope.libraryDoc !== null) {
            libId = $rootScope.libraryDoc.datatypeLibrary.id;
            children = $rootScope.libraryDoc.datatypeLibrary.children;

          } else if ($rootScope.igdocument && $rootScope.igdocument !== null) {
            libId = $rootScope.datatypeLibrary.id;
            children = $rootScope.datatypeLibrary.children;
          }
          var oldLink = DatatypeLibrarySvc.findOneChild(result.id, children);
          var newLink = DatatypeService.getDatatypeLink(result);
          newLink.ext = ext;
          DatatypeLibrarySvc.updateChild(libId, newLink).then(function(link) {
            DatatypeService.merge($rootScope.datatypesMap[result.id], result);
            if (oldLink && oldLink != null) {
              oldLink.ext = newLink.ext;
              oldLink.name = newLink.name;
            }
            $scope.continue();

          }, function(error) {
            $rootScope.msg().text = "Sorry an error occured. Please try again";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
          });

        }, function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      } else {
        $rootScope.saveBindingForDatatype();
        $scope.continue();
      }

    } else if (data.type && data.type === "table") {
      if (data.scope === 'USER' || (data.status && data.status === 'UNPUBLISHED')) {
        var table = $rootScope.table;
        var libId = "";
        var children = [];
        var bindingIdentifier = table.bindingIdentifier;
        if ($rootScope.libraryDoc && $rootScope.libraryDoc !== null) {
          libId = $rootScope.libraryDoc.tableLibrary.id;
          children = $rootScope.libraryDoc.tableLibrary.children;

        } else if ($rootScope.igdocument && $rootScope.igdocument !== null) {
          libId = $rootScope.tableLibrary.id;
          children = $rootScope.tableLibrary.children;
        }
        TableService.save(table).then(function(result) {
          var oldLink = TableLibrarySvc.findOneChild(result.id, children);
          TableService.merge($rootScope.tablesMap[result.id], result);
          var newLink = TableService.getTableLink(result);
          newLink.bindingIdentifier = bindingIdentifier;
          TableLibrarySvc.updateChild(libId, newLink).then(function(link) {
            if (oldLink && oldLink != null) oldLink.bindingIdentifier = link.bindingIdentifier;
            $rootScope.msg().text = "tableSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
            $scope.continue();
          }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
          });
        }, function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      } else {
        $rootScope.saveBindingForValueSet();

      }

    } else if (data.type === "document") {

      IgDocumentService.saveMetadata($rootScope.igdocument.id, $rootScope.metaData).then(function(result) {
        $rootScope.igdocument.metaData = angular.copy($rootScope.metaData);
        $scope.continue();

      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });

    } else if (data.type === "profile") {

      if ($rootScope.igdocument != null && $rootScope.metaData != null) {
        ProfileSvc.saveMetaData($rootScope.igdocument.id, $rootScope.metaData).then(function(result) {
          $scope.continue();
        }, function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      }
    }

  }
});

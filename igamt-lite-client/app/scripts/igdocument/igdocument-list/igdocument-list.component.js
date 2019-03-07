/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('IGDocumentListCtrl', function (TableService, $scope, $rootScope, $templateCache, Restangular, $http, $filter, $modal, $cookies, $timeout, userInfoService, ToCSvc, ContextMenuSvc, ProfileAccessSvc, ngTreetableParams, $interval, ViewSettings, StorageService, $q, Notification, DatatypeService, SegmentService, PcLibraryService, IgDocumentService, ElementUtils, AutoSaveService, DatatypeLibrarySvc, SegmentLibrarySvc, TableLibrarySvc, MastermapSvc, MessageService, FilteringSvc, blockUI, PcService, CompositeMessageService, VersionAndUseService, ValidationService, orderByFilter, $mdDialog) {
  $scope.loading = false;
  $scope.loadingReferences=false;
  $scope.tocView = 'views/toc.html';
  $scope.uiGrid = {};
  $rootScope.igs = [];
  $rootScope.currentData = null;
  $rootScope.editForm = $scope.editForm;
  $scope.tmpIgs = [].concat($rootScope.igs);
  $scope.error = null;
  $rootScope.chipsReadOnly = true;
  $scope.loadingTree = false;
  $scope.tocView = 'views/toc.html';
  $scope.print = function (param) {
    //console.log(param);
  }
  $rootScope.versionAndUseMap = {};

  $scope.loading = true;
  $scope.viewSettings = ViewSettings;
  $scope.igDocumentMsg = {};
  $scope.igDocumentConfig = {
    selectedType: null
  };
  $rootScope.usageF = false;
  $scope.nodeReady = true;
  $scope.igDocumentTypes = [{
    name: "My IG Documents",
    type: 'USER'
  },
    {
      name: "Preloaded IG Documents",
      type: 'PRELOADED'
    }, {
      name: "Shared IG Documents",
      type: 'SHARED'
    }, {
      name: "All IG Documents",
      type: "all"
    }
  ];

  $scope.showToc = true;

  $scope.loadingIGDocument = false;
  $scope.toEditIGDocumentId = null;
  $scope.verificationResult = null;
  $scope.verificationError = null;
  $scope.loadingSelection = false;
  $scope.accordi = {
    metaData: false,
    definition: true,
    igList: true,
    igDetails: false,
    active: {list: true, edit: false}
  };
  $rootScope.autoSaving = false;
  //        AutoSaveService.stop();
  $rootScope.saved = false;

  $scope.usageFilter = function () {
    blockUI.start();
    $rootScope.usageF = true;
    $('#treeTable').treetable('collapseAll');
    blockUI.stop();
    return false;

  };

  $scope.tabs = [{active: true}, {active: false}, {active: false}];
  $scope.make_active = function (x) {

    for (i = 0; i < $scope.tabs.length; i++) {
      if (i == x) {
        $scope.tabs[i].active = true;
      } else {
        $scope.tabs[i].active = false;
      }
    }

  };

  $scope.Dndenabled = function () {
    return $scope.igDocumentConfig.selectedType == 'USER';
  }
  $scope.showIgErrorNotification = false;
  $rootScope.showMsgErrorNotification = false;
  $rootScope.showSegErrorNotification = false;
  $rootScope.showDtErrorNotification = false;
  $rootScope.validationMap = {};
  $rootScope.childValidationMap = {};
  $rootScope.validationResult = null;
  $scope.validateIg = function () {
    ValidationService.validateIg($rootScope.igdocument).then(function (result) {
      $rootScope.validationMap = {};
      $rootScope.childValidationMap = {};
      $scope.showIgErrorNotification = true;
      $rootScope.showMsgErrorNotification = true;
      $rootScope.showSegErrorNotification = true;
      $rootScope.showDtErrorNotification = true;
      $rootScope.validationResult = result;
      console.log($rootScope.validationResult);
      $rootScope.buildValidationMap($rootScope.validationResult);
      console.log($rootScope.validationMap);
      console.log($rootScope.childValidationMap);


    }, function (error) {
      console.log(error);
    });
  };
  $scope.setIgErrorNotification = function () {
    $scope.showIgErrorNotification = !$scope.showIgErrorNotification;
  };

  $scope.selectIgTab = function (value) {
    if (value === 1) {
      $scope.accordi.igList = false;
      $scope.accordi.igDetails = true;
    } else {
      $scope.selectIGDocumentType('USER');

      $scope.accordi.igList = true;
      $scope.accordi.igDetails = false;
    }
    $scope.make_active(0);
  };

  $scope.segmentsParams = new ngTreetableParams({
    getNodes: function (parent) {
      return SegmentService.getNodes(parent, $rootScope.segment);
    },
    getTemplate: function (node) {
      return SegmentService.getTemplate(node, $rootScope.segment);
    }
  });

  $scope.datatypesParams = new ngTreetableParams({
    getNodes: function (parent) {
      return DatatypeService.getNodes(parent, $rootScope.datatype);
    },
    getTemplate: function (node) {
      return DatatypeService.getTemplate(node, $rootScope.datatype);
    }
  });

  $scope.messagesParams = new ngTreetableParams({
    getNodes: function (parent) {
      console.log($rootScope.messageTree);
      return MessageService.getNodes(parent, $rootScope.messageTree);
    },
    getTemplate: function (node) {
      return MessageService.getTemplate(node, $rootScope.messageTree);
    }
  });

  $rootScope.closeIGDocument = function () {
    $rootScope.clearChanges();
    $rootScope.igdocument = null;
    $rootScope.tocView = null;
    $rootScope.subview = null;

    $rootScope.isEditing = false;
    $scope.selectIgTab(0);
    // $scope.make_active(0);
    $rootScope.initMaps();
    StorageService.setIgDocument(null);
  };

  $scope.getMessageParams = function () {
    return new ngTreetableParams({
      getNodes: function (parent) {
        return MessageService.getNodes(parent, $rootScope.messageTree);
      },
      getTemplate: function (node) {
        return MessageService.getTemplate(node, $rootScope.messageTree);
      }
    });
  };


    /**
   * init the controller
   */
  $scope.initIGDocuments = function () {
    $scope.loadIGDocuments();
    $scope.getScrollbarWidth();
    /**
     * On 'event:loginConfirmed', resend all the 401 requests.
     */
    $scope.$on('event:loginConfirmed', function (event) {
      $scope.loadIGDocuments();
    });

    $rootScope.$on('event:openIGDocumentRequest', function (event, igdocument) {
      $scope.selectIGDocument(igdocument);

    });

    $scope.$on('event:openDatatype', function (event, datatype) {

      $scope.selectDatatype(datatype);

        // Should we open in a dialog ??
    });

    $scope.$on('event:openSegment', function (event, segment) {


        $scope.selectSegment(segment); // Should we open in a dialog ??
    });

    $scope.$on('event:openMessage', function (event, message) {
      $rootScope.messageTree = null;

        $scope.selectMessage(message); // Should we open in a dialog ??
    });
    $scope.$on('event:openPc', function (event) {
      $rootScope.pcTree = null;
      $scope.selectPc(); // Should we open in a dialog ??
    });

    $scope.$on('event:openCP', function (event, cp) {
      $rootScope.cpTree = null;
      $scope.selectCp(cp); // Should we open in a dialog ??
    });

    $scope.$on('event:openTable', function (event, table) {
      $scope.selectTable(table); // Should we open in a dialog ??
    });

    $scope.$on('event:openSection', function (event, section, referencer) {
      $scope.selectSection(section, referencer); // Should we open in a dialog ??
    });

    $scope.$on('event:openValueSetRoot', function (event, section, referencer) {
      $scope.selectValueSetRoot(section); // Should we open in a dialog ??
    });

      $scope.$on('event:openConformanceProfileRoot', function (event, section, referencer) {
          $scope.selectConformanceProfileRoot(section); // Should we open in a dialog ??
      });

    $scope.$on('event:openDocumentMetadata', function (event, metaData) {
      $scope.selectDocumentMetaData(metaData); // Should we open in a dialog ??
    });

    $scope.$on('event:openProfileMetadata', function (event, metaData) {
      $scope.selectProfileMetaData(metaData); // Should we open in a dialog ??
    });

    $rootScope.$on('event:updateIgDate', function (event, dateUpdated) {
      if (!dateUpdated || dateUpdated === null) {
        IgDocumentService.updateDate($rootScope.igdocument);
      } else {
        $rootScope.igdocument.dateUpdated = dateUpdated;
      }
    });


    $rootScope.$on('event:IgsPushed', function (event, igdocument) {

      //                console.log("event:IgsPushed=" + igdocument)
      if ($scope.igDocumentConfig.selectedType === 'USER') {
        var idx = $rootScope.igs.findIndex(function (igd) {
          return igd.id === igdocument.id;
        });
        if (idx > -1) {
          $timeout(function () {
            //                            _.each($rootScope.igs, function (igd) {
            //                                console.log("b msgs=" + igd.metaData.title + " eq=" + (igd === igdocument));
            //                            });
            $rootScope.igs.splice(idx, 1);
            $scope.tmpIgs = [].concat($rootScope.igs);
            //                            _.each($scope.tmpIgs, function (igd) {
            //                                console.log("a msgs=" + igd.metaData.title + " eq=" + (igd === igdocument));
            //                                console.log("msgs=" + igd.metaData.title + " len=" + igd.profile.messages.children.length);
            //                            });
          }, 100);
          $rootScope.igs.push(igdocument);
        } else {
          //                        console.log("pushed=>");
          $rootScope.igs.push(igdocument);
        }
      } else {
        $scope.igDocumentConfig.selectedType = 'USER';
        $scope.loadIGDocuments();
      }
    });

    $rootScope.$on('event:saveAndExecLogout', function (event) {
      if ($rootScope.igdocument != null) {
        if ($rootScope.hasChanges()) {
          $rootScope.openConfirmLeaveDlg().then(function () {
            $rootScope.$emit('event:execLogout');
          });
        } else {
          $rootScope.$emit('event:execLogout');
        }
      } else {
        $rootScope.$emit('event:execLogout');
      }
    });
  };
  $scope.getTemplateRow = function (row) {
    $rootScope.row = row;
    return 'templateRow.html';

  }
  $scope.orderIgs = function (igs) {
    console.log(igs);
    var positionList = [];
    for (i = 0; i < igs.length; i++) {
      igs[i].position = i + 1;
      positionList.push({"id": igs[i].id, "position": igs[i].position});

    }


    IgDocumentService.orderIgDocument(positionList).then(function (response) {
      $rootScope.msg().text = "OrderChanged";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;

    }, function (error) {
      $scope.tmpIgs = angular.copy($scope.IgsCopy);
      $rootScope.msg().text = "OrderChangedFaild";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;

    });
  };

  $scope.selectIGDocumentType = function (selectedType) {
    //console.log("selectIGDocumentType msgs=" + selectedType.metaData.title + " len=" + selectedType.profile.messages.children.length);
    $scope.igDocumentConfig.selectedType = selectedType;
    StorageService.setSelectedIgDocumentType(selectedType);
    $scope.loadIGDocuments();
  };


  $scope.isOwner=function (ig) {

      return ig.accountId === userInfoService.getAccountID();
  };


  $scope.selectIGDocument = function (igdocument) {
    $rootScope.igdocument = igdocument;
    $rootScope.accountId = igdocument.accountId;
      $scope.openIGDocument(igdocument);
  };

  $scope.loadIGDocuments = function () {
    var delay = $q.defer();
    $scope.igDocumentConfig.selectedType = StorageService.getSelectedIgDocumentType() != null ? StorageService.getSelectedIgDocumentType() : 'USER';
    $scope.error = null;
    $rootScope.igs = [];
    $scope.tmpIgs = [].concat($rootScope.igs);
    if (userInfoService.isAuthenticated() && !userInfoService.isPending()) {
      $scope.loading = true;
      StorageService.setSelectedIgDocumentType($scope.igDocumentConfig.selectedType);

      $http.get('api/igdocuments', {params: {"type": $scope.igDocumentConfig.selectedType}}).then(function (response) {
        console.log(response);
        $rootScope.igs = angular.fromJson(response.data);
        $scope.tmpIgs = [].concat($rootScope.igs);

        console.log($scope.tmpIgs);
        for (i = 0; i < $scope.tmpIgs.length; i++) {
          if (!$scope.tmpIgs[i].position || $scope.tmpIgs[i].position == 'undefined' || $scope.tmpIgs[i].position == 'null') {
            $scope.tmpIgs[i].position = i + 1;
          }
        }
        $scope.IgsCopy = angular.copy($scope.tmpIgs);
        $scope.loading = false;
        delay.resolve(true);
      }, function (error) {
        $scope.loading = false;
        $scope.error = error.data;
        delay.reject(false);
      });
    } else {
      delay.reject(false);
    }
    return delay.promise;
  };

  $scope.clone = function (igdocument) {
    $scope.toEditIGDocumentId = igdocument.id;
    $http.post('api/igdocuments/' + igdocument.id + '/clone').then(function (response) {
      $scope.toEditIGDocumentId = null;
      var cloned = angular.fromJson(response.data);

        $scope.igDocumentConfig.selectedType = 'USER';
        $scope.loadIGDocuments();
      // console.log($scope.tabs);
      // $rootScope.msg().text = "igClonedSuccess";
      // $rootScope.msg().type = "success";
      // $rootScope.msg().show = true;
      $scope.afterCloneDone(cloned);
    }, function (error) {
      $scope.toEditIGDocumentId = null;
      $rootScope.msg().text = "igClonedFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
    });
  };


  $scope.afterCloneDone = function (clonedIgDocument) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'AfterClonedIgDlg.html',
      controller: 'AfterClonedIgCtrl',
      escapeToClose: true,
      locals: {
        clonedIgDocument: clonedIgDocument
      }
    });
    modalInstance.then(function (clonedIgDocument) {
      if (clonedIgDocument && clonedIgDocument != null) {
        $scope.edit(clonedIgDocument);
      }
    }, function () {
      $scope.selectIgTab(0);
      $scope.make_active(0);
    });
  };


  $scope.findOne = function (id) {
    for (var i = 0; i < $rootScope.igs.length; i++) {
      if ($rootScope.igs[i].id === id) {
        return $rootScope.igs[i];
      }
    }
    return null;
  };

  var preventChangesLost = function () {
    //            if ($rootScope.hasChanges()) {
    //                if(!confirm("You have unsaved changes, Do you want to stay on the page?")) {
    //                    event.preventDefault();
    //                }
    //            }
  }

  $scope.show = function (igdocument) {
    var process = function () {
      $scope.toEditIGDocumentId = igdocument.id;
      try {
        $scope.openIGDocument(igdocument);
      } catch (e) {
        $rootScope.msg().text = "igInitFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        $scope.loadingIGDocument = false;
        $scope.toEditIGDocumentId = null;
      }
    };

    if ($rootScope.hasChanges()) {
      $rootScope.openConfirmLeaveDlg().then(function () {
        process();
      });
    } else {
      process();
    }
  };

  $scope.ready = false;

  $scope.ready = function () {
    return $scope.ready;
  }

  $scope.finishLoading = function () {

    $scope.loadingTree = false;

    $scope.setReady(true);
  }

  $scope.setReady = function (b) {

    $scope.ready = b;
  }

  // $scope.setFilter = function (b) {
  //
  //   $scope.filtering = b;
  // }
  // $scope.getFilter = function () {
  //
  //   return $scope.filtering;
  // }


  $scope.toggleLoading = function () {
    $scope.loadingTree = true;
  }

  $scope.showLoading = function () {
    return $scope.loadingTree;
  }



  $scope.navToIg = function (igdocument) {
    if ($scope.igDocumentConfig.selectedType === 'USER') {
      $scope.edit(igdocument);
    } else {
      $scope.view(igdocument);
    }
  };


  $scope.edit = function (igdocument) {
    $scope.viewSettings.setTableReadonly(false);
    $scope.tocView = 'views/toc.html';
    $scope.show(igdocument);
  };

  $scope.view = function (igdocument) {
    $scope.viewSettings.setTableReadonly(true);
    $scope.tocView = 'views/tocReadOnly.html';
    $scope.show(igdocument);
  };


  // switcher
  $scope.enabled = true;
  $scope.onOff = true;
  $scope.yesNo = true;
  $scope.disabled = true;

  $scope.changeCallback = function () {
    console.log('This is the state of my model ' + $scope.enabled);
  };


  $scope.orderSectionsByPosition = function (sections) {
    sections = $filter('orderBy')(sections, 'sectionPosition');
    angular.forEach(sections, function (section) {
      if (section.childSections && section.childSections != null && section.childSections.length > 0) {
        section.childSections = $scope.orderSectionsByPosition(section.childSections);
      }
    });
    return sections;
  };

  $scope.orderMesagesByPositon = function (messages) {
    return $filter('orderBy')(messages, 'position');
  };
  $scope.clearIGScope=function(){
      delete $rootScope['igs'];
      delete $rootScope['customIgs'];
      delete $rootScope['preloadedIgs'];
      delete $scope['tmpIgs'];
      delete $scope['IgsCopy'];
  };

  $scope.openIGDocument = function (igdocument) {
      $rootScope.validationResult=null;
      $scope.notifications = [];
      // Find Notifications for this IGDocument
      if (igdocument != null) {
          $http.get('api/notifications/igdocument/' + igdocument.id).then(
              function (response) {
                  $scope.notifications = response.data;
              },
              function (error) {}
          );
          // Set rootscope accountId for sharing
      $rootScope.accountId = igdocument.accountId;
      $timeout(function () {
        $scope.selectIgTab(1);
        $rootScope.subview=null;
        $rootScope.TreeIgs = [];
        $rootScope.TreeIgs.push(igdocument);
        $rootScope.selectedMessagesIDS = [];
        igdocument.childSections = $scope.orderSectionsByPosition(igdocument.childSections);
        igdocument.profile.messages.children = $scope.orderMesagesByPositon(igdocument.profile.messages.children);
        $rootScope.datatypeLibrary = igdocument.profile.datatypeLibrary;
        $rootScope.tableLibrary = igdocument.profile.tableLibrary;
        $rootScope.ext = igdocument.metaData.ext;
        $rootScope.igVersion = igdocument.profile.metaData.hl7Version;
        $rootScope.selectedMessages = angular.copy(igdocument.profile.messages.children);
        $scope.loadingIGDocument = true;
        $rootScope.isEditing = true;
        $rootScope.igdocument = igdocument;
        $scope.loadCm();
        if (igdocument.profile.metaData.hl7Version != undefined || igdocument.profile.metaData.hl7Version != null) {
          $rootScope.hl7Version = igdocument.profile.metaData.hl7Version;
        }
        $rootScope.initMaps();

        $scope.loadSegments().then(function () {
          //$rootScope.filteredSegmentsList=[];
          $scope.loadDatatypes().then(function () {
            $scope.loadVersionAndUseInfo().then(function () {
              $scope.loadTables().then(function () {
               $scope.collectMessages();


                try {
                  if ($scope.messagesParams)
                    $scope.messagesParams.refresh();
                } catch (e) {

                }
                $scope.loadIgDocumentMetaData();

                // Find share participants
                if ($rootScope.igdocument.shareParticipantIds && $rootScope.igdocument.shareParticipantIds.length > 0) {
                  $rootScope.igdocument.shareParticipants = [];
                  $rootScope.igdocument.shareParticipantIds.forEach(function (participant) {
                    $http.get('api/shareparticipant', {params: {id: participant.accountId}})
                      .then(
                        function (response) {
                          response.data.pendingApproval = participant.pendingApproval;
                          response.data.permission = participant.permission;
                          $rootScope.igdocument.shareParticipants.push(response.data);
                        },
                        function (error) {
                        }
                      );
                  });
                }
                $scope.loadPc().then(function () {
                    $scope.clearIGScope();

                }, function () {
                });
              }, function () {
              });
            }, function () {
            });
          }, function () {
          });
        }, function () {
        });
      }, function () {
      });
    }

  };


  $rootScope.getMessagesFromIDS = function (selectedMessagesIDS, ig) {
    $rootScope.selectedMessages = []

  }

  $scope.loadIgDocumentMetaData = function () {
    if (!$rootScope.config || $rootScope.config === null) {
      $http.get('api/igdocuments/config').then(function (response) {
        $rootScope.config = angular.fromJson(response.data);
        $scope.loadingIGDocument = false;
        $scope.toEditIGDocumentId = null;
        $scope.selectDocumentMetaData();
      }, function (error) {
        $scope.loadingIGDocument = false;
        $scope.toEditIGDocumentId = null;
      });
    } else {
      $scope.loadingIGDocument = false;
      $scope.toEditIGDocumentId = null;
      $scope.selectDocumentMetaData();
    }
  };

  $scope.loadDatatypes = function () {
    $rootScope.usingVersionMap = {};
    var delay = $q.defer();
    $rootScope.igdocument.profile.datatypeLibrary.type = "datatypes";
    DatatypeLibrarySvc.getDatatypesByLibrary($rootScope.igdocument.profile.datatypeLibrary.id).then(function (children) {
      $rootScope.datatypes = children;
      $rootScope.datatypesMap = {};
      angular.forEach(children, function (child) {
        this[child.id] = child;
      }, $rootScope.datatypesMap);

      angular.forEach($rootScope.datatypes, function (dt) {
        if (dt.parentVersion) {

          var objectMap = dt.parentVersion + "VV" + dt.hl7Version;
          $rootScope.usingVersionMap[objectMap] = dt;
          console.log($rootScope.usingVersionMap[objectMap]);


        }
      });
      console.log("TESTING MAP");
      console.log($rootScope.usingVersionMap);


      delay.resolve(true);
    }, function (error) {
      $rootScope.msg().text = "DatatypesLoadFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      delay.reject(false);

    });
    return delay.promise;
  };

  $scope.loadVersionAndUseInfo = function () {
    var delay = $q.defer();
    var dtIds = [];
    for (var i = 0; i < $rootScope.datatypeLibrary.children.length; i++) {
      dtIds.push($rootScope.datatypeLibrary.children[i].id);
      //console.log(0)
    }
    VersionAndUseService.findAll().then(function (result) {
      console.log("==========Adding Datatypes from their IDS============");
      //$rootScope.datatypes = result;
      console.log(result);
      angular.forEach(result, function (info) {
        $rootScope.versionAndUseMap[info.id] = info;
      });
      delay.resolve(true);

    }, function (error) {
      $rootScope.msg().text = "DatatypesLoadFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      delay.reject(false);

    });
    return delay.promise;
  };

  $scope.loadSegments = function () {
    var delay = $q.defer();
    $rootScope.igdocument.profile.segmentLibrary.type = "segments";
    SegmentLibrarySvc.getSegmentsByLibrary($rootScope.igdocument.profile.segmentLibrary.id).then(function (children) {
      $rootScope.segments = children;
      $rootScope.segmentsMap = {};
      angular.forEach(children, function (child) {
        this[child.id] = child;
      }, $rootScope.segmentsMap);
      delay.resolve(true);
    }, function (error) {
      $rootScope.msg().text = "SegmentsLoadFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      delay.reject(false);
    });
    return delay.promise;
  };


  $scope.loadPc = function () {
    var delay = $q.defer();
    if ($rootScope.igdocument.profile.profileComponentLibrary) {
      PcLibraryService.getProfileComponentLibrary($rootScope.igdocument.profile.profileComponentLibrary.id).then(function (lib) {
        PcLibraryService.getProfileComponentsByLibrary($rootScope.igdocument.profile.profileComponentLibrary.id).then(function (pcs) {
          console.log("++++++++++++++++++++++++++++++++++");
          console.log(pcs);
          console.log($rootScope.igdocument);
          $rootScope.profileComponentLib = lib
          $rootScope.profileComponents = pcs;
          $rootScope.profileComponentsMap = {};
          angular.forEach(pcs, function (child) {
            this[child.id] = child;
          }, $rootScope.profileComponentsMap);
          delay.resolve(true);
        }, function (error) {
          $rootScope.msg().text = "ProfileComplonentLoadFail";
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          delay.reject(false);
        });
      });
    }
    return delay.promise;
  };
  $scope.loadCm = function () {

    if ($rootScope.igdocument.profile.compositeProfiles) {
      $rootScope.compositeProfiles = $rootScope.igdocument.profile.compositeProfiles.children;
    }
    $rootScope.compositeProfilesStructureMap = {};
    console.log("$rootScope.compositeProfiles");
    console.log($rootScope.compositeProfiles);
    angular.forEach($rootScope.compositeProfiles, function (child) {
      this[child.id] = child;
    }, $rootScope.compositeProfilesStructureMap);
    console.log("compositeProfilesStructureMap");
    console.log($rootScope.compositeProfiles);
    console.log($rootScope.compositeProfilesStructureMap);


  };
  $scope.loadTables = function () {
    var delay = $q.defer();
    $rootScope.igdocument.profile.tableLibrary.type = "tables";


    TableLibrarySvc.getTablesByLibrary($rootScope.igdocument.profile.tableLibrary.id).then(function (children) {
      $rootScope.tables = children;
      $rootScope.tablesMap = {};
      angular.forEach(children, function (child) {
        this[child.id] = child;
      }, $rootScope.tablesMap);
      delay.resolve(true);
    }, function (error) {
      $rootScope.msg().text = "TablesLoadFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      delay.reject(false);
    });
    return delay.promise;

  };


  $scope.loadFilter = function () {
    $rootScope.$emit('event:loadFilter', $rootScope.igdocument);
  };

  $scope.loadMastermap = function () {
    //            $rootScope.$emit('event:loadMastermap', $rootScope.igdocument);
    //            MastermapSvc.parseIg($rootScope.igdocument);
  };


  $scope.collectTables = function () {
    $rootScope.tables = $rootScope.igdocument.profile.tableLibrary.children;
    $rootScope.tablesMap = {};
    angular.forEach($rootScope.igdocument.profile.tableLibrary.children, function (child) {
      this[child.id] = child;
      if (child.displayName) {
        child.label = child.displayName;
      }
      angular.forEach(child.codes, function (code) {
        if (code.displayName) {
          code.label = code.displayName;
        }
      });
    }, $rootScope.tablesMap);
  };

  $scope.collectMessages = function () {
    $rootScope.messagesMap = {};
    $rootScope.messages = $rootScope.igdocument.profile.messages;
    angular.forEach($rootScope.igdocument.profile.messages.children, function (child) {
      if (child != null) {
        this[child.id] = child;
        var cnt = 0;
        //angular.forEach(child.children, function (segmentRefOrGroup) {
          // $rootScope.processElement(segmentRefOrGroup);
        // });
      }
    }, $rootScope.messagesMap);
  };

  $scope.collectData = function (node, segRefOrGroups, segments, datatypes) {
    if (node) {
      if (node.type === 'message') {
        angular.forEach(node.children, function (segmentRefOrGroup) {
          $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
        });
      } else if (node.type === 'group') {
        segRefOrGroups.push(node);
        if (node.children) {
          angular.forEach(node.children, function (segmentRefOrGroup) {
            $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
          });
        }
        segRefOrGroups.push({name: node.name, "type": "end-group"});
      } else if (node.type === 'segment') {
        if (segments.indexOf(node) === -1) {
          segments.push(node);
        }
        angular.forEach(node.fields, function (field) {
          $scope.collectData(field, segRefOrGroups, segments, datatypes);
        });
      } else if (node.type === 'segmentRef') {
        segRefOrGroups.push(node);
        $scope.collectData($rootScope.segmentsMap[node.ref.id], segRefOrGroups, segments, datatypes);
      } else if (node.type === 'component' || node.type === 'subcomponent' || node.type === 'field') {
        $scope.collectData($rootScope.datatypesMap[node.datatype.id], segRefOrGroups, segments, datatypes);
      } else if (node.type === 'datatype') {
        if (datatypes.indexOf(node) === -1) {
          datatypes.push(node);
        }
        if (node.components) {
          angular.forEach(node.children, function (component) {
            $scope.collectData(component, segRefOrGroups, segments, datatypes);
          });
        }
      }
    }
  };

  $scope.confirmDelete = function (igdocument) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'ConfirmIGDocumentDeleteCtrlMd.html',
      controller: 'ConfirmIGDocumentDeleteCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {
        igdocumentToDelete: igdocument
      }

    });
    modalInstance.then(function (igdocument) {

      $scope.igdocumentToDelete = igdocument;
      console.log("DELETING ======");
      console.log($scope.igdocumentToDelete);
      for (i = 0; i < $rootScope.igs.length; i++) {
        if ($rootScope.igs[i].id == $scope.igdocumentToDelete.id) {
          $rootScope.igs.splice(i, 1);
        }

      }
      for (i = 0; i < $scope.tmpIgs.length; i++) {
        if ($scope.tmpIgs[i].id == $scope.igdocumentToDelete.id) {
          $scope.tmpIgs.splice(i, 1);
        }

      }

    });
  };

  $scope.confirmClose = function () {
    var modalInstance = $modal.open({
      templateUrl: 'ConfirmIGDocumentCloseCtrl.html',
      controller: 'ConfirmIGDocumentCloseCtrl'
    });
    modalInstance.result.then(function () {
      $rootScope.clearChanges();
    }, function () {
    });
  };
  $rootScope.deleteProfileComponent = function (pcLibId, profileComponent) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'DeleteProfileComponentCtrl.html',
      controller: 'DeleteProfileComponentCtrl',
      scope: $scope,        // use parent scope in template
      preserveScope: true,
      locals: {
        profileComponentToDelete: profileComponent,

        pcLibId: pcLibId
      }

    });
    modalInstance.then(function (profileComponent) {

    }, function () {
    });
  };
  $rootScope.addMorePcsToCompositeProfile = function (compositeProfile) {
    var createCMInstance = $mdDialog.show({
      templateUrl: 'addMorePcsToCompositeProfile.html',
      controller: 'addMorePcsToCompositeProfileCtrl',
        scope: $rootScope,
        preserveScope: true,

      locals: {
        compositeProfileStructure:compositeProfile
        }


    }).then(function (results) {
      console.log("$rootScope.compositeProfilesStructureMap");
      console.log($rootScope.compositeProfilesStructureMap);
      $rootScope.editCM(results);

    });
  };
  $rootScope.deleteCompositeProfile = function (compositeMessage) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'DeleteCompositeProfileCtrl.html',
      controller: 'DeleteCompositeProfileCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {
        compositeMessageToDelete: compositeMessage
      }

    });
    modalInstance.then(function (compositeMessage) {

    }, function () {
    });

  };
  $rootScope.cantDeleteMsg = function (msg) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'CantDeleteMsgCtrl.html',
      controller: 'CantDeleteMsgCtrl',
      scope: $rootScope,
      preserveScope: true,
      locals: {
        msg: msg
      }

    });
    modalInstance.then(function (msg) {

    }, function () {
    });
  };

  $rootScope.cantDeletePc = function (profileComponent) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'CantDeletePcCtrl.html',
      controller: 'CantDeletePcCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {
        profileComponent: profileComponent
      }


    });
    modalInstance.then(function (profileComponent) {

    }, function () {
    });
  };

  $scope.confirmOpen = function (igdocument) {
    var modalInstance = $modal.open({
      templateUrl: 'ConfirmIGDocumentOpenCtrl.html',
      controller: 'ConfirmIGDocumentOpenCtrl',
      resolve: {
        igdocumentToOpen: function () {
          return igdocument;
        }
      }
    });
    modalInstance.result.then(function (igdocument) {
      $rootScope.clearChanges();
      $scope.openIGDocument(igdocument);
    }, function () {
    });
  };


  $scope.selectMessagesForExport = function (igdocument, toGVT) {
    if ($rootScope.hasChanges()) {
      $rootScope.openConfirmLeaveDlg().then(function (result) {
        if(result&&result==='cancel'){
          $scope.processSelectMessagesForExport(igdocument, toGVT);
        }
      });
    } else {
      $scope.processSelectMessagesForExport(igdocument, toGVT);
    }
  };

  $scope.deleteNotification = function (id) {
      $scope.notifications = null;
      $http.post('api/notifications/' + id + '/delete');
  };

  $scope.selectCompositeProfilesForExport = function (igdocument, toGVT) {
    if ($rootScope.hasChanges()) {
      $rootScope.openConfirmLeaveDlg().result.then(function (result) {
        if(result&&result==='cancel') {

          $rootScope.clearChanges();
          $scope.processSelectCompositeProfilesForExport(igdocument, toGVT);
        }

      });
    } else {
      $scope.processSelectCompositeProfilesForExport(igdocument, toGVT);
    }
  };

  $scope.processSelectMessagesForExport = function (igdocument, toGVT) {
    $mdDialog.show({

      parent: angular.element(document).find('body'),
      templateUrl: 'SelectMessagesForExportCtrlMd.html',
      controller: 'SelectMessagesForExportCtrl',
      locals: {
          igdocumentToSelect: igdocument,
          toGVT: toGVT,
          dynamicMessages:$scope.buildMessagesTable($rootScope.igdocument)

      }
    }).then(function () {

    });
  };

  $scope.buildMessagesTable=function (ig) {
     var ret = [];
     var messages= ig.profile.messages.children;
    for (i=0; i<messages.length; i++) {
        if (messages[i].messageType.toLowerCase() !== "ack") {
            ret.push({
                msgId: messages[i].id,
                autoGenerated: false,
                originId: null,
                name: messages[i].name,
                description: messages[i].description,
                identifier:messages[i].identifier

            });
            if(ig.profile.messages.config && ig.profile.messages.config.ackBinding && ig.profile.messages.config.ackBinding[messages[i].id]){
                var ackId = ig.profile.messages.config.ackBinding[messages[i].id];
                if($rootScope.messagesMap[ackId]){

                    ret.push({
                        msgId: messages[i].id,
                        autoGenerated: true,
                        originId:ackId,
                        name: messages[i].name+"_ACK",
                        description:$rootScope.messagesMap[ackId].description,
                        identifier:messages[i].identifier+"_ACK",
                        event:messages[i].event

                    });
                }
            }
        }else if(messages[i].messageType.toLowerCase() == "ack"){
          if(messages[i].event && messages[i].event.toLowerCase() !== "varies" && messages[i].event!=""){
              ret.push({
                  msgId: messages[i].id,
                  autoGenerated: false,
                  originId: null,
                  name: messages[i].name,
                  description:messages[i].description,
                  identifier:messages[i].identifier
              });
          }
        }
    }
    return ret;
  };


  $scope.processSelectCompositeProfilesForExport = function (igdocument, toGVT) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'SelectCompositeProfilesForExportCtrlMd.html',
      controller: 'SelectCompositeProfilesForExportCtrl',

      locals: {
        igdocumentToSelect: igdocument,
        toGVT: toGVT
      }


    });
    modalInstance.then(function () {
    }, function () {
    });
  };

  $scope.addSegments = function (hl7Version) {

    $mdDialog.show({
      templateUrl: 'AddSegmentMd.html',
      controller: 'AddSegmentDlg',
      parent: angular.element(document).find('body'),
      locals: {
        hl7Version: $scope.hl7Version

      }
    });

  };


  $rootScope.addHL7Table = function (selectedTableLibary, hl7Version) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'AddHL7TableOpenCtrlMd.html',
      controller: 'AddHL7TableOpenCtrl',
      locals: {
        selectedTableLibary: selectedTableLibary,
        hl7Version: hl7Version

      }
    });
    modalInstance.then(function () {
    }, function () {
    });
  };

  $scope.addDatatypes = function (hl7Version) {
    var scopes = ['HL7STANDARD'];

    DatatypeService.getDataTypesByScopesAndVersion(scopes, $scope.hl7Version).then(function (datatypes) {

      $mdDialog.show({
        templateUrl: 'AddHL7DatatypeMd.html',
        parent: angular.element(document).find('body'),
        controller: 'AddDatatypeDlgCtl',
        locals: {

          hl7Version: $scope.hl7Version,

          datatypes: datatypes
        }

      })
    });

  };

  $scope.addMasterDatatype = function () {
    console.log("=========versionwwww=======");
    var scopes = ['MASTER'];

    DatatypeService.getPublishedMaster($rootScope.igdocument.profile.metaData.hl7Version).then(function (result) {
      var addDatatypeInstance = $modal.open({
        templateUrl: 'AddDatatypeDlg.html',
        controller: 'AddDatatypeDlgCtl',
        size: 'lg',
        windowClass: 'flavor-modal-window',
        resolve: {
          hl7Version: function () {
            return $rootScope.igdocument.profile.metaData.hl7Version;
          },
          datatypes: function () {
            console.log("datatypes");
            console.log(result);

            return result;
          }
        }
      }).result.then(function (results) {
        var ids = [];
        angular.forEach(results, function (result) {
          ids.push(result.id);
        });
      });
    });
  };
  $scope.createProfileComponent = function () {

    var createPCInstance = $mdDialog.show({
      templateUrl: 'createProfileComponent.html',
      controller: 'createProfileComponentCtrl',

      locals: {
        // PcLibrary: function() {
        //     return $rootScope.igdocument.profile.profileComponentLibrary;
        // }

      }
    }).then(function (results) {

      $rootScope.editPC(results)
      if ($scope.profileComponentParams)
        $scope.profileComponentParams.refresh();
      if ($scope.applyPcToParams)
        $scope.applyPcToParams.refresh();
    });

  };
  $scope.createCompositeProfile = function () {
    var createCMInstance = $mdDialog.show({
      templateUrl: 'createCompositeProfile.html',
      controller: 'createCompositeProfileCtrl',
      windowClass: 'app-modal-window',

      // windowClass: 'composite-profiles-modal',
      locals: {}
    }).then(function (results) {
      console.log("results");
      console.log(results);
      $rootScope.editCM(results);

    });

  };

  $scope.exportAsMessages = function (id, mids) {
    blockUI.start();
    var form = document.createElement("form");
    form.action = $rootScope.api('api/igdocuments/' + id + '/export/pdf/' + mids);
    form.method = "POST";
    form.target = "_target";
    var csrfInput = document.createElement("input");
    csrfInput.name = "X-XSRF-TOKEN";
    csrfInput.value = $cookies['XSRF-TOKEN'];
    form.appendChild(csrfInput);
    form.style.display = 'none';
    document.body.appendChild(form);
    form.submit();
    blockUI.stop();
  };

  $scope.exportAs = function (format) {


    if ($rootScope.hasChanges()) {

      $rootScope.openConfirmLeaveDlg().then(function () {

        if ($rootScope.igdocument != null) {
          if ($scope.editForm) {
            console.log("Cleeaning");
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $scope.editForm.$invalid = false;

          }
          $rootScope.clearChanges();
          IgDocumentService.exportAs($rootScope.igdocument, format);
        }

      });
    } else if ($rootScope.igdocument != null) {
      IgDocumentService.exportAs($rootScope.igdocument, format);
    }
  };

  $scope.exportAsWithLayout = function (format, layout) {
    if ($rootScope.hasChanges()) {


      $rootScope.openConfirmLeaveDlg().then(function () {

        if ($rootScope.igdocument != null) {
          if ($scope.editForm) {
            console.log("Cleeaning");
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $scope.editForm.$invalid = false;

          }
          $rootScope.clearChanges();

          IgDocumentService.exportAsWithLayout($rootScope.igdocument, format, layout);
        }

      });
    } else if ($rootScope.igdocument != null) {
      IgDocumentService.exportAsWithLayout($rootScope.igdocument, format, layout);
    }
  };

  $scope.exportDelta = function (id, format) {
    blockUI.start();
    var form = document.createElement("form");
    form.action = $rootScope.api('api/igdocuments/' + id + '/delta/' + format);
    form.method = "POST";
    form.target = "_target";
    var csrfInput = document.createElement("input");
    csrfInput.name = "X-XSRF-TOKEN";
    csrfInput.value = $cookies['XSRF-TOKEN'];
    form.appendChild(csrfInput);
    form.style.display = 'none';
    document.body.appendChild(form);
    form.submit();
    blockUI.stop();
  };

  $scope.close = function () {
    if ($rootScope.hasChanges()) {
      $rootScope.openConfirmLeaveDlg().then(function () {
        $rootScope.closeIGDocument();
      });
    } else {
      $rootScope.closeIGDocument();
    }
  };

  $scope.gotoSection = function (obj, type) {
    $rootScope.section['data'] = obj;
    $rootScope.section['type'] = type;
  };

  $scope.exportChanges = function () {
    blockUI.start();
    var form = document.createElement("form");
    form.action = 'api/igdocuments/export/changes';
    form.method = "POST";
    form.target = "_target";
    var input = document.createElement("textarea");
    input.name = "content";
    input.value = angular.fromJson($rootScope.changes);
    form.appendChild(input);
    var csrfInput = document.createElement("input");
    csrfInput.name = "X-XSRF-TOKEN";
    csrfInput.value = $cookies['XSRF-TOKEN'];
    form.appendChild(csrfInput);
    form.style.display = 'none';
    document.body.appendChild(form);
    form.submit();
    blockUI.stop();
  };

  $scope.viewChanges = function (changes) {
    var modalInstance = $modal.open({
      templateUrl: 'ViewIGChangesCtrl.html',
      controller: 'ViewIGChangesCtrl',
      resolve: {
        changes: function () {
          return changes;
        }
      }
    });
    modalInstance.result.then(function (changes) {
      $scope.changes = changes;
    }, function () {
    });
  };


  $scope.reset = function () {
    $rootScope.changes = {};
    $rootScope.closeIGDocument();
  };


  $scope.initIGDocument = function () {
    $scope.loading = true;
    if ($rootScope.igdocument != null && $rootScope.igdocument != undefined)
      $scope.gotoSection($rootScope.igdocument.metaData, 'metaData');
    $scope.loading = false;

  };

  $scope.createGuide = function () {
    $scope.isVersionSelect = true;
  };

  $scope.listHL7Versions = function () {
    var hl7Versions = [];
    $http.get('api/igdocuments/hl7/findVersions', {
      timeout: 60000
    }).then(
      function (response) {
        var len = response.data.length;
        for (var i = 0; i < len; i++) {
          hl7Versions.push(response.data[i]);
        }
      });
    return hl7Versions;
  };

  $scope.showSelected = function (node) {
    $scope.selectedNode = node;
  };


  $scope.clearSegmentScope=function(){
    delete $rootScope["datatype"];
    delete $rootScope["message"];
    delete $rootScope["profileComponent"];
    delete $rootScope["messageTree"];
    delete $rootScope["pcTree"];
    delete $rootScope["cpTree"];
    delete $rootScope["originalCompositeProfileStructure"];
    delete $rootScope["compositeProfileStructure"];
    delete $rootScope["table"];
    delete $rootScope["codeSystems"];
    delete $rootScope["codes"];
    delete $rootScope["section"];
  };

  $scope.selectSegment = function (segment) {
    $rootScope.processElement(segment);
    var startTime = new Date();
    $rootScope.Activate(segment.id);
    if (segment && segment !== null) {
      $scope.loadingSelection = true;
      blockUI.start();
      $timeout(
          function () {
              try {
                  SegmentService.get(segment.id).then(function (result) {
                      $rootScope.segment = angular.copy(result);
                      $rootScope.segment.fields = $filter('orderBy')($rootScope.segment.fields, 'position');
                      $rootScope.currentData = $rootScope.segment;
                      $rootScope.segment.ext = $rootScope.getSegmentExtension($rootScope.segment);
                      $rootScope.segment["type"] = "segment";
                      $rootScope.crossRef = {};
                      $scope.clearSegmentScope();
                      $scope.loadingSelection = false;
                      try {
                          if ($scope.segmentsParams)
                              $scope.segmentsParams.refresh();
                      } catch (e) {

                      }
                      $scope.loadingReferences = true;

                      var crossRefPromise = SegmentService.crossRef($rootScope.segment.id,$rootScope.igdocument.id);
                      crossRefPromise.then (function (result) {
                          $scope.loadingReferences = false;

                          $rootScope.crossRef = result;
                      }, function (error) {
                          $scope.loadingSelection = false;
                          $rootScope.msg().text = error.data.text;
                          $rootScope.msg().type = error.data.type;
                          $rootScope.msg().show = true;
                          blockUI.stop();
                      });

                      if($rootScope.segment.scope === 'USER' && $rootScope.segment.name === 'OBX'){
                          SegmentService.updateDynamicMappingInfo().then (function (dynamicMappingTable) {
                              $rootScope.dynamicMappingTable = dynamicMappingTable;
                              SegmentService.initCoConstraintsTable($rootScope.segment).then (function (coConstraintsTable) {
                                  $rootScope.segment.coConstraintsTable = coConstraintsTable;
                                  SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then (function (coConRowIndexList) {
                                      $rootScope.coConRowIndexList = coConRowIndexList;
                                      $q.all([crossRefPromise]).then (function (result) {
                                          $rootScope.$emit("event:initEditArea");
                                          $rootScope.$emit("event:initSegment");
                                          $rootScope.subview = "EditSegments.html";
                                          $scope.loadingSelection = false;
                                          blockUI.stop();
                                      }, function (error) {
                                          $scope.loadingSelection = false;
                                          $rootScope.msg().text = error.data.text;
                                          $rootScope.msg().type = error.data.type;
                                          $rootScope.msg().show = true;
                                          blockUI.stop();
                                      });
                                  });
                              });
                          });
                      }else {
                          $q.all([crossRefPromise]).then (function (result) {
                              $rootScope.$emit("event:initEditArea");
                              $rootScope.$emit("event:initSegment");
                              $rootScope.subview = "EditSegments.html";
                              $scope.loadingSelection = false;
                              blockUI.stop();
                          }, function (error) {
                              $scope.loadingSelection = false;
                              $rootScope.msg().text = error.data.text;
                              $rootScope.msg().type = error.data.type;
                              $rootScope.msg().show = true;
                              blockUI.stop();
                          });
                      }

                  }, function (error) {
                      $scope.loadingSelection = false;
                      $rootScope.msg().text = error.data.text;
                      $rootScope.msg().type = error.data.type;
                      $rootScope.msg().show = true;
                      blockUI.stop();
                  });
              } catch (e) {
                  $scope.loadingSelection = false;
                  $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                  $rootScope.msg().type = "danger";
                  $rootScope.msg().show = true;
                  blockUI.stop();
              }
          }, 100);
    }
  };

  $scope.selectDocumentMetaData = function () {
    $scope.loadingSelection = true;
    blockUI.start();
    $rootScope.metaData = angular.copy($rootScope.igdocument.metaData);
    $rootScope.currentData = $rootScope.igdocument;
    $timeout(
      function () {
        $scope.loadingSelection = false;
        $rootScope.subview = "EditDocumentMetadata.html";
        $rootScope.$emit("event:initEditArea");
        blockUI.stop();
      }, 100);
  };

  $scope.selectProfileMetaData = function () {
    $rootScope.metaData = angular.copy($rootScope.igdocument.profile.metaData);
    console.log(metaData);
    $rootScope.currentData = $rootScope.igdocument.profile;
    $scope.loadingSelection = true;
    blockUI.start();
    $timeout(
      function () {
        $scope.loadingSelection = false;
        $rootScope.subview = "EditProfileMetadata.html";
        $rootScope.$emit("event:initEditArea");
        blockUI.stop();
      }, 100);
  };

  $scope.clearDatatypeScope=function(){
      delete $rootScope["segment"];
      delete $rootScope["message"];
      delete $rootScope["profileComponent"];
      delete $rootScope["messageTree"];
      delete $rootScope["pcTree"];
      delete $rootScope["cpTree"];
      delete $rootScope["originalCompositeProfileStructure"];
      delete $rootScope["compositeProfileStructure"];
      delete $rootScope["table"];
      delete $rootScope["codeSystems"];
      delete $rootScope["codes"];
      // delete $rootScope["smallCodes"];
      delete $rootScope["section"];

  };


  $scope.selectDatatype = function (datatype) {
      $rootScope.processElement(datatype);
      $scope.clearDatatypeScope();

      for (var prop in $rootScope) {
      if (typeof $rootScope[prop] !== 'function' && prop.indexOf('$') == -1 && prop.indexOf('$$') == -1) {

          console.log(prop);

      }

  };


      $rootScope.Activate(datatype.id);
    if (datatype && datatype != null) {
      $scope.loadingSelection = true;
      blockUI.start();
      $timeout(
        function () {
          try {
            DatatypeService.getOne(datatype.id).then(function (result) {
              $rootScope.datatype = angular.copy(result);
              $rootScope.$emit("event:initDatatype");

              $rootScope.currentData = datatype;

              $rootScope.datatype.ext = $rootScope.getDatatypeExtension($rootScope.datatype);
              $scope.loadingSelection = false;
              $rootScope.datatype["type"] = "datatype";
              $rootScope.tableWidth = null;
              $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
              $rootScope.csWidth = $rootScope.getDynamicWidth(1, 5, 890);
              $rootScope.predWidth = $rootScope.getDynamicWidth(1, 5, 890);
              $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 5, 890);
              $scope.loadingSelection = false;
              try {
                if ($scope.datatypesParams)
                  $scope.datatypesParams.refresh();
              } catch (e) {

              }
              $rootScope.crossRef = null;
                $scope.loadingReferences = true;

              DatatypeService.crossRef($rootScope.datatype.id,$rootScope.igdocument.id).then(function (result) {
                  $scope.loadingReferences = false;
                  $rootScope.crossRef = result;
                console.log($rootScope.crossRef);

              }, function (error) {
                $scope.loadingSelection = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
              });
              $scope.clearDatatypeScope();
              $rootScope.subview = "EditDatatypes.html";
              $rootScope.$emit("event:initEditArea");

              blockUI.stop();
            }, function (error) {
              $scope.loadingSelection = false;
              $rootScope.msg().text = error.data.text;
              $rootScope.msg().type = error.data.type;
              $rootScope.msg().show = true;
              blockUI.stop();
            });
          } catch (e) {
            $scope.loadingSelection = false;
            $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            blockUI.stop();
          }
        }, 100);

      setTimeout(function () {
        $scope.$broadcast('reCalcViewDimensions');
        console.log("refreshed Slider!!");
      }, 1000);
    }
  };

    $scope.clearMessageScope=function(){
        delete $rootScope["datatype"];
        delete $rootScope["segment"];
        delete $rootScope["profileComponent"];
        delete $rootScope["pcTree"];
        delete $rootScope["cpTree"];
        delete $rootScope["originalCompositeProfileStructure"];
        delete $rootScope["compositeProfileStructure"];
        delete $rootScope["table"];
        delete $rootScope["codeSystems"];
        delete $rootScope["codes"];
        // delete $rootScope["smallCodes"];
        delete $rootScope["section"];

    };
  $scope.selectMessage = function (message) {
      $rootScope.processElement(message);

      $rootScope.Activate(message.id);
    $scope.loadingSelection = true;
    blockUI.start();
    $timeout(
      function () {
        try {
          $rootScope.originalMessage = message;
          $rootScope.message = angular.copy(message);

          $rootScope.$emit("event:initMessage");

          $rootScope.currentData = $rootScope.message;
          $rootScope.processMessageTree($rootScope.message);
          $rootScope.tableWidth = null;
          $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
          $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $scope.loadingSelection = false;
          try {
            if ($scope.messagesParams)
              $scope.messagesParams.refresh();
          } catch (e) {

          }
            $scope.clearMessageScope();

          MessageService.crossRef($rootScope.message.id,$rootScope.igdocument.id).then(function (result) {
            $rootScope.crossRef = result;
            console.log("Cross REF Found!!![" + $rootScope.message.id + "][" + $rootScope.igdocument.id + "]");
            console.log($rootScope.crossRef);
          }, function (error) {
            $scope.loadingSelection = false;
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
          });

          $rootScope.subview = "EditMessages.html";
          $rootScope.$emit("event:initEditArea");

          blockUI.stop();
        } catch (e) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          blockUI.stop();
        }
      }, 100);
  };
  var getAppliedProfileComponentsById = function (cp) {
    var result = [];
    for (var i = 0; i < cp.profileComponentIds.length; i++) {
      result.push($rootScope.profileComponentsMap[cp.profileComponentIds[i]]);
    }
    return result;
  }
  var getAppliedCompositeProfilesByIds = function (cpIds) {
    var result = [];

    for (var i = 0; i < cpIds.length; i++) {
      result.push({
        compositeName: $rootScope.compositeProfilesStructureMap[cpIds[i]].name,
        pcs: getAppliedProfileComponentsById($rootScope.compositeProfilesStructureMap[cpIds[i]]),
        coreMessageName: $rootScope.messagesMap[$rootScope.compositeProfilesStructureMap[cpIds[i]].coreProfileId].name
      });
    }
    return result;

  }
  $scope.appliedPcInMsgParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($rootScope.message.compositeProfileStructureList && $rootScope.message.compositeProfileStructureList.length > 0) {

        return getAppliedCompositeProfilesByIds($rootScope.message.compositeProfileStructureList);


      }
    },
    getTemplate: function (node) {
      return 'applyPcToTable';
    }
  });
  $scope.applyPcToParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($rootScope.profileComponent.compositeProfileStructureList && $rootScope.profileComponent.compositeProfileStructureList.length > 0) {

        return getAppliedCompositeProfilesByIds($rootScope.profileComponent.compositeProfileStructureList);


      }
    },
    getTemplate: function (node) {
      return 'applyPcToTable';
    }
  });
  $scope.profileComponentParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($rootScope.igdocument.profile.profileComponentLibrary !== undefined) {

        return orderByFilter($rootScope.profileComponent.children, 'position');

      }
    },
    getTemplate: function (node) {
      return 'profileComponentTable';
    }
  });
    $scope.clearPCScope=function(){
        delete $rootScope["segment"];
        delete $rootScope["message"];
        delete $rootScope["datatype"];
        delete $rootScope["messageTree"];
        delete $rootScope["cpTree"];
        delete $rootScope["originalCompositeProfileStructure"];
        delete $rootScope["compositeProfileStructure"];
        delete $rootScope["table"];
        delete $rootScope["codeSystems"];
        delete $rootScope["codes"];
        // delete $rootScope["smallCodes"];
        delete $rootScope["section"];
    };


  $scope.selectPc = function () {

    $rootScope.Activate($rootScope.profileComponent.id);
    $rootScope.currentData = $rootScope.profileComponent;
    $scope.loadingSelection = true;
    blockUI.start();
    $timeout(
      function () {
        try {
          $rootScope.originalPcLib = $rootScope.igdocument.profile.profileComponentLibrary;
          //$rootScope.profileComponentLib = angular.copy($rootScope.igdocument.profile.profileComponentLibrary);
          $rootScope.currentData = $rootScope.profileComponent;
          //$rootScope.processMessageTree($rootScope.message);
          $rootScope.tableWidth = null;
          $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
          $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $scope.loadingSelection = false;
          $rootScope.subview = "EditProfileComponent.html";
          $rootScope.$emit("event:initEditArea");
            $scope.clearPCScope();
          console.log($scope.editForm);
          //temporary fix
          if ($scope.editForm) {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
          }

          $rootScope.clearChanges();

          //$rootScope.subview = "EditProfileComponent.html";

          try {
            if ($scope.profileComponentParams)
              $scope.profileComponentParams.refresh();
            if ($scope.applyPcToParams)
              $scope.applyPcToParams.refresh();
          } catch (e) {

          }

          PcService.crossRef($rootScope.profileComponent.id,$rootScope.igdocument.id).then(function (result) {
            $rootScope.crossRef = result;
            console.log("Cross REF Found!!![" + $rootScope.profileComponent.id + "][" + $rootScope.igdocument.id + "]");
            console.log($rootScope.crossRef);
          }, function (error) {
            $scope.loadingSelection = false;
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
          });


          blockUI.stop();
        } catch (e) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          blockUI.stop();
        }
      }, 100);
  };
  $scope.compositeMessageParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($rootScope.igdocument.profile.compositeMessages !== undefined) {
        if (parent) {
          if (parent.ref) {
            return parent.ref.fields;
          } else if (parent.datatype) {
            return parent.datatype.components;
          } else if (parent.children) {
            return parent.children
          }

        } else {
          return $rootScope.compositeMessage.children;
        }


      }
    },
    getTemplate: function (node) {
      return 'compositeMessageTable';
    }
  });

  $scope.compositeProfileParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($rootScope.igdocument.profile.compositeProfiles !== undefined) {
        if (parent) {
          if (parent.ref) {
            return parent.ref.fields;
          } else if (parent.datatype) {
            return parent.datatype.components;
          } else if (parent.children) {
            return parent.children
          }
        } else {
          return $rootScope.compositeProfile.children;
        }


      }
    },
    getTemplate: function (node) {
      return 'compositeProfileTable';
    }
  });
  var buildCpDatatype = function (path, segPath, segmentId, type, fieldDatatype, datatype, datatypesMap) {
    datatype.components.sort(function (a, b) {
      return a.position - b.position
    });
    for (var i = 0; i < datatype.components.length; i++) {
      datatype.components[i].path = path + "." + datatype.components[i].position + "[1]";
      datatype.components[i].segmentPath = segPath + "." + datatype.components[i].position;
      datatype.components[i].segment = segmentId;
      if (type === "field") {
        datatype.components[i].fieldDT = datatype.id;
      } else if (type === "component") {
        datatype.components[i].componentDT = datatype.id;
        datatype.components[i].fieldDT = fieldDatatype;

      }
      var dt = angular.copy(datatypesMap[datatype.components[i].datatype.id]);
      if(!dt) {
          console.log("NOT FOUND!!!");
          console.log(datatypesMap);
          console.log($rootScope.compositeProfile.datatypesMap);
          console.log(datatype.components[i].datatype);
      }
      buildCpDatatype(datatype.components[i].path, datatype.components[i].segmentPath, datatype.components[i].segment, "component", fieldDatatype, dt, datatypesMap);
      datatype.components[i].datatype = dt;
    }
  };
  var buildCpSegment = function (path, segment, datatypesMap) {
    segment.fields.sort(function (a, b) {
      return a.position - b.position
    });

    for (var i = 0; i < segment.fields.length; i++) {
      segment.fields[i].path = path + "." + segment.fields[i].position + "[1]";
      segment.fields[i].segmentPath = segment.fields[i].position;
      segment.fields[i].segment = segment.id;
      var dt = angular.copy(datatypesMap[segment.fields[i].datatype.id]);
      buildCpDatatype(segment.fields[i].path, segment.fields[i].segmentPath, segment.fields[i].segment, "field", segment.fields[i].datatype.id, dt, datatypesMap);
      segment.fields[i].datatype = dt;
    }
  };
  var buildCpSegmentRef = function (path, segmentRef, segmentsMap, datatypesMap) {
    var seg = angular.copy(segmentsMap[segmentRef.ref.id]);
    buildCpSegment(path, seg, datatypesMap);
    segmentRef.ref = seg;
  };
  var buildCompositeProfile = function (path, children, segmentsMap, datatypesMap) {
    for (var i = 0; i < children.length; i++) {
      if (path === null) {
        children[i].path = children[i].position + "[1]";

      } else {
        children[i].path = path + "." + children[i].position + "[1]";

      }
      if (children[i].type === "segmentRef") {
        buildCpSegmentRef(children[i].path, children[i], segmentsMap, datatypesMap);
      } else if (children[i].type === "group") {
        buildCompositeProfile(children[i].path, children[i].children, segmentsMap, datatypesMap);
      }
    }
  };
  //


  $rootScope.clickSource = {};
  $scope.selectedHL7Version = "";

  $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();

  $scope.hl7Versions = function (clickSource) {
    $rootScope.clickSource = clickSource;
    if ($rootScope.hasChanges()) {
      $rootScope.openConfirmLeaveDlg().then(function () {
        $rootScope.clearChanges();
        $rootScope.closeIGDocument();
        $rootScope.hl7Versions = [];
        $scope.hl7VersionsInstance();
      });
    } else {
      if (clickSource === 'btn' && $rootScope.igdocument != null) {
        $rootScope.clearChanges();
        $rootScope.closeIGDocument();
      }
      $rootScope.hl7Versions = [];
      $scope.hl7VersionsInstance();
    }
  };

  $scope.confirmOpen = function (igdocument) {
    return $mdDialog.show({
      templateUrl: 'ConfirmIGDocumentOpenCtrl.html',
      controller: 'ConfirmIGDocumentOpenCtrl',
      resolve: {
        igdocumentToOpen: function () {
          return igdocument;
        }
      }
    }).then(function (igdocument) {
      $rootScope.clearChanges();
      $scope.hl7VersionsInstance();
    }, function () {
      console.log("Changes discarded.");
    });
  };
  $scope.hl7VersionsInstance = function () {
    $scope.listHL7Versions().then(function (response) {
      var hl7Versions = [];
      var length = response.data.length;
      for (var i = 0; i < length; i++) {
        hl7Versions.push(response.data[i]);
      }
      return $mdDialog.show({
        templateUrl: 'hl7VersionsDlgMD.html',
        controller: 'HL7VersionsInstanceDlgCtrl',
        scope: $scope,
        preserveScope: true,
        resolve: {
          hl7Versions: function () {
            return hl7Versions;
          },
          hl7Version: function () {
            console.log("$rootScope.clickSource=" + $rootScope.clickSource);
            if ($rootScope.clickSource === "ctx") {
              console.log("hl7Version=" + $rootScope.igdocument.profile.metaData.hl7Version);
              return $rootScope.igdocument.profile.metaData.hl7Version;
            } else {
              return null;
            }
          }
        }
      }).then(function (igdocument) {
        $rootScope
          .$emit(
            'event:openIGDocumentRequest',
            igdocument);
        $rootScope.$broadcast('event:IgsPushed',
          igdocument);
      });
    }, function (response) {
      $rootScope.msg().text = "Cannot load the versions. Please try again";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
    });


  };

  $scope.listHL7Versions = function () {
    return $http.get('api/igdocuments/findVersions', {
      timeout: 60000
    });
  };


  $scope.closedCtxMenu = function (node, $index) {
    console.log("closedCtxMenu");
  };

  $scope.clearCpScope=function () {
      delete $rootScope["segment"];
      delete $rootScope["message"];
      delete $rootScope["profileComponent"];
      delete $rootScope["messageTree"];
      delete $rootScope["table"];
      delete $rootScope["codeSystems"];
      delete $rootScope["codes"];
      // delete $rootScope["smallCodes"];
      delete $rootScope["section"];
  };

  $scope.selectCp = function (cp) {
    $rootScope.originalCompositeProfileStructure = cp;
    $rootScope.compositeProfileStructure = angular.copy(cp);
    // for (var i = 0; i < $rootScope.compositeProfile.children.length; i++) {
    //     $rootScope.compositeProfile.children[i].path = "1[1]";
    // }

    buildCompositeProfile(null, $rootScope.compositeProfile.children, $rootScope.compositeProfile.segmentsMap, $rootScope.compositeProfile.datatypesMap);
    console.log("$rootScope.compositeProfile");

    console.log($rootScope.messages.children);
    console.log($rootScope.profileComponents);
      $scope.clearCpScope();
    $rootScope.currentData = $rootScope.compositeProfileStructure;

    $rootScope.compositeProfile.coreMessageMetaData = {
      name: $rootScope.messagesMap[$rootScope.compositeProfile.coreProfileId].name,
      identifier: $rootScope.messagesMap[$rootScope.compositeProfile.coreProfileId].identifier,
      description: $rootScope.messagesMap[$rootScope.compositeProfile.coreProfileId].description,
      comment: $rootScope.messagesMap[$rootScope.compositeProfile.coreProfileId].comment
    };
    $rootScope.compositeProfile.appliedProfileComponents = [];
    for (var i = 0; i < $rootScope.compositeProfile.profileComponents.length; i++) {
      $rootScope.compositeProfile.appliedProfileComponents.push({
        position: $rootScope.compositeProfile.profileComponents[i].position,
        pc: $rootScope.profileComponentsMap[$rootScope.compositeProfile.profileComponents[i].id],
        pcDate: $rootScope.compositeProfile.profileComponents[i].pcDate
      });
    }
    $rootScope.subview = "EditCompositeProfile.html";
    $scope.loadingSelection = true;
    blockUI.start();
    $timeout(
      function () {
        try {
          $rootScope.tableWidth = null;
          $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
          $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 630);
          $scope.loadingSelection = false;
          try {
            if ($scope.compositeProfileParams)
              $scope.compositeProfileParams.refresh();

          } catch (e) {

          }
          $rootScope.$emit("event:initEditArea");
          blockUI.stop();
        } catch (e) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          blockUI.stop();
        }
      }, 100);


  };

  $scope.codeCompare = function (a, b) {
    if (a.value < b.value)
      return -1;
    if (a.value > b.value)
      return 1;
    return 0;
  };

  $rootScope.isNonEditableValueSet=function(table){
    if(table) return $scope.viewSettings.tableReadonly||table.status == 'PUBLISHED';
    else return true;
  };
  $rootScope.definitionDisabled=function(table){
    return $rootScope.isNonEditableValueSet(table)|| table.extensibility=='Not Defined';
  };
  $rootScope.removeCodeSystem=function(chip){

    angular.forEach($rootScope.table.codes, function (code) {
      if(code.codeSystem===chip){
        console.log("found");
        code.codeSystem=null;
      }
    });

  };

    $scope.clearTableScope=function(){
        delete $rootScope["datatype"];
        delete $rootScope["message"];
        delete $rootScope["profileComponent"];
        delete $rootScope["messageTree"];
        delete $rootScope["pcTree"];
        delete $rootScope["cpTree"];
        delete $rootScope["originalCompositeProfileStructure"];
        delete $rootScope["compositeProfileStructure"];
        delete $rootScope["segment"];
        delete $rootScope["section"];

    };

  $scope.selectTable = function (t) {
    $rootScope.Activate(t.id);
    $rootScope.subview = "EditValueSets.html";
    blockUI.start();

    try {
          TableService.getOneInLibrary(t.id,$rootScope.tableLibrary.id).then(function (tbl) {
              $rootScope.searchObject = {};
              $rootScope.crossRef = {};
              $rootScope.table = angular.copy(tbl);
              $rootScope.$emit("event:initTable");
              $rootScope.currentData = $rootScope.table;
              $rootScope.codeSystems = [];
              $rootScope.codeSystems = $rootScope.table.codeSystems;
              $rootScope.entireTable = angular.copy($rootScope.table);
              $scope.loadingSelection = false;
              $rootScope.$emit("event:initEditArea");
              blockUI.stop();
              $scope.clearTableScope();
              $rootScope.clearChanges();
              $scope.loadingReferences = true;
              TableService.crossRef($rootScope.table, $rootScope.igdocument.id).then(function (result) {
                  $rootScope.crossRef = result;
                  $scope.loadingReferences = false;
              }, function (error) {
                  $scope.loadingSelection = false;
                  $rootScope.msg().text = error.data.text;
                  $rootScope.msg().type = error.data.type;
                  $rootScope.msg().show = true;
              });

          }, function (errr) {
              $scope.loadingSelection = false;
              $rootScope.msg().text = errr.data.text;
              $rootScope.msg().type = errr.data.type;
              $rootScope.msg().show = true;
              blockUI.stop();
          });

    } catch (e) {
      $scope.loadingSelection = false;
      $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      blockUI.stop();
    }
  };

  $scope.selectSection = function (section) {
    if (section.sectionContents === null || section.sectionContents === undefined) {
      section.sectionContents = "";
    }
    $rootScope.subview = "EditSections.html";
    $scope.loadingSelection = true;
    blockUI.start();

    $timeout(
      function () {
        try {
          $rootScope.section = angular.copy(section);
          $rootScope.currentData = $rootScope.section;
          $rootScope.originalSection = section;
          $scope.loadingSelection = false;
          $rootScope.$emit("event:initEditArea");
          blockUI.stop();
        } catch (e) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          blockUI.stop();
        }
      }, 100);
  };


  $scope.selectValueSetRoot = function (section) {
    if (section.sectionContents === null || section.sectionContents === undefined) {
      section.sectionContents = "";
      console.log(section);
    }
    $rootScope.subview = "EditValueSetRoot.html";
    $scope.loadingSelection = true;
    blockUI.start();

    $timeout(
      function () {
        try {
          $rootScope.section = angular.copy(section);
          $rootScope.currentData = $rootScope.section;
          $rootScope.originalSection = section;
          $scope.loadingSelection = false;
          $rootScope.$emit("event:initEditArea");
          $rootScope.$emit("event:initTableLibrarySection");
          blockUI.stop();
        } catch (e) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          blockUI.stop();
        }
      }, 100);
  };

    $scope.selectConformanceProfileRoot = function (section) {
        if (section.sectionContents === null || section.sectionContents === undefined) {
            section.sectionContents = "";
            console.log(section);
        }
        $rootScope.subview = "editConformanceProfileRoot.html";
        $scope.loadingSelection = true;
        blockUI.start();

        $timeout(
            function () {
                try {
                    $rootScope.section = angular.copy(section);
                    $rootScope.currentData = $rootScope.section;
                    $rootScope.originalSection = section;
                    $scope.loadingSelection = false;
                    $rootScope.$emit("event:initEditArea");
                    $rootScope.$emit("event:initTableLibrarySection");
                    blockUI.stop();
                } catch (e) {
                    $scope.loadingSelection = false;
                    $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                    blockUI.stop();
                }
            }, 100);
    };






  $scope.getFullName = function () {
    if (userInfoService.isAuthenticated() === true) {
      return userInfoService.getFullName();
    }
    return '';
  };

  $scope.shareModal = function (igdocument) {
    $http.get('api/usernames').then(function (response) {
      var userList = response.data;
      var filteredUserList = userList.filter(function (user) {
        var isPresent = false;
        if (igdocument.shareParticipants) {
          for (var i = 0; i < igdocument.shareParticipants.length; i++) {
            if (igdocument.shareParticipants[i].id == user.id) {
              isPresent = true;
            }
          }
        }
        if (!isPresent) return user;
      });

      var modalInstance = $mdDialog.show({
        templateUrl: 'ShareIGDocumentModal.html',
        controller: 'ShareIGDocumentCtrl',
        scope: $scope,
        preserveScope: true,
        locals: {
          igdocumentSelected: igdocument
          ,
          userList: _.filter(filteredUserList, function (user) {

            return user.id != igdocument.accountId && igdocument.shareParticipantIds && igdocument.shareParticipantIds != null && igdocument.shareParticipantIds.indexOf(user.id) == -1;
          })

        }

      });
    }, function (error) {

      console.log(error);
    });
  };


  $scope.unshareModal = function (igdocument, shareParticipant) {
    var modalInstance = $modal.open({
      templateUrl: 'ConfirmIGDocumentUnshareCtrl.html',
      controller: 'UnShareIGDocumentCtrl',
      resolve: {
        igdocumentSelected: function () {
          return igdocument;
        },
        shareParticipant: function () {

          return shareParticipant;
        }
      }
    });
  };

  $scope.confirmShareDocument = function (igdocument) {
    $http.get('api/shareconfimation/' + igdocument.id).then(function (response) {
      $rootScope.msg().text = "igSharedConfirmationSuccessful";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $scope.loadIGDocuments();
    }, function (error) {
      $rootScope.msg().text = "igSharedConfirmationFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      console.log(error);
    });
  };

  $scope.rejectShareDocument = function (igdocument) {
    $http.get('api/sharereject/' + igdocument.id).then(function (response) {
      $rootScope.msg().text = "igSharedRejectedSuccessfully";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $scope.loadIGDocuments();
    }, function (error) {
      $rootScope.msg().text = "igSharedRejectFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      console.log(error);
    });
  };

  $scope.customExportModal = function () {
    var modalInstance = $modal.open({
      templateUrl: 'CustomExportModal.html',
      controller: 'CustomExportCtrl',
      resolve: {}
    });
  };

});


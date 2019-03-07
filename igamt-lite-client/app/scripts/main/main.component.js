/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('MainCtrl', ['$document', '$scope', '$rootScope', 'i18n', '$location', 'userInfoService', '$modal', 'Restangular', '$filter', 'base64', '$http', 'Idle', 'IdleService', 'AutoSaveService', 'StorageService', 'ViewSettings', 'DatatypeService', 'SegmentService', 'MessageService', 'ElementUtils', 'SectionSvc', 'VersionAndUseService', '$q', 'DatatypeLibrarySvc', 'CloneDeleteSvc', 'TableService', 'TableLibrarySvc', '$mdDialog','PcService', 'md5','$mdSidenav','SearchService',function($document, $scope, $rootScope, i18n, $location, userInfoService, $modal, Restangular, $filter, base64, $http, Idle, IdleService, AutoSaveService, StorageService, ViewSettings, DatatypeService, SegmentService, MessageService, ElementUtils, SectionSvc, VersionAndUseService, $q, DatatypeLibrarySvc, CloneDeleteSvc, TableService, TableLibrarySvc, $mdDialog,PcService,md5,$mdSidenav,SearchService) {


  $rootScope.goNav = function(path){
    $location.url(path);
  };
  $rootScope.sourceTypes=[{value:"INTERNAL",label:"Internally Managed"}, {value:"EXTERNAL",label:"Externally Managed"}];


  $rootScope.generateHash=function(string){
    var hash = md5.createHash(string);
    var link="//www.gravatar.com/avatar/"+hash+"?s=50&d=retro";
    return link;

  };
  $rootScope.generateHashDebug=function(string){
    var hash = md5.createHash(string);
    console.log("hash")
    console.log(hash);

    return hash;

  }
  $rootScope.getElementUrl=function (element) {
    if(element != null && !element) {
        var base = $location.absUrl().substring(0, $location.absUrl().length - ($location.url().length + 1));
        return base + SearchService.getExportUrl(element, 'html');
    }
    return null;
  };

  $rootScope.getPhinvadsURL=function(table){
    return $rootScope.appInfo.properties["PHINVADS"]+table.oid;
  };
  $rootScope.versionAndUseMap = {};
  $rootScope.validationMap = {};
  userInfoService.loadFromServer();
  $rootScope.loginDialog = null;

  $rootScope.csWidth = null;
  $rootScope.predWidth = null;
  $rootScope.tableWidth = null;
  $rootScope.commentWidth = null;
  $scope.viewSettings = ViewSettings;
  $rootScope.addedSegments = [];
  $rootScope.dateFormat = 'MM/dd/yyyy HH:mm';
  $scope.state = false;

  $scope.toggleState = function() {
    $scope.state = !$scope.state;
  };
  $scope.language = function() {
    return i18n.language;
  };

  $scope.setLanguage = function(lang) {
    i18n.setLanguage(lang);
  };

  $scope.activeWhen = function(value) {
    return value ? 'active' : '';
  };
  $scope.activeIfInList = function(value, pathsList) {
    var found = false;
    if (angular.isArray(pathsList) === false) {
      return '';
    }
    var i = 0;
    while ((i < pathsList.length) && (found === false)) {
      if (pathsList[i] === value) {
        return 'active';
      }
      i++;
    }
    return '';
  };
  $rootScope.setCardinalities = function(obj) {
    if (obj.usage === 'R') {
      obj.min = 1;
    } else if (obj.usage === 'X' || obj.usage === 'BW') {
      obj.min = 0;
      obj.max = 0;
    } else if (obj.usage === 'O') {
      obj.min = 0;

    }

  };
  $rootScope.buildValidationMap = function(validation) {

    if (validation.items) {
      Object.keys(validation.items).forEach(function(key, index) {
        $rootScope.validationMap[key] = validation.items[key];

      });
    }
    if (validation.blocks) {
      Object.keys(validation.blocks).forEach(function(key, index) {
        if ($rootScope.validationMap[key] && angular.isArray($rootScope.validationMap[key])) {
          $rootScope.validationMap[key].push(validation.blocks[key]);


        } else {
          $rootScope.validationMap[key] = validation.blocks[key];


        }
        if (validation.blocks[key].targetId) {
          $rootScope.childValidationMap[validation.blocks[key].targetId] = validation.blocks[key];
        }
        $rootScope.buildValidationMap(validation.blocks[key]);

      });
    }




  };
  $rootScope.errorCount = function(id) {
    if ($rootScope.validationResult && $rootScope.validationResult.targetId === id) {
      return $rootScope.validationResult.errorCount;

    } else if ($rootScope.childValidationMap && $rootScope.childValidationMap[id]) {
      return $rootScope.childValidationMap[id].errorCount;
    }
  };

  $rootScope.showErrorCount = function(id) {
    if (($rootScope.validationResult && $rootScope.validationResult.targetId === id) || ($rootScope.childValidationMap && $rootScope.childValidationMap[id])) {
      return true;
    } else {
      return false;
    }
  };
  $rootScope.hasDatatypeError = function(id) {
    if ($rootScope.validationResult) {

      if (($rootScope.validationMap[id] && $rootScope.validationMap[id].errorCount > 0) || ($rootScope.validationMap[id] && !$rootScope.validationMap[id].errorCount === undefined) || ($rootScope.validationResult.targetId === id && $rootScope.validationResult.errorCount > 0) || ($rootScope.childValidationMap[id] && $rootScope.childValidationMap[id].errorCount !== undefined && $rootScope.childValidationMap[id].errorCount > 0)) {
        return true;
      } else {
        return false;
      }
    }
  };

  $rootScope.hasUsageError = function(id) {
    if ($rootScope.validationResult) {
      if ($rootScope.validationMap[id]) {
        if ($rootScope.validationMap[id].length > 0) {
          for (var item in $rootScope.validationMap[id]) {
            if ($rootScope.validationMap[id][item].validationType && $rootScope.validationMap[id][item].validationType === "USAGE") {
              return 'col-md-1 col-fixed-80 has-validation-error';
            }
          }
        } else {
          if ($rootScope.validationMap[id].validationType && $rootScope.validationMap[id].validationType === "USAGE") {
            return 'col-md-1 col-fixed-80 has-validation-error';
          }
        }

        // return 'col-md-1 col-fixed-80 has-validation-error';
      } else {
        return 'col-md-1 col-fixed-80';
      }
    } else {
      return 'col-md-1 col-fixed-80';
    }

  };

  $scope.noop = function(event){
    event.stopImmediatePropagation();
  };

  $scope.closeSubMenu = function(event){
    // $scope.mdMenu.hide();
  }

  $rootScope.hasConfError = function(id) {

    if ($rootScope.validationResult) {
      if ($rootScope.validationMap[id]) {
        if ($rootScope.validationMap[id].length > 0) {
          for (var item in $rootScope.validationMap[id]) {
            if ($rootScope.validationMap[id][item].validationType && $rootScope.validationMap[id][item].validationType === "CONFLENGTH") {
              return true;
            }
          }
        } else {
          if ($rootScope.validationMap[id].validationType && $rootScope.validationMap[id].validationType === "CONFLENGTH") {
            return true;
          }
        }

        // return 'col-md-1 col-fixed-80 has-validation-error';
      } else {
        return false;
      }
    } else {
      return false;
    }
  };
  $rootScope.hasLengthError = function(id) {

    if ($rootScope.validationResult) {
      if ($rootScope.validationMap[id]) {
        if ($rootScope.validationMap[id].length > 0) {
          for (var item in $rootScope.validationMap[id]) {
            if ($rootScope.validationMap[id][item].validationType && $rootScope.validationMap[id][item].validationType === "LENGTH") {
              return true;
            }
          }
        } else {
          if ($rootScope.validationMap[id].validationType && $rootScope.validationMap[id].validationType === "LENGTH") {
            return true;
          }
        }

        // return 'col-md-1 col-fixed-80 has-validation-error';
      } else {
        return false;
      }
    } else {
      return false;
    }
  };

  $rootScope.hasCardinalityError = function(id) {

    if ($rootScope.validationResult) {
      if ($rootScope.validationMap[id]) {
        if ($rootScope.validationMap[id].length > 0) {
          for (var item in $rootScope.validationMap[id]) {
            if ($rootScope.validationMap[id][item].validationType && $rootScope.validationMap[id][item].validationType === "CARDINALITY") {
              return true;
            }
          }
        } else {
          if ($rootScope.validationMap[id].validationType && $rootScope.validationMap[id].validationType === "CARDINALITY") {
            return true;
          }
        }

        // return 'col-md-1 col-fixed-80 has-validation-error';
      } else {
        return false;
      }
    } else {
      return false;
    }
  };
  $rootScope.hasError = function(id) {

    if ($rootScope.validationResult) {
      if (($rootScope.validationMap[id] && $rootScope.validationMap[id].errorCount > 0) || ($rootScope.validationMap[id] && $rootScope.validationMap[id].errorCount === undefined)) {
        return true;
      } else {
        return false;
      }
    }
  };
  $rootScope.displayMessageError = function(id, type) {
    if ($rootScope.validationResult) {
      if ($rootScope.validationMap[id]) {
        if ($rootScope.validationMap[id].length > 0) {
          for (var item in $rootScope.validationMap[id]) {
            if ($rootScope.validationMap[id][item].validationType && $rootScope.validationMap[id][item].validationType === type) {
              return $rootScope.validationMap[id][item].errorMessage;
            }
          }
        }
        // return 'col-md-1 col-fixed-80 has-validation-error';
      } else {
        return null;
      }
    } else {
      return null;
    }
    // if ($rootScope.validationResult) {
    //     if ($rootScope.validationMap[id]) {
    //         return $rootScope.validationMap[id].errorMessage;
    //     } else {
    //         return null;
    //     }
    // }
  };


  $scope.path = function() {
    return $location.url();
  };

  $scope.login = function() {
    // ////console.log("in login");
    $scope.$emit('event:loginRequest', $scope.username, $scope.password);
  };

  $scope.loginReq = function() {
    // ////console.log("in loginReq");
    if ($rootScope.loginMessage()) {
      $rootScope.loginMessage().text = "";
      $rootScope.loginMessage().show = false;
    }
    $scope.$emit('event:loginRequired');
  };

  $scope.logout = function() {
    if ($rootScope.hasChanges()) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'ConfirmLogout.html',
        controller: 'ConfirmLogoutCtrl',
        escapeToClose: true
      });
      modalInstance.then(function(logout) {
        if(logout) {
          $scope.execLogout();
        }
      }, function() {
      });
    } else {
      $scope.execLogout();
    }
  };

  $scope.execLogout = function() {
    userInfoService.setCurrentUser(null);
    $scope.username = $scope.password = null;
    $scope.$emit('event:logoutRequest');
    StorageService.remove(StorageService.IG_DOCUMENT);
    $rootScope.initMaps();
    $rootScope.igdocument = null;
    AutoSaveService.stop();
    if ($location.path() === '/compare') {
      $location.url('/compare');
    } else {
      $location.url('/ig');
    }
  };

  $scope.cancel = function() {
    $scope.$emit('event:loginCancel');
  };

  $scope.isAuthenticated = function() {
    return userInfoService.isAuthenticated();
  };

  $scope.isPending = function() {
    return userInfoService.isPending();
  };


  $scope.isSupervisor = function() {
    return userInfoService.isSupervisor();
  };

  $scope.isVendor = function() {
    return userInfoService.isAuthorizedVendor();
  };

  $scope.isAuthor = function() {
    return userInfoService.isAuthor();
  };

  $scope.isCustomer = function() {
    return userInfoService.isCustomer();
  };

  $scope.isAdmin = function() {
    return userInfoService.isAdmin();
  };


  $scope.getAccountID = function(accountId) {
    return userInfoService.getAccountID();
  };


  $scope.getRoleAsString = function() {
    if ($scope.isAuthor() === true) {
      return 'author';
    }
    if ($scope.isSupervisor() === true) {
      return 'Supervisor';
    }
    if ($scope.isAdmin() === true) {
      return 'Admin';
    }
    return 'undefined';
  };

  $scope.getUsername = function() {
    if (userInfoService.isAuthenticated() === true) {
      return userInfoService.getUsername();
    }
    return '';
  };

  $rootScope.showLoginDialog = function(username, password) {
    if(!$rootScope.loginOpen){
        $mdDialog.show({
            controller: 'LoginCtrl',
            parent: angular.element(document).find('body'),
            templateUrl: 'views/account/login.html',
            locals: {
                user: { username: $scope.username, password: $scope.password }
            }
        });
    };
  };

  $rootScope.started = false;

  Idle.watch();

  $rootScope.$on('IdleStart', function() {
    closeModals();
    $rootScope.warning = $modal.open({
      templateUrl: 'warning-dialog.html',
      windowClass: 'modal-danger'
    });
  });

  $rootScope.$on('IdleEnd', function() {
    closeModals();
  });

  $rootScope.$on('IdleTimeout', function() {
    closeModals();
    if ($scope.isAuthenticated()) {
      if ($rootScope.igdocument && $rootScope.igdocument != null && $rootScope.hasChanges()) {
        $rootScope.$emit('event:saveAndExecLogout');
      } else {
        $rootScope.$emit('event:execLogout');
      }
    }
    $rootScope.timedout = $modal.open({
      templateUrl: 'timedout-dialog.html',
      windowClass: 'modal-danger'
    });
  });

  $scope.$on('Keepalive', function() {
    if ($scope.isAuthenticated()) {
      IdleService.keepAlive();
    }
  });

  $rootScope.$on('event:execLogout', function() {
    $scope.execLogout();
  });

  function closeModals() {
    if ($rootScope.warning) {
      $rootScope.warning.close();
      $rootScope.warning = null;
    }

    if ($rootScope.timedout) {
      $rootScope.timedout.close();
      $rootScope.timedout = null;
    }
  };

  $rootScope.start = function() {
    closeModals();
    Idle.watch();
    $rootScope.started = true;
  };

  $rootScope.stop = function() {
    closeModals();
    Idle.unwatch();
    $rootScope.started = false;

  };
  $rootScope.setCardinalities = function(obj) {
    if (obj.usage === 'R') {
      obj.min = 1;
    } else if (obj.usage === 'X' || obj.usage === 'BW') {
      obj.min = 0;
      obj.max = 0;
    } else if (obj.usage === 'O') {
      obj.min = 0;

    }

  };


  $scope.checkForIE = function() {
    var BrowserDetect = {
      init: function() {
        this.browser = this.searchString(this.dataBrowser) || 'An unknown browser';
        this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || 'an unknown version';
        this.OS = this.searchString(this.dataOS) || 'an unknown OS';
      },
      searchString: function(data) {
        for (var i = 0; i < data.length; i++) {
          var dataString = data[i].string;
          var dataProp = data[i].prop;
          this.versionSearchString = data[i].versionSearch || data[i].identity;
          if (dataString) {
            if (dataString.indexOf(data[i].subString) !== -1) {
              return data[i].identity;
            }
          } else if (dataProp) {
            return data[i].identity;
          }
        }
      },
      searchVersion: function(dataString) {
        var index = dataString.indexOf(this.versionSearchString);
        if (index === -1) {
          return;
        }
        return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
      },
      dataBrowser: [{
        string: navigator.userAgent,
        subString: 'Chrome',
        identity: 'Chrome'
      }, {
        string: navigator.userAgent,
        subString: 'OmniWeb',
        versionSearch: 'OmniWeb/',
        identity: 'OmniWeb'
      }, {
        string: navigator.vendor,
        subString: 'Apple',
        identity: 'Safari',
        versionSearch: 'Version'
      }, {
        prop: window.opera,
        identity: 'Opera',
        versionSearch: 'Version'
      }, {
        string: navigator.vendor,
        subString: 'iCab',
        identity: 'iCab'
      }, {
        string: navigator.vendor,
        subString: 'KDE',
        identity: 'Konqueror'
      }, {
        string: navigator.userAgent,
        subString: 'Firefox',
        identity: 'Firefox'
      }, {
        string: navigator.vendor,
        subString: 'Camino',
        identity: 'Camino'
      }, { // for newer Netscapes (6+)
        string: navigator.userAgent,
        subString: 'Netscape',
        identity: 'Netscape'
      }, {
        string: navigator.userAgent,
        subString: 'MSIE',
        identity: 'Explorer',
        versionSearch: 'MSIE'
      }, {
        string: navigator.userAgent,
        subString: 'Gecko',
        identity: 'Mozilla',
        versionSearch: 'rv'
      }, { // for older Netscapes (4-)
        string: navigator.userAgent,
        subString: 'Mozilla',
        identity: 'Netscape',
        versionSearch: 'Mozilla'
      }],
      dataOS: [{
        string: navigator.platform,
        subString: 'Win',
        identity: 'Windows'
      }, {
        string: navigator.platform,
        subString: 'Mac',
        identity: 'Mac'
      }, {
        string: navigator.userAgent,
        subString: 'iPhone',
        identity: 'iPhone/iPod'
      }, {
        string: navigator.platform,
        subString: 'Linux',
        identity: 'Linux'
      }]

    };
    BrowserDetect.init();

    if (BrowserDetect.browser === 'Explorer') {
      var title = 'You are using Internet Explorer';
      var msg = 'This site is not yet optimized with Internet Explorer. For the best user experience, please use Chrome, Firefox or Safari. Thank you for your patience.';
      var btns = [
        { result: 'ok', label: 'OK', cssClass: 'btn' }
      ];

      // $dialog.messageBox(title, msg, btns).open();


    }
  };


  $rootScope.readonly = false;
  $rootScope.igdocument = null; // current igdocument
  $rootScope.message = null; // current message
  $rootScope.datatype = null; // current datatype

  $rootScope.pages = ['list', 'edit', 'read'];
  $rootScope.context = { page: $rootScope.pages[0] };
  $rootScope.messagesMap = {}; // Map for Message;key:id, value:object
  $rootScope.segmentsMap = {}; // Map for Segment;key:id, value:object
  $rootScope.datatypesMap = {}; // Map for Datatype; key:id, value:object
  $rootScope.tablesMap = {}; // Map for tables; key:id, value:object
  $rootScope.segments = []; // list of segments of the selected messages
  $rootScope.datatypes = []; // list of datatypes of the selected messages
  $rootScope.segmentPredicates = []; // list of segment level predicates of
  // the selected messages
  $rootScope.segmentConformanceStatements = []; // list of segment level
  // Conformance Statements of
  // the selected messages
  $rootScope.datatypePredicates = []; // list of segment level predicates of
  // the selected messages
  $rootScope.datatypeConformanceStatements = []; // list of segment level
  // Conformance Statements of
  // the selected messages
  $rootScope.tables = []; // list of tables of the selected messages
  $rootScope.postfixCloneTable = 'CA';
  $rootScope.newCodeFakeId = 0;
  $rootScope.newTableFakeId = 0;
  $rootScope.newPredicateFakeId = 0;
  $rootScope.newConformanceStatementFakeId = 0;
  $rootScope.segment = null;
  $rootScope.config = null;
  $rootScope.messagesData = [];
  $rootScope.messages = []; // list of messages
  $rootScope.customIgs = [];
  $rootScope.preloadedIgs = [];
  $rootScope.changes = {};
  $rootScope.generalInfo = { type: null, 'message': null };
  $rootScope.references = []; // collection of element referencing a datatype
  $rootScope.tmpReferences = [];
  // to delete
  $rootScope.section = {};
  $rootScope.conformanceStatementIdList = [];
  $rootScope.parentsMap = {};
  $rootScope.igChanged = false;


  $rootScope.messageTree = null;

  $scope.scrollbarWidth = 0;


  // TODO: remove
  $rootScope.selectIGDocumentTab = function(value) {
    // $rootScope.igdocumentTabs[0] = false;
    // $rootScope.igdocumentTabs[1] = false;
    // $rootScope.igdocumentTabs[2] = false;
    // $rootScope.igdocumentTabs[3] = false;
    // $rootScope.igdocumentTabs[4] = false;
    // $rootScope.igdocumentTabs[5] = false;
    // $rootScope.igdocumentTabs[value] = true;
  };

  $scope.getScrollbarWidth = function() {
    if ($scope.scrollbarWidth == 0) {
      var outer = document.createElement("div");
      outer.style.visibility = "hidden";
      outer.style.width = "100px";
      outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

      document.body.appendChild(outer);

      var widthNoScroll = outer.offsetWidth;
      // force scrollbars
      outer.style.overflow = "scroll";

      // add innerdiv
      var inner = document.createElement("div");
      inner.style.width = "100%";
      outer.appendChild(inner);

      var widthWithScroll = inner.offsetWidth;

      // remove divs
      outer.parentNode.removeChild(outer);

      $scope.scrollbarWidth = widthNoScroll - widthWithScroll;
    }

    return $scope.scrollbarWidth;
  };
  $rootScope.initMaps = function() {
    $rootScope.segment = null;
    $rootScope.datatype = null;
    $rootScope.message = null;
    $rootScope.table = null;
    $rootScope.codeSystems = [];
    $rootScope.messagesMap = {};
    $rootScope.segmentsMap = {};
    $rootScope.datatypesMap = {};
    $rootScope.tablesMap = {};
    $rootScope.segments = [];
    $rootScope.tables = [];
    $rootScope.segmentPredicates = [];
    $rootScope.segmentConformanceStatements = [];
    $rootScope.datatypePredicates = [];
    $rootScope.datatypeConformanceStatements = [];
    $rootScope.datatypes = [];
    $rootScope.messages = [];
    $rootScope.messagesData = [];
    $rootScope.newCodeFakeId = 0;
    $rootScope.newTableFakeId = 0;
    $rootScope.newPredicateFakeId = 0;
    $rootScope.newConformanceStatementFakeId = 0;
    $rootScope.clearChanges();
    $rootScope.parentsMap = [];
    $rootScope.conformanceStatementIdList = [];

    $rootScope.messageTree = null;
  };

  $rootScope.$watch(function() {
    return $location.path();
  }, function(newLocation, oldLocation) {
    $rootScope.setActive(newLocation);
  });

  $rootScope.isPublishedMaster = function(dtLink) {
    DatatypeService.getOneDatatype(dtLink.id).then(function(datatype) {
      console.log("called")
      return datatype.status == "PUBLISHED" && datatype.scope == "MASTER";
    });
  }

  $rootScope.api = function(value) {
    return value;
  };

  $rootScope.updateLength=function(node){
        if(node.minLength&&node.minLength=='NA'){
            node.maxLength='NA';
        }else if(node.maxLength&&node.maxLength=='NA'){
            node.minLength='NA';
        }
  };

  $rootScope.isActive = function(path) {
    return path === $rootScope.activePath;
  };

  $rootScope.setActive = function(path) {
    if (path === '' || path === '/') {
      $location.path('/home');
    } else {
      $rootScope.activePath = path;
    }
  };

  $rootScope.clearChanges = function(path) {
    // $rootScope.changes = {};
    $rootScope.igChanged = false;
  };

  $rootScope.hasChanges = function() {

    // return Object.getOwnPropertyNames($rootScope.changes).length !== 0;
    if($scope.editForm){
      return $scope.editForm.$dirty&&!$scope.editForm.$pristine||$rootScope.igChanged;
    }else{
      return $rootScope.igChanged;
    }
    //return $rootScope.igChanged;
  };

  $rootScope.recordChanged = function() {
    $rootScope.igChanged = true;

  };


  $rootScope.recordChange = function(object, changeType) {
    // var type = object.type;


    // if($rootScope.changes[type] === undefined){
    // $rootScope.changes[type] = {};
    // }

    // if($rootScope.changes[type][object.id] === undefined){
    // $rootScope.changes[type][object.id] = {};
    // }

    // if(changeType === "datatype"){
    // $rootScope.changes[type][object.id][changeType] = object[changeType].id;
    // }else{
    // $rootScope.changes[type][object.id][changeType] = object[changeType];
    // }

    // ////console.log("Change is " + $rootScope.changes[type][object.id][changeType]);
    $rootScope.recordChanged();
  };
  $rootScope.addHL7Table = function(selectedTableLibary, hl7Version) {
    var modalInstance = $modal.open({
      templateUrl: 'AddHL7TableOpenCtrl.html',
      controller: 'AddHL7TableOpenCtrl',
      windowClass: 'conformance-profiles-modal',
      resolve: {
        selectedTableLibary: function() {
          return selectedTableLibary;
        },
        hl7Version: function() {
          return hl7Version;
        }
      }
    });
    modalInstance.result.then(function() {}, function() {});
  };

  $rootScope.recordChange2 = function(type, id, attr, value) {
    // if($rootScope.changes[type] === undefined){
    // $rootScope.changes[type] = {};
    // }
    // if($rootScope.changes[type][id] === undefined){
    // $rootScope.changes[type][id] = {};
    // }
    // if(attr != null) {
    // $rootScope.changes[type][id][attr] = value;
    // }else {
    // $rootScope.changes[type][id] = value;
    // }
    $rootScope.recordChanged();
  };

  $rootScope.recordChangeForEdit = function(object, changeType) {
    // var type = object.type;

    // if($rootScope.changes[type] === undefined){
    // $rootScope.changes[type] = {};
    // }

    // if($rootScope.changes[type]['edit'] === undefined){
    // $rootScope.changes[type]['edit'] = {};
    // }

    // if($rootScope.changes[type]['edit'][object.id] === undefined){
    // $rootScope.changes[type]['edit'][object.id] = {};
    // }
    // $rootScope.changes[type]['edit'][object.id][changeType] = object[changeType];
    $rootScope.recordChanged();
  };

  $rootScope.recordChangeForEdit2 = function(type, command, id, valueType, value) {
    // var obj = $rootScope.findObjectInChanges(type, "add", id);
    // if (obj === undefined) { // not a new object
    // if ($rootScope.changes[type] === undefined) {
    // $rootScope.changes[type] = {};
    // }
    // if ($rootScope.changes[type][command] === undefined) {
    // $rootScope.changes[type][command] = [];
    // }
    // if (valueType !== type) {
    // var obj = $rootScope.findObjectInChanges(type, command, id);
    // if (obj === undefined) {
    // obj = {id: id};
    // $rootScope.changes[type][command].push(obj);
    // }
    // obj[valueType] = value;
    // } else {
    // $rootScope.changes[type][command].push(value);
    // }
    // }
    $rootScope.recordChanged();
  };

  $rootScope.recordDelete = function(type, command, id) {
    //            if (id < 0) { // new object
    //                $rootScope.removeObjectFromChanges(type, "add", id);
    //            } else {
    //                $rootScope.removeObjectFromChanges(type, "edit", id);
    // if ($rootScope.changes[type] === undefined) {
    // $rootScope.changes[type] = {};
    // }
    // if ($rootScope.changes[type][command] === undefined) {
    // $rootScope.changes[type][command] = [];
    // }

    // if ($rootScope.changes[type]["delete"] === undefined) {
    // $rootScope.changes[type]["delete"] = [];
    // }

    // $rootScope.changes[type]["delete"].push({id:id});
    //$rootScope.recordChanged();
    //}

    // if($rootScope.changes[type]) { //clean the changes object
    // if ($rootScope.changes[type]["add"] && $rootScope.changes[type]["add"].length
    // === 0) {
    // delete $rootScope.changes[type]["add"];
    // }
    // if ($rootScope.changes[type]["edit"] &&
    // $rootScope.changes[type]["edit"].length === 0) {
    // delete $rootScope.changes[type]["edit"];
    // }

    // if (Object.getOwnPropertyNames($rootScope.changes[type]).length === 0) {
    // delete $rootScope.changes[type];
    // }
    // }
  };


  $rootScope.findObjectInChanges = function(type, command, id) {
    if ($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
      for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
        var tmp = $rootScope.changes[type][command][i];
        if (tmp.id === id) {
          return tmp;
        }
      }
    }
    return undefined;
  };
  $rootScope.redirectVS = function(valueSet) {
    TableService.getOne(valueSet.id).then(function(valueSet) {
      var modalInstance = $modal.open({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        resolve: {
          destination: function() {
            return valueSet;
          }
        }



      });
      modalInstance.result.then(function() {
        if (!$rootScope.SharingScope) {
          $rootScope.editTable(valueSet);
        } else {
          $rootScope.hideToc = true;
          $scope.hideToc = true;
          $scope.editTable(valueSet);
        }

      });



    });
  };

  $rootScope.isNewObject = function(type, command, id) {
    if ($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
      for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
        var tmp = $rootScope.changes[type][command][i];
        if (tmp.id === id) {
          return true;
        }
      }
    }
    return false;
  };


  $rootScope.removeObjectFromChanges = function(type, command, id) {
    if ($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
      for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
        var tmp = $rootScope.changes[type][command][i];
        if (tmp.id === id) {
          $rootScope.changes[type][command].splice(i, 1);
        }
      }
    }
    return undefined;
  };


  Restangular.setBaseUrl('api/');
  // Restangular.setResponseExtractor(function(response, operation) {
  // return response.data;
  // });

  $rootScope.showError = function(error) {
    var modalInstance = $modal.open({
      templateUrl: 'ErrorDlgDetails.html',
      controller: 'ErrorDetailsCtrl',
      resolve: {
        error: function() {
          return error;
        }
      }
    });
    modalInstance.result.then(function(error) {
      $rootScope.error = error;
    }, function() {});
  };


  $rootScope.apply = function(label) { // FIXME. weak check
    return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
  };

  $rootScope.isFlavor = function(label) { // FIXME. weak check
    return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
  };

  $rootScope.getDatatype = function(id) {
    //console.log("WAAAAAAAAAAA HEREREEEEEEEEEEEE");
    return $rootScope.datatypesMap && $rootScope.datatypesMap[id];
  };

  $rootScope.calNextCSID = function(ext, flavorName) {
    var prefix = '';
    if (ext != null && ext !== '') {
      prefix = ext;
    } else if (flavorName != null && flavorName !== '') {
      prefix = flavorName;
    } else {
      prefix = 'Default';
    }

    return $rootScope.createNewFlavorName(prefix);
  };

  $rootScope.usedSegsLink = [];
  $rootScope.usedDtLink = [];
  $rootScope.usedVsLink = [];
  $rootScope.fillMaps = function(element) {
    if (element != undefined && element != null) {
      if (element.type === "message") {
        for (var i = 0; i < element.children.length; i++) {
          $rootScope.fillMaps(element.children[i]);
        }

        var tableIds = [];
        for (var i = 0; i < element.valueSetBindings.length; i++) {
          tableIds.push(element.valueSetBindings[i].tableId);
        }

        TableService.get(tableIds).then(function(tables) {
          for (var j = 0; j < tables.length; j++) {
            var tempTableLink = {};
            tempTableLink.id = tables[j].id;
            tempTableLink.bindingIdentifier = tables[j].bindingIdentifier;
            $rootScope.usedVsLink.push(tempTableLink);
          }
        });
      } else if (element.type === "segmentRef") {
        $rootScope.usedSegsLink.push(element.ref);
      } else if (element.type === "group" && element.children) {
        for (var i = 0; i < element.children.length; i++) {
          $rootScope.fillMaps(element.children[i]);
        }
      } else if (element.type === "segment") {
        for (var i = 0; i < element.fields.length; i++) {
          $rootScope.fillMaps(element.fields[i]);
        }

        var tableIds = [];
        for (var i = 0; i < element.valueSetBindings.length; i++) {
          tableIds.push(element.valueSetBindings[i].tableId);
        }

        TableService.get(tableIds).then(function(tables) {
          for (var j = 0; j < tables.length; j++) {
            var tempTableLink = {};
            tempTableLink.id = tables[j].id;
            tempTableLink.bindingIdentifier = tables[j].bindingIdentifier;
            $rootScope.usedVsLink.push(tempTableLink);
          }
        });
      } else if (element.type === "field") {
        $rootScope.usedDtLink.push(element.datatype);
      } else if (element.type === "component") {
        $rootScope.usedDtLink.push(element.datatype);
      } else if (element.type === "datatype") {
        for (var i = 0; i < element.components.length; i++) {
          $rootScope.fillMaps(element.components[i]);
        }

        var tableIds = [];
        for (var i = 0; i < element.valueSetBindings.length; i++) {
          tableIds.push(element.valueSetBindings[i].tableId);
        }

        TableService.get(tableIds).then(function(tables) {
          for (var j = 0; j < tables.length; j++) {
            var tempTableLink = {};
            tempTableLink.id = tables[j].id;
            tempTableLink.bindingIdentifier = tables[j].bindingIdentifier;
            $rootScope.usedVsLink.push(tempTableLink);
          }
        });
      }
    }
  };



  $rootScope.processElement = function(element, parent) {
    try {
      if (element != undefined && element != null) {
        if (element.type === "message") {
          element.children = $filter('orderBy')(element.children, 'position');
          angular.forEach(element.conformanceStatements, function(cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
          });
          angular.forEach(element.children, function(segmentRefOrGroup) {
            $rootScope.processElement(segmentRefOrGroup, element);
          });
        } else if (element.type === "group" && element.children) {
          if (parent) {
            $rootScope.parentsMap[element.id] = parent;
          }
          element.children = $filter('orderBy')(element.children, 'position');
          angular.forEach(element.children, function(segmentRefOrGroup) {
            $rootScope.processElement(segmentRefOrGroup, element);
          });
        } else if (element.type === "segmentRef") {
          if (parent) {
            $rootScope.parentsMap[element.id] = parent;
          }
          $rootScope.processElement($rootScope.segmentsMap[element.ref.id], element);
        } else if (element.type === "segment") {
          element.fields = $filter('orderBy')(element.fields, 'position');
          angular.forEach(element.conformanceStatements, function(cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
          });
          angular.forEach(element.fields, function(field) {
            $rootScope.processElement(field, element);
          });
        } else if (element.type === "field") {
          $rootScope.parentsMap[element.id] = parent;
          $rootScope.processElement($rootScope.datatypesMap[element.datatype.id], element);
        } else if (element.type === "component") {
            console.log("---");
          console.log(element.name);
          console.log(element.position);
          console.log(parent);
          $rootScope.parentsMap[element.id] = parent;
          $rootScope.processElement($rootScope.datatypesMap[element.datatype.id], element);
        } else if (element.type === "datatype") {
          element.components = $filter('orderBy')(element.components, 'position');
          angular.forEach(element.conformanceStatements, function(cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
          });
          angular.forEach(element.components, function(component) {
            $rootScope.processElement(component, element);
          });
        }
      }
    } catch (e) {
      throw e;
    }
  };

  $rootScope.upgradeOrDowngrade = function(id, datatype, list) {
    $rootScope.selectedDatatypes = [];
    $rootScope.TablesIds = [];
    $rootScope.DTlinksToAdd = [];

    DatatypeService.getOne(id).then(function(sourceParent) {

      console.log(id);
      $scope.DatatypeToAdd = angular.copy(sourceParent);
      $scope.DatatypeToAdd.hl7Version = datatype.hl7Version;


      // $scope.DatatypeToAdd.publicationVersion=0;

      $scope.DatatypeToAdd.valueSetBindings = [];
      DatatypeService.getMergedMaster($scope.DatatypeToAdd).then(function(standard) {
        $scope.DatatypeToAdd = standard;
        $scope.DatatypeToAdd.parentVersion = sourceParent.id;


        $scope.DatatypeToAdd.participants = [];
        $scope.DatatypeToAdd.hl7versions = []
        $scope.DatatypeToAdd.hl7versions.push($scope.DatatypeToAdd.hl7Version);
        $scope.DatatypeToAdd.id = new ObjectId().toString();
        $scope.DatatypeToAdd.libIds = [];
        $rootScope.selectedDatatypes.push($scope.DatatypeToAdd);
        $scope.DatatypeToAdd.participants = [];
        $scope.DatatypeToAdd.libIds = [];
        if ($scope.DatatypeToAdd.components != undefined && $scope.DatatypeToAdd.components != null && $scope.DatatypeToAdd.components.length != 0) {
          for (var i = 0; i < $scope.DatatypeToAdd.components.length; i++) {
            $scope.DatatypeToAdd.components[i].id = new ObjectId().toString();
          }
        }

        var predicates = $scope.DatatypeToAdd['predicates'];
        if (predicates != undefined && predicates != null && predicates.length != 0) {
          angular.forEach(predicates, function(predicate) {
            predicate.id = new ObjectId().toString();
          });
        }

        var conformanceStatements = $scope.DatatypeToAdd['conformanceStatements'];
        if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
          angular.forEach(conformanceStatements, function(conformanceStatement) {
            conformanceStatement.id = new ObjectId().toString();
          });
        }
        if ($scope.DatatypeToAdd.valueSetBindings && $scope.DatatypeToAdd.valueSetBindings.length !== 0) {
          angular.forEach($scope.DatatypeToAdd.valueSetBindings, function(binding) {
            if (binding.tableId && !$rootScope.tablesMap[binding.tableId]) {
              var temp = [];
              temp.push(binding.tableId);
              $rootScope.TablesIds = _.union($rootScope.TablesIds, temp);
            }

          })
        }

        angular.forEach($rootScope.selectedDatatypes, function(dt) {
          $rootScope.processAddedDT(dt);
        });

        DatatypeService.saves($rootScope.selectedDatatypes).then(function(result) {

          DatatypeLibrarySvc.addChildrenFromDatatypes($rootScope.datatypeLibrary.id, $rootScope.selectedDatatypes).then(function(result) {
            angular.forEach(result, function(dtToAdd) {
              console.log(dtToAdd);
              $rootScope.datatypeLibrary.children.push({ name: dtToAdd.name, ext: dtToAdd.ext, id: dtToAdd.id });
              if (dtToAdd.parentVersion) {
                var objectMap = dtToAdd.parentVersion + "VV" + dtToAdd.hl7Version;
                $rootScope.usingVersionMap[objectMap] = dtToAdd;

              }

              $rootScope.datatypesMap[dtToAdd.id] = dtToAdd;
              $rootScope.datatypes.push(dtToAdd);

              $rootScope.processElement(dtToAdd);
            });

            var usedDtId1 = _.map($rootScope.DTlinksToAdd, function(num, key) {
              return num.id;
            });


            TableLibrarySvc.addChildrenByIds($rootScope.tableLibrary.id, $rootScope.TablesIds).then(function(result) {
              console.log(result);
              angular.forEach(result, function(table) {

                if (!$rootScope.tablesMap[table.id]) {
                  $rootScope.tables.push(table);
                  $rootScope.tablesMap[table.id] = table;
                }
              });
              var objectMap = id + "VV" + datatype.hl7Version;

              $rootScope.replaceElement($rootScope.datatype, $rootScope.usingVersionMap[objectMap], list);


            });
          });
        });
      });

    });



  };






  $rootScope.processAddedDT = function(datatype) {
    if (!$rootScope.datatypesMap[datatype.id]) {
      $rootScope.DTlinksToAdd.push({
        id: datatype.id,
        name: datatype.name,
        ext: datatype.ext
      });
    }
    if (datatype.components && datatype.components.length != 0) {
      angular.forEach(datatype.components, function(component) {
        if (component.datatype) {

          $rootScope.processAddedDT(component.datatype);
        }

      });
    }
    console.log("DEBUG");
    console.log(datatype);
    if (datatype.valueSetBindings && datatype.valueSetBindings.length > 0) {
      angular.forEach(datatype.valueSetBindings, function(binding) {
        if (binding.tableId && !$rootScope.tablesMap[binding.tableId]) {
          var temp = [];
          temp.push(binding.tableId);
          $rootScope.TablesIds = _.union($rootScope.TablesIds, temp);
        }

      })
    }
  };


  $rootScope.processList = function() {


    angular.forEach($rootScope.selectedDatatypes, function(dt) {
      $rootScope.processAddedDT(dt);
    });

    DatatypeService.saves($rootScope.selectedDatatypes).then(function(result) {




      for (var i = 0; i < result.length; i++) {
        if (!$rootScope.datatypesMap[result[i].id]) {
          $rootScope.datatypesMap[result[i].id] = result[i];
          $rootScope.datatypes.push(result[i]);
        }
      }

      DatatypeLibrarySvc.addChildren($rootScope.datatypeLibrary.id, $rootScope.DTlinksToAdd).then(function(link) {
        $rootScope.datatypeLibrary.children.push(link);
        var usedDtId1 = _.map($rootScope.DTlinksToAdd, function(num, key) {
          return num.id;
        });

        DatatypeService.get(usedDtId1).then(function(datatypes) {
          angular.forEach(datatypes, function(datatype) {
            if (!$rootScope.datatypesMap[datatype.id]) {
              $rootScope.datatypesMap[datatype.id] = datatype;
              $rootScope.datatypes.push(datatype);
              if (datatype.parentVersion) {
                $rootScope.datatypesMap[datatype.parentVersion] = datatype;
              }
            }
          })
          TableLibrarySvc.addChildrenByIds($rootScope.tableLibrary.id, $rootScope.TablesIds).then(function(result) {
            console.log(result);
            angular.forEach(result, function(table) {

              if (!$rootScope.tablesMap[table.id]) {
                $rootScope.tables.push(table);
                $rootScope.tablesMap[table.id] = table;

              }



            });

          });
        });
      });
    });
  };






  $rootScope.processMessageTree = function(element, parent) {
    try {
      if (element != undefined && element != null) {
        if (element.type === "message") {
          $rootScope.selectedMessage = element;
          var m = {};
          m.children = [];
          $rootScope.messageTree = m;
          angular.forEach(element.children, function(segmentRefOrGroup) {
            $rootScope.processMessageTree(segmentRefOrGroup, m);
          });

        } else if (element.type === "group" && element.children) {
          var g = {};
            g.path = element.position + "[1]";
          g.locationPath = element.name.substr(element.name.lastIndexOf('.') + 1) + '[1]';
          g.obj = element;
          g.children = [];
          if (parent.path) {
            g.path = parent.path + "." + g.path;
            g.locationPath = parent.locationPath + "." + g.locationPath;
          }
          parent.children.push(g);
          angular.forEach(element.children, function(segmentRefOrGroup) {
            $rootScope.processMessageTree(segmentRefOrGroup, g);
          });
        } else if (element.type === "segmentRef") {
          var s = {};
          s.path = element.position + "[1]";
          s.locationPath = $rootScope.segmentsMap[element.ref.id].name + '[1]';
          s.obj = element;
          s.children = [];
          if (parent.path) {
            s.path = parent.path + "." + element.position + "[1]";
            s.locationPath = parent.locationPath + "." + s.locationPath;
          }

          if ($rootScope.segmentsMap[s.obj.ref.id] == undefined) {
            throw new Error("Cannot find Segment[id=" + s.obj.ref.id + ", name= " + s.obj.ref.name + "]");
          }
          s.obj.ref.ext = $rootScope.segmentsMap[s.obj.ref.id].ext;
          s.obj.ref.label = $rootScope.getLabel(s.obj.ref.name, s.obj.ref.ext);
          parent.children.push(s);

          var ref = $rootScope.segmentsMap[element.ref.id];
          $rootScope.processMessageTree(ref, s);

        } else if (element.type === "segment") {
          if (!parent) {
            var s = {};
            s.obj = element;
            s.path = element.name;
            s.locationPath = element.name;
            s.children = [];
            parent = s;
          }
          angular.forEach(element.fields, function(field) {
            $rootScope.processMessageTree(field, parent);
          });
        } else if (element.type === "field") {
          var f = {};
          f.obj = element;
          f.path = parent.path + "." + element.position + "[1]";
          f.segmentPath = '' + element.position;
          f.segment = parent.obj.ref.id;
          f.locationPath = parent.locationPath + "." + element.position + "[1]";

          if ($rootScope.message) {
            f.sev = _.find($rootScope.message.singleElementValues, function(sev) { return sev.location == $rootScope.refinePath(f.path); });
            if (f.sev) {
              f.sev.from = 'message';
            } else {
              f.sev = _.find($rootScope.segmentsMap[f.segment].singleElementValues, function(sev) { return sev.location == f.segmentPath; });
              if (f.sev) f.sev.from = 'segment';
            }
          }

          f.children = [];
          var d = $rootScope.datatypesMap[f.obj.datatype.id];
          if (d === undefined) {
            throw new Error("Cannot find Data Type[id=" + f.obj.datatype.id + ", name= " + f.obj.datatype.name + "]");
          }
          f.obj.datatype.ext = $rootScope.datatypesMap[f.obj.datatype.id].ext;
          f.obj.datatype.label = $rootScope.getLabel(f.obj.datatype.name, f.obj.datatype.ext);
          parent.children.push(f);

          $rootScope.processMessageTree($rootScope.datatypesMap[element.datatype.id], f);
        } else if (element.type === "component") {
          var c = {};

          c.obj = element;
          c.path = parent.path + "." + element.position + "[1]";
          c.segmentPath = parent.segmentPath + "." + element.position;
          c.segment = parent.segment;
          if (c.segmentPath.split(".").length - 1 == 1) {
            c.fieldDT = parent.obj.datatype.id;
            if ($rootScope.message) {
              c.sev = _.find($rootScope.message.singleElementValues, function(sev) { return sev.location == $rootScope.refinePath(c.path); });
              if (c.sev) {
                c.sev.from = 'message';
              } else {
                c.sev = _.find($rootScope.segmentsMap[c.segment].singleElementValues, function(sev) { return sev.location == c.segmentPath; });
                if (c.sev) {
                  c.sev.from = 'segment';
                } else {
                  var fieldPath = c.segmentPath.substr(c.segmentPath.indexOf('.') + 1);
                  c.sev = _.find($rootScope.datatypesMap[c.fieldDT].singleElementValues, function(sev) { return sev.location == fieldPath; });
                  if (c.sev) {
                    c.sev.from = 'field';
                  }
                }
              }
            }
          } else if (c.segmentPath.split(".").length - 1 == 2) {
            c.fieldDT = parent.fieldDT;
            c.componentDT = parent.obj.datatype.id;
            if ($rootScope.message) {
              c.sev = _.find($rootScope.message.singleElementValues, function(sev) { return sev.location == $rootScope.refinePath(c.path); });
              if (c.sev) {
                c.sev.from = 'message';
              } else {
                c.sev = _.find($rootScope.segmentsMap[c.segment].singleElementValues, function(sev) { return sev.location == c.segmentPath; });
                if (c.sev) {
                  c.sev.from = 'segment';
                } else {
                  var fieldPath = c.segmentPath.substr(c.segmentPath.indexOf('.') + 1);
                  c.sev = _.find($rootScope.datatypesMap[c.fieldDT].singleElementValues, function(sev) { return sev.location == fieldPath; });
                  if (c.sev) {
                    c.sev.from = 'field';
                  } else {
                    var componentPath = c.segmentPath.substr(c.segmentPath.split('.', 2).join('.').length + 1);
                    c.sev = _.find($rootScope.datatypesMap[c.componentDT].singleElementValues, function(sev) { return sev.location == componentPath; });
                    if (c.sev) {
                      c.sev.from = 'component';
                    }
                  }
                }
              }
            }
          }

          c.locationPath = parent.locationPath + "." + element.position + "[1]";
          c.children = [];
          var d = $rootScope.datatypesMap[c.obj.datatype.id];
          if (d === undefined) {
            throw new Error("Cannot find Data Type[id=" + c.obj.datatype.id + ", name= " + c.obj.datatype.name + "]");
          }
          c.obj.datatype.ext = d.ext;
          c.obj.datatype.label = $rootScope.getLabel(c.obj.datatype.name, c.obj.datatype.ext);
          parent.children.push(c);
          $rootScope.processMessageTree($rootScope.datatypesMap[element.datatype.id], c);
        } else if (element.type === "datatype") {
          if (!parent) {
            var d = {};
            d.obj = element;
            d.path = element.name;
            d.locationPath = element.name;
            d.children = [];
            parent = d;
          }
            angular.forEach(element.components, function(component) {
            $rootScope.processMessageTree(component, parent);
          });
        }
      }
    } catch (e) {
      throw e;
    }
  };

  $rootScope.processSegmentsTree = function(element, parent) {
    //console.log(element);

    try {
      if (element.type === "segment") {

        if (!parent) {
          var s = {};
          s.obj = element;
          s.path = element.name;
          s.children = [];
          parent = s;
        }
        element.fields = $filter('orderBy')(element.fields, 'position');

        angular.forEach(element.fields, function(field) {
          $rootScope.processSegmentsTree(field, parent);
        });
      } else if (element.type === "field") {
        var f = {};
        f.obj = element;
        f.path = parent.path + "." + element.position + "[1]";
        f.children = [];
        parent.children.push(f);
          $rootScope.processSegmentsTree($rootScope.datatypesMap[element.datatype.id], f);
      } else if (element.type === "component") {
        var c = {};
        c.obj = element;
        c.path = parent.path + "." + element.position + "[1]";
        c.children = [];
        parent.children.push(c);
        $rootScope.processSegmentsTree($rootScope.datatypesMap[element.datatype.id], c);
      } else if (element.type === "datatype") {

        if (!parent) {
          var d = {};
          d.obj = element;
          d.path = element.name;
          d.children = [];
          parent = d;
        }

        angular.forEach(element.components, function(component) {
          $rootScope.processSegmentsTree(component, parent);
        });
      }

    } catch (e) {
      throw e;
    }
  };

  $rootScope.checkedDatatype = null;

  $rootScope.processDatatypeTree = function(element, parent) {
    ////console.log(element);

    try {
      if (element.type === "datatype") {
        if (!parent) {
          var d = {};
          d.obj = element;
          d.path = element.name;
          d.children = [];
          parent = d;
        }
        ////console.log("IN Data TYPE ")

        angular.forEach(element.components, function(component) {
          $rootScope.processDatatypeTree(component, parent);
        });
      } else if (element.type === "component") {
        var c = {};
        c.obj = element;
        c.path = parent.path + "." + element.position + "[1]";
        c.children = [];
        parent.children.push(c);

        $rootScope.processDatatypeTree($rootScope.datatypesMap[element.datatype.id], c);
      }

    } catch (e) {
      throw e;
    }
  };

  $rootScope.createNewFlavorName = function(label) {
    return label + "_" + (Math.floor(Math.random() * 10000000) + 1);
  };

  $rootScope.createNewExtension = function(ext) {
    if(ext !== null && ext !== "") {
      return ext + "_" + (Math.floor(Math.random() * 10000000) + 1);
    }else {
      return Math.floor(Math.random() * 10000000) + 1;
    }
  };

  $rootScope.isSubComponent = function(node) {
    node.type === 'component' && $rootScope.parentsMap[node.id] && $rootScope.parentsMap[node.id].type === 'component';
  };

  $rootScope.findDatatypeRefs = function(datatype, obj, path, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'field')) {
        if (obj.datatype.id === datatype.id) {
          var found = angular.copy(obj);
          found.path = path;
          found.target = angular.copy(target);
          found.datatypeLink = angular.copy(obj.datatype);
          $rootScope.references.push(found);
        }
        // $rootScope.findDatatypeRefs(datatype, $rootScope.datatypesMap[obj.datatype.id], path, target);
      } else if (angular.equals(obj.type, 'component')) {
        if (obj.datatype.id === datatype.id) {
          var found = angular.copy(obj);
          found.path = path;
          found.target = angular.copy(target);
          found.datatypeLink = angular.copy(obj.datatype);
          $rootScope.references.push(found);
        }
        $rootScope.findDatatypeRefs(datatype, $rootScope.datatypesMap[obj.datatype.id], path, target);
      } else if (angular.equals(obj.type, 'segment')) {
        angular.forEach(obj.fields, function(field) {
          $rootScope.findDatatypeRefs(datatype, field, path + "-" + field.position, target);
        });
      } else if (angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findDatatypeRefs(datatype, component, path + "." + component.position, target);
          });
        }
      }
      else if (angular.equals(obj.type, 'profilecomponent')) {
        if (obj.children) {
          angular.forEach(obj.children, function(subPc) {
            if(subPc.attributes.datatype&&subPc.attributes.datatype.id&&subPc.attributes.datatype&&subPc.attributes.datatype.id===datatype.id){
              var found = angular.copy(obj);
              found.path = path+'.'+subPc.path;
              found.target = angular.copy(target);
              found.datatypeLink = angular.copy(obj.datatype);
              $rootScope.references.push(found);
            }
          });
        }
      }
    }
  };
  $rootScope.findDatatypeRefsForMenu = function(datatype, obj, path, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
        if (obj.datatype.id === datatype.id) {
          var found = angular.copy(obj);
          found.path = path;
          found.target = angular.copy(target);
          found.datatypeLink = angular.copy(obj.datatype);
          $rootScope.referencesForMenu.push(found);
        }
        $rootScope.findDatatypeRefsForMenu(datatype, $rootScope.datatypesMap[obj.datatype.id], path, target);
      } else if (angular.equals(obj.type, 'segment')) {
        angular.forEach(obj.fields, function(field) {
          $rootScope.findDatatypeRefsForMenu(datatype, field, path + "-" + field.position, target);
        });
      } else if (angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findDatatypeRefsForMenu(datatype, component, path + "." + component.position, target);
          });
        }
      }
      else if (angular.equals(obj.type, 'profilecomponent')) {
        if (obj.children) {
          angular.forEach(obj.children, function(subPc) {
            if(subPc.attributes.datatype&&subPc.attributes.datatype.id&&subPc.attributes.datatype&&subPc.attributes.datatype.id===datatype.id){
              var found = angular.copy(obj);
              found.path = path+'.'+subPc.path;
              found.target = angular.copy(target);
              found.datatypeLink = angular.copy(obj.datatype);
              $rootScope.referencesForMenu.push(found);
              console.log($rootScope.referencesForMenu);
            }
          });
        }
      }
    }
  };

  $rootScope.findTempDatatypeRefs = function(datatype, obj, path, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
        if (obj.datatype.id === datatype.id) {
          var found = angular.copy(obj);
          found.path = path;
          found.target = angular.copy(target);
          found.datatypeLink = angular.copy(obj.datatype);
          $rootScope.refsForDelete.push(found);
        }
        $rootScope.findTempDatatypeRefs(datatype, $rootScope.datatypesMap[obj.datatype.id], path, target);
      } else if (angular.equals(obj.type, 'segment')) {
        angular.forEach(obj.fields, function(field) {
          $rootScope.findTempDatatypeRefs(datatype, field, path + "-" + field.position, target);
        });
      } else if (angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findTempDatatypeRefs(datatype, component, path + "." + component.position, target);
          });
        }
      }
    }
  };

  $rootScope.findSegmentRefs = function(segment, obj, path, positionPath, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'message')) {
        angular.forEach(obj.children, function(child) {
          $rootScope.findSegmentRefs(segment, child, obj.name + '-' + obj.identifier, obj.name + '-' + obj.identifier, target);
        });
      } else if (angular.equals(obj.type, 'group')) {
        angular.forEach(obj.children, function(child) {
          var groupNames = obj.name.split(".");
          var groupName = groupNames[groupNames.length - 1];
          $rootScope.findSegmentRefs(segment, child, path + '.' + groupName, positionPath + '.' + obj.position, target);
        });
      } else if (angular.equals(obj.type, 'segmentRef')) {
        if (obj.ref.id === segment.id) {
          var found = angular.copy(obj);
          found.path = path + '.' + segment.name;
          found.positionPath = positionPath + '.' + obj.position;
          found.target = angular.copy(target);
          found.segmentLink = angular.copy(obj.ref);
          $rootScope.references.push(found);
        }
      }
    }
  };

  $rootScope.findSegmentRefsForMenu = function(segment, obj, path, positionPath, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'message')) {
        angular.forEach(obj.children, function(child) {
          $rootScope.findSegmentRefsForMenu(segment, child, obj.name + '-' + obj.identifier, obj.name + '-' + obj.identifier, target);
        });
      } else if (angular.equals(obj.type, 'group')) {
        angular.forEach(obj.children, function(child) {
          var groupNames = obj.name.split(".");
          var groupName = groupNames[groupNames.length - 1];
          $rootScope.findSegmentRefsForMenu(segment, child, path + '.' + groupName, positionPath + '.' + obj.position, target);
        });
      } else if (angular.equals(obj.type, 'segmentRef')) {
        if (obj.ref.id === segment.id) {
          var found = angular.copy(obj);
          found.path = path + '.' + segment.name;
          found.positionPath = positionPath + '.' + obj.position;
          found.target = angular.copy(target);
          found.segmentLink = angular.copy(obj.ref);
          $rootScope.referencesForMenu.push(found);
        }
      }
    }
  };

  $rootScope.findValueSetBindings = function() {
    $rootScope.references = [];
    angular.forEach($rootScope.messages.children, function(message) {
      angular.forEach(message.valueSetBindings, function(vsb) {
        if (vsb.tableId == $rootScope.table.id) {
          var found = angular.copy(vsb);
          found.type = 'message';
          found.id = message.id;

          $rootScope.references.push(found);
        }
      });
    });

    angular.forEach($rootScope.segments, function(segment) {
      angular.forEach(segment.valueSetBindings, function(vsb) {
        if (vsb.tableId == $rootScope.table.id) {
          var found = angular.copy(vsb);
          found.type = 'segment';
          found.id = segment.id;

          $rootScope.references.push(found);
        }
      });
    });

    angular.forEach($rootScope.datatypes, function(dt) {
      angular.forEach(dt.valueSetBindings, function(vsb) {
        if (vsb.tableId == $rootScope.table.id) {
          var found = angular.copy(vsb);
          found.type = 'datatype';
          found.id = dt.id;

          $rootScope.references.push(found);
        }
      });
    });

    //Need CoConstraints, Constraints, DynamicMapping
  };

  $rootScope.findTableRefs = function(table, obj, path, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
        if (obj.tables != undefined && obj.tables.length > 0) {
          angular.forEach(obj.tables, function(tableInside) {
            if (tableInside.id === table.id) {
              var found = angular.copy(obj);
              found.path = path;
              found.target = angular.copy(target);
              found.tableLink = angular.copy(tableInside);
              $rootScope.references.push(found);
            }
          });
        }
        // $rootScope.findTableRefs(table, $rootScope.datatypesMap[obj.datatype.id], path);
      } else if (angular.equals(obj.type, 'segment')) {
        angular.forEach(obj.fields, function(field) {
          $rootScope.findTableRefs(table, field, path + "-" + field.position, target);
        });
      } else if (angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findTableRefs(table, component, path + "." + component.position, target);
          });
        }
      }
    }
  };

  $rootScope.findTableRefsForMenu = function(table, obj, path, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
        if (obj.tables != undefined && obj.tables.length > 0) {
          angular.forEach(obj.tables, function(tableInside) {
            if (tableInside.id === table.id) {
              var found = angular.copy(obj);
              found.path = path;
              found.target = angular.copy(target);
              found.tableLink = angular.copy(tableInside);
              $rootScope.referencesForMenu.push(found);
            }
          });
        }
        // $rootScope.findTableRefs(table, $rootScope.datatypesMap[obj.datatype.id], path);
      } else if (angular.equals(obj.type, 'segment')) {
        angular.forEach(obj.fields, function(field) {
          $rootScope.findTableRefsForMenu(table, field, path + "-" + field.position, target);
        });
      } else if (angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findTableRefsForMenu(table, component, path + "." + component.position, target);
          });
        }
      } else if (angular.equals(obj.type, 'profilecomponent')) {
        if (obj.children) {
          angular.forEach(obj.children, function(subPc) {
            if(subPc.valueSetBindings){
              angular.forEach(subPc.valueSetBindings, function(b){
                if(b.tableId===table.id){
                  var found = angular.copy(obj);
                  found.path = path+'.'+subPc.path;
                  found.target = angular.copy(target);
                  found.datatypeLink = angular.copy(obj.datatype);
                  $rootScope.referencesForMenu.push(found);
                  console.log($rootScope.referencesForMenu);
                }
              });

            }
          });
        }
      }
    }
  };



  $rootScope.findTableRefsForDelete = function(table, obj, path, target) {
    if (obj != null && obj != undefined) {
      if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
        if (obj.tables != undefined && obj.tables.length > 0) {
          angular.forEach(obj.tables, function(tableInside) {
            if (tableInside.id === table.id) {
              var found = angular.copy(obj);
              found.path = path;
              found.target = angular.copy(target);
              found.tableLink = angular.copy(tableInside);
              $rootScope.refsForDelete.push(found);
            }
          });
        }
        // $rootScope.findTableRefs(table, $rootScope.datatypesMap[obj.datatype.id], path);
      } else if (angular.equals(obj.type, 'segment')) {
        angular.forEach(obj.fields, function(field) {
          $rootScope.findTableRefsForDelete(table, field, path + "-" + field.position, target);
        });
      } else if (angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findTableRefsForDelete(table, component, path + "." + component.position, target);
          });
        }
      }
    }
  };

  $rootScope.showConfLength = function() {
    //return $rootScope.igVersion > "2.5.1";
    return true;
  };

  $rootScope.refinePath = function(instancePath) {
    var pathArray = [];

    if (instancePath) pathArray = instancePath.split('.');
    var positionPath = '';
    for (var i in pathArray) {
      var position = pathArray[i].split('[')[0];
      positionPath = positionPath + '.' + position;
    }

    if (positionPath != '') positionPath = positionPath.substr(1);
    return positionPath;
  };

  $rootScope.refinePathDebug = function(instancePath) {
    var pathArray = [];

    if (instancePath) pathArray = instancePath.split('.');
    var positionPath = '';
    for (var i in pathArray) {
      var position = pathArray[i].split('[')[0];
      positionPath = positionPath + '.' + position;
    }

    if (positionPath != '') positionPath = positionPath.substr(1);
    console.log("positionPath")

    console.log(positionPath);

    return positionPath;
  };


  $rootScope.saveBindingForSegment = function() {
    var segmentBindingUpdateParameterList = [];

    for (var q = 0; q < $rootScope.references.length; q++) {
      var ref = $rootScope.references[q];
      if (ref.segmentLink.isChanged) {
        ref.segmentLink.isNew = null;
        ref.segmentLink.isChanged = null;
        var segmentBindingUpdateParameter = {};
        segmentBindingUpdateParameter.messageId = ref.target.id;
        segmentBindingUpdateParameter.newSegmentLink = angular.copy(ref.segmentLink);
        segmentBindingUpdateParameter.positionPath = ref.positionPath;
        segmentBindingUpdateParameterList.push(segmentBindingUpdateParameter);

        var message = angular.copy($rootScope.messagesMap[segmentBindingUpdateParameter.messageId]);
        var paths = segmentBindingUpdateParameter.positionPath.split('.');
        $rootScope.updateSegmentBinding(message.children, paths, segmentBindingUpdateParameter.newSegmentLink);

        $rootScope.messagesMap[message.id] = message;
        var oldMessage = _.find($rootScope.igdocument.profile.messages.children, function(msg) {
          return msg.id == message.id;
        });

        var index = $rootScope.igdocument.profile.messages.children.indexOf(oldMessage);
        if (index > -1) $rootScope.igdocument.profile.messages.children[index] = message;

      }
    }

    MessageService.updateSegmentBinding(segmentBindingUpdateParameterList).then(function(result) {}, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

    $rootScope.references = [];
    angular.forEach($rootScope.igdocument.profile.messages.children, function(message) {
      $rootScope.findSegmentRefs($rootScope.segment, message, '', '', message);
    });

  };

  $rootScope.updateSegmentBinding = function(children, paths, newSegmentLink) {
    var position = parseInt(paths[1]);
    var child = $rootScope.findChildByPosition(children, position);

    if (paths.length == 2) {
      if (child.type === "segmentRef") {
        child.ref = newSegmentLink;
      }
    } else {
      $rootScope.updateSegmentBinding(child.children, paths.slice(1), newSegmentLink);
    }
  };

  $rootScope.findChildByPosition = function(children, position) {
    for (var i = 0; i < children.length; i++) {
      if (children[i].position == position) return children[i];
    }
    return null;
  };

  $rootScope.saveBindingForDatatype = function() {
    var datatypeUpdateParameterList = [];
    var segmentUpdateParameterList = [];

    for (var q = 0; q < $rootScope.references.length; q++) {
      var ref = $rootScope.references[q];
      if (ref.datatypeLink.isNew) {
        if (ref.type == 'component') {
          var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
          ref.datatypeLink.isNew = null;
          ref.datatypeLink.isChanged = null;
          var newDatatypeLink = angular.copy(ref.datatypeLink);
          var targetComponent = angular.copy(ref);
          targetComponent.target = null;
          targetComponent.path = null;
          targetComponent.datatypeLink = null;

          var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
            return component.position == targetComponent.position;
          });
          if (toBeUpdateComponent) toBeUpdateComponent.datatype = newDatatypeLink;
          $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
          var oldDatatype = _.find($rootScope.datatypes, function(dt) {
            return dt.id == targetDatatype.id;
          });
          var index = $rootScope.datatypes.indexOf(oldDatatype);
          if (index > -1) $rootScope.datatypes[index] = targetDatatype;

          var datatypeUpdateParameter = {};
          datatypeUpdateParameter.datatypeId = targetDatatype.id;
          datatypeUpdateParameter.componentId = targetComponent.id;
          datatypeUpdateParameter.datatypeLink = newDatatypeLink;
          datatypeUpdateParameterList.push(datatypeUpdateParameter);
        } else if (ref.type == 'field') {
          var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
          ref.datatypeLink.isNew = null;
          ref.datatypeLink.isChanged = null;
          var newDatatypeLink = angular.copy(ref.datatypeLink);
          var targetField = angular.copy(ref);
          targetField.target = null;
          targetField.path = null;
          targetField.datatypeLink = null;

          var toBeUpdateField = _.find(targetSegment.fields, function(field) {
            return field.position == targetField.position;
          });
          if (toBeUpdateField) toBeUpdateField.datatype = newDatatypeLink;
          $rootScope.segmentsMap[targetSegment.id] = targetSegment;
          var oldSegment = _.find($rootScope.segments, function(seg) {
            return seg.id == targetSegment.id;
          });
          var index = $rootScope.segments.indexOf(oldSegment);
          if (index > -1) $rootScope.segments[index] = targetSegment;

          var segmentUpdateParameter = {};
          segmentUpdateParameter.segmentId = targetSegment.id;
          segmentUpdateParameter.fieldId = targetField.id;
          segmentUpdateParameter.datatypeLink = newDatatypeLink;
          segmentUpdateParameterList.push(segmentUpdateParameter);
        }
      } else if (ref.datatypeLink.isChanged) {
        if (ref.type == 'component') {
          var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
          ref.datatypeLink.isNew = null;
          ref.datatypeLink.isChanged = null;
          var newDatatypeLink = angular.copy(ref.datatypeLink);
          var targetComponent = angular.copy(ref);
          targetComponent.target = null;
          targetComponent.path = null;
          targetComponent.datatypeLink = null;

          var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
            return component.position == targetComponent.position;
          });
          if (toBeUpdateComponent) {

            if (toBeUpdateComponent.datatype.id == $rootScope.datatype.id) {
              toBeUpdateComponent.datatype = newDatatypeLink;
            }

          }
          $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
          var oldDatatype = _.find($rootScope.datatypes, function(dt) {
            return dt.id == targetDatatype.id;
          });
          var index = $rootScope.datatypes.indexOf(oldDatatype);
          if (index > -1) $rootScope.datatypes[index] = targetDatatype;

          var datatypeUpdateParameter = {};
          datatypeUpdateParameter.datatypeId = targetDatatype.id;
          datatypeUpdateParameter.componentId = targetComponent.id;
          datatypeUpdateParameter.datatypeLink = newDatatypeLink;
          datatypeUpdateParameter.key = $rootScope.datatype.id;
          datatypeUpdateParameterList.push(datatypeUpdateParameter);
        } else if (ref.type == 'field') {
          var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
          ref.datatypeLink.isNew = null;
          ref.datatypeLink.isChanged = null;
          var newDatatypeLink = angular.copy(ref.datatypeLink);
          var targetField = angular.copy(ref);
          targetField.target = null;
          targetField.path = null;
          targetField.datatypeLink = null;

          var toBeUpdateField = _.find(targetSegment.fields, function(field) {
            return field.position == targetField.position;
          });
          if (toBeUpdateField) {
            if (toBeUpdateField.datatype.id == $rootScope.datatype.id) {
              toBeUpdateField.datatype = newDatatypeLink;
            }

          }
          $rootScope.segmentsMap[targetSegment.id] = targetSegment;
          var oldSegment = _.find($rootScope.segments, function(seg) {
            return seg.id == targetSegment.id;
          });
          var index = $rootScope.segments.indexOf(oldSegment);
          if (index > -1) $rootScope.segments[index] = targetSegment;

          var segmentUpdateParameter = {};
          segmentUpdateParameter.segmentId = targetSegment.id;
          segmentUpdateParameter.fieldId = targetField.id;
          segmentUpdateParameter.datatypeLink = newDatatypeLink;
          segmentUpdateParameter.key = $rootScope.datatype.id;
          segmentUpdateParameterList.push(segmentUpdateParameter);
        }
      }
      console.log("clearing");
      $rootScope.clearChanges();
    }

    SegmentService.updateDatatypeBinding(segmentUpdateParameterList).then(function(result) {}, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

    DatatypeService.updateDatatypeBinding(datatypeUpdateParameterList).then(function(result) {}, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

    $rootScope.references = [];
    angular.forEach($rootScope.segments, function(segment) {
      $rootScope.findDatatypeRefs($rootScope.datatype, segment, $rootScope.getSegmentLabel(segment), segment);
    });
    angular.forEach($rootScope.datatypes, function(dt) {
      $rootScope.findDatatypeRefs($rootScope.datatype, dt, $rootScope.getDatatypeLabel(dt), dt);
    });
  };

  $rootScope.replaceElement = function(source, dest, listOfRefs) {
    $rootScope.SegmentsToUpdate = [];
    $rootScope.datatypeToUpdate = [];
    $rootScope.profileComponentToUpdate = [];
    var newLink = angular.fromJson({
      id: dest.id,
      name: dest.name,
      ext: dest.ext
    });
    var refs = angular.copy(listOfRefs);
    angular.forEach(listOfRefs, function (ref) {
      if (ref.target.status !== "PUBLISHED") {
        if (ref.type == 'field') {
          console.log(ref.target);
          var segment = angular.copy(ref.target);
          angular.forEach(ref.target.fields, function (field) {
            if (field.position == ref.position) {
              field.datatype.id = dest.id;
            }
          });
          $rootScope.SegmentsToUpdate.push(ref.target);

        } else if (ref.type == 'component') {
          angular.forEach(ref.target.components, function (component) {
            if (component.position == ref.position) {
              component.datatype.id = dest.id;
            }
          });
          $rootScope.datatypeToUpdate.push(ref.target);
        } else if (ref.type == 'profilecomponent') {

          angular.forEach(ref.target.children, function (subPc) {

            if (ref.target.name + "." + subPc.path == ref.path) {
              if (subPc.attributes.datatype && subPc.attributes.datatype.id && subPc.attributes.datatype && subPc.attributes.datatype.id === source.id) {
                subPc.attributes.datatype.id = dest.id;
              }
            }

          });
          $rootScope.profileComponentToUpdate.push(ref.target);


        }

        console.log(" refs to update ");
        console.log($rootScope.profileComponentToUpdate);

      }
    });

    SegmentService.saves($rootScope.SegmentsToUpdate).then(function (segs) {
      angular.forEach(segs, function (seg) {
        SegmentService.merge($rootScope.segmentsMap[seg.id], seg);


      });


      DatatypeService.saves($rootScope.datatypeToUpdate).then(function (dts) {

        angular.forEach(dts, function (dt) {
          DatatypeService.merge($rootScope.datatypesMap[dt.id], dt);
        });

        PcService.saveAll($rootScope.profileComponentToUpdate).then(function (pcs) {
          angular.forEach(pcs, function (pc) {
            DatatypeService.merge($rootScope.profileComponentsMap[pc.id], pc);
            $rootScope.editDatatype(dest);

          });
        });
      });


      //
    });
  }




  $rootScope.addOneDatatypeById = function(id) {
    $scope.selectedDatatypes = [];


    DatatypeService.getOneDatatype(id).then(function(datatype) {

      $scope.selectedDatatypes.push(datatype);


      $scope.selectFlv = [];
      var newLinks = [];
      for (var i = 0; i < $scope.selectedDatatypes.length; i++) {

        newLinks.push({
          id: $scope.selectedDatatypes[i].id,
          name: $scope.selectedDatatypes[i].name
        })

      }
      $rootScope.usedDtLink = [];
      $rootScope.usedVsLink = [];
      for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
        $rootScope.fillMaps($scope.selectedDatatypes[i]);
      }
      DatatypeService.saves($scope.selectFlv).then(function(result) {
        for (var i = 0; i < result.length; i++) {
          newLinks.push({
            id: result[i].id,
            name: result[i].name,
            ext: result[i].ext
          })
        }
        DatatypeLibrarySvc.addChildren($rootScope.datatypeLibrary.id, newLinks).then(function(link) {
          for (var i = 0; i < newLinks.length; i++) {
            $rootScope.datatypeLibrary.children.splice(0, 0, newLinks[i]);
          }
          for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
            $rootScope.datatypes.splice(0, 0, $scope.selectedDatatypes[i]);
          }
          for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
            $rootScope.datatypesMap[$scope.selectedDatatypes[i].id] = $scope.selectedDatatypes[i];
          }
          var usedDtId1 = _.map($rootScope.usedDtLink, function(num, key) {
            return num.id;
          });

          DatatypeService.get(usedDtId1).then(function(datatypes) {
            for (var j = 0; j < datatypes.length; j++) {
              if (!$rootScope.datatypesMap[datatypes[j].id]) {

                $rootScope.datatypesMap[datatypes[j].id] = datatypes[j];
                $rootScope.datatypes.push(datatypes[j]);
                //$rootScope.getDerived(datatypes[j]);
              }
            }

            var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
              return num.id;
            });
            console.log("$rootScope.usedVsLink");

            console.log($rootScope.usedVsLink);
            var newTablesLink = _.difference($rootScope.usedVsLink, $rootScope.tableLibrary.children);
            console.log(newTablesLink);

            TableLibrarySvc.addChildren($rootScope.tableLibrary.id, newTablesLink).then(function() {
              $rootScope.tableLibrary.children = _.union(newTablesLink, $rootScope.tableLibrary.children);

              TableService.get(usedVsId).then(function(tables) {
                for (var j = 0; j < tables.length; j++) {
                  if (!$rootScope.tablesMap[tables[j].id]) {
                    $rootScope.tablesMap[tables[j].id] = tables[j];
                    $rootScope.tables.push(tables[j]);

                  }
                }
              });

            });
          });
          $rootScope.msg().text = "datatypeAdded";
          $rootScope.msg().type = "success";
          $rootScope.msg().show = true;
        });

      }, function(error) {
        $rootScope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });

    });


  };
  $rootScope.saveBindingForValueSet = function() {
    var datatypeUpdateParameterList = [];
    var segmentUpdateParameterList = [];

    for (var q = 0; q < $rootScope.references.length; q++) {
      var ref = $rootScope.references[q];
      if(ref && ref.tableLink){
        if (ref.tableLink.isNew) {
          if (ref.type == 'component') {
            var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
            ref.tableLink.isNew = null;
            ref.tableLink.isChanged = null;
            var newTableLink = angular.copy(ref.tableLink);
            var targetComponent = angular.copy(ref);
            targetComponent.target = null;
            targetComponent.path = null;
            targetComponent.tableLink = null;

            var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
              return component.position == targetComponent.position;
            });
            if (toBeUpdateComponent) toBeUpdateComponent.tables.push(newTableLink);
            $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
            var oldDatatype = _.find($rootScope.datatypes, function(dt) {
              return dt.id == targetDatatype.id;
            });
            var index = $rootScope.datatypes.indexOf(oldDatatype);
            if (index > -1) $rootScope.datatypes[index] = targetDatatype;

            var datatypeUpdateParameter = {};
            datatypeUpdateParameter.datatypeId = targetDatatype.id;
            datatypeUpdateParameter.componentId = targetComponent.id;
            datatypeUpdateParameter.tableLink = newTableLink;
            datatypeUpdateParameterList.push(datatypeUpdateParameter);
          } else if (ref.type == 'field') {
            var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
            ref.tableLink.isNew = null;
            ref.tableLink.isChanged = null;
            var newTableLink = angular.copy(ref.tableLink);
            var targetField = angular.copy(ref);
            targetField.target = null;
            targetField.path = null;
            targetField.tableLink = null;

            var toBeUpdateField = _.find(targetSegment.fields, function(field) {
              return field.position == targetField.position;
            });
            if (toBeUpdateField) toBeUpdateField.tables.push(newTableLink);
            $rootScope.segmentsMap[targetSegment.id] = targetSegment;
            var oldSegment = _.find($rootScope.segments, function(seg) {
              return seg.id == targetSegment.id;
            });
            var index = $rootScope.segments.indexOf(oldSegment);
            if (index > -1) $rootScope.segments[index] = targetSegment;

            var segmentUpdateParameter = {};
            segmentUpdateParameter.segmentId = targetSegment.id;
            segmentUpdateParameter.fieldId = targetField.id;
            segmentUpdateParameter.tableLink = newTableLink;
            segmentUpdateParameterList.push(segmentUpdateParameter);
          }
        } else if (ref.tableLink.isChanged) {
          if (ref.type == 'component') {
            var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
            ref.tableLink.isNew = null;
            ref.tableLink.isChanged = null;
            var newTableLink = angular.copy(ref.tableLink);
            var targetComponent = angular.copy(ref);
            targetComponent.target = null;
            targetComponent.path = null;
            targetComponent.tableLink = null;

            var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
              return component.position == targetComponent.position;
            });
            if (toBeUpdateComponent) {
              for (var i = 0; i < toBeUpdateComponent.tables.length; i++) {
                if (toBeUpdateComponent.tables[i].id == $rootScope.table.id) {
                  toBeUpdateComponent.tables[i] = newTableLink;
                }
              }
            }
            $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
            var oldDatatype = _.find($rootScope.datatypes, function(dt) {
              return dt.id == targetDatatype.id;
            });
            var index = $rootScope.datatypes.indexOf(oldDatatype);
            if (index > -1) $rootScope.datatypes[index] = targetDatatype;

            var datatypeUpdateParameter = {};
            datatypeUpdateParameter.datatypeId = targetDatatype.id;
            datatypeUpdateParameter.componentId = targetComponent.id;
            datatypeUpdateParameter.tableLink = newTableLink;
            datatypeUpdateParameter.key = $rootScope.table.id;
            datatypeUpdateParameterList.push(datatypeUpdateParameter);
          } else if (ref.type == 'field') {
            var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
            ref.tableLink.isNew = null;
            ref.tableLink.isChanged = null;
            var newTableLink = angular.copy(ref.tableLink);
            var targetField = angular.copy(ref);
            targetField.target = null;
            targetField.path = null;
            targetField.tableLink = null;

            var toBeUpdateField = _.find(targetSegment.fields, function(field) {
              return field.position == targetField.position;
            });
            if (toBeUpdateField) {
              for (var i = 0; i < toBeUpdateField.tables.length; i++) {
                if (toBeUpdateField.tables[i].id == $rootScope.table.id) {
                  toBeUpdateField.tables[i] = newTableLink;
                }
              }
            }
            $rootScope.segmentsMap[targetSegment.id] = targetSegment;
            var oldSegment = _.find($rootScope.segments, function(seg) {
              return seg.id == targetSegment.id;
            });
            var index = $rootScope.segments.indexOf(oldSegment);
            if (index > -1) $rootScope.segments[index] = targetSegment;

            var segmentUpdateParameter = {};
            segmentUpdateParameter.segmentId = targetSegment.id;
            segmentUpdateParameter.fieldId = targetField.id;
            segmentUpdateParameter.tableLink = newTableLink;
            segmentUpdateParameter.key = $rootScope.table.id;
            segmentUpdateParameterList.push(segmentUpdateParameter);
          }
        }
      }
    }




    SegmentService.updateTableBinding(segmentUpdateParameterList).then(function(result) {}, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

    DatatypeService.updateTableBinding(datatypeUpdateParameterList).then(function(result) {}, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

    $rootScope.references = [];
    angular.forEach($rootScope.segments, function(segment) {
      $rootScope.findTableRefs($rootScope.table, segment, $rootScope.getSegmentLabel(segment), segment);
    });
    angular.forEach($rootScope.datatypes, function(dt) {
      $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt), dt);
    });
    console.log("clearing");
    $rootScope.clearChanges();
  };

  $rootScope.genRegex = function(format) {
    if (format === 'YYYY') {
      return '([0-9]{4})(((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?)?)?)?((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYYMM') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?)?)?((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYYMMDD') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?)?((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYYMMDDhh') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYYMMDDhhmm') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYYMMDDhhmmss') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])(\.[0-9]{1,4})?((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYYMMDDhhmmss.sss') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\.[0-9]{1,4}((\\+|\\-)[0-9]{4})?';
    } else if (format === 'YYYY+-ZZZZ') {
      return '([0-9]{4})(((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?)?)?)?(\\+|\\-)[0-9]{4}';
    } else if (format === 'YYYYMM+-ZZZZ') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?)?)?(\\+|\\-)[0-9]{4}';
    } else if (format === 'YYYYMMDD+-ZZZZ') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?)?(\\+|\\-)[0-9]{4}';
    } else if (format === 'YYYYMMDDhh+-ZZZZ') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?)?(\\+|\\-)[0-9]{4}';
    } else if (format === 'YYYYMMDDhhmm+-ZZZZ') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])(([0-5][0-9])(\.[0-9]{1,4})?)?(\\+|\\-)[0-9]{4}';
    } else if (format === 'YYYYMMDDhhmmss+-ZZZZ') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])(\.[0-9]{1,4})?(\\+|\\-)[0-9]{4}';
    } else if (format === 'YYYYMMDDhhmmss.sss+-ZZZZ') {
      return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\.[0-9]{1,4}(\\+|\\-)[0-9]{4}';
    } else if (format === 'ISO-compliant OID') {
      return '[0-2](\\.(0|[1-9][0-9]*))*';
    } else if (format === 'Alphanumeric') {
      return '^[a-zA-Z0-9]*$';
    } else if (format === 'Positive Integer') {
      return '^[1-9]\d*$';
    }

    return format;
  };

  $rootScope.isAvailableDTForTable = function(dt) {
    if (dt != undefined) {
      if (dt.name === 'IS' || dt.name === 'ID' || dt.name === 'CWE' || dt.name === 'CNE' || dt.name === 'CE') return true;

      if (dt.components != undefined && dt.components.length > 0) return true;

    }
    return false;
  };

  $rootScope.validateNumber = function(event) {
    var key = window.event ? event.keyCode : event.which;
    if (event.keyCode == 8 || event.keyCode == 46 || event.keyCode == 37 || event.keyCode == 39) {
      return true;
    } else if (key < 48 || key > 57) {
      return false;
    } else return true;
  };


  $rootScope.displayLocationForDatatype = function(dt, constraintTarget) {
    var position = constraintTarget.substring(0, constraintTarget.indexOf('['));
    var component = _.find(dt.components, function(c) {
      return c.position == position;
    });
    if (component) return dt.name + "." + position + " (" + component.name + ")";
    return dt.name;
  };

  $rootScope.displayLocationForSegment = function(segment, constraintTarget) {
    var position = constraintTarget.substring(0, constraintTarget.indexOf('['));
    var field = _.find(segment.fields, function(f) {
      return f.position == position;
    });
    if (field) return segment.name + "-" + position + " (" + field.name + ")";
    return segment.name;
  };

  $rootScope.getUpdatedBindingIdentifier = function(table) {
    if (table.hl7Version && table.hl7Version !== '') {
      return table.bindingIdentifier + "_" + table.hl7Version.split(".").join("-");
    }

    return table.bindingIdentifier;
  };

  $rootScope.generateCompositeConformanceStatement = function(compositeType, firstConstraint, secondConstraint, constraints) {
    var cs = null;
    if (compositeType === 'AND' || compositeType === 'OR' || compositeType === 'XOR') {
      var firstConstraintAssertion = firstConstraint.assertion.replace("<Assertion>", "");
      firstConstraintAssertion = firstConstraintAssertion.replace("</Assertion>", "");
      var secondConstraintAssertion = secondConstraint.assertion.replace("<Assertion>", "");
      secondConstraintAssertion = secondConstraintAssertion.replace("</Assertion>", "");

      cs = {
        id: new ObjectId().toString(),
        constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
        description: '[' + firstConstraint.description + '] ' + compositeType + ' [' + secondConstraint.description + ']',
        assertion: '<Assertion><' + compositeType + '>' + firstConstraintAssertion + secondConstraintAssertion + '</' + compositeType + '></Assertion>'
      };
    } else if (compositeType === 'IFTHEN') {
      var firstConstraintAssertion = firstConstraint.assertion.replace("<Assertion>", "");
      firstConstraintAssertion = firstConstraintAssertion.replace("</Assertion>", "");
      var secondConstraintAssertion = secondConstraint.assertion.replace("<Assertion>", "");
      secondConstraintAssertion = secondConstraintAssertion.replace("</Assertion>", "");

      var modifiedFirstDescription = firstConstraint.description.replace("should not be", "is not").replace("SHALL NOT be", "is not").replace("may not be", "is not").replace("SHALL be", "is").replace("should be", "is").replace("may be", "is");

      cs = {
        id: new ObjectId().toString(),
        constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
        description: 'IF [' + modifiedFirstDescription + '] THEN [' + secondConstraint.description + ']',
        assertion: '<Assertion><IMPLY>' + firstConstraintAssertion + secondConstraintAssertion + '</IMPLY></Assertion>'
      };
    } else if (compositeType === 'FORALL' || compositeType === 'EXIST') {
      var forALLExistId = compositeType;
      var forALLExistAssertion = '';
      var forALLExistDescription = compositeType;

      angular.forEach(constraints, function(c) {
        forALLExistAssertion = forALLExistAssertion + c.assertion.replace("<Assertion>", "").replace("</Assertion>", "");
        forALLExistDescription = forALLExistDescription + '[' + c.description + ']';
        forALLExistId = forALLExistId + '(' + c.constraintId + ')';
      });

      cs = {
        id: new ObjectId().toString(),
        constraintId: forALLExistId,
        description: forALLExistDescription,
        assertion: '<Assertion><' + compositeType + '>' + forALLExistAssertion + '</' + compositeType + '></Assertion>'
      };
    }
    return cs;
  };

  $rootScope.findPredicateForPC = function (pc){
    if(pc.from === 'message' && pc.type !== 'message'){
      if(pc.type === 'field'){
        var path = pc.path;
        var splitPath = path.split(".");
        var copyPath = angular.copy(splitPath);
        var result;
        var message = $rootScope.messagesMap[pc.source.messageId];
        var segment = $rootScope.segmentsMap[pc.source.segmentId];


        var parent = message;
        var contextPath = null;
        for (var i = 1; i < splitPath.length - 1; i++) {
          copyPath.shift();
          var newPath = copyPath.join('[1].') + '[1]';
          console.log(newPath);
          result = _.find(parent.predicates, function(p){
            p.context = {};
            p.context.type = 'message';
            p.context.id = pc.source.messageId;
            p.context.path =  contextPath;
            return p.constraintTarget === newPath;
          });
          if(!contextPath) {
            contextPath = splitPath[i];
          }else {
            contextPath = contextPath + "." + splitPath[i];
          }
          parent = _.find(parent.children, function(child){
            return child.position + "" === splitPath[i] + "";
          });
        }

        if(!result) {
          result = _.find(segment.predicates, function(p) {
            p.context = {};
            p.context.type = 'segment';
            p.context.id = pc.source.segmentId;
            p.context.path =  null;
            return p.constraintTarget === splitPath[splitPath.length - 1] + "[1]";
          });
        }

        console.log(result);
        return result;
      }else if(pc.type === 'component'){
        var path = pc.path;
        var splitPath = path.split(".");
        var copyPath = angular.copy(splitPath);
        var result;
        var message = $rootScope.messagesMap[pc.source.messageId];
        var segment = $rootScope.segmentsMap[pc.source.segmentId];
        var fieldDT = $rootScope.datatypesMap[pc.source.fieldDt];

        if(!pc.source.componentDT){
          var parent = message;
          var contextPath = null;
          for (var i = 1; i < splitPath.length - 2; i++) {
            copyPath.shift();
            var newPath = copyPath.join('[1].') + '[1]';
            console.log(newPath);
            result = _.find(parent.predicates, function(p){
              p.context = {};
              p.context.type = 'message';
              p.context.id = pc.source.messageId;
              p.context.path =  contextPath;
              return p.constraintTarget === newPath;
            });
            if(!contextPath) {
              contextPath = splitPath[i];
            }else {
              contextPath = contextPath + "." + splitPath[i];
            }
            parent = _.find(parent.children, function(child){
              return child.position + "" === splitPath[i] + "";
            });
          }

          if(!result) {
            result = _.find(segment.predicates, function(p) {
              p.context = {};
              p.context.type = 'segment';
              p.context.id = pc.source.segmentId;
              p.context.path =  null;
              return p.constraintTarget === splitPath[splitPath.length - 2] + "[1]." + splitPath[splitPath.length - 1] + "[1]";
            });
          }

          if(!result) {
            result = _.find(fieldDT.predicates, function(p) {
              p.context = {};
              p.context.type = 'datatype';
              p.context.id = pc.source.fieldDt;
              p.context.path =  null;
              return p.constraintTarget === splitPath[splitPath.length - 1] + "[1]";
            });
          }
          return result;

        }else{
          var componentDT = $rootScope.datatypesMap[pc.source.componentDt];

          var parent = message;
          var contextPath = null;
          for (var i = 1; i < splitPath.length - 3; i++) {
            copyPath.shift();
            var newPath = copyPath.join('[1].') + '[1]';
            console.log(newPath);
            result = _.find(parent.predicates, function(p){
              p.context = {};
              p.context.type = 'message';
              p.context.id = pc.source.messageId;
              p.context.path =  contextPath;
              return p.constraintTarget === newPath;
            });
            if(!contextPath) {
              contextPath = splitPath[i];
            }else {
              contextPath = contextPath + "." + splitPath[i];
            }
            parent = _.find(parent.children, function(child){
              return child.position + "" === splitPath[i] + "";
            });
          }

          if(!result) {
            result = _.find(segment.predicates, function(p) {
              p.context = {};
              p.context.type = 'segment';
              p.context.id = pc.source.segmentId;
              p.context.path =  null;
              return p.constraintTarget === splitPath[splitPath.length  - 3] + "[1]." + splitPath[splitPath.length  - 2] + "[1]." + splitPath[splitPath.length - 1] + "[1]";
            });
          }

          if(!result) {
            result = _.find(fieldDT.predicates, function(p) {
              p.context = {};
              p.context.type = 'datatype';
              p.context.id = pc.source.fieldDt;
              p.context.path =  null;
              return p.constraintTarget === splitPath[splitPath.length  - 2] + "[1]." + splitPath[splitPath.length  - 1] + "[1]";
            });
          }

          if(!result) {
            result = _.find(componentDT.predicates, function(p) {
              p.context = {};
              p.context.type = 'datatype';
              p.context.id = pc.source.componentDt;
              p.context.path =  null;
              return p.constraintTarget === splitPath[splitPath.length - 1] + "[1]";
            });
          }
          return result;
        }

      }else {
        var path = pc.path;
        var splitPath = path.split(".");
        var copyPath = angular.copy(splitPath);
        var result;
        var message = $rootScope.messagesMap[pc.source.messageId];

        var parent = message;
        var contextPath = null;
        for (var i = 1; i < splitPath.length - 1; i++) {
          copyPath.shift();
          var newPath = copyPath.join('[1].') + '[1]';
          console.log(newPath);
          result = _.find(parent.predicates, function(p){
            p.context = {};
            p.context.type = 'message';
            p.context.id = pc.source.messageId;
            p.context.path =  contextPath;
            return p.constraintTarget === newPath;
          });
          if(!contextPath) {
            contextPath = splitPath[i];
          }else {
            contextPath = contextPath + "." + splitPath[i];
          }
          parent = _.find(parent.children, function(child){
            return child.position + "" === splitPath[i] + "";
          });
        }
        return result;
      }

    }else if(pc.from === 'segment' && pc.type === 'field'){
      var path = pc.path;
      var segment = $rootScope.segmentsMap[pc.source.segmentId];
      var position = path.split(".")[1];
      return _.find(segment.predicates, function(p) {
        p.context = {};
        p.context.type = 'segment';
        p.context.id = pc.source.segmentId;
        p.context.path =  null;
        return p.constraintTarget === position + "[1]";
      });
    }else if(pc.from === 'segment' && pc.type === 'component'){
      var path = pc.path;
      var splitPath = path.split(".");
      var segment = $rootScope.segmentsMap[pc.source.segmentId];
      if(splitPath.length === 3){
        var fieldDT = $rootScope.datatypesMap[pc.source.fieldDt];
        var result = _.find(segment.predicates, function(p) {
          p.context = {};
          p.context.type = 'segment';
          p.context.id = pc.source.segmentId;
          p.context.path =  null;
          return p.constraintTarget === splitPath[1] + "[1]." + splitPath[2] + "[1]";
        });

        if(!result) {
          result = _.find(fieldDT.predicates, function(p) {
            p.context = {};
            p.context.type = 'datatype';
            p.context.id = pc.source.fieldDt;
            p.context.path =  null;
            return p.constraintTarget === splitPath[2] + "[1]";
          });
        }
        return result;
      }else if(splitPath.length === 4){
        var fieldDT = $rootScope.datatypesMap[pc.source.fieldDt];
        var componentDT = $rootScope.datatypesMap[pc.source.componentDt];
        var result = _.find(segment.predicates, function(p) {
          p.context = {};
          p.context.type = 'segment';
          p.context.id = pc.source.segmentId;
          p.context.path =  null;
          return p.constraintTarget === splitPath[1] + "[1]." + splitPath[2] + "[1]." + splitPath[3] + "[1]";
        });

        if(!result) {
          result = _.find(fieldDT.predicates, function(p) {
            p.context = {};
            p.context.type = 'datatype';
            p.context.id = pc.source.fieldDt;
            p.context.path =  null;
            return p.constraintTarget === splitPath[2] + "[1]." + splitPath[3] + "[1]";
          });
        }

        if(!result) {
          result = _.find(componentDT.predicates, function(p) {
            p.context = {};
            p.context.type = 'datatype';
            p.context.id = pc.source.componentDt;
            p.context.path =  null;
            return p.constraintTarget === splitPath[3] + "[1]";
          });
        }
        return result;
      }
    }
    return null;
  };


  $rootScope.generateCompositePredicate = function(compositeType, firstConstraint, secondConstraint, constraints) {
    var cp = null;
    if (compositeType === 'AND' || compositeType === 'OR' || compositeType === 'XOR') {
      var firstConstraintAssertion = firstConstraint.assertion.replace("<Condition>", "");
      firstConstraintAssertion = firstConstraintAssertion.replace("</Condition>", "");
      var secondConstraintAssertion = secondConstraint.assertion.replace("<Condition>", "");
      secondConstraintAssertion = secondConstraintAssertion.replace("</Condition>", "");

      cp = {
        id: new ObjectId().toString(),
        constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
        constraintTarget: firstConstraint.constraintTarget,
        description: '[' + firstConstraint.description + '] ' + compositeType + ' [' + secondConstraint.description + ']',
        trueUsage: '',
        falseUsage: '',
        assertion: '<Condition><' + compositeType + '>' + firstConstraintAssertion + secondConstraintAssertion + '</' + compositeType + '></Condition>'
      };
    } else if (compositeType === 'IFTHEN') {
      var firstConstraintAssertion = firstConstraint.assertion.replace("<Condition>", "");
      firstConstraintAssertion = firstConstraintAssertion.replace("</Condition>", "");
      var secondConstraintAssertion = secondConstraint.assertion.replace("<Condition>", "");
      secondConstraintAssertion = secondConstraintAssertion.replace("</Condition>", "");

      cp = {
        id: new ObjectId().toString(),
        constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
        constraintTarget: firstConstraint.constraintTarget,
        description: 'IF [' + firstConstraint.description + '] THEN [' + secondConstraint.description + ']',
        trueUsage: '',
        falseUsage: '',
        assertion: '<Condition><IMPLY>' + firstConstraintAssertion + secondConstraintAssertion + '</IMPLY></Condition>'
      };
    } else if (compositeType === 'FORALL' || compositeType === 'EXIST') {
      var forALLExistId = compositeType;
      var forALLExistAssertion = '';
      var forALLExistDescription = compositeType;
      var forALLExistConstraintTarget = '';

      angular.forEach(constraints, function(c) {
        forALLExistAssertion = forALLExistAssertion + c.assertion.replace("<Condition>", "").replace("</Condition>", "");
        forALLExistDescription = forALLExistDescription + '[' + c.description + ']';
        forALLExistId = forALLExistId + '(' + c.constraintId + ')';
        forALLExistConstraintTarget = c.constraintTarget;
      });

      cp = {
        id: new ObjectId().toString(),
        constraintId: forALLExistId,
        constraintTarget: forALLExistConstraintTarget,
        description: forALLExistDescription,
        assertion: '<Condition><' + compositeType + '>' + forALLExistAssertion + '</' + compositeType + '></Condition>'
      };
    }
    return cp;
  };

  $rootScope.generateFreeTextConformanceStatement = function(newConstraint) {
    var cs = {
      id: new ObjectId().toString(),
      constraintId: newConstraint.constraintId,
      description: newConstraint.freeText,
      assertion: null
    };

    return cs;
  };

  $rootScope.generateConformanceStatement = function(newConstraint) {
    var cs = null;
    if (newConstraint.contraintType === 'valued') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + '.',
        assertion: '<Assertion><Presence Path=\"' + newConstraint.position_1 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'a literal value') {
      if (newConstraint.value.indexOf("^") == -1) {
        cs = {
          id: new ObjectId().toString(),
          constraintId: newConstraint.constraintId,
          description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
          assertion: '<Assertion><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"' + newConstraint.value + '\" IgnoreCase=\"' + newConstraint.ignoreCase + '\"/></Assertion>'
        };
      } else {
        console.log(newConstraint.value);
        if (newConstraint.value == '^~\\&') {
          cs = {
            id: new ObjectId().toString(),
            constraintId: newConstraint.constraintId,
            description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'^~\\&amp;\'.',
            assertion: '<Assertion><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"^~\\&amp;\" IgnoreCase=\"' + newConstraint.ignoreCase + '\"/></Assertion>'
          };
        } else {
          var componetsList = newConstraint.value.split("^");
          var assertionScript = "";
          var componentPosition = 0;

          angular.forEach(componetsList, function(componentValue) {
            componentPosition = componentPosition + 1;
            var script = '<PlainText Path=\"' + newConstraint.position_1 + "." + componentPosition + "[1]" + '\" Text=\"' + componentValue + '\" IgnoreCase="false"/>';
            if (assertionScript === "") {
              assertionScript = script;
            } else {
              assertionScript = "<AND>" + assertionScript + script + "</AND>";
            }
          });


          cs = {
            id: new ObjectId().toString(),
            constraintId: newConstraint.constraintId,
            description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
            assertion: '<Assertion>' + assertionScript + '</Assertion>'
          };
        }
      }
    } else if (newConstraint.contraintType === 'one of list values') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.value + '.',
        assertion: '<Assertion><StringList Path=\"' + newConstraint.position_1 + '\" CSV=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'one of codes in ValueSet') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.valueSetId + '.',
        assertion: '<Assertion><ValueSet Path=\"' + newConstraint.position_1 + '\" ValueSetID=\"' + newConstraint.valueSetId + '\" BindingStrength=\"' + newConstraint.bindingStrength + '\" BindingLocation=\"' + newConstraint.bindingLocation + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'formatted value') {
      if (newConstraint.value === 'Regular expression') {
        cs = {
          id: new ObjectId().toString(),
          constraintId: newConstraint.constraintId,
          description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value2 + '\'.',
          assertion: '<Assertion><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + newConstraint.value2 + '\"/></Assertion>'
        };
      } else {
        cs = {
          id: new ObjectId().toString(),
          constraintId: newConstraint.constraintId,
          description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value + '\'.',
          assertion: '<Assertion><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex(newConstraint.value) + '\"/></Assertion>'
        };
      }
    } else if (newConstraint.contraintType === 'identical to another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' identical to the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'equal to another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'not-equal to another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="NE" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'greater than another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GT" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'equal to or greater than another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GE" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'less than another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LT" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'equal to or less than another node') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than the value of ' + newConstraint.location_2 + '.',
        assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LE" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'equal to') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to ' + newConstraint.value + '.',
        assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="EQ" Value=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'not-equal to') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with ' + newConstraint.value + '.',
        assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="NE" Value=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'greater than') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than ' + newConstraint.value + '.',
        assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GT" Value=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'equal to or greater than') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than ' + newConstraint.value + '.',
        assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GE" Value=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'less than') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than ' + newConstraint.value + '.',
        assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LT" Value=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === 'equal to or less than') {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than ' + newConstraint.value + '.',
        assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LE" Value=\"' + newConstraint.value + '\"/></Assertion>'
      };
    } else if (newConstraint.contraintType === "valued sequentially starting with the value '1'") {
      cs = {
        id: new ObjectId().toString(),
        constraintId: newConstraint.constraintId,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + " valued sequentially starting with the value '1'.",
        assertion: '<Assertion><SetID Path=\"' + newConstraint.position_1 + '\"/></Assertion>'
      };
    }


    if (newConstraint.verb.includes('NOT') || newConstraint.verb.includes('not')) {
      cs.assertion = cs.assertion.replace("<Assertion>", "<Assertion><NOT><AND><Presence Path=\"" + newConstraint.position_1 + "\"/>");
      cs.assertion = cs.assertion.replace("</Assertion>", "</AND></NOT></Assertion>");
    }else {
      cs.assertion = cs.assertion.replace("<Assertion>", "<Assertion><AND><Presence Path=\"" + newConstraint.position_1 + "\"/>");
      cs.assertion = cs.assertion.replace("</Assertion>", "</AND></Assertion>");
    }
    cs.description = cs.description.split('[1]').join('');

    return cs;
  };

  $rootScope.generateFreeTextPredicate = function(positionPath, newConstraint) {
    var cp = {
      id: new ObjectId().toString(),
      constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
      constraintTarget: positionPath,
      description: newConstraint.freeText,
      trueUsage: newConstraint.trueUsage,
      falseUsage: newConstraint.falseUsage,
      assertion: null
    };
    return cp;
  };

  $rootScope.generatePredicate = function(positionPath, newConstraint) {
    var cp = null;
    if (newConstraint.contraintType === 'valued') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType,
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><Presence Path=\"' + newConstraint.position_1 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'a literal value') {
      if (newConstraint.value.indexOf("^") == -1) {
        cp = {
          id: new ObjectId().toString(),
          constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
          constraintTarget: positionPath,
          description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
          trueUsage: newConstraint.trueUsage,
          falseUsage: newConstraint.falseUsage,
          assertion: '<Condition><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"' + newConstraint.value + '\" IgnoreCase=\"' + newConstraint.ignoreCase + '\"/></Condition>'
        };
      } else {
        var componetsList = newConstraint.value.split("^");
        var assertionScript = "";
        var componentPosition = 0;

        angular.forEach(componetsList, function(componentValue) {
          componentPosition = componentPosition + 1;
          var script = '<PlainText Path=\"' + newConstraint.position_1 + "." + componentPosition + "[1]" + '\" Text=\"' + componentValue + '\" IgnoreCase="false"/>';
          if (assertionScript === "") {
            assertionScript = script;
          } else {
            assertionScript = "<AND>" + assertionScript + script + "</AND>";
          }
        });
        cp = {
          id: new ObjectId().toString(),
          constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
          constraintTarget: positionPath,
          description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
          trueUsage: newConstraint.trueUsage,
          falseUsage: newConstraint.falseUsage,
          assertion: '<Condition>' + assertionScript + '</Condition>'
        };
      }
    } else if (newConstraint.contraintType === 'one of list values') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><StringList Path=\"' + newConstraint.position_1 + '\" CSV=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'one of codes in ValueSet') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.valueSetId + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><ValueSet Path=\"' + newConstraint.position_1 + '\" ValueSetID=\"' + newConstraint.valueSetId + '\" BindingStrength=\"' + newConstraint.bindingStrength + '\" BindingLocation=\"' + newConstraint.bindingLocation + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'formatted value') {
      if (newConstraint.value === 'Regular expression') {
        cp = {
          id: new ObjectId().toString(),
          constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
          constraintTarget: positionPath,
          description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value2 + '\'.',
          trueUsage: newConstraint.trueUsage,
          falseUsage: newConstraint.falseUsage,
          assertion: '<Condition><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + newConstraint.value2 + '\"/></Condition>'
        };
      } else {
        cp = {
          id: new ObjectId().toString(),
          constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
          constraintTarget: positionPath,
          description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value + '\'.',
          trueUsage: newConstraint.trueUsage,
          falseUsage: newConstraint.falseUsage,
          assertion: '<Condition><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex(newConstraint.value) + '\"/></Condition>'
        };
      }
    } else if (newConstraint.contraintType === 'identical to another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' identical to the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'equal to another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'not-equal to another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="NE" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'greater than another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GT" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'equal to or greater than another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GE" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'less than another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LT" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'equal to or less than another node') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than the value of ' + newConstraint.location_2 + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LE" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'equal to') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="EQ" Value=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'not-equal to') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="NE" Value=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'greater than') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GT" Value=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'equal to or greater than') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GE" Value=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'less than') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LT" Value=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === 'equal to or less than') {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than ' + newConstraint.value + '.',
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LE" Value=\"' + newConstraint.value + '\"/></Condition>'
      };
    } else if (newConstraint.contraintType === "valued sequentially starting with the value '1'") {
      cp = {
        id: new ObjectId().toString(),
        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
        constraintTarget: positionPath,
        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + " valued sequentially starting with the value '1'.",
        trueUsage: newConstraint.trueUsage,
        falseUsage: newConstraint.falseUsage,
        assertion: '<Condition><SetID Path=\"' + newConstraint.position_1 + '\"/></Condition>'
      };
    }

    if (newConstraint.verb.includes('NOT') || newConstraint.verb.includes('not')) {
      cp.assertion = cp.assertion.replace("<Condition>", "<Condition><NOT><AND><Presence Path=\"" + newConstraint.position_1 + "\"/>");
      cp.assertion = cp.assertion.replace("</Condition>", "</AND></NOT></Condition>");
    }else {
      cp.assertion = cp.assertion.replace("<Condition>", "<Condition><AND><Presence Path=\"" + newConstraint.position_1 + "\"/>");
      cp.assertion = cp.assertion.replace("</Condition>", "</AND></Condition>");
    }

    cp.description = cp.description.split('[1]').join('');

    return cp;
  };


  $rootScope.erorrForComplexConfStatement = function(newComplexConstraintId, targetComplexId, compositeType, firstConstraint, secondConstraint, constraints) {
    if ($rootScope.isEmptyCompositeType(compositeType)) return true;
    if ($rootScope.isEmptyComplexConstraintID(newComplexConstraintId)) return true;
    if ($rootScope.isDuplicatedComplexConstraintID(newComplexConstraintId, targetComplexId)) return true;

    if (compositeType == 'FORALL' || compositeType == 'EXIST') {
      if (constraints.length < 2) return true;
    } else {
      if (firstConstraint == null) return true;
      if (secondConstraint == null) return true;
    }

    return false;
  };

  $rootScope.erorrForComplexPredicate = function(compositeType, firstConstraint, secondConstraint, complexConstraintTrueUsage, complexConstraintFalseUsage, constraints) {
    if ($rootScope.isEmptyCompositeType(compositeType)) return true;
    if (compositeType == 'FORALL' || compositeType == 'EXIST') {
      if (constraints.length < 2) return true;
    } else {
      if (firstConstraint == null) return true;
      if (secondConstraint == null) return true;
    }
    return false;
  };

  $rootScope.erorrForPredicate = function(newConstraint, type, selectedNode) {
    if (!selectedNode) return true;
    if ($rootScope.isEmptyConstraintNode(newConstraint, type)) return true;
    if ($rootScope.isEmptyConstraintVerb(newConstraint)) return true;
    if ($rootScope.isEmptyConstraintPattern(newConstraint)) return true;
    if (newConstraint.contraintType == 'a literal value' ||
      newConstraint.contraintType == 'equal to' ||
      newConstraint.contraintType == 'not-equal to' ||
      newConstraint.contraintType == 'greater than' ||
      newConstraint.contraintType == 'equal to or greater than' ||
      newConstraint.contraintType == 'less than' ||
      newConstraint.contraintType == 'equal to or less than' ||
      newConstraint.contraintType == 'one of list values' ||
      newConstraint.contraintType == 'formatted value') {
      if ($rootScope.isEmptyConstraintValue(newConstraint)) return true;
      if (newConstraint.value == 'Regular expression') {
        if ($rootScope.isEmptyConstraintValue2(newConstraint)) return true;
      }
    } else if (newConstraint.contraintType == 'identical to another node' ||
      newConstraint.contraintType == 'equal to another node' ||
      newConstraint.contraintType == 'not-equal to another node' ||
      newConstraint.contraintType == 'greater than another node' ||
      newConstraint.contraintType == 'equal to or greater than another node' ||
      newConstraint.contraintType == 'less than another node' ||
      newConstraint.contraintType == 'equal to or less than another node') {
      if ($rootScope.isEmptyConstraintAnotherNode(newConstraint)) return true;
    } else if (newConstraint.contraintType == 'one of codes in ValueSet') {
      if ($rootScope.isEmptyConstraintValueSet(newConstraint)) return true;
    }

    return false;
  };

  $rootScope.erorrForConfStatement = function(newConstraint, targetId, type, selectedNode) {
    if ($rootScope.isEmptyConstraintID(newConstraint)) return true;
    if ($rootScope.isDuplicatedConstraintID(newConstraint, targetId)) return true;
    if ($rootScope.isEmptyConstraintNode(newConstraint, type)) return true;
    if ($rootScope.isEmptyConstraintVerb(newConstraint)) return true;
    if ($rootScope.isEmptyConstraintPattern(newConstraint)) return true;
    if (newConstraint) {
      if (newConstraint.contraintType == 'a literal value' ||
        newConstraint.contraintType == 'equal to' ||
        newConstraint.contraintType == 'not-equal to' ||
        newConstraint.contraintType == 'greater than' ||
        newConstraint.contraintType == 'equal to or greater than' ||
        newConstraint.contraintType == 'less than' ||
        newConstraint.contraintType == 'equal to or less than' ||
        newConstraint.contraintType == 'one of list values' ||
        newConstraint.contraintType == 'formatted value') {
        if ($rootScope.isEmptyConstraintValue(newConstraint)) return true;
        if (newConstraint.value == 'Regular expression') {
          if ($rootScope.isEmptyConstraintValue2(newConstraint)) return true;
        }
      } else if (newConstraint.contraintType == 'identical to another node' ||
        newConstraint.contraintType == 'equal to another node' ||
        newConstraint.contraintType == 'not-equal to another node' ||
        newConstraint.contraintType == 'greater than another node' ||
        newConstraint.contraintType == 'equal to or greater than another node' ||
        newConstraint.contraintType == 'less than another node' ||
        newConstraint.contraintType == 'equal to or less than another node') {
        if ($rootScope.isEmptyConstraintAnotherNode(newConstraint)) return true;
      } else if (newConstraint.contraintType == 'one of codes in ValueSet') {
        if ($rootScope.isEmptyConstraintValueSet(newConstraint)) return true;
      }
    }

    return false;
  };

  $rootScope.isEmptyConstraintID = function(newConstraint) {
    if (newConstraint && newConstraint.constraintId === null) return true;
    if (newConstraint && newConstraint.constraintId === '') return true;

    return false;
  }

  $rootScope.isEmptyComplexConstraintID = function(id) {
    if (id === null) return true;
    if (id === '') return true;

    return false;
  }
  $rootScope.getVersionToSelect = function(element) {
    if (element.publicationVersion) {
      return "(v" + element.publicationVersion + ")";
    } else {
      return "";
    }
  }
  $rootScope.isDuplicatedConstraintID = function(newConstraint, targetId) {
    if (newConstraint && $rootScope.conformanceStatementIdList.indexOf(newConstraint.constraintId) != -1 && targetId == newConstraint.constraintId) return true;

    return false;
  }

  $rootScope.isDuplicatedComplexConstraintID = function(newComplexConstraintId, targetComplexId) {
    if ($rootScope.conformanceStatementIdList.indexOf(newComplexConstraintId) != -1 && targetComplexId == newComplexConstraintId) return true;

    return false;
  }

  $rootScope.isEmptyConstraintNode = function(newConstraint, type) {
    if (type == 'datatype') {
      if (newConstraint && newConstraint.component_1 === null) return true;
    } else if (type == 'segment') {
      if (newConstraint && newConstraint.location_1 === null) return true;
    } else if (type == 'message') {
      if (newConstraint && newConstraint.position_1 === null) return true;
    }

    return false;
  }

  $rootScope.isEmptyConstraintVerb = function(newConstraint) {
    if (newConstraint && newConstraint.verb === null) return true;

    return false;
  }

  $rootScope.isEmptyConstraintPattern = function(newConstraint) {
    if (newConstraint && newConstraint.contraintType === null) return true;

    return false;
  }

  $rootScope.isEmptyConstraintValue = function(newConstraint) {
    if (newConstraint && newConstraint.value === null) return true;

    return false;
  }

  $rootScope.isEmptyConstraintValue2 = function(newConstraint) {
    if (newConstraint && newConstraint.value2 === null) return true;

    return false;
  }

  $rootScope.isEmptyConstraintAnotherNode = function(newConstraint, type) {
    if (type == 'datatype') {
      if (newConstraint.component_2 === null) return true;
    } else if (type == 'segment') {
      if (newConstraint.field_2 === null) return true;
    } else if (type == 'message') {
      if (newConstraint.position_2 === null) return true;
    }

    return false;
  }

  $rootScope.isEmptyConstraintValueSet = function(newConstraint) {
    if (newConstraint && newConstraint.valueSetId === null) return true;

    return false;
  }

  $rootScope.isEmptyCompositeType = function(compositeType) {
    if (compositeType === null) return true;

    return false;
  }


  // We check for IE when the user load the main page.
  // TODO: Check only once.
  // $scope.checkForIE();


  $rootScope.openRichTextDlg = function(obj, key, title, disabled) {
    return $mdDialog.show({
      templateUrl: 'RichTextCtrlMd.html',
      controller: 'RichTextCtrl',
      scope: $scope,
      preserveScope: true,

      locals: {
        editorTarget:
          {
            key: key,
            obj: obj,
            disabled: disabled,
            title: title
          }
      }
    });
  };

  $rootScope.openInputTextDlg = function(obj, key, title, disabled) {
    return $mdDialog.open({
      templateUrl: 'InputTextCtrlMd.html',
      controller: 'InputTextCtrl',
      locals: {
        editorTarget:{

          key: key,
          obj: obj,
          disabled: disabled,
          title: title

        }
      }
    });
  };

  // $rootScope.isDuplicated = function(obj, context, list) {
  //   if (obj == null || obj == undefined || obj[context] == null) return false;
  //   return _.find(_.without(list, obj), function(item) {
  //     return item[context] == obj[context] && item.id != obj.id;
  //   });
  // };

  // $rootScope.validateExtension = function (obj, context, list) {
  // //if (obj == null || obj == undefined) return false;
  // if(obj[context] == null) return false;
  // return _.find(_.without(list, obj), function (item) {
  // return item[context] == obj[context];
  // });


  // };


  $rootScope.isDuplicatedTwoContexts = function(obj, context1, context2, list) {
    if (obj === null || obj === undefined) return false;

    return _.find(_.without(list, obj), function(item) {
      if (item[context1] === obj[context1]) {
        return obj[context2] !== null && obj[context2] !== '' && item[context2] === obj[context2] && item.id !== obj.id;
      } else {
        return false;
      }
    });
  };


    $rootScope.isDuplicated= function(obj,list) {
        if (obj === null || obj === undefined) return false;

        return _.find(list, function(item) {
          if(obj.type ==='datatype'){
              if($scope.datatypeDuplicated(obj,item)){
                if($scope.editForm){
                    $scope.editForm.$invalid=true;
                    $scope.editForm.$valid=false;
                    return true;
                }
              }

          }else if(obj.type==='table'){
                  if($scope.tableDuplicated(obj,item)){
                      if($scope.editForm){
                          $scope.editForm.$invalid=true;
                          $scope.editForm.$valid=false;
                          return true;
                      }
                  }

          }else if(obj.type==='segment'){

                  if($scope.segmentDuplicated(obj,item)){
                      if($scope.editForm){
                          $scope.editForm.$invalid=true;
                          $scope.editForm.$valid=false;
                          return true;
                      }
                  }

              }else if(obj.type=='message'){

              if($scope.messageDuplicated(obj,item)){
                  if($scope.editForm){
                      $scope.editForm.$invalid=true;
                      $scope.editForm.$valid=false;
                      return true;
                  }
              }

          }else if(obj.type ==='profilecomponent'){

              if($scope.profileComponentDuplicated(obj,item)){
                  if($scope.editForm){
                      $scope.editForm.$invalid=true;
                      $scope.editForm.$valid=false;
                      return true;
                  }
              }

          }else if(obj.type==='compositeprofilestructure'){

              if($scope.compositeProfileDuplicated(obj,item)){
                  if($scope.editForm){
                      $scope.editForm.$invalid=true;
                      $scope.editForm.$valid=false;
                      return true;
                  }
              }

          };

        });
    };
    $scope.datatypeDuplicated = function(obj,item){
      return obj.id!==item.id&&obj.scope===item.scope&&obj.name ===item.name&&obj.ext===item.ext&&obj.hl7Version===item.hl7Version&&obj.publicationVersion===item.publicationVersion;
    };
    $scope.segmentDuplicated = function(obj,item){
        return obj.id!==item.id&&obj.scope===item.scope&&obj.name ===item.name&& obj.ext===item.ext&& obj.hl7Version===item.hl7Version;
    };
    
    $scope.tableDuplicated  = function (obj ,item ) {
      return obj.id!==item.id&&obj.bindingIdentifier===item.bindingIdentifier&& obj.scope===item.scope;
        
    };
    $scope.messageDuplicated=function (obj,item) {
        return obj.id!==item.id&&obj.hl7Version===item.hl7Version&&obj.identifier==item.identifier;
    };
    $scope.profileComponentDuplicated=function (obj,item) {
        return obj.id!==item.id&&obj.name==item.name;
    };
    $scope.compositeProfileDuplicated=function (obj,item) {
        return obj.id!==item.id&&obj.name==item.name&&obj.ext==item.ext;

    };



    $rootScope.isEmpty = function (value) {
    if(!value) return true;
    if(value === '') return true;
    return false;
  };

  $rootScope.mergeEmptyProperty = function(to, from) {
    Object.keys(to).forEach(function(key, index) {
      if (!to[key] && from[key])
        to[key] = from[key];
      // key: the name of the object key
      // index: the ordinal position of the key within the object
    });

  };
  $scope.init = function() {
    if (userInfoService.isAuthenticated()) {
      VersionAndUseService.findAll().then(function(result) {
        angular.forEach(result, function(info) {
          $rootScope.versionAndUseMap[info.id] = info;
        });
      });
    }
    $http.get('api/igdocuments/config', { timeout: 60000 }).then(function(response) {
      $rootScope.config = angular.fromJson(response.data);
      var delay = $q.defer();

    }, function(error) {});

  };


  $scope.getFullName = function() {
    if (userInfoService.isAuthenticated() === true) {
      return userInfoService.getFullName();
    }
    return '';
  };

  $rootScope.getLabel = function(name, ext) {
    var label = name;
    if (ext && ext !== null && ext !== "") {
      label = label + "_" + ext;
    }
    return label;
  };

  $rootScope.getDynamicWidth = function(a, b, otherColumsWidth) {
    var tableWidth = $rootScope.getTableWidth();
    if (tableWidth > 0) {
      var left = tableWidth - otherColumsWidth;
      return { "width": a * parseInt(left / b) + "px" };
    }
    return "";
  };


  $rootScope.getTableWidth = function() {
    if ($rootScope.tableWidth === null || $scope.tableWidth == 0) {
      $rootScope.tableWidth = $("#nodeDetailsPanel").width();
    }
    return $rootScope.tableWidth;
  };


  $rootScope.getConstraintAsString = function(constraint) {
    return constraint.constraintId + " - " + constraint.description;
  };

  $rootScope.getConformanceStatementAsString = function(constraint) {
    return "[" + constraint.constraintId + "]" + constraint.description;
  };
  $rootScope.getConstraintAsId = function(constraint) {
    return "[" + constraint.constraintId + "]";
  };

  $rootScope.getPredicateAsString = function(constraint) {
    if (constraint) return constraint.description;
    return null;
  };

  $rootScope.getConstraintAsTruncatedString = function(constraint, num) {
    if (constraint && constraint.description) return constraint.description.substring(0, num) + "...";
    return null;
  };

  $rootScope.getTextAsTruncatedString = function(value, num) {
    if(value && num){
      if (value.length > num) return value.substring(0, num) + "...";
      return value;
    }
    return null;

  };

  $rootScope.getTextValue = function(value) {
    return value;
  };

  $rootScope.getConstraintsAsString = function(constraints) {
    var str = '';
    for (var index in constraints) {
      str = str + "<p style=\"text-align: left\">" + constraints[index].id + " - " + constraints[index].description + "</p>";
    }
    return str;
  };

  $rootScope.getPredicatesAsMultipleLinesString = function(node) {
    var html = "";
    angular.forEach(node.predicates, function(predicate) {
      html = html + "<p>" + predicate.description + "</p>";
    });
    return html;
  };

  $rootScope.getPredicatesAsOneLineString = function(node) {
    var html = "";
    angular.forEach(node.predicates, function(predicate) {
      html = html + predicate.description;
    });
    return $sce.trustAsHtml(html);
  };


  $rootScope.getConfStatementsAsMultipleLinesString = function(node) {
    var html = "";
    angular.forEach(node.conformanceStatements, function(conStatement) {
      html = html + "<p>" + conStatement.id + " : " + conStatement.description + "</p>";
    });
    return html;
  };

  $rootScope.getConfStatementsAsOneLineString = function(node) {
    var html = "";
    angular.forEach(node.conformanceStatements, function(conStatement) {
      html = html + conStatement.id + " : " + conStatement.description;
    });
    return $sce.trustAsHtml(html);
  };

  $rootScope.getSegmentRefNodeName = function(node) {
    var seg = $rootScope.segmentsMap[node.ref.id];
    return node.position + "." + $rootScope.getSegmentLabel(seg) + ":" + seg.description;
  };

  $rootScope.getSegmentLabel = function(seg) {
    // var ext = $rootScope.getSegmentExtension(seg);
    return seg != null ? $rootScope.getLabel(seg.name, seg.ext) : "";
  };

  $rootScope.getSegmentExtension = function(seg) {
    return $rootScope.getExtensionInLibrary(seg.id, $rootScope.igdocument.profile.segmentLibrary, "ext");
  };

  $rootScope.getDatatypeExtension = function(datatype) {
    return $rootScope.getExtensionInLibrary(datatype.id, $rootScope.datatypeLibrary, "ext");
  };

  $rootScope.getTableBindingIdentifier = function(table) {
    return $rootScope.getExtensionInLibrary(table.id, $rootScope.tableLibrary, "bindingIdentifier");
  };


  $rootScope.getDatatypeLabel = function(datatype) {
    if (datatype && datatype != null) {
      // var ext = $rootScope.getDatatypeExtension(datatype);
      return $rootScope.getLabel(datatype.name, datatype.ext);
    }
    return "";
  };

  $rootScope.getVersionLabel = function(id) {
    if ($rootScope.versionAndUseMap[id]) {
      return "(v" + $rootScope.versionAndUseMap[id].publicationVersion + ")";
    } else {
      return "";
    }

  }
  $rootScope.hasSameVersion = function(element) {
    if (element) return element.hl7Version;
    return null;
  };

  $rootScope.getScopeLabel = function(leaf) {
    if (leaf) {
      if (leaf.scope === 'HL7STANDARD') {
        return 'HL7';
      } else if (leaf.scope === 'USER') {
        return 'USR';
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

  $rootScope.getTableLabel = function(binding) {
    var table = $rootScope.tablesMap[binding.tableId];
    if (table && table.bindingIdentifier) {
      return $rootScope.getLabel(table.bindingIdentifier, table.ext);
    }
    return "";
  };

  $rootScope.getExtensionInLibrary = function(id, library, propertyType) {
    // ////console.log("main Here id=" + id);
    if (propertyType && library.children) {
      for (var i = 0; i < library.children.length; i++) {
        if (library.children[i].id === id) {
          return library.children[i][propertyType];
        }
      }
    }
    return "";
  };
  $rootScope.publishTable = function(table) {
    var modalInstance = $modal.open({
      templateUrl: 'ConfirmTablePublish.html',
      controller: 'ConfirmTablePublishCtl',
      resolve: {
        tableToPublish: function() {
          return table;
        }
      }
    });
    modalInstance.result.then(function(table) {

      var newLink = {
        name: table.name,
        ext: table.ext,
        id: table.id
      }



      TableService.publish($rootScope.table).then(function(published) {
        console.log("published Results");
        console.log(published);
        TableLibrarySvc.updateChild($rootScope.tableLibrary.id, newLink).then(function(link) {

          $rootScope.table = published;


          $rootScope.tablesMap[published.id].status = "PUBLISHED";

          console.log("rootScope");
          $rootScope.$broadcast('event:openTable', $rootScope.table);
          console.log($rootScope.table);
          if ($scope.editForm) {
            console.log("Cleeaning");
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $scope.editForm.$invalid = false;

          }
          $rootScope.clearChanges();
          VersionAndUseService.findById(published.id).then(function(inf) {
            $rootScope.versionAndUseMap[inf.id] = inf;
            if ($rootScope.versionAndUseMap[inf.sourceId]) {
              $rootScope.versionAndUseMap[inf.sourceId].deprecated = true;

            }

          });
        });
      });
    });
  };
  $rootScope.canCreateNewVersion = function(element) {
    if (element.status !== "PUBLISHED" || element.scope == "HL7STANDARD") {
      return false;
    } else if ($rootScope.versionAndUseMap[element.id] && $rootScope.versionAndUseMap[element.id].deprectaed) {
      console.log($rootScope.versionAndUseMap[element.id].deprectaed);
      return false;

    } else {
      return true;
    }

  }
  $rootScope.publishDatatype = function(datatype) {
    console.log("publisheing")

    $rootScope.containUnpublished = false;
    $rootScope.unpublishedTables = [];
    $rootScope.unpublishedDatatypes = [];
    $rootScope.ContainUnpublished(datatype);

    if ($rootScope.containUnpublished) {
      $rootScope.abortPublish(datatype);
      datatype.status = "UNPUBLISHED";
    } else {
      $rootScope.confirmPublish(datatype);

    }
  };
  $rootScope.confirmSwitch = function(source, dest) {
    var modalInstance = $modal.open({
      templateUrl: 'confirmSwitch.html',
      controller: 'confirmSwitch',
      resolve: {
        source: function() {
          return source;
        },
        dest: function() {
          return dest;
        },

      }
    });
    modalInstance.result.then(function() {
      $rootScope.replaceElement(source, dest);

    });
  };
  $scope.getDatatypeForUpgrade = function(id) {




  }












  $rootScope.getGroupNodeName = function(node) {
    return node.position + "." + node.name;
  };

  $rootScope.getFieldNodeName = function(node) {
    return  node.position + "."  + node.name;
  };

  $rootScope.getComponentNodeName = function(node) {
    return node.position + "." + node.name;
  };

  $rootScope.getDatatypeNodeName = function(node) {
    return node.position + "." + node.name;
  };

  $rootScope.onColumnToggle = function(item) {
    $rootScope.viewSettings.save();
  };

  $rootScope.getDatatypeLevelConfStatements = function(element) {
    return DatatypeService.getDatatypeLevelConfStatements(element);
  };

  $rootScope.getDatatypeLevelPredicates = function(element) {
    return DatatypeService.getDatatypeLevelPredicates(element);
  };

  $rootScope.isDatatypeSubDT = function(component) {
    return DatatypeService.isDatatypeSubDT(component, $rootScope.datatype);
  };



  $rootScope.setUsage = function(node) {
    ElementUtils.setUsage(node);
  };


  $rootScope.findDatatypeInLibrary = function(datatypeId, datatypeLibary) {
    if (datatypeLibary.children) {
      for (var i = 0; i < datatypeLibary.children.length; i++) {
        if (datatypeLibary.children[i].id === id) {
          return datatypeLibary.children[i];
        }
      }
    }
    return null;
  };


  $rootScope.openConfirmLeaveDlg = function() {
      // if ($rootScope.modalInstance != undefined && $rootScope.modalInstance != null && $rootScope.modalInstance.opened) {
      //   $rootScope.modalInstance.close();
      // }

      var validForm=$rootScope.isValidData($rootScope.currentData);

      console.log(validForm);
      $rootScope.modalInstance = $mdDialog.show({
          templateUrl: 'ConfirmLeaveDlg.html',
          controller: 'ConfirmLeaveDlgCtrl',
          locals:{valid:validForm}
      });
      return $rootScope.modalInstance;
      // };
  };

    $rootScope.isValidCoConstraints = function() {
        if($rootScope.segment.coConstraintsTable){
            if($rootScope.segment.coConstraintsTable.ifColumnDefinition && $rootScope.segment.coConstraintsTable.ifColumnData){
                var tempIfData = [];
                for(var i = 0; i < $rootScope.segment.coConstraintsTable.ifColumnData.length; i++){
                    if($rootScope.segment.coConstraintsTable.ifColumnData[i]){
                        if($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData){
                            if($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value){
                                if($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value === '') return false;

                                else{
                                    if(tempIfData.indexOf($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value) > -1) return false;
                                    tempIfData.push($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value);
                                }
                            } else return false;
                        } else return false;
                    } else return false;
                }
            } else return false;

            if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
                for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
                    var def = $rootScope.segment.coConstraintsTable.thenColumnDefinitionListForDisplay[i];
                    if(def && def.id){
                        if(def.path === '2'){
                            if(def.constraintType === 'dmf'){
                                var thenDataList = $rootScope.segment.coConstraintsTable.thenMapData[def.id];
                                if(thenDataList){
                                    for(var j = 0; j < thenDataList.length; j++){
                                        var data = thenDataList[j];
                                        if(data){
                                            if(data.valueData.value){
                                                var foundName = _.find($rootScope.dynamicMappingTable.codes, function(code){ return code.value === data.valueData.value; });
                                                if(foundName) {
                                                    if(!data.datatypeId || data.datatypeId === '') return false;
                                                    var found = _.find($rootScope.igdocument.profile.datatypeLibrary.children, function(link){ return link.id === data.datatypeId; });
                                                    if(!found) return false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    };
    $rootScope.isValidData=function (currentData) {

        if(currentData.type=='table'){
           return  $scope.isValidTable()&&$rootScope.isValidFrom();
        }else if(currentData.type=='segment'&&currentData.name=='OBX'){
            return $rootScope.isValidCoConstraints()&&$rootScope.isValidFrom();

        }else{
            return $rootScope.isValidFrom();
        }

    };

    $rootScope.isValidFrom=function(){
      console.log($scope.editForm);
      if($scope.editForm){
        return $scope.editForm.$valid;
      }
      return true;

    };

    $scope.isValidTable = function () {
        var valueCodeSystemList = [];
        var labelCodeSystemList = [];

        for (var i = 0; i < $rootScope.table.codes.length; i++) {
            var value = $rootScope.table.codes[i].value;
            var label = $rootScope.table.codes[i].label;
            var codeSystem = $rootScope.table.codes[i].codeSystem;

            if(!value || value === '') return false;
            if(!label || label === '') return false;
            if(!codeSystem || codeSystem === '') return false;

            var valueCodeSystem = value + codeSystem;
            // var labelCodeSystem = label + codeSystem;


            if ($.inArray(valueCodeSystem,valueCodeSystemList) === -1) {
                valueCodeSystemList.push(valueCodeSystem);
            }else {
                return false;
            }

            // if ($.inArray(labelCodeSystem,labelCodeSystemList) === -1) {
            //     labelCodeSystemList.push(labelCodeSystem);
            // }else {
            //     return false;
            // }
        }
        return true;
    };






  $rootScope.displayNullView = function() {
    $rootScope.subview = 'Blank.html';
  }

  $rootScope.Activate = function(param) {
    $rootScope.activeModel = param;
  }


  var vm = this;

  //        $scope.$on("getMenuState", function (event, data) {
  //            $scope.$apply(function () {
  //                vm.opened = data;
  //            });
  //        });
  //
  //        this.toggleNavigation = function() {
  //            $mdSidenav('navigation-drawer').toggle();
  //        };

  $scope.checkedNavigation = false;
  $scope.toggleNavigation = function() {
    $scope.checkedNavigation = !$scope.checkedNavigation;
  };


}]);


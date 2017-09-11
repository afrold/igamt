/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('AddSegmentDlg',
  function ($scope, $rootScope, $mdDialog, hl7Version, $http, SegmentService, SegmentLibrarySvc, DatatypeService, DatatypeLibrarySvc, TableService, TableLibrarySvc, IgDocumentService) {

    $scope.selectedSegments = [];
    $scope.checkedExt = true;
    $scope.NocheckedExt = true;

    $scope.addseg = function (segment) {
      $scope.selectedSegments.push(segment);
      console.log($scope.selectedSegments);

    };
    $scope.checkExist = function (segment) {
      // if ($scope.selectedSegments.indexOf(segment) !== -1) {
      //     return true;
      // }
      for (var i = 0; i < $scope.selectedSegments.length; i++) {
        if ($scope.selectedSegments[i].id === segment.id) {
          return true;
        }
      }
      return false;
    }
    $scope.checkExt = function (segment) {
      console.log(segment);
      $scope.checkedExt = true;
      $scope.NocheckedExt = true;
      if (segment.ext === "") {
        $scope.NocheckedExt = false;
        return $scope.NocheckedExt;
      }
      for (var i = 0; i < $rootScope.segments.length; i++) {
        if ($rootScope.segments[i].name === segment.name && $rootScope.segments[i].ext === segment.ext) {
          $scope.checkedExt = false;
          return $scope.checkedExt;
        }
      }
      console.log($scope.selectedSegments.indexOf(segment));
      for (var i = 0; i < $scope.selectedSegments.length; i++) {
        if ($scope.selectedSegments.indexOf(segment) !== i) {
          if ($scope.selectedSegments[i].name === segment.name && $scope.selectedSegments[i].ext === segment.ext) {
            $scope.checkedExt = false;
            return $scope.checkedExt;
          }
        }

      }

      return $scope.checkedExt;
    };
    $scope.addsegFlv = function (segment) {
      var newSegment = angular.copy(segment);
      newSegment.participants = [];
      newSegment.scope = 'USER';
      newSegment.id = new ObjectId().toString();
      newSegment.libIds = [];
      newSegment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
      newSegment.ext = $rootScope.createNewExtension(newSegment.ext);

      if (newSegment.fields != undefined && newSegment.fields != null && newSegment.fields.length != 0) {
        for (var i = 0; i < newSegment.fields.length; i++) {
          newSegment.fields[i].id = new ObjectId().toString();
        }
      }


      var dynamicMappings = newSegment['dynamicMappings'];
      if (dynamicMappings != undefined && dynamicMappings != null && dynamicMappings.length != 0) {
        angular.forEach(dynamicMappings, function (dynamicMapping) {
          dynamicMapping.id = new ObjectId().toString();
          angular.forEach(dynamicMapping.mappings, function (mapping) {
            mapping.id = new ObjectId().toString();
          });
        });
      }
      $scope.selectedSegments.push(newSegment);
    }
    $scope.deleteSeg = function (segment) {
      var index = $scope.selectedSegments.indexOf(segment);
      if (index > -1) $scope.selectedSegments.splice(index, 1);
    };

    var listHL7Versions = function () {
      return $http.get('api/igdocuments/findVersions', {
        timeout: 60000
      }).then(function (response) {
        var hl7Versions = [];
        var length = response.data.length;
        for (var i = 0; i < length; i++) {
          hl7Versions.push(response.data[i]);
        }
        console.log(hl7Versions);
        return hl7Versions;
      });
    };


    var init = function () {
      listHL7Versions().then(function (versions) {
        //$scope.versions = versions;
        var v = [];
        for (var i = 0; i < versions.length; i++) {
          if (versions.indexOf(hl7Version) <= i) {
            v.push(versions[i]);
          }
        }

        $scope.version1 = hl7Version;
        $scope.versions = v;
        var scopes = ['HL7STANDARD'];
        SegmentService.getSegmentsByScopesAndVersion(scopes, hl7Version).then(function (result) {
          console.log("result");
          console.log(result);

          $scope.hl7Segments = result.filter(function (current) {
            return $rootScope.segments.filter(function (current_b) {
                return current_b.id == current.id;
              }).length == 0
          });


          console.log("addSegment scopes=" + scopes.length);


        });
      });

    };
    init();
    var secretEmptyKey = '[$empty$]'
    $scope.segComparator = function (seg, viewValue) {

      return viewValue === secretEmptyKey || ('' + seg).toLowerCase().indexOf(('' + viewValue).toLowerCase()) > -1;
    };


    $scope.setVersion = function (version) {
      console.log($scope.selectedSegments);
      $scope.version1 = version;
      var scopes = ['HL7STANDARD'];
      SegmentService.getSegmentsByScopesAndVersion(scopes, version).then(function (result) {
        console.log("result");
        console.log(result);

        $scope.hl7Segments = result.filter(function (current) {
          return $rootScope.segments.filter(function (current_b) {
              return current_b.id == current.id;
            }).length == 0
        });


        console.log("addSegment scopes=" + scopes.length);


      });
    }

    console.log("=----");
    console.log($scope.hl7Segments);
    $scope.isInSegs = function (segment) {

      if (segment && $scope.hl7Segments.indexOf(segment) === -1) {
        return false;
      } else {
        return true;
      }

    };
    $scope.selectSeg = function (segment) {
      $scope.newSegment = segment;
    };
    $scope.selected = function () {
      return ($scope.newSegment !== undefined);
    };
    $scope.unselect = function () {
      $scope.newSegment = undefined;
    };
    $scope.isActive = function (id) {
      if ($scope.newSegment) {
        return $scope.newSegment.id === id;
      } else {
        return false;
      }
    };


    $scope.ok = function () {
      // var newLink = angular.fromJson({
      //     id: $scope.newSegment.id,
      //     name: $scope.newSegment.name
      // });

      $scope.selectFlv = [];
      var newLinks = [];
      for (var i = 0; i < $scope.selectedSegments.length; i++) {
        if ($scope.selectedSegments[i].scope === 'USER') {
          $scope.selectFlv.push($scope.selectedSegments[i]);
        } else {
          newLinks.push({
            id: $scope.selectedSegments[i].id,
            name: $scope.selectedSegments[i].name
          })
        }
      }

      // for (var i = 0; i < $scope.selectedSegments.length; i++) {
      //     newLinks.push({
      //         id: $scope.selectedSegments[i].id,
      //         name: $scope.selectedSegments[i].name
      //     })
      // }


      console.log("newLinks");
      console.log(newLinks);
      $rootScope.usedDtLink = [];
      $rootScope.usedVsLink = [];
      for (var i = 0; i < $scope.selectedSegments.length; i++) {
        $rootScope.fillMaps($scope.selectedSegments[i]);
      }
      SegmentService.saves($scope.selectFlv).then(function (result) {
          for (var i = 0; i < result.length; i++) {
            newLinks.push({
              id: result[i].id,
              name: result[i].name,
              ext: result[i].ext
            })
          }
          console.log("result");
          console.log(result);
          SegmentLibrarySvc.addChildren($rootScope.igdocument.profile.segmentLibrary.id, newLinks).then(function (link) {
            // $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newLinks);
            for (var i = 0; i < newLinks.length; i++) {
              $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newLinks[i]);
            }
            //$rootScope.segments.splice(0, 0, $scope.selectedSegments);
            for (var i = 0; i < $scope.selectedSegments.length; i++) {
              $rootScope.segments.splice(0, 0, $scope.selectedSegments[i]);
            }
            //$rootScope.segment = $scope.newSegment;
            //$rootScope.segmentsMap[$scope.newSegment.id] = $scope.newSegment;
            for (var i = 0; i < $scope.selectedSegments.length; i++) {
              $rootScope.segmentsMap[$scope.selectedSegments[i].id] = $scope.selectedSegments[i];
            }
            //TODO MasterMap need to add Segment

            //                  MastermapSvc.addSegmentObject(newSegment, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
            // $rootScope.filteredSegmentsList.push($scope.newSegment);
            // $rootScope.filteredSegmentsList = _.uniq($rootScope.filteredSegmentsList);
            // $rootScope.$broadcast('event:openSegment', $scope.newSegment);
            $rootScope.msg().text = "segmentAdded";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
            var usedDtId = _.map($rootScope.usedDtLink, function (num, key) {
              return num.id;
            });
            DatatypeService.get(usedDtId).then(function (datatypes) {
              for (var j = 0; j < datatypes.length; j++) {

                $rootScope.fillMaps(datatypes[j]);

              }
              var usedDtId1 = _.map($rootScope.usedDtLink, function (num, key) {
                return num.id;
              });
              var newDatatypesLink = _.difference($rootScope.usedDtLink, $rootScope.igdocument.profile.datatypeLibrary.children);
              DatatypeLibrarySvc.addChildren($rootScope.igdocument.profile.datatypeLibrary.id, newDatatypesLink).then(function () {
                $rootScope.igdocument.profile.datatypeLibrary.children = _.union(newDatatypesLink, $rootScope.igdocument.profile.datatypeLibrary.children);

                DatatypeService.get(usedDtId1).then(function (datatypes) {
                  for (var j = 0; j < datatypes.length; j++) {
                    if (!$rootScope.datatypesMap[datatypes[j].id]) {

                      $rootScope.datatypesMap[datatypes[j].id] = datatypes[j];
                      $rootScope.datatypes.push(datatypes[j]);
                      $rootScope.processElement(datatypes[j]);
                    }
                  }

                  var usedVsId = _.map($rootScope.usedVsLink, function (num, key) {
                    return num.id;
                  });
                  console.log("$rootScope.usedVsLink");

                  console.log($rootScope.usedVsLink);
                  var newTablesLink = _.difference($rootScope.usedVsLink, $rootScope.igdocument.profile.tableLibrary.children);
                  console.log(newTablesLink);

                  TableLibrarySvc.addChildren($rootScope.igdocument.profile.tableLibrary.id, newTablesLink).then(function () {
                    $rootScope.igdocument.profile.tableLibrary.children = _.union(newTablesLink, $rootScope.igdocument.profile.tableLibrary.children);

                    TableService.get(usedVsId).then(function (tables) {
                      for (var j = 0; j < tables.length; j++) {
                        if (!$rootScope.tablesMap[tables[j].id]) {
                          $rootScope.tablesMap[tables[j].id] = tables[j];
                          $rootScope.tables.push(tables[j]);
                          $rootScope.processElement(tables[j]);

                        }
                      }


                      $rootScope.processElement($scope.newSegment);
                      $mdDialog.hide();

                    });
                  });


                });
              });
            });

          })
        },
        function (error) {
          $scope.saving = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        })

    };

    $scope.cancel = function () {
      $mdDialog.hide();
    };
  });

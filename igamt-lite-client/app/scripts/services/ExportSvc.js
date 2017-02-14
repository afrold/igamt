angular.module('igl').factory(
    'ExportSvc',

    function ($rootScope, $modal, $cookies, GVTSvc,StorageService) {

        var svc = this;


        svc.exportAsXMLByMessageIds = function (id, mids, xmlFormat) {
            var form = document.createElement("form");

            if (xmlFormat === 'Validation') {
                form.action = $rootScope.api('api/igdocuments/' + id + '/export/Validation/' + mids);
            } else if (xmlFormat === 'Display') {
                form.action = $rootScope.api('api/igdocuments/' + id + '/export/Display/' + mids);
            } else if (xmlFormat === 'Gazelle') {
                form.action = $rootScope.api('api/igdocuments/' + id + '/export/Gazelle/' + mids);
            }
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        };

        svc.exportAsZIPToGVT = function (id, mids) {
            if ($rootScope.gvtLoginDialog && $rootScope.gvtLoginDialog != null && $rootScope.gvtLoginDialog.opened) {
                $rootScope.gvtLoginDialog.dismiss('cancel');
            }
            $rootScope.gvtLoginDialog = $modal.open({
                backdrop: 'static',
                keyboard: 'false',
                controller: 'GVTLoginCtrl',
                size: 'lg',
                templateUrl: 'views/gvt/login.html',
                resolve: {
                    user: function () {
                        return { username:null, password: null };
                    }
                }
            });

            $rootScope.gvtLoginDialog.result.then(function (auth) {
                $http({
                    url: 'api/igdocuments/' + id + '/export/gvt',
                    method: "POST",
                    data: { 'mids' : mids, auth:auth}
                })
                    .then(function(response) {
                        // success
                    },
                    function(response) { // optional
                        // failed
                    });

            });
        };


        return svc;
    })
;

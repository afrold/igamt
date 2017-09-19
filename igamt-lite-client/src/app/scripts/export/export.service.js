angular.module('igl').factory(
    'ExportSvc',

    function ($rootScope, $modal, $cookies, GVTSvc,StorageService) {

        var svc = this;


        svc.exportAsXMLByMessageIds = function (id, mids, xmlFormat) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + id + '/export/' + xmlFormat + '/' + mids);
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

        svc.exportAsXMLByCompositeProfileIds = function (id, mids, xmlFormat) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + id + '/export/' + xmlFormat + '/Composite/' + mids);
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

        return svc;
    })
;

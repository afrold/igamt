angular.module('igl').factory(
    'ExportSvc',

    function ($rootScope, $modal, $cookies) {

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
        
        
        return svc;
    });

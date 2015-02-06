/**
 * Created by haffo on 2/2/15.
 */


(function(ng){

    if(!document.URL.match(/\?fake/)){
        return;
    }

    init();

    function init(){
        ng.module ('igl').config(function($provide){

        }).run(function($httpBackEnd){
            //fake/api/getMotion
            $httpBackend.whenGET('/fake/api/v1/user/1/profiles').respond(function(method, url, data, headers) {
                return
            });
        });
    }

})(angular);
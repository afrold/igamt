/**
 * Created by haffo on 3/3/16.
 */

angular.module('igl').filter('flavors',function(){
    return function(inputArray,name){
        return inputArray.filter(function(item){
            return item.name === name || angular.equals(item.name,name);
        });
    };
});
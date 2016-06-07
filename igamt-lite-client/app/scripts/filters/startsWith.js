angular.module('igl').filter('startsWith', function() {
    return function(input, alpha, property){
		
		var _out = [];

		if(angular.isUndefined(alpha)){
			_out = input;
		}
		
		angular.forEach(input, function(item){
			var filteritem = item;
			if(angular.isDefined(property) && item[property]){
				filteritem = item[property];
			}

			if(_(filteritem).startsWith(alpha)){
				_out.push(item);
			}
		});
		return _out;
	};
});

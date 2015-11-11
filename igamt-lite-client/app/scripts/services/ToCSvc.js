angular.module('igl').factory ('ToCSvc', function() {
	
	var toc = this;
	
	var headers = ["Metadata","Data Types","Segments","Messages","Value Sets"];

	toc.getRoot = function(profile) {
		
		var root = [];
		_.each(headers, function(header){
			root.push(toc.getEntry(null, header));
		});
		
		return root;
	}
	
	toc.getEntries = function(profile) {
		return	[getEntry(null, "Data Types"),
		      	getEntry(null, "Segments"),
		      	getEntry(null, "Messages"),
		      	getEntry(null, "Value Sets")];
	}
	
	toc.getBranches = function(parent, children) {
		
		var rows = [];
		_.each(children, function(child) {
			rows.push(toc.getRow(parent, child.title));
		});
		return rows;

	}
	
	toc.getEntry = function(parent, name) {
		return {
		    state: 'expanded',
		    row: name,
		    parentRow: parent,
		    aggregations: []
		};
	}
	
//	toc.getEntry = function(parent, name) {
//		return {
//		    state: 'expanded',
//		    row: name,
//		    parentRow: parent,
//		    aggregations: [{
//		      type: 'count',
//		      col: 'gridCol',
//		      value: 2,
//		      label: 'count: ',
//		      rendered: 'count: 2'
//		    }]
//		};
//	}
	
	return toc;
})
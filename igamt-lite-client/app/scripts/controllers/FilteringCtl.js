angular
  .module('igl')
  .controller(
      'FilteringCtl',
          function ($scope, $rootScope) {
            var ctl = this;

           // $scope.filtermsgmodel = $rootScope.igdocument.profile.messages.children;
          
          $scope.selectAll= function(){

            $rootScope.selectedMessages=$rootScope.igdocument.profile.messages.children;
            $rootScope.selectedSegments=$rootScope.segments;
            $rootScope.selectedDataTypes=$rootScope.datatypes;
            $rootScope.selectedTables=$rootScope.tables;

          } 
          $scope.addToSelection  =function(item) {
                if($rootScope.selectedMessages.length===$rootScope.igdocument.profile.messages.children.length){
                      $rootScope.selectedMessages=[];
                      $rootScope.selectedSegments=[];
                      $rootScope.selectedDataTypes=[];
                      $rootScope.selectedTables=[]
                        }

                for(var j=0; j< $rootScope.igdocument.profile.messages.children.length; j++){
                  
                    if(item.id===$rootScope.igdocument.profile.messages.children[j].id){
                      $rootScope.selectedMessages.push($rootScope.igdocument.profile.messages.children[j]);
                      for(var i=0; i<$rootScope.igdocument.profile.messages.children[j].children.length; i++){
                      console.log($rootScope.igdocument.profile.messages.children[j].children[i]);
                      var child=$rootScope.igdocument.profile.messages.children[j].children[i];
                      if( child.type==="segmentRef"){
                        var seg = $rootScope.segmentsMap[child.ref.id];
                        console.log(seg);
                        var temp=[];
                        temp.push(seg);
                        $rootScope.selectedSegments=_.union($rootScope.selectedSegments,temp);
                        $rootScope.selectedDataTypes=_.union($rootScope.selectedDataTypes, $scope.getDatataypeFromSegment(seg));
                        $scope.getTablesFromSegment(seg);


                        console.log($rootScope.selectedSegments);
                        }else if(child.type==="group"){
                            console.log($rootScope.selectedSegments)
                            $scope.getSegmentsFromgroup(child);
                            console.log($rootScope.selectedSegments)
                        }
                      }
                   }
              }
            }




            $scope.getSegmentsFromgroup= function(group){

              //_.union($rootScope.selectedSegments,temp);
              for( var i=0; i<group.children.length; i++){
                if(group.children[i].type === "segmentRef"){
                        console.log("IN IF ");
                        var segment = $rootScope.segmentsMap[group.children[i].ref.id];
                        var temp2=[];
                        temp2.push(segment);
                        $rootScope.selectedSegments=_.union($rootScope.selectedSegments,temp2);
                        $rootScope.selectedDataTypes=_.union($rootScope.selectedDataTypes, $scope.getDatataypeFromSegment(segment));
                        $scope.getTablesFromSegment(segment);

                    
              }else if(group.children[i].type==="group"){
                        console.log("group case ");
                        $scope.getSegmentsFromgroup(group.children[i]);
              }
            }

          }
            $scope.getDatataypeFromSegment=function(seg){
              var data=[];
              for(var i=0; i<seg.fields.length; i++){
                console.log(seg.fields[i].datatype.id);
                var datatype = $rootScope.datatypesMap[seg.fields[i].datatype.id];
                console.log(datatype);
                $scope.getTablesFromDatatype(datatype);
                var temp=[];
                temp.push(datatype);
                temp=_.union(temp, $scope.getDatatypeFromDatatype(datatype));
                data=_.union(data,temp);

              }
              return data;
            }

              $scope.getTablesFromSegment=function(seg){
              var tables=[];
              for(var i=0; i<seg.fields.length; i++){
                if(seg.fields[i].table!=null){
                var table = $rootScope.tablesMap[seg.fields[i].table.id];
                //console.log(datatype);
                var temp=[];
                
                temp.push(table);
                tables=_.union(tables,temp);
                $rootScope.selectedTables=_.union($rootScope.selectedTables,tables);
                }

              }
            
            }


            $scope.getDatatypeFromDatatype = function(datatype){
              var data=[];
              if(datatype.components.length===0){
                $scope.getTablesFromDatatype(datatype);
                return 0;
              }
              else {

                for(var i=0; i<datatype.components.length; i++){

                  var temp= [];
                  temp.push($rootScope.datatypesMap[datatype.components[i].datatype.id]);
                  console.log($rootScope.tablesMap[datatype.components[i].datatype.id]);
                  $scope.getTablesFromDatatype($rootScope.datatypesMap[datatype.components[i].datatype.id]);
                  data=_.union(data,temp);
                }
              }
              return data;
            }

            $scope.getTablesFromDatatype = function (datatype){
             // var tables=[];
             console.log(datatype);
             if(datatype===undefined){
             }
              if(datatype.components&&datatype.components.length>0){
               for (var i = datatype.components.length - 1; i >= 0; i--) {
                  if(datatype.components[i].table && datatype.components[i].table!=null){
                        var table =$rootScope.tablesMap[datatype.components[i].table.id];
                        var tmp=[];
                        tmp.push(table);
                        $rootScope.selectedTables=_.union($rootScope.selectedTables,tmp);
                  }                 
               }
              }
            }





            $scope.removeFromSelection= function(item){
              console.log(item.id);
              console.log($rootScope.selectedMessages);

                    for(var j=0; j< $rootScope.selectedMessages.length; j++){
                  
                    if(item.id===$rootScope.selectedMessages[j].id){
                                    console.log("true");

                      $rootScope.selectedMessages.splice(1,j); 

                   }
              }

            }
            $scope.getRelatedSegments= function(message){


            }
         
            $scope.filtermsgdata = function(){
                return FilteringSvc.getMsgdata();
            };
            
            $scope.filtermsgsettings = function(){
                return FilteringSvc.getMsgsettings();
            };

            $scope.filtermsgsettings = function(){
                return FilteringSvc.getMsgsettings();
            };

            $scope.filterusagesmodel = function(){
                return FilteringSvc.getUsagesmodel();
            };

            $scope.filterusagesdata = function(){
                return FilteringSvc.getUsagesdata();
            };

            $scope.filterusagessettings = function(){
                return FilteringSvc.getUsagessettings();
            };

            $scope.filterusagetexts = function(){
                return FilteringSvc.getUsagestexts();
            };



//            $rootScope.$on('event:loadFilter', function (event, igdocument) {
//         //       FilteringSvc.loadMessages(igdocument);
//  //              FilteringSvc.loadUsages();
//            });

//            $rootScope.$on('event:loadMastermap', function (event, igdocument) {
//              MastermapSvc.parseIg(igdocument);
//            });
            
            
  }
);

/**
 * Created by haffo on 2/9/15.
 */


angular.module('igl').service('CustomProfileDataModel', function CustomProfileDataModel() {
    this.data =  [
        {
            "id": "2",
            "type": "Constrainable",
            "hl7Version": "2.5.1",
            "schemaVersion": "2.5",
            "metaData": {
                "name": "My Profile 1",
                "orgName": "",
                "version": "1",
                "status": "",
                "topics": ""
            },
            "encodings": [
                {
                    "value": "ER7"
                },
                {
                    "value": "XML"
                }
            ],
            "messages": [
                {
                    "id": "1",
                    "messages": [
                        {
                            "id": "1",
                            "type": "ORU",
                            "event": "R01",
                            "structID": "ORU_R01",
                            "description": "ORU/ACK - Unsolicited transmission of an observation message",
                            "segmentRefOrGroups": []
                        },
                        {
                            "id": "2",
                            "type": "ACK",
                            "event": "R01",
                            "structID": "ACK_R01",
                            "description": "ORU/ACK - Unsolicited transmission of an observation message",
                            "segmentRefOrGroups": []
                        }
                    ]
                }
            ],
            "segments": [],
            "datatypes": [],
            "conforamnceStatements": [],
            "predicates": []
        }
    ];


    this.getData = function() {
        return this.data;
    };

    this.setData = function(data) {
        this.data = data;
    };


    this.findAll = function() {
        return this.getData();
    };


});

angular.module('igl').service('PredefinedProfileDataModel', function PredefinedProfileDataModel() {

    this.data =  [
        {
            "id": "1",
            "type": "Constrainable",
            "hl7Version": "2.5.1",
            "schemaVersion": "2.5",
            "metaData": {
                "name": "VXU_V04",
                "orgName": "NIST",
                "version": "1",
                "status": "",
                "topics": ""
            },
            "encodings": [
                {
                    "value": "ER7"
                },
                {
                    "value": "XML"
                }
            ],
            "messages": [
                {
                    "id": "1",
                    "messages": [
                        {
                            "id": "1",
                            "type": "ORU",
                            "event": "R01",
                            "structID": "ORU_R01",
                            "description": "ORU/ACK - Unsolicited transmission of an observation message",
                            "segmentRefOrGroups": []
                        },
                        {
                            "id": "2",
                            "type": "ACK",
                            "event": "R01",
                            "structID": "ACK_R01",
                            "description": "ORU/ACK - Unsolicited transmission of an observation message",
                            "segmentRefOrGroups": []
                        }
                    ]
                }
            ],
            "segments": [],
            "datatypes": [],
            "conforamnceStatements": [],
            "predicates": []
        }
    ];

    this.getData = function() {
        return this.data;
    };

    this.setData = function(data) {
        this.data = data;
    };


    this.findAll = function() {
        return this.getData();
    };




});
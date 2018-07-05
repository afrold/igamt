"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var testing_1 = require("@angular/core/testing");
var indexed_db_service_1 = require("./indexed-db.service");
describe('IndexedDbService', function () {
    beforeEach(function () {
        testing_1.TestBed.configureTestingModule({
            providers: [indexed_db_service_1.IndexedDbService]
        });
    });
    it('should be created', testing_1.inject([indexed_db_service_1.IndexedDbService], function (service) {
        expect(service).toBeTruthy();
    }));
});

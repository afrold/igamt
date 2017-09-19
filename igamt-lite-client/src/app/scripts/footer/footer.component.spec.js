"use strict";
var testing_1 = require("@angular/core/testing");
var footer_component_1 = require("./footer.component");
describe('App', function () {
    beforeEach(function () {
        testing_1.TestBed.configureTestingModule({
            declarations: [footer_component_1.FooterComponent],
            providers: []
        });
    });
    it('should work', function () {
        var fixture = testing_1.TestBed.createComponent(footer_component_1.FooterComponent);
        expect(fixture.componentInstance instanceof footer_component_1.FooterComponent).toBe(true, 'should create FooterComponent');
    });
});
//# sourceMappingURL=footer.component.spec.js.map
"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var common_1 = require("@angular/common");
var platform_browser_1 = require("@angular/platform-browser");
var animations_1 = require("@angular/platform-browser/animations");
var static_1 = require("@angular/upgrade/static");
var router_1 = require("@angular/router");
var primeng_1 = require("primeng/primeng");
var app_component_1 = require("./app.component");
var footer_component_1 = require("./scripts/footer/footer.component");
var toc_service_1 = require("./scripts/table-of-content/tree/toc.service");
var toc_component_1 = require("./scripts/table-of-content/tree/toc.component");
var AppModule = (function () {
    function AppModule() {
    }
    AppModule.prototype.ngDoBootstrap = function () {
    };
    return AppModule;
}());
AppModule = __decorate([
    core_1.NgModule({
        imports: [
            common_1.CommonModule,
            platform_browser_1.BrowserModule,
            primeng_1.TreeModule,
            animations_1.BrowserAnimationsModule,
            static_1.UpgradeModule,
            router_1.RouterModule.forRoot([], { initialNavigation: false })
        ],
        providers: [
            toc_service_1.NodeService
        ],
        declarations: [app_component_1.AppComponent, footer_component_1.FooterComponent, toc_component_1.TreeComponent],
        bootstrap: [app_component_1.AppComponent],
        entryComponents: [footer_component_1.FooterComponent, toc_component_1.TreeComponent]
    })
], AppModule);
exports.AppModule = AppModule;
//# sourceMappingURL=app.module.js.map
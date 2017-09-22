"use strict";
var platform_browser_dynamic_1 = require("@angular/platform-browser-dynamic");
var static_1 = require("@angular/upgrade/static");
var app_module_1 = require("./app/app.module");
var router_1 = require("@angular/router");
var footer_component_1 = require("./app/scripts/footer/footer.component");
var static_2 = require("@angular/upgrade/static");
angular.module('igl')
    .directive('igamtFooter', static_2.downgradeComponent({ component: footer_component_1.FooterComponent
}));
platform_browser_dynamic_1.platformBrowserDynamic().bootstrapModule(app_module_1.AppModule).then(function (platformRef) {
    var upgrade = platformRef.injector.get(static_1.UpgradeModule);
    upgrade.bootstrap(document.documentElement, ['igl']);
    platformRef.injector.get(router_1.Router).initialNavigation();
});
//# sourceMappingURL=main.js.map
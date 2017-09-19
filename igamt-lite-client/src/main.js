"use strict";
var platform_browser_dynamic_1 = require("@angular/platform-browser-dynamic");
var static_1 = require("@angular/upgrade/static");
var app_module_1 = require("./app.module");
var router_1 = require("@angular/router");
platform_browser_dynamic_1.platformBrowserDynamic().bootstrapModule(app_module_1.AppModule).then(function (platformRef) {
    var upgrade = platformRef.injector.get(static_1.UpgradeModule);
    upgrade.bootstrap(document.documentElement, ['igl']);
    platformRef.injector.get(router_1.Router).initialNavigation();
});
//# sourceMappingURL=main.js.map
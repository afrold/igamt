"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var common_1 = require("@angular/common");
var configuration_1 = require("./configuration");
var configuration_routing_module_1 = require("./configuration-routing.module");
var primeng_1 = require("primeng/primeng");
var ConfigurationModule = (function () {
    function ConfigurationModule() {
    }
    return ConfigurationModule;
}());
ConfigurationModule = __decorate([
    core_1.NgModule({
        imports: [
            common_1.CommonModule,
            configuration_routing_module_1.ConfigurationRoutingModule,
            primeng_1.AccordionModule,
            primeng_1.ButtonModule,
            primeng_1.TabViewModule,
            primeng_1.GrowlModule
        ],
        declarations: [
            configuration_1.Configuration
        ]
    })
], ConfigurationModule);
exports.ConfigurationModule = ConfigurationModule;

"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
require("../../node_modules/primeng/resources/themes/omega/theme.css");
require("../../node_modules/primeng/resources/primeng.min.css");
require("../..//node_modules/font-awesome/css/font-awesome.min.css");
require("./lib/material-custom/bootstrap-material-design.min.css");
require("./lib/material-custom/ripples.min.css");
require("../bower_components/froala-wysiwyg-editor/css/froala_editor.min.css");
require("../bower_components/froala-wysiwyg-editor/css/froala_editor.pkgd.min.css");
require("../bower_components/froala-wysiwyg-editor/css/froala_style.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/char_counter.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/code_view.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/colors.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/draggable.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/emoticons.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/file.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/fullscreen.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/image_manager.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/image.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/line_breaker.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/quick_insert.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/table.min.css");
require("../bower_components/froala-wysiwyg-editor/css/plugins/video.min.css");
require("../bower_components/ng-notifications-bar/dist/ngNotificationsBar.min.css");
require("../bower_components/angular-ui-tree/dist/angular-ui-tree.css");
require("../bower_components/angular-block-ui/dist/angular-block-ui.css");
require("../bower_components/angular-ui-select/dist/select.css");
require("../bower_components/angular-ui-notification/dist/angular-ui-notification.css");
require("../bower_components/angular-object-diff/dist/angular-object-diff.css");
require("../bower_components/ng-tags-input/ng-tags-input.css");
require("../bower_components/angularjs-slider/dist/rzslider.css");
require("../bower_components/angular-material/angular-material.css");
require("../bower_components/bootstrap/dist/css/bootstrap.css");
require("../assets/css/general.css");
require("../assets/css/igamt.css");
require("../assets/css/toc.css");
require("../assets/css/nav.css");
require("../assets/css/dialog.css");
require("./lib/angular-treetable/style/jquery.treetable.css");
require("./lib/angular-treetable/style/jquery.treetable.theme.default.css");
var AppComponent = (function () {
    function AppComponent() {
    }
    return AppComponent;
}());
AppComponent = __decorate([
    core_1.Component({
        selector: 'igamt-root',
        templateUrl: './app.component.html'
    })
], AppComponent);
exports.AppComponent = AppComponent;
//# sourceMappingURL=app.component.js.map
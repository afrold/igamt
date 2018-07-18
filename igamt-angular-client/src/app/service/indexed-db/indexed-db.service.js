"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var dexie_1 = require("dexie");
var LinkDatabase = (function (_super) {
    __extends(LinkDatabase, _super);
    function LinkDatabase() {
        var _this = _super.call(this, 'LinkDatabase') || this;
        _this.version(1).stores({
            datatypeLink: '++id,label',
            tableLink: '++id,label'
        });
        return _this;
    }
    return LinkDatabase;
}(dexie_1.default));
var IndexedDbService = (function () {
    function IndexedDbService() {
        var _this = this;
        this.igDatabase = new LinkDatabase();
        this.igDatabase.transaction('rw', this.igDatabase.datatypeLink, function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.igDatabase.datatypeLink.add({ 'id': '1', 'label': 'HD' })];
                    case 1:
                        _a.sent();
                        return [4 /*yield*/, this.igDatabase.datatypeLink.add({ 'id': '2', 'label': 'ST' })];
                    case 2:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); });
        this.igDatabase.transaction('rw', this.igDatabase.tableLink, function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.igDatabase.tableLink.add({ 'id': '1', 'label': '0001' })];
                    case 1:
                        _a.sent();
                        return [4 /*yield*/, this.igDatabase.tableLink.add({ 'id': '2', 'label': '0304' })];
                    case 2:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); });
    }
    IndexedDbService.prototype.getDatatypeLinks = function (callback) {
        var _this = this;
        this.igDatabase.transaction('rw', this.igDatabase.datatypeLink, function () { return __awaiter(_this, void 0, void 0, function () {
            var datatypeLinks;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.igDatabase.datatypeLink.toArray()];
                    case 1:
                        datatypeLinks = _a.sent();
                        callback(datatypeLinks);
                        return [2 /*return*/];
                }
            });
        }); });
    };
    IndexedDbService.prototype.getDatatypeLink = function (id, callback) {
        var _this = this;
        this.igDatabase.transaction('rw', this.igDatabase.datatypeLink, function () { return __awaiter(_this, void 0, void 0, function () {
            var datatypeLink;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.igDatabase.datatypeLink.get(id)];
                    case 1:
                        datatypeLink = _a.sent();
                        callback(datatypeLink);
                        return [2 /*return*/];
                }
            });
        }); });
    };
    return IndexedDbService;
}());
IndexedDbService = __decorate([
    core_1.Injectable()
], IndexedDbService);
exports.IndexedDbService = IndexedDbService;

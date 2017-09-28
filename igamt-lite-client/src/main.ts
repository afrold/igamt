import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UpgradeModule } from '@angular/upgrade/static';
import { AppModule } from './app/app.module';
import { Router } from '@angular/router';
import { FooterComponent } from './app/scripts/footer/footer.component';
import { SegttComponent } from './app/scripts/segment/segment-structure/segtt.component';
import { SegmetaComponent } from './app/scripts/segment/segment-metadata/segmeta.component';
import {MessageTree} from  './app/scripts/message/message-structure/message.component';
import { downgradeComponent } from '@angular/upgrade/static';

declare var angular: any;
angular.module('igl').directive('igamtFooter',downgradeComponent({ component: FooterComponent }));
angular.module('igl').directive('igamtSegmentStructure',downgradeComponent({ component: SegttComponent }));
angular.module('igl').directive('igamtSegmentMetadata',downgradeComponent({ component: SegmetaComponent }));
angular.module('igl').directive('igamtMessageStructure',downgradeComponent({ component: MessageTree }));


platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.documentElement, ['igl']);
  platformRef.injector.get(Router).initialNavigation();
});

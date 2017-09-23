import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UpgradeModule } from '@angular/upgrade/static';
import { AppModule } from './app/app.module';
import { Router } from '@angular/router';
import { FooterComponent } from './app/scripts/footer/footer.component';
import { SegttComponent } from './app/scripts/segment/segment-structure/segtt.component';
import { downgradeComponent } from '@angular/upgrade/static';

declare var angular: any;
angular.module('igl').directive('igamtFooter',downgradeComponent({ component: FooterComponent }));
angular.module('igl').directive('igamtSegmentTreetable',downgradeComponent({ component: SegttComponent }));

platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.documentElement, ['igl']);
  platformRef.injector.get(Router).initialNavigation();
});

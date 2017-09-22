import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UpgradeModule } from '@angular/upgrade/static';
import { AppModule } from './app/app.module';
import { Router } from '@angular/router';
import { FooterComponent } from './app/scripts/footer/footer.component';
import { JooterComponent } from './app/scripts/segment/treetable/jooter.component';
import { TreetableComponent } from './app/scripts/segment/treetable/treetable.component';
import { downgradeComponent } from '@angular/upgrade/static';



declare var angular: any;
angular.module('igl').directive('igamtJooter',downgradeComponent({ component: JooterComponent }));
angular.module('igl').directive('igamtTreetable',downgradeComponent({ component: TreetableComponent }));

platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.documentElement, ['igl']);
  platformRef.injector.get(Router).initialNavigation();
});

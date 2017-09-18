import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UpgradeModule } from '@angular/upgrade/static';
import { AppModule } from './app.module';
import { Router } from '@angular/router';
import { FooterComponent } from './scripts/footer/footer.component';
import { downgradeComponent } from '@angular/upgrade/static';


declare var angular: any;
angular.module('igl')
  .directive(
    'igamtFooter',
    downgradeComponent({ component: FooterComponent }) as angular.IDirectiveFactory
  );


platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.documentElement, ['igl']);
  platformRef.injector.get(Router).initialNavigation();

});

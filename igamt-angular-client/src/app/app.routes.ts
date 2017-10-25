import {Routes, RouterModule} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';
import {HomeComponent} from './home/home.component';
import {AboutComponent} from './about/about.component';
import {DocumentationComponent} from './documentation/documentation.component';


export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'igDocuments', loadChildren: './igdocuments/igdocument.module#IgDocumentModule'},
  {path: 'datatypeLibraries', loadChildren: './datatype-library/datatype-library.module#DatatypeLibraryModule'},
  {path: 'sharedElements', loadChildren: './shared-elements/shared-elements.module#SharedElementsModule'},
  {path: 'delta', loadChildren: './delta/delta.module#DeltaModule'},
  {path: 'configuration', loadChildren: './configuration/configuration.module#ConfigurationModule'},
  {path: 'search', loadChildren: './search/search.module#SearchModule'},
  {path: 'about', component: AboutComponent},
  {path: 'documentation', component: DocumentationComponent}
];
export const AppRoutes: ModuleWithProviders = RouterModule.forRoot(routes);

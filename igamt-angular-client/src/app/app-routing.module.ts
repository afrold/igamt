import { Routes,RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { AboutComponent } from './about/about.component';
import { DocumentationComponent } from './documentation/documentation.component';

@NgModule({
    imports: [
        RouterModule.forRoot([
            {path: '', component: HomeComponent},
            {path: 'igDocuments', loadChildren: './igdocuments/igdocuments.module#IgdocumentsModule'},
            {path: 'datatypeLibraries', loadChildren: './datatype-libraries/datatype-libraries.module#DatatypeLibrariesModule'},
            {path: 'sharedElements', loadChildren: './shared-elements/shared-elements.module#SharedElementsModule'},
            {path: 'delta', loadChildren: './delta/delta.module#DeltaModule'},
            {path: 'configuration', loadChildren: './configuration/configuration.module#ConfigurationModule'},
            {path: 'search', loadChildren: './search/search.module#SearchModule'},
            {path: 'about', component: AboutComponent},
            {path: 'documentation', component: DocumentationComponent}
        ])
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {}


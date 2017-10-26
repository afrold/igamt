import { Routes,RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { AboutComponent } from './about/about.component';
import { DocumentationComponent } from './documentation/documentation.component';
import {NotFoundComponent} from "./common/404/404.component";

@NgModule({
    imports: [
        RouterModule.forRoot([
            {path: '', component: HomeComponent},
            {path: 'igDocuments', loadChildren: './igdocuments/igdocument.module#IgDocumentModule'},
            {path: 'datatypeLibraries', loadChildren: './datatype-library/datatype-library.module#DatatypeLibraryModule'},
            {path: 'sharedElements', loadChildren: './shared-elements/shared-elements.module#SharedElementsModule'},
            {path: 'delta', loadChildren: './delta/delta.module#DeltaModule'},
            {path: 'configuration', loadChildren: './configuration/configuration.module#ConfigurationModule'},
            {path: 'search', loadChildren: './search/search.module#SearchModule'},
            {path: 'about', component: AboutComponent},
            {path: 'documentation', component: DocumentationComponent},
            {path : '**', component: NotFoundComponent}
        ])
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {}


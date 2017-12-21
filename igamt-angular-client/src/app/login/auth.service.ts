/**
 * Created by ena3 on 12/20/17.
 */
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import {Http, Headers} from '@angular/http';


@Injectable()
export class AuthService {
  isLoggedIn = false;

  // store the URL so we can redirect after logging in
  redirectUrl: string;

  constructor(private  http : Http){

  }

  login(username,password): Observable<boolean> {
    let headers: Headers = new Headers();
    // headers.append("Authorization", "Basic " + btoa(username + ":" + password));
    // headers.append("Content-Type", "application/x-www-form-urlencoded");
    // this.http.get('api/accounts/login', headers).subscribe(data => {
    //   this.isLoggedIn = true;
    //   return this.http.get('api/accounts/cuser').subscribe(user=>{
    //     this.isLoggedIn = true;
    //     return Observable.of(true);
    //   });
    // });
     this.isLoggedIn = true;
    return Observable.of(true);
  }
  logout(): void {
    this.isLoggedIn = false;
  }
}


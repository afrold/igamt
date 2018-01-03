/**
 * Created by ena3 on 12/20/17.
 */
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import {Http, Headers} from '@angular/http';
import {HttpHeaders, HttpClient} from "@angular/common/http";


@Injectable()
export class AuthService {
  isLoggedIn = false;

  // store the URL so we can redirect after logging in
  redirectUrl: string;

  constructor(private  http :HttpClient){

  }

  login(username,password): Observable<boolean> {
    var auth="Basic "+ btoa(username + ":" + password);
    let headers = new HttpHeaders(
      {
        'Authorization':auth,
        'Content-Type': 'application/json'
      }
    );
   // let other_headers=headers.append('Content-Type', 'application/json');
   //  console.log(btoa(username + ":" + password));
   //  other_headers.append('Authorization', "Basic"+ btoa(username + ":" + password));
     console.log(headers);
    this.http.get('api/accounts/login',{headers: headers}).subscribe(data => {
      this.isLoggedIn = true;
      return this.http.get('api/accounts/cuser').subscribe(user=>{
        this.isLoggedIn = true;
        return Observable.of(true);
      });
    });
     this.isLoggedIn = false;
    return Observable.of(false);
  }
  logout(): void {
    this.isLoggedIn = false;
  }
}


/**
 * Created by hnt5 on 10/25/17.
 */
import {Injectable} from "@angular/core";
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from "@angular/router";
import {WorkspaceService, Entity} from "../../../service/workspace/workspace.service";
import {Http} from "@angular/http";

@Injectable()
export class SegmentGuard implements CanActivate {

  constructor(private _ws : WorkspaceService,
              private $http : Http){};

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      let obs = this.$http.get('api/segments/'+route.params['id']).map(res => res.json()).subscribe(data => {
        let ig = this._ws.getCurrent(Entity.IG);
        for(let segment of ig.profile.segmentLibrary.children){
          if(segment.id === data.id){
            this._ws.setCurrent(Entity.SEGMENT, data);
            obs.unsubscribe();
            resolve(true);
          }
        }
        obs.unsubscribe();
        resolve(false);
      });
    });
  }

}

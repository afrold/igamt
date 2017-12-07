import {Injectable} from '@angular/core';

import { ObjectsDatabase } from './objects-database';
import { DatatypesService } from '../datatypes/datatypes.service';

@Injectable()
export class IndexedDbService {

  objectsDatabase;
  changedObjectsDatabase;
  constructor(private datatypesService: DatatypesService) {
    this.objectsDatabase = new ObjectsDatabase('ObjectsDatabase');
    this.changedObjectsDatabase = new ObjectsDatabase('ChangedObjectsDatabase');
    this.objectsDatabase.transaction('rw', this.objectsDatabase.datatypes, async() => {
      this.objectsDatabase.datatypes.clear().then(this.injectDatatypes('588f2d4184ae56b0b8a41197'));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async() => {
      this.changedObjectsDatabase.datatypes.clear();
    });
  }

  public getDatatype (id, callback) {
    let datatype;
    this.changedObjectsDatabase.transaction('r', this.changedObjectsDatabase.datatypes, async() => {
      datatype = await this.changedObjectsDatabase.datatypes.get(id);
      if (datatype != null) {
        callback(datatype.object);
      } else {
        this.objectsDatabase.transaction('r', this.objectsDatabase.datatypes, async() => {
          datatype = await this.objectsDatabase.datatypes.get(id);
          callback(datatype.object);
        });
      }
    });
  }

  private injectDatatypes(igDocumentId) {
    this.datatypesService.getDatatypes('588f2d4184ae56b0b8a41197', this.populateDatatypes.bind(this));
  }

  private populateDatatypes (datatypes) {
    console.log(JSON.stringify(datatypes));
    datatypes.forEach(datatype => {
      this.objectsDatabase.transaction('rw', this.objectsDatabase.datatypes, async() => {
        await this.objectsDatabase.datatypes.add({
          'id': datatype.id,
          'object': datatype
        });
      });
    });
  }
  public saveDatatype(datatype) {
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async() => {
      await this.changedObjectsDatabase.datatypes.add({
        'id': datatype.id,
        'object': datatype
      });
    });
    console.log('save datatype with id ' + datatype.id);
  }

  public saveChangedDatatypes() {
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async () => {
      const changedDatatypes = await this.changedObjectsDatabase.datatypes.toArray();
      this.datatypesService.saveDatatypes(changedDatatypes);
    });
  }
}

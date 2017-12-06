import { Injectable } from '@angular/core';

import { ObjectsDatabase } from './objects-database';

@Injectable()
export class IndexedDbService {

  objectsDatabase;
  changedObjectsDatabase;
  constructor() {
    this.objectsDatabase = new ObjectsDatabase('ObjectsDatabase');
    this.changedObjectsDatabase = new ObjectsDatabase('ChangedObjectsDatabase');
    this.objectsDatabase.transaction('rw', this.objectsDatabase.datatypes, async() => {
      await this.objectsDatabase.datatypes.add({'id': '1', 'object': {'label': 'HD_IZ', 'version': '2.1.5'}});
      await this.objectsDatabase.datatypes.add({'id': '2', 'object': {'label': 'ST', 'version': '2.1.5'}});
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async() => {
      await this.changedObjectsDatabase.datatypes.add({'id': '1', 'object': {'label': 'HD_IZ_CHANGED', 'version': '2.1.5'}});
      await this.changedObjectsDatabase.datatypes.add({'id': '3', 'object': {'label': 'CWE_CHANGED', 'version': '2.1.5'}});
    });
  }

  public getDatatype (id, callback) {
    let datatype;
    this.changedObjectsDatabase.transaction('r', this.changedObjectsDatabase.datatypes, async() => {
      datatype = await this.changedObjectsDatabase.datatypes.get(id);
    });
    if (datatype != null) {
      console.log('datatype value from changedDb' + JSON.stringify(datatype));
      callback(datatype);
    } else {
      this.objectsDatabase.transaction('r', this.objectsDatabase.datatypes, async() => {
        datatype = await this.objectsDatabase.datatypes.get(id);
        callback(datatype);
      });
    }
    this.objectsDatabase.transaction('r', this.objectsDatabase.datatypes, async() => {
      const datatype = await this.objectsDatabase.datatypes.get(id);
      callback(datatype);
    });
  }

}

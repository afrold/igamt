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
      this.objectsDatabase.datatypes.clear();
      await this.objectsDatabase.datatypes.add({'id': '1', 'object': {'id': '1234', 'label': 'HD_IZ', 'version': '2.1.5'}});
      await this.objectsDatabase.datatypes.add({'id': '2', 'object': {'id': '5678', 'label': 'ST', 'version': '2.1.5'}});
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async() => {
      this.changedObjectsDatabase.datatypes.clear();
      await this.changedObjectsDatabase.datatypes.add({'id': '1', 'object': {'id': '1234', 'label': 'HD_IZ_CHANGED', 'version': '2.1.5'}});
      await this.changedObjectsDatabase.datatypes.add({'id': '3', 'object': {'id': '9101', 'label': 'CWE_CHANGED', 'version': '2.1.5'}});
    });
  }

  public getDatatype (id, callback) {
    let datatype;
    this.changedObjectsDatabase.transaction('r', this.changedObjectsDatabase.datatypes, async() => {
      datatype = await this.changedObjectsDatabase.datatypes.get(id);
      if (datatype != null) {
        callback(datatype);
      } else {
        this.objectsDatabase.transaction('r', this.objectsDatabase.datatypes, async() => {
          datatype = await this.objectsDatabase.datatypes.get(id);
          callback(datatype);
        });
      }
    });
  }

}

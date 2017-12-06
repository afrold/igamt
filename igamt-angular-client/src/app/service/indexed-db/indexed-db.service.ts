import { Injectable } from '@angular/core';

import Dexie from 'dexie';

export interface IObject {
  id?: string;
  object?: object;
}

/*
export interface IDatatype {
  id?: string;
  datatype?: object;
}
export interface IValueSet {
  id?: string;
  valueSet?: object;
}
export interface ISegment {
  id?: string;
  segment?: object;
}
export interface ISection {
  id?: string;
  section?: object;
}
export interface IProfileComponent {
  id?: string;
  profileComponent?: object;
}
export interface IProfile {
  id?: string;
  profile?: object;
}
*/

class ObjectsDatabase extends Dexie {
  datatypes: Dexie.Table<IObject, number>;
  valueSets: Dexie.Table<IObject, number>;
  segments: Dexie.Table<IObject, number>;
  sections: Dexie.Table<IObject, number>;
  profileComponents: Dexie.Table<IObject, number>;
  profiles: Dexie.Table<IObject, number>;

  constructor(name) {
    super(name);
    this.version(1).stores({
      datatypes: '++id,object',
      segments: '++id,object',
      sections: '++id,object',
      profileComponents: '++id,object',
      profiles: '++id,object',
      valueSets: '++id,object'
    });
  }
}

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
    });

  }

  public getDatatype (id) {
    const datatype = this.changedObjectsDatabase.transaction('r', this.changedObjectsDatabase.datatypes, async() => {
      return await this.changedObjectsDatabase.datatypes.get(id);
    });
    if (datatype != null) {
      return datatype;
    } else {
      return this.objectsDatabase.transaction('r', this.objectsDatabase.datatypes, async() => {
        return await this.objectsDatabase.datatypes.get(id);
      });
    }
  }

}

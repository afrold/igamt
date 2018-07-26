import Dexie from 'dexie';

interface IDeletedObject {
  id?: string;
}

export class DeletedObjectsDatabase extends Dexie {
  datatypes: Dexie.Table<IDeletedObject, number>;
  valueSets: Dexie.Table<IDeletedObject, number>;
  segments: Dexie.Table<IDeletedObject, number>;
  sections: Dexie.Table<IDeletedObject, number>;
  profileComponents: Dexie.Table<IDeletedObject, number>;
  profiles: Dexie.Table<IDeletedObject, number>;

  constructor() {
    super('deletedObjectsDatabase');
    this.version(1).stores({
      datatypes: '++id',
      segments: '++id',
      sections: '++id',
      profileComponents: '++id',
      profiles: '++id',
      valueSets: '++id'
    });
  }
}

import { Injectable } from '@angular/core';

import Dexie from 'dexie';

interface ILink {
  id?: string;
  label?: string;
}

class LinkDatabase extends Dexie {
  datatypeLink: Dexie.Table<ILink, number>;
  tableLink: Dexie.Table<ILink, number>;

  constructor() {
    super('LinkDatabase');
    this.version(1).stores({
      datatypeLink: '++id,label',
      tableLink: '++id,label'
    });
  }
}

@Injectable()
export class IndexedDbService {

  linkDatabase;
  constructor() {
    this.linkDatabase = new LinkDatabase();
    this.linkDatabase.transaction('rw', this.linkDatabase.datatypeLink, async() => {
      await this.linkDatabase.datatypeLink.add({'id': '1', 'label': 'HD'});
      await this.linkDatabase.datatypeLink.add({'id': '2', 'label': 'ST'});
    });
    this.linkDatabase.transaction('rw', this.linkDatabase.tableLink, async() => {
      await this.linkDatabase.tableLink.add({'id': '1', 'label': '0001'});
      await this.linkDatabase.tableLink.add({'id': '2', 'label': '0304'});
    });
  }

  public getDatatypeLinks (callback) {
    this.linkDatabase.transaction('rw', this.linkDatabase.datatypeLink, async() => {
      const datatypeLinks = await this.linkDatabase.datatypeLink.toArray();
      callback(datatypeLinks);
    });
  }

  public getDatatypeLink (id, callback) {
    this.linkDatabase.transaction('rw', this.linkDatabase.datatypeLink, async() => {
      const datatypeLink = await this.linkDatabase.datatypeLink.get(id);
      callback(datatypeLink);
    });
  }

}

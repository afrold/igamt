import {Injectable} from '@angular/core';

import { ObjectsDatabase } from './objects-database';
import { DatatypesService } from '../datatypes/datatypes.service';
import { ValueSetsService } from '../valueSets/valueSets.service';

@Injectable()
export class IndexedDbService {

  objectsDatabase;
  changedObjectsDatabase;
  constructor(private datatypesService: DatatypesService, private valueSetsService: ValueSetsService) {
    this.objectsDatabase = new ObjectsDatabase('ObjectsDatabase');
    this.changedObjectsDatabase = new ObjectsDatabase('ChangedObjectsDatabase');
  }
  public init(igDocumentId) {
    // datatypes
    this.objectsDatabase.transaction('rw', this.objectsDatabase.datatypes, async() => {
      this.objectsDatabase.datatypes.clear().then(this.injectDatatypes(igDocumentId));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async() => {
      // this.changedObjectsDatabase.datatypes.clear();
    });
    // valueSets
    this.objectsDatabase.transaction('rw', this.objectsDatabase.valueSets, async() => {
      this.objectsDatabase.valueSets.clear().then(this.injectValueSets(igDocumentId));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.valueSets, async() => {
      // this.changedObjectsDatabase.valueSets.clear();
    });
    // segments
    this.objectsDatabase.transaction('rw', this.objectsDatabase.segments, async() => {
      this.objectsDatabase.segments.clear().then(this.injectDatatypes(igDocumentId));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.segments, async() => {
      // this.changedObjectsDatabase.segments.clear();
    });
    // sections
    this.objectsDatabase.transaction('rw', this.objectsDatabase.sections, async() => {
      this.objectsDatabase.sections.clear().then(this.injectDatatypes(igDocumentId));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.sections, async() => {
      // this.changedObjectsDatabase.sections.clear();
    });
    // profileComponents
    this.objectsDatabase.transaction('rw', this.objectsDatabase.profileComponents, async() => {
      this.objectsDatabase.profileComponents.clear().then(this.injectDatatypes(igDocumentId));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.profileComponents, async() => {
      // this.changedObjectsDatabase.profileComponents.clear();
    });
    // profiles
    this.objectsDatabase.transaction('rw', this.objectsDatabase.profiles, async() => {
      this.objectsDatabase.profiles.clear().then(this.injectDatatypes(igDocumentId));
    });
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.profiles, async() => {
      // this.changedObjectsDatabase.profiles.clear();
    });
  }

  /*
  .filter(function (fullDatatype) {
        const metadataDatatype = {
          'label': fullDatatype.label,
          'hl7Version': fullDatatype.hl7Version
        }
        return metadataDatatype;
      });
   */

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

  public getDatatypeMetadata (id, callback) {
    this.getDatatype(id, function(datatype){
      const metadataDatatype = {
        'id': datatype.id,
        'name': datatype.name,
        'ext': datatype.ext,
        'label': datatype.label,
        'scope': datatype.scope,
        'publicationVersion': datatype.publicationVersion,
        'hl7Version': datatype.hl7Version,
        'hl7Versions': datatype.hl7Versions,
        'numberOfComponents': datatype.components.length,
        'type': datatype.type
      }
       callback(metadataDatatype);
    });
  }

  private injectDatatypes(igDocumentId) {
    this.datatypesService.getDatatypes(igDocumentId, this.populateDatatypes.bind(this));
  }
  private injectValueSets(igDocumentId) {
    this.valueSetsService.getValueSets(igDocumentId, this.populateValueSets.bind(this));
  }

  private populateDatatypes (datatypes) {
    console.log(JSON.stringify(datatypes));
    datatypes.forEach(datatype => {
      this.objectsDatabase.transaction('rw', this.objectsDatabase.datatypes, async() => {
        await this.objectsDatabase.datatypes.put({
          'id': datatype.id,
          'object': datatype
        });
      });
    });
  }
  private populateValueSets (valueSets) {
    console.log(JSON.stringify(valueSets));
    valueSets.forEach(valueSet => {
      this.objectsDatabase.transaction('rw', this.objectsDatabase.valueSets, async() => {
        await this.objectsDatabase.valueSets.put({
          'id': valueSet.id,
          'object': valueSet
        });
      });
    });
  }
  public saveDatatype(datatype) {
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async() => {
      await this.changedObjectsDatabase.datatypes.put({
        'id': datatype.id,
        'object': datatype
      });
    });
    console.log('save datatype with id ' + datatype.id);
  }

  /*public saveChangedDatatypes() {
    this.changedObjectsDatabase.transaction('rw', this.changedObjectsDatabase.datatypes, async () => {
      const changedDatatypes = await this.changedObjectsDatabase.datatypes.toArray();
      this.datatypesService.saveDatatypes(changedDatatypes);
    });
  }*/
}

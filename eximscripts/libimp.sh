#!/usr/bin/env bash

mongoimport --db igl -j 4 --collection igdocument --file ../igamt-lite-resource/src/main/resources/eximfiles/igdocument-expimp.json
mongoimport --db igl -j 4 --collection messages --file ../igamt-lite-resource/src/main/resources/eximfiles/messages-expimp.json
mongoimport --db igl -j 4 --collection segment-library --file ../igamt-lite-resource/src/main/resources/eximfiles/segment-library-expimp.json
mongoimport --db igl -j 4 --collection segment --file ../igamt-lite-resource/src/main/resources/eximfiles/segment-expimp.json
mongoimport --db igl -j 4 --collection datatype-library --file ../igamt-lite-resource/src/main/resources/eximfiles/datatype-library-expimp.json
mongoimport --db igl -j 4 --collection datatype --file ../igamt-lite-resource/src/main/resources/eximfiles/datatype-expimp.json
mongoimport --db igl -j 4 --collection table-library --file ../igamt-lite-resource/src/main/resources/eximfiles/table-library-expimp.json
mongoimport --db igl -j 4 --collection table --file ../igamt-lite-resource/src/main/resources/eximfiles/table-expimp.json

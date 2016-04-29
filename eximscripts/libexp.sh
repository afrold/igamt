#!/usr/bin/env bash

mongoexport --db igl --collection igdocument --out ../igamt-lite-resource/src/main/resources/eximfiles/igdocument-expimp.json
mongoexport --db igl --collection messages --out ../igamt-lite-resource/src/main/resources/eximfiles/messages-expimp.json
mongoexport --db igl --collection segment-library --out ../igamt-lite-resource/src/main/resources/eximfiles/segment-library-expimp.json
mongoexport --db igl --collection segment --out ../igamt-lite-resource/src/main/resources/eximfiles/segment-expimp.json
mongoexport --db igl --collection datatype-library --out ../igamt-lite-resource/src/main/resources/eximfiles/datatype-library-expimp.json
mongoexport --db igl --collection datatype --out ../igamt-lite-resource/src/main/resources/eximfiles/datatype-expimp.json
mongoexport --db igl --collection table-library --out ../igamt-lite-resource/src/main/resources/eximfiles/table-library-expimp.json
mongoexport --db igl --collection table --out ../igamt-lite-resource/src/main/resources/eximfiles/table-expimp.json

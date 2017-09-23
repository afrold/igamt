export interface Config {
    usages?: any[];
    conditionalUsage?: string[];
    codeUsages?: string[];
    codeSources?: string[];
    tableStabilities?: string[];
    tableContentDefinitions?: string[];
    tableExtensibilities?: string[];
    constraintVerbs?: string[];
    conditionalConstraintVerbs?: string[];
    constraintTypes?: string[];
    predefinedFormats?: string[];
    statuses?: string[];
    domainVersions?: string[];
    schemaVersions?: string[];
    valueSetAllowedDTs?:string[];
    singleValueSetDTs?:string[];
}
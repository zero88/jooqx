oss {
    baseName.set("jpa-ext")
}

dependencies {
    compileOnlyApi(JacksonLibs.databind)
    testImplementation(JacksonLibs.databind)

    compileOnly(UtilLibs.lombok)
    annotationProcessor(UtilLibs.lombok)
}



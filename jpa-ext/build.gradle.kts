oss {
    baseName.set("jpa-ext")
}

dependencies {
    compileOnlyApi(libs.jacksonDatabind)
    testImplementation(libs.jacksonDatabind)
}



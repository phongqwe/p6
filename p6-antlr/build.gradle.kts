plugins {
    java
    antlr
    `maven-publish`
}
val id = "p6-antlr"
val _group = "com.qxdzbc.p6"
group = _group
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    antlr ("org.antlr:antlr4:4.9.3")
}

//configurations {
//    compile {
//        extendsFrom = extendsFrom.findAll { it != configurations.antlr }
//    }
//}

tasks{
    generateGrammarSource{
        this.outputDirectory = File("${buildDir.absolutePath}/generated-src/antlr4/com/qxdzbc/p6/formula/translator/antlr")
        this.arguments = this.arguments +
                listOf("-package" ,"${_group}.formula.translator.antlr") +
                listOf("-visitor")

    }
}
sourceSets {
    main {
        java {
            this.srcDirs("${buildDir.absolutePath}/generated-src/antlr4/com/qxdzbc/p6/formula/translator/antlr")
//            srcDirs = [ "${buildDir.absolutePath}/generated-src/antlr4/com/qxdzbc/p6/formula/translator/antlr"]
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = id
            version = version
            from(components["java"])
        }
    }
}

plugins {
  `java-library`
}
group = "com.github.twh"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.netty:netty-all:4.1.72.Final")
  implementation("org.slf4j:slf4j-api:1.7.32")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.16.0")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}


tasks.getByName<Test>("test") {
  useJUnitPlatform()
}

tasks.jar {
  manifest {
    attributes(
      "Main-Class" to "com.github.twh.redis.RedisApplication"
    )
  }

  from({
    configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
  }).duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
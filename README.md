# FrameEconomy
```text
Gradle:
repositories {
	maven { url 'https://framedev.ch:444/releases' }
}
dependencies {
	compileOnly 'de.framedev:FrameEconomy:1.7-SNAPSHOT'
}

Maven:
<repository>
  <id>repository</id>
  <url>https://framedev.ch:444/releases</url>
</repository>
<dependency>
  <groupId>de.framedev</groupId>
  <artifactId>FrameEconomy</artifactId>
  <version>1.7-SNAPSHOT</version>
</dependency>
```


Build by your Self

```
git clone https://github.com/frame-dev/FrameEconomy
cd FrameEconomy
./gradlew clean fatJar
```
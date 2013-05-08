java-concurrency-scratchpad
===========================

Messing around with snippets of concurrent Java

This project builds with Gradle (1.6), you can either install Gradle manually from gradle.org or use the Gradle wrapper e.g.
```
./gradlew test
```
The first time you use the wrapper it will download[1] the appropriate Gradle distribution and the project dependencies

Useful tasks (use gradle, gradlew.bat or gradlew as appropriate for your machine)
+  `eclipse`    --- generate the eclipse project files
+  `idea`       --- (re)generate the IntelliJ IDEA project files (although Gradle projects can be imported in IDEA 12 onwards)
+  `test`       --- run all the unit tests

-----------------------------------------------------------------------------------------------
[1] If you're behind a proxy you'll first need to configure Gradle to know how to use it.  Edit
   the file `<HOME>/.gradle/gradle.properties` to look like this
```
systemProp.http.proxyHost=proxy.mynet.com
systemProp.http.proxyPort=8080
systemProp.http.nonProxyHosts=*.mynet.com|*.mynet.co.uk|localhost
systemProp.https.proxyHost=proxy.mynet.com
systemProp.https.proxyPort=8080
systemProp.https.nonProxyHosts=*.mynet.com|*.mynet.co.uk|localhost
```

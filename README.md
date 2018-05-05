# Extra Java Inspections IntelliJ IDEA plugin #

### Inspections List ###

* Find instances of object fields being used in serializable lambdas - this will break serializability of the lambda. Provides a quick fix to alias the variable as local and final.

### To Add Inspections ###

Follow the example set out by `MemberVariableInLambda`, and remember to register your inspection in the `InspectionRegistration` class. To test locally, use `gradle build` to create the plugin zip in `build/distributions/` and install the plugin from disk in Intellij. When you are ready to publish, increment the version number of the plugin in build.grade, plugins.zml, and src/main/resources/META-INF/plugin.xml

### Plugin Repository ###

To be able to download and update the plugin in regular IntelliJ IDEA way, please, follow "[Managing Enterprise Plugin Repositories](https://www.jetbrains.com/help/idea/managing-enterprise-plugin-repositories.html)" steps using link to `http://library.liveramp.net/artifactory/ext-release-local/com/liveramp/liveramp-idea-inspections-plugin/plugins.xml` as repository URL.

# Extra Java Inspections IntelliJ IDEA plugin #

### Inspections List ###

* Find instances of object fields being used in serializable lambdas - this will break serializability of the lambda. Provides a quick fix to alias the variable as local and final.

### To Add Inspections ###

Follow the example set out by `MemberVariableInLambda`, and remember to register your inspection in the `InspectionRegistration` class. To test locally, use `gradle build` to create the plugin zip in `build/distributions/` and install the plugin from disk in Intellij. When you are ready to publish, increment the version number of the plugin in build.gradle, and you're all set!

### Plugin Repository ###

*For LiveRamp internal users*: To be able to download and update the plugin in the regular IntelliJ IDEA way, please, follow the instructions at "[Managing Enterprise Plugin Repositories](https://www.jetbrains.com/help/idea/managing-enterprise-plugin-repositories.html)" and use `http://library.liveramp.net/artifactory/ext-release-local/com/liveramp/liveramp-idea-inspections-plugin/plugins.xml` as the repository URL.

*For external users*: LiveRamp does not currently have a good way to host the plugin for external users. Feel free to host the plugin somewhere yourself, or simply use `gradle build` and install the plugin from disk using the created zip archive.

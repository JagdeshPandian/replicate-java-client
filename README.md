# replicate-java-client

This is an early stage implementation of the Replicate.com HTTP API to utilize models hosted on Replicate.com.

Copy ```replicateClient.conf.template``` to ```replicateClient.conf``` into the config directory and set your API key.

To build:

```mvn package```

run with:

```./bin/runReplicateClientMain.sh```

resulting images go into outputImages directory

There is a build dependency on an older version of groovy related to the Eclipse groovy-compiler plugin used in the maven POM.  The POM can be modified to switch to a more recent version of groovy, but the maven groovy plugins would need to be adjusted as well.  This is based on an Eclipse IDE project with a groovy plugin that has specific requirements which should be upgraded eventually.

The runtime shell script uses the groovy jars in the "lib" directory.

The "main" class ```ReplicateClientMain``` provides an example use, such as specifying the prompt used.

A class is specified for each model used on Replicate.com to help with data validation and composing requests.

The currently defined models are:
* openjourney: https://replicate.com/prompthero/openjourney


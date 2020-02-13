# Minecraft Server Plugin [![Build Status](https://travis-ci.com/MEGarrusVakarian/minecraft-server-plugin.svg?branch=master)](https://travis-ci.com/MEGarrusVakarian/minecraft-server-plugin) [![Maven Central](https://img.shields.io/maven-central/v/de.garrus.maven/minecraft-server-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22de.garrus.maven%22%20AND%20a:%22minecraft-server-plugin%22)

Maven Plugin to launch an Server for Plugin Development.

__Notice__: Current only [Papermc](http://papermc.io) Server supported.
## Installation

```xml
<plugin>
    <groupId>de.garrus.maven</groupId>
    <artifactId>minecraft-server-plugin</artifactId>
    <version>1.0.1</version>
    <configuration>
        <serverVersion>1.15.2</serverVersion>
        <createEula>true</createEula>
    </configuration>
</plugin>
```

## Usages

Install the server in the configured folder 
````shell script
mvn minecraft-server:server-install
````

Start the minecraft server on the default  server port __25565__

````shell script
mvn minecraft-server:server-start
````

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)

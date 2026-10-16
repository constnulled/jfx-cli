# jfx-cli - Command line Java Decompiler

A command line Java decompiler with a choice of decompiler engines. It decompiles single `.class` files, archives
(`.jar`, `.war`, `.zip`, ...) and directories of classes.

## Decompiler engines

| `--decompiler` value | Engine                                                                                          |
|----------------------|-------------------------------------------------------------------------------------------------|
| `jd` (default)       | [JD-Core](https://github.com/nbauma109/jd-core)                                                  |
| `cfr`                | [CFR](https://github.com/leibnitz27/cfr)                                                         |
| `fernflower`         | [Fernflower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler) (JetBrains) |
| `procyon`            | [Procyon](https://github.com/mstrobel/procyon)                                                   |

JD-Core is the oldest of them and can struggle with newer Java features (records, switch expressions, pattern
matching, sealed classes). For modern bytecode try `cfr` first. If one engine produces messy
output, run the same input with another one and compare.

## Download

Get the latest `.jar` from the **Releases** page of this repository.

## Requirements

[Java runtime](https://adoptium.net/temurin/releases) version **25 or newer**.

## Usage

```bash
java -jar jd-cli.jar [options] [Files to decompile]
```

### Examples

```bash
java -jar jd-cli.jar HelloWorld.class
java -jar jd-cli.jar app.jar
java -jar jd-cli.jar -dc cfr app.jar
java -jar jd-cli.jar -dc fernflower -od decompiled -sr app.jar
java -jar jd-cli.jar -dc procyon -n classes/
```

Default output depends on the input type:

* `.class` file: printed to the console
* `.jar` / `.zip` / `.war`: written to a new archive named `<name>.src.<ext>`
* directory: written to a new directory named `<name>.src`

Output options can be combined, for example `-od out -oc`.

### Options

```
Usage: java -jar jd-cli.jar [options] [Files to decompile]
  Options:
    --decompiler, -dc
       decompiler engine to use. Possible values are: jd, cfr,
       fernflower, procyon
       Default: JD
    --displayLineNumbers, -n
       displays line numbers in decompiled classes (jd and procyon only)
       Default: false
    --escapeUnicodeCharacters, -eu
       escape unicode characters in decompiled classes
       Default: false
    --help, -h
       shows this help
       Default: false
    --logLevel, -g
       takes [level] as parameter and sets it as the CLI log level. Possible
       values are: ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF
       Default: INFO
    --outputConsole, -oc
       enables output to system output stream
       Default: false
    --outputDir, -od
       takes a [directoryPath] as a parameter and configures a flat DIR output
       for this path
    --outputDirStructured, -ods
       takes a [directoryPath] as a parameter and configures a structured DIR
       output for this path
    --outputZipFile, -oz
       takes a [zipFilePath] as a parameter and configures ZIP output for this
       path
    --pattern, -p
       RegExp pattern which the to-be-decompiled file has to match. Not matching
       entries are skipped.
    --serialProcessing, -sp
       don't use parallel processing
       Default: false
    --skipResources, -sr
       skips processing resources
       Default: false
    --version, -v
       shows the version
       Default: false
```

## Building

Requires JDK 25 and Maven.

```bash
mvn clean package
```

The runnable jar with all dependencies is created at `jd-cli/target/jd-cli.jar`.

## Using as a Maven dependency

```xml
<dependency>
    <groupId>com.github.kwart.jd</groupId>
    <artifactId>jd-lib</artifactId>
    <version>${jd-cli.version}</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>${slf4j.version}</version>
</dependency>
```

```java
public static void main(String[] args) {
    JDInput input = new ZipFileInput("/opt/hazelcast/hazelcast.jar");
    JDOutput output = new DirOutput(new File("/tmp/hz-src"));
    JavaDecompiler decompiler = new JavaDecompiler(new DecompilerOptions() {

        @Override
        public boolean isSkipResources() {
            return true;
        }

        @Override
        public boolean isEscapeUnicodeCharacters() {
            return false;
        }

        @Override
        public boolean isDisplayLineNumbers() {
            return true;
        }

        @Override
        public boolean isParallelProcessingAllowed() {
            return true;
        }

        @Override
        public DecompilerType getDecompilerType() {
            return DecompilerType.CFR;
        }
    });
    input.decompile(decompiler, output);
}
```

`getDecompilerType()` is optional and defaults to `DecompilerType.JD`.

## Credits

This project was originally forked from [JDCommandLine](https://github.com/betterphp/JDCommandLine) and
[jd-cli](https://github.com/intoolswetrust/jd-cli).

## License

* jfx-cli is licensed under [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html) as the original JD-Core library.
* Bundled decompilers keep their own licenses: JD-Core (GPLv3), CFR (MIT), Fernflower (Apache 2.0),
  Procyon (Apache 2.0).

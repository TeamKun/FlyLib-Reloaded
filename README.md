<p align="center">
    <a href="https://github.com/TeamKun/flylib-reloaded/blob/master/wiki/en/welcome.md">
        <img height="150" src="https://raw.githubusercontent.com/TeamKun/flylib-reloaded/master/logo.png" alt="Logo">
        <h1 align="center">FlyLib Reloaded</h1>
    </a>
</p>

<p align="center">
<b>FlyLib Reloaded</b> is a powerful and useful Java/Kotlin library for the Minecraft Paper.<br>
<b>⚠️ It's still work in progress. API will be changed or removed without notice.</b>
</p>

<p align="center">
  <a aria-label="Developer" href="https://twitter.com/kotx__">
    <img src="https://img.shields.io/badge/MADE%20BY%20Kotx__-000000.svg?style=for-the-badge&logo=Twitter&labelColor=000">
  </a>
  <a aria-label="Maven Central" href="https://search.maven.org/artifact/dev.kotx/flylib-reloaded/">
    <img alt="" src="https://img.shields.io/maven-central/v/dev.kotx/flylib-reloaded?style=for-the-badge&labelColor=000000&color=blue">
  </a>
  <a aria-label="License" href="https://github.com/TeamKun/flylib-reloaded/blob/master/LICENSE">
    <img alt="" src="https://img.shields.io/github/license/TeamKun/flylib-reloaded?style=for-the-badge&labelColor=000000&color=red">
  </a>
</p>

### 🔥 Features

- command/config engine
- builders and utilities for Bukkit
- advanced event flow manager
- easy to use

### 📎 Links

##### [Wiki (en)](https://github.com/TeamKun/flylib-reloaded/blob/master/wiki/en/welcome.md) / [Wiki (ja)](https://github.com/TeamKun/flylib-reloaded/blob/master/wiki/ja/welcome.md)

##### [JavaDoc](https://teamkun.github.io/flylib-reloaded/javadoc) / [Kdoc](https://teamkun.github.io/flylib-reloaded/html)

### ⚙️ Installation

<a aria-label="Maven Central" href="https://search.maven.org/artifact/dev.kotx/flylib-reloaded/">
  <img alt="" src="https://img.shields.io/maven-central/v/dev.kotx/flylib-reloaded?style=for-the-badge&labelColor=000000&color=blue">
</a>

Replace `[version]` with the version you want to use.

<details>
<summary>Gradle Kotlin DSL</summary>
<div>

Please add the following configs to your `build.gradle.kts`.  
Use the `shadowJar` task when building plugins (generating jars to put in plugins/).

```kotlin
plugins {
    id("com.github.johnrengelman.shadow") version "6.0.0"
}
```

```kotlin
dependencies {
    implementation("dev.kotx:flylib-reloaded:[version]")
}
```

The following code is a configuration of shadowJar that combines all dependencies into one jar.  
It relocates all classes under the project's groupId to avoid conflicts that can occur when multiple plugins using
different versions of flylib are deployed to the server.

By setting the following, the contents of the jar file will look like this

```kotlin
import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

//some gradle configurations

val relocateShadow by tasks.registering(ConfigureShadowRelocation::class) {
    target = tasks.shadowJar.get()
    prefix = project.group.toString()
}

tasks.shadowJar {
    dependsOn(relocateShadow)
}
```

</div>
</details>

<details>
<summary>Gradle</summary>
<div>

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '6.0.0'
}
```

```groovy
dependencies {
    implementation 'dev.kotx:flylib-reloaded:[version]'
}
```

The following code is a configuration of shadowJar that combines all dependencies into one jar.  
It relocates all classes under the project's groupId to avoid conflicts that can occur when multiple plugins using
different versions of flylib are deployed to the server.

By setting the following, the contents of the jar file will look like this

```groovy
import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

//some gradle configurations

task relocateShadow(type: ConfigureShadowRelocation) {
    target = tasks.shadowJar
    prefix = project.group
}

tasks.shadowJar.dependsOn tasks.relocateShadow
```

</div>
</details>
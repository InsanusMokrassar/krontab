# krontab

 [ ![Download](https://api.bintray.com/packages/insanusmokrassar/InsanusMokrassar/krontab/images/download.svg) ](https://bintray.com/insanusmokrassar/InsanusMokrassar/krontab/_latestVersion) 
 
Library was created to give oppotunity to launch some things from time to time according to some schedule in
runtime of applications.

| Table of content |
|---|
| [ How to use ](#how-to-use) |
| [ How to use: Including in project ](#including-in-project) |
| [ How to use: Config from string ](#config-from-string) |
| [ How to use: Config via builder (DSL preview) ](#config-via-builder) |

## How to use

There are several ways to configure and use this library:

* From some string
* From builder

Anyway, to start some action from time to time you will need to use one of extensions/functions:

```kotlin
val kronScheduler = /* creating of KronScheduler instance */;

kronScheuler.doWhile {
    // some action
    true // true - repeat on next time
}
```

### Including in project

If you want to include `krontab` in your project, just add next line to your
dependencies part:

```groovy
implementation "com.insanusmokrassar:krontab:$krontab_version"
```

Next version is the latest currently for the library:

[ ![Download](https://api.bintray.com/packages/insanusmokrassar/InsanusMokrassar/krontab/images/download.svg) ](https://bintray.com/insanusmokrassar/InsanusMokrassar/krontab/_latestVersion)

For old version of Gradle, instead of `implementation` word developers must use `compile`.

### Config from string

Developers can use more simple way to configure repeat times is string. String configuring
like a `crontab`, but with a little bit different meanings:
```
/-------- Seconds
| /------ Minutes
| | /---- Hours
| | | /-- Days of months
| | | | / Months
| | | | |
* * * * *
```

It is different with original `crontab` syntax for the reason, that expected that in practice developers
will use seconds and minutes with more probability than months (for example). In fact, developers will use something
like:

```kotlin
doWhile("/5 * * * *") {
    println("Called")
    true // true - repeat on next time
}
```

Or more version:

```kotlin
doInfinity("/5 * * * *") {
    println("Called")
}
```

Both of examples will print `Called` message every five seconds.

### Config via builder

Also this library currently supports DSL for creating the same goals:

```kotlin
val kronScheduler = buildSchedule {
    seconds {
        from (0) every 5
    }
}
kronScheduler.doWhile {
    println("Called")
    true // true - repeat on next time
}
```

Or

```kotlin
val kronScheduler = buildSchedule {
    seconds {
        0 every 5
    }
}
kronScheduler.doWhile {
    println("Called")
    true // true - repeat on next time
}
```

Or

```kotlin
val kronScheduler = buildSchedule {
    seconds {
        0 every 5
    }
}
kronScheduler.doInfinity {
    println("Called")
}
```

All of these examples will do the same things: print `Called` message every five seconds.

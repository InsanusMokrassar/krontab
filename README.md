# krontab

 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.inmo/krontab/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.inmo/krontab)
 [![Build Status](https://travis-ci.com/InsanusMokrassar/krontab.svg?branch=master)](https://travis-ci.com/InsanusMokrassar/krontab)
 
Library was created to give oppotunity to launch some things from time to time according to some schedule in
runtime of applications.

| Table of content |
|---|
| [ How to use ](#how-to-use) |
| [ How to use: Including in project ](#including-in-project) |
| [ How to use: Config from string ](#config-from-string) |
| [ How to use: Config via builder (DSL preview) ](#config-via-builder) |
| [ How to use: KronScheduler as a Flow ](#KronScheduler-as-a-Flow) |
| [ How to use: Offsets ](#Offsets) |
| [ How to use: Note about week days ](#Note-about-week-days) |

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
implementation "dev.inmo:krontab:$krontab_version"
```

Next version is the latest currently for the library:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.inmo/krontab/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.inmo/krontab)

For old version of Gradle, instead of `implementation` word developers must use `compile`.

### Config from string

Developers can use more simple way to configure repeat times is string. String configuring
like a `crontab`, but with a little bit different meanings:

```
/--------------- Seconds
| /------------- Minutes
| | /----------- Hours
| | | /--------- Days of months
| | | | /------- Months
| | | | | /----- (optional) Year
| | | | | | /--- (optional) Timezone offset
| | | | | | |  / (optional) Week days
* * * * * * 0o *w
```

It is different with original `crontab` syntax for the reason, that expected that in practice developers
will use seconds and minutes with more probability than months (for example) or even years. In fact, developers will use
something like:

```kotlin
doWhile("/5 * * * *") {
    println("Called")
    true // true - repeat on next time
}
```

An other version:

```kotlin
doInfinity("/5 * * * *") {
    println("Called")
}
```

Both of examples will print `Called` message every five seconds.

### Config via builder

Also, this library currently supports DSL for creating the same goals:

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

### KronScheduler as a Flow

Any `KronScheduler`can e converted to a `Flow<DateTime` using extension `asFlow`:

```kotlin
val kronScheduler = buildSchedule {
    seconds {
        0 every 1
    }
}

val flow = kronScheduler.asFlow()
```

So, in this case any operations related to flow are available and it is expected that they will work correctly. For
example, it is possible to use this flow with `takeWhile`:

```kotlin
flow.takeWhile {
    condition()
}.collect {
    action()
}
```

### Offsets

Offsets in this library works via passing parameter ending with `o` in any place after `month` config. Currently
there is only one format supported for offsets: minutes of offsets. To use time zones you will need to call `next`
method with `DateTimeTz` argument or `nextTimeZoned` method with any `KronScheduler` instance, but in case if this
scheduler is not instance of `KronSchedulerTz` it will works like you passed just `DateTime`.

Besides, in case you wish to use time zones explicitly, you will need to get `KronSchedulerTz`. It is possible by:

* Using `createSimpleScheduler`/`buildSchedule`/`KrontabTemplate#toSchedule`/`KrontabTemplate#toKronScheduler` methods
with passing `defaultOffset` parameter
* Using `SchedulerBuilder#build`/`createSimpleScheduler`/`buildSchedule`/`KrontabTemplate#toSchedule`/`KrontabTemplate#toKronScheduler`
methods with casting to `KronSchedulerTz` in case you are pretty sure that it is timezoned `KronScheduler`
* Creating your own implementation of `KronSchedulerTz`

### Note about week days

Unlike original CRON, here week days:

* Works as `AND`: cron date time will search first day which will pass requirement according all parameters including
week days
* You may use any related to numbers syntax with week days: `0-3w`, `0,1,2,3w`, etc.
* Week days (like years and offsets) are optional and can be placed anywhere after `month`

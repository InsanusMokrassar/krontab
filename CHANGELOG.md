# Changelog

## 0.5.2

* Versions
  * `Kotlin`: `1.4.31` -> `1.4.32`

## 0.5.1

* Versions
  * `Kotlin`: `1.4.21` -> `1.4.31`
  * `Coroutines`: `1.4.2` -> `1.4.3`
  * `Klock`: `2.0.3` -> `2.0.7`
  * `Androidx Work`: `2.4.0` -> `2.5.0`

## 0.5.0 Years

**BREAKING CHANGES**

* `CronDateTimeScheduler` has been marked as `internal` and no longer accessible outside of internal functions
  * Old methods `merge` and `plus` related to `CronDateTimeScheduler` has been marked as `deprecated` and changed their
  parameters types - it is `KronScheduler` now
* New methods `merge` has been added
* **`KronScheduler#next` method now is nullable. Use `nextOrRelative`/`nextOrNow` to get next time certainly**
* **Years was added as optional part of krontab template and opportunity in `SchedulerBuilder`**
  * New builder `YearsBuilder`
  * `SchedulerFlow#collectSafely` will be normally (without exceptions) finish when `next` of scheduler will return
    null
* `KronScheduler#doOnce` will run code immediately in case when `next` is returning null value
* `KrontabTemplateWrapper` has been added
* New extension `KrontabTemplate#toKronScheduler` (works as `toSchedule`)
* **Fixed issue related to the fact that `toNearDateTime` of `CronDateTime` incorrectly handled months**
* **Android target has been added**

## 0.4.2

* Versions
  * `Kotlin`: `1.4.20` -> `1.4.21`
  * `Klock`: `2.0.1` -> `2.0.3`
* `CronDateTimeScheduler` now is deprecated and will be set up as `internal` in future

## 0.4.1

* Versions:
  * `Coroutines`: `1.4.1` -> `1.4.2`
  * `Klock`: `2.0.0` -> `2.0.1`
* `CronDateTimeScheduler` now is public
* New functions for `CronDateTimeScheduler`
* Add `CollectionKronScheduler`. It will give opportunity to unite several schedulers in one

## 0.4.0

**BREAKING CHANGES**
Package of project has been changed. Migration:

* Replace in your dependencies `com.insanusmokrassar:krontab` by `dev.inmo:krontab`
* Replace in your project all imports `com.insanusmokrassar.krontab` by `dev.inmo.krontab`

* Versions:
    * `Kotlin`: `1.4.10` -> `1.4.20`
    * `Klock`: `1.12.1` -> `2.0.0`

## 0.3.3

* Versions:
    * `Coroutines`: `1.3.9` -> `1.4.1`

## 0.3.2

* Function `TimeBuilder#each` was added (works as `at`)
* Add opportunity to use `first` shortcuts:
    * Value property `TimeBuilder#first` for including via functions like `TimeBuilder#at`
    * Shortcut for kron string format `f` or `F`
* Add opportunity to use `last` shortcuts:
    * Value property `TimeBuilder#last` for including via functions like `TimeBuilder#at`
    * Shortcut for kron string format `l` or `L`

## 0.3.1

* Versions:
    * `Kotlin`: `1.4.0` -> `1.4.10`
    * `Klock`: `1.12.0` -> `1.12.1`

## 0.3.0

* Versions:
    * `Kotlin`: `1.3.72` -> `1.4.0`
    * `Coroutines`: `1.3.8` -> `1.3.9`
    * `Klock`: `1.11.14` -> `1.12.0`
* Typealias `KrontabTemplate` was added
* Extension `KrontabTemplate#toSchedule` was added

### 0.2.4

* Updates in libraries:
    * Klock `1.11.3` -> `1.11.14`
    * Coroutines `1.3.7` -> `1.3.8`
* Ranges support were included. Now it is possible to correctly use syntax `0-5` in strings schedules

## 0.2.3

* Updates in libraries:
    * Kotlin `1.3.70` -> `1.3.72`
    * Coroutines `1.3.5` -> `1.3.7`
    * Klock `1.10.0` -> `1.11.3`
* A lot of KDocs added and fixed
* `EverySecondScheduler` changed its building logic - now it is lazy with builder using
* `KronScheduler#doOnce` was optimized: now it will be explicitly called once and return result of its calculations
    * `KronScheduler#doWhile` was rewritten to use `KronScheduler#doOnce` for calculations of `block` result
* New `buildSchedule(String)` function as a shortcut for `createSimpleScheduler(String)`

## 0.2.2

* Updates in libraries:
    * Kotlin `1.3.61` -> `1.3.70`
    * Coroutines `1.3.3` -> `1.3.5`
    * Klock `1.8.6` -> `1.10.0`

## 0.2.1

* Added support of flows: now any `KronScheduler` can be convert to `Flow<DateTime>` using `asFlow` extension

## 0.2.0

* Updated way of publishing (for more info look at the [git](https://git.insanusmokrassar.com/InsanusMokrassar/krontab))
* Updates in libraries:
    * Coroutines `1.3.2` -> `1.3.3`
    * Klock `1.7.3` -> `1.8.6`

# Changelog

## 0.2.0

* Updated way of publishing (for more info look at the [git](https://git.insanusmokrassar.com/InsanusMokrassar/krontab))
* Updates in libraries:
    * Coroutines `1.3.2` -> `1.3.3`
    * Klock `1.7.3` -> `1.8.6`

### 0.2.3

* Updates in libraries:
    * Kotlin `1.3.70` -> `1.3.72`
    * Coroutines `1.3.5` -> `1.3.7`
    * Klock `1.10.0` -> `1.11.3`
* `EverySecondScheduler` changed its building logic - now it is lazy with builder using

### 0.2.2

* Updates in libraries:
    * Kotlin `1.3.61` -> `1.3.70`
    * Coroutines `1.3.3` -> `1.3.5`
    * Klock `1.8.6` -> `1.10.0`

### 0.2.1

* Added support of flows: now any `KronScheduler` can be convert to `Flow<DateTime>` using `asFlow` extension

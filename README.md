# Mini DI container

A small dependency injection container written in plain Java with reflection. I built it while learning how Spring creates and wires beans.

## What it does
- registers classes by name and creates objects from their public constructors
- resolves constructor dependencies recursively (controller → service → repository)
- selects an implementation by type, and by name with `@MiniNamed`
- chooses a constructor marked with `@MiniInject` when there are several
- supports `SINGLETON` and `PROTOTYPE` scopes and external instances (`registerInstance`)
- detects dependency cycles and reports the path, e.g. `Cycle: a -> b -> a`
- rejects interfaces, abstract and non-static nested classes, empty and duplicate names

## Run the scenarios

```
javac -encoding UTF-8 -d out course/di/*.java
java -cp out course.di.HW0122
```
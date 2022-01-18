# Linda

- [Linda](#linda)
  - [Description](#description)
  - [Installation](#installation)
  - [Usage](#usage)
  - [Known issues](#known-issues)
  - [Contributors](#contributors)

## Description

A remotely accessible Tuple database, implemented in Java using RMI Remote objects.

## Installation

This project uses the gradle package manager. To install the required dependencies, run `gradle clean install`.

Tested on Java 17. Any versions above should hopefully work.

## Usage

This project is mostly a proof of concept, and does not include a main class. It does however contain examples, which you can run if you want to try it out.
These can be found in the following packages :
- `linda.prime`
- `linda.search.basic`
- `linda.search.improved`

## Known issues

- Text Search (`linda.search.improved.TextSearch`) cannot use multiple instances of `linda.search.improved.Searcher`. Trying to do so will result in a deadlock (we have tracked down its origin : it seems to occur in CentralizedLinda.java, but sadly, we were unable to fix it).
- LindaServer's (`linda.server.LindaServer`) `stop` method uses `System.exit(0)` to stop all the RMI connections opened by the remote callbacks, which shuts down the entire process instead of only interrupting the srver-side threads.
  > Edit : temporary fix would be to disable server timeout, which makes it so the server never shuts down automatically. This way, the server will never kill the client-side threads when they are running in the same process.

## Contributors

- Philippe Négrel-Jerzy ([l3alr0g](https://github.com/l3alr0g))
- Sébastien Pont ([seba1204](https://github.com/seba1204))
- Ying Liu ([yingliu126](https://github.com/yingliu126))

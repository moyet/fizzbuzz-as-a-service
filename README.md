# fizz-buzz-as-a-service

Are you tired of rewriting fizz buzz at every job interview, then this
package is for you. 

Just build the Docker container and run it on your favorite platform, 
and you now have a REST-api that should give you the results, you'll need.

    $ curl 127.0.0.1:3000/fizzbuzz/135 

    $ FizzBuzz%

## Installation

You'll need a system with leiningen and Docker installed

To build to docker container just run make with:

    $ make uberjar build

Which should build the docker container called fizzbuzz/fizzbuzz.

## Usage

You can start the docker container on your own computer with

    $ docker run -dp 3000:3000 fizzbuzz/fizzbuzz

To test that it works, try to call the service with curl, or
another web-client:

    $ curl 127.0.0.1:3000/fizzbuzz/12    

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2022 Magnus Dreyer

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

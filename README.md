# fizz-buzz-as-a-service

Are you tired of rewriting fizz buzz at every job interview, then this
package is for you. 

Just build the Docker container and run it on your favorite platform, 
and you now have a REST-api that should give you the results, you'll need.

    $ curl 127.0.0.1:3000/fizzbuzz/135 

    $ {"result":"FizzBuzz"}%  

To parse the result you could use jq  to remove that unused information like this:

    $ curl 127.0.0.1:3000/fizzbuzz/15 -s | jq -r .result

## Advanced uses

If the interviewer then ask you how to handle a more varied input, fizzbuzz-as-a-service also have you covered.

    $ curl -X POST http://localhost:3000/fizzbuzz/105 \ 
        -H "Content-Type: application/json" \
        -d '{"Fazz":"3", "Bizz":"5", "Jazz":"7"}'

## Installation

You'll need a system with leiningen and Docker installed

    $ sudo pacman -S leinigen docker jq

To build to docker container just run make with:

    $ make uberjar build

Which should build the docker container called fizzbuzz/fizzbuzz.

## Usage

You can start the docker container on your own computer with

    $ docker run -dp 3000:3000 fizzbuzz/fizzbuzz

To test that it works, try to call the service with curl, or
another web-client:

    $ curl 127.0.0.1:3000/fizzbuzz/12    

**⚠️ Note:** If you're not seeing any response, double-check that you started the container with `-p 3000:3000`. The web server listens on port 3000 inside the container, and you need to publish that port to your host.


## Examples

I have included a test shell script that will query the api 200 times, and write the results
to the screen.

    $ ./fizzbuzz_example.sh

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2025 Magnus Dreyer

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

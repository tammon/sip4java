# SERCOS Internet Protocol for Java
This is a library to communicate to sercos devices over the Sercos Internet Protocol (SIP).

### Functionality
In the first steps of development the focus is set to reading data from a sercos drive.
Further functionality will follow in later releases.

Currently the following message types are supported:
* Connect
* Ping
* ReadOnlyData

Coming soon:
* ReadDataDescription
* ReadEverything
* WriteData

### Latest Release
The latest release is `0.4.2` 

Add sip4java using maven:

```
<dependency>
    <groupId>net.tammon</groupId>
    <artifactId>sip</artifactId>
    <version>0.4.2</version>
</dependency>
 ```
 
Add sip4java using gradle:

```
dependencies {
  compile 'net.tammon:sip:0.4.2'
}
```

## License and Copyright
**Sercos Internet Protocol (SIP) version 1 for Java**

Copyright (c) 2017. tammon (Tammo Schwindt)

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

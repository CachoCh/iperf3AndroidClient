# iPerf3 Android client
## What is iPerf3 ?

iPerf3 is a tool for active measurements of the maximum achievable bandwidth on IP networks. It supports tuning of various parameters related to timing, buffers and protocols (TCP, UDP, SCTP with IPv4 and IPv6). For each test it reports the bandwidth, loss, and other parameters.

For more informatiion, see https://github.com/esnet/iperf, which also includes the iperf3 source code (note that this repository does not include any iperf3 source code).

## iPerf3 Android client
![ ](/images/running.png " ")

# Usage
Starting your own server:

```
$ iperf3 -s
-----------------------------------------------------------
Server listening on 5201 (test #1)
-----------------------------------------------------------
```

## On Android:
![ ](/images/new_test.png" ")

# Public iPerf3 servers

Servers iPerf3 servers will only allow one iPerf connection at a time. Multiple tests at the same time is not supported. If a test is in progress, the following message is displayed: "iperf3: error - the server is busy running a test. try again later"

The Android iPerf3 client app comes with 4 presaved servers configured
![ ](/images/fav.png " ")

# License

This program is Free Software: You can use, study share and improve it at your will. Specifically you can redistribute and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

[protocol]
ip
tcp
udp
icmp
arp

[attr]
mac
srcip
srcport
dstip
dstport

[operator]
and
or
>
<
=
has

[command]
find
list
dump
where
count
limit
sort
skip
help
use

[info]
time
protocol

[example]
find time >= 1231231
find protocol has "tcp.ip" and time > 123 and ip.srcip|ip.dstip in ["1.1.1.1", "2.2.2.2"] or ip[:2] = "x"
find time > 123 and ip.srcip&ip.dstip in ["1.1.1.1", "2.2.2.2"]
dump "/opt/x.pcap" where time > 123
list pcap

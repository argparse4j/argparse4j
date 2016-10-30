#!/usr/bin/env python
import sys
import re

res = [(0, 0, '')]
pat = re.compile(r'^([^;]+);([WAF]) # .*$')
for line in sys.stdin:
    m = pat.match(line)
    if m:
        if len(m.group(1)) > 5:
            first, last = [int(x, 16) for x in m.group(1).split('..')]
            last += 1
        else:
            first = int(m.group(1), 16)
            last = first+1
        lastrange = res[len(res)-1]
        if lastrange[1] == first and lastrange[2] == m.group(2):
            res[len(res)-1] = (lastrange[0], last, lastrange[2])
        else:
            res.append((first, last, m.group(2)))
print "private static final CpRange ranges_[] = {"
for range in res:
    if range[1] == 0: continue
    print "new CpRange(0x{:04X}, 0x{:04X}, EastAsianWidth.{}),"\
        .format(range[0], range[1], range[2])
print "}"

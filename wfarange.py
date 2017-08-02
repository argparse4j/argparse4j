#!/usr/bin/env python
from __future__ import print_function
import sys
import re

res = [(0, 0, '')]
pat = re.compile(r'^([^;]+);([WAF]).*')
for line in sys.stdin:
    m = pat.match(line)
    if m:
        if '..' in m.group(1):
            first, last = (int(x, 16) for x in m.group(1).split('..'))
            last += 1
        else:
            first = int(m.group(1), 16)
            last = first+1
        lastrange = res[len(res)-1]
        if lastrange[1] == first and lastrange[2] == m.group(2):
            res[len(res)-1] = (lastrange[0], last, lastrange[2])
        else:
            res.append((first, last, m.group(2)))
print ("private static final CpRange[] ranges_ = {")
for r in res:
    if r[1] == 0: continue
    print("new CpRange(0x{:04X}, 0x{:04X}, EastAsianWidth.{}),".format(r[0], r[1], r[2]))
print ("};")

/*
 * Copyright (C) 2011 Tatsuhiro Tsujikawa
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.argparse4j.helper;

import java.util.Arrays;
import java.util.Comparator;

/**
 * <p>
 * CJK aware TextWidthCounter implementation.
 * </p>
 * 
 * <p>
 * This class uses East Asian Width information of Unicode and counts 2 for
 * Wide, Full and Ambiguous characters.
 * </p>
 * 
 * <p>
 * <strong>The application code should not use this class directly.</strong>
 * </p>
 * 
 */
public class CJKTextWidthCounter implements TextWidthCounter {

    @Override
    public int width(String text) {
        int len = text.length();
        CpRange key = new CpRange();
        int cnt = 0;
        for (int i = 0, cp; i < len; i += Character.charCount(cp)) {
            cp = text.codePointAt(i);
            key.first = cp;
            key.last = cp + 1;
            if (Arrays.binarySearch(ranges_, key, cpRangeCmp_) >= 0) {
                cnt += 2;
            } else {
                ++cnt;
            }
        }
        return cnt;
    }

    private static enum EastAsianWidth {
        W, // Wide
        F, // Full
        A // Ambiguous
    }

    /**
     * Range is [first, last)
     * 
     */
    private static class CpRange {
        int first, last;
        EastAsianWidth w;

        public CpRange(int first, int last, EastAsianWidth w) {
            this.first = first;
            this.last = last;
            this.w = w;
        }

        public CpRange() {
        }

    }

    private static class CpRangeCmp implements Comparator<CpRange> {

        @Override
        public int compare(CpRange lhs, CpRange rhs) {
            // Assumes rhs is [x, x+1)
            if (lhs.last <= rhs.first) {
                return -1;
            } else if (rhs.last <= lhs.first) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    private static final CpRangeCmp cpRangeCmp_ = new CpRangeCmp();

    /**
     * Compiled using wfarange.py.
     * 
     * # EastAsianWidth-6.0.0.txt
     * # Date: 2010-08-17, 12:17:00 PDT [KW]
     */
    private static final CpRange[] ranges_ = {
            new CpRange(0x00A1, 0x00A2, EastAsianWidth.A),
            new CpRange(0x00A4, 0x00A5, EastAsianWidth.A),
            new CpRange(0x00A7, 0x00A9, EastAsianWidth.A),
            new CpRange(0x00AA, 0x00AB, EastAsianWidth.A),
            new CpRange(0x00AD, 0x00AF, EastAsianWidth.A),
            new CpRange(0x00B0, 0x00B5, EastAsianWidth.A),
            new CpRange(0x00B6, 0x00BB, EastAsianWidth.A),
            new CpRange(0x00BC, 0x00C0, EastAsianWidth.A),
            new CpRange(0x00C6, 0x00C7, EastAsianWidth.A),
            new CpRange(0x00D0, 0x00D1, EastAsianWidth.A),
            new CpRange(0x00D7, 0x00D9, EastAsianWidth.A),
            new CpRange(0x00DE, 0x00E2, EastAsianWidth.A),
            new CpRange(0x00E6, 0x00E7, EastAsianWidth.A),
            new CpRange(0x00E8, 0x00EB, EastAsianWidth.A),
            new CpRange(0x00EC, 0x00EE, EastAsianWidth.A),
            new CpRange(0x00F0, 0x00F1, EastAsianWidth.A),
            new CpRange(0x00F2, 0x00F4, EastAsianWidth.A),
            new CpRange(0x00F7, 0x00FB, EastAsianWidth.A),
            new CpRange(0x00FC, 0x00FD, EastAsianWidth.A),
            new CpRange(0x00FE, 0x00FF, EastAsianWidth.A),
            new CpRange(0x0101, 0x0102, EastAsianWidth.A),
            new CpRange(0x0111, 0x0112, EastAsianWidth.A),
            new CpRange(0x0113, 0x0114, EastAsianWidth.A),
            new CpRange(0x011B, 0x011C, EastAsianWidth.A),
            new CpRange(0x0126, 0x0128, EastAsianWidth.A),
            new CpRange(0x012B, 0x012C, EastAsianWidth.A),
            new CpRange(0x0131, 0x0134, EastAsianWidth.A),
            new CpRange(0x0138, 0x0139, EastAsianWidth.A),
            new CpRange(0x013F, 0x0143, EastAsianWidth.A),
            new CpRange(0x0144, 0x0145, EastAsianWidth.A),
            new CpRange(0x0148, 0x014C, EastAsianWidth.A),
            new CpRange(0x014D, 0x014E, EastAsianWidth.A),
            new CpRange(0x0152, 0x0154, EastAsianWidth.A),
            new CpRange(0x0166, 0x0168, EastAsianWidth.A),
            new CpRange(0x016B, 0x016C, EastAsianWidth.A),
            new CpRange(0x01CE, 0x01CF, EastAsianWidth.A),
            new CpRange(0x01D0, 0x01D1, EastAsianWidth.A),
            new CpRange(0x01D2, 0x01D3, EastAsianWidth.A),
            new CpRange(0x01D4, 0x01D5, EastAsianWidth.A),
            new CpRange(0x01D6, 0x01D7, EastAsianWidth.A),
            new CpRange(0x01D8, 0x01D9, EastAsianWidth.A),
            new CpRange(0x01DA, 0x01DB, EastAsianWidth.A),
            new CpRange(0x01DC, 0x01DD, EastAsianWidth.A),
            new CpRange(0x0251, 0x0252, EastAsianWidth.A),
            new CpRange(0x0261, 0x0262, EastAsianWidth.A),
            new CpRange(0x02C4, 0x02C5, EastAsianWidth.A),
            new CpRange(0x02C7, 0x02C8, EastAsianWidth.A),
            new CpRange(0x02C9, 0x02CC, EastAsianWidth.A),
            new CpRange(0x02CD, 0x02CE, EastAsianWidth.A),
            new CpRange(0x02D0, 0x02D1, EastAsianWidth.A),
            new CpRange(0x02D8, 0x02DC, EastAsianWidth.A),
            new CpRange(0x02DD, 0x02DE, EastAsianWidth.A),
            new CpRange(0x02DF, 0x02E0, EastAsianWidth.A),
            new CpRange(0x0300, 0x0370, EastAsianWidth.A),
            new CpRange(0x0391, 0x03A2, EastAsianWidth.A),
            new CpRange(0x03A3, 0x03AA, EastAsianWidth.A),
            new CpRange(0x03B1, 0x03C2, EastAsianWidth.A),
            new CpRange(0x03C3, 0x03CA, EastAsianWidth.A),
            new CpRange(0x0401, 0x0402, EastAsianWidth.A),
            new CpRange(0x0410, 0x0450, EastAsianWidth.A),
            new CpRange(0x0451, 0x0452, EastAsianWidth.A),
            new CpRange(0x1100, 0x1160, EastAsianWidth.W),
            new CpRange(0x11A3, 0x11A8, EastAsianWidth.W),
            new CpRange(0x11FA, 0x1200, EastAsianWidth.W),
            new CpRange(0x2010, 0x2011, EastAsianWidth.A),
            new CpRange(0x2013, 0x2017, EastAsianWidth.A),
            new CpRange(0x2018, 0x201A, EastAsianWidth.A),
            new CpRange(0x201C, 0x201E, EastAsianWidth.A),
            new CpRange(0x2020, 0x2023, EastAsianWidth.A),
            new CpRange(0x2024, 0x2028, EastAsianWidth.A),
            new CpRange(0x2030, 0x2031, EastAsianWidth.A),
            new CpRange(0x2032, 0x2034, EastAsianWidth.A),
            new CpRange(0x2035, 0x2036, EastAsianWidth.A),
            new CpRange(0x203B, 0x203C, EastAsianWidth.A),
            new CpRange(0x203E, 0x203F, EastAsianWidth.A),
            new CpRange(0x2074, 0x2075, EastAsianWidth.A),
            new CpRange(0x207F, 0x2080, EastAsianWidth.A),
            new CpRange(0x2081, 0x2085, EastAsianWidth.A),
            new CpRange(0x20AC, 0x20AD, EastAsianWidth.A),
            new CpRange(0x2103, 0x2104, EastAsianWidth.A),
            new CpRange(0x2105, 0x2106, EastAsianWidth.A),
            new CpRange(0x2109, 0x210A, EastAsianWidth.A),
            new CpRange(0x2113, 0x2114, EastAsianWidth.A),
            new CpRange(0x2116, 0x2117, EastAsianWidth.A),
            new CpRange(0x2121, 0x2123, EastAsianWidth.A),
            new CpRange(0x2126, 0x2127, EastAsianWidth.A),
            new CpRange(0x212B, 0x212C, EastAsianWidth.A),
            new CpRange(0x2153, 0x2155, EastAsianWidth.A),
            new CpRange(0x215B, 0x215F, EastAsianWidth.A),
            new CpRange(0x2160, 0x216C, EastAsianWidth.A),
            new CpRange(0x2170, 0x217A, EastAsianWidth.A),
            new CpRange(0x2189, 0x218A, EastAsianWidth.A),
            new CpRange(0x2190, 0x219A, EastAsianWidth.A),
            new CpRange(0x21B8, 0x21BA, EastAsianWidth.A),
            new CpRange(0x21D2, 0x21D3, EastAsianWidth.A),
            new CpRange(0x21D4, 0x21D5, EastAsianWidth.A),
            new CpRange(0x21E7, 0x21E8, EastAsianWidth.A),
            new CpRange(0x2200, 0x2201, EastAsianWidth.A),
            new CpRange(0x2202, 0x2204, EastAsianWidth.A),
            new CpRange(0x2207, 0x2209, EastAsianWidth.A),
            new CpRange(0x220B, 0x220C, EastAsianWidth.A),
            new CpRange(0x220F, 0x2210, EastAsianWidth.A),
            new CpRange(0x2211, 0x2212, EastAsianWidth.A),
            new CpRange(0x2215, 0x2216, EastAsianWidth.A),
            new CpRange(0x221A, 0x221B, EastAsianWidth.A),
            new CpRange(0x221D, 0x2221, EastAsianWidth.A),
            new CpRange(0x2223, 0x2224, EastAsianWidth.A),
            new CpRange(0x2225, 0x2226, EastAsianWidth.A),
            new CpRange(0x2227, 0x222D, EastAsianWidth.A),
            new CpRange(0x222E, 0x222F, EastAsianWidth.A),
            new CpRange(0x2234, 0x2238, EastAsianWidth.A),
            new CpRange(0x223C, 0x223E, EastAsianWidth.A),
            new CpRange(0x2248, 0x2249, EastAsianWidth.A),
            new CpRange(0x224C, 0x224D, EastAsianWidth.A),
            new CpRange(0x2252, 0x2253, EastAsianWidth.A),
            new CpRange(0x2260, 0x2262, EastAsianWidth.A),
            new CpRange(0x2264, 0x2268, EastAsianWidth.A),
            new CpRange(0x226A, 0x226C, EastAsianWidth.A),
            new CpRange(0x226E, 0x2270, EastAsianWidth.A),
            new CpRange(0x2282, 0x2284, EastAsianWidth.A),
            new CpRange(0x2286, 0x2288, EastAsianWidth.A),
            new CpRange(0x2295, 0x2296, EastAsianWidth.A),
            new CpRange(0x2299, 0x229A, EastAsianWidth.A),
            new CpRange(0x22A5, 0x22A6, EastAsianWidth.A),
            new CpRange(0x22BF, 0x22C0, EastAsianWidth.A),
            new CpRange(0x2312, 0x2313, EastAsianWidth.A),
            new CpRange(0x2329, 0x232B, EastAsianWidth.W),
            new CpRange(0x2460, 0x24EA, EastAsianWidth.A),
            new CpRange(0x24EB, 0x254C, EastAsianWidth.A),
            new CpRange(0x2550, 0x2574, EastAsianWidth.A),
            new CpRange(0x2580, 0x2590, EastAsianWidth.A),
            new CpRange(0x2592, 0x2596, EastAsianWidth.A),
            new CpRange(0x25A0, 0x25A2, EastAsianWidth.A),
            new CpRange(0x25A3, 0x25AA, EastAsianWidth.A),
            new CpRange(0x25B2, 0x25B4, EastAsianWidth.A),
            new CpRange(0x25B6, 0x25B8, EastAsianWidth.A),
            new CpRange(0x25BC, 0x25BE, EastAsianWidth.A),
            new CpRange(0x25C0, 0x25C2, EastAsianWidth.A),
            new CpRange(0x25C6, 0x25C9, EastAsianWidth.A),
            new CpRange(0x25CB, 0x25CC, EastAsianWidth.A),
            new CpRange(0x25CE, 0x25D2, EastAsianWidth.A),
            new CpRange(0x25E2, 0x25E6, EastAsianWidth.A),
            new CpRange(0x25EF, 0x25F0, EastAsianWidth.A),
            new CpRange(0x2605, 0x2607, EastAsianWidth.A),
            new CpRange(0x2609, 0x260A, EastAsianWidth.A),
            new CpRange(0x260E, 0x2610, EastAsianWidth.A),
            new CpRange(0x2614, 0x2616, EastAsianWidth.A),
            new CpRange(0x261C, 0x261D, EastAsianWidth.A),
            new CpRange(0x261E, 0x261F, EastAsianWidth.A),
            new CpRange(0x2640, 0x2641, EastAsianWidth.A),
            new CpRange(0x2642, 0x2643, EastAsianWidth.A),
            new CpRange(0x2660, 0x2662, EastAsianWidth.A),
            new CpRange(0x2663, 0x2666, EastAsianWidth.A),
            new CpRange(0x2667, 0x266B, EastAsianWidth.A),
            new CpRange(0x266C, 0x266E, EastAsianWidth.A),
            new CpRange(0x266F, 0x2670, EastAsianWidth.A),
            new CpRange(0x269E, 0x26A0, EastAsianWidth.A),
            new CpRange(0x26BE, 0x26C0, EastAsianWidth.A),
            new CpRange(0x26C4, 0x26CE, EastAsianWidth.A),
            new CpRange(0x26CF, 0x26E2, EastAsianWidth.A),
            new CpRange(0x26E3, 0x26E4, EastAsianWidth.A),
            new CpRange(0x26E8, 0x2700, EastAsianWidth.A),
            new CpRange(0x273D, 0x273E, EastAsianWidth.A),
            new CpRange(0x2757, 0x2758, EastAsianWidth.A),
            new CpRange(0x2776, 0x2780, EastAsianWidth.A),
            new CpRange(0x2B55, 0x2B5A, EastAsianWidth.A),
            new CpRange(0x2E80, 0x2E9A, EastAsianWidth.W),
            new CpRange(0x2E9B, 0x2EF4, EastAsianWidth.W),
            new CpRange(0x2F00, 0x2FD6, EastAsianWidth.W),
            new CpRange(0x2FF0, 0x2FFC, EastAsianWidth.W),
            new CpRange(0x3000, 0x3001, EastAsianWidth.F),
            new CpRange(0x3001, 0x303F, EastAsianWidth.W),
            new CpRange(0x3041, 0x3097, EastAsianWidth.W),
            new CpRange(0x3099, 0x3100, EastAsianWidth.W),
            new CpRange(0x3105, 0x312E, EastAsianWidth.W),
            new CpRange(0x3131, 0x318F, EastAsianWidth.W),
            new CpRange(0x3190, 0x31BB, EastAsianWidth.W),
            new CpRange(0x31C0, 0x31E4, EastAsianWidth.W),
            new CpRange(0x31F0, 0x321F, EastAsianWidth.W),
            new CpRange(0x3220, 0x3248, EastAsianWidth.W),
            new CpRange(0x3248, 0x3250, EastAsianWidth.A),
            new CpRange(0x3250, 0x32FF, EastAsianWidth.W),
            new CpRange(0x3300, 0x4DC0, EastAsianWidth.W),
            new CpRange(0x4E00, 0xA48D, EastAsianWidth.W),
            new CpRange(0xA490, 0xA4C7, EastAsianWidth.W),
            new CpRange(0xA960, 0xA97D, EastAsianWidth.W),
            new CpRange(0xAC00, 0xD7A4, EastAsianWidth.W),
            new CpRange(0xD7B0, 0xD7C7, EastAsianWidth.W),
            new CpRange(0xD7CB, 0xD7FC, EastAsianWidth.W),
            new CpRange(0xE000, 0xF900, EastAsianWidth.A),
            new CpRange(0xF900, 0xFB00, EastAsianWidth.W),
            new CpRange(0xFE00, 0xFE10, EastAsianWidth.A),
            new CpRange(0xFE10, 0xFE1A, EastAsianWidth.W),
            new CpRange(0xFE30, 0xFE53, EastAsianWidth.W),
            new CpRange(0xFE54, 0xFE67, EastAsianWidth.W),
            new CpRange(0xFE68, 0xFE6C, EastAsianWidth.W),
            new CpRange(0xFF01, 0xFF61, EastAsianWidth.F),
            new CpRange(0xFFE0, 0xFFE7, EastAsianWidth.F),
            new CpRange(0xFFFD, 0xFFFE, EastAsianWidth.A),
            new CpRange(0x1B000, 0x1B002, EastAsianWidth.W),
            new CpRange(0x1F100, 0x1F10B, EastAsianWidth.A),
            new CpRange(0x1F110, 0x1F12E, EastAsianWidth.A),
            new CpRange(0x1F130, 0x1F16A, EastAsianWidth.A),
            new CpRange(0x1F170, 0x1F19B, EastAsianWidth.A),
            new CpRange(0x1F200, 0x1F203, EastAsianWidth.W),
            new CpRange(0x1F210, 0x1F23B, EastAsianWidth.W),
            new CpRange(0x1F240, 0x1F249, EastAsianWidth.W),
            new CpRange(0x1F250, 0x1F252, EastAsianWidth.W),
            new CpRange(0x20000, 0x2F740, EastAsianWidth.W),
            new CpRange(0x2B740, 0x2FFFE, EastAsianWidth.W),
            new CpRange(0x30000, 0x3FFFE, EastAsianWidth.W),
            new CpRange(0xE0100, 0xE01F0, EastAsianWidth.A),
            new CpRange(0xF0000, 0xFFFFE, EastAsianWidth.A),
            new CpRange(0x100000, 0x10FFFE, EastAsianWidth.A) };

}

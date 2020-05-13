package com.pacmac.devinfo.wifi;

/**
 * Created by pacmac on 2020-05-10.
 */

public enum WifiChannel {

    CH_2(2,2406,2417,2428),
    CH_3(3,2411,2422,2433),
    CH_4(4,2416,2427,2438),
    CH_5(5,2421,2432,2443),
    CH_6(6,2426,2437,2448),
    CH_7(7,2431,2442,2453),
    CH_8(8,2436,2447,2458),
    CH_9(9,2441,2452,2463),
    CH_10(10,2446,2457,2468),
    CH_11(11,2451,2462,2473),
    CH_12(12,2456,2467,2478),
    CH_13(13,2461,2472,2483),
    CH_14(14,2473,2484,2495),
    CH_32(36,0,5170,0),
    CH_34(36,0,5160,0),
    CH_36(36,0,5180,0),
    CH_40(40,0,5200,0),
    CH_44(44,0,5220,0),
    CH_48(48,0,5240,0),
    CH_52(52,0,5260,0),
    CH_56(56,0,5280,0),
    CH_60(60,0,5300,0),
    CH_64(64,0,5320,0),
    CH_100(100,0,5500,0),
    CH_104(104,0,5520,0),
    CH_108(108,0,5540,0),
    CH_112(112,0,5560,0),
    CH_116(116,0,5580,0),
    CH_120(120,0,5600,0),
    CH_124(124,0,5620,0),
    CH_128(128,0,5640,0),
    CH_132(132,0,5660,0),
    CH_136(136,0,5680,0),
    CH_140(140,0,5700,0),
    CH_149(149,0,5745,0),
    CH_153(153,0,5765,0),
    CH_157(157,0,5785,0),
    CH_161(161,0,5805,0),
    CH_165(165,0,5825,0),
    CH_169(165,0,5845,0),
    CH_173(165,0,5865,0),
    UNKNOWN(-1,-1,-1,-1);


    private int channel;
    private double centerFrequency;
    private int lowFrequency;
    private int upperFrequency;

    WifiChannel(int channel, int lowFrequency, double centerFrequency, int upperFrequency) {
        this.channel = channel;
        this.centerFrequency = centerFrequency;
        this.lowFrequency = lowFrequency;
        this.upperFrequency = upperFrequency;
    }


    public static WifiChannel getChannel(int centerFrequency) {
        for(WifiChannel channel :WifiChannel.values()) {
            if (channel.centerFrequency == centerFrequency) {
                return channel;
            }
        }
        return UNKNOWN;
    }

    public int getChannel() {
        return channel;
    }
}


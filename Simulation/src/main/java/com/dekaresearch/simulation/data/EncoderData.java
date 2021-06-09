package com.dekaresearch.simulation.data;

public class EncoderData {
    public static final int MAX_DEVICES = 2;

    private static final EncoderData[] instances = new EncoderData[MAX_DEVICES];

    private int channelA;
    private int channelB;

    static {
        for(int i = 0; i < MAX_DEVICES; i++) {
            instances[i] = new EncoderData();
        }
    }

    public static EncoderData[] getInstances() {
        return instances;
    }

    public void initialize(int channelA, int channelB) {
        // Set channels before init so that the init callbacks can use them if needed
        this.channelA = channelA;
        this.channelB = channelB;
        this.init.set(true);
    }

    public int getChannelA() {
        return channelA;
    }

    public int getChannelB() {
        return channelB;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);
    public SimDataValue<Integer> count = new SimDataValue<>(0);
}

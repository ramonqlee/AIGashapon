package android_serialport_api.vmc.AIGashaponMachine;

import com.idreems.openvm.utils.ByteUtil;

/**
 * Created by ramonqlee on 10/05/2017.
 */

public class LockerReport extends Location implements Report, Instruction {
    public static final byte UNKOWN = (byte) 0xFE;
    public static final byte OPEN = 0x0;
    public static final byte CLOSE = 0x1;
    public static final byte RESET = 0x2;

    private byte mType;

    @Override
    public byte getCode() {
        return (byte) 0x92;
    }

    public byte getType() {
        return mType;
    }

    private String getTypeDesc() {
        String r = "";
        switch (mType) {
            case OPEN:
                r = "旋转锁开";
                break;
            case CLOSE:
                r = "旋转锁关";
                break;
            case RESET:
                r = "复位扭蛋机";
                break;
        }
        return r;
    }

    @Override
    public String name() {
        return "LockerReport Type =" + getTypeDesc();
    }

    @Override
    public int handle(byte[] stream) {
        final int NOMATCH = Report.UNKNOWN;
        try {
            final int protocoLength = 6 + Instruction.CHECKSUM_LEN + 1;

            //  定位开始标志
            // 开始标识 (1)
            final int offset = AIUtils.indexOf(stream, Report.FLAG);
            if (offset < 0) {
                return NOMATCH;
            }

            // 消息长度（1）
            if (stream.length < offset + protocoLength) {
                return NOMATCH;
            }
            final byte messageLen = stream[offset + Instruction.LEN_POS];//消息长度
            if (messageLen != protocoLength - Instruction.CHECKSUM_LEN) {
                return NOMATCH;
            }

            // 柜子地址 (1)
            if (stream[offset + Instruction.ADDR_POS] < Location.MIN_BUS_ADDRESS || stream[offset + Instruction.ADDR_POS] > Location.MAX_BUS_ADDRESS) {
                return NOMATCH;
            }

            // 消息类型 (1)
            if (stream[offset + Instruction.MT_POS] != getCode()) {
                return NOMATCH;
            }

            // 柜子地址 (1)
            setBusAddress(stream[offset + Instruction.ADDR_POS]);
            // 未定义   (1)
            int cursor = offset + Instruction.UNDEF_POS;
            // 自定义数据 (9+4)
            cursor++;
            mType = stream[offset + cursor];

            // 校验和   (2)
            short checkSum = AIUtils.checkSum(stream, offset + protocoLength - Instruction.CHECKSUM_LEN);
            if (ByteUtil.HIBYTE(checkSum) != stream[offset + protocoLength - Instruction.CHECKSUM_LEN] || ByteUtil.LOBYTE(checkSum) != stream[offset + protocoLength - Instruction.CHECKSUM_LEN + 1]) {
                return NOMATCH;
            }

            return offset + protocoLength;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return NOMATCH;
    }
}

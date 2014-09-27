package me.montgome.matasano;

public class Bits {
    private byte[] bytes = new byte[0];
    
    public void putBits(int offset, byte bits, int rightmostBitsToKeep) {
        //System.out.println(String.format("Offset: %s, bits: %s, keep: %s",
        //    offset, Bytes.toBinaryString(bits), rightmostBitsToKeep));
        if (rightmostBitsToKeep == 0) {
            return;
        }
        
        expand(offset + rightmostBitsToKeep);
        int lowIndex = offset / 8;
        int highIndex = (offset + rightmostBitsToKeep - 1) / 8;
        if (lowIndex == highIndex) {
            int numberOfLeadingBits = offset % 8;
            //System.out.println("# leading bits: " + numberOfLeadingBits);
            byte leadingBitMask = (byte) (0xFF << 8 - numberOfLeadingBits);
            //System.out.println("Leading bit mask: " + Bytes.toBinaryString(leadingBitMask));
            int numberOfTrailingBits = 8 - (numberOfLeadingBits + rightmostBitsToKeep);
            //System.out.println("# trailing bits: " + numberOfTrailingBits);
            byte trailingBitMask = (byte) ((1 << numberOfTrailingBits) - 1);
            //System.out.println("Trailing bit mask: " + Bytes.toBinaryString(trailingBitMask));
            byte mask = (byte) (leadingBitMask | trailingBitMask);
            //System.out.println("Mask: " + Bytes.toBinaryString(mask));
            byte shiftedBits = (byte) (bits << numberOfTrailingBits);
            //System.out.println("Shifted bits: " + Bytes.toBinaryString(shiftedBits));
            byte shiftedBitsMasked = (byte) (shiftedBits & ~mask);
            //System.out.println("Shifted bits masked: " + Bytes.toBinaryString(shiftedBitsMasked));
            byte currentValue = bytes[lowIndex];
            //System.out.println("Current value: " + Bytes.toBinaryString(currentValue));
            byte currentMaskedValue = (byte) (bytes[lowIndex] & mask);
            //System.out.println("Current masked value: " + Bytes.toBinaryString(currentMaskedValue));
            byte newValue = (byte) (shiftedBitsMasked | currentMaskedValue);
            //System.out.println("New value: " + Bytes.toBinaryString(newValue));
            bytes[lowIndex] = newValue;
        } else {
            int lowLength = highIndex * 8 - offset;
            int highLength = rightmostBitsToKeep - lowLength;
            putBits(offset, (byte) (0xFF & (bits >>> highLength)), lowLength);
            putBits(offset + lowLength, bits, rightmostBitsToKeep - lowLength);
        }
    }
    
    public void setBit(int offset) {
        expand(offset);
        int index = offset / 8;
        int bit = offset % 8;
        int shift = 7 - bit;
        bytes[index] |= 1 << shift;
    }
    
    public void clearBit(int offset) {
        expand(offset);
        int index = offset / 8;
        int bit = offset % 8;
        int shift = 7 - bit;
        bytes[index] &= 0xFF - (1 << shift);
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    private void expand(int size) {
        int required = (size + 7) / 8;
        if (required > bytes.length) {
            byte[] newBytes = new byte[required];
            for (int i = 0; i < bytes.length; i++) {
                newBytes[i] = bytes[i];
            }
            bytes = newBytes;
        }
    }
}

package codificacoes.golomb;

import codificacoes.Decoder;
import codificacoes.Encoder;

import java.util.ArrayList;
import java.util.BitSet;

public class GolombCodification implements Encoder, Decoder {

	final int divisor;
	final int suffixSize;

	public GolombCodification(int divisor) {
		this.divisor = divisor;
		this.suffixSize = calculateLog2(divisor);
	}

	@Override
	public byte[] decode(byte[] data) {
		ArrayList<Byte> decoded = new ArrayList<>();
		BitSet byteSuffix = new BitSet();
		boolean binaryArea = false;
		int countPrefix = 0;
		int suffixSizeHelp = suffixSize;
		int value, rest, result;

		for (int index = 2; index < data.length; index++) {
			BitSet bits = BitSet.valueOf(new long[] { data[index] });
			for (int i = 7; i >= 0; i--) {
				if (!binaryArea) {
					if (!bits.get(i)) {
						countPrefix++;
					} else {
						binaryArea = true;
					}
				} else {
					if (bits.get(i)) {
						byteSuffix.set(suffixSizeHelp - 1);
					}
					suffixSizeHelp--;
					if (suffixSizeHelp <= 0) {
						value = countPrefix * divisor;
						rest = !byteSuffix.isEmpty() ? byteSuffix.toByteArray()[0] : 0;
						result = value + rest;
						decoded.add((byte) result);
						countPrefix = 0;
						binaryArea = false;
						byteSuffix.clear();
						suffixSizeHelp = suffixSize;
					}
				}
			}
		}

		byte[] decodedBytes = new byte[decoded.size()];
		for (int i = 0; i < decodedBytes.length; i++) {
			decodedBytes[i] = decoded.get(i);
		}

		return decodedBytes;
	}

	@Override
	public byte[] encode(byte[] data) {
		ArrayList<Byte> resultBytes = new ArrayList<>();
		byte resultByte = 0;
		int bitPosition = 0;

		int value, rest, valToShift, aux;

		addHeaderValues(resultBytes);

		for (byte b : data) {
			if (b < 0) {
				aux = 256 + b;
			} else {
				aux = b;
			}

			value = aux / divisor;
			rest = aux - (value * divisor);

			for (int i = 0; i < value; i++) {
				if (bitPosition >= 8) {

					resultBytes.add(resultByte);
					resultByte = 0;
					bitPosition = 0;
				}
				bitPosition++;
			}

			if (bitPosition >= 8) {

				resultBytes.add(resultByte);
				resultByte = 0;
				bitPosition = 0;
			}

			valToShift = 7 - bitPosition;
			resultByte = (byte) (resultByte | (1 << valToShift));
			bitPosition++;

			BitSet bitsOfRest = BitSet.valueOf(new long[] { rest });
			for (int i = suffixSize - 1; i >= 0; i--) {
				if (bitPosition >= 8) {
					// byte is complete, add to array and start over
					resultBytes.add(resultByte);
					resultByte = 0;
					bitPosition = 0;
				}

				if (bitsOfRest.get(i)) {
					valToShift = 7 - bitPosition;
					resultByte = (byte) (resultByte | (1 << valToShift));
				}

				bitPosition++;
			}
		}

		if (bitPosition > 0) {
			resultBytes.add(resultByte);
		}

		byte[] result = new byte[resultBytes.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultBytes.get(i);
		}

		return result;
	}

	private int calculateLog2(int value) {
		return (int) (Math.log(value) / Math.log(2) + 1e-10);
	}

	private void addHeaderValues(ArrayList<Byte> resultBytes) {
		resultBytes.add((byte) 0);
		resultBytes.add((byte) divisor);
	}
}

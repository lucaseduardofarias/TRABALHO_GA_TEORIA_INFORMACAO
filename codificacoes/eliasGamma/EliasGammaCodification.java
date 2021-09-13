package codificacoes.eliasGamma;

import codificacoes.Decoder;
import codificacoes.Encoder;

import java.util.ArrayList;
import java.util.BitSet;

public class EliasGammaCodification implements Encoder, Decoder {

	@Override
	public byte[] decode(byte[] data) {
		ArrayList<Byte> decoded = new ArrayList<>();
		BitSet byteSuffix = new BitSet();
		int count = 0;
		boolean charCodeArea = false;
		boolean isZero = false;

		for (int index = 2; index < data.length; index++) {
			BitSet bits = BitSet.valueOf(new long[] { data[index] });

			for (int i = 7; i >= 0; i--) {
				if (charCodeArea) {

					if (isZero) {
						if (bits.get(i)) {
							decoded.add((byte) 1);
						} else {
							decoded.add((byte) 0);
						}
						charCodeArea = false;
						isZero = false;
						byteSuffix.clear();
						count = 0;
						continue;
					}
					if (count > -1) {
						if (bits.get(i)) {
							byteSuffix.set(count);
						}

						if (count == 0) {

							byte[] converted = byteSuffix.toByteArray();
							decoded.add(converted[0]);
							count = 0;
							charCodeArea = false;
							byteSuffix.clear();
							continue;
						}
						count--;
					}
				} else {
					if (!bits.get(i)) {
						count++;
					} else {

						charCodeArea = true;
						if (count == 0) {
							isZero = true;
						}
					}
				}
			}
		}

		byte[] decodedBytes = new byte[decoded.size()];
		for (int i = 0; i < decodedBytes.length; i++) {
			int ascii = decoded.get(i);
			decodedBytes[i] = (byte) ascii;
		}
		return decodedBytes;
	}

	@Override
	public byte[] encode(byte[] data) {
		ArrayList<Byte> resultBytes = new ArrayList<>();
		byte resultByte = 0;
		int bitPosition = 0;

		addHeaderValues(resultBytes);

		for (byte b : data) {

			int byteLength = Integer.toBinaryString(b).length();

			for (int i = 0; i < byteLength - 1; i++) {
				if (bitPosition >= 8) {

					resultBytes.add(resultByte);
					resultByte = 0;
					bitPosition = 0;
				}
				bitPosition++;
			}

			if (bitPosition >= 8) {
				// byte is complete, add to array and start over
				resultBytes.add(resultByte);
				resultByte = 0;
				bitPosition = 0;
			}
			int valToShift = 7 - bitPosition;
			resultByte = (byte) (resultByte | (1 << valToShift));
			bitPosition++;

			// escreve binario(b)
			for (int i = 0; i < byteLength; i++) {
				if (bitPosition >= 8) {

					resultBytes.add(resultByte);
					resultByte = 0;
					bitPosition = 0;
				}

				String bitsOfRest = Integer.toBinaryString(b);
				if (bitsOfRest.charAt(i) == '1') {
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

	private void addHeaderValues(ArrayList<Byte> resultBytes) {
		resultBytes.add((byte) 1);
		resultBytes.add((byte) 0);
	}
}

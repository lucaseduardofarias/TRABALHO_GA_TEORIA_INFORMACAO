package codificacoes.unaria;

import codificacoes.Decoder;
import codificacoes.Encoder;
import java.util.ArrayList;
import java.util.BitSet;

public class UnaryCodification implements Encoder, Decoder {

	@Override
	public byte[] decode(byte[] data) {
		ArrayList<Byte> decoded = new ArrayList<>();
		int count = 0;

		for (int index = 2; index < data.length; index++) {
			BitSet bits = BitSet.valueOf(new long[] { data[index] });
			for (int i = 7; i >= 0; i--) {
				if (!bits.get(i)) {
					count++;
				} else {
					decoded.add((byte) count);
					count = 0;
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
		int aux;

		addHeaderValues(resultBytes);

		for (byte b : data) {

			if (b < 0) {
				aux = 256 + b;
			} else {
				aux = b;
			}

			for (int i = 0; i < aux; i++) {
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

			int valToShift = 7 - bitPosition;
			resultByte = (byte) (resultByte | (1 << valToShift));
			bitPosition++;
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
		resultBytes.add((byte) 3);
		resultBytes.add((byte) 0);
	}
}
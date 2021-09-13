package codificacoes.fibonacci;

import codificacoes.CodingType;
import codificacoes.Decoder;
import codificacoes.Encoder;

import java.util.ArrayList;
import java.util.Collections;

public class FibonacciCodification implements Encoder, Decoder {

	private final int zeroValue = 235;

	@Override
	public byte[] decode(byte[] data) {
		ArrayList<Integer> decoded = new ArrayList<>();
		int currentNumber = 1;
		int nextNumber = 2;

		int decodeValue = 0;

		int bitPosition = 7;
		for (int bytePosition = 2; bytePosition < data.length;) {
			boolean bit = (data[bytePosition] & (1 << bitPosition)) > 0;
			boolean nextBit = !(bitPosition - 1 < 0 && bytePosition + 1 > data.length - 1)
					&& (data[bitPosition - 1 < 0 ? (bytePosition + 1) : bytePosition]
							& (1 << (bitPosition - 1 < 0 ? 7 : (bitPosition - 1)))) > 0;

			if (bit) {
				decodeValue += currentNumber;
			}

			int temp = currentNumber;
			currentNumber = nextNumber;
			nextNumber = temp + nextNumber;

			bitPosition--;

			if (bit && nextBit) {
				currentNumber = 1;
				nextNumber = 2;
				if (decodeValue == zeroValue) {
					decodeValue = 0;
				}

				decoded.add(decodeValue);
				decodeValue = 0;
		
				bitPosition--;
			}

			if (bitPosition < 0) {
				bitPosition = 7 + bitPosition + 1;
				bytePosition++;
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
		int totalBitsUsed = 0;

		for (byte b : data) {
			if (b == 0) {
				b = (byte) zeroValue;
			}

			int value = Byte.toUnsignedInt(b);
			int rest = value;
			ArrayList<Integer> fibonacciNumbers = getFibonacciNumbersByValue(value);
			fibonacciNumbers.sort(Collections.reverseOrder());

			totalBitsUsed += (fibonacciNumbers.size() + 1);
			int totalBytes = (int) Math.ceil(totalBitsUsed / 8.00);

			while (resultBytes.size() < totalBytes) {
				resultBytes.add((byte) 0);
			}

			int bytePosition = resultBytes.size() - 1;
			byte resultByte = resultBytes.get(bytePosition);

			int bitPosition = 8 - totalBitsUsed % 8;

			if (bitPosition >= 8) {
				bitPosition = 8 - bitPosition;
			}

			resultByte = (byte) (resultByte | (1 << bitPosition));
			bitPosition++;

			@SuppressWarnings("unused")
			int i = 0;
			for (Integer fibonacciNumber : fibonacciNumbers) {
				if (bitPosition >= 8) {
					resultBytes.set(bytePosition, resultByte);
					bytePosition--;
					bitPosition = 0;
					resultByte = resultBytes.get(Math.max(bytePosition, 0));
				}

				if (rest - fibonacciNumber >= 0) {
					rest -= fibonacciNumber;

					resultByte = (byte) (resultByte | (1 << bitPosition));
				}

				bitPosition++;
				i++;
			}

			resultBytes.set(bytePosition, resultByte);
		}

		byte[] result = new byte[resultBytes.size() + 2];

		addHeaderValues(result);

		for (int i = 2; i < result.length; i++) {
			result[i] = resultBytes.get(i - 2);
		}

		return result;
	}

	public ArrayList<Integer> getFibonacciNumbersByValue(int value) {
		ArrayList<Integer> fibonacciNumbers = new ArrayList<>();
		int currentNumber = 1;
		int nextNumber = 2;
		fibonacciNumbers.add(currentNumber);

		while (nextNumber <= value) {
			fibonacciNumbers.add(nextNumber);
			int temp = currentNumber;
			currentNumber = nextNumber;
			nextNumber = temp + nextNumber;
		}

		return fibonacciNumbers;
	}

	private void addHeaderValues(byte[] result) {
		result[0] = (byte) CodingType.Fibonacci.getIdentifier();
		result[1] = (byte) 0;
	}

}

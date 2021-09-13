package codificacoes.delta;

import codificacoes.Decoder;
import codificacoes.Encoder;

import java.util.ArrayList;

public class DeltaCodification implements Encoder, Decoder {

	@Override
	public byte[] decode(byte[] data) {
		// nao finalizado o encode
		return null;
	}

	@Override
	public byte[] encode(byte[] data) {

		ArrayList<Byte> resultBytes = new ArrayList<>();
		byte delta = 0;
		int largestDelta = 0;
		addHeaderValues(resultBytes);

		ArrayList<Byte> deltas = new ArrayList<>();

		for (int i = 0; i < data.length - 1; i++) {
			delta = (byte) (data[i + 1] - data[i]);
			deltas.add(delta);

			if (Math.abs(delta) > Math.abs(largestDelta))
				largestDelta = delta;

			if (i == 0)
				resultBytes.add(data[i]);
		}

		int byteLength = 0;

		for (int i = 0; i < deltas.size() - 1; i++) {
			if (deltas.get(i) != deltas.get(i + 1)) {
				byteLength = Integer.toBinaryString(deltas.get(i)).length();
				delta = (byte) (deltas.get(i) | (1 << byteLength));
			}
			resultBytes.add((byte) (delta));
		}

		byte[] result = new byte[resultBytes.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = resultBytes.get(i);
		}
		return result;
	}

	private void addHeaderValues(ArrayList<Byte> resultBytes) {
		resultBytes.add((byte) 4);
		resultBytes.add((byte) 0);
	}
}

package Inicilizador;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import codificacoes.CodingType;
import codificacoes.Decoder;
import codificacoes.Encoder;
import codificacoes.delta.DeltaCodification;
import codificacoes.eliasGamma.EliasGammaCodification;
import codificacoes.fibonacci.FibonacciCodification;
import codificacoes.golomb.GolombCodification;
import codificacoes.unaria.UnaryCodification;

import static codificacoes.CodingType.*;

public class Main {

	public static void main(String[] args) {
		boolean isOn = true;

		while (isOn) {

			Object[] functions = { "Codificar", "Decodificar" };
			int op = JOptionPane.showOptionDialog(null, "Escolha a funcao desejada: (Para encerrar feche a janela!)",
					"Funcao", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, functions,
					functions[0]);

			if (op == -1) {
				Object[] options = { "Sim, finalizar programa", "Nao, desejo recomeçar" };
				int end = JOptionPane.showOptionDialog(null, "Deseja encerrar o programa?", "Finalizar",
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

				if (end == 0) {
					isOn = false;
					break;
				} else {
					continue;
				}
			}

			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setCurrentDirectory(new java.io.File("./arquivos"));
			if (op == 1) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.cod", "cod");
				fileChooser.setFileFilter(filter);
				fileChooser.addChoosableFileFilter(filter);
			}
			File selectedFile = null;
			int retVal = fileChooser.showOpenDialog(null);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				if (op == 1) {
					while (retVal == JFileChooser.APPROVE_OPTION
							&& !fileChooser.getSelectedFile().getName().endsWith(".cod")) {
						JOptionPane.showMessageDialog(null,
								"O arquivo " + fileChooser.getSelectedFile().getName() + " não um arquivo codificado!",
								"Erro de compatibilidade", JOptionPane.ERROR_MESSAGE);
						retVal = fileChooser.showOpenDialog(null);
					}
				}
				selectedFile = fileChooser.getSelectedFile();
				JOptionPane.showMessageDialog(null, selectedFile.getName());
			}

			if (retVal == 1) {
				continue;
			}

			if (op == 1) {
				try {
					Decoder decoder = null;
					byte[] data = Files.readAllBytes(selectedFile.toPath());
					switch (data[0]) {
					case 0:
						decoder = new GolombCodification(data[1]);
						break;

					case 1:
						decoder = new EliasGammaCodification();
						break;

					case 2:
						decoder = new FibonacciCodification();
						break;

					case 3:
						decoder = new UnaryCodification();
						break;

					case 4:
						decoder = new DeltaCodification();
						break;

					default:
						break;
					}

					if (decoder != null) {
						byte[] result = decoder.decode(data);
						final String ext = ".dec";
						String filePath = selectedFile.getPath();
						int extIndex = filePath.lastIndexOf(".");
						String newPath = (extIndex > -1 ? filePath.substring(0, extIndex) : filePath) + ext;
						Files.write(Paths.get(newPath), result);
						JOptionPane.showMessageDialog(null, "Decodificacao concluida com sucesso");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				/* codificador 
				*	0 - Golomb 
				*	1 - Elias-Gamma 
				*	2 - Fibonacci 
				*	3 - Unaria 
				* 	4 - Delta 
				* */
				Object[] items = { Golomb.getName(), EliasGamma.getName(), Fibonacci.getName(), Unary.getName(),
						Delta.getName() };
				Object selectedValue = JOptionPane.showInputDialog(null, "Escolha um codificador:", "Opcao",
						JOptionPane.INFORMATION_MESSAGE, null, items, items[0]);

				if (selectedValue == null) {
					continue;
				} else {
					final CodingType selectedCodingType = getValueByName((String) selectedValue);
					Encoder encoder = null;

					switch (selectedCodingType) {
					case Golomb:
						boolean invalidDivisor = true;
						String inputValue = null;

						while (invalidDivisor) {
							inputValue = JOptionPane.showInputDialog("Insira o valor do divisor: (Entre 1 e 255)");

							if (inputValue == null) {
								break;
							}

							try {
								int divisor = Integer.parseInt(inputValue);
								encoder = new GolombCodification(divisor);
								if (divisor > 0 && divisor < 256) {
									invalidDivisor = false;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						if (inputValue == null) {
							continue;
						}
						break;
					case EliasGamma:
						encoder = new EliasGammaCodification();
						break;
					case Fibonacci:
						encoder = new FibonacciCodification();
						break;
					case Unary:
						encoder = new UnaryCodification();
						break;
					case Delta:
						encoder = new DeltaCodification();
						break;
					}

					try {
						byte[] data = Files.readAllBytes(selectedFile.toPath());
						byte[] result = encoder.encode(data);
						final String ext = ".cod";
						String filePath = selectedFile.getPath();
						int extIndex = filePath.lastIndexOf(".");
						String newPath = (extIndex > -1 ? filePath.substring(0, extIndex) : filePath) + ext;
						Files.write(Paths.get(newPath), result);
						JOptionPane.showMessageDialog(null, "Codificacao concluda com sucesso");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.exit(0);
	}
}

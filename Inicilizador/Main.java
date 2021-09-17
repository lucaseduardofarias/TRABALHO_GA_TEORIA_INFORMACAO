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

	private static final int CODIFICAR 					= 0;
	private static final int DECODIFICAR 				= 1;
	private static final int BOTAO_FECHAR 				= -1;
	private static final int FINALIZAR_PROGRAMA 		= 0;
	private static final String EXTENSAO_CODIFICADO 	= ".cod";
	private static final String EXTENSAO_DECODIFICADO 	= ".dec";

	public static void main(String[] args) {
		boolean isOn = true;

		while (isOn) {

			Object[] functions = { "Codificar", "Decodificar" };
			int operacao = JOptionPane.showOptionDialog(null,
					"Escolha a funcao desejada: (Para encerrar feche a janela)", "Funcao", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, functions, functions[0]);

			if (operacao == BOTAO_FECHAR) {
				Object[] options = { "Sim, finalizar programa.", "Nao, recomeçar." };

				int end = JOptionPane.showOptionDialog(null, "Deseja encerrar o programa?", "Finalizar",
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

				if (end != FINALIZAR_PROGRAMA) {
					continue;
				}

				isOn = false;
				break;
			}

			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setCurrentDirectory(new java.io.File("./arquivos"));
			
			if (operacao == DECODIFICAR) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.cod", "cod");
				fileChooser.setFileFilter(filter);
				fileChooser.addChoosableFileFilter(filter);
			}
			
			File selectedFile = null;
			int retVal = fileChooser.showOpenDialog(null);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				
				if (operacao == DECODIFICAR) {
					while (retVal == JFileChooser.APPROVE_OPTION
							&& !fileChooser.getSelectedFile().getName().endsWith(EXTENSAO_CODIFICADO)) {
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

			if (operacao == DECODIFICAR) {
				
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
						
						String filePath = selectedFile.getPath();
						int extIndex = filePath.lastIndexOf(".");
						String newPath = (extIndex > -1 ? filePath.substring(0, extIndex) : filePath) + EXTENSAO_DECODIFICADO;
						Files.write(Paths.get(newPath), result);
						JOptionPane.showMessageDialog(null, "Decodificacao concluida com sucesso");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (operacao == CODIFICAR) {

				Object[] items = { Golomb.getName(), EliasGamma.getName(), Fibonacci.getName(), Unary.getName(), Delta.getName() };
						
				Object selectedValue = JOptionPane.showInputDialog(null, "Escolha um codificador:", "Opcao",
						JOptionPane.INFORMATION_MESSAGE, null, items, items[0]);

				if (selectedValue == null) {
					continue;
				} 
				
				final CodingType selectedCodingType = getValueByName((String) selectedValue);
				Encoder encoder = null;

				switch (selectedCodingType) {
				
					case Golomb:
						boolean invalidDivisor = true;
						String inputValue = null;
	
						while (invalidDivisor) {
							
							inputValue = JOptionPane.showInputDialog("Insira o valor do divisor: (Entre os numeros 1 e 255)");
	
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
			
					String filePath = selectedFile.getPath();
					int extIndex = filePath.lastIndexOf(".");
					
					String newPath = (extIndex > -1 ? filePath.substring(0, extIndex) : filePath) + EXTENSAO_CODIFICADO;
					
					Files.write(Paths.get(newPath), result);
					
					JOptionPane.showMessageDialog(null, "Codificacao concluda com sucesso");
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		System.exit(0);
	}
}

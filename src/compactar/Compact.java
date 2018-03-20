package compactar;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class Compact extends JFrame{

	/**
	 * 
	 */

	static final int TAMANHO_BUFFER = 4096;
	private static String [] signatureDB;
	private JPanel contentPane;
	private JTextField text;
	private JButton find;
	private JButton load;
	private JButton cancel;
	private static JLabel mensagem;
	private static ServerSocket server;
	
	public Compact() {
		
		contentPane = new JPanel();
		contentPane.setLayout(null);
		JLabel info = new JLabel("Arquivo:");
		text = new JTextField();
		mensagem = new JLabel();
		find = new JButton("Encontrar");
		load = new JButton("Enviar");
		cancel = new JButton("Cancelar");
		
		info.setLocation(20, 20);
		info.setSize(50, 20);
		text.setLocation(80, 20);
		text.setSize(250,30);
		find.setLocation(350, 20);
		find.setSize(100,30);
		load.setLocation(120,100);
		load.setSize(100,30);
		cancel.setLocation(240,100);
		cancel.setSize(100,30);
		mensagem.setLocation(140, 160);
		mensagem.setSize(250, 20);
		
		contentPane.add(cancel);
		contentPane.add(load);
		contentPane.add(find);
		contentPane.add(text);
		contentPane.add(info);
		contentPane.add(mensagem);
		
		JButtonHandler handler = new JButtonHandler();
		find.addActionListener(handler);
		load.addActionListener(handler);
		cancel.addActionListener(handler);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 300);
		setContentPane(contentPane);
	}
	
	private class JButtonHandler implements ActionListener {

		private File arquivo;

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			if (e.getSource() == find) {
				JFileChooser file = new JFileChooser(); 
		        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        int i= file.showSaveDialog(null);
		        if (i==1){
		           text.setText("");
		        } else {
		           arquivo = file.getSelectedFile();
		           text.setText(arquivo.getPath());
		        }
			}
			
			if (e.getSource() == load) {
				
				try {
					
					String arquivoCompactar = arquivo.getPath();
					String arquivoSaida = System.getProperty("user.dir") + "\\virus.zip";
					
					Compact.compactarParaZip(arquivoCompactar, arquivoSaida);
					
					Compact.sendSock(arquivoSaida);
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NullPointerException e2) {
					mensagem.setText("Nenhum arquivo Inserido");
				}
			}
			
			if (e.getSource() == cancel) {
				System.exit(0);			
			}
			
		}

	}
	
	 public static void compactarParaZip(String arquivoCompactar, String arquivoSaida)throws IOException {
		 		
			 try {
		            FileOutputStream destino = new FileOutputStream(arquivoSaida);
		            ZipOutputStream saida = new ZipOutputStream(destino);
		            File file = new File(arquivoCompactar);
		            FileInputStream streamDeEntrada = new FileInputStream(file);
		            BufferedInputStream origem = new BufferedInputStream(streamDeEntrada, TAMANHO_BUFFER);
		            
		            ZipEntry entry = new ZipEntry(file.getName());
		            saida.putNextEntry(entry);
		                       
		            int cont;
					byte[] dados = new byte[TAMANHO_BUFFER];
					while((cont = origem.read(dados , 0, TAMANHO_BUFFER)) != -1) {
		                saida.write(dados, 0, cont);
		            }
		            origem.close();
		            saida.close();
		        } catch(IOException e) {
		            throw new IOException(e.getMessage());
		        }
		   
		 
	 }
	 
	 private static void sendSock(String arquivoCompactado) {
			// TODO Auto-generated method stub
			
		 try {
			Socket cliente = new Socket("192.168.0.115", 6001);
			mensagem.setText("Cliente Conectado na Porta 6001.");
			
			DataOutputStream saida = new DataOutputStream(cliente.getOutputStream());
			FileInputStream entradaArquivo = new FileInputStream(arquivoCompactado);
			DataInputStream entrada = new DataInputStream(entradaArquivo);
			BufferedInputStream origem =  new BufferedInputStream(entrada, TAMANHO_BUFFER);
			
			int cont;
			byte[] dados = new byte[TAMANHO_BUFFER];
			while((cont = origem.read(dados , 0, TAMANHO_BUFFER)) != -1) {
                saida.write(dados, 0, cont);
            }
			
			saida.close();
			entrada.close();
			cliente.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			
		} catch(ConnectException e3) {
			mensagem.setText("Erro de Conexão: Servidor Não Encontrado!");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	 
	 private static void receiveInfo() {
		// TODO Auto-generated method stub
		
		try {
			server = new ServerSocket(6004);
			
			while(true) {
				
				Socket client = server.accept();
				
		        //PrintWriter saida = new PrintWriter(client.getInputStream().toString());
		        //signatureDB = (String[]) saida.readObj ect();
		       
		        BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        String msg = bf.readLine();
		        System.out.println(msg);
		        JOptionPane.showMessageDialog(null, msg);
		        //Compact.frame();
		        
		        //saida.close();
		        bf.close();
		        client.close();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void frame(){
		// TODO Auto-generated method stub
		JFrame j = new JFrame();
		JPanel panel = new JPanel();
		JLabel vd = new JLabel("Virus Detecdados:");
		JList lista = new JList(signatureDB);

		lista.setVisibleRowCount(signatureDB.length);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setLayoutOrientation (JList.VERTICAL);
		vd.setLocation(20, 20);
		vd.setSize(80,20);
		lista.setLocation(40,40);
		lista.setSize(350,250);
		
		panel.add(vd);
		panel.add(lista);
		panel.add(new JScrollPane(lista));
		
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setBounds(100, 100, 500, 300);
		j.setContentPane(panel);
		j.setVisible(true);
	}

	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Compact frame = new Compact();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Compact.receiveInfo();

	}

}

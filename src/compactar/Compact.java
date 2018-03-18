package compactar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compact {

	static final int TAMANHO_BUFFER = 4096;
	private static String [] signatureDB;
	
	 public static void compactarParaZip(String arquivoCompactar, String arquivoCompactado)throws IOException {

			 try {
		            FileOutputStream destino = new FileOutputStream(new File(arquivoCompactado));
		            ZipOutputStream saida = new ZipOutputStream(new BufferedOutputStream(destino));
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
			Socket cliente = new Socket("192.168.0.2", 3001);
			System.out.print("Cliente Conectado");
			
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
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	private static void receiveInfo() {
		// TODO Auto-generated method stub
		
		try {
			ServerSocket server = new ServerSocket(6002);
			
			while(true) {
				
				Socket client = server.accept();
		        ObjectInputStream saida = new ObjectInputStream(client.getInputStream());
		        signatureDB = (String[]) saida.readObject();
		        
		        System.out.println("Virus detectados: ");
		        for (int i = 0; i < signatureDB.length; i++) {
		        	
		        	System.out.println(signatureDB[i] + "; ");
		        }
		        
		        saida.close();
		        client.close();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	
	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		String arquivoCompactar = System.getProperty("user.dir") + "\\virusDB\\dummyfile.bin";
		String arquivoCompactado = System.getProperty("user.dir") + "\\virusDB.zip";
		
		try {
			Compact.compactarParaZip(arquivoCompactar, arquivoCompactado);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Compact.sendSock(arquivoCompactado);
		Compact.receiveInfo();

	}

}

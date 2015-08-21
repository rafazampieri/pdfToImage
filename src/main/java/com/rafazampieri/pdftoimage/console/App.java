package com.rafazampieri.pdftoimage.console;

import java.io.File;

import com.rafazampieri.pdftoimage.model.enums.ImageTypeEnum;
import com.rafazampieri.pdftoimage.model.service.PdfToImageService;


public class App {
	
	public static void main(String[] args) throws Exception {
		Integer dpiOut = null;
		File pdfFilePath = null;
		ImageTypeEnum imageType = null;
		Integer page = null;
		File directoryOut = null;
		
		boolean isShowHelp = false;
		if( args.length == 0 || args.length == 1){
			isShowHelp = true;
		} else if( args.length > 0 ){
			for (int i = 0; i < args.length; i++) {
				String parameter = args[i].trim();
				
				if(parameter.equalsIgnoreCase("-pdf") && (i+1 < args.length)){
					pdfFilePath = new File( args[++i] );
					if( pdfFilePath.exists() == false){ showMessageAndShutdown("Informe o caminho de um arquivo PDF Válido."); }
				} else if(parameter.equalsIgnoreCase("-dpi") && (i+1 < args.length)){
					String paramDpi = args[++i];
					dpiOut = (paramDpi.matches("[0-9]*") ? Integer.parseInt(paramDpi) : null);
				} else if(parameter.equalsIgnoreCase("-imageType") && (i+1 < args.length)){
					try{
						imageType = ImageTypeEnum.valueOf(args[++i].toUpperCase());
					} catch (Exception e) { showMessageAndShutdown("Preencha o valor do -imageType com: jpg, png ou gif"); }
				} else if(parameter.equalsIgnoreCase("-page") && (i+1 < args.length)){
					String paramPage = args[++i];
					page = (paramPage.matches("[0-9]*") ? Integer.parseInt(paramPage) : null);
				} else if(parameter.equalsIgnoreCase("-directoryOut") && (i+1 < args.length)){
					pdfFilePath = new File( args[++i] );
					
				} else {
					isShowHelp = true;
					break;
				}
			}
		}
		
		if( isShowHelp ){
			showHelp();
		} else {
			new PdfToImageService().createImagesFromPdf(pdfFilePath, imageType, dpiOut, directoryOut, page);
		}
	}

	private static void showMessageAndShutdown(String message) {
		System.out.println( message );
		System.exit(0);
	}

	private static void showHelp() {
		System.out.println( "Help" );
		System.exit(0);
	}

}

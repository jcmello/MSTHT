
package MSTHT;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel;
import inra.ijpb.morphology.strel.DiskStrel;
import java.util.ArrayList;

/**
 *
 * @author Julio César Mello Román, Jesús César Ariel López Colmán
 * @author Facultad Politécnica, Universidad Nacional de Asunción - Universidad Nacional de Concepción
 * @version 2021
 */

public class MultiscaleMathematicalMorphology {

    public static ImageProcessor MSTHT(String path, int radio, int n, double w){
        ImagePlus imp = IJ.openImage(path);
        IJ.run(imp, "8-bit", "");
        ImagePlus imp2 = imp.duplicate();
        int M = imp2.getWidth();
        int N = imp2.getHeight();
        ImageProcessor f = imp2.getProcessor();
        
        int i = 1;
        int r = radio;
        Strel B = DiskStrel.fromRadius(r);
        ImageProcessor eRosion = Morphology.erosion(f, B); 
        ImageProcessor apertura = Morphology.dilation(eRosion, B);  //Apertura morfológica clásica
        ImageProcessor BM = resta(f,apertura);
        ImageProcessor dIlation = Morphology.dilation(f, B);
        ImageProcessor cierre = Morphology.erosion(dIlation, B);  //Cierre morfológico clásico
        ImageProcessor OM = resta(cierre,f);
        
        ArrayList<ImageProcessor> matrizBM = new ArrayList<>();
        ArrayList<ImageProcessor> matrizOM = new ArrayList<>();
        matrizBM.add(BM);
        matrizOM.add(OM);
        ArrayList<ImageProcessor> matrizRBM = new ArrayList<>();
        ArrayList<ImageProcessor> matrizROM = new ArrayList<>();
        ImageProcessor dRBM = f.createProcessor(M, N);
        ImageProcessor dROM = f.createProcessor(M, N);
        
        //
        if(n > 1){
            for(i = 2; i <= n; i++){ 
                
                r = r + 1;
                B = DiskStrel.fromRadius(r);
                eRosion = Morphology.erosion(f, B); 
                apertura = Morphology.dilation(eRosion, B);  //Apertura morfológica clásica
                BM = resta(f,apertura);
                dIlation = Morphology.dilation(f, B);
                cierre = Morphology.erosion(dIlation, B);  //Cierre morfológico clásico
                OM = resta(cierre,f);
                matrizBM.add(BM);
                matrizOM.add(OM);
                 
                if(i == 2){
                    dRBM = resta(matrizBM.get(i-1), matrizBM.get(i-2));
                    matrizRBM.add(dRBM);
                    dROM = resta(matrizOM.get(i-1), matrizOM.get(i-2));
                    matrizROM.add(dROM);
                }else if(i > 2){
                    dRBM = resta(matrizBM.get(i-1), matrizRBM.get(i-3));
                    matrizRBM.add(dRBM);
                    dROM = resta(matrizOM.get(i-1), matrizROM.get(i-3));
                    matrizROM.add(dROM);
                }   
            }
        }

        ImageProcessor SBM = matrizBM.get(0);
        ImageProcessor SOM = matrizOM.get(0);        
        ImageProcessor SRBM = matrizRBM.get(0);
        ImageProcessor SROM = matrizROM.get(0);
        
        for (int x = 1; x < n; x++) {
            SBM = suma(SBM, matrizBM.get(x));
            SOM = suma(SOM, matrizOM.get(x));
        }
        
        for (int x = 1; x < n-1; x++) {
            SRBM = suma(SRBM, matrizRBM.get(x));
            SROM = suma(SROM, matrizROM.get(x));
        }     
        
        ImageProcessor fen = enhancement(f, SBM, SRBM, SOM, SROM, w);

        return fen;
    
    }
    
    //**********************************************************************//
    //**********************************************************************//
    
    private static ImageProcessor resta(ImageProcessor f1, ImageProcessor f2) {
        int M = f1.getWidth();
        int N = f1.getHeight();
        ImageProcessor res = f1.createProcessor(M, N);
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                int val = f1.getPixel(i, j) - f2.getPixel(i, j);
                if(val<0) val=0;
                res.putPixel(i, j, val);
            }
        }
        return res;
    }
    
    private static ImageProcessor suma(ImageProcessor f1, ImageProcessor f2) {
        int M = f1.getWidth();
        int N = f1.getHeight();
        ImageProcessor res = f1.createProcessor(M, N);
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                int val = f1.getPixel(i, j) + f2.getPixel(i, j);
                if(val<0) val=0;
                res.putPixel(i, j, val);
            }
        }
        return res;
    }
    

    private static ImageProcessor enhancement(ImageProcessor f, ImageProcessor SBM, ImageProcessor SRBM, ImageProcessor SOM, ImageProcessor SROM, double w) {
        int M = f.getWidth();
        int N = f.getHeight();
        ImageProcessor res = f.createProcessor(M, N);
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){                
                int val = (int)(f.getPixel(i, j) + (w * (SBM.getPixel(i, j) + SRBM.getPixel(i, j))) - (w * (SOM.getPixel(i, j) + SROM.getPixel(i, j))));
                if(val<0) val=0;
                if(val>255) val=255;
                res.putPixel(i, j, val);    
            }
        }
        return res;
    }
    
}

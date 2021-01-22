
package MSTHT;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.io.File;

public class Test_MSTHT {

  
    public static void main(String[] args){
        String pathBase=System.getProperty("user.dir")+File.separator+"src"+File.separator+"MSTHT"+File.separator;
        String path = pathBase+"68.png";
        ImagePlus imp = IJ.openImage(path);
        IJ.run(imp, "8-bit", "");
        imp.show();
        ImagePlus imp2 = imp.duplicate();
        int M = imp2.getWidth();
        int N = imp2.getHeight();
        ImageProcessor ip = imp2.getProcessor();
        int[][] imageO = new int[M][N];

        for (int j = 0; j <N; j++) {
            for (int i = 0; i <M; i++) {
                int p = ip.getPixel(i, j);
                imageO[i][j] = p;

            }
        }
        
        //MSTHT
        int[][] image = new int[M][N];
        int rad = 1;//Radio inicial del elemento estructurante en forma de disco
        int n = 6;//Nú,ero de iteraciones
        double w = 0.35;//Peso de ajuste de contraste
        ImageProcessor ip2 = MultiscaleMathematicalMorphology.MSTHT(path, rad, n, w);
        
        for (int j = 0; j <N; j++) {
            for (int i = 0; i <M; i++) {
                int p = ip2.getPixel(i, j);
                image[i][j] = p;

            }
        }

        for (int i = 0; i <M; i++) {
            for (int j = 0; j <N; j++) {
                int p = image[i][j];
                ip.putPixel(i, j, p);
            }
        }        
                
        imp2.show();
        
    }
    
}

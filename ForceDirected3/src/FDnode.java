import java.util.ArrayList;

public class FDnode {
    double x,y,num;
    double forceX,forceY;
    ArrayList neighbor = new ArrayList<>();
    FDnode(){
        x=y=num=0;
        forceX=forceY=0.0;
        neighbor.clear();
    }
}

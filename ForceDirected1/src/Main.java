import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import java.util.*;

class FD extends JFrame  
{  
    FD(String s)  
    {  
        super(s);  
        setLayout(null);  
        setBounds(0,0,Main.screenWidth,Main.screenHeight);   
        setBackground(new Color(204,204,255));  
        setVisible(true);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }  
    public void paint(Graphics g)//重写paint()方法  
    {  
        for(int i=0;i<=Main.maxnode;i++)
        {  
            g.setColor(Color.orange);    //设置颜色  
            FDnode node1 = Main.nodes[i];
            g.fillOval((int)node1.x+Main.det,(int)node1.y+Main.det,10,10); //画实心圆  
            //System.out.println(Main.nodes[i].x+" "+Main.nodes[i].y);
            int sz = Main.nodes[i].neighbor.size();
            for(int j=0;j<sz;j++) {
                int tmp = (int) node1.neighbor.get(j);
                FDnode node2 = Main.nodes[tmp];
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine((int)node1.x+Main.det,(int)node1.y+Main.det,(int)node2.x+Main.det,(int)node2.y+Main.det);
            }
            
        }  
    }  
    
}  



public class Main {
    static final double eps = 0.001;

    static int det = 0;
    
    
    static double L = 100; // spring rest length
    static int delta_t = 1; // time step
    static double MAX_DISPLACEMENT_SQUARED = 5000;
    static final int maxcalc = 500;
    static final int magsize = 100;
    
    static int level = 100;
    static int cho = 1;
    static String st[] = {"bus685","bus1138","facebook_899","facebook_1899","facebook_4039"};
    static int stcho = 4;
    static String filename = "facebook_4039";
    static int screenWidth,screenHeight;
    static double K_r; //repulsive force constant
    static double K_s; //spring constant
    static Random random = new Random();
    static FDnode nodes[] = new FDnode[4039];
    static int maxnode = 0;
    
    public static double randWid() {
        return Math.random()*screenWidth;
    }
    public static double randHei() {
        return Math.random()*screenHeight;
    }
    static double dx,dy,fx,fy; 
    public static void calc() {
        level= Math.min(level, maxnode);
        //initialize net forces
        for(int i=0;i<=maxnode;i++) {nodes[i].forceX=0;nodes[i].forceY=0;}
        //repulsion between all pairs
        for(int i1 = 0;i1<=maxnode-1;i1++) {
            FDnode node1 = nodes[i1];
            int cnt  = 1;
            int nodenum = i1;
            while(cnt<level) {
                cnt++;
                int nxtsz = nodes[nodenum].neighbor.size();
                if(nxtsz==0)continue;
                int i2 = (int) nodes[nodenum].neighbor.get(0);
                FDnode node2 = nodes[i2];
                dx = node2.x - node1.x;
                dy = node2.y - node1.y;
//                System.out.println(dx+" "+dy);
                if(Math.abs(dx-0.0)>eps || Math.abs(dy-0.0)>eps) {
                    double distanceSquared = dx *dx + dy*dy;
                    double distance = Math.sqrt( distanceSquared );
                    double force = K_r / distanceSquared;
                    
                    fx = force * dx / distance;
                    fy = force * dy / distance;
                    
//                    System.out.println(fx+" "+fy);
                    nodes[nodenum].forceX = nodes[nodenum].forceX - fx;
                    nodes[nodenum].forceY = nodes[nodenum].forceY - fy;
                    nodes[i2].forceX = nodes[i2].forceX + fx;
                    nodes[i2].forceY = nodes[i2].forceY + fy;
                }
                node1 = nodes[i2];
                nodenum = i2;
                
            }
           
        }
//            System.out.println(node1.forceX+" "+ node1.forceY);
            

        

     //     spring force between adjacent pairs
        for(int i1 = 0;i1<=maxnode;i1++) {
            FDnode node1 = nodes[i1];
            int sz = node1.neighbor.size();
            for(int j=0;j<sz;j++) {
                int i2 = (int) node1.neighbor.get(j);
                FDnode node2 = nodes[i2];
                if(i1<i2) {
                    dx = node2.x - node1.x;
                    dy = node2.y - node1.y;
                    if(Math.abs(dx-0.0)>eps || Math.abs(dy-0.0)>eps) {
                        double distance = Math.sqrt( dx *dx + dy*dy );
                        double force = K_s * (distance-L);
                        //System.out.println(fx+" "+fy);
                        fx = force * dx / distance;
                        fy = force * dy / distance;
                        nodes[i1].forceX = nodes[i1].forceX + fx;
                        nodes[i1].forceY = nodes[i1].forceY + fy;
                        nodes[i2].forceX = nodes[i2].forceX - fx;
                        nodes[i2].forceY = nodes[i2].forceY - fy;
                    }
                }
            }
        }
       double sum = 0;
     // update positions
        for(int i=0;i<=maxnode;i++) {
            FDnode node = nodes[i];
            dx = delta_t*node.forceX;
            dy = delta_t*node.forceY;
            //sum+=Math.sqrt(node.forceX*node.forceX+node.forceY*node.forceY);
//            System.out.println(fx+" "+fy);
//            System.out.println(dx+" "+dy);
            double displacementSquared = dx*dx + dy*dy;
//            System.out.println(dx+" "+dy);
            //System.out.println(displacementSquared);
            if ( displacementSquared > MAX_DISPLACEMENT_SQUARED ) {
                double s = Math.sqrt( MAX_DISPLACEMENT_SQUARED / displacementSquared );
                dx = dx * s;
                dy = dy * s;
                
            }
//            sum+=Math.sqrt(dx*dx+dy*dy);
            
            nodes[i].x = nodes[i].x + dx;
            nodes[i].y = nodes[i].y + dy;
        }
        //System.out.println(sum);
        
    }
    
    public static void ReadFile(String Filename) throws IOException {
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(Filename));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                
                result.append(s+System.lineSeparator());
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
//        System.out.println(result.toString());
        String str = result.toString();
        Scanner in = new Scanner(str);
        int x,y;
        while(in.hasNext()) {
            x = in.nextInt();
            y = in.nextInt();
            maxnode = Math.max(maxnode, x);
            maxnode = Math.max(maxnode, y);
            if(nodes[x].num==0) {
                nodes[x].x = randWid();
                nodes[x].y = randHei();
                nodes[x].num=1;
                fw.write(x+" "+nodes[x].x+" "+nodes[x].y+"\r\n");
                
            }
            nodes[x].neighbor.add(y);
            if(nodes[y].num==0) {
                nodes[y].x = randWid();
                nodes[y].y = randHei();
                nodes[y].num=1;
                fw.write(y+" "+nodes[y].x+" "+nodes[y].y+"\r\n");
            }
            nodes[y].neighbor.add(x);
            
        }
        
        K_r = screenHeight*screenWidth*1.0*magsize/maxnode;
        K_s = Math.sqrt(K_r);
        //System.out.println(maxnode);
        return;
        
        
    }
    
    
    public static void ReadFile2(String Filename,String source) throws IOException {
        StringBuilder result = new StringBuilder();
        System.out.println(Filename);
        try{
            BufferedReader br = new BufferedReader(new FileReader(Filename));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                
                result.append(s+System.lineSeparator());
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(result.toString());
        String str = result.toString();
        //System.out.println(str);
        Scanner in = new Scanner(str);
        int n;
        double x,y;
        while(in.hasNextInt()) {
            n = in.nextInt();
            x = in.nextDouble();
            y = in.nextDouble();
//            System.out.println(n+" "+x+" "+y);
            maxnode = Math.max(maxnode, n);
            nodes[n].x = x;
            nodes[n].y = y;
            nodes[n].num=1;


        }
        
        result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(source));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                
                result.append(s+System.lineSeparator());
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }    
        str = result.toString();
        in = new Scanner(str);
        int tx,ty;
        while(in.hasNext()) {
            tx = in.nextInt();
            ty = in.nextInt();
            //System.out.println(tx+" "+ty);
            nodes[tx].neighbor.add(ty);
            nodes[ty].neighbor.add(tx);
        }
        
        K_r = screenHeight*screenWidth*1.0*magsize/maxnode;
        K_s = Math.sqrt(K_r);
        //System.out.println(maxnode);
        return;
        
        
    }
    
    
    static FileWriter fw ;
    public static void init() {
        screenWidth=((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
        for(int i=0;i<4039;i++)nodes[i] = new FDnode();
        filename = st[stcho];
        
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        init();
        System.out.println("Reading Files...");
        if(cho==1) {
            
            try {
                try {
                    fw = new FileWriter("D:\\D\\rand.txt");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ReadFile("D:\\D\\"+filename+".txt");
                fw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(cho == 2) {
            try {
                ReadFile2("D:\\D\\rand.txt","D:\\D\\"+filename+".txt");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Calculating...");
        long startTime=System.currentTimeMillis();   //获取开始时间
        for(int ii=0;ii<maxcalc;ii++)
            calc();
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        System.out.println("Drawing...");
        new FD("FR");  
    }

}

package jams.tree;
import java.io.*;
import java.util.*;

public class baumpart {
    
    static String hrudatei="hrus_new.par";
    static String reachdatei="reach.par";
    static int headerLines = 5;
    static int maxsize=100;
    static int hruToPolyPos = 7;// impliziert aktuell, dass die n�chste Position to_reach enth�lt, etwas sperrig...
    static int reachToReachPos = 3;
    
    
    public static void main(String[] args) {
        
        //bestimmt die Anzahl der Gebiete in den einzelnen Datein
        int hruanzahl=anz(hrudatei);
        int reachanzahl=anz(reachdatei);
        
        //legt das Array, in dem die Verkn�pfungen zwischen Kindern und V�tern stehen sollen, an
        int[] baumarray=new int[hruanzahl+reachanzahl+1];
        //legt das Array, in dem die urspr�ngliche Bezeichnung jedes Index steht, an
        String[] namensarray=new String[hruanzahl+reachanzahl+1];
        //Zwischenspeicherung der Fliesselemente (hru/reaches) entsprechend dem Namen 
        String[] toFlowArray=new String[hruanzahl+reachanzahl+1];
       // String[] toFlowHashMap = new HashMap(hruanzahl+reachanzahl);
        fillArray(hrudatei, "hru", 1, namensarray,toFlowArray);
        fillArray(reachdatei, "reach", hruanzahl+1, namensarray,toFlowArray);
        
        //liest die Kind/Vater-Beziehungen der hru- bzw. der reach-Datei ein. Diese stehen dann im baumarray.
   
        bindArray(namensarray,toFlowArray,baumarray);
       // bindArray(reachdatei, "reach", hruanzahl+1, namensarray,baumarray);
        
        //legt den Baum aus dem baumarray an. Der Elementeintrag entspricht dem gespeicherten Namen im namensarray
        Node wurzel=baum(baumarray, namensarray);
        //gibt den gespeicherten Baum aus
        //baumausgabe(wurzel);System.out.println();
        
        //legt eine Liste von B�umen an, die dann nacheinander parallel bearbeitet werden k�nnen
        Liste partitioningtree = partitioning(wurzel);
        listenausgabe(partitioningtree);
    }
    
    public static Liste<Node> partitioning(Node wurzel){
        return partitioning(wurzel, 1);
    }
    
    public static Liste<Node> partitioning(Node wurzel, int n){
        baumumsortierung(wurzel); //sortiert den Baum so um, dass immer die Knoten mit gr��ter Size ganz links stehen
        Node kindknoten=wurzel;
        Liste<Node> teilbaum = new Liste<Node>();
        teilbaum.setNummer(n); //nummeriert die Teilb�ume durch
        if (wurzel.getSize()<=maxsize){//letzter Baum
            teilbaum.setElement(wurzel);
            teilbaum.setNext(null);
            return teilbaum;
        }
        while (kindknoten.getSize()>maxsize){//Suche nach Teilbaum, der klein genug ist.
            kindknoten=kindknoten.getlinkesterSohn();
        }
        int size=kindknoten.getSize();
        //l�scht die Verbindung des Vaterknotens zum gefundenen Teilbaum
        kindknoten.getVater().setDegree(kindknoten.getVater().getDegree()-1);
        kindknoten.getVater().setlinkesterSohn(kindknoten.getrechterBruder());
        kindknoten.setrechterBruder(null);
        teilbaum.setElement(kindknoten); //f�gt den neuen Teilbaum in die Liste der Teilb�ume ein und aktualisiert H�he der Knoten im Tailbaum
        //Aktualisieren der "Size"-Eintr�ge oberhalb des gefundenen Teilbaums
        while (kindknoten.getVater()!=null){
            kindknoten=kindknoten.getVater();
            kindknoten.setSize(kindknoten.getSize()-size);
        }
        teilbaum.setNext(partitioning(wurzel, n+1));
        return teilbaum;
    }
    
    public static void baumumsortierung(Node wurzel){
        if (wurzel.getDegree()!=0){
            Node[] kindarray=new Node[wurzel.getDegree()];
            Node naechsterSohn=wurzel.getlinkesterSohn();
            for (int i=0; i<kindarray.length; i++){
                kindarray[i]=naechsterSohn;
                naechsterSohn=naechsterSohn.getrechterBruder();
            }
            for(int i=0; i<kindarray.length; i++){
                Node max=kindarray[i];
                int pos=i;
                for (int j=i+1; j<kindarray.length; j++){
                    if(max.getSize()<kindarray[j].getSize()){
                        max=kindarray[j];
                        pos=j;
                    }
                }
                kindarray[pos]=kindarray[i];
                kindarray[i]=max;
            }
            wurzel.setlinkesterSohn(kindarray[0]);
            for (int i=0; i<kindarray.length; i++){
                if (i<kindarray.length-1){
                    kindarray[i].setrechterBruder(kindarray[i+1]);
                } else{
                    kindarray[i].setrechterBruder(null);
                }
                baumumsortierung(kindarray[i]);
            }
        }
    }
    
    public static void listenausgabe(Liste<Node> wurzel){
       //// if (wurzel.getNummer() == 0)
            System.out.println("baum: " + wurzel.getNummer());
        baumausgabe(wurzel.getElement()); System.out.println();
        if(wurzel.getNext()!=null){
            listenausgabe(wurzel.getNext());
        }
    }
    
    public static void baumausgabe(Node wurzel){
       if (wurzel.getHoehe()==0)
            System.out.println("hoehe: " + wurzel.getHoehe() + ": " + wurzel.getElement()+ " Vaterknoten: " + wurzel.getVater().getElement() +" Size: "+ wurzel.getSize()+ " Degree: "+ wurzel.getDegree());
        Node naechsterSohn=wurzel.getlinkesterSohn();
        while (naechsterSohn!=null){
            baumausgabe(naechsterSohn);
            naechsterSohn=naechsterSohn.getrechterBruder();
        }
    }
    
    public static Node baum(int[] barray, String[] narray){
        return baum(barray, narray, new Node<String>(0,"0"));
    }
    
    public static Node baum(int[] barray, String[] narray, Node<String> node){//gibt jeweils den linkesten Teilbaum des Wurzelknoten node zur�ck
        if (node.getIndex()==0){
            node.setHoehe(-1);
        }
        Node[] soehne = findChildren(barray,narray, node.getIndex()); //legt ein Array mit allen Sohnknoten an
        int size=1;
        for (int i=0; i<soehne.length; i++){
            if (i<soehne.length-1){
                soehne[i].setrechterBruder(soehne[i+1]); //l�sst jeden Knoten auf seinen rechten Bruder zeigen
            }
            soehne[i].setVater(node); //speichert Vaterknoten
            soehne[i].setHoehe(node.getHoehe()+1); //errechnet die H�he der Kinderknoten aus H�he des Vaterknotens+1
            Node teilbaum=baum(barray, narray, soehne[i]); //bestimmt f�r jedes Kind wiederum den linkesten Teilbaum und die Verkettung der Geschwister dieses Teilbaums
            soehne[i].setlinkesterSohn(teilbaum);
            size+=soehne[i].getSize();
        }
        node.setDegree(soehne.length);
        node.setSize(size);
        if (soehne.length>0) {
            return soehne[0]; //wenn der aktuelle Wuerzelknoten Kinder hat, wird der linkeste Teilbaum des Wurzelknotens zur�ckgegeben
        }
        return null; //ansonsten wird null zur�ckgegeben
    }
    
    public static Node[] findChildren(int[] b, String n[], int index){
        int sohnanzahl=0;
        for (int i=1; i<b.length; i++){
            if (b[i]==index){
                sohnanzahl=sohnanzahl+1;//z�hlt alle Array-Eintr�ge, die den gegeben Index als Vater haben
            }
        }
        Node[] kinderarray=new Node[sohnanzahl];
        sohnanzahl=0;
        for (int i=1; i<b.length; i++){
            if (b[i]==index){
                kinderarray[sohnanzahl]= new Node<String>(i,n[i]);//legt die Kinderknoten an und speichert sie im Array
                sohnanzahl=sohnanzahl+1;
            }
        }
        return kinderarray;
    }
    
    public static int anz(String datei){
        int anzahl=0;
        int h=0;
        try{
            //bestimmt die hru/reach-Zahlen;
            Reader reader = new FileReader(datei);
            
            while (h!=-1){
                h=reader.read(); //erstes Zeichen der Zeile
                StringBuffer zeile=new StringBuffer();
                while(h!=10&&h!=-1){
                    zeile.append((char)h);
                    h=reader.read();
                }//liest eine Zeile ein
                anzahl+=1;
                if (zeile.indexOf("#end")>-1){
                    //Verhindern des Weiterz�hlens bei CR/LF nach dem #end
                    h=-1;
                    anzahl-=1;
                }
            }
//            while(h!=-1){
//                h=reader.read(); //liest alle Zeichen nacheinander ein
//                if (h==10) {
//                    anzahl+=1; //bei Zeilenumbruch wird die hru/rech-Zahl um eins erh�ht
//                }
//            }
            anzahl-=5; //zieht die ersten "unwichtigen" Zeilen ab
        } catch(IOException e) {
            System.out.println( "Fehler beim Lesen der Datei" );
        }
        return anzahl;
    }
    
    public static void bindArray(String[] namensarray,String[] toFlowArray,int[] baumarray){
       String nameToSearch;
       int toIndex,pos;
       boolean found;
       namensarray[0]="";
       String stringElement;
       for (int i=0;i<namensarray.length;i++){
           if (namensarray[i]==null){
               System.out.println(i);
           }
       }
     // Arrays.sort(namensarray);
       int all = 0;
       for ( int i = 1;i<toFlowArray.length;i++ ){

           nameToSearch  =  toFlowArray[i];
           if (nameToSearch != null){
           
           //toIndex wird als substring vom Schl�ssel ausgelesen
           found=false;
           toIndex=1;
           while (toIndex<namensarray.length && !found){
               stringElement=(String)namensarray[toIndex];
               if (stringElement.equals(nameToSearch))found = true;
               else  toIndex+=1;
           }
          
           //toIndex = Arrays.binarySearch(namensarray,nameToSearch);
//           pos = nameToSearch.indexOf("hru");
//           if (pos<0)pos=nameToSearch.indexOf("reach");
//           toIndex=Integer.parseInt(nameToSearch.substring(0,pos));
           if (toIndex<namensarray.length) 
               baumarray[i]=toIndex;
           else 
               baumarray[i]=0;
           }
           all+=1;
       }
System.out.println("all:"+all);
  
           
    }
    
      public static void fillArray(String datei, String typ,int arrayStartPos, String[] namensarray,String[] toFlowArray){
        int zeilennummer=0;
        StringBuffer zeile=null;
          try{
          
            int erstezeile=headerLines+1;
            int tabulatorPos = 0;
            FileReader reader = new FileReader(datei);
            int h=0;
           
            int index=arrayStartPos;
            boolean noParaEnd=true;
            String flowInType=typ;
            while(h!=-1){
                h=reader.read(); //erstes Zeichen der Zeile
                zeile=new StringBuffer();
                while(h!=10&&h!=-1){
                    zeile.append((char)h);
                    h=reader.read();
                }//liest eine Zeile ein
                zeilennummer+=1;
                if (zeile.indexOf("#end")>=0) noParaEnd = false; 
                if (zeilennummer>=erstezeile&&noParaEnd){ //nur "relevante" Zeilen werden betrachtet
                    
                    StringTokenizer tokenizer = new StringTokenizer(zeile.toString());
                    //int indexalt=Integer.parseInt(tokenizer.nextToken()); //liest hru/reach-Nummer ein
                    int id=Math.round(Float.parseFloat(tokenizer.nextToken())); //liest hru/reach-Nummer ein
                    
                  //hru-Eintr�ge und reach-Eintr�ge werden fortlaufend durchnummeriert
                    namensarray[index]=(id+typ); // den durchnummerierten Indizes werden die alten Namen zugeordnet
                  
                    String s="";
                    //bestimmt Gebiet, in das das im Index gespeicherte Gebiet abflie�t, s enth�lt nach
                    //dem Durchlauf den Wert (HRU oder Abfluss)
                    if (typ.equals("reach")){
                        tabulatorPos = reachToReachPos;
                    }
                     if (typ.equals("hru")){
                        tabulatorPos = hruToPolyPos;
                    }
                    for (int i=1; i<tabulatorPos; i++){
                        s=tokenizer.nextToken("	 ");
                    }
                    int tohru=Math.round(Float.parseFloat(s));
                    flowInType=typ;
                    if (typ.equals("hru")&&tohru==0){ //wenn hru ind reach abflie�t
                        tohru=Math.round(Float.parseFloat(tokenizer.nextToken()));
                        flowInType="reach";
                        //tohru=Integer.parseInt(tokenizer.nextToken());
                        //tohru=tohru/100+anzahl;
                    }
                   
                    toFlowArray[index]=new String(tohru+flowInType);
                    index+=1;               }
            }
            //
        } catch( IOException e ) {
            System.out.println( "Fehler beim Lesen der Datei" );
            
        }
        catch(NumberFormatException nfe){
            System.out.println( "Fehler beim Einlesen der Datei "+datei+". Zeilennummer:"+zeilennummer+", Zeile:"+zeile );
                
            }
    }
    
}

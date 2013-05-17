/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proj;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.Jsoup;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author raj
 */
public class Proj {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        List<String> dis = new ArrayList<String>();
        Document doc = Jsoup.connect("http://www.healthline.com/symptomsearch").get();
        //Elements hyper=doc.getElementsByClass("box-directory-list");
        Elements commondis= doc.select("a[href]");
        for(Element link: commondis)
        {   int i=link.attr("href").indexOf("symptom.healthline.com");
            if(i!= -1 )
            {
            String linkstr = link.attr("href");
          // System.out.println(linkstr); 
            dis.add(linkstr.toString());
            }
        }
        getsymptoms(dis);
    }
    
    public  static void getsymptoms (List<String> a) throws Exception
    {int i=0;
        Iterator it=a.iterator();
       while(it.hasNext() && i++!=2)
        { 
            String str1=(String)it.next();
            //System.out.println(str1);
            /*URL u = new URL (str1 ); 
   HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection (); 
   huc.setRequestMethod ("GET"); 
   huc.connect () ; 
  // OutputStream os = huc.getOutputStream (  ) ; 
   System.out.println(huc.getResponseCode()); 
  if(huc.getResponseCode()!=404 )
            {        //System.out.println(str1); 
            * 
            * */
            String str2;
            str2=str1.replaceAll(" ","%20");
               //System.out.println(str2); 
                //Socket s = new Socket();
     // System.out.println(s.getSoTimeout());
               
                 Document doc = Jsoup.connect(str2).timeout(0).get();
                 findsym(doc);
                 Elements pages= doc.select("div.pagingdefault");
                 Element pages1=pages.get(0);
                  Element nextpage;
                 for(int  j=0;j<5 && j<pages1.select("a").size();j++)
                 {nextpage= pages1.select("a").get(j);
                    doc = Jsoup.connect(nextpage.attr("href")).timeout(0).get();
                 findsym(doc);
                  System.out.println(nextpage); 
                 }
                 
                 
                
        }
    }
    
    public static void findsym(Document d) throws Exception
    {
                 int n;
                 String symstr="";
           Elements item= d.getElementsByClass("resultitem");
              for(Element res:item)
                 {  //System.out.println(res); 
                    Elements item1=res.getElementsByClass("articletitle");
                    System.out.println(item1.text().substring(3)); 
                      Elements item2= res.getElementsByClass("resultsymptoms");
                     Elements item3= item2.select("li");
                 n=0;
                     for(Element sym:item3)
                     {  
                           if(n++==0)
                           symstr= sym.text();
                           else 
                           {symstr=symstr+"--"+sym.text(); 
                           n++;
                           }
                             
                                 
            
                     }
                      System.out.println(symstr); 
                       insertdb(item1.text().substring(3),symstr);
                 }   
              
            
        }
    public static Connection conn = null;
     public static String TABLE = "disym";
    public static void insertdb(String disease,String symptom) throws Exception
    {
           
            String url = "jdbc:mysql://localhost:3306";
             String dbName = "/crawl";
             String driver = "com.mysql.jdbc.Driver";
              String userName = "root";
             String password = "";
           Class.forName(driver).newInstance();
    conn = DriverManager.getConnection(url+dbName,userName,password);
    
    if(disease.startsWith(" "))
    { System.out.println("hahahahahahahah"); 
      disease= disease.replaceFirst(" ", "");
    }
          String insertString = "INSERT INTO " + TABLE + " VALUES (\""+disease+"\",\""+symptom+"\")";
  System.out.println(insertString);
  
    Statement stmt = conn.createStatement();
    stmt.executeUpdate(insertString);
    stmt.close();
  } 
  }
           
  
    
  

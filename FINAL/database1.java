import java.io.File;
import java.io.IOException;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.FSDataInputStream;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.Path;
import java.awt.List; 
import java.io.BufferedReader; 
import java.io.BufferedWriter; 
import java.io.DataInputStream;
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.InputStreamReader;

import java.sql.Timestamp;
import java.util.ArrayList; 
import java.util.Dictionary; 
import java.util.Enumeration; 
import java.util.HashSet; 
import java.util.Hashtable; 
import java.util.Iterator; 
import java.util.Scanner; 
import java.util.Set;

public class database1{
	String[] Filename={"follow.out","politics.out","sports.out","meme.out","music.out"};

	int[] freq;
    
    long start;
    long interv ;
    int N;

	public database1(String hashtag , int interval , Timestamp start_time , Timestamp end_time)
	{
		long ms1=end_time.getTime();
		long ms2=start_time.getTime();
		long diff1=ms1 - ms2;
		// long diff1=diff/1000;
		// long interval=window.getTime();
		interv = interval ;
		N =(int)( diff1/interval )+1;
		// long freq[N];
		start = ms2;

		int i;
		long sts;
		long ets;
		Timestamp t;
		freq = new int[N];
		// for(i=0;i<N;i++)
		// {
		//   sts=ms2+(i*interval);
		//   ets=ms2+(i*interval)+interval;
		//   Timestamp t1 = new Timestamp(sts);
		//   Timestamp t2 = new Timestamp(ets);
		//   freq[i] = getFreq(hashtag , t1 , t2);
		// }
		for(i=0;i< N;i++)
		{
		freq[i]=0;
		}
		getFreq(hashtag);

		for(i=1;i< N;i++)
		{
		freq[i]=freq[i] + freq[i-1];
		}
	}
	public int month(String mon)
	{
		int mm=0;
		switch(mon){
			case "Jan": mm=1;
			         break;
			case "Feb": mm=2;
			         break;
			case "Mar": mm=3;
			         break;
			case "Apr": mm=4;
			         break;
			case "May": mm=5;
			         break;
			case "Jun": mm=6;
			         break;
			case "Jul": mm=7;
			         break;
			case "Aug": mm=8;
			         break;
			case "Sep": mm=9;
			         break;
			case "Oct": mm=10;
			         break;
			case "Nov": mm=11;
			         break;
			case "Dec": mm=12;
			         break;
		}
		return mm;
	}

public void getFreq(String hashtag){

    //returns number of tweets with given hashtag within specified time  
    long count=0;
    int j=0;
    for(j=0;j<5;j++){
		try{
			// Open the file that is the first 
			// command line parameter
		  	FileInputStream fstream = new FileInputStream(Filename[j]);
		 	// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i=0;
			String time="";
			int mm;
			//Read File Line By Line
			long count_ = 0;
			while ((strLine = br.readLine()) != null)   {
			 	// Print the content on the console
			 	//System.out.println (strLine);
			 	// ArrayList <String> list= new Arraylist<String>();
			    //       Scanner sc=new Scanner(strLine);
			    //       while(sc.hasNext())
			    //       {
			    //       list.add(sc.next)
			 	String [] list = strLine.split(" ");
			 	//System.out.println(list[7]);
			 	// str.contains("world")
			 	
			 	if(hashtag.contains(list[7])||list[7].contains(hashtag))
			 	{
				    System.out.println("list[7] = "+list[7]+" hashtag = "+hashtag+"FILE = "+Filename[j]);
				    time="";
			        time=time+list[6]+"-";
			        mm=month(list[2]);
			        time=time+mm+"-"+list[3]+" "+list[4];
			        Timestamp timestamp=Timestamp.valueOf(time);
			        //System.out.println(timestamp.getTime());
			        long t = timestamp.getTime();
			        int i1 = (int)(( t- start ) / interv) ;
			        if(i1 < 0) i1 = 0;
			        if(i1 >= N) i1 = N-1;
			        freq[i1]++;
			        count_++;
			 	} 	
			}
			System.out.println("COUNT = " + count_);
		 	
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}

	public long getFreq_final(Timestamp start_time , Timestamp end_time)
	{
		long sta = start_time.getTime();
		long ena = end_time.getTime();
		// System.out.println("STA = "+sta+" ENA = "+ena+" START = "+start);
		int st = (int)(( sta - start ) / interv) ;
		int en = (int)(( ena - start )  / interv );
		// System.out.println("ST = "+st+" EN = "+en+" N = "+N);
		long ret = ( freq[en] - freq[st] ) ;
		return ret ;
	}

}
import java.util.* ;
import java.io.* ;
import java.sql.*;
import java.lang.*;

class trend{	

	long timeDifference(Timestamp t1, Timestamp t2){
		long startMilliseconds = t1.getTime();
		long endMilliseconds = t2.getTime();
		return (endMilliseconds - startMilliseconds);
	}
	void interpolate(long a[],int smallSize,int largeSize){
		long []A = new long[smallSize];
		double ratio = largeSize/smallSize ;
		for(int i=0 ;i<smallSize; i++){
			A[i] = -1;
		}
		for(int i=0;i<largeSize;i++){
			int index = (int)(i*(smallSize/largeSize));
			if(index >= smallSize){
				index = smallSize - 1;
			}
			A[index] = a[i]; 
		}
		A[smallSize - 1] = a[largeSize - 1];
		for(int i=0;i<smallSize;i++){
			int nextPositive;
			if(A[i] == -1){
				for(nextPositive =i+1;nextPositive<smallSize;nextPositive++ ){
					if(A[nextPositive] != -1){
						break;
					}
				}
				double slope = (A[nextPositive] - A[i-1])/(nextPositive-i+2);
				for(int j = i;j<nextPositive;j++){
					A[j] = A[i-1] +(long)( slope) * (j-i+1);
				}
				i = nextPositive;
			}
		}
		for(int i=0;i<smallSize;i++){
			a[i] = A[i];
		}
		return;
	}
	void getMomentum(String hashtag, int S, int L, Timestamp start, Timestamp end,database obj){

		Timestamp step = new Timestamp(0);

		long T = timeDifference(start,end);
		int n1,n2;
		int frame1,size;
		Timestamp start_frame = new Timestamp(0);
		Timestamp end_frame = new Timestamp(0);

		
		int largeSize,smallSize;
		largeSize = (int)(T/L);
		smallSize = (int)(T/S);

		// long []freq_shortwindow = new long[(int)(T/S)];
		// long []freq_longwindow = new long[(int)(T/L)];
		// double []MA_short = new double[(int)(T/S)];
		// double []MA_long = new double[(int)(T/L)];
		// double []Momentum = new double[(int)(T/L)];
		// double []Momentum_avg = new double[(int)(T/L)];
		

		long []freq_shortwindow = new long[smallSize];
		long []freq_longwindow = new long[smallSize];
		double []MA_short = new double[smallSize];
		double []MA_long = new double[smallSize];
		double []Momentum = new double[smallSize];
		double []Momentum_avg = new double[smallSize];

		double alpha = 0.01;

		database1 smallWindow = new database1(hashtag,S,start,end);
		database1 largeWindow = new database1(hashtag,L,start,end);

		System.out.println("FOR SMALL WINDOWS **************");
		long smallOccurence = 0;
		for(n1 = 0; n1 < smallSize; n1++){

			if(n1 < S){
				frame1 = 0;
				size = n1 + 1;
			}
			else{
				frame1 = n1 - S + 1;
				size = S;
				System.out.println("*********SHORT********");
			}
			start_frame.setTime(start.getTime() + (long)n1*(long)S);
			end_frame.setTime(start_frame.getTime() +(long)(S)); 
			// freq_shortwindow [n1] = obj.getFreq(hashtag, start_frame, end_frame);
			// System.out.println("START = "+start_frame+" END = "+end_frame);
			freq_shortwindow [n1] = smallWindow.getFreq_final(start_frame, end_frame);
			smallOccurence = smallOccurence + freq_shortwindow [n1];
			// System.out.println("START = "+start_frame+" END = "+end_frame+"FREQ = "+freq_shortwindow [n1]);
			// debug here check whether its the expected window or not --- works fine and retrieval is also ok
			MA_short[n1] = 0.0;
			for(;frame1 <= n1;frame1++){
				MA_short[n1] = MA_short[n1] + freq_shortwindow[frame1]; 
			}
			MA_short[n1] = MA_short[n1] / (double)(size);			
			// System.out.println("MASHORT["+n1+"] = "+MA_short[n1]);
		}
		System.out.println("FOR LARGE WINDOWS **************");
		long largeOccurence = 0;
		for(n2 = 0; n2 < largeSize; n2++){

			if(n2 < L){
				frame1 = 0;
				size = n2 + 1;
			}
			else{
				frame1 = n2 - L + 1;
				size = L;
				System.out.println("*********LONG********");
			}
			start_frame.setTime(start.getTime() + (long)n2*(long)L);
			end_frame.setTime(start.getTime() +(long)(n2+1)*(long)L);
			// debug here check whether its the expected window or not ---- works fine
			// freq_longwindow [n2] = obj.getFreq(hashtag, start_frame, end_frame);
			freq_longwindow [n2] = largeWindow.getFreq_final(start_frame, end_frame);
			largeOccurence = largeOccurence + freq_longwindow [n2];
			// System.out.println("START = "+start_frame+" END = "+end_frame+"FREQ = "+freq_longwindow [n2]);
			MA_long[n2] = 0.0;
			for(;frame1 <= n2;frame1++){
				MA_long[n2] = MA_long[n2] + freq_longwindow[frame1]; 
			}
			MA_long[n2] = MA_long[n2] / (double)(size);			
			// System.out.println("MALONG["+n2+"] = "+MA_long[n2]);
		}
		System.out.println("LARGE = "+largeOccurence+" SMALL = "+smallOccurence);
		interpolate(freq_longwindow,smallSize,largeSize);
		for(n2 =0;n2 < smallSize; n2++){
			if(MA_long[n2]<0.000000000001||MA_long[n2] == 0.0){
				continue;
			}else{
				break;
			}
		}
		try {
			File file = new File("output.txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			for(;n2 < smallSize; n2++){

				// Momentum[n2] = MA_short[n2] - Math.pow(MA_long[n2],alpha);
				Momentum[n2] = Math.pow(MA_long[n2],alpha) - MA_short[n2];
				for(frame1 = 0; frame1 <= n2;frame1++){
					Momentum_avg[n2] = Momentum_avg[n2] + Momentum[frame1]; // need to handle .... need to ensure
					// that they are non zero ... once they are non zero they will always be the same ... so start from where they are 
					// both non zero /.... remove the infinity/nan cases //TODO SIKHAR
				}
				Momentum_avg[n2] = Momentum_avg[n2] / (double)(n2);
				// System.out.println("MomentumAVG = " + Momentum_avg[n2]);
				output.write(n2 + " " + Momentum_avg[n2] + "\n");

			}
			output.close();
		} catch ( IOException e ) {
           e.printStackTrace();
        }
        return;
	}

	void getUserCorr(String hashtag, int numUsers, Timestamp start, Timestamp end,database obj){

		int i;
		double []corr = new double[numUsers];
		long A, B, C, D;
		double fact1, fact2, fact3;

		for(i = 0;i < numUsers;i++){

			A = obj.getuserinfo_A(hashtag, i, start, end);
			B = obj.getuserinfo_B(hashtag, i, start, end);
			C = obj.getuserinfo_C(hashtag, i, start, end);
			D = obj.getuserinfo_D(hashtag, i, start, end);

			// (A+B+C+D)(AD-BC)2/(A+B)(C+D)(A+C)(B+D)

			fact1 = A + B +C +D;
			fact2 = (A*D - B*C) * (A*D - B*C);
			fact3 = (A+B) * (B+C) * (C+D) * (D+A);

			corr[i] = (fact1)*(fact2)/(fact3);
		}
	}

	public trend(String hashtag, int numUsers, Timestamp start, Timestamp end, int S, int L){
		
		database obj = new database();
		getMomentum(hashtag,S,L,start,end,obj);
		// getUserCorr(hashtag,numUsers,start,end,obj);

	}
	public static void main(String []args){

		String hashtag = "SFS14";
		int numUsers = 100;
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);// I might have the wrong Calendar constant...
		cal.set(Calendar.MONTH, 2-1);// -1 as month is zero-based
		cal.set(Calendar.YEAR, 2014);
		Timestamp start = new Timestamp(cal.getTimeInMillis());
		// Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 31);// I might have the wrong Calendar constant...
		cal.set(Calendar.MONTH, 4-1);// -1 as month is zero-based
		cal.set(Calendar.YEAR, 2014);
		Timestamp end   = new Timestamp(cal.getTimeInMillis());
		// MAX INT 2147483647
		int S = 1000000;
		int L = 3000000;
		System.out.println(start);
		System.out.println(end);
		System.out.println(S);
		System.out.println(L);
		trend obj = new trend(hashtag,numUsers,start,end,S,L);
	}
}


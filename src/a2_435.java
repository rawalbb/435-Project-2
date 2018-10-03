import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class a2_435 
{
	private static DecimalFormat truncate = new DecimalFormat(".#####");
	private static DecimalFormat truncate2 = new DecimalFormat(".####");
	
	public static void main(String[] args) throws IOException 
	{
		System.out.println("HELLO");
		String [][] complete = new String[8796][14];
		String [][] missing01 = new String[8796][14];
		String [][] missing10 = new String[8796][14];
		readInFile(complete, "dataset_complete.csv");
		readInFile(missing01, "dataset_missing01.csv");
		System.out.println(missing01.length);
		//String [][] hotMean01 = hotDeckImp(missing01);
		///exportCSV(hotMean01, "HELLOO.csv");
		readInFile(missing10, "dataset_missing10.csv");
		String [][] mean01 = meanImp(missing01);
		String [][] mean10 = meanImp(missing10);
		String [][] condMean01 = condMeanImp(missing01);
		String [][] condMean10 = condMeanImp(missing10);
		String [][] hotMean01 = hotDeckImp(missing01);
		String [][] condHot01 = condHotDec(missing01);
		String [][] hotMean10 = hotDeckImp(missing10);
		String [][] condHot10 = condHotDec(missing10);
		exportCSV(mean01, "V00742469_missing01_imputed_mean.csv");
		exportCSV(mean10, "V00742469_missing10_imputed_mean.csv");
		exportCSV(condMean01, "V00742469_missing01_imputed_mean_conditional.csv");
		exportCSV(condMean10, "V00742469_missing10_imputed_mean_conditional.csv");
		exportCSV(hotMean01, "V00742469_missing01_imputed_hd.csv");
		exportCSV(hotMean10, "V00742469_missing10_imputed_hd.csv");
		exportCSV(condHot01, "V00742469_missing01_imputed_hd_conditional.csv");
		exportCSV(condHot10, "V00742469_missing10_imputed_hd_conditional.csv");
		String MAEmean01 = MAE(missing01, mean01, complete);
		System.out.println(MAEmean01);
		String MAEmean10 = MAE(missing10, mean10, complete);
		String MAEcondMean01 = MAE(missing01, condMean01, complete);
		String MAEcondMean10 = MAE(missing10,condMean10, complete);
		String MAEhotMean01 = MAE(missing01, hotMean01, complete);
		//System.out.println(MAEhotMean01);
		String MAEcondHot01 = MAE(missing01, condHot01, complete);
		String MAEhotMean10 = MAE(missing10, hotMean10, complete);
		String MAEcondHot10 = MAE(missing10, condHot10, complete);
		System.out.println("MAE VALUES:" + "\n" 
							+ "MAE_01_mean: " + MAEmean01 + "\n" 
							+ "MAE_01_mean_conditional: " + MAEcondMean01 + "\n" 
							+ "MAE_01_hd: " + MAEhotMean01 + "\n" 
							+ "MAE_01_hd_conditional: " + MAEcondHot01 + "\n" 
							+ "MAE_10_mean: " + MAEmean10 + "\n" 
							+ "MAE_10_mean_conditional: " + MAEcondMean10 + "'\n" 
							+ "MAE_10_hd: " + MAEhotMean10 + "\n"  
							+ "MAE_10_hd_conditional: " + MAEcondHot10);
	}
	
	public static void readInFile(String [][] matrix, String filename) throws IOException
	{
		File file=new File(filename);
    
		int row=0;
		int col=0;
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String line=null;
     
		while((line=reader.readLine())!=null)
		{
			StringTokenizer st=new StringTokenizer(line, ",");
			while(st.hasMoreTokens())
			{
	            matrix[row][col]=st.nextToken();
	            col++;
			}
	        col=0;
	        row++;
		}
	}
	public static String[][] meanImp(String[][] values) 
	{
		String [][] matrix = new String[8796][14];
		for (int y = 0; y < values.length; y++)
			    for (int x = 0; x < values[y].length; x++)
			        matrix[y][x] = values[y][x];

		double sum;
		int numValues, questions;
		//System.out.println(matrix[8095][0]);
		for (int x = 0; x<matrix[x].length-1; x++)
		{	
			sum = 0;
			numValues = 0;
			questions = 0;
			
			for (int i = 1 ; i< matrix.length; i++)
			{
				if (matrix[i][x].equals("?"))
				{
					questions++;
					//System.out.println(x + "," + i);
				}
				else
				{
					sum += Float.parseFloat(matrix[i][x]);
					numValues++;
				}
			}
			for (int i = 1 ; i< matrix.length; i++)
			{
				if (matrix[i][x].equals("?"))
				{
					matrix[i][x] = truncate.format(sum/numValues);
					//Double.toString(sum/numValues);
				}
			}
			//System.out.println(sum + " " + numValues + " " + (sum/numValues)); 
			//System.out.println(questions);
		}
		return matrix;
	}

	public static String[][] condMeanImp(String[][] values) 
	{
		String [][] matrix = new String[8796][14];
		for (int y = 0; y < values.length; y++)
			    for (int x = 0; x < values[y].length; x++)
			        matrix[y][x] = values[y][x];
		double nSum;
		double ySum;
		int numN, numY, questions;
		System.out.println("Algorithm 2");
		for (int x = 0; x<matrix[x].length-1; x++)
		{	
			nSum = 0;
			ySum = 0;
			numN = 0;
			numY = 0;
			questions = 0;
			
			for (int i = 1 ; i< matrix.length; i++)
			{
				if (matrix[i][x].equals("?"))
				{
					questions++;
					//System.out.println(x + "," + i);
				}
				else if (!matrix[i][x].equals("?") && matrix[i][13].equals("N"))
				{
					nSum += Float.parseFloat(matrix[i][x]);
					numN++;
				}
				else if(!matrix[i][x].equals("?") && matrix[i][13].equals("Y"))
				{
					ySum += Float.parseFloat(matrix[i][x]);
					numY++;
				}
			}
			for (int i = 1 ; i< matrix.length; i++)
			{
				if (matrix[i][x].equals("?") && matrix[i][13].equals("N"))
				{
					matrix[i][x] = truncate.format(nSum/numN);
							//Double.toString(nSum/numN);
					System.out.println(i + ", " + x);
					System.out.println(nSum/numN);
				}
				if(matrix[i][x].equals("?") && matrix[i][13].equals("Y"))
				{
					matrix[i][x] = truncate.format(ySum/numY);
							//Double.toString(ySum/numY);
					System.out.println("YYYY" + ySum/numY);
				}
				else
				{
					System.out.print("");
				}
			}
		}
	
		return matrix;
	}
	
	public static String[][] hotDeckImp(String[][] values) 
	{
		System.out.println("IN HOT DECK");
		String [][] matrix = new String[8796][14];
		String [][] newmatrix = new String[8796][14];
		for (int y = 0; y < values.length; y++) {
		    for (int x = 0; x < values[y].length; x++) {
		        matrix[y][x] = values[y][x];
				newmatrix[y][x] = values[y][x];}}
		
		
		for(int row1 = 1; row1<8796; row1++) {
			HashMap<Double, Integer> deckmap = new HashMap<>();
				for(int row2 = 1; row2<8796; row2++) {
					double sumDiff = 0;
						for(int col2 = 0; col2<13; col2++)
						{
							if (row1 == row2)
							{
								sumDiff = 0;
							}
							else if((matrix[row1][col2].equals("?")) || matrix[row2][col2].equals("?"))
							{
								
								sumDiff += 1;
							}
							else
							{
								//System.out.println("SumDiff " + ((Double.parseDouble(matrix[row1][col2]) - Double.parseDouble(matrix[row2][col2]))));
								sumDiff += Math.pow(Double.parseDouble(matrix[row1][col2]) - Double.parseDouble(matrix[row2][col2]), 2);
								
							}
							
						}
						//System.out.println("SumDiff  "+ sumDiff);
					double square = Math.sqrt(sumDiff);
					//System.out.println(row1 + "," + row2 + "  " + square);
					deckmap.put(square, row2);
				}
				Map<Double, Integer> treeMap = new TreeMap<Double, Integer>(deckmap);
				Iterator it = treeMap.entrySet().iterator();
				for (int col1 = 0; col1 < 13; col1++) {
				while (it.hasNext() && (newmatrix[row1][col1].equals("?"))){
					
					
			    	if(!newmatrix[row1][col1].equals("?")) {
			    		System.out.println();
			    	}
			        Map.Entry pair = (Map.Entry)it.next(); 
			        if(!matrix[(int) pair.getValue()][col1].equals("?") && !((double)pair.getKey()== 0.0) && (newmatrix[row1][col1].equals("?")))
			        {
			        	double value = Double.parseDouble(matrix[(int) pair.getValue()][col1]);
			        	//newmatrix[row1][col1] = matrix[(int) pair.getValue()][col1];
			        	newmatrix[row1][col1] = truncate.format(value);
			        	//newmatrix[row1][col1] = truncate.format(nSum/numN);
			        	//System.out.println("MATCH:   " + row1 + ", " + col1 + "   " + newmatrix[row1][col1]); 
			        	//break;
			        	//counter = 1;
			        }
				}
			    //System.out.println("NO:  " + row1 + ", " + col1 + "   " + newmatrix[row1][col1]);
		
		}}
		return newmatrix;
	}

	
	public static String[][] condHotDec(String[][] values) 
	{
		System.out.println("IN Conditional Hot DECK");
		String [][] matrix = new String[8796][14];
		String [][] newmatrix = new String[8796][14];
		for (int y = 0; y < values.length; y++) {
		    for (int x = 0; x < values[y].length; x++) {
		        matrix[y][x] = values[y][x];
				newmatrix[y][x] = values[y][x];}}
		
		
		for(int row1 = 1; row1<8796; row1++) {
			HashMap<Double, Integer> deckmap = new HashMap<>();
				for(int row2 = 1; row2<8796; row2++) {
					double sumDiff = 0;
						for(int col2 = 0; col2<13; col2++)
						{
							//System.out.println(row1 + "," + col1 + "   COLII   " + row2 + "," +col2);
							//System.out.println(matrix[row2][col2]);
							//System.out.println(matrix[row1][col1].equals("?"));
							if (row1 == row2)
							{
								sumDiff = 0;
							}
							else if((matrix[row1][col2].equals("?")) || matrix[row2][col2].equals("?"))
							{
								
								sumDiff += 1;
							}
							else
							{
								//System.out.println("SumDiff " + ((Double.parseDouble(matrix[row1][col2]) - Double.parseDouble(matrix[row2][col2]))));
								sumDiff += Math.pow(Double.parseDouble(matrix[row1][col2]) - Double.parseDouble(matrix[row2][col2]), 2);
								
							}
							
						}
						//System.out.println("SumDiff  "+ sumDiff);
					double square = Math.sqrt(sumDiff);
					//System.out.println(row1 + "," + row2 + "  " + square);
					if(matrix[row1][13].equals(matrix[row2][13]))
					{
//						System.out.println(matrix[row1][13] + " .... " + matrix[row2][13])
					deckmap.put(square, row2);
					//System.out.println(deckmap.size());
					}

				}
				//System.out.println(deckmap.size());
				Map<Double, Integer> treeMap = new TreeMap<Double, Integer>(deckmap);
				Iterator it = treeMap.entrySet().iterator();
				for (int col1 = 0; col1 < 13; col1++) {
				while (it.hasNext() && (newmatrix[row1][col1].equals("?"))){
					
					
			    	if(!newmatrix[row1][col1].equals("?")) {
			    		System.out.println();
			    	}
			        Map.Entry pair = (Map.Entry)it.next(); 
			        if(!matrix[(int) pair.getValue()][col1].equals("?") && !((double)pair.getKey()== 0.0) && (newmatrix[row1][col1].equals("?")))
			        {
			        	//newmatrix[row1][col1] = matrix[(int) pair.getValue()][col1];
			        	double value = Double.parseDouble(matrix[(int) pair.getValue()][col1]);
			        	//newmatrix[row1][col1] = matrix[(int) pair.getValue()][col1];
			        	newmatrix[row1][col1] = truncate.format(value);
			        	
			        	//System.out.println("MATCH:   " + row1 + ", " + col1 + "   " + newmatrix[row1][col1]); 
			        	//break;
			        	//counter = 1;
			        }
				}
			    //System.out.println("NO:  " + row1 + ", " + col1 + "   " + newmatrix[row1][col1]);
		
		}}
		return newmatrix;
	}
	
	
	
	
	
	public static String MAE(String[][] missing, String [][] imputed, String[][] complete)
	{
		int numMissing = 0;
		double sum = 0;
		for(int j = 0; j<missing.length; j++)
		{
		for(int i = 0; i<missing[0].length-1; i++)
		{
			if (missing[j][i].equals("?"))
			{
				numMissing++;
				//System.out.println(numMissing + "   " + (Math.abs(Double.parseDouble(imputed[j][i]) - Double.parseDouble(complete[j][i]))) );
				sum += Math.abs(Double.parseDouble(imputed[j][i]) - Double.parseDouble(complete[j][i]));
			}
		}
		
		}
		System.out.println("LAST " + sum);
		return (truncate2.format(sum/numMissing));
		
	}
	public static void exportCSV(String [][] matrix, String filename) throws IOException
	{
		BufferedWriter b = new BufferedWriter(new FileWriter(filename));
		StringBuilder s = new StringBuilder();
		
		for(int i = 0; i<matrix.length; i++) 
		{
			for (int j = 0; j<matrix[j].length; j++)
			{
				s.append(matrix[i][j]);
				s.append(",");
			}
			s.append("\n");
	}
		b.write(s.toString());
		b.close();

	
	}
}



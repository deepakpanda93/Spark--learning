package com.packt.sfjd.ch5;



import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
//29,"City of Lost Children, The (Cité des enfants perdus, La) (1995)",Adventure|Drama|Fantasy|Mystery|Sci-Fi
//40,"Cry, the Beloved Country (1995)",Drama
//
public class CSVFileOperations {

	public static void main(String[] args) {
		  System.setProperty("hadoop.home.dir", "E:\\sumitK\\Hadoop");
			
	      SparkSession sparkSession = SparkSession
	      .builder()
	      .master("local")
		  .config("spark.sql.warehouse.dir","file:///E:/sumitK/Hadoop/warehouse")
	      .appName("JavaALSExample")
	      .getOrCreate();
	      Logger rootLogger = LogManager.getRootLogger();
			rootLogger.setLevel(Level.WARN); 

	    JavaRDD<Movies> moviesRDD = sparkSession
	      .read().textFile("C:/Users/sumit.kumar/git/learning/src/main/resources/movies.csv")
	      .javaRDD().filter( str-> !(null==str))
	      .filter(str-> !(str.length()==0))
	      .filter(str-> !str.contains("movieId"))	      
	      .map(new Function<String, Movies>() {
			private static final long serialVersionUID = 1L;
			public Movies call(String str) {		        	
	          return Movies.parseRating(str);
	        }
	      });
	    
	    moviesRDD.foreach(new VoidFunction<Movies>() {			
			@Override
			public void call(Movies t) throws Exception {
				System.out.println(t);				
			}
		});
	    
	       Dataset<Row> csv_read = sparkSession.read().format("com.databricks.spark.csv")
		        		      .option("header", "true")
		        		      .option("inferSchema", "true")
		        		      .load("C:/Users/sumit.kumar/git/learning/src/main/resources/movies.csv");
		       
		       csv_read.printSchema();
		       
		       csv_read.show();
		       
		       
		       StructType customSchema = new StructType(new StructField[] {
		    		    new StructField("movieId", DataTypes.LongType, true, Metadata.empty()),
		    		    new StructField("title", DataTypes.StringType, true, Metadata.empty()),
		    		    new StructField("genres", DataTypes.StringType, true, Metadata.empty())
		    		});
   
		       Dataset<Row> csv_custom_read = sparkSession.read().format("com.databricks.spark.csv")
	        		      .option("header", "true")
	        		      .schema(customSchema)
	        		      .load("C:/Users/sumit.kumar/git/learning/src/main/resources/movies.csv");
	       
		       csv_custom_read.printSchema();
	       
		       csv_custom_read.show(); 
		       
		       
		       csv_custom_read.write()
		       .format("com.databricks.spark.csv")
		       .option("header", "true")
		       .option("codec", "org.apache.hadoop.io.compress.GzipCodec")
		       .save("C:/Users/sumit.kumar/git/learning/src/main/resources/newMovies.csv");
		       
	}

}

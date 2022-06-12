import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object InvertedIndex {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("bingbing").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)
	//调用Spark读取文件API,读取文件内容
    val wordRDD= sc.textFile("D:\\data\\index.txt")
    //使用flatMap进行分词后展开
      .flatMap {
        line =>
          val array = line.split("\\.", 2)
          val bookName = array(0)
          array(1).split("\"")(1).split(" ").map(word => (bookName, word))
      }

    val kvRDD= wordRDD.map(kv => (kv._2, kv._1)).map((_, 1))
      .reduceByKey((x,y) => x + y)
      .map{case ((k,v),cnt) => (k,(v,cnt))}
      .groupByKey() //只分组不聚合
      .collect()
      .foreach(println)
  }
}

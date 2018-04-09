package calDocumentSimilarity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.io.File;  
import java.io.FileWriter;  
import java.io.Writer;  
public class calSimilarity {
        private static int len=0;   //文档数量
        private static HashMap<String, Float> []tfidf; //词频*逆文档频率	 
    	private static int leng[];	//每个文档长度的集合
    	private static float  modulus[]; //向量的模
        private static ArrayList<String[]> doc=new ArrayList<String[]>(); //文档集合
        public static void readFile(String path) throws IOException{ //读取文档
        	long a = System.currentTimeMillis();
    		FileReader reader = new FileReader("E:/questions_cut.csv");
    		BufferedReader br = new BufferedReader(reader);
    		String str = null,strdoc = "";
    		while((str=br.readLine())!=null){ //按行读取，已字符串数组的形式存入doc
    			doc.add(str.replace("'", "").replace(" ","").replace("\"[","").replace("]\"","").split(","));
    			++len;
    		}
    		br.close();
    		reader.close();
    		System.out.println(len);
    		long b=System.currentTimeMillis();
    		System.out.println("读取文件时间"+(b-a)+" ms");
    		
        }
        /**
         * td：词频（该词在所属文档中出现的次数）
         * idf：逆文档词频（该词在所有文档中出现的次数）
         * 
         */
        public static void calTfidf(){ //计算tfidf
            long a = System.currentTimeMillis(); 
        	HashMap<String, Float> []tmp_tfidf = new HashMap[len];  
    		int size;
    		int n = 0;
    		String str;
    		HashMap<String,Integer> idf = new HashMap<String, Integer>();
    		for(String d[]:doc){
    			size = d.length;
    			leng[n] = size;	
    			HashMap<String, Float> td = new HashMap<String, Float>();
    			for(int i = 0; i < size; ++i){
    				str = d[i];
    				if(!td.containsKey(str)){   //如果td中不存在该词，说明该词第一次出现，令该词的td的value为1，并存入td
    					td.put(str, (float) 1.0);
    					if(idf.containsKey(str))  //idf中如果出现该词，则总文档中该词词频+1
    						idf.put(str, idf.get(str)+1);
    					else
    						idf.put(str, (int) 1);//否则，说明该词在总文档中第一次出现，令value为1，并存入idf
    				}
    				else
    					td.put(str, td.get(str)+1);  //如果td中有该词，说明不是第一次出现，词频加1
    			}
    			
    			tmp_tfidf[n++] = td; //将该文档的词频存入所有文档的词频数组中
    			
    		}
    		float ft,sum_tdidf[]=new float[len];
    		for(int i=0;i<len;++i){
    			for(String st:tmp_tfidf[i].keySet()){ //遍历词频数组
    				ft=(float)((tmp_tfidf[i].get(st)/leng[i])*Math.log(len/(idf.get(st)+1)));//计算文档的逆文档频率
    				sum_tdidf[i]+=ft;
    				tmp_tfidf[i].put(st, ft);
    			}
    		}
    		System.out.println(tmp_tfidf[0]);
    		float ftmp;
    		for(int i = 0; i < len; ++i){
    			HashMap<String, Float> td = new HashMap<String, Float>();
    			
    			ValueComparator bvc =  new ValueComparator(tmp_tfidf[i]);  //对逆文档频率进行排序
    	        TreeMap<String,Float> sorted_map = new TreeMap<String,Float>(bvc);  
    	        sorted_map.putAll(tmp_tfidf[i]);
    	        ft = 0;

    	        for(String key:sorted_map.keySet()){
        			ftmp = tmp_tfidf[i].get(key);
        			ft += ftmp;
    	        	if(ft/sum_tdidf[i] > 0.99)
    	        		break;
    	        	td.put(key, ftmp);
    	        }
    	        tfidf[i] = td;   //将计算出的tfidf存入数组
    		}
    		
        }
    	public static void calFilm(){
    		for(int i=0;i<len;++i){
    			 modulus[i]=(float)0.0;  
    			for(String str:tfidf[i].keySet()){
    				 modulus[i]+=tfidf[i].get(str)*tfidf[i].get(str); //计算出每个文档的模
    				
    			}
    			 modulus[i]=(float)Math.sqrt(modulus[i]); //将结果开方得到模
    			
    		}
    	}
    	public static float cosine(int i,int j){
    		int x=i,y=j;
    		float ans=0;
    		if(tfidf[i].size()>tfidf[j].size()){
    			x=j;
    			y=i;
    		}
    		for(String str:tfidf[x].keySet()){
    			if(tfidf[y].containsKey(str))
    			{ ans+=tfidf[x].get(str)*tfidf[y].get(str);  
    			   }
    		}
    		
    		return ans/(modulus[x]*modulus[y]);  //利用余弦公式计算余弦
    		
    	}
        /**
         * 利用余弦值为相似度得到与每一个文档最相似的文档
         * 在进行存储时对相似度进行了排序选出了相似度最高的十个数据并选择存储这些文档在源文档中的位置
         * 
         * @throws IOException
         */
    	 
    	public static void calSimilarity() throws IOException{
    		FileWriter fw = new FileWriter("2.csv");
    		HashMap<Integer, Float> []res = new HashMap[len];
    		for(int i = 0; i < len; i++){
    			HashMap<Integer,Float>tt=new HashMap<Integer,Float>();
    			for(String[] str:doc ){
    				tt.put(doc.indexOf(str),cosine(i,doc.indexOf(str)));
    			}
    			List<Map.Entry<Integer,Float>>list = new ArrayList<Map.Entry<Integer,Float>>(tt.entrySet());
    	        Collections.sort(list,new Comparator<Map.Entry<Integer,Float>>() {
    	            //升序排序
    	            public int compare(Entry<Integer, Float> o1,
    	                    Entry<Integer, Float> o2) {
    	                return o2.getValue().compareTo(o1.getValue());
    	            }
    	            
    	        });
    	        HashMap<Integer,Float>tt1=new HashMap<Integer ,Float>();
    	        
    	        for (int j=0;j<10;++j) {  
    	           tt1.put(list.get(j).getKey(), list.get(j).getValue());
    	           fw.write(list.get(j).getKey()+",");
    	        }
    	        res[i]=tt1;
    	        fw.write("\n");
    		    System.out.println("running");
    		}
    		fw.close();
     }  
    		
    		
    	
    	
        public static void main(String[] args) throws IOException{
        	long a = System.currentTimeMillis();
        	String path="E:/2.txt";
        	readFile(path);
        	tfidf =  new HashMap[len];
    		leng = new int[len];
    		modulus = new float[len];
        	calTfidf();
        	calFilm();
    		calSimilarity();
    		long b=System.currentTimeMillis();
    		System.out.println("总运行时间"+(b-a)+" ms");
        }
       static class  ValueComparator implements Comparator<String> {  
      	  
            Map<String, Float> base;  
            public ValueComparator(Map<String, Float> base) {  
                this.base = base;  
            }  
          
           
            public int compare(String a, String b) {  
                if (base.get(a) >= base.get(b)) {  
                    return -1;  
                } else {  
                    return 1;  
                } 
            }  
        } 

 
}

package com.company.product.project.examples;

import com.company.product.project.model.CpdSearchModelFacade;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.company.product.project.model.DNNModelImp;
import com.company.product.project.model.LogisticRegressionImp;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by luojiahua on 2017/01/09.
 */
public class DNNExample {

    public static void main(String args[]) {
        if(args == null || args.length < 1){
            System.out.println("plz. provide arguments: test_data_file");
            return;
        }

        String testFilePath = args[0];
        //long startTime=System.currentTimeMillis();
        System.out.printf( "start loading model\n" );

        DNNModelImp dnn = DNNModelImp.getInstance();


        try {
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);

            InputStream fin = new FileInputStream(testFilePath);
            DataInputStream in = new DataInputStream(new BufferedInputStream(fin));
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            String line = "";
            int flag = 1;

            List<List<Long>> sparseShape = new ArrayList<>();

            int instanceNum = 0;

            List<Long> shape = new ArrayList<>();
            shape.add(1l);
            shape.add(124l);
            sparseShape.add(shape);
            long startTime=System.currentTimeMillis();
            long featureNum = 128l;

            while((line = br.readLine()) != null ) {
                //dnn.predictSeq(line);
                instanceNum += 1;
                String[] tokens = line.split(" ");
                List<Integer> labels = new ArrayList();
                List<Long> featureIds = new ArrayList();
                List<Float> featureValues = new ArrayList();
                //List<List<Long>> featureIndex = new ArrayList();
                List<Long> featureIndex = new ArrayList();

                labels.add(Integer.parseInt(tokens[0]));

                for(int i = 1; i < tokens.length; i ++) {
                    String[] idValue = tokens[i].split(":");
                    featureIds.add(Long.parseLong(idValue[0]));
                    featureValues.add(Float.parseFloat(idValue[1]));
                }

                Map<String, Object> featureMap = new HashMap<>();
                //"sparse_ids", "sparse_values", "sparse_shape", "output_probs"]
                featureMap.put("sparse_ids", featureIds);
                featureMap.put("sparse_values", featureValues);
                featureMap.put("feature_size", featureNum);
                //featureMap.put("instance_num", instanceNum);

                //System.out.println("process :  " + line);
//                fixedThreadPool.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            double score = dnn.predict(featureMap, Map.class);
//                            System.out.format("%d\t%10.6f\t%10.6f\n", featureMap.get("instance_num"), score, 1-score);
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });

                double score = dnn.predict(featureMap, Map.class);
                //System.out.println(line);
                System.out.format("%d\t%10.6f\t%10.6f\t%2.1f\n", instanceNum, 1-score, score, Float.parseFloat(tokens[0]));

                //System.out.println(score);

            }
            long endTime=System.currentTimeMillis(); //获取结束时间
            //System.out.println("instance Num: " + instanceNum);
            //System.out.printf( "predict time :%d\n", endTime-startTime );
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}

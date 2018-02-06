package com.company.product.project.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.company.product.project.utils.IOUtils;
import com.company.product.project.utils.ModelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

/**
 * Created by luojiahua on 2017/01/09.
 */
public class DNNModelImp implements DLModel {
    private static Logger logger = Logger.getLogger(DNNModelImp.class);

    private final String ModelFile = "nn/graph-10.db";

    private Session session = null;

    private static DNNModelImp dnn = new DNNModelImp();

    private DNNModelImp(){
        install();
    }

    public static DNNModelImp getInstance(){
        return dnn;
    }


    @Override
    public boolean install(){
        if(session != null){
            System.out.println("Tensorflow session initialized");
            return true;
        }
        byte[] graphDef = IOUtils.readBytesFromClassPath(ModelFile);
        Graph g = new Graph();
        try {
            g.importGraphDef(graphDef);
            session = new Session(g);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public void uninstall(){
        if(session == null) return;
        session.close();
        session = null;
    }

    /**
     *
     * @param input a two-dimension array input[batch_size][input_len] wrapped by Tensor.create.
     * @return a double probability
     */
    @Override
    public <T> double predict(Object input, Class<T> inputType){
        if(session == null || inputType != Map.class  || !(input instanceof Map)) {
            logger.error("the argument type of input for prediction is not Map: type(input)=" + String.valueOf(input.getClass()));
            return 0d;
        }

        try {
            Map<String, Object> inputMap = (Map<String, Object>) input;
            List<Long> sparseIds = (List<Long>) inputMap.get("sparse_ids");
            List<Float> sparseValues = (List<Float>) inputMap.get("sparse_values");
            Long featureSize = (Long)inputMap.get("feature_size");

            Tensor result = session.runner()
                    //remember that, below variables must be consistent to the export placeholder in python training file
                    .feed("sparse_ids", Tensor.create(list2BasicLongArray(sparseIds)))
                    .feed("sparse_values", Tensor.create(list2BasicFloatArray(sparseValues)))
                    .feed("sparse_shape", Tensor.create(new long[]{1l,featureSize}))
                    .feed("sparse_index", Tensor.create(genSparseIndex(sparseIds.size())))
                    .fetch("output_probs").run().get(0);
            Tensor bias = session.runner().fetch("input/biases").run().get(0);
            long[] rshape = result.shape();
            int batchSize = (int) rshape[0];
            int nlabels = (int) rshape[1];

            float[][] logits = (float[][]) result.copyTo(new float[batchSize][nlabels]);
            return logits[0][0];
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            logger.error("error in session.run: "+e.getMessage());
        }
        return 0d;
    }

    //convert list to basic java data type array, to feed for tensorflow graph. boxed java data type is not accepted.
    private long[] list2BasicLongArray(List<Long> srcList){
        int n = srcList.size();
        long[] ret = new long[n];
        int i = 0;
        for (Long e:srcList){
            ret[i] = Long.parseLong(e.toString());
            i += 1;
        }
        return ret;

    }
    //unbox java object to basic type, for feeding tensorflow graph
    private float[] list2BasicFloatArray(List<Float> srcList){
        int n = srcList.size();
        float[] ret = new float[n];
        int i = 0;
        for (Float e:srcList){
            ret[i] = Float.parseFloat(e.toString());
            i += 1;
        }
        return ret;

    }

    //generate indices sequences for both of the indices and values of the sparse array. e.g [0,0,0,1,0,2,0,3,0,4...0,n]
    //tensorflow model builds sparsetensors using this sequences. plz. refer to the python file.
    private long[] genSparseIndex(int size){

        long[] ret = new long[2 * size];

        for (int i = 0; i < size; i ++){
            ret[2 * i] = 0;
            ret[2 * i + 1] = i;
        }
        return ret;

    }


    private Long[][] list22DArray(List<List<Long>> srcList){
        int M = srcList.get(0).size();
        int N = srcList.size();
        Long[][] result = new Long[N][M];
        for( int i = 0; i<N; i++) {
            for (int j = 0; j < M; j++) {
                result[i][j] = srcList.get(i).get(j);
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
        return result;
    }

    private <T> void debugList(List<T> srcList, Class<T> type){
        String s = "";
        for(T e : srcList)
            s += " " + String.valueOf(e);
        System.out.println(s);
    }

    private <T> void debugArray(Object[] srcArr, Class<T> type){
        String s = "";
        for(Object e : srcArr)
            s += " " + String.valueOf(e);
        System.out.println(s);
    }
    private <T> void debugFloatArray(float[] srcArr){
        String s = "";
        for(float e : srcArr)
            s += " " + String.valueOf(e);
        System.out.println(s);
    }

}


















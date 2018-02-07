package com.company.product.utils.model;

import com.company.product.utils.IOUtils;
import com.company.product.utils.ModelUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

/**
 * Created by luojiahuaguet@163.com on 2017/12/19.
 * This file provides a general implementation of model inference with dense features input. not just for rnn.
 */
public class RNNModelImp implements DLModel {

    private  Map<String, Long> word2ID = new HashMap<>();

    private  final int MaxSeqLen = 300;

    private final String VocabFile = "nn/rnn_vocab.pkl.json";

    private final String ModelFile = "nn/rnn_graph.db";

    private Session session = null;

    private static RNNModelImp lstmModel = new RNNModelImp();

    private RNNModelImp(){
        loadWordDict();
        install();
    }

    public static RNNModelImp getInstance(){
        return lstmModel;
    }

    /**
     * loading word dict
     * @param
     */

    private void loadWordDict() {
        String dictStr = IOUtils.readTextFromClassPath(VocabFile);
        //System.out.println(dictStr);
        Map<String, Long> m = new Gson().fromJson(dictStr, new TypeToken<Map<String, Long>>(){}.getType());
        word2ID.putAll(m);
        //System.out.println("word2ID.size is:"+word2ID.size());
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
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void uninstall(){
        if(session == null) return;
        session.close();
        session = null;
    }

    /**
     *
     * @param input a tensor which wraps a dense two-dimension array input[batch_size][input_len].
     * @return a double probability
     */
    @Override
    public <T> double predict(Object input, Class<T> inputType){
        if(session == null || inputType != Tensor.class  || !(input instanceof Tensor)) {
            return 0d;
        }
        Tensor y = Tensor.create(0l);
        try {
            //remember that, index_x, target, softmaxLayer/probs must be consistent to the export placeholder in python training file
            Tensor result = session.runner().feed("input_x", (Tensor) input).feed("target", y)
                    .fetch("softmaxLayer/probs").run().get(0);
            long[] rshape = result.shape();
            int batchSize = (int) rshape[0];
            int nlabels = (int) rshape[1];

            float[][] logits = (float[][]) result.copyTo(new float[batchSize][nlabels]);
            return logits[0][1];
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return 0d;
    }

    /**
     *
     * @param text, for predicting by rnn/lstm
     * @return
     */
    public double predictSeq(String text){
        if(session == null) return 0d;
        //long[] arr = new long[text.length()];
        long[] arr = ModelUtils.text2IDArray(word2ID, text);
        //long[] arr = {1,2,3,4,6,7,1,2,3,4,6,7,1,2,3,4,6,7,1,2};
        if(arr == null) {
            return 0d;
        }
        String s = "";
        long[][] inputArrForTensor = new long[1][arr.length];
        for(int i = 0; i<arr.length; i++){
            inputArrForTensor[0][i] = arr[i];
            //s += String.valueOf(arr[i]) + " ";
        }
        Tensor inputTensor = Tensor.create(inputArrForTensor);
        return predict(inputTensor, Tensor.class);

    }

    public void explainModel(){

        byte[] bytes = session.runner().fetch("softmaxLayer/probs").runAndFetchMetadata().metadata;
        List<Tensor<?>> lt = session.runner().fetch("softmaxLayer/probs").runAndFetchMetadata().outputs;
        System.out.println(new String(bytes, Charset.forName("UTF-8")));

        for(Tensor s : lt){
            System.out.println(s.toString());
            System.out.println(s.dataType());
            System.out.println(s.shape());
            System.out.println(s.numDimensions());
        }
        System.exit(0);

    }


}

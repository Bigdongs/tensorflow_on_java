package com.company.product.utils.examples;

/**
 * Created by Administrator on 2017/12/7.
 */
import com.company.product.utils.model.RNNModelImp;
import com.company.product.utils.IOUtils;
import java.util.List;
//impor org.apache.commons.io.FileUtils;
//impor org.apache.commons.lang.StringUtils;


public class RNNExample {
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("plz. provide argument: input_file. format: a sequence a line.");
            return;
        }
        RNNModelImp rnn = RNNModelImp.getInstance();
        //rnn.explainModel();
        List<String> rawList = IOUtils.readAllLinesFromLocalFile(args[0]);
        for(String e : rawList) {
            double prob = rnn.predictSeq(e);
            System.out.println(e + "\t" + prob);
        }
    }

}



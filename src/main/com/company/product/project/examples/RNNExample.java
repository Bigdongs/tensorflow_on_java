package com.company.product.project.examples;

/**
 * Created by Administrator on 2017/12/7.
 */
import java.util.List;
//impor org.apache.commons.io.FileUtils;
//impor org.apache.commons.lang.StringUtils;
import com.company.product.project.model.RNNModelImp;
import com.company.product.project.utils.IOUtils;


public class RNNExample {
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("plz. provide argument: input file");
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



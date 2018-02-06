package com.company.product.project.model;

import org.tensorflow.Tensor;

import java.util.List;

/**
 * Created by luojiahua on 2017/12/19.
 */
public interface DLModel {

    /**
     * install model
     * @param
     */

    public boolean install();
    /**
     * uninstall model
     * @param
     */
    public void uninstall();
    /**
     * model prediction interface
     * @return
     */
    public <T> double predict(Object input, Class<T> inputType);

}

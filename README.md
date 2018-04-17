## tensorflow_on_java
This project provides a simple framework showing how to inference a tensorflow model in java program, without requirement of third part python serving. The framework supports both sparse and dense input features, you may follow the examples of java and python programs to build your graph using tensorflow.

## run training
    cd python
    python sparse_classifier.py --train_file a8a_train.csv.tfrecords --validate_file a8a_train.csv.tfrecords --feature_size 128 --label_size 2  --enable_colored_log

## export freezed model with sparse inputs
  1. define placeholders with uniq names for input:
    e.g. from sparse_classifier.py:
  
    sparse_ids = tf.placeholder(tf.int64, [None], name="sparse_ids")
    sparse_values = tf.placeholder(tf.float32, [None], name="sparse_values")
    sparse_shape = tf.placeholder(tf.int64, [2], name="sparse_shape")
    sparse_indices_dim = tf.placeholder(tf.int64, [None], name="sparse_index")
    ...

  2. export serialized model:

    graph = tf.graph_util.convert_variables_to_constants(sess, sess.graph_def,["sparse_index", "sparse_ids", "sparse_values", "sparse_shape", "output_probs"])
    tf.train.write_graph(graph, ".", FLAGS.freezed_model_path, as_text=False)

3. for inference using java, please refer to DNNModelImp.java.

## export freeze model with dense inputs:
e.g. 

  1. define placeholders(dense array values with any basic data type, e.g. double/int/long/float) with unique names for input
  
    input_data = tf.placeholder(tf.int64, [None, args.seq_length], name='input_x')
    ....
    with tf.variable_scope('softmaxLayer'):
        self.probs = tf.nn.softmax(logits, name='probs')
         
  2. export serialized model
  
    graph = tf.graph_util.convert_variables_to_constants(sess, sess.graph_def,["input_x", "softmaxLayer/probs"])
    tf.train.write_graph(graph, ".", "save/graph.db", as_text=False)
     
  3. for inference using java, please refer to RNNModelImp.java.

## deploy online
When running java codes in linux. if it fails and reminds lack of some versions of glibc.so, then install them. In case, you may upgrade your gcc version to 7.1.0 or above . Or run it in a container, e.g. docker


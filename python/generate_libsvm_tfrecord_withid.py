#!/usr/bin/env python

import tensorflow as tf
import os,sys


def generate_tfrecords(input_filename, output_filename):
  print("Start to convert {} to {}".format(input_filename, output_filename))
  writer = tf.python_io.TFRecordWriter(output_filename)
  instance_num = 0
  for line in open(input_filename, "r"):
    instance_num += 1
    data = line.split(" ")
    label = float(data[0])
    ids = []
    values = []
    for fea in data[1:]:
      id, value = fea.split(":")
      ids.append(int(id))
      values.append(float(value))

    # Write each example one by one
    example = tf.train.Example(features=tf.train.Features(feature={
        "label":
        tf.train.Feature(float_list=tf.train.FloatList(value=[label])),
        "ids": tf.train.Feature(int64_list=tf.train.Int64List(value=ids)),
        "values": tf.train.Feature(float_list=tf.train.FloatList(value=values)),
        "sample_id": tf.train.Feature(int64_list=tf.train.Int64List(value=[instance_num]))
    }))

    writer.write(example.SerializeToString())

  writer.close()
  print("Successfully convert {} to {}".format(input_filename,
                                               output_filename))


def main():
  if len(sys.argv) < 2:
    print "plz. privide input argument: file.libsvm"
    return
  file = sys.argv[1]
  generate_tfrecords(file, file+".tfrecords")
  

if __name__ == "__main__":
  main()

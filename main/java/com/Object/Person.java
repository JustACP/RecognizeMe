package com.Object;

import org.opencv.core.Mat;

import java.io.Serializable;

public class Person implements Serializable {
    public String  name;
    public Integer id;
    public Mat     faceFeature;

    public Person(String name, Integer id){
        this.name = name;
        this.id = id;
    }
    Person(String name,Integer id,Mat faceFeature){
        this.name = name;
        this.id = id;
        this.faceFeature = faceFeature;
    }
}

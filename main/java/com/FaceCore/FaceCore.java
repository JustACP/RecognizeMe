package com.FaceCore;

import com.Object.NoFaceFeatureException;
import com.Object.Person;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.objdetect.FaceRecognizerSF;

import javax.imageio.ImageWriteParam;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.InflaterInputStream;

public class FaceCore {

    public FaceCore() throws IOException {

        System.out.println("begin");
        writeResources();


        StringBuilder builder = new StringBuilder();
        String[] os = System.getProperties().getProperty("os.name").toLowerCase().split(" ");
        if(os[0].equals("windows")){
            System.load(System.getProperty("user.dir")+"\\resources\\opencv_java460.dll");
        }else{
            System.load(System.getProperty("user.dir")+"/resources/libopencv_java460.so");
        }



    }



    public boolean isRegistered(Person person){
        String path = System.getProperty("user.dir")+"/facefeature/"+person.id+'-'+person.name+".jpg";
        File file = new File(path);
        if(file.exists())return true;
        else return false;
    }


    public boolean faceRegister(Person person,String srcImg) throws IOException {
        isFileExist(true);
        String savePath = System.getProperty("user.dir")+"/facefeature/"+person.id+'-'+person.name+".jpg";
        Mat src= Imgcodecs.imread(srcImg);
        Mat srcFace = new Mat();
        FaceDetectorYN faceDetectorYN =FaceDetectorYN.create(System.getProperty("user.dir")+"/resources"+"/face_detection_yunet_2022mar.onnx","",src.size(),new Float(0.9),new Float(0.3),5000);
        faceDetectorYN.detect(src,srcFace);
        Integer pt1_x = Math.round(srcFace.at(Float.class,0,0).getV());
        Integer pt1_y = Math.round(srcFace.at(Float.class,0,1).getV());
        Integer pt2_x = Math.round(srcFace.at(Float.class,0,2).getV());
        Integer pt2_y = Math.round(srcFace.at(Float.class,0,3).getV());
        Mat registerFace = new Mat(src,new Rect(pt1_x,pt1_y,pt2_x,pt2_y));
        Mat tmp = new Mat();
        registerFace.copyTo(tmp);
        Imgcodecs.imwrite(savePath,tmp);

        return true;

    }



    private void writeResources() throws IOException {
        String path = System.getProperty("user.dir")+"/resources";
        File resources = new File(path);
        if(!resources.exists()){
            resources.mkdir();

            writeResoucesFile("face_detection_yunet_2022mar.onnx",path+"/face_detection_yunet_2022mar.onnx");
            writeResoucesFile("face_recognition_sface_2021dec.onnx",path+"/face_recognition_sface_2021dec.onnx");
            writeResoucesFile("libopencv_java460.so",path+"/libopencv_java460.so");
            writeResoucesFile("opencv_java460.dll",path+"/opencv_java460.dll");

        }
    }

    private  void writeResoucesFile(String resourcesName,String dstPath) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcesName);
        FileOutputStream outputStream = new FileOutputStream(dstPath);
        int flag = 0;
        byte[] bytes = new byte[1024];
        while((flag = inputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,flag);
            outputStream.flush();
        }
        outputStream.close();
    }

    private boolean isFileExist(boolean doCreateFile){
        File file = new File(System.getProperty("user.dir")+"/facefeature");
        if(!file.exists()) {

            if( doCreateFile == true){

                file.mkdir();
                return true;

            }

            return false;

        }
        return true;

    }


    public boolean faceRecognizerByPerson(String srcImage, Person person) throws NoFaceFeatureException, IOException {
        if(!isFileExist(false)) {

            throw new NoFaceFeatureException("从未保存人脸信息");
        }
        String getPath = System.getProperty("user.dir") + "/facefeature/" + person.id + '-' + person.name+".jpg";
        File file = new File(getPath);
        if(!file.exists()) return false;

        Mat feature  = getImgFeature(getPath);
        person.faceFeature = feature;

        Mat srcFeatrue = getImgFeature(srcImage);




        FaceRecognizerSF faceRecognizerSF = FaceRecognizerSF.create(System.getProperty("user.dir")+"/resources"+"/face_recognition_sface_2021dec.onnx","");
        Double score = faceRecognizerSF.match(srcFeatrue,person.faceFeature,0)*100;


        if(score >= 38.2) return true;
        return false;


    }


    public Double faceRecognizerSimilarityRatioByPerson(String srcImage, Person person) throws NoFaceFeatureException, IOException {
        if(!isFileExist(false)) {

            throw new NoFaceFeatureException("从未保存人脸信息");
        }
        String getPath = System.getProperty("user.dir") + "/facefeature/" + person.id + '-' + person.name+".jpg";
        File file = new File(getPath);
        if(!file.exists()) throw  new NoFaceFeatureException();

        Mat feature  = getImgFeature(getPath);
        person.faceFeature = feature;

        Mat srcFeatrue = getImgFeature(srcImage);




        FaceRecognizerSF faceRecognizerSF = FaceRecognizerSF.create(System.getProperty("user.dir")+"/resources"+"/face_recognition_sface_2021dec.onnx","");
        Double score = faceRecognizerSF.match(srcFeatrue,person.faceFeature,0)*100;
        return score;




    }


    private Mat getImgFeature(String srcImg){

        Mat srcFaces= new Mat();
        Mat srcToMatchFace = new Mat();
        Mat src = Imgcodecs.imread(srcImg, CvType.CV_64F);
        FaceDetectorYN faceDetectorYN =FaceDetectorYN.create(System.getProperty("user.dir")+"/resources"+"/face_detection_yunet_2022mar.onnx","",src.size(),new Float(0.9),new Float(0.3),5000);
        FaceRecognizerSF faceRecognizerSF = FaceRecognizerSF.create(System.getProperty("user.dir")+"/resources"+"/face_recognition_sface_2021dec.onnx","");
        faceDetectorYN.detect(src,srcFaces);
        faceRecognizerSF.alignCrop(src,srcFaces.row(0),srcToMatchFace);
        faceRecognizerSF.feature(srcToMatchFace,srcToMatchFace);
        srcToMatchFace = srcToMatchFace.clone();

        return srcToMatchFace;
    }





    public boolean faceRecognizerByImages(String srcImage,String dstImage){
        Mat src = Imgcodecs.imread(srcImage, CvType.CV_64F);
        Mat srcFaces= new Mat();
        Mat srcToMatchFace = new Mat();

        Mat target = Imgcodecs.imread(dstImage, CvType.CV_64F);
        Mat targetFace= new Mat();
        Mat targetToMatchFace = new Mat();

        FaceDetectorYN faceDetectorYN =FaceDetectorYN.create(System.getProperty("user.dir")+"/resources"+"/face_detection_yunet_2022mar.onnx","",src.size(),new Float(0.9),new Float(0.3),5000);
        FaceRecognizerSF faceRecognizerSF = FaceRecognizerSF.create(System.getProperty("user.dir")+"/resources"+"/face_recognition_sface_2021dec.onnx","");

        faceDetectorYN.detect(src,srcFaces);
        if(srcFaces.rows() < 1) return false;

        faceDetectorYN.setInputSize(target.size());
        faceDetectorYN.detect(target,targetFace);
        faceRecognizerSF.alignCrop(target,targetFace,targetToMatchFace);
        faceRecognizerSF.feature(targetToMatchFace,targetToMatchFace);
        targetToMatchFace = targetToMatchFace.clone();  // 绝对不可以删除

        for(int i =0 ;i < srcFaces.rows();i++){


            Integer pt1_x = Math.round(srcFaces.at(Float.class,i,0).getV());
            Integer pt1_y = Math.round(srcFaces.at(Float.class,i,1).getV());
            Integer pt2_x = Math.round(srcFaces.at(Float.class,i,2).getV());
            Integer pt2_y = Math.round(srcFaces.at(Float.class,i,3).getV());


            faceRecognizerSF.alignCrop(src,srcFaces.row(i),srcToMatchFace);
            faceRecognizerSF.feature(srcToMatchFace,srcToMatchFace);
            srcToMatchFace = srcToMatchFace.clone();

            Float tmp = srcFaces.at(Float.class,i,3).getV()+srcFaces.at(Float.class,i,1).getV() + 25;


            org.opencv.core.Point pt1 = new org.opencv.core.Point(Double.valueOf(srcFaces.at(Float.class,i,0).getV().toString()),Double.valueOf(srcFaces.at(Float.class,i,1).getV().toString()));
            org.opencv.core.Point pt2 = new Point(Double.valueOf(srcFaces.at(Float.class,i,0).getV().toString()),
                    Double.valueOf(tmp.toString()));
            Double score = faceRecognizerSF.match(srcToMatchFace, targetToMatchFace,0)*100;


            if(score >= 38.2) return true;






        }
        return false;
    }

    public Map<Integer,Double> faceRecognizerSimilarityRatioByImages(String srcImage, String dstImage) throws NoFaceFeatureException {
        Mat src = Imgcodecs.imread(srcImage, CvType.CV_64F);
        Mat srcFaces= new Mat();
        Mat srcToMatchFace = new Mat();

        Map<Integer,Double> faces =new HashMap<>();

        Mat target = Imgcodecs.imread(dstImage, CvType.CV_64F);
        Mat targetFace= new Mat();
        Mat targetToMatchFace = new Mat();

        FaceDetectorYN faceDetectorYN =FaceDetectorYN.create(System.getProperty("user.dir")+"/resources"+"/face_detection_yunet_2022mar.onnx","",src.size(),new Float(0.9),new Float(0.3),5000);
        FaceRecognizerSF faceRecognizerSF = FaceRecognizerSF.create(System.getProperty("user.dir")+"/resources"+"/face_recognition_sface_2021dec.onnx","");

        faceDetectorYN.detect(src,srcFaces);
        if(srcFaces.rows() != 1) throw new NoFaceFeatureException();

        faceDetectorYN.setInputSize(target.size());
        faceDetectorYN.detect(target,targetFace);
        faceRecognizerSF.alignCrop(target,targetFace,targetToMatchFace);
        faceRecognizerSF.feature(targetToMatchFace,targetToMatchFace);
        targetToMatchFace = targetToMatchFace.clone();  // 绝对不可以删除

        for(int i =0 ;i < srcFaces.rows();i++){


            Integer pt1_x = Math.round(srcFaces.at(Float.class,i,0).getV());
            Integer pt1_y = Math.round(srcFaces.at(Float.class,i,1).getV());
            Integer pt2_x = Math.round(srcFaces.at(Float.class,i,2).getV());
            Integer pt2_y = Math.round(srcFaces.at(Float.class,i,3).getV());


            faceRecognizerSF.alignCrop(src,srcFaces.row(i),srcToMatchFace);
            faceRecognizerSF.feature(srcToMatchFace,srcToMatchFace);
            srcToMatchFace = srcToMatchFace.clone();

            Point pt1 = new Point(Double.valueOf(srcFaces.at(Float.class,i,0).getV().toString()),Double.valueOf(srcFaces.at(Float.class,i,1).getV().toString()));

            Imgproc.rectangle(src,new Rect(pt1_x,pt1_y,pt2_x,pt2_y),new Scalar(255,0,0),8);


            Imgproc.putText(src,"Face"+i,pt1,0,2,new Scalar(255.0,0.0,0.0),8);
            Double score = faceRecognizerSF.match(srcToMatchFace, targetToMatchFace,0)*100;


            faces.put(i,score);






        }
        File file = new File(System.getProperty("user.dir")+"/FaceBox");
        if(!file.exists()) file.mkdir();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_SSS");
        Date date = new Date();

        Imgcodecs.imwrite(System.getProperty("user.dir")+"/res/"+ dateFormat.format(date).toString()+".jpg",src);
        return faces;


    }

    public static void main(String[] args) throws IOException {


    }
}

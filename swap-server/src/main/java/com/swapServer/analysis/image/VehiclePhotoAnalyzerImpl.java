package com.swapServer.analysis.image;

import com.swapServer.analysis.Analyzed;
import com.swapServer.analysis.Analyzer;
import com.swapServer.config.NettyProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Data :  2021/3/17 11:42
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class VehiclePhotoAnalyzerImpl implements Analyzer {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Autowired
    private NettyProperties nettyProperties;

    @Override
    public void analyze(Analyzed analyzed) {
        VehiclePhoto vehiclePhoto = (VehiclePhoto) analyzed;
        try {
            //备份文件到服务器
            String goalFileName = backUp(vehiclePhoto.getMultipartFile());
            handle(goalFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean support(Analyzed analyzed) {
        return analyzed instanceof VehiclePhoto;
    }

    /**
     * 备份文件
     *
     * @param multipartFile
     * @return 备份目录
     * @throws IOException
     */
    public String backUp(MultipartFile multipartFile) throws IOException {
        String goalFileName = nettyProperties.getFilePath() + UUID.randomUUID() + multipartFile.getOriginalFilename();
        File backUp = new File(goalFileName);
        if (!backUp.exists()) {
            backUp.createNewFile();
        }
        multipartFile.transferTo(backUp);
        return goalFileName;
    }

    public static void main(String[] args) {
        VehiclePhotoAnalyzerImpl vehiclePhotoAnalyzer = new VehiclePhotoAnalyzerImpl();
        vehiclePhotoAnalyzer.handle("D:" + File.separator + "chrom" + File.separator + "vehicle" + File.separator + "1.jpg");
        log.info("complete");
//        String basePath = "D:/chrom/vehicle/";
//        String folderName = "1045";
//        Mat source = Imgcodecs.imread(basePath + "1.jpg");
//        //灰度图
//        Mat grayMat = new Mat();
//        Imgproc.cvtColor(source, grayMat, Imgproc.COLOR_BGR2GRAY);
//        out(grayMat, Type.gray, folderName, basePath, 0);
//
//        //二值化
//        Mat inarizationMat = new Mat();
//        Imgproc.adaptiveThreshold(grayMat, inarizationMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 13, 5);
//        out(inarizationMat, Type.inarization, folderName, basePath, 0);
//        Imgproc.adaptiveThreshold(grayMat, inarizationMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 13, 5);
//        out(inarizationMat, Type.inarization, folderName, basePath, 1);
//        Imgproc.adaptiveThreshold(grayMat, inarizationMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 13, 5);
//        out(inarizationMat, Type.inarization, folderName, basePath, 2);
//        Imgproc.adaptiveThreshold(grayMat, inarizationMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 13, 5);
//        out(inarizationMat, Type.inarization, folderName, basePath, 3);
//        //去除噪点
//        contoursRemoveNoise(inarizationMat);
//        out(inarizationMat, Type.floodFill, folderName, basePath, 0);
//        //腐蚀，膨胀
//        Mat outImage = new Mat();
//        Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 2));
//        Imgproc.erode(inarizationMat, outImage, structImage, new Point(-1, -1), 2);
//        out(outImage, Type.erode, folderName, basePath, 0);
//        Imgproc.dilate(inarizationMat, outImage, structImage , new Point(-1, -1), 2);
//        out(outImage, Type.dilate, folderName, basePath, 0);
    }

    public static void out(Mat goalMat, Type type, String folderName, String parentPath, int typeCount) {
        File folder = new File(parentPath + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        Imgcodecs.imwrite(parentPath + folderName + "/" + type + (typeCount != 0 ? typeCount : "") + ".jpg", goalMat);
    }

    enum Type {
        gray("灰度"), inarization("二值化"), erode("腐蚀"), dilate("膨胀"), floodFill("去除噪点");

        private String alias;

        Type(String alias) {
            this.alias = alias;
        }
    }

    public static void contoursRemoveNoise(Mat inarizationMat) {
        int i, j, color = 1;
        int nWidth = inarizationMat.cols(), nHeight = inarizationMat.rows();

        for (i = 0; i < nWidth; ++i) {
            for (j = 0; j < nHeight; ++j) {
                if (inarizationMat.get(j, i)[0] == 0) {
                    //用不同颜色填充连接区域中的每个黑色点
                    //floodFill就是把一个点x的所有相邻的点都涂上x点的颜色，一直填充下去，直到这个区域内所有的点都被填充完为止
                    Imgproc.floodFill(inarizationMat, new Mat(), new Point(i, j), new Scalar(color));
                    color++;
                }
            }
        }
        //统计不同颜色点的个数
        int[] ColorCount = new int[255];

        for (i = 0; i < nWidth; ++i) {
            for (j = 0; j < nHeight; ++j) {
                if (inarizationMat.get(j, i)[0] != 255) {
                    ColorCount[(int) inarizationMat.get(j, i)[0] - 1]++;
                }
            }
        }
        //去除噪点
        for (i = 0; i < nWidth; ++i) {
            for (j = 0; j < nHeight; ++j) {

                if (ColorCount[(int) inarizationMat.get(j, i)[0] - 1] <= 1) {
                    inarizationMat.put(j, i, 255);
                }
            }
        }
        for (i = 0; i < nWidth; ++i) {
            for (j = 0; j < nHeight; ++j) {
                if (inarizationMat.get(j, i)[0] < 255) {
                    inarizationMat.put(j, i, 0);
                }
            }
        }
    }


    /**
     * 处理文件
     * 图像预处理：
     * 1、 图像灰度化；二值化
     * 2、 图像降噪，去除干扰线
     * 3、 图像腐蚀、膨胀处理
     * 4、 字符分割
     * 5、 字符归一化
     * <p>
     * 图像识别：
     * 1、 特征值提取
     * 2、 训练
     * 3、 测试
     *
     * @param fileName
     * @return 车牌号
     */
    private String handle(String fileName) {
        String LicensePlate = null;

        Mat source = Imgcodecs.imread(fileName);
        Size sourceSize = source.size();

        Mat join = CompletableFuture.supplyAsync(() -> source)
                //缩小图片
                .thenApply(handler -> ScaleDown(handler, 600))
                //灰度图片
                .thenApply(this::grayScale)
                //高斯过滤
                .thenApply(this::gaussianBlur)
                //
                .thenApply(this::sobel)
                .thenApply(this::threshold)
                .thenApply(this::morphologyClose)
                .thenApply(this::erode)
                .thenApply(handler -> dilate(handler, true))
                .thenApply(handler -> enlarge(handler, sourceSize))
                .join();
        Imgcodecs.imwrite("D:" + File.separator + "chrom" + File.separator + "vehicle" + File.separator + "goal" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + ".jpg", join);
        contours(source, join);

        return LicensePlate;
    }

    /**
     * 缩小图片，减少资源
     *
     * @param sourceMat
     * @param goalCols
     * @return
     */
    private Mat ScaleDown(Mat sourceMat, int goalCols) {
        if (goalCols < 0) {
            goalCols = 600;
        }
        if (goalCols > sourceMat.cols()) {
            return sourceMat;
        }
        final float proportion = sourceMat.rows() * 1.0f / sourceMat.cols();//源图比例
        int goalRows = Math.round(proportion * goalCols);
        Mat handleMat = new Mat(goalRows, goalCols, sourceMat.type());//目标图片

        double px = (double) handleMat.cols() / sourceMat.cols();
        double py = (double) handleMat.rows() / sourceMat.rows();
        Imgproc.resize(sourceMat, handleMat, handleMat.size(), px, py, Imgproc.INTER_LINEAR);
        return handleMat;
    }

    /**
     * 灰度图片
     *
     * @param sourceMat
     * @return
     */
    private Mat grayScale(Mat sourceMat) {
        Mat handleMat = new Mat();
        Imgproc.cvtColor(sourceMat, handleMat, Imgproc.COLOR_BGR2GRAY);
        return handleMat;
    }

    /**
     * 高斯滤波，用于 抑制噪声，平滑图像， 防止把噪点也检测为边缘
     * 高斯滤波器相比于均值滤波器对图像个模糊程度较小
     * https://blog.csdn.net/qinchao315/article/details/81269328
     * https://blog.csdn.net/qq_35294564/article/details/81142524
     *
     * @param sourceMat
     * @return
     */
    private Mat gaussianBlur(Mat sourceMat) {
        Size size = new Size(3, 3);
        Mat handleMat = new Mat();
        Imgproc.GaussianBlur(sourceMat, handleMat, size, 0, 0, Core.BORDER_DEFAULT);
        return handleMat;
    }

    private Mat sobel(Mat sourceMat) {
        int SOBEL_SCALE = 1;
        int SOBEL_DELTA = 0;
        int SOBEL_X_WEIGHT = 1;
        int SOBEL_Y_WEIGHT = 0;
        int SOBEL_KERNEL = 3;// 内核大小必须为奇数且不大于31
        double alpha = 1.5; // 乘数因子
        double beta = 10.0; // 偏移量

        Mat handleMat = new Mat();

        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();

        // Sobel滤波 计算水平方向灰度梯度的绝对值
        Imgproc.Sobel(sourceMat, grad_x, CvType.CV_8U, 1, 0, SOBEL_KERNEL, SOBEL_SCALE, SOBEL_DELTA, Core.BORDER_DEFAULT);
        Core.convertScaleAbs(grad_x, abs_grad_x, alpha, beta);   // 增强对比度

        // Sobel滤波 计算垂直方向灰度梯度的绝对值
        Imgproc.Sobel(sourceMat, grad_y, CvType.CV_8U, 0, 1, SOBEL_KERNEL, SOBEL_SCALE, SOBEL_DELTA, Core.BORDER_DEFAULT);
        Core.convertScaleAbs(grad_y, abs_grad_y, alpha, beta);
        grad_x.release();
        grad_y.release();

        // 计算结果梯度
        Core.addWeighted(abs_grad_x, SOBEL_X_WEIGHT, abs_grad_y, SOBEL_Y_WEIGHT, 0, handleMat);
        abs_grad_x.release();
        abs_grad_y.release();
        return handleMat;
    }

    /**
     * 二值化
     * 就是将图像上的像素点的灰度值设置位0或255这两个极点
     *
     * @param source
     * @return
     */
    private Mat threshold(Mat source) {
        Mat handleMat = new Mat();
        Imgproc.threshold(source, handleMat, 100, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
        source.release();
        return handleMat;
    }

    private Mat morphologyClose(Mat sourceMat) {
        Mat handleMat = sourceMat.clone();
        Size size = new Size(10, 10);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
        Imgproc.morphologyEx(sourceMat, handleMat, Imgproc.MORPH_CLOSE, kernel);
        return handleMat;
    }

    /**
     * 边缘腐蚀
     *
     * @param sourceMat
     * @return
     */
    private Mat erode(Mat sourceMat) {
        Mat handleMat = sourceMat.clone();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Imgproc.erode(sourceMat, handleMat, element);
        return handleMat;
    }

    /**
     * 进行膨胀操作
     * 也可以理解为字体加粗操作
     *
     * @param sourceMat 二值图像
     * @return
     */
    private Mat dilate(Mat sourceMat, Boolean correct) {
        Mat handleMat = sourceMat.clone();
        // 返回指定形状和尺寸的结构元素  矩形：MORPH_RECT;交叉形：MORPH_CROSS; 椭圆形：MORPH_ELLIPSE
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Imgproc.dilate(sourceMat, handleMat, element);

        // 先腐蚀 后扩张，会存在一定的偏移； 这里校正偏移量
        if (correct) {
            Mat transformMat = Mat.eye(2, 3, CvType.CV_32F);
            transformMat.put(0, 2, -4 / 2);
            transformMat.put(1, 2, -4 / 2);
            Imgproc.warpAffine(handleMat, handleMat, transformMat, sourceMat.size());
        }
        return handleMat;
    }

    /**
     * 放大尺寸
     *
     * @param sourceMat
     * @param size
     * @return
     */
    private Mat enlarge(Mat sourceMat, Size size) {
        Mat handleMat = sourceMat.clone();
        Imgproc.resize(sourceMat, handleMat, size, 0, 0, Imgproc.INTER_CUBIC);
        return handleMat;
    }

    /**
     * 画轮廓
     * @param sourceMat
     * @param handle
     * @return
     */
    public List<MatOfPoint> contours(Mat sourceMat, Mat handle) {
        List<MatOfPoint> contours = Lists.newArrayList();
        Mat hierarchy = new Mat();
        Point offset = new Point(0, 0); // 偏移量
        /*if(inMat.width() > 600) {
            offset = new Point(-5, -10); // 偏移量 // 对应sobel的偏移量
        }*/
        // RETR_EXTERNAL只检测最外围轮廓， // RETR_LIST   检测所有的轮廓
        // CHAIN_APPROX_NONE 保存物体边界上所有连续的轮廓点到contours向量内
        Imgproc.findContours(handle, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, offset);

        Mat result = new Mat();
        sourceMat.copyTo(result); //  复制一张图，不在原图上进行操作，防止后续需要使用原图
        // 将轮廓用红色描绘到原图
        Imgproc.drawContours(result, contours, -1, new Scalar(0, 0, 255, 255));

        Imgcodecs.imwrite("D:" + File.separator + "chrom" + File.separator + "vehicle" + File.separator + "goal" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + ".jpg", result);

        return contours;
    }
}

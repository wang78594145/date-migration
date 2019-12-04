package cn.com.geovis.datamigration.etl.sqlite2Sqlite;


import cn.com.geovis.datamigration.domain.Task;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.geotools.factory.Hints;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

/**
 * 此类生成layer.properties文件，初始化实例，调用setLayerProperties方法，设置实例相关成员变量参数，
 * 调用writeLayerPropertiesFile方法将相关信息写入文件。
 */

@Data
@Slf4j
public class LayerProperties {

    private LocalDateTime dateTime;
    private String mimetype;
    private short minZoom;
    private int maxZoom;
    private String gridset;
    private String bounds;

    private void setLayerProperties(Task task) {
        this.dateTime = task.getFinishTime();
        this.mimetype = "jpeg";
        this.minZoom = task.getLayerMin();
        this.maxZoom = task.getLayerMax();
        this.gridset = "EPSG\\:" + task.getEpsg();
//        GeoUtils geoUtils = new GeoUtils();
        double[] bbox = new double[4];
        bbox[0] = task.getLonBegin();
        bbox[1] = task.getLatBegin();
        bbox[2] = task.getLonEnd();
        bbox[3] = task.getLatEnd();
        if (task.getEpsg() == 3857) {


            double minX = transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[0];
            double minY = transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[1];
            double maxX = transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[2];
            double maxY = transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[3];
            this.bounds = String.format("%.6f", minX) + "," + String.format("%.6f", minY)
                    + "," + String.format("%.6f", maxX) + "," + String.format("%.6f", maxY);
        }
        if (task.getEpsg()==4326){
            double minX = task.getLonBegin();
            double minY = task.getLatBegin();
            double maxX =task.getLonEnd();
            double maxY = task.getLatEnd();
            this.bounds = String.format("%.6f", minX) + "," + String.format("%.6f", minY)
                    + "," + String.format("%.6f", maxX) + "," + String.format("%.6f", maxY);
        }
    }

    public static double[] transformCRSs(double[] bbox, String srcGridset, String destGridset) {
        if (srcGridset.equals(destGridset)) {
            return bbox;
        } else {
            boolean compensate1 = false;
            boolean compensate2 = false;
            if (srcGridset.equals("EPSG:4326")) {
                if (bbox[1] == -90.0D) {
                    bbox[1] = -89.9999D;
                    compensate1 = true;
                }

                if (bbox[3] == 90.0D) {
                    bbox[3] = 89.9999D;
                    compensate2 = true;
                }
            }

            Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, true);
            ReferencedEnvelope referencedEnvelope = null;
            double[] result = null;
            try {
                referencedEnvelope = new ReferencedEnvelope(bbox[0], bbox[2], bbox[1], bbox[3], CRS.decode(srcGridset.toString()));
                GeneralEnvelope targetEnvelope = CRS.transform(referencedEnvelope, CRS.decode(destGridset));
                referencedEnvelope = new ReferencedEnvelope(targetEnvelope);
                double minX = referencedEnvelope.getMinX();
                double maxX = referencedEnvelope.getMaxX();
                double minY = referencedEnvelope.getMinY();
                double maxY = referencedEnvelope.getMaxY();
                if (compensate1) {
                    minY = -2.0037508342789244E7D;
                }

                if (compensate2) {
                    maxY = 2.0037508342789244E7D;
                }
                result = new double[]{minX, minY, maxX, maxY};
                calibration(result, destGridset);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;

        }
    }

    private static void calibration(double[] ds, String gridset) {
        if (gridset.equals("EPSG:4326")) {
            calibration4326Value(ds);
        } else if (gridset.equals("EPSG:3857")) {
            calibration3857Value(ds);
        }

    }

    private static void calibration4326Value(double[] ds) {
        ds[0] = ds[0] < -180.0D ? -180.0D : ds[0];
        ds[1] = ds[1] < -90.0D ? -90.0D : ds[1];
        ds[2] = ds[2] > 180.0D ? 180.0D : ds[2];
        ds[3] = ds[3] > 90.0D ? 90.0D : ds[3];
    }

    private static void calibration3857Value(double[] ds) {
        ds[0] = ds[0] < -2.0037508342789244E7D ? -2.0037508342789244E7D : ds[0];
        ds[1] = ds[1] < -2.0037508342789244E7D ? -2.0037508342789244E7D : ds[1];
        ds[2] = ds[2] > 2.0037508342789244E7D ? 2.0037508342789244E7D : ds[2];
        ds[3] = ds[3] > 2.0037508342789244E7D ? 2.0037508342789244E7D : ds[3];
    }

    private static Map<String, Double> lonLat2Mercator(double latitude, double longitude) {
        Map<String, Double> mercator = new HashMap<String, Double>();
        double x = longitude * 20037508.342787 / 180;
        double y = log(tan((90 + latitude) * PI / 360)) / (PI / 180);
        y = y * 20037508.342787 / 180;
        mercator.put("x", x);
        mercator.put("y", y);
        return mercator;
    }


    public void writeLayerPropertiesFile(Task task) {
        setLayerProperties(task);
        String outputPath = task.getOutputPath() + "/layer.properties";
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(outputPath));
            pw.println("#layer.properties");
            pw.println("#" + dateTime);
            pw.println("mimetype=" + mimetype);
            pw.println("minZoom=" + minZoom);
            pw.println("maxZoom=" + maxZoom);
            pw.println("gridset=" + gridset);
            pw.println("bounds=" + bounds);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}

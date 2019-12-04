package cn.com.geovis.datamigration.etl.hbase2sqlite;





import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class test {

    @Value("${s2SthreadNum}")
    public int s2SThreadNum;

//    @Test
//    public void Testtt() {
//        GeoUtils geoUtils = new GeoUtils();
//        double[] bbox = new double[]{-180, -90, 180, 90};
//
//        double minX = geoUtils.transformCRS(bbox, "EPSG:4326", "EPSG:3857")[0];
//        double minY = geoUtils.transformCRS(bbox, "EPSG:4326", "EPSG:3857")[1];
//        double maxX = geoUtils.transformCRS(bbox, "EPSG:4326", "EPSG:3857")[2];
//        double maxY = geoUtils.transformCRS(bbox, "EPSG:4326", "EPSG:3857")[3];
//        String bounds = String.format("%.6f", minX) + "," + String.format("%.6f", minY * 2)
//                + "," + String.format("%.6f", maxX) + "," + String.format("%.6f", maxY * 2);
//        System.out.println(bounds);
//        System.out.println(minY);
//    }
    @Test
    @Bean
    public void PeiZhi(){
        System.out.println(this.s2SThreadNum);
    }
}

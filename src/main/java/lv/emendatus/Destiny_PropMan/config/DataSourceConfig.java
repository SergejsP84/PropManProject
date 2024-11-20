package lv.emendatus.Destiny_PropMan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Autowired
    private DataSource dataSource;

}

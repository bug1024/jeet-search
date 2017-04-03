package elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * user实体类
 *
 * @author bug1024
 * @date 2017-04-02
 */
@Data
@Document(indexName="jeet-search", type="user", refreshInterval="-1")
public class User implements Serializable {

    private static final long serialVersionUID = -2312110729335920029L;

    @Id
    private Integer id;

    private String username;

    private String real_name;

    private String status;

    private String create_time;

    private String update_time;

}

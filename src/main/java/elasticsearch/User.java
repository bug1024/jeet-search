package elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

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

    private Integer username;

    @JsonProperty("real_name")
    private String realName;

    private Integer status;

    @JsonProperty("create_time")
    private Timestamp createTime;

    @JsonProperty("update_time")
    private Timestamp updateTime;

}

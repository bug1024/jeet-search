package elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * user仓库
 *
 * @author bug1024
 * @date 2017-04-02
 */
public interface UserRepository extends ElasticsearchRepository<User, Long> {

}

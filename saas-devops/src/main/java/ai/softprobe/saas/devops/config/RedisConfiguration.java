package ai.softprobe.saas.devops.config;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.DefaultRedisCacheProvider;
import com.arextest.common.cache.SentinelRedisCacheProvider;
import com.arextest.common.cache.redistemplate.RedisTemplateCacheProvider;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author b_yu
 * @since 2023/4/6
 */
@Configuration
public class RedisConfiguration {

  @Value("${sp.redis.uri:}")
  private String redisUri;

  @Value("${sp.redis.sentinelUrl:}")
  private String sentinelUrl;


  @Bean
  @ConditionalOnMissingBean(CacheProvider.class)
  @ConditionalOnExpression("!'${sp.redis.uri:}'.isEmpty() or !'${sp.redis.sentinelUrl:}'.isEmpty()")
  public CacheProvider cacheProvider() {
    if (StringUtils.isNotEmpty(sentinelUrl)) {
      return new SentinelRedisCacheProvider(sentinelUrl);
    }
    return new DefaultRedisCacheProvider(redisUri);
  }

  @Bean
  @ConditionalOnExpression("!'${sp.redis.uri:}'.isEmpty() or !'${sp.redis.sentinelUrl:}'.isEmpty()")
  public RedissonClient redissonClient(CacheProvider cacheProvider) {
    return cacheProvider.getRedissionClient();
  }

  @Bean
  @ConditionalOnExpression("'${sp.redis.uri:}'.isEmpty() and '${sp.redis.sentinelUrl:}'.isEmpty()")
  public RedisTemplate<byte[], byte[]> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(RedisSerializer.byteArray());
    template.setValueSerializer(RedisSerializer.byteArray());
    return template;
  }


  @Bean
  @ConditionalOnExpression("'${sp.redis.uri:}'.isEmpty() and '${sp.redis.sentinelUrl:}'.isEmpty()")
  @ConditionalOnMissingBean(CacheProvider.class)
  public CacheProvider redisCacheProvider(RedisTemplate redisTemplate,
      RedissonClient redissonClient) {
    return new RedisTemplateCacheProvider(redisTemplate, redissonClient);
  }
}

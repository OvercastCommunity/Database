package tc.oc.occ.database.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

  private JedisPool pool;

  public RedisPool(RedisConfig config) {
    createConnection(config);
  }

  private void createConnection(RedisConfig config) {
    if (!config.isEnabled()) return;
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(config.getMaxConnections());

    this.pool =
        new JedisPool(
            poolConfig,
            config.getHost(),
            config.getPort(),
            config.getTimeout(),
            config.getPassword(),
            config.getDatabase());
  }

  public void reload(RedisConfig config) {
    shutdown();
    createConnection(config);
  }

  public Jedis getConnection() {
    if (pool == null) return null;

    return pool.getResource();
  }

  public void shutdown() {
    if (pool != null) {
      pool.close();
    }
  }
}

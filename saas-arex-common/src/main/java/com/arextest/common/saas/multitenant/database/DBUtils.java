package com.arextest.common.saas.multitenant.database;

import com.mongodb.MongoClientSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * @author: QizhengMo
 * @date: 2024/4/2 16:41
 */
public class DBUtils {

  public static CodecRegistry customCodecRegistry(List<CodecProvider> additionalCodecProviders) {
    List<CodecProvider> codecProviders =
        new ArrayList<>(
            Optional.ofNullable(additionalCodecProviders).orElse(Collections.emptyList()));

    codecProviders.add(PojoCodecProvider.builder().automatic(true).build());

    CodecRegistry registry = CodecRegistries.fromProviders(codecProviders);
    return CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), registry);
  }
}

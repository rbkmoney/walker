package com.rbkmoney.walker.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv;

/**
 * Created by vpankrashkin on 10.08.16.
 */
//@Configuration
public class HandlerConfig {

//    @Bean
//    public MerchantStatisticsSrv.Iface merchantStatisticsHandler(StatisticsDao statisticsDao) {
//        return new MerchantStatisticsHandler(new QueryProcessorImpl(new JsonQueryParser() {
//            @Override
//            protected ObjectMapper getMapper() {
//                ObjectMapper mapper = super.getMapper();
//                mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//                return mapper;
//            }
//        }, new QueryBuilderImpl(), new QueryContextFactoryImpl(statisticsDao)));
//    }
}

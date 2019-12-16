package com.cad.elasticsearchservice.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cad.elasticsearchservice.Util.getIntervalTime;
import com.cad.entity.domain.Line;
import com.cad.entity.domain.SuperLine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.BucketsAggregator;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregator;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ESService {

    @Resource(name = "highLevelClient")
    RestHighLevelClient restHighLevelClient ;


    /**
     * 获取es中所有索引
     * @return
     */
    public Set getIndex(){
        try {
            GetAliasesResponse response = restHighLevelClient.indices().getAlias(new GetAliasesRequest(),RequestOptions.DEFAULT);
            return response.getAliases().keySet();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //创建索引
    public void createIndex(String INDEX_NAME,int number_of_shards,int number_of_replicas) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        buildSetting(request,number_of_shards,number_of_replicas);
        buildDefaultIndexMapping(request);
        restHighLevelClient.indices().create(request,RequestOptions.DEFAULT);
    }


    //设置分片
    public void buildSetting(CreateIndexRequest request,int number_of_shards,int number_of_replicas){
        request.settings(Settings.builder().put("index.number_of_shards",number_of_shards)
                .put("index.number_of_replicas",number_of_replicas));
    }

    //设置index的 默认 mapping
    public void buildDefaultIndexMapping(CreateIndexRequest request){
        Map<String, Object> jsonMap = new HashMap<>();
        Map<String, Object> number = new HashMap<>();
        number.put("type", "text");
        Map<String, Object> price = new HashMap<>();
        price.put("type", "float" );
        Map<String, Object> title = new HashMap<>();
        title.put("type", "text");
        Map<String, Object> province = new HashMap<>();
        province.put("type", "text");
        Map<String, Object> publishTime = new HashMap<>();
        publishTime.put("type", "date");
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", number);
        properties.put("price", price);
        properties.put("title", title);
        properties.put("province", province);
        properties.put("publishTime", publishTime);
        Map<String, Object> book = new HashMap<>();
        book.put("properties", properties);
        jsonMap.put("books", book);
        request.mapping("books", jsonMap);
    }


    /**
     * 删除索引
     * @param INDEX_NAME
     * @throws IOException
     */
    public boolean deleteIndex(String INDEX_NAME) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(INDEX_NAME);
        if(restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT)) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX_NAME);
            restHighLevelClient.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
            return true;
        }
        return false;
    }

    /**
     * 删除数据
     * @param id
     * @return
     * @throws IOException
     */
    public String deleteData(String INDEX_NAME,String TYPE,String id) throws IOException{
        DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, TYPE, id);
        DeleteResponse deleteResponse =  restHighLevelClient.delete(deleteRequest,RequestOptions.DEFAULT);
        return deleteResponse.getResult().toString();
    }

    /**
     * 获取es中索引基础信息
     * @return
     */
    public Object getIndexBaseInfo(){
        try {
            GetSettingsResponse getSettingsResponse = restHighLevelClient.indices().getSettings(new GetSettingsRequest(),RequestOptions.DEFAULT);
            System.out.println(restHighLevelClient.info(RequestOptions.DEFAULT).toString());

            return getSettingsResponse.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取es中索引及其各种状态
     * @return
     */
    public Object getNodeDetails(String ipAddress){
        try {
            RestTemplate restTemplate = new RestTemplate();
            String ip = ipAddress.split(":")[0];
            String port = ipAddress.split(":")[1];
            JSONObject jsonObject = restTemplate.getForObject("http://{1}:{2}/{3}",JSONObject.class,ip,port,"_stats");
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public BoolQueryBuilder parseToQueryBuilder(String FilterJsonString){
        if (FilterJsonString ==null ){
            return null;
        }
        if (FilterJsonString.equals("")|| FilterJsonString.equals("{}")){
            return null;
        }
        try {
            JSONObject filterjsonObject = JSONObject.parseObject(FilterJsonString);
            JSONObject filterjsonObject1 = ((JSONObject) filterjsonObject.get("bool"));
            if (filterjsonObject1 == null) {
                return null;
            }
            JSONArray mustjsonObject = ((JSONArray) filterjsonObject1.get("must"));
            JSONArray mustnotjsonObject = ((JSONArray) filterjsonObject1.get("must_not"));
            JSONArray shouldjsonObject = ((JSONArray) filterjsonObject1.get("should"));

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//            System.out.println(filterjsonObject1);
            if (mustjsonObject != null) {
                for (Object object : mustjsonObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    // 若为不存在term此属性，说明可能是嵌套，
                    if (jsonObject.get("term") == null) {
                        //如果bool也为空，说明既不是嵌套也不是term
                        if (jsonObject.get("bool") == null) {
                            continue;
                        } else {
                            boolQueryBuilder.must(this.parseToQueryBuilder(jsonObject.toJSONString()));
                        }
                    } else {
                        boolQueryBuilder.must(this.singleTermQueryBuilder(jsonObject));
                    }
                }
            }
            if (mustnotjsonObject != null) {
                for (Object object : mustnotjsonObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    // 若为不存在term此属性，说明可能是嵌套，
                    if (jsonObject.get("term") == null) {
                        //如果bool也为空，说明既不是嵌套也不是term
                        if (jsonObject.get("bool") == null) {
                            continue;
                        } else {
                            boolQueryBuilder.mustNot(this.parseToQueryBuilder(jsonObject.toJSONString()));
                        }
                    } else {
                        boolQueryBuilder.mustNot(this.singleTermQueryBuilder(jsonObject));
                    }
                }
            }
            if (shouldjsonObject != null) {
                for (Object object : shouldjsonObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    // 若为不存在term此属性，说明可能是嵌套，
                    if (jsonObject.get("term") == null) {
                        //如果bool也为空，说明既不是嵌套也不是term
                        if (jsonObject.get("bool") == null) {
                            continue;
                        } else {
                            boolQueryBuilder.should(this.parseToQueryBuilder(jsonObject.toJSONString()));
                        }
                    } else {
                        boolQueryBuilder.should(this.singleTermQueryBuilder(jsonObject));
                    }
                }
            }

            return boolQueryBuilder;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    /**
     * 根据 {"term": {"type": "equal","property": "some_P",value": "2019-01-10 12:15:00"}} 此种数据，返回querybuilder
     *
     * @return QueryBuilder 错误则返回null
     */
    public QueryBuilder singleTermQueryBuilder(JSONObject jsonObject){
        if(jsonObject.get("term")==null){
            return null;
        }
        JSONObject jsonObject1 = (JSONObject) jsonObject.get("term");
        if (jsonObject1.get("type") ==null || jsonObject1.get("property") ==null ||jsonObject1.get("value") ==null ){
            return null;
        }
        if(jsonObject1.get("type").equals("equal")){
            return QueryBuilders.termQuery((String)jsonObject1.get("property"),(String)jsonObject1.get("value"));
        }else if (jsonObject1.get("type").equals("regexp")){
            return QueryBuilders.regexpQuery((String)jsonObject1.get("property"),(String) jsonObject1.get("value"));
        }else if (jsonObject1.get("type").equals("gte")){
            return QueryBuilders.rangeQuery((String)jsonObject1.get("property")).from(jsonObject1.get("value"));
        }else if (jsonObject1.get("type").equals("lte")){
            return QueryBuilders.rangeQuery((String)jsonObject1.get("property")).to(jsonObject1.get("value"));
        }

        return null;
    }
    /**
     * 根据索引查询数据（最后以时间排序）
     * @param index
     * @param lines
     * @return
     */
    public SearchHits getDataByIndex(String index, Integer lines,String type,Date From,Date To,String FilterJsonString){

//        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        //将时间转化为UTC格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        System.out.println(sdf.format(From));
//        System.out.println(sdf.format(To));
//        System.out.println(type);
        //查
        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //        sourceBuilder.query(QueryBuilders.termQuery("_type", "doc")).size(lines).sort("@timestamp", SortOrder.DESC);
//        sourceBuilder.query(QueryBuilders.matchAllQuery()).size(lines).sort("@timestamp", SortOrder.DESC);
        BoolQueryBuilder boolQueryBuilder =this.parseToQueryBuilder(FilterJsonString);
        if (boolQueryBuilder==null){
            boolQueryBuilder = QueryBuilders.boolQuery();
        }
        sourceBuilder.query(boolQueryBuilder
                .must(QueryBuilders.termQuery("_type", type))
                .must(QueryBuilders.rangeQuery("@timestamp")
                        .from(sdf.format(From)).to(sdf.format(To)))).size(lines)
                .sort("@timestamp", SortOrder.DESC);

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
            return response.getHits();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取某索引中某些字段的类型
     * @param index
     * @return
     */
    public Object getAllFieldsDetail(String index){

        //查
        GetFieldMappingsRequest request = new GetFieldMappingsRequest();
        request.indices(index);
        request.types("doc");
        request.fields("*");

        try {
            GetFieldMappingsResponse response = restHighLevelClient.indices().getFieldMapping(request, RequestOptions.DEFAULT);
            Map<String,Object> map = new HashMap<>();
            for (String field: response.mappings().get(index).get("doc").keySet()){
//                System.out.println(field);
//                System.out.println(response.mappings().get(index).get("doc").get(field).sourceAsMap().keySet());

                for(String key : response.mappings().get(index).get("doc").get(field).sourceAsMap().keySet()){
//                    System.out.println(response.mappings().get(index).get("doc").get(field).sourceAsMap().get(key));
                    map.put(field,response.mappings().get(index).get("doc").get(field).sourceAsMap().get(key));
                }
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 根据索引查询所有字段名称
     * @param index
     * @return
     */
    public Object getFieldsByIndex(String index){

        //查
        GetFieldMappingsRequest request = new GetFieldMappingsRequest();
        request.indices(index);
        request.types("doc");
        request.fields("*");
        try {
            GetFieldMappingsResponse response = restHighLevelClient.indices().getFieldMapping(request, RequestOptions.DEFAULT);

            return response.mappings().get(index).get("doc").keySet();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *  获取指标型数据单线条（x轴为时间）
     * @param index  数据索引（类似数据库）
     * @param datefield 横坐标（类型为时间）
     * @param Fromdate 起始时间
     * @param Todate 截止时间
     * @param interval 间隔 （为 "1s"  "5s"  "1m"  "1d"  "1w"  等）
     * @param field 查询的属性
     * @param Indicator 指标（max，avg，min,count）
     * @param LineName 线条名称
     * @param searchName 该次查询名称
     * @return
     */
    public Object dateMetricHistogram(String index,String datefield,Date Fromdate,Date Todate,String interval,String field,String Indicator,String LineName,String searchName,String FilterJsonString){

        //查
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        AggregationBuilder Builder = AggregationBuilders.avg(LineName).field(field);
        if(Indicator.equals("avg")) {
            Builder = AggregationBuilders.avg(LineName).field(field);
        }else if(Indicator.equals("max")) {
            Builder = AggregationBuilders.max(LineName).field(field);
        }else if(Indicator.equals("min")) {
            Builder = AggregationBuilders.min(LineName).field(field);
        }else if(Indicator.equals("count")) {
            Builder = AggregationBuilders.count(LineName).field(field);
        }else if(Indicator.equals("sum")) {
            Builder = AggregationBuilders.sum(LineName).field(field);
        }
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder  = AggregationBuilders.dateHistogram(searchName)
                .dateHistogramInterval(new DateHistogramInterval(interval))
                .timeZone(DateTimeZone.getDefault())        // 统计的时候考虑到时区问题
                .format("yyyy-MM-dd HH:mm:ss")
                .field(datefield)
                .minDocCount(0L)
                .extendedBounds(new ExtendedBounds(Fromdate.getTime(),Todate.getTime()))
                .subAggregation(Builder);

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp").from(Fromdate.getTime()).to(Todate.getTime());
        BoolQueryBuilder boolQueryBuilder =this.parseToQueryBuilder(FilterJsonString);
        if (boolQueryBuilder==null){
            boolQueryBuilder = QueryBuilders.boolQuery();
        }
        searchSourceBuilder.aggregation(dateHistogramAggregationBuilder).query(boolQueryBuilder.must(rangeQueryBuilder)).fetchSource(false);

        searchRequest.source(searchSourceBuilder);
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
//            System.out.println(response.toString());
            return response.getAggregations().getAsMap().get(searchName);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  获取指标型数据多线条（x轴为时间）
     * @param index  数据索引（类似数据库）
     * @param datefield 横坐标（类型为时间）
     * @param Fromdate 起始时间
     * @param Todate 截止时间
     * @param interval 间隔 （为 "1s"  "5s"  "1m"  "1d"  "1w"  等）
     * @param searchName 该次查询名称
     * @param mutilines
    //     * @param field 查询的属性
    //     * @param Indicator 指标（max，avg，min,count）
    //     * @param LineName 线条名称
     * @return
     */
    public Object mutiDateMetricHistogram(String index, String datefield, Date Fromdate, Date Todate, String interval, String searchName, ArrayList<Line> mutilines,String FilterJsonString){

        //查
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        DateHistogramAggregationBuilder dateHistogramAggregationBuilder  = AggregationBuilders.dateHistogram(searchName)
                .dateHistogramInterval(new DateHistogramInterval(interval))
                .timeZone(DateTimeZone.getDefault())        // 统计的时候考虑到时区问题
                .format("yyyy-MM-dd HH:mm:ss")
                .field(datefield)
                .minDocCount(0L)
                .extendedBounds(new ExtendedBounds(Fromdate.getTime(),Todate.getTime()));

//                .subAggregation(Builder);


        for (Line line:mutilines){

            AggregationBuilder Builder = AggregationBuilders.avg(line.getLineName()).field(line.getField());
            if(line.getIndicator().equals("avg")) {
                Builder = AggregationBuilders.avg(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("max")) {
                Builder = AggregationBuilders.max(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("min")) {
                Builder = AggregationBuilders.min(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("count")) {
                Builder = AggregationBuilders.count(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("sum")) {
                Builder = AggregationBuilders.sum(line.getLineName()).field(line.getField());
            }


            dateHistogramAggregationBuilder.subAggregation(Builder);
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp").from(Fromdate.getTime()).to(Todate.getTime());
        BoolQueryBuilder boolQueryBuilder =this.parseToQueryBuilder(FilterJsonString);
        if (boolQueryBuilder==null){
            boolQueryBuilder = QueryBuilders.boolQuery();
        }
        searchSourceBuilder.aggregation(dateHistogramAggregationBuilder).query(boolQueryBuilder.must(rangeQueryBuilder)).fetchSource(false);


        searchRequest.source(searchSourceBuilder);
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
//            System.out.println(response.toString());
            return response.getAggregations().getAsMap().get(searchName);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     *  获取指标型数据多线条（x轴为时间） （每条线都额外存在一个filter）
     * @param index  数据索引（类似数据库）
     * @param datefield 横坐标（类型为时间）
     * @param Fromdate 起始时间
     * @param Todate 截止时间
     * @param interval 间隔 （为 "1s"  "5s"  "1m"  "1d"  "1w"  等）
     * @param searchName 该次查询名称
     * @param mutilines
     * @return
     */
    public Object superMutiDateMetricHistogram(String index, String datefield, Date Fromdate, Date Todate, String interval, String searchName, ArrayList<SuperLine> mutilines, String FilterJsonString)throws Exception{
        List<ParsedDateHistogram> list = new ArrayList<ParsedDateHistogram>();
        for (SuperLine line:mutilines){
            //查
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //直接将line.getLineName() 当做searchName
            DateHistogramAggregationBuilder dateHistogramAggregationBuilder  = AggregationBuilders.dateHistogram(searchName)
                    .dateHistogramInterval(new DateHistogramInterval(interval))
                    .timeZone(DateTimeZone.getDefault())        // 统计的时候考虑到时区问题
                    .format("yyyy-MM-dd HH:mm:ss")
                    .field(datefield)
                    .minDocCount(0L)
                    .extendedBounds(new ExtendedBounds(Fromdate.getTime(),Todate.getTime()));
            AggregationBuilder Builder = AggregationBuilders.avg(line.getLineName()).field(line.getField());
            if(line.getIndicator().equals("avg")) {
                Builder = AggregationBuilders.avg(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("max")) {
                Builder = AggregationBuilders.max(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("min")) {
                Builder = AggregationBuilders.min(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("count")) {
                Builder = AggregationBuilders.count(line.getLineName()).field(line.getField());
            }else if(line.getIndicator().equals("sum")) {
                Builder = AggregationBuilders.sum(line.getLineName()).field(line.getField());
            }

            dateHistogramAggregationBuilder.subAggregation(Builder);
            if (FilterJsonString ==null ||FilterJsonString.equals("")){
                FilterJsonString = "{}";
            }
            JSONObject filterjsonObject = JSONObject.parseObject(FilterJsonString);
            if (filterjsonObject==null){
                filterjsonObject = new JSONObject();
            }
            if(filterjsonObject.get("bool")==null){
                filterjsonObject.put("bool",new JSONObject());
            }
            if(((JSONObject)filterjsonObject.get("bool")).get("must")==null ){
                ((JSONObject)filterjsonObject.get("bool")).put("must",new JSONArray());
            }
            if (line.getFilter()==null || line.getFilter().toString().equals("")|| line.getFilter().toString().equals(" ")){
                System.out.println(line.getFilter());
            }else{
                String  FilterString = JSONObject.toJSONString(line.getFilter());
                JSONObject jsonObject =JSONObject.parseObject(FilterString);

                ((JSONArray)(((JSONObject)filterjsonObject.get("bool")).get("must"))).add(jsonObject);
            }

//            System.out.println("- - - - - - - - - - - ");
//            System.out.println(filterjsonObject.toJSONString());
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp").from(Fromdate.getTime()).to(Todate.getTime());
            BoolQueryBuilder boolQueryBuilder =this.parseToQueryBuilder(filterjsonObject.toJSONString());
            if (boolQueryBuilder==null){
                boolQueryBuilder = QueryBuilders.boolQuery();
            }
            searchSourceBuilder.aggregation(dateHistogramAggregationBuilder).query(boolQueryBuilder.filter(rangeQueryBuilder)).fetchSource(false);
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
//            Gson gson = new Gson();
//            ParsedDateHistogram parsedDateHistogram = response.getAggregations().get(searchName);
//            Histogram.Bucket parsedBucket = parsedDateHistogram.getBuckets().get(0);
//            Aggregations aggregations = parsedBucket.getAggregations();
//
//            String kas_string = parsedBucket.getKeyAsString();
//
//            Map<String, Aggregation> map = aggregations.asMap();

            // 此处设计为只会出现一个
            // 因此 only get(0)
            if(response.getAggregations().asList().size()>=1){
                list.add(response.getAggregations().get(searchName));
            }
        }
        if(list.size()>=2){
            return combineAggregation(list,interval);
        }else if(list.size()==1){
            return list.get(0);
        }else {
            throw new Exception("Maybe your param or filter is wrong?");
        }

    }
    public ParsedDateHistogram combineAggregation(List<ParsedDateHistogram> list,String interval){

        ParsedDateHistogram parsedDateHistogram = list.get(0);
        for (int i = 1;i<list.size();i++){
            try {
                parsedDateHistogram = this.combineTwoLines(parsedDateHistogram,list.get(i),interval);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parsedDateHistogram;
    }
    public ParsedDateHistogram combineTwoLines(ParsedDateHistogram dateHistogram,ParsedDateHistogram dateHistogram2,String interval)throws Exception{
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        if( dateHistogram==null ||dateHistogram.getBuckets().size()==0){
            return dateHistogram2;
        }
        if(dateHistogram2==null ||dateHistogram2.getBuckets().size()==0){
            return dateHistogram;
        }
        int index = 0;
        for(int index2 = 0;index2<dateHistogram2.getBuckets().size();index2++){
            Histogram.Bucket bucket2 = dateHistogram2.getBuckets().get(index2);
            // 当第一条线已经到末尾
            if(index>=dateHistogram.getBuckets().size()){
                //将所有后面的数据插入其后面,而后退出循环
                for (;index2<dateHistogram2.getBuckets().size();index2++){
                    bucket2 = dateHistogram2.getBuckets().get(index2);

                    Long date2  = sdf.parse( bucket2.getKeyAsString()).getTime();
                    Long date1 = sdf.parse(dateHistogram.getBuckets().get(index-1).getKeyAsString()).getTime();
                    Long gap =getIntervalTime.getAsMil(interval) ;
                    Long newdate =this.getMyInt(date2-date1-gap/2,gap)*gap+date1;
                    String date = sdf.format(new Date(newdate));
                    //新增一个x轴上的点 ,利用反射去修改keyAsString，然后利用反射修改final修饰的buckets
                    try {
                        Class classT = bucket2.getClass();
                        classT = classT.getSuperclass();
                        Field field = classT.getDeclaredField("keyAsString");
                        field.setAccessible(true);
                        field.set(bucket2,date);

                        Class classD =dateHistogram.getClass();
                        classD = classD.getSuperclass();
                        Field field_list = classD.getDeclaredField("buckets");
                        field_list.setAccessible(true);
                        ((List<ParsedDateHistogram.ParsedBucket>)field_list.get(dateHistogram)).add(index-1, (ParsedDateHistogram.ParsedBucket) bucket2);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            if(bucket2.getKeyAsString().equals(dateHistogram.getBuckets().get(index).getKeyAsString())){
                // 若两个时间相等,将 bucket2 的数据合并到第一条线
                // 此处考虑到bucket2 中可能存在多条线，因此使用循环
                for(String keyname : bucket2.getAggregations().asMap().keySet()){
                    Aggregation agg = bucket2.getAggregations().asMap().get(keyname);
                    Class classT = dateHistogram.getBuckets().get(index).getAggregations().asMap().getClass();
                    // 此处为unmodifiableMap ，所以只能通过反射修改它的值
                    try {
                        Field field = classT.getDeclaredField("m");
                        field.setAccessible(true);
                        ((Map<String,Aggregation>)field.get(dateHistogram.getBuckets().get(index).getAggregations().asMap())).put(keyname,agg);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                // 融合后第一条线下标加一
                index++;
            }else {
                try {
                    Long date2  = sdf.parse( bucket2.getKeyAsString()).getTime();
                    Long date1 = sdf.parse(dateHistogram.getBuckets().get(index).getKeyAsString()).getTime();

                    Long gap =getIntervalTime.getAsMil(interval) ;

                    // 判断两者的差距有没有达到 gap的一半，如果小于，则融合，大于则新增
                    if((date2>=date1-gap/2) && (date2<=date1+gap/2)){
                        // 将 bucket2 的数据合并到第一条线
                        // 此处考虑到bucket2 中可能存在多条线，因此使用循环
                        for(String keyname : bucket2.getAggregations().asMap().keySet()){
                            Aggregation agg = bucket2.getAggregations().asMap().get(keyname);
                            Class classT = dateHistogram.getBuckets().get(index).getAggregations().asMap().getClass();
                            // 此处为unmodifiableMap ，所以只能通过反射修改它的值
                            try {
                                Field field = classT.getDeclaredField("m");
                                field.setAccessible(true);
                                ((Map<String,Aggregation>)field.get(dateHistogram.getBuckets().get(index).getAggregations().asMap())).put(keyname,agg);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                        }
                        index++;
                    }else if((date2<date1-gap/2)) {
                        Long newdate = date1-this.getMyInt((date1-gap/2-date2),gap)*gap;
                        String date = sdf.format(new Date(newdate));
                        //新增一个x轴上的点 ,利用反射去修改keyAsString，然后利用反射修改final修饰的buckets
                        try {
                            Class classT = bucket2.getClass();
                            // 发现是在父类中的属性，而不是在子类中的属性
                            classT = classT.getSuperclass();
//                            for (Field fi:classT.getDeclaredFields()){
//                                System.out.println(fi.toString());
//                            }
                            Field field = classT.getDeclaredField("keyAsString");
                            field.setAccessible(true);
                            field.set(bucket2,date);

                            Class classD =dateHistogram.getClass();
                            classD = classD.getSuperclass();
                            Field field_list = classD.getDeclaredField("buckets");
                            field_list.setAccessible(true);
                            ((List<ParsedDateHistogram.ParsedBucket>)field_list.get(dateHistogram)).add(index, (ParsedDateHistogram.ParsedBucket) bucket2);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        //因为list整体向后移动一位，所以此处也++
                        index++;
                    }else {
                        //让第二个x轴区间的当前游标保持不变，并且第一个x轴区间的向后挪动一位
                        index++;
                        index2--;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        return dateHistogram;
    }

    //除法 向上取整
    public Long getMyInt(Long a,Long b) {
        return (((double)a/(double)b)>(a/b)?a/b+1:a/b);
    }
    /**
     *  一段时间内 平均值  test
     * @param index
     * @param lines
     * @return
     */
    public Object avg(String index, Integer lines){

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        AvgAggregationBuilder cpu_avgBuilder = AggregationBuilders.avg("avg_name").field("system.cpu.total.pct");
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder  = AggregationBuilders.dateHistogram("by_day")

                .dateHistogramInterval(DateHistogramInterval.HOUR)
                .timeZone(DateTimeZone.getDefault())        // 统计的时候考虑到时区问题
                .format("yyyy-MM-dd hh")
                .field("@timestamp")
                .minDocCount(0L)
                .subAggregation(cpu_avgBuilder);


        searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
            System.out.println(response.toString());
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

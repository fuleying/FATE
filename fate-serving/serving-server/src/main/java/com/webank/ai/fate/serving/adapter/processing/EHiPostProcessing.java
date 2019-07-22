package com.webank.ai.fate.serving.adapter.processing;

import com.webank.ai.fate.core.bean.ReturnResult;
import com.webank.ai.fate.core.utils.Configuration;
import com.webank.ai.fate.serving.bean.PostProcessingResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EHiPostProcessing implements PostProcessing {
    private static final Logger LOGGER = LogManager.getLogger();
    private Double producta_freqs_bp;
    private Double producta_nonfreqs_bp;
    private Double productb_freqs_bp;
    private Double productb_nonfreqs_bp;
    //风险系数，影响成本价；
    private Double oc;
    // 城市系数；每个城市根据全国基准价*城市系数*风险系数来得到成本价；
    private Double ac;
    // 售价系数；售价 = 成本价 * 售价系数；
    private Double sc;
    // 模型收支平衡的调整因子；
    private Double alpha;
    private Integer is_freqs;
    // 违章概率cut值；超过该概率，则不投放；
    private Double prob_cutoff;
    private JSONObject modelJsonObject;
    private Properties city_oc_properties;
    private Properties city_sc_properties;
    private Properties producta_city_ac_properties;
    private Properties productb_city_ac_properties;
    private Properties city_oc_properties_src;
    private Properties city_sc_properties_src;
    private Properties producta_city_ac_properties_src;
    private Properties productb_city_ac_properties_src;
    public JSONObject city_price_interval;
    public JSONObject city_price_interval_src;
    private Properties  smartprice_properties;
    public static String order_time;
    public JSONObject discount_time_interval;



    public EHiPostProcessing() {
        String smartprice_properties_file = "smartprice.properties";
        String city_oc_properties_file = "city_oc.properties";
        String city_sc_properties_file = "city_sc.properties";
        String producta_city_ac_properties_file = "producta_city_ac.properties";
        String productb_city_ac_properties_file = "productb_city_ac.properties";
        String city_oc_properties_file_src = "city_oc_src.properties";
        String city_sc_properties_file_src = "city_sc_src.properties";
        String producta_city_ac_properties_file_src = "producta_city_ac_src.properties";
        String productb_city_ac_properties_file_src = "productb_city_ac_src.properties";
        String scale_model_file = "lr_scale.json";
        String lr_model_file = "lr_model.json";
        String city_price_interval_file = "city_price_interval.json";
        String city_price_interval_file_src = "city_price_interval_src.json";
        String discount_time_interval_file = "discount_time_interval.json";

        this.smartprice_properties = Configuration.getAdapterProperties(smartprice_properties_file);
        this.city_oc_properties = Configuration.getAdapterProperties(city_oc_properties_file);
        this.city_sc_properties = Configuration.getAdapterProperties(city_sc_properties_file);
        this.city_oc_properties_src = Configuration.getAdapterProperties(city_oc_properties_file_src);
        this.city_sc_properties_src = Configuration.getAdapterProperties(city_sc_properties_file_src);

        this.producta_city_ac_properties = Configuration.getAdapterProperties(producta_city_ac_properties_file);
        this.productb_city_ac_properties = Configuration.getAdapterProperties(productb_city_ac_properties_file);
        this.producta_city_ac_properties_src = Configuration.getAdapterProperties(producta_city_ac_properties_file_src);
        this.productb_city_ac_properties_src = Configuration.getAdapterProperties(productb_city_ac_properties_file_src);

        this.city_price_interval = Configuration.getAdapterJsonConfig(city_price_interval_file);
        this.city_price_interval_src = Configuration.getAdapterJsonConfig(city_price_interval_file_src);
        this.discount_time_interval = Configuration.getAdapterJsonConfig(discount_time_interval_file);

//        String  pre = "D:\\jarven\\src\\main\\resources\\";
//        this.smartprice_properties = load_properties(smartprice_properties_file);
//        this.city_oc_properties = load_properties(city_oc_properties_file);
//        this.city_sc_properties = load_properties(city_sc_properties_file);
//        this.city_oc_properties_src = load_properties(city_oc_properties_file_src);
//        this.city_sc_properties_src = load_properties(city_sc_properties_file_src);
//
//        this.producta_city_ac_properties = load_properties(producta_city_ac_properties_file);
//        this.productb_city_ac_properties = load_properties(productb_city_ac_properties_file);
//        this.producta_city_ac_properties_src = load_properties(producta_city_ac_properties_file_src);
//        this.productb_city_ac_properties_src = load_properties(productb_city_ac_properties_file_src);
//
//        this.city_price_interval = load_json(city_price_interval_file);
//        this.city_price_interval_src = load_json(city_price_interval_file_src);
//        this.discount_time_interval = load_json(discount_time_interval_file);
//
        JSONObject jsonObjectScale =Configuration.getAdapterJsonConfig(scale_model_file);
        JSONObject jsonObjectLR = Configuration.getAdapterJsonConfig(lr_model_file);
        modelJsonObject = new JSONObject();
        this.modelJsonObject.put("Scale", jsonObjectScale);
        this.modelJsonObject.put("LR", jsonObjectLR);


    }

    private void init() {
        this.producta_freqs_bp = this.StringtoDouble(smartprice_properties.getProperty("producta_freqs_bp"));
        this.producta_nonfreqs_bp = this.StringtoDouble(smartprice_properties.getProperty("producta_nonfreqs_bp"));
        this.productb_freqs_bp = this.StringtoDouble(smartprice_properties.getProperty("productb_freqs_bp"));
        this.productb_nonfreqs_bp = this.StringtoDouble(smartprice_properties.getProperty("productb_nonfreqs_bp"));

        this.alpha = this.StringtoDouble(smartprice_properties.getProperty("alpha"));
        this.oc = this.StringtoDouble(smartprice_properties.getProperty("oc"));
        this.ac = this.StringtoDouble(smartprice_properties.getProperty("ac"));
        this.sc = this.StringtoDouble(smartprice_properties.getProperty("sc"));
        this.prob_cutoff = this.StringtoDouble(smartprice_properties.getProperty("prob_cutoff"));
    }

    @Override
    public PostProcessingResult getResult(Map<String, Object> featureData, Map<String, Object> modelResult) {
        init();
        PostProcessingResult postProcessingResult = new PostProcessingResult();
        Integer rcode;
        if(modelResult.get("retcode")!=null)
            rcode=(Integer)modelResult.get("retcode");
        else
            throw  new  RuntimeException("retcode is null");
        ReturnResult  result  =  new  ReturnResult();
        result.setData(postProcessing(featureData, Double.parseDouble(modelResult.get("prob").toString()),rcode));
        result.setRetcode(rcode);
        postProcessingResult.setProcessingResult(result);
        return postProcessingResult;
    }

    public void test_get_price() {
        Map para_obj = new HashMap();
        para_obj.put("order_time", "2019/7/24 20:11:00");
        para_obj.put("pick_car_time", "2019/11/08 17:40:00");
        para_obj.put("return_car_time", "2019/11/11 13:00:00");
//        理赔标记；
        para_obj.put("user_claim", 0);
        para_obj.put("sex", "male");
        para_obj.put("use_car_days", 3);
//		para_obj.put("amount", 800);
        para_obj.put("user_avg_amount", "300");
        para_obj.put("user_offence_num", 0);
        para_obj.put("user_offence_amount", 0);
        para_obj.put("user_diff_city_offence_num", 0);
        para_obj.put("user_point_penalty", 0);
        para_obj.put("user_lovedrive_index", 5);
        para_obj.put("user_success_pick_car_num", 0);
        para_obj.put("user_car_rental_freq", 1);
        para_obj.put("user_account_age", 1);
        para_obj.put("is_diff_city", 0);

        // 额外输入；
        para_obj.put("user_car_type_total", "suv:1,豪华：2,");
        para_obj.put("car_type", "经济型");
        para_obj.put("user_offence_car_type_total", "");
        para_obj.put("pick_car_city", "深圳");
        para_obj.put("return_car_city", "深圳");
        para_obj.put("user_offence_city_total", "");


        Double test_prob=0.0;
        Integer test_rcode=1;


        Map  result = this.postProcessing(para_obj,test_prob,test_rcode);
        System.out.println(result);

    }

//    private ReturnResult success_price_result(Map<String, Object> product_price_info, Map<String, Object> loginfo, JSONObject warninfo) {
//
//        // 格式化返回字段信息;
//        ReturnResult returnResult = new ReturnResult();
//        returnResult.setRetcode(0);
//        returnResult.setRetmsg("pricing model success");
//        returnResult.setData(product_price_info);
//        returnResult.setLog(loginfo);
//        if (warninfo != null && (Boolean) warninfo.get("warnflag")) {
//            warninfo.remove("warnflag");
//            returnResult.setWarn(toMap(warninfo));
//        }
//        return returnResult;
//    }

    private Map<String,Object> error_price_result(Integer code) {
        // 格式化返回字段信息;
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> priceinfo = new HashMap<String, Object>();
        Map<String, Object> loginfo = new HashMap<String, Object>();
        ret.put("code", code);
        ret.put("msg", "pricing model input para error");
        ret.put("data", priceinfo);
        ret.put("log", loginfo);
       // String ret_string = JSONObject.valueToString(ret);

        return ret;

    }

    public Map<String,Object> postProcessing(Map<String, Object> in_para_map, Double offence_prob, Integer rcode) {

       // Map<String, Object> in_para_map = this.parse_in_paras(in_para_map);

//        Integer code = (Integer) in_para_map.get("code");
//        if (code == 1001) {
//            Map ret = this.error_price_result(code);
//            return ret;
//        }
        JSONObject warn_obj = (JSONObject) in_para_map.get("warn");
        Integer user_success_pick_car_num = (Integer) in_para_map.getOrDefault("user_success_pick_car_num", 1);
        String pick_car_city = (String) in_para_map.get("pick_car_city");

        JSONObject time_intervel=discount_time_interval;

        //time check if call function
//        String order_time_1=order_time;
        String order_time_1 = (String) in_para_map.get("order_time");
        int time_check= this.time_intervel_check(order_time_1,time_intervel,pick_car_city);
        Map<String, Object> product_price_info = new HashMap<String, Object>();
        Map product_price_info_src= null;
        if (time_check ==1){
            //run discount price
            product_price_info=get_price_by_type(in_para_map, city_oc_properties,  city_sc_properties,  producta_city_ac_properties,  productb_city_ac_properties,
                    city_price_interval,  user_success_pick_car_num, offence_prob, rcode);
            //run src price
            product_price_info_src=get_price_by_type(in_para_map, city_oc_properties_src,  city_sc_properties_src,  producta_city_ac_properties_src,  productb_city_ac_properties_src,
                    city_price_interval_src,  user_success_pick_car_num, offence_prob, rcode);

            Object producta_rights_interests_amount_eachday_src=product_price_info_src.get("producta_rights_interests_amount_eachday");
            Object productb_rights_interests_amount_eachday_src=product_price_info_src.get("productb_rights_interests_amount_eachday");
            Object producta_rights_interests_amount_src=product_price_info_src.get("producta_rights_interests_amount");
            Object productb_rights_interests_amount_src=product_price_info_src.get("productb_rights_interests_amount");

            product_price_info.put("producta_rights_interests_amount_eachday_src",producta_rights_interests_amount_eachday_src);
            product_price_info.put("productb_rights_interests_amount_eachday_src",productb_rights_interests_amount_eachday_src);
            product_price_info.put("producta_rights_interests_amount_src",producta_rights_interests_amount_src);
            product_price_info.put("productb_rights_interests_amount_src",productb_rights_interests_amount_src);
            product_price_info.put("producta_discount",time_check);
            product_price_info.put("productb_discount",time_check);
            //output result
        }
        else
        {
            product_price_info=get_price_by_type(in_para_map, city_oc_properties_src,  city_sc_properties_src,  producta_city_ac_properties_src,  productb_city_ac_properties_src,
                    city_price_interval_src,  user_success_pick_car_num , offence_prob, rcode);
            Object producta_rights_interests_amount_eachday_src=product_price_info.get("producta_rights_interests_amount_eachday");
            Object productb_rights_interests_amount_eachday_src=product_price_info.get("productb_rights_interests_amount_eachday");
            Object producta_rights_interests_amount_src=product_price_info.get("producta_rights_interests_amount");
            Object productb_rights_interests_amount_src=product_price_info.get("productb_rights_interests_amount");
            product_price_info.put("producta_rights_interests_amount_src",producta_rights_interests_amount_src);
            product_price_info.put("productb_rights_interests_amount_src",productb_rights_interests_amount_src);
            product_price_info.put("producta_rights_interests_amount_eachday_src",producta_rights_interests_amount_eachday_src);
            product_price_info.put("productb_rights_interests_amount_eachday_src",productb_rights_interests_amount_eachday_src);
            product_price_info.put("producta_discount",time_check);
            product_price_info.put("productb_discount",time_check);
        }

        Integer user_claim = (Integer) in_para_map.getOrDefault("user_claim", 0);

        if (user_claim != 0) {
            product_price_info.put("is_throw", 0);
        }

        Map<String, Object> loginfo = new HashMap<String, Object>();
        loginfo.put("oc", oc);

//		loginfo.put("producta_ac", producta_ac);
//		loginfo.put("productb_ac", productb_ac);
		loginfo.put("offence_prob", this.round(offence_prob, 6));
		loginfo.put("is_freqs", is_freqs);
        loginfo.put("alpha", this.alpha);
        loginfo.put("prob_cutoff", this.prob_cutoff);
        loginfo.put("producta_freqs_bp", this.producta_freqs_bp);
        loginfo.put("producta_nonfreqs_bp", this.producta_nonfreqs_bp);
        loginfo.put("productb_freqs_bp", this.productb_freqs_bp);
        loginfo.put("productb_nonfreqs_bp", this.productb_nonfreqs_bp);

         Map ret = this.success_price_result(product_price_info, loginfo, warn_obj);

        return ret;

    }

    private Map success_price_result(Map<String, Object> product_price_info, Map<String, Object> loginfo,
                                        JSONObject warninfo) {

        // 格式化返回字段信息;
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("code", 0);
        ret.put("msg", "pricing model success");
        ret.put("data", product_price_info);
        ret.put("log", loginfo);
        if (warninfo!=null&&(Boolean) warninfo.get("warnflag")) {
            warninfo.remove("warnflag");
            ret.put("warn", warninfo);
        }

     //   String ret_string = JSONObject.valueToString(ret);

        return ret;

    }

    private Map<String, Object> get_price_by_type(Map in_para_map, Properties city_oc_properties, Properties city_sc_properties, Properties producta_city_ac_properties, Properties productb_city_ac_properties,
                                                  JSONObject city_price_interval, int user_success_pick_car_num, Double offence_prob , int rcode) {


        Double oc = this.oc;
        Double sc = this.sc;
        Double producta_ac = this.ac;
        Double productb_ac = this.ac;


        String pick_car_city = (String) in_para_map.get("pick_car_city");
        String oc_s = city_oc_properties.getProperty(pick_car_city);
        if (oc_s != null) {
            oc = StringtoDouble(oc_s);
        }

        String sc_s = city_sc_properties.getProperty(pick_car_city);
        if (sc_s != null) {
            sc = StringtoDouble(sc_s);
        }


        String ac_s = producta_city_ac_properties.getProperty(pick_car_city);
        if (ac_s != null) {
            producta_ac = StringtoDouble(ac_s);
        }

        ac_s = productb_city_ac_properties.getProperty(pick_car_city);
        if (ac_s != null) {
            productb_ac = StringtoDouble(ac_s);
        }

        // 城市价格区间；
        JSONObject price_interval;
        if (city_price_interval.has(pick_car_city)) {
            price_interval = city_price_interval.getJSONObject(pick_car_city);
        } else {
            price_interval = new JSONObject();
        }



        Map<String, Object> product_price_info;

        Integer is_freqs = 0;
        if (user_success_pick_car_num < 2 & rcode != 0) {
            is_freqs = 0;
            product_price_info = this.compute_product_price_by_nonfreqs(oc, sc, producta_ac, productb_ac, price_interval,in_para_map);
        } else {
            is_freqs = 1;

            product_price_info = this.compute_product_price_by_freqs(oc, sc, producta_ac, productb_ac, price_interval,in_para_map, offence_prob);
        }
        return  product_price_info;
    }

    private int time_intervel_check(String order_time, JSONObject time_intervel, String pick_car_city){
        //get product discount time intervel
        int time_check ;
        if (time_intervel.has(pick_car_city)) {
            time_intervel = time_intervel.getJSONObject(pick_car_city);
            String product_discount_start= time_intervel.getJSONObject("producta").getString("product_discount_start");
            String product_discount_end= time_intervel.getJSONObject("producta").getString("product_discount_end");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            Date start_time= null;
            try {
                start_time = formatter.parse(product_discount_start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date end_time= null;
            try {
                end_time = formatter.parse(product_discount_end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date order_time_2 = null;
            try {
                order_time_2 =  sdf.parse(String.valueOf(order_time));
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String startTime = sdf2.format(order_time_2);
                order_time_2 = formatter.parse(String.valueOf(startTime));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (order_time_2.before(end_time) & start_time.before(order_time_2)){
                time_check=1;
            }
            else {
                time_check=0;
            }
        } else {
            time_check=0;
        }


        return time_check;
    }


    public Properties load_properties(String properties_file) {

        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(properties_file), "UTF-8");
            properties.load(inputStream);
            inputStream.close();

            return properties;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return properties;
        }
    }

    public JSONObject load_json(String jsonfile) {
        JSONObject jsonObject;
        try {
            StringBuilder json_s = new StringBuilder();
            InputStreamReader inputStream = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(jsonfile));

            BufferedReader bufferedReader = new BufferedReader(inputStream);
            String Line = null;
            while ( (Line = bufferedReader.readLine()) != null) {
                json_s.append(Line);
            }

            bufferedReader.close();
            inputStream.close();

            jsonObject = new JSONObject(json_s.toString());

            return jsonObject;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            jsonObject = new JSONObject();
            return jsonObject;
        }

    }

    private Double StringtoDouble(String val_s) throws ClassCastException {
        Double val = Double.valueOf(val_s);
        return val;
    }

    private Double round(Double x, Integer n) {
        BigDecimal x_big = new BigDecimal(x);
        Double x_round = x_big.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
        return x_round;
    }


    /**
     * @param in_para_map:
     *            特征字段, offence_prob: 违章概率.
     * @return 新用户产品A和产品B的定价信息，包括天级别价格和总价.
     *
     */
    private Map<String, Object> compute_product_price_by_nonfreqs(Double oc, Double sc, Double producta_ac, Double productb_ac,
                                                                  JSONObject price_interval, Map<String, Object> in_para_map) {

        String usetype = "nonfreqs";

        Integer is_throw = 1;

        Double producta_price_eachday = sc * oc * producta_ac * this.producta_nonfreqs_bp;
        Double productb_price_eachday = sc * oc * productb_ac * this.productb_nonfreqs_bp;
        Integer use_car_days = (Integer) in_para_map.get("use_car_days");

        Map<String, Object> product_price_info = this.scale_to_price_interval(producta_price_eachday,
                productb_price_eachday, price_interval, use_car_days, is_throw, usetype);

        return product_price_info;

    }
    public Double compute_offence_prob(Map<String, Object> in_para_map) {

        Double prob = 0.0;
        JSONObject ScaleJsonObject = this.modelJsonObject.getJSONObject("Scale");
        JSONObject LRJsonObject = this.modelJsonObject.getJSONObject("LR");

        Iterator<String> iter = LRJsonObject.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Double weight = LRJsonObject.getDouble(key);
            if (key.equals("intercept_")) {
                prob += weight;
            } else if (ScaleJsonObject.has(key)) {
                Object fea = in_para_map.getOrDefault(key, 0);
                Double scale_fea = fea instanceof Number ? ((Number) fea).doubleValue()
                        : Double.parseDouble((String) fea);

                scale_fea = Math.log(scale_fea + 1);
                JSONObject scale_para = ScaleJsonObject.getJSONObject(key);
                Double mean = scale_para.getDouble("mean");
                Double scale = scale_para.getDouble("scale");
                scale_fea = (scale_fea - mean) / scale;
                prob += weight * scale_fea;
            } else {
                prob += weight * (Integer) in_para_map.getOrDefault(key, 0);
            }
        }

        prob = 1.0 / (1 + Math.exp(-prob));

        return prob;

    }
    /**
     * @param in_para_map:
     *            特征字段, offence_prob: 违章概率.
     * @return 常用用户产品A和产品B的定价信息，包括天级别价格和总价.
     *
     */
    private Map<String, Object> compute_product_price_by_freqs(Double oc, Double sc, Double producta_ac, Double productb_ac,
                                                               JSONObject price_interval, Map<String, Object> in_para_map, Double offence_prob) {

        String usetype = "freqs";
        if (offence_prob==0){
            offence_prob = this.compute_offence_prob(in_para_map);
        }



        Integer is_throw = 1;
        if (offence_prob > this.prob_cutoff) {
            is_throw = 0;
        }

        Double producta_price_eachday = sc * oc * producta_ac * this.producta_freqs_bp * (this.alpha + offence_prob);
        Double productb_price_eachday = sc * oc * productb_ac * this.productb_freqs_bp * (this.alpha + offence_prob);

        Integer use_car_days = (Integer) in_para_map.get("use_car_days");
        Map<String, Object> product_price_info = this.scale_to_price_interval(producta_price_eachday,
                productb_price_eachday, price_interval, use_car_days, is_throw, usetype);

        return product_price_info;

    }

//    private String success_price_result(Map<String, Object> product_price_info, Map<String, Object> loginfo,
//                                        JSONObject warninfo) {
//
//        // 格式化返回字段信息;
//        Map<String, Object> ret = new HashMap<String, Object>();
//        ret.put("code", 0);
//        ret.put("msg", "pricing model success");
//        ret.put("data", product_price_info);
//        ret.put("log", loginfo);
//        if ((Boolean) warninfo.get("warnflag")) {
//            warninfo.remove("warnflag");
//            ret.put("warn", warninfo);
//        }
//
//        String ret_string = JSONObject.valueToString(ret);
//
//        return ret_string;
//
//    }

    private Map<String, Object> scale_to_price_interval(Double producta_price_eachday, Double productb_price_eachday,
                                                        JSONObject price_interval, Integer use_car_days, Integer is_throw, String usertype) {

        producta_price_eachday = Math.ceil(producta_price_eachday);
        productb_price_eachday = Math.ceil(productb_price_eachday);

        if (price_interval.length() > 0) {
            JSONArray producta_pi = price_interval.getJSONObject("producta").getJSONArray(usertype);
            JSONArray productb_pi = price_interval.getJSONObject("productb").getJSONArray(usertype);

            // 产品A定价区间；
            if (producta_price_eachday < producta_pi.getDouble(0)) {
                producta_price_eachday = producta_pi.getDouble(0);
            } else if (producta_price_eachday >= producta_pi.getDouble(1)) {
                producta_price_eachday = producta_pi.getDouble(1);
            }

            // 产品B定价区间；
            if (productb_price_eachday < productb_pi.getDouble(0)) {
                productb_price_eachday = productb_pi.getDouble(0);
            } else if (productb_price_eachday >= productb_pi.getDouble(1)) {
                productb_price_eachday = productb_pi.getDouble(1);
            }
        }

        Double producta_price = producta_price_eachday * use_car_days;
        Double productb_price = productb_price_eachday * use_car_days;

        // 输出价格信息；

        Map<String, Object> product_price_info = new HashMap<String, Object>();
        product_price_info.put("producta_rights_interests_amount", producta_price);
        product_price_info.put("productb_rights_interests_amount", productb_price);
        product_price_info.put("producta_rights_interests_amount_eachday", producta_price_eachday);
        product_price_info.put("productb_rights_interests_amount_eachday", productb_price_eachday);
        product_price_info.put("is_throw", is_throw);

        return product_price_info;
    }


    public static  void  main(String[] args){

        EHiPostProcessing eHiPostProcessing  = new EHiPostProcessing();
        eHiPostProcessing.init();

        eHiPostProcessing.test_get_price();





    }
}




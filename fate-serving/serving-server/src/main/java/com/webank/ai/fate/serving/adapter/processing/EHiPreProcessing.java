package com.webank.ai.fate.serving.adapter.processing;


import com.webank.ai.fate.serving.bean.PreProcessingResult;
import jdk.nashorn.internal.runtime.ParserException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EHiPreProcessing implements PreProcessing {
    @Override
    public PreProcessingResult getResult(String paras) {
        PreProcessingResult preProcessingResult = new PreProcessingResult();
        preProcessingResult.setProcessingResult(preProcessing(paras));
        Map<String, Object> featureIds = new HashMap<>();
        JSONObject para_obj = new JSONObject(paras);
        Arrays.asList("device_id", "phone_num").forEach((field -> {
            if (field.equals("device_id") && para_obj.getString(field).length() == 32) {
                featureIds.put(field, idfaTrans(para_obj.getString(field)));
            } else {
                featureIds.put(field, para_obj.getString(field));
            }
        }));
        preProcessingResult.setFeatureIds(featureIds);
        return preProcessingResult;
    }





    public Map<String, Object> preProcessing(String paras) throws ClassCastException, ParserException {
        Map<String, Object> in_para_map = this.parse_in_paras(paras);
        int code = (Integer) in_para_map.get("code");
        if (code == 1001) {
            throw new ParserException("pricing model input para error");
        }
        return in_para_map;
    }

    private JSONObject paras_valid_check(JSONObject para_obj) {
        Boolean warnflag = false;
        JSONObject warn = new JSONObject();
        String sex = para_obj.getString("sex");
        Integer use_car_days = para_obj.getInt("use_car_days");
        Double user_avg_amount = para_obj.getDouble("user_avg_amount");
        Integer user_offence_num = para_obj.getInt("user_offence_num");
        Double user_offence_amount = para_obj.getDouble("user_offence_amount");
        Integer user_diff_city_offence_num = para_obj.getInt("user_diff_city_offence_num");
        Integer user_point_penalty = para_obj.getInt("user_point_penalty");
        Integer user_lovedrive_index = para_obj.getInt("user_lovedrive_index");
        Integer user_success_pick_car_num = para_obj.getInt("user_success_pick_car_num");
        Integer user_car_rental_freq = para_obj.getInt("user_car_rental_freq");
        Integer user_account_age = para_obj.getInt("user_account_age");
        Integer is_diff_city = para_obj.getInt("is_diff_city");

        String pick_car_time = para_obj.getString("pick_car_time");
        String return_car_time = para_obj.getString("return_car_time");

        String pick_car_city = para_obj.getString("pick_car_city");
        String return_car_city = para_obj.getString("return_car_city");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");



        //order_time validation check :if not ,get system time
//        try {
//
//            String order_time_1 = para_obj.getString("order_time");
////			Date order_date = (Date) formatter.parse(order_time_1);
//            order_time =order_time_1;
//        } catch (Exception e) {
//            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            order_time = df.format(new Date());
//            warn.put("use system time", order_time);
//        }


        try {
            Integer user_claim = para_obj.getInt("user_claim");
            if (user_claim != 0 && user_claim != 1) {
                warnflag = true;
                warn.put("user_claim", user_claim);
            }

        } catch (Exception e) {
            warnflag = true;
            warn.put("user_claim", "not exsit");
        }

        try {
            Date pick_car_date = (Date) formatter.parse(pick_car_time);
            Date return_car_date = (Date) formatter.parse(return_car_time);
            int days = (int) Math
                    .ceil((return_car_date.getTime() - pick_car_date.getTime()) / (1000 * 3600 * 24 + 0.0));
            if (use_car_days != days) {
                warnflag = true;
                warn.put("use_car_days", use_car_days);
            }
        } catch (Exception e) {
            // TODO: handle exception
            warnflag = true;
            warn.put("pick_car_time", pick_car_time);
            warn.put("return_car_time", return_car_time);
        }

        if (pick_car_city.matches("[a-zA-Z]+")) {
            warnflag = true;
            warn.put("pick_car_city", pick_car_city);
        }

        if (return_car_city.matches("[a-zA-Z]+")) {
            warnflag = true;
            warn.put("return_car_city", return_car_city);
        }

        if (!sex.equals("male") && !sex.equals("female") && !sex.equals("")) {
            warnflag = true;
            warn.put("sex", sex);
        }

        if (use_car_days < 0) {
            warnflag = true;
            warn.put("use_car_days", use_car_days);
        }

        if (user_avg_amount < 0) {
            warnflag = true;
            warn.put("user_avg_amount", user_avg_amount);
        }

        if (user_offence_num < 0) {
            warnflag = true;
            warn.put("user_offence_num", user_offence_num);
        }

        if (user_offence_amount < 0) {
            warnflag = true;
            warn.put("user_offence_amount", user_offence_amount);
        }

        if (user_diff_city_offence_num < 0) {
            warnflag = true;
            warn.put("user_diff_city_offence_num", user_diff_city_offence_num);
        }

        if (user_point_penalty < 0) {
            warnflag = true;
            warn.put("user_point_penalty", user_point_penalty);
        }

        if (user_lovedrive_index < -1) {
            warnflag = true;
            warn.put("user_lovedrive_index", user_lovedrive_index);
        }

        if (user_success_pick_car_num < 0) {
            warnflag = true;
            warn.put("user_success_pick_car_num", user_success_pick_car_num);
        }

        if (user_car_rental_freq < 0) {
            warnflag = true;
            warn.put("user_car_rental_freq", user_car_rental_freq);
        }

        if (user_account_age < 0) {
            warnflag = true;
            warn.put("user_account_age", user_account_age);
        }

        if (is_diff_city != 0 && is_diff_city != 1) {
            warnflag = true;
            warn.put("is_diff_city", is_diff_city);
        }

        warn.put("warnflag", warnflag);

        return warn;

    }

    public Map<String, Object> parse_in_paras(String paras) {
        JSONObject para_obj = new JSONObject(paras);
        Map<String, Object> ret_parse = new HashMap<String, Object>();

        Map<String,Object>  warn =  new  HashMap();
        try {
            // 参数合法性检查；
            JSONObject warn_obj = this.paras_valid_check(para_obj);

            // 参数；
            Integer use_car_days = para_obj.getInt("use_car_days");
            Double user_avg_amount = para_obj.getDouble("user_avg_amount");
            Integer user_offence_num = para_obj.getInt("user_offence_num");
            Double user_offence_amount = para_obj.getDouble("user_offence_amount");
            Integer user_diff_city_offence_num = para_obj.getInt("user_diff_city_offence_num");
            Integer user_point_penalty = para_obj.getInt("user_point_penalty");
            Integer user_lovedrive_index = para_obj.getInt("user_lovedrive_index");
            Integer user_success_pick_car_num = para_obj.getInt("user_success_pick_car_num");
            Integer user_car_rental_freq = para_obj.getInt("user_car_rental_freq");
            Integer user_account_age = para_obj.getInt("user_account_age");
            Integer is_diff_city = para_obj.getInt("is_diff_city");
            String order_time ;
            // 额外输入；
            String user_car_type_total = para_obj.getString("user_car_type_total");
            String car_type = para_obj.getString("car_type");
            String user_offence_car_type_total = para_obj.getString("user_offence_car_type_total");
            String pick_car_city = para_obj.getString("pick_car_city");
            String return_car_city = para_obj.getString("return_car_city");
            String user_offence_city_total = para_obj.getString("user_offence_city_total");



            try {
                String order_time_1 = para_obj.getString("order_time");
//			Date order_date = (Date) formatter.parse(order_time_1);
                order_time =order_time_1;
            } catch (Exception e) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                order_time = df.format(new Date());
                warn.put("use system time", order_time);
            }
            // 衍生变量计算方式；
            Integer is_normal_car_type = 0;
            if (user_car_type_total.contains(car_type.trim())) {
                is_normal_car_type = 1;
            } else {
                is_normal_car_type = 0;
            }

            Integer is_offence_car_type = 0;
            if (user_offence_car_type_total.contains(car_type.trim())) {
                is_offence_car_type = 1;
            } else {
                is_offence_car_type = 0;
            }

            Integer user_car_type_num = 0;
            if (user_car_type_total.trim().length() > 0) {
                String[] arr = user_car_type_total.trim().split(",");
                user_car_type_num = arr.length;
            }

            Integer is_user_offence_city = 0;
            if (user_offence_city_total.contains(pick_car_city.trim())
                    && user_offence_city_total.contains(return_car_city.trim())) {
                is_user_offence_city = 1;
            } else {
                is_user_offence_city = 0;
            }

            Integer user_claim = new Integer(0);
            try {
                user_claim = para_obj.getInt("user_claim");
            } catch (Exception e) {
                user_claim = 0;
            }

            // 原始变量异常值处理；
            user_avg_amount = user_avg_amount <= 0 ? 0 : user_avg_amount;
            user_offence_num = user_offence_num <= 0 ? 0 : user_offence_num;
            user_offence_amount = user_offence_amount <= 0 ? 0 : user_offence_amount;
            user_diff_city_offence_num = user_diff_city_offence_num <= 0 ? 0 : user_diff_city_offence_num;
            user_point_penalty = user_point_penalty <= 0 ? 0 : user_point_penalty;
            user_lovedrive_index = user_lovedrive_index <= 0 ? 0 : user_lovedrive_index;
            user_success_pick_car_num = user_success_pick_car_num <= 0 ? 0 : user_success_pick_car_num;
            user_car_rental_freq = user_car_rental_freq <= 0 ? 0 : user_car_rental_freq;
            user_account_age = user_account_age <= 0 ? 0 : user_account_age;
            is_diff_city = is_diff_city <= 0 ? 0 : is_diff_city;
            use_car_days = use_car_days <= 0 ? 1 : use_car_days;

            // 原始变量；
            ret_parse.put("user_claim", user_claim);

            ret_parse.put("use_car_days", use_car_days);
            ret_parse.put("user_avg_amount", user_avg_amount);
            ret_parse.put("user_offence_num", user_offence_num);
            ret_parse.put("user_offence_amount", user_offence_amount);
            ret_parse.put("user_point_penalty", user_point_penalty);
            ret_parse.put("user_lovedrive_index", user_lovedrive_index);
            ret_parse.put("user_success_pick_car_num", user_success_pick_car_num);
            ret_parse.put("user_car_rental_freq", user_car_rental_freq);
            ret_parse.put("user_account_age", user_account_age);
            ret_parse.put("is_diff_city", is_diff_city);
            ret_parse.put("user_diff_city_offence_num", user_diff_city_offence_num);
            ret_parse.put("order_time", order_time);
            // 衍生变量；
            ret_parse.put("is_normal_car_type", is_normal_car_type);
            ret_parse.put("is_offence_car_type", is_offence_car_type);
            ret_parse.put("user_car_type_num", user_car_type_num);
            ret_parse.put("is_user_offence_city", is_user_offence_city);

            // 定价需要的城市字段；
            ret_parse.put("pick_car_city", pick_car_city);

            // 成功状态；
            ret_parse.put("code", 0);
            ret_parse.put("warn", warn_obj);

            return ret_parse;

        } catch (Exception e) {
            ret_parse.put("code", 1001);
            e.printStackTrace();
            return ret_parse;

        }
    }

    private String idfaTrans(String idfaWithoutSeparator) {
        return StringUtils.join(
                Arrays.asList(idfaWithoutSeparator.substring(0, 8),
                        idfaWithoutSeparator.substring(8, 12),
                        idfaWithoutSeparator.substring(12, 16),
                        idfaWithoutSeparator.substring(16, 20),
                        idfaWithoutSeparator.substring(20)), "-");
    }


    public static  void main(String[] args){




    }
}

#!/usr/bin/env bash

#
#  Copyright 2019 The FATE Authors. All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

work_mode=$1
jobid=$2
guest_partyid=$3
host_partyid=$4
if [[ $work_mode -eq 1 ]]; then
    role=$5
fi

cur_dir=$(pwd)
data_dir=$cur_dir/../data
load_file_program=$cur_dir/../load_file/load_file.py
conf_dir=$cur_dir/conf
log_dir=$cur_dir/../../logs
load_data_conf=$conf_dir/load_file.json
guest_runtime_conf=$conf_dir/guest_runtime_conf.json
host_runtime_conf=$conf_dir/host_runtime_conf.json

#data_set=vehicle_scale
#data_set=student-mat
data_set=breast
#data_set=default_credit
#data_set=give_credit
train_data_host=$data_dir/${data_set}_a.csv
train_data_guest=$data_dir/${data_set}_b.csv
predict_data_host=$data_dir/${data_set}_a.csv
predict_data_guest=$data_dir/${data_set}_b.csv
cv_data_host=$data_dir/${data_set}_a.csv
cv_data_guest=$data_dir/${data_set}_b.csv

echo "data dir is : "$data_dir
mode=cross_validation
#mode=train
#mode=predict
task_type="\"classification\""
#task_type="\"regression\""
eval_type="\"binary\""
#eval_type="\"multi\""
#eval_type="\"regression\""
objective="\"cross_entropy\""
#objective="\"lse\""
eval_method="\"auc\""
#eval_method="\"accuracy\"" 
#eval_method="\"root_mean_squared_error\""
data_table=''
log_file=''

load_file() {
    input_path=$1
    role=$2   
    load_mode=$3
    if [ "$role"x = "guest"x ];then
        my_party_id=$guest_partyid
        partner_party_id=$host_partyid
    elif [ "$role"x = "host"x ];then
        my_party_id=$host_partyid
        partner_party_id=$guest_partyid
    fi
    conf_path=$conf_dir/load_file.json_${role}_${load_mode}_$jobid
    cp $load_data_conf $conf_path
    data_table=${data_set}_${role}_${load_mode}_$jobid
	sed -i "s|_input_path|${input_path}|g" $conf_path 
	sed -i "s/_table_name/${data_table}/g" $conf_path 
    sed -i "s/_work_mode/${work_mode}/g" $conf_path
    
    python $load_file_program -c $conf_path
}

train() {
    role=$1
    train_table=$2
    predict_table=$3
    runtime_conf=''
    if [ $role = 'guest' ]; then
        runtime_conf=$guest_runtime_conf
    else
        runtime_conf=$host_runtime_conf
    fi

    cur_runtime_conf=${runtime_conf}_$jobid
    cp $runtime_conf $cur_runtime_conf

    echo "current runtime conf is "$cur_runtime_conf
    echo "training table is"$train_table
    echo $predict_table
    sed -i "s/_workflow_method/train/g" $cur_runtime_conf
    sed -i "s/_train_table_name/$train_table/g" $cur_runtime_conf
    sed -i "s/_predict_table_name/$predict_table/g" $cur_runtime_conf
    sed -i "s/_work_mode/$work_mode/g" $cur_runtime_conf
    sed -i "s/_guest_party_id/$guest_partyid/g" $cur_runtime_conf
    sed -i "s/_host_party_id/$host_partyid/g" $cur_runtime_conf
    sed -i "s/_jobid/$jobid/g" $cur_runtime_conf
    
    if [ $role = 'guest' ]; then
        sed -i "s/_task_type/$task_type/g" $cur_runtime_conf
        sed -i "s/_objective/$objective/g" $cur_runtime_conf
        sed -i "s/_eval_type/$eval_type/g" $cur_runtime_conf
        sed -i "s/_eval_method/$eval_method/g" $cur_runtime_conf
    fi

    log_file=$log_dir/$jobid
    echo "pleask check log file in "$log_file
    if [ $role == 'guest' ]; then
        echo "enter guest"
        nohup bash run_guest.sh $cur_runtime_conf $jobid > nohup.guest &
    else
        echo "enter host"
        nohup bash run_host.sh $cur_runtime_conf $jobid > nohup.host &
    fi

}

predict() {
    role=$1
    predict_table=$2
    runtime_conf=''
    if [ $role = 'guest' ]; then
        runtime_conf=$guest_runtime_conf
    else
        runtime_conf=$host_runtime_conf
    fi

    cur_runtime_conf=${runtime_conf}_$jobid
    cp $runtime_conf $cur_runtime_conf

    echo "current runtime conf is "$cur_runtime_conf
    sed -i "s/_workflow_method/predict/g" $cur_runtime_conf
    sed -i "s/_predict_table_name/$predict_table/g" $cur_runtime_conf
    sed -i "s/_work_mode/$work_mode/g" $cur_runtime_conf
    sed -i "s/_guest_party_id/$guest_partyid/g" $cur_runtime_conf
    sed -i "s/_host_party_id/$host_partyid/g" $cur_runtime_conf
    sed -i "s/_jobid/$jobid/g" $cur_runtime_conf

    if [ $role = 'guest' ]; then
        sed -i "s/_task_type/$task_type/g" $cur_runtime_conf
        sed -i "s/_objective/$objective/g" $cur_runtime_conf
        sed -i "s/_eval_type/$eval_type/g" $cur_runtime_conf
        sed -i "s/_eval_method/$eval_method/g" $cur_runtime_conf
    fi
    
    log_file=$log_dir/$jobid
    echo "pleask check log file in "$log_file
    if [ $role == 'guest' ]; then
        echo "enter guest"
        nohup bash run_guest.sh $cur_runtime_conf $jobid > nohup.guest &
    else
        echo "enter host"
        nohup bash run_host.sh $cur_runtime_conf $jobid > nohup.host &
    fi

}

cross_validation() {
    role=$1
    cv_table=$2
    runtime_conf=''
    if [ $role = 'guest' ]; then
        runtime_conf=$guest_runtime_conf
    else
        runtime_conf=$host_runtime_conf
    fi

    cur_runtime_conf=${runtime_conf}_$jobid
    cp $runtime_conf $cur_runtime_conf

    echo "current runtime conf is "$cur_runtime_conf
    echo "cv table is: "$cv_table
    sed -i "s/_workflow_method/cross_validation/g" $cur_runtime_conf
    sed -i "s/_cross_validation_table_name/$cv_table/g" $cur_runtime_conf
    sed -i "s/_work_mode/$work_mode/g" $cur_runtime_conf
    sed -i "s/_guest_party_id/$guest_partyid/g" $cur_runtime_conf
    sed -i "s/_host_party_id/$host_partyid/g" $cur_runtime_conf
    sed -i "s/_jobid/$jobid/g" $cur_runtime_conf

    if [ $role = 'guest' ]; then
        sed -i "s/_task_type/$task_type/g" $cur_runtime_conf
        sed -i "s/_objective/$objective/g" $cur_runtime_conf
        sed -i "s/_eval_type/$eval_type/g" $cur_runtime_conf
        sed -i "s/_eval_method/$eval_method/g" $cur_runtime_conf
    fi

    log_file=$log_dir/$jobid
    echo "pleask check log file in "$log_file
    if [ $role == 'guest' ]; then
        echo "enter guest"
        nohup bash run_guest.sh $cur_runtime_conf $jobid > nohup.guest &
    else
        echo "enter host"
        nohup bash run_host.sh $cur_runtime_conf $jobid > nohup.host &
    fi

}

get_log_result() {
    log_path=$1
    keyword=$2
    sleep 5s
    while true
    do
        num=$(cat $log_path | grep $keyword | wc -l)
        if [ $num -ge 1 ]; then
            cat $log_path | grep $keyword
            break
        else
            echo "please wait or check more info in "$log_path
            sleep 10s
        fi
    done
}

if [ $mode = 'train' ]; then
    if [ $work_mode -eq 0 ]; then
        load_file $train_data_guest guest train
        train_table_guest=$data_table
        echo "train_table guest is:"$train_table_guest

        load_file $train_data_host host train
        train_table_host=$data_table
        echo "train_table host is:"$train_table_host

        load_file $predict_data_guest guest predict
        predict_table_guest=$data_table
        echo "predict_table guest is:"$predict_table_guest

        load_file $predict_data_host host predict
        predict_table_host=$data_table
        echo "predict_table host is:"$predict_table_host
        
        train guest $train_table_guest $predict_table_guest 
        train host $train_table_host $predict_table_host
        workflow_log=${log_file}/workflow.log
        get_log_result $workflow_log eval_result
    elif [[ $role == 'guest' ]]; then
        load_file $train_data_guest guest train
        train_table_guest=$data_table
        echo "train_table guest is:"$train_table_guest

        load_file $predict_data_guest guest predict
        predict_table_guest=$data_table
        echo "predict_table guest is:"$predict_table_guest

        train guest $train_table_guest $predict_table_guest 
        
        workflow_log=${log_file}/workflow.log
        get_log_result $workflow_log eval_result
    elif [[ $role == 'host' ]]; then
        load_file $train_data_host host train
        train_table_host=$data_table
        echo "train_table host is:"$train_table_host

        load_file $predict_data_host host predict
        predict_table_host=$data_table
        echo "predict_table host is:"$predict_table_host
        
        train host $train_table_host $predict_table_host
    fi
elif [ $mode = 'cross_validation' ]; then
    if [[ $work_mode -eq 0 ]]; then
        load_file $cv_data_guest guest cross_validation
        cv_table_guest=$data_table
        load_file $cv_data_host host cross_validation
        cv_table_host=$data_table

        echo "cv table guest is:"$cv_table_guest
        echo "cv table host is:"$cv_table_host

        cross_validation guest $cv_table_guest
        cross_validation host $cv_table_host
        
        workflow_log=${log_file}/workflow.log
        get_log_result $workflow_log mean
    elif [[ $role == 'guest' ]]; then
        load_file $cv_data_guest guest cross_validation
        cv_table_guest=$data_table
        echo "cv table guest is:"$cv_table_guest
        cross_validation guest $cv_table_guest
        workflow_log=${log_file}/workflow.log
        get_log_result $workflow_log mean

    elif [[ $role == 'host' ]]; then
        load_file $cv_data_host host cross_validation
        cv_table_host=$data_table
        echo "cv table host is:"$cv_table_host
        cross_validation host $cv_table_host
    else
        echo $role" not supported"
    fi
elif [ $mode = 'predict' ]; then
    if [[ $work_mode -eq 0 ]]; then
        load_file $predict_data_guest guest predict
        predict_table_guest=$data_table
        load_file $predict_data_host host predict
        predict_table_host=$data_table

        echo "predict table guest is:"$predict_data_guest
        echo "predict table host is:"$predict_data_host

        predict guest $predict_table_guest
        predict host $predict_table_host

        workflow_log=${log_file}/workflow.log
        get_log_result ${workflow_log} predict

    elif [[ $role == 'guest' ]]; then
        load_file $predict_data_guest guest predict
        predict_table_guest=$data_table
        echo "predict table guest is:"$predict_table_guest
        predict guest $predict_table_guest

        workflow_log=${log_file}/workflow.log
        get_log_result ${workflow_log} predict

    elif [[ $role == 'host' ]]; then
        load_file $predict_table_host host predict
        predict_table_host=$data_table
        echo "cv table host is:"$predict_table_host
        predict host $predict_table_host
    
    fi

fi

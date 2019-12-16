#!/usr/bin/env bash

logsystemUserName="default"
# here changes the default logsystemUserName in the log-analysis-system process



#yes |yum install golang

#go get github.com/ghodss/yaml
#go get github.com/bitly/go-simplejson

name="beatwatcher"
rootbase="/var/local"

binaryname="go_build_github_com_beatwatcher_linux"

runtype="testing"

username=$(whoami)
if [[ ${username} == "root" ]]; then
    base=${rootbase}/${logsystemUserName}
else
    userPath=$(echo $HOME)
    base=${userPath}/${logsystemUserName}
fi

echo "start to run..."
[[ ! -d "${base}" ]] && mkdir -p ${base}

if [[ $? -ne 0 ]]; then
    echo "can not create ${base}, has exit "
fi

cd ${base}



# download code



# if git is not exit. it should download git first
git clone https://github.com/chenyuanxing/beatwatcher.git

if [[ $? -ne 0 ]]; then
    echo "can not clone from https://github.com/chenyuanxing/beatwatcher.git,maybe exit.try to remove beatwatcher"
    if [[ ${runtype} == "testing" ]]; then
        echo "it is testing ,do not download again"
    else
        rm -rf beatwatcher
        if [[ $? -ne 0 ]]; then
            echo "remove beatwatcher failed"
        fi
        git clone https://github.com/chenyuanxing/beatwatcher.git
        if [[ $? -ne 0 ]]; then
            echo "can not clone from https://github.com/chenyuanxing/beatwatcher.git,do not know why !!!"
            exit 1
        fi
    fi
fi

cd beatwatcher

tar xzvf metricbeat-6.5.4-linux-x86_64.tar.gz
if [[ $? -ne 0 ]]; then
    echo " can not excute tar xzvf metricbeat-6.5.4-linux-x86_64.tar.gz . so exit  "
    exit 1
fi

tar xzvf filebeat-6.5.4-linux-x86_64.tar.gz
if [[ $? -ne 0 ]]; then
    echo " can not excute tar xzvf filebeat-6.5.4-linux-x86_64.tar.gz . so exit  "
    exit 1
fi

chmod 777 ${binaryname}
# run it 

# ./go_build_github_com_beatwatcher_linux -k "xxx"
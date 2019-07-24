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

FROM python:3.6

WORKDIR /fate


RUN apt-get update && apt-get -y install wget && apt-get -y install libc6 libgmp10 multiarch-support
RUN wget http://ftp.cn.debian.org/debian/pool/main/m/mpfr4/libmpfr4_3.1.2-2_amd64.deb
RUN wget http://ftp.cn.debian.org/debian/pool/main/m/mpfr4/libmpfr4-dbg_3.1.2-2_amd64.deb
RUN dpkg -i libmpfr4_3.1.2-2_amd64.deb
RUN dpkg -i libmpfr4-dbg_3.1.2-2_amd64.deb

RUN apt-get update && apt-get install libgmp3-dev -y && apt-get install -y libmpfr-dev libmpfr-doc libmpfr4 libmpfr4-dbg && apt-get install libmpc-dev -y

RUN rm /bin/sh && ln -sf /bin/bash /bin/sh

COPY requirements.txt /fate
RUN pip install -r requirements.txt

COPY fate.tar /fate
RUN tar -xf fate.tar


RUN rm fate.tar
RUN rm libmpfr4_3.1.2-2_amd64.deb
RUN rm libmpfr4-dbg_3.1.2-2_amd64.deb

ENV PYTHONPATH /fate

VOLUME /fate_src/

WORKDIR /fate_src/

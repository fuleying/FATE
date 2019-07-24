#!/bin/sh


docker run --rm -it -h docker \
    -v /oocl/Github/FATE/:/fate_src/ \
    fuleying/standalone:v2 \
    /bin/bash -c "which python && bash"

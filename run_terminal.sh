#!/bin/sh


docker run --rm -it -h docker \
    fuleying/standalone:v1 \
    /bin/bash -c "which python && bash"

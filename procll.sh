#!/bin/bash

end() {
    status=$?
    echo press any key

    read -n 1

    exit $?
}

cat "$1"

echo
echo ------------------------------------
echo

opt "$1" -O2 > tmp.bc || { cat "$1"; end; }

llvm-dis tmp.bc -o tmpO2.ll
cat tmpO2.ll

llc tmp.bc 

gcc tmp.s -o tmp || { cat "$1"; end; }

./tmp

echo eval: $?

end

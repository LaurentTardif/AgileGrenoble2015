#!/bin/bash
cd INSTALLATION_DIR
echo -ne "[" >twittos.txt; cat /tmp/out.txt.twittos | sort -r | sort -t, -k 1,1 -u   | tr '\n' ',' | sed s/,$// >>twittos.txt; echo "]" >> twittos.txt
echo -ne "[" >twits.txt; cat /tmp/out.txt.twits | sort -r | sort -t, -k 1,1 -u   | tr '\n' ',' | sed s/,$// >>twits.txt; echo "]" >> twits.txt
scp twit*.txt LOGIN@HOST:/WWW_PATH

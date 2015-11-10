# AgileGrenoble2015

## Build , merge and release 

### Backend compilation 

To generate the backend jar, it will be deploy in the flink lib runtime directory.
```mvn package 

### Frontend 

For the moment, you have to manually copy the files in the /var/www/public_html directory .... 

###TRAVIS

Travis will continuously build the code


### RULTOR                                   

Rultor will assist us in merging and releasing
To merge comment on an issue "@rultor merge this issue" 
To create a release "@rultor release tag=`1.0.1`" 


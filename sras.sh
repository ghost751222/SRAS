link=`dirname $0`
cd $link;
service_path="./Service/"
java_process="SRAS"




start(){
	 stop
	 nohup java -Denv.url=https://twjjsulapp010.aia.biz:8443/SRAS/  -jar $service_path$java_process.war  > /dev/null 2>&1 &
         sleep 2s 
         ps -ef | grep $java_process | grep -v grep 
}


stop(){
	status $java_process 0

	if [ $? -gt 0 ];then
	  ps -ef | grep $java_process | grep -v grep | awk '{print $2}'  | xargs kill
        fi
}


restart(){
 stop
 start
}

status(){
	 count=`ps -ef | grep $java_process | grep -v grep |wc -l`
	 if [ "$count" -gt "0"  ];then
	     if [ "$2" == "1" ] ; then
		      echo "$1 is running ..."
	     fi
             return 1
   else
	     if [ "$2" == "1" ];then
		      echo "$1 is not running ..."
	     fi
           return 0
       fi

}

case $1 in
  status)
         status $java_process 1
	 ;;
  start) start  ;;
  stop) stop ;;
  restart) restart ;;
  *) echo "unknow commnad(status|start|stop|restart";;
esac


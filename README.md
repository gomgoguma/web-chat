zookeeper
- 분산 시스템의 구성, 구성 관리, 리더 선출, 네이밍 서비스 등 다양한 기능을 제공.  
- 노드 간의 상태 및 구성 정보를 저장하고, 분산 잠금, 순서 생성, 이벤트 처리 등을 제공하여 분산 시스템의 일관성/신뢰성 유지.

zookeeper 실행 명령어
> .\zookeeper-server-start.bat ..\..\config\zookeeper.properties


kafka
- 대량의 실시간 데이터를 수집, 저장, 처리, 전송하는 데 사용
- 프로듀서(데이터 생성자)가 데이터를 카프카 클러스터로 보내고, 컨슈머(데이터 소비자)가 해당 데이터를 읽어가는 메시지 큐 시스템
- 최신 버전에서는 zookeeper 없이 kafka에 내장된 zookeeper 호환 API를 사용한다.
- 일부 기능은 zookeeper가 여전히 필요하고, 앞으로 의존성이 계속 줄어들 것

kafka-server 실행 명령어
> .\kafka-server-start.bat ..\..\config\server.properties

zookeeper, kafka bat파일 만들어서 실행하기
```
@echo off
cd [zookeeper path]
.\zookeeper-server-start.bat ..\..\config\zookeeper.properties

@echo off
cd [kafka path]
.\kafka-server-start.bat ..\..\config\server.properties
```

ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


kafka topic 생성 (단일 복제본, 단일 파티션)
> .\kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic [토픽 이름]

kafka topic 조회
> .\kafka-topics --list --bootstrap-server localhost:9092

kafka topic 메시지 확인
> .\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic [토픽 이름] --from-beginning --partition [partition number] 
```
--from-beginning : 처음부터 읽기 (생략하면 현재부터. )
--partition 2 : 2번 파티션만 읽기
```

kafka 컨슈머 그룹 조회 (특정 그룹, 특정 토픽)
> ./kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group [group id] --describe | findstr /C:[topic이름]


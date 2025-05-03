# NARI-Backend

## Docker 
### Docker Image 설치
```bash
docker run -d   --name nari-app -v /var/log/app/spring:/var/log/app/spring  -p 80:8080   gorani41/nari-app:latest
```

## 로그
### 로그 저장 
현재 NARI프로젝트는 logback을 사용하여 파일로 로그를 남기고 있어요


### 로그 관련 설정 파일
- src/main/resources/logback-spring.xml - 로그 설정 파일
- application.yml - 로그 파일 위치 설정

### 로그 저장 방법
현재 로그 설정대로 로그를 남기기 위해서는 아래의 명령어를 통해 로그 디렉토리를 생성해야 해요
```
sudo mkdir -p /var/log/app/spring
sudo chown $USER:$USER /var/log/app/spring
sudo chmod 750 /var/log/app/spring         
```

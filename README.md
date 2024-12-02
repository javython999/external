# 외부설정과 프로필
## 외부설정이란?
유지보수하기 좋은 애플리케이션을 개발하는 단순하면서도 중요한 원칙은 변하는 것과 변하지 않는 것을 분리하는 것이다.
각 환경에 따라 변하는 외부설정 값은 분리하고, 변하지 않는 코드와 빌드 결과물은 유지하는 것이다.

## 외부설정
애플리케이션을 실행할 때 필요한 설정 값을 외부에서 어떻게 불러와서 애플리케이션에 전달할 수 있을까?
* OS 환경 변수: OS에서 지원하는 외부 설정, 해당 OS를 사용하는 모든 프로세스에서 사용
* 자바 시스템 속성: 자바에서 지원하는 외부 설정, 해당 JVM안에서 사용
* 자바 커맨드 라인 인수: 커맨드 라인에서 전달하는 외부 설정, 실행시 `main(args)` 메서드에서 사용
* 외부 파일(설정 데이터): 프로그램에서 외부 파일을 직접 읽어서 사용
  * 애플리케이션에서 특정 위치의 파일을 읽도록 해둔다. 예) `data/hello.txt`
  * 그리고 각 서버마다 해당 파일안에 다른 설정 정보를 남겨둔다.

## OS 환경 변수
OS 환경 변수(OS enviroment variables)는 해당 OS를 사용하는 모든 프로그램에서 읽을 수 있는 설정 값이다.
한 마디로 다른 외부 설정과 비교해서 사용 범위가 가장 넓다.
OS 환경 변수를 설정하고, 필요한 곳에서 `System.getenv()`를 사용하면 외부설정을 사용할 수 있다.

## 자바 시스템 속성
자바 시스템 속성(Java System properties)은 실행한 JVM 안에서 접근 가능한 외부 설정이다.
추가로 자바가 내부에 미리 설정해두고 사용하는 속성들도 있다.

자바에서 시스템 속성은 다음과 같이 자바 프로그램을 실행할 때 사용한다.
* 예) `java -Durl=dev -jar app.jar`
* `-D` VM 옵션을 통해서 `key=value` 형식을 주면된다. 이 예제는 `url=dev` 속성이 추가된다.

```java
@Slf4j
public class JavaSystemProperties {
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        
        for (Object key : properties.keySet()) {
            log.info("prop {}={}", key, 
            System.getProperty(String.valueOf(key)));
        }
    }
 }
```
* `System.getProperties()`를 사용하면 `Map`과 유사한 `key=value` 형식의 `Properties`를 받을 수 있다.
* `System.getProperty(key)`를 사용하면 속성값을 조회할 수 있다.

## 커맨드 라인 인수
커맨드 라인 인수(Command line arguments)는 애플리케이션 실행 시점에 외부 설정 값을 `main(args)` 메서드의 `args` 파라미터로 전달하는 방법이다.
다음과 같이 사용한다.
* 예) `java -jar app.jar dataA dataB`
* 필요한 데이터를 마지막 위치에 스페이스로 구분해서 전달하면 된다.

커맨드 라인 인수는 `key=value` 형식이 아니다. 단순히 문자를 여러개 입력 받는 형식인 것이다.
이것은 파싱되지 않은 통 문자이다. 이 경우 `=`을 기준으로 직접 데이터를 파싱해서 `key=value` 형식에 맞도록 분리해야 한다.
그리고 형식이 배열이기 때문에 루프를 돌면서 원하는 데이터를 찾아야 하는 번거로움도 발생한다.


## 커맨드 라인 옵션 인수
커맨드 라인 인수를 `key=value` 형식으로 구분하는 방법이 필요하다. 그래서 스프링에서 커맨드 라인 인수를 `key=value` 형식으로 편리하게 사용할 수 있도록
스프링만의 표준 방식을 정의했는데, 그것이 커맨드라인 옵션 인수이다.

스프링은 커맨드 라인에 `-` 2개 (`--`)를 연결해서 시작하면 `key=value` 형식으로 정하고 이것을 커맨드 라인 옵션 인수라 한다.
* `--key=value` 형식으로 사용한다.
* `--username=userA --username=userB` 하나의 키에 여러 값도 지정할 수 있다.
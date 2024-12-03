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

## 외부설정 - 스프링 통합
외부 설정값이 어디에 위치하든 상관없이 일관성 있고, 편리하게 `key=value` 형식의 외부 설정값을 읽을 수 있으면 사용하는 개발자 입장에서 더 편리하고 또 외부 설정값을 설정하는 방법도 더 유연해질 수 있다. 예를 들어서 외부 설정
값을 OS 환경변수를 사용하다가 자바 시스템 속성으로 변경하는 경우에 소스코드를 다시 빌드하지 않고 그대로 사용할 수 있다.
스프링은 이 문제를 `Environment`와 `PropertySource`라는 추상화를 통해서 해결한다.

### 스프링의 외부 설정 통합
PropertySource
* `org.springframework.core.env.PropertySource`
* 스프링은 `PropertySource`라는 추상 클래스를 제공하고, 각각의 외부 설정을 조회하는 xxPropertySource 구현체를 만들어 두었다.
  * 예) `CommandLinePropertySource`, `SystemEnvironmentPropertySource`
* 스프링은 로딩 시점에 필요한 `PropertySource`들을 생성하고, `Environment`에서 사용할 수 있게 연결해둔다.

Environment
* `org.springframework.core.env.Environment`
* `Environment`를 통해서 특정 외부 설정에 종속되지 않고, 일관성 있게 `key=value` 형식의 외부 설정에 접근할 수 있다.
  * `environment.getProperty(key)`를 통해서 값을 조회할 수 있다.
  * `Environment`는 내부에서 여러 과정을 거쳐서 `PropertySource`들에 접근한다.
  * 같은 값이 있을 경우를 대비해서 스프링은 미리 우선순위를 정해두었다.
* 모든 외부 설정은 이제 `Environment`를 통해 조회하면 된다.

설정 데이터(파일)
`application.properties`, `application.yml`도 `PropertySource`에 추가된다. 따라서 `Environment`를 통해 접근할 수 있다.

우선순위
우선순위는 상식 선에서 2가지를 기억하면 된다.
* 더 유연한 것이 우선권을 가진다.(변경하기 어려운 파일 보다 실행시 원하는 값을 줄 수 있는 자바 시스템 속성이 더 우선권을 가진다.)
* 범위가 넓은 것 보다 좁은 것이 우선권을 가진다.(자바 시스템 속성은 해당 JVM 안에서 모두 접근할 수 있다. 반면 커맨드 라인 옵션 인수는 `main`의 args를 통해서 들어오기 때문에 접근 번위가 더 좁다.)

## 설정 데이터 - 외부 파일
실무에서는 수십개의 설정값을 사용하기도 하므로 이런 값들을 프로그램을 실행할 때 마다 입력하게 되면 번거롭고, 관리도 어렵다.
그래서 등장하는 대안으로는 설정값을 파일에 넣어서 관리하는 방법이다. 그리고 애플리케이션 로딩 시점에 해당 파일을 읽어들이면 된다.
그 중에서도 `.properties`라는 파일은 `key=value` 형식을 사용해서 설정값을 관리하기에 아주 적합하다.

## 설정 데이터 - 내부 파일 분리
설정 파일을 외부에 관리하는 것은 상당히 번거로운 일이다. 설정을 변경할 때마다 서버에 들어가서 각각의 변경 사항을 수정해두어야 한다.
이 문제를 해결하는 간단한 방법은 설정 파일을 프로젝트 내부에 포함해서 관리하는 것이다.
그리고 빌드 시점에 함께 빌드되게 하는 것이다. 이렇게 하면 애플리케이션을 배포할 때 설정 파일의 변경 사항도 함께 배포할 수 있다.
쉽게 이야기해서 `jar` 하나로 설정 데이터까지 포함해서 관리하는 것이다.
1. 프로젝트 안에 소스 코드 뿐만 아니라 각 환경에 필요한 설정 데이터도 함께 포함해서 관리한다.
   * 개발용 설정 파일: `application-dev.properties`
   * 운영용 설정 파일: `application-prod.properties`
2. 빌드 시점에 개발, 운영 설정 파일을 모두 포함해서 빌드 한다.
3. `app.jar`는 개발, 운영 두 설정 파일을 모두 가지고 배포된다.
4. 실행할 때 어떤 설정 데이터를 읽어야 할지 최소한의 구분은 필요하다.
   * 개발 환경이라면 `application-dev.properties`
   * 운영 환경이라면 `application-prod.properties`
   * 실행할 때 외부 설정을 사용해서 개발서버는 `dev`라는 값을 제공하고, 운영서버는 `prod`라는 값을 제공하자
     * `dev` 프로필이 넘어오면 `application-dev.properties`를 읽어서 사용한다.
     * `prod` 프로필이 넘어오면 `application-prod.properties`를 읽어서 사용한다.

## 설정 데이터 - 내부 파일 합체
설정 파일을 각각 분리해서 관리하면 한눈에 전체가 들어오지 않는 단점이 있다.
스프링은 이런 단점을 보완하기 위해 물리적인 하나의 파일 안에서 논리적으로 영역을 구분하는 방법을 제공한다.

```properties
spring.config.activate.on-profile=dev
url=dev.db.com
username=dev_user
password=dev_pw

#---
spring.config.activate.on-profile=prod
url=prod.db.com
username=prod_user
password=prod_pw
```
## 우선순위 - 설정 데이터
프로필을 적용하지 않고 실행하면 해당하는 프로필이 없으므로 키를 각각 조회하면 값은 `null`이 된다.
프로필을 지정하지 않고 실행하면 스프링은 기본적으로 `default`라는 이름의 프로필을 사용한다.

### 기본 값
로컬 개발 환경에서 항상 프로필을 지정하면서 실행하는 것은 상당히 피곤할 것이다.
설정 데이터에는 기본 값을 지정할 수 있는데, 프로필 지정과 무관하게 이 값은 항상 사용된다.
```properties
url=local.db.com
username=local_user
password=lcoal_pw
#--
spring.config.activate.on-profile=dev
url=dev.db.com
username=dev_user
password=dev_pw
#---
spring.config.activate.on-profile=prod
url=prod.db.com
username=prod_user
password=prod_pw
```
스프링은 문서를 위에서 아래로 순서대로 읽으면서 설정한다.
여기서 처음에 나오는 다음 논리 문서는 `spring.config.activate.on-profile`와 같은 프로필 정보가 없다.
따라서 프로필과 무관하게 설정 데이터를 읽어서 사용한다. 이렇게 프로필 지정과 무관하게 사용되는 것을 기본 값이라 한다.
```properties
url=local.db.com
username=local_user
password=local_pw
```
### 설정 데이터 적용 순서
```properties
url=local.db.com
username=local_user
password=lcoal_pw
#--
spring.config.activate.on-profile=dev
url=dev.db.com
username=dev_user
password=dev_pw
#---
spring.config.activate.on-profile=prod
url=prod.db.com
username=prod_user
password=prod_pw
```
스프링 단순하게 문서를 위에서 아래로 순서대로 읽으면서 사용할 값을 설정한다.
1. 스프링은 순서상 위에 있는 `local` 관련 논리 문서의 데이터들을 읽어서 설정한다. 여기에는 `spring.config.activate.on-profile` 같은 별도의 프로필을 지정하지 않았기 때문에 프로필과 무관하게 항상 값을 사용하도록 설정한다.

```properties
spring.config.activate.on-profile=dev
url=dev.db.com
username=dev_user
password=dev_pw
```
2. 스프링은 그 다음 순서로 `dev` 관련 논리 문서를 읽는데 만약 `dev` 프로필이 설정되었다면 기존 데이터를 `dev` 관련 논리 문서의 값으로 대체한다. `dev` 관련 논리문서는 무시되고, 그 값도 사용하지 않는다.
```properties
url=local.db.com    --> dev.db.com
username=local_user --> dev_user
password=lcoal_pw   --> dev_pw
```
3. 스프링은 그 다음 순서로 `prod` 관련 논리문서를 읽는데 만약 `prod` 프로필이 설정되었다면 기존 데이터를 `prod` 관련 논리문서의 값으로 대체한다. 물론 `prod`프로필을 사용하지 않는다면 `prod`관련 논리문서는 무시되고 그 값도 사용하지 않는다.

참고로 프로필을 한번에 둘 이상 설정하는것도 가능하다.
`--spring.profiles.active=dev,prod`

## 우선순위 - 전체
스프링 부트는 같은 애플리케이션 코드를 유지하면서 다양한 외부 설정을 사용할 수 있도록 지원한다.
우선순위는 위에서 아래로 적용된다. 아래가 더 우선순위가 높다.
자주 사용하는 우선순위
* 설정 데이터(`application.properties`, `application.yml`)
* OS 환경 변수
* 자바 시스템 속성
* 커맨드 라인 옵션 인수
* `@TestPropertySource`(테스트에서 사용)

설정 데이터 우선순위
* jar 내부 `application.properties`
* jar 내부 프로필 적용 파일 `application-{profile}.properties`
* jar 외부 `application.properties`
* jar 외부 프로필 적용 파일 `application-{profile}.properties`